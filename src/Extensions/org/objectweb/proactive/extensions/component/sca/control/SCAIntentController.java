/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.component.sca.control;

import java.util.List;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;


/**
 * Intent control interface for SCA components.
 */
public interface SCAIntentController {
    /**
     * Add the specified intent handler on all service and reference methods of
     * the current component.
     * 
     * @param handler  the intent handler to add
     * @throw IllegalLifeCycleException  if the component is not stopped
     */
    public void addFcIntentHandler(IntentHandler handler) throws IllegalLifeCycleException;

    /**
     * Add the specified intent handler on all interfaces (business or control)
     * which match the specified interface filter.
     * 
     * @param handler  the intent handler to add
     * @param filter   the interface filter
     * @throw IllegalLifeCycleException  if the component is not stopped
     */
    /*public void addFcIntentHandler(
        IntentHandler handler, InterfaceFilter filter )
    throws IllegalLifeCycleException;
     */
    /**
     * Add the specified intent handler on all pairs interface (business or
     * control), method which match the specified filter.
     * 
     * @param handler  the intent handler to add
     * @param filter   the filter for interface, method pairs
     * @throw IllegalLifeCycleException  if the component is not stopped
     * @since 1.0
     */
    /*
        public void addFcIntentHandler(
            IntentHandler handler, InterfaceMethodFilter filter )
        throws IllegalLifeCycleException;
        
     *//**
                 * Add the specified intent handler on the service or reference interface
                 * whose name is specified.
                 * 
                 * @param handler  the intent handler to add
                 * @param name     the interface name
                 * @throws NoSuchInterfaceException  if the interface does not exist
                 * @since 0.4.4
                 */
    /*
        public void addFcIntentHandler( IntentHandler handler, String name )
        throws NoSuchInterfaceException;
     */
    /**
     * Add the specified intent handler on the method of the service or
     * reference interface whose name is specified.
     * 
     * @param handler  the intent handler to add
     * @param name     the interface name
     * @param method   the method
     * @throws NoSuchInterfaceException  if the interface does not exist
     * @throws NoSuchMethodException     if the method does not exist
     * @since 1.0
     */
    /*
        public void addFcIntentHandler(
            IntentHandler handler, String name, Method method )
        throws NoSuchInterfaceException, NoSuchMethodException;

     *//**
                 * Return the list of all intent handlers associated with the interface
                 * (service or reference) whose name is specified.
                 * 
                 * @param name  the interface name
                 * @throws NoSuchInterfaceException  if the interface does not exist
                 * @since 0.4.4
                 */
    public List<IntentHandler> listFcIntentHandler(String name) throws NoSuchInterfaceException;

    /**
     * Return the list of all intent handlers associated with the method of the
     * interface (service or reference) whose name is specified.
     * 
     * @param name    the interface name
     * @param method  the method
     * @throws NoSuchInterfaceException  if the interface does not exist
     * @throws NoSuchMethodException     if the method does not exist
     * @since 1.0
     */
    /*
        public List<IntentHandler> listFcIntentHandler( String name, Method method )
        throws NoSuchInterfaceException, NoSuchMethodException;
     */
    /**
     * Remove the specified intent handler on all interfaces (service and
     * reference) of the current component.
     * 
     * @param handler  the intent handler to remove
     * @since 0.4.4
     */
    public void removeFcIntentHandler(IntentHandler handler);

    /**
     * Remove the specified intent handler on the interface (service or
     * reference) whose name is specified.
     * 
     * @param handler  the intent handler to remove
     * @param name     the interface name
     * @throws NoSuchInterfaceException  if the interface does not exist
     * @since 0.4.4
     */
    /*
        public void removeFcIntentHandler( IntentHandler handler, String name )
        throws NoSuchInterfaceException;
     */
    /**
     * Remove the specified intent handler on the method of the interface
     * (service or reference) whose name is specified.
     * 
     * @param handler  the intent handler to remove
     * @param name     the interface name
     * @param method   the method
     * @throws NoSuchInterfaceException  if the interface does not exist
     * @throws NoSuchMethodException     if the method does not exist
     * @since 1.0
     */
    /*
        public void removeFcIntentHandler(
            IntentHandler handler, String name, Method method )
        throws NoSuchInterfaceException, NoSuchMethodException;*/
}
