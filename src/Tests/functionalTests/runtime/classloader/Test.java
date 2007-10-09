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
package functionalTests.runtime.classloader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import org.junit.Before;
import org.objectweb.proactive.api.ProActiveObject;
import org.objectweb.proactive.api.ProDeployment;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;

import functionalTests.FunctionalTest;
import static junit.framework.Assert.assertTrue;

/**
 * 2 steps hierarchical deployment with dynamic classloading through runtimes.
 * <p>
 * 1. A is created on VN1 where A.class is not available <br> => the class A is
 * asked to the initial deployment runtime <br>
 * 2. B is created from A, and creates a remote object C on a place where C is
 * not available. <br> => class C is asked to the runtime of A and B, which asks
 * to the initial deployment runtime.
 * <p>
 * There is no need to configure paths, as a deployment file with the correct
 * paths is created on the fly <br>
 *
 * @author Matthieu Morel
 */
public class Test extends FunctionalTest {
    ProActiveDescriptor descriptor;

    @Before
    public void initTest() throws Exception {
        PAProperties.PA_CLASSLOADER.setValue(PAProperties.TRUE);
        String oldFilePath = getClass()
                                 .getResource("/functionalTests/runtime/classloader/deployment.xml")
                                 .getPath();
        String newFilePath = oldFilePath.replaceFirst("deployment.xml",
                "deployment-tmp.xml");

        // if tests are run from the /compile directory : getParent for root directory 
        File userDir = new File(System.getProperty("user.dir"));
        String proactiveDir;
        if (userDir.getName().equals("compile")) {
            proactiveDir = userDir.getParent();
        } else {
            proactiveDir = userDir.getPath();
        }
        searchAndReplace(oldFilePath, newFilePath, "proactive.home",
            proactiveDir);
        descriptor = ProDeployment.getProactiveDescriptor(getClass()
                                                              .getResource("/functionalTests/runtime/classloader/deployment-tmp.xml")
                                                              .getPath());
        descriptor.activateMappings();
    }

    @org.junit.Test
    public void action() throws Exception {
        A a = (A) ProActiveObject.newActive("functionalTests.runtime.classloader.A",
                new Object[] {  }, descriptor.getVirtualNode("VN1").getNode());
        a.createActiveObjectB();

        assertTrue(true);
    }

    private void searchAndReplace(String oldFilePath, String newFilePath,
        String oldString, String newString) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(
                        oldFilePath));
            BufferedWriter writer = new BufferedWriter(new FileWriter(
                        newFilePath));
            while (true) {
                String oldLine = reader.readLine();
                if (oldLine == null) {
                    break;
                }
                String newLine = oldLine.replace(oldString, newString);
                writer.write(newLine);
                writer.newLine();
            }
            reader.close();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
