/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.objectweb.proactive.core.jmx.client;

import java.io.IOException;
import java.io.Serializable;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.objectweb.proactive.core.jmx.ProActiveConnection;
import org.objectweb.proactive.core.jmx.ProActiveJMXConstants;
import org.objectweb.proactive.core.util.URIBuilder;


/**
 *  This class creates a JMX client connector whose communication protocol is ProActive
 * @author The ProActive Team
 *
 */
public class ClientConnector implements Serializable {
    private ProActiveConnection connection;

    private JMXConnector connector;

    private String url;

    private String serverName;

    /**
     * Creates a ClientConnector and connect to the remote JMX MBean Server whose at the specified url
     * @param url The ProActive JMX connector url ( for example localhost:8080 )
     */
    public ClientConnector(String url) {
        this.url = url;
    }

    public ClientConnector(String url, String serverName) {
        this(url);
        this.serverName = serverName;
    }

    /*
     * Connect to the ProActive Connector
     */
    public void connect() throws IOException {
        /* build the jmx Url */
        if (this.serverName != null) {
            this.url = URIBuilder.buildURI(URIBuilder.getHostNameFromUrl(this.url),
                                           ProActiveJMXConstants.SERVER_REGISTERED_NAME + "_" + serverName,
                                           "service:jmx:proactive",
                                           URIBuilder.getPortNumber(this.url))
                                 .toString();
        } else {
            this.url = URIBuilder.buildURI(URIBuilder.getHostNameFromUrl(this.url),
                                           ProActiveJMXConstants.SERVER_REGISTERED_NAME,
                                           "service:jmx:proactive",
                                           URIBuilder.getPortNumber(this.url))
                                 .toString();
        }

        JMXServiceURL jmxUrl = new JMXServiceURL(url);

        /* connect to the connector server */
        this.connector = JMXConnectorFactory.connect(jmxUrl, ProActiveJMXConstants.PROACTIVE_JMX_ENV);
        /*
         * retrieve the ProActive Connection that will enable the remote calls onto the remote MBean
         * server
         */
        this.connection = (ProActiveConnection) this.connector.getMBeanServerConnection();
    }

    /**
     *  Returns the ProActiveConnection that will enable the calls onto the remote MBean Server
     * @return the JMX ProActive Connection
     */
    public ProActiveConnection getConnection() {
        return this.connection;
    }

    /**
     * Returns the ProActive Remote connector that can be used to establish a connection to a connector server.
     * @return the ProActive JMX Connector Server
     */
    public JMXConnector getConnector() {
        return this.connector;
    }
}
