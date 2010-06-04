/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive;

import java.util.List;
import java.util.Map;

import org.objectweb.proactive.api.PAVersion;
import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.config.PAProperty;
import org.objectweb.proactive.core.util.ProActiveInet;


public class Main {

    /**
     * Returns the version number
     *
     * @return String
     */
    public static String getProActiveVersion() {
        return "2008-07-10 11:59:18";
    }

    public static void main(String[] args) {
        ProActiveInet pInet = ProActiveInet.getInstance();

        System.out.println("\t\t--------------------");
        System.out.println("\t\tProActive " + PAVersion.getProActiveVersion());
        System.out.println("\t\t--------------------");
        System.out.println();
        System.out.println();

        String localAddress = null;
        localAddress = pInet.getInetAddress().getHostAddress();
        System.out.println("Local IP Address: " + localAddress);

        System.out.println("Config dir: " + Constants.USER_CONFIG_DIR);
        System.out.println();

        System.out.println("Network setup:");
        for (String s : pInet.listAllInetAddress()) {
            System.out.println("\t" + s);
        }
        System.out.println();

        System.out.println("Available properties:");
        Map<Class<?>, List<PAProperty>> allProperties = PAProperties.getAllProperties();
        for (Class<?> cl : allProperties.keySet()) {
            System.out.println("From class " + cl.getCanonicalName());
            for (PAProperty prop : allProperties.get(cl)) {
                System.out.println("\t" + prop.getType() + "\t" + prop.getName() + " [" +
                    prop.getValueAsString() + "]");
            }
        }
    }
}
