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
package org.objectweb.proactive.branchnbound.core;

import java.io.Serializable;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


/**
 * Wrapper result for a branch and bound problem.
 *
 * @author Alexandre di Costanzo
 *
 * Created on Sep 28, 2006
 */
public class BnBResult<Value extends Comparable<Value>> implements Comparable<BnBInternalResult>,
    Serializable {
    private Value resultValue;
    private Throwable exception;

    /**
     * The no args and empty constructor for ProActive new active.
     */
    public BnBResult() {
        // The empty no arg constructor for activing the object
    }

    /**
     * Construct a new result with the given value.
     * @param value the real result.
     */
    public BnBResult(Value value) {
        this.resultValue = value;
    }

    /**
     * Construct a new result with an exception.
     * @param exception the exception.
     */
    public BnBResult(Throwable exception) {
        this.exception = exception;
    }

    /**
     * @return the real value.
     * @throws Throwable thrown if the value is an exception.
     */
    public Value getTheValue() throws Throwable {
        if (this.exception != null) {
            throw this.exception;
        }
        return this.resultValue;
    }

    /**
     * @return the exception or null if there is a value.
     */
    public Throwable getTheExcpetion() {
        return this.exception;
    }

    /**
     * @return true if the result is an exception, false else.
     */
    public BooleanWrapper isAnException() {
        return new BooleanWrapper(this.exception != null);
    }

    /**
     * Called on the real value or the exception.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        return (this.exception == null) ? this.resultValue.equals(obj)
                                        : this.exception.equals(obj);
    }

    /**
     * Called on the real value or the exception.
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return (this.exception == null) ? this.resultValue.hashCode()
                                        : this.exception.hashCode();
    }

    /**
     * Called on the real value or the exception.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return (this.exception == null) ? this.resultValue.toString()
                                        : this.exception.toString();
    }

    /**
     * Called on the real value.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public int compareTo(BnBInternalResult o) {
        if (o.isAnException().booleanValue()) {
            throw new RuntimeException(
                "Cannot compare a result with an exception");
        } else {
            try {
                return this.resultValue.compareTo((Value) o.getTheValue());
            } catch (Throwable e) {
                // Cannot happen
                throw new RuntimeException("Houston we have a problem!");
            }
        }
    }
}
