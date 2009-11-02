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
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.component.wsbindings;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactory;
import org.objectweb.proactive.core.component.webservices.WSInfo;
import org.objectweb.proactive.extensions.webservices.AbstractWebServicesFactory;
import org.objectweb.proactive.extensions.webservices.WSConstants;


/**
 * Test CXF web service binding.
 *
 * @author The ProActive Team
 */
public class TestCXFWSBindings extends CommonSetup {
    @Before
    public void setUpAndDeploy() throws Exception {
        wsf = AbstractWebServicesFactory.getWebServicesFactory("cxf");
        super.setUpAndDeploy();
    }

    @Test
    public void testCXFWebServicesBindingWithPrimitiveComponent() throws Exception {
        Component client = gf.newFcInstance(componentType, new ControllerDescription("Client",
            Constants.PRIMITIVE), new ContentDescription(Client.class.getName()));
        Fractal.getBindingController(client).bindFc(
                Client.SERVICES_NAME,
                url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME + "(" +
                    WSInfo.CXFWSCALLER_ID + ")");
        for (int i = 0; i < NUMBER_SERVERS; i++) {
            Fractal.getBindingController(client).bindFc(
                    Client.SERVICEMULTICASTREAL_NAME,
                    url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + i + "_" +
                        SERVER_SERVICEMULTICAST_NAME + "(" + WSInfo.CXFWSCALLER_ID + ")");
        }
        Fractal.getLifeCycleController(client).startFc();
        Runner runner = (Runner) client.getFcInterface("Runner");
        Assert.assertTrue("Failed to invoke web services with primitive component", runner.execute()
                .booleanValue());
    }

    @Test
    public void testCXFWebServicesBindingWithMethodError() throws Exception {
        ComponentType cType = tf.createFcType(new InterfaceType[] {
                tf.createFcItfType("Runner", Runner.class.getName(), ProActiveTypeFactory.SERVER,
                        ProActiveTypeFactory.MANDATORY, ProActiveTypeFactory.SINGLE),
                tf.createFcItfType(Client.SERVICEERROR_NAME, ServiceError.class.getName(),
                        ProActiveTypeFactory.CLIENT, ProActiveTypeFactory.MANDATORY,
                        ProActiveTypeFactory.SINGLE) });
        Component client = gf.newFcInstance(cType, new ControllerDescription("Client", Constants.PRIMITIVE),
                new ContentDescription(Client.class.getName()));
        Fractal.getBindingController(client).bindFc(
                Client.SERVICEERROR_NAME,
                url + WSConstants.SERVICES_PATH + SERVER_DEFAULT_NAME + "0_" + SERVER_SERVICES_NAME + "(" +
                    WSInfo.CXFWSCALLER_ID + ")");
        Fractal.getLifeCycleController(client).startFc();
        Runner runner = (Runner) client.getFcInterface("Runner");
        Assert.assertFalse("Successful access to a non existing method", runner.execute().booleanValue());
    }
}