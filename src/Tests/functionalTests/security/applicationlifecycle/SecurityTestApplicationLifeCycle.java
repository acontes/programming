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
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
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
package functionalTests.security.applicationlifecycle;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.junit.Before;
import org.junit.Test;
import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.ProActiveSecurityDescriptorHandler;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;

import functionalTests.FunctionalTest;
import static junit.framework.Assert.*;

/**
 * Test the generation of entity certificate from an application one
 * @author arnaud
 *
 */
public class SecurityTestApplicationLifeCycle extends FunctionalTest {
    private static final long serialVersionUID = 1312765218867401690L;
    private ProActiveSecurityManager psm = null;
    private ProActiveSecurityManager psm2 = null;

    @Test
    public void action() throws Exception {
        psm = psm.generateSiblingCertificate("subcert");

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bout);

        out.writeObject(psm);
        out.close();

        // Get the bytes of the serialized object
        byte[] buf = bout.toByteArray();

        // retrieve policyserver
        ByteArrayInputStream bis = new ByteArrayInputStream(buf);
        ObjectInputStream is = new ObjectInputStream(bis);

        psm2 = (ProActiveSecurityManager) is.readObject();

        assertNotNull(psm2);
    }

    @Before
    public void initTest() throws Exception {
        PolicyServer ps = ProActiveSecurityDescriptorHandler.createPolicyServer(SecurityTestApplicationLifeCycle.class.getResource(
                    "/functionalTests/security/applicationPolicy.xml").getPath());
        psm = new ProActiveSecurityManager(ps);
    }
}
