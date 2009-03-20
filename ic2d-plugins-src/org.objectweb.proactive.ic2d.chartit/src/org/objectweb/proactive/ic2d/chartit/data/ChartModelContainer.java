/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s): ActiveEon Team - http://www.activeeon.com
 *
 * ################################################################
 * $$ACTIVEEON_CONTRIBUTOR$$
 */
package org.objectweb.proactive.ic2d.chartit.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.objectweb.proactive.ic2d.chartit.data.store.IDataStore;


/**
 * This class represents a model of a chart that can be updated.
 * <p>
 * The chart is associated to some data providers that will be 
 * asked for values.
 * <p>
 * To avoid any concurrency problems at runtime any user interactions
 * should be avoided.
 * <p>
 * The user should explicitly stop the runtime.
 * 
 * @author <a href="mailto:support@activeeon.com">ActiveEon Team</a>.
 */
public final class ChartModelContainer {
    /**
     * Minimal period for refreshing models (seconds)
     */
    public static final int MIN_REFRESH_PERIOD_IN_SECS = 3;

    /**
     * Minimal period for refreshing models (milliseconds)
     */
    public static final int MIN_REFRESH_PERIOD_IN_MILLIS = MIN_REFRESH_PERIOD_IN_SECS * 1000;

    /**
     * Maximal period for refreshing models (seconds)
     */
    public static final int MAX_REFRESH_PERIOD_IN_SECS = 100;

    /**
     * Default period for refreshing models (seconds)
     */
    public static final int DEFAULT_REFRESH_PERIOD_IN_SECS = 4;

    /**
     * Default refresh thread name
     */
    public static final String REFRESH_THREAD_NAME = "ChartIt-Models-Refresher";

    /**
     * The models used by this data store
     */
    protected final List<ChartModel> models;

    /**
     * The underlying data store
     */
    protected final IDataStore dataStore;

    /**
     * A timer used to run models updates as tasks 
     */
    protected final Timer scheduledModelsCollector;

    /**
     * All scheduled tasks
     */
    private final List<TimerTask> scheduledTasks;

    /**
     * To know if this collector is running or not
     */
    protected volatile boolean isRunning;

    /**
     * Creates a new instance of the models collector
     * @param models
     */
    public ChartModelContainer(final List<ChartModel> models, final IDataStore dataStore) {
        this.models = models;
        this.dataStore = dataStore;
        this.scheduledModelsCollector = new Timer(REFRESH_THREAD_NAME);
        this.scheduledTasks = new java.util.LinkedList<TimerTask>();
    }

    /**
     * Creates a new instance of the models collector
     */
    public ChartModelContainer(final IDataStore dataStore) {
        this(new ArrayList<ChartModel>(), dataStore);
    }

    /**
     * Adds a model to this data store.
     * 
     * @param model The model to add
     */
    public void addModel(final ChartModel model) {
        this.models.add(model);
    }

    public void removeModel(final ChartModel model) {
        this.models.remove(model);
    }

    public void removeByName(final String name) {
        final ChartModel toRemove = this.getModelByName(name);
        if (toRemove != null)
            this.models.remove(toRemove);
    }

    public ChartModel getModelByName(String name) {
        for (final ChartModel c : this.models) {
            if (name.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    public ChartModel createNewChartModel() {
        final ChartModel c = new ChartModel(ChartModel.DEFAULT_CHART_NAME + this.models.size());
        this.models.add(c);
        return c;
    }

    /**
     * 
     * @return True if running and false otherwise
     */
    public boolean isRunning() {
        return this.isRunning;
    }

    /**
     * Starts collecting data. For each chart model a timer task is created then 
     * these tasks are submitted to a timer that will execute them periodically.
     */
    public void startCollector() {
        // Set this collector as running
        this.isRunning = true;

        // Find all providers from stored models
        final List<ChartModel> modelsToStore = new ArrayList<ChartModel>();
        for (final ChartModel model : this.models) {
            if (model.isChronological()) {
                modelsToStore.add(model);
            }
        }

        // An extra task for updating data store
        TimerTask dataStoreUpdaterTask = null;
        // Only init the data store if there are some models to store 
        if (modelsToStore.size() > 0) {
            // Init the data store
            this.dataStore.init(modelsToStore, MIN_REFRESH_PERIOD_IN_SECS);

            // Create the extra task
            dataStoreUpdaterTask = new TimerTask() {
                @Override
                public final void run() {
                    ChartModelContainer.this.dataStore.update();
                }
            };

            // First Schedule the store updater
            this.scheduledModelsCollector.schedule(dataStoreUpdaterTask, MIN_REFRESH_PERIOD_IN_MILLIS,
                    MIN_REFRESH_PERIOD_IN_MILLIS);
        }

        // Then schedule all tasks that run models
        for (final ChartModel model : this.models) {
            // First create the task that will run the model
            final TimerTask task = new TimerTask() {
                @Override
                public final void run() {
                    model.run();
                }
            };

            // Then schedule it
            this.scheduledModelsCollector.schedule(task, model.getRefreshPeriodInMs(), //initial delay
                    model.getRefreshPeriodInMs());

            // Finally add it to the task list
            this.scheduledTasks.add(task);
        }

        // Finally add the extra task to tasks list
        if (dataStoreUpdaterTask != null) {
            this.scheduledTasks.add(dataStoreUpdaterTask);
        }
    }

    /**
     * Stops this collector
     */
    public void stopCollector() {
        // Cancel all tasks  
        for (final TimerTask task : this.scheduledTasks) {
            task.cancel();
        }
        // Clear the task list
        this.scheduledTasks.clear();
        //  Purge the timer so that it can be reused
        this.scheduledModelsCollector.purge();

        // Close the data store
        this.dataStore.close();

        this.isRunning = false;
    }

    /**
     * Returns the data store used by this container.
     * 
     * @return The data store
     */
    public IDataStore getDataStore() {
        return dataStore;
    }

    /**
     * Returns the list of chart models.
     * 
     * @return The list of chart models of this container
     */
    public List<ChartModel> getModels() {
        return this.models;
    }
}