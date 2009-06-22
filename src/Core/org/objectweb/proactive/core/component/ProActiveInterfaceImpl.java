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
package org.objectweb.proactive.core.component;

import java.io.Serializable;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.Type;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.component.representative.ProActiveComponentRepresentativeImpl;


/**
 * Abstract implementation of the {@link Interface} interface of the Fractal API.
 * <p>
 * As functional interfaces are specified for each component, they are generated
 * at instantiation time (bytecode generation), by subclassing this class.
 *
 * @author The ProActive Team
 */
public abstract class ProActiveInterfaceImpl implements ProActiveInterface, Serializable {
    private Component owner;
    private String name;
    private Type type;
    private boolean isInternal;

    public ProActiveInterfaceImpl() {
    }

    /*
     *
     * @see org.objectweb.fractal.api.Interface#getFcItfOwner()
     */
    public Component getFcItfOwner() {
        return owner;
    }

    /*
     *
     * @see org.objectweb.fractal.api.Interface#getFcItfName()
     */
    public String getFcItfName() {
        return name;
    }

    /*
     *
     * @see org.objectweb.fractal.api.Interface#getFcItfType()
     */
    public Type getFcItfType() {
        return type;
    }

    /*
     *
     * @see org.objectweb.fractal.api.Interface#isFcInternalItf()
     */
    public boolean isFcInternalItf() {
        return isInternal;
    }

    // The four following setters are only used once after the interface generation

    /**
     * Sets the isInternal.
     * @param isInternal The isInternal to set
     */
    public void setFcIsInternal(boolean isInternal) {
        this.isInternal = isInternal;
    }

    /**
     * Sets the name.
     * @param name The name to set
     */
    public void setFcItfName(String name) {
        this.name = name;
    }

    /**
     * Sets the owner.
     * @param owner The owner to set
     */
    public void setFcItfOwner(Component owner) {
        this.owner = owner;
    }

    /**
     * Sets the type.
     *
     * @param type The type to set
     */
    public void setFcType(Type type) {
        this.type = type;
    }

    /**
     *
     * @see org.objectweb.proactive.core.component.ProActiveInterface#getFcItfImpl()
     */
    public abstract Object getFcItfImpl();

    /**
     *
     * @see org.objectweb.proactive.core.component.ProActiveInterface#setFcItfImpl(java.lang.Object)
     */
    public abstract void setFcItfImpl(final Object impl);

    @Override
    public boolean equals(Object anObject) {
        if (Interface.class.isAssignableFrom(anObject.getClass())) {

            Interface itf = (Interface) anObject;
            boolean nameEquality = itf.getFcItfName().equals(name);

            // Are the two itf belong to the same component?
            UniqueID objectID = ((ProActiveComponentRepresentativeImpl) itf.getFcItfOwner()).getID();
            UniqueID thisID = ((ProActiveComponentRepresentativeImpl) owner).getID();
            boolean ownerEquality = objectID.equals(thisID);

            return nameEquality && ownerEquality;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode() + owner.hashCode();
    }

    @Override
    public String toString() {
        String string = "name : " + getFcItfName() + "\n" + //            "componentIdentity : " + getFcItfOwner() + "\n" + "type : " +
            getFcItfType() + "\n" + "isInternal : " + isFcInternalItf() + "\n";
        return string;
    }
}
