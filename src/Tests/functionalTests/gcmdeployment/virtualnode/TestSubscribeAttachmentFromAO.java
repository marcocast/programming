/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.gcmdeployment.virtualnode;

import java.io.Serializable;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.core.xml.VariableContractType;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.objectweb.proactive.utils.OperatingSystem;

import functionalTests.FunctionalTest;
import functionalTests.GCMFunctionalTestDefaultNodes;


public class TestSubscribeAttachmentFromAO extends FunctionalTest {

    @Test
    public void test() throws ActiveObjectCreationException, NodeException, InterruptedException {
        TestSubscribeAttachmentFromAODeployer ao = (TestSubscribeAttachmentFromAODeployer) PAActiveObject
                .newActive(TestSubscribeAttachmentFromAODeployer.class.getName(), new Object[] {});
        ao.deploy();
        Assert.assertTrue(ao.waitUntilCallbackOccur());
    }

    static public class TestSubscribeAttachmentFromAODeployer implements Serializable, InitActive {
        /**
         * 
         */
        private static final long serialVersionUID = 51L;
        GCMApplication gcma;
        boolean notified = false;

        public TestSubscribeAttachmentFromAODeployer() {

        }

        public TestSubscribeAttachmentFromAODeployer(GCMApplication gcma) {
            this.gcma = gcma;
        }

        public void initActivity(Body body) {
            PAActiveObject.setImmediateService("callback");
        }

        public void deploy() {
            try {
                URL appDesc = this.getClass().getResource("/functionalTests/_CONFIG/JunitApp.xml");

                VariableContractImpl vContract = new VariableContractImpl();
                vContract.setVariableFromProgram(GCMFunctionalTestDefaultNodes.VAR_OS, OperatingSystem
                        .getOperatingSystem().name(), VariableContractType.DescriptorDefaultVariable);

                vContract.setVariableFromProgram(FunctionalTest.VAR_JVM_PARAMETERS, FunctionalTest
                        .getJvmParameters(), VariableContractType.ProgramVariable);
                GCMApplication gcma = PAGCMDeployment.loadApplicationDescriptor(appDesc, vContract);

                GCMVirtualNode vn = gcma.getVirtualNode("nodes");
                vn.subscribeNodeAttachment(PAActiveObject.getStubOnThis(), "callback", true);

                gcma.startDeployment();
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("EoInitActivity");
        }

        public void callback(Node node, String vn) {
            System.out.println("Callback called");
            notified = true;
        }

        public boolean waitUntilCallbackOccur() throws InterruptedException {
            while (!notified) {
                System.out.println("!notified");
                Thread.sleep(250);
            }

            return true;
        }

    }
}
