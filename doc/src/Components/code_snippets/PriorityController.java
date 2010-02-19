/*
 * ################################################################
 *
 *                    Fractal GCM Management API
 *
 * Copyright (C) 2009 INRIA, University of
 *                    Nice-Sophia Antipolis, ActiveEon
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: proactive@ow2.org
 *
 * Authors: INRIA, University of Nice-Sophia Antipolis, ActiveEon.
 *
 * ################################################################
 */
package org.etsi.uri.gcm.api.control;

import org.objectweb.fractal.api.NoSuchInterfaceException;


/**
 * Component interface to control the priority for requests of methods exposed by the interfaces of the
 * component to which it belongs.
 *
 * @author INRIA, University of Nice-Sophia Antipolis, ActiveEon
 */
public interface PriorityController {

    /**
     * All the possible kinds of priority for a request on the component to which this interface belongs.
     * 
     */
    public enum RequestPriority {
        /**
         * Functional priority
         */
        F,
        /**
         * Non-Functional priority
         */
        NF1,
        /**
         * Non-Functional priority higher than Functional priority (F)
         */
        NF2,
        /**
         * Non-Functional priority higher than Functional priority (F) and Non-Functional priorities (NF1 and
         * NF2)
         */
        NF3;
    }

    /**
     * Set priority of a method exposed by a server interface of the component to which this interface belongs.
     * 
     * @param itfName Name of an interface of the component to which this interface belongs.
     * @param methodName Name of a method exposed by the interface corresponding to the given interface name.
     * @param parameterTypes Parameter types of the method corresponding to the given method name.
     * @param priority Priority to set to the method corresponding to the given method name.
     * @throws NoSuchInterfaceException If there is no such server interface.
     * @throws NoSuchMethodException If there is no such method.
     */
    public void setGCMPriority(String itfName, String methodName, Class<?>[] parameterTypes,
            RequestPriority priority) throws NoSuchInterfaceException, NoSuchMethodException;

    /**
     * Get the priority of a method exposed by a server interface of the component to which this interface
     * belongs.
     * 
     * @param itfName Name of an interface of the component to which this interface belongs.
     * @param methodName Name of a method exposed by the interface corresponding to the given interface name.
     * @param parameterTypes Parameter types of the method corresponding to the given method name.
     * @return Priority of this method.
     * @throws NoSuchInterfaceException If there is no such server interface.
     * @throws NoSuchMethodException If there is no such method.
     */
    public RequestPriority getGCMPriority(String itfName, String methodName, Class<?>[] parameterTypes)
            throws NoSuchInterfaceException, NoSuchMethodException;
}
