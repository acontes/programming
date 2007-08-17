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
package org.objectweb.proactive.extra.infrastructuremanager.core;

import java.io.Serializable;


/**
 * @deprecated
 */
public class IMActionStatus implements Serializable {
    /**  */
	private static final long serialVersionUID = 4031245901810564047L;
	// Attributes
    private boolean successAction;
    private String status;
    private Exception exception;

    //----------------------------------------------------------------------//
    // Constructors

    /** ProActive compulsory no-args constructor */
    public IMActionStatus() {
    }

    public IMActionStatus(String status) {
        this.successAction = true;
        this.status = status;
    }

    public IMActionStatus(String status, Exception exception) {
        this.successAction = false;
        this.status = status;
        this.exception = exception;
    }

    //----------------------------------------------------------------------//
    // Accessors
    public boolean isSuccessAction() {
        return successAction;
    }

    public String getStatus() {
        return status;
    }

    public Exception getException() {
        return exception;
    }

    //----------------------------------------------------------------------//
    public String toString() {
        String actionStatus;
        if (this.successAction) {
            actionStatus = "success";
        } else {
            actionStatus = "failure";
        }
        return "Action " + actionStatus + " : " + this.status;
    }
}
