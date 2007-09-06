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

import java.io.Serializable;

import org.objectweb.proactive.core.security.exceptions.IncompatiblePolicyException;


/**
 *  This class represents security attributes granted to a targeted communication
 *
 */
public class Communication implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -4624752375050653382L;
	public static final int REQUIRED = 1;
    public static final int DENIED = -1;
    public static final int OPTIONAL = 0;
    
    public static final String STRING_REQUIRED = "required";
	public static final String STRING_OPTIONAL = "optional";
	public static final String STRING_DENIED = "denied";

    /* indicates if authentication is required,optional or denied */
    private int authentication;

    /* indicates if confidentiality is required,optional or denied */
    private int confidentiality;

    /* indicates if integrity is required,optional or denied */
    private int integrity;

    /* indicates if communication between active objects is allowed or not */
    private boolean communication;

    /**
     * Default constructor, initialize a policy with communication attribute sets to allowed and
     * authentication,confidentiality and integrity set to optional
     */
    public Communication() {
        authentication = DENIED;
        confidentiality = DENIED;
        integrity = DENIED;
        communication = false;
    }

    /**
     * Copy constructor
     */
    public Communication(Communication com) {
        authentication = com.authentication;
        confidentiality = com.confidentiality;
        integrity = com.integrity;
        communication = com.communication;
    }

    /**
     * This method specifies if communication is allowed
     * @param authentication specifies if authentication is required, optional, or denied
     * @param confidentiality specifies if confidentiality is required, optional, or denied
     * @param integrity specifies if integrity is required, optional, or denied
     */
    public Communication(boolean allowed, int authentication,
        int confidentiality, int integrity) {
        this.communication = allowed;
        this.authentication = authentication;
        this.confidentiality = confidentiality;
        this.integrity = integrity;
    }

    /**
     * Method isAuthenticationEnabled.
     * @return boolean true if authentication is required
     */
    public boolean isAuthenticationEnabled() {
        return authentication == REQUIRED;
    }

    /**
     * Method isConfidentialityEnabled.
     * @return boolean true if confidentiality is required
     */
    public boolean isConfidentialityEnabled() {
        return confidentiality == REQUIRED;
    }

    /**
     * Method isIntegrityEnabled.
     * @return boolean true if integrity is required
     */
    public boolean isIntegrityEnabled() {
        return integrity == REQUIRED;
    }

    /**
     * Method isAuthenticationForbidden.
     * @return boolean true if confidentiality is forbidden
     */
    public boolean isAuthenticationForbidden() {
        return authentication == DENIED;
    }

    /**
     * Method isConfidentialityForbidden.
     * @return boolean true if confidentiality is forbidden
     */
    public boolean isConfidentialityForbidden() {
        return confidentiality == DENIED;
    }

    /**
     * Method isIntegrityForbidden.
     * @return boolean true if integrity is forbidden
     */
    public boolean isIntegrityForbidden() {
        return integrity == DENIED;
    }

    /**
     * Method isCommunicationAllowed.
     * @return boolean true if confidentiality is allowed
     */
    public boolean isCommunicationAllowed() {
        return communication;
    }

    @Override
    public String toString() {
        return "Com : " + communication + " Auth : " + authentication +
        " Conf : " + confidentiality + " Integrity : " + integrity + "\n";
    }

    /**
     * Method computePolicy.
     * @param from the client policy
     * @param to the server policy
     * @return Policy returns a computation of the from and server policies
     * @throws IncompatiblePolicyException policies are incomptables, conflicting communication attributes
     */
    public static Communication computeCommunication(Communication from,
        Communication to) throws IncompatiblePolicyException {
        if (from.isCommunicationAllowed() && to.isCommunicationAllowed()) {
            if (((from.authentication == REQUIRED) &&
                    (to.authentication == DENIED)) ||
                    ((from.confidentiality == REQUIRED) &&
                    (to.confidentiality == DENIED)) ||
                    ((from.integrity == REQUIRED) && (to.integrity == DENIED)) ||
                    ((from.authentication == DENIED) &&
                    (to.authentication == REQUIRED)) ||
                    ((from.confidentiality == DENIED) &&
                    (to.confidentiality == REQUIRED)) ||
                    ((from.integrity == DENIED) && (to.integrity == REQUIRED))) {
                throw new IncompatiblePolicyException("incompatible policies");
            }
            return new Communication(true,
                realValue(from.authentication + to.authentication),
                realValue(from.confidentiality + to.confidentiality),
                realValue(from.integrity + to.integrity));
        }
        return null;
    }

    private static int realValue(int value) {
        if (value > 0) {
            return REQUIRED;
        }
        if (value < 0) {
            return DENIED;
        }
        return OPTIONAL;
    }

    /**
     * @return communication
     */
    public boolean getCommunication() {
        return communication;
    }

    /**
     * @param i
     */
    public void setCommunication(boolean i) {
        communication = i;
    }

	public int getAuthentication() {
		return authentication;
	}

	public int getConfidentiality() {
		return confidentiality;
	}

	public int getIntegrity() {
		return integrity;
	}
    
    public static int valToInt(String val) {
		if (val.equalsIgnoreCase(STRING_REQUIRED)) {
			return REQUIRED;
		} else if (val.equalsIgnoreCase(STRING_OPTIONAL)) {
			return OPTIONAL;
		} else if (val.equalsIgnoreCase(STRING_DENIED)) {
			return DENIED;
		}
		return -1;
	}

	public static String valToString(int val) {
		switch (val) {
		case REQUIRED:
			return STRING_REQUIRED;
		case OPTIONAL:
			return STRING_OPTIONAL;
		case DENIED:
			return STRING_DENIED;
		default:
			return null;
		}
	}
}
