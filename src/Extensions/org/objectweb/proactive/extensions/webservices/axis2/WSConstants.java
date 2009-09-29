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
package org.objectweb.proactive.extensions.webservices.axis2;

/**
 * Utility constants for deploying active objects and components as Web Services
 *
 * @author The ProActive Team
 */
public class WSConstants extends org.objectweb.proactive.extensions.webservices.WSConstants {

    public static final String PROACTIVE_JAR;
    static {
        String temp = WSConstants.class.getResource("/org/objectweb/proactive").getPath();
        temp = temp.substring(0, temp.indexOf('!'));
        PROACTIVE_JAR = temp.substring(temp.indexOf(':') + 1);
    }

    public static final String AXIS_XML_ENTRY = "org/objectweb/proactive/extensions/webservices/axis2/conf/axis2.xml";
    public static final String AXIS_REPOSITORY_ENTRY = "org/objectweb/proactive/extensions/webservices/axis2/repository/";

}
