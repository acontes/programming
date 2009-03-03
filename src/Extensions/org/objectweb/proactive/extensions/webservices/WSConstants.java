/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.extensions.webservices;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

import org.apache.soap.util.xml.QName;


/**
 * @author The ProActive Team
 * Utility constants for deploying active objects and components as Web Services
 */
public class WSConstants {


	public static final String AXIS_XML_PATH = "axis2/conf/axis2.xml";
	public static final String AXIS_REPOSITORY_PATH = "axis2/repository/";
	public static final String AXIS_SERVICES_PATH = "axis2/services/";
	public static final String AXIS_SERVLET = "/" + AXIS_SERVICES_PATH + "*";

    public static final Vector<String> disallowedMethods = new Vector<String>();

    static {
        disallowedMethods.addElement("equals");
        disallowedMethods.addElement("toString");
        disallowedMethods.addElement("runActivity");
        disallowedMethods.addElement("setProxy");
        disallowedMethods.addElement("getProxy");
        disallowedMethods.addElement("wait");
        disallowedMethods.addElement("notify");
        disallowedMethods.addElement("notifyAll");
        disallowedMethods.addElement("getClass");
        disallowedMethods.addElement("hashCode");
        // component methods
        disallowedMethods.addElement("setFcItfName");
        disallowedMethods.addElement("isFcInternalItf");
        disallowedMethods.addElement("setFcOwner");
        disallowedMethods.addElement("setFcItfOwner");
        disallowedMethods.addElement("setSenderItfID");
        disallowedMethods.addElement("getSenderItfID");
        disallowedMethods.addElement("setFcIsInternal");
        disallowedMethods.addElement("getFcItfName");
        disallowedMethods.addElement("getFcItfType");
        disallowedMethods.addElement("getFcItfOwner");
        disallowedMethods.addElement("isFcInternalItf");
        disallowedMethods.addElement("setFcItfImpl");
        disallowedMethods.addElement("getFcItfImpl");
        disallowedMethods.addElement("setFcType");
        disallowedMethods.addElement("getFcItfImpl");
        disallowedMethods.addElement("getFcItfImpl");
    }

    /* A vector containing all supported types by Apache Soap */
    protected static Vector<Class<?>> supportedTypes = new Vector<Class<?>>();

    static {
        supportedTypes.addElement(String.class);
        supportedTypes.addElement(Boolean.class);
        supportedTypes.addElement(Boolean.TYPE);
        supportedTypes.addElement(Double.class);
        supportedTypes.addElement(Double.TYPE);
        supportedTypes.addElement(Long.class);
        supportedTypes.addElement(Long.TYPE);
        supportedTypes.addElement(Float.class);
        supportedTypes.addElement(Float.TYPE);
        supportedTypes.addElement(Integer.class);
        supportedTypes.addElement(Integer.TYPE);
        supportedTypes.addElement(Short.class);
        supportedTypes.addElement(Byte.class);
        supportedTypes.addElement(Byte.TYPE);
        supportedTypes.addElement(BigDecimal.class);
        supportedTypes.addElement(GregorianCalendar.class);
        supportedTypes.addElement(Date.class);
        supportedTypes.addElement(QName.class);
        supportedTypes.addElement(Array.newInstance(Byte.TYPE, 0).getClass());
        supportedTypes.addElement(Array.newInstance(Boolean.TYPE, 0).getClass());
        supportedTypes.addElement(Array.newInstance(Double.TYPE, 0).getClass());
        supportedTypes.addElement(Array.newInstance(Long.TYPE, 0).getClass());
        supportedTypes.addElement(Array.newInstance(Float.TYPE, 0).getClass());
        supportedTypes.addElement(Array.newInstance(Integer.TYPE, 0).getClass());
        supportedTypes.addElement(Array.newInstance(Object.class, 0).getClass());
        supportedTypes.addElement(Vector.class);
        supportedTypes.addElement(Hashtable.class);
        supportedTypes.addElement(Map.class);
        supportedTypes.addElement(Enumeration.class);
    }
}
