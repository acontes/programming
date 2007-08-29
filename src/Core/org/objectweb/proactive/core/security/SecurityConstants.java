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
package org.objectweb.proactive.core.security;


/**
 * @author ProActive Team
 * Defines usefull constants for security
 *
 */
public abstract class SecurityConstants {
    public static final String XML_CERTIFICATE = "/Policy/Certificate";
    public static final String XML_PRIVATE_KEY = "/Policy/PrivateKey";
    public static final String XML_TRUSTED_CERTIFICATION_AUTHORITY = "/Policy/TrustedCertificationAuthority/CertificationAuthority";
    public static final String XML_CERTIFICATION_AUTHORITY_CERTIFICATE = "Certificate";
    
    public static final int MAX_SESSION_VALIDATION_WAIT = 30;

    public static final int ENTITY_TYPE_UNKNOWN = -1;
    public static final int ENTITY_TYPE_OBJECT = 0;
    public static final int ENTITY_TYPE_NODE = 1;
    public static final int ENTITY_TYPE_RUNTIME = 2;
    public static final int ENTITY_TYPE_APPLICATION = 3;
    public static final int ENTITY_TYPE_USER = 4;
    public static final int ENTITY_TYPE_DOMAIN = 5;

    public static final String ENTITY_STRING_UNKNOWN = "Unknown";
    public static final String ENTITY_STRING_OBJECT = "Object";
    public static final String ENTITY_STRING_NODE = "Node";
    public static final String ENTITY_STRING_RUNTIME = "Runtime";
    public static final String ENTITY_STRING_APPLICATION = "Application";
    public static final String ENTITY_STRING_USER = "User";
    public static final String ENTITY_STRING_DOMAIN = "Domain";
    
    public static String typeToString(int type) {
    	switch (type) {
    	case ENTITY_TYPE_OBJECT:
    		return ENTITY_STRING_OBJECT;
    	case ENTITY_TYPE_NODE:
    		return ENTITY_STRING_NODE;
    	case ENTITY_TYPE_RUNTIME:
    		return ENTITY_STRING_RUNTIME;
    	case ENTITY_TYPE_APPLICATION:
    		return ENTITY_STRING_APPLICATION;
    	case ENTITY_TYPE_USER:
    		return ENTITY_STRING_USER;
    	case ENTITY_TYPE_DOMAIN:
    		return ENTITY_STRING_DOMAIN;
    	default:
    		System.out.println("Unknown type");
    		return ENTITY_STRING_UNKNOWN;
    	}
    }
    
    public static int typeToInt(String type) {
    	if (ENTITY_STRING_OBJECT.equalsIgnoreCase(type)) {
    		return ENTITY_TYPE_OBJECT;
    	} else if (ENTITY_STRING_NODE.equalsIgnoreCase(type)) {
    		return ENTITY_TYPE_NODE;
    	} else if (ENTITY_STRING_RUNTIME.equalsIgnoreCase(type)) {
    		return ENTITY_TYPE_RUNTIME;
    	} else if (ENTITY_STRING_APPLICATION.equalsIgnoreCase(type)) {
    		return ENTITY_TYPE_APPLICATION;
    	} else if (ENTITY_STRING_USER.equalsIgnoreCase(type)) {
    		return ENTITY_TYPE_USER;
    	} else if (ENTITY_STRING_DOMAIN.equalsIgnoreCase(type)) {
    		return ENTITY_TYPE_DOMAIN;
    	} else {
    		System.out.println("Unknown type");
    		return ENTITY_TYPE_UNKNOWN;
    	}
    }
    
    public static int getParentType(int type) {
    	switch (type) {
    	case ENTITY_TYPE_OBJECT:
    	case ENTITY_TYPE_NODE:
		case ENTITY_TYPE_RUNTIME:
			return ENTITY_TYPE_APPLICATION;
		case ENTITY_TYPE_APPLICATION:
			return ENTITY_TYPE_USER;
		case ENTITY_TYPE_USER:
			return ENTITY_TYPE_DOMAIN;
		case ENTITY_TYPE_DOMAIN:
			return ENTITY_TYPE_DOMAIN;
		default:
			return ENTITY_TYPE_UNKNOWN;
    	}
    }
}
