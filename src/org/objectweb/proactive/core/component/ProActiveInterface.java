/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.core.component;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.Type;


/**
 * Abstract implementation of the Interface interface of the Fractal api.
 * As functional interfaces are specified for each component, they are generated at
 * instantiation time (bytecode generation), by subclassing this class.
 *
 * @author Matthieu Morel
 *
 */
public interface ProActiveInterface extends Interface {

    /**
     * Sets the isInternal.
     * @param isInternal The isInternal to set
     */
    public abstract void setFcIsInternal(boolean isInternal);

    /**
     * Sets the name.
     * @param name The name to set
     */
    public abstract void setFcItfName(String name);

    /**
     * Sets the owner.
     * @param owner The owner to set
     */
    public abstract void setFcItfOwner(Component owner);

    /**
     * Sets the type.
     * @param type The type to set
     */
    public abstract void setFcType(Type type);

    /**
     * getter
     * @return the delegatee
     */
    public abstract Object getFcItfImpl();

    /**
     * Sets the object to which this interface reference object should delegate
     * method calls.
     *
     * @param impl the object to which this interface reference object should
     *      delegate method calls.
     * @see #getFcItfImpl getFcItfImpl
     */
    public abstract void setFcItfImpl(final Object impl);
}
