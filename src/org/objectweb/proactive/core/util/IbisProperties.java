/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
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
package org.objectweb.proactive.core.util;


/**
 * @author fhuet
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class IbisProperties {
    public static final String IBIS_DEFAULT_NAME_SERVER = "name_server";
    public static final String IBIS_DEFAULT_NAME_SERVER_POOL = "name_server_pool";
    public static final String IBIS_DEFAULT_POOL_HOST_NUMBER = "pool_host_number";
    private static java.util.Properties defaultProperties;

    static {
        defaultProperties = new java.util.Properties();
        IbisProperties.loadDefaultProperties();
    }

    public static void loadDefaultProperties() {
        defaultProperties.setProperty(IBIS_DEFAULT_NAME_SERVER, "localhost");
        defaultProperties.setProperty(IBIS_DEFAULT_NAME_SERVER_POOL, "rutget");
        defaultProperties.setProperty(IBIS_DEFAULT_POOL_HOST_NUMBER, "1");
        IbisProperties.addPropertiesToSystem(defaultProperties);
    }

    /**
     * Add a set of properties to the system properties
     * Does not overide any existing one
     *
     */
    protected static void addPropertiesToSystem(java.util.Properties p) {
        for (java.util.Enumeration e = p.propertyNames(); e.hasMoreElements();) {
            String s = (String) e.nextElement();

            //we don't override existing value
            if (System.getProperty(s) == null) {
                System.setProperty(s, p.getProperty(s));
            }
        }
    }

    public static void load() {
        //nothing, just called to trigger the loading of the class
    }
}
