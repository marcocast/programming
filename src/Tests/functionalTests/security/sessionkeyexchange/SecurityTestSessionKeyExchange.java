/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package functionalTests.security.sessionkeyexchange;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.ProActiveSecurityDescriptorHandler;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;

import functionalTests.FunctionalTest;
import functionalTests.SecurityTest;


/**
 * Test if the session exchange works.
 *
 * @author arnaud
 *
 */
public class SecurityTestSessionKeyExchange extends SecurityTest {

    /**
         *
         */
    private static final long serialVersionUID = 7116369482051444133L;
    private ProActiveSecurityManager psm = null;

    @Test
    public void action() throws Exception {
        ProActiveSecurityManager psm1 = psm.generateSiblingCertificate("caller");
        DummySecurityEntity entity1 = new DummySecurityEntity(psm1);

        ProActiveSecurityManager psm2 = psm.generateSiblingCertificate("Callee");
        DummySecurityEntity entity2 = new DummySecurityEntity(psm2);

        entity1.initiateSession(1, entity2);
    }

    @Before
    public void initTest() throws Exception {
        PolicyServer ps = ProActiveSecurityDescriptorHandler.createPolicyServer(SecurityTestSessionKeyExchange.class.getResource(
                    "/functionalTests/security/applicationPolicy.xml").getPath());
        psm = new ProActiveSecurityManager(ps);
    }
}
