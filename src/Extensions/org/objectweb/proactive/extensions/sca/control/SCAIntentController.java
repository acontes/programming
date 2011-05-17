/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.sca.control;

import java.util.List;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.extensions.sca.exceptions.NoSuchIntentHandlerException;


/**
 * Component interface to control SCA intents of the SCA/GCM component to which it belongs.
 *
 * @author The ProActive Team
 */
@PublicAPI
public interface SCAIntentController {
    /**
     * Adds the given intent handler on all service and reference interfaces.
     *
     * @param intentHandler The intent handler to add.
     * @throws NoSuchInterfaceException If there is no {@link LifeCycleController}.
     * @throws IllegalLifeCycleException If the component is not stopped.
     */
    public void addIntentHandler(IntentHandler intentHandler) throws NoSuchInterfaceException,
            IllegalLifeCycleException;

    /**
     * Adds the given intent handler on the given service or reference interface.
     *
     * @param intentHandler The intent handler to add.
     * @param itfName The service or reference interface name.
     * @throws NoSuchInterfaceException If there is no {@link LifeCycleController} or if the service or
     * reference interface does not exist.
     * @throws IllegalLifeCycleException If the component is not stopped.
     */
    public void addIntentHandler(IntentHandler intentHandler, String itfName)
            throws NoSuchInterfaceException, IllegalLifeCycleException;

    /**
    * Adds the given intent handler on the given method of the given service or reference interface.
    *
    * @param intentHandler The intent handler to add.
    * @param itfName The service or reference interface name.
    * @param methodName The method name.
    * @throws NoSuchInterfaceException If there is no {@link LifeCycleController} or if the service or
    * reference interface does not exist.
    * @throws NoSuchMethodException If the method does not exist.
    * @throws IllegalLifeCycleException If the component is not stopped.
    */
    public void addIntentHandler(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException, IllegalLifeCycleException;

    /**
     * Indicates if any method in the given service or reference interface has intent handlers.
     *
     * @param itfName The service or reference interface name.
     * @return True if the given service or reference interface has at least one intent applied to 
     * one methods of given interface, false otherwise.
     * @throws NoSuchInterfaceException If the service or reference interface does not exist.
     */
    public boolean hasAtleastOneIntentHandler(String itfName) throws NoSuchInterfaceException;

    /**
     * Indicates if the component has intent handlers, i.e. if at least one of its service or reference interfaces
     * has intent handlers.
     *
     * @return True if the component has intent handlers, i.e. if at least one of its service or reference interfaces
     * has intent handlers.
     */
    public boolean hasIntentHandler();

    /**
     * Indicates if the given service or reference interface has intent handlers.
     *
     * @param itfName The service or reference interface name.
     * @return True if the given service or reference interface has at least one intent applied 
     * to all methods of given interface, false otherwise.
     * @throws NoSuchInterfaceException If the service or reference interface does not exist.
     */
    public boolean hasIntentHandler(String itfName) throws NoSuchInterfaceException;

    /**
     * Indicates if the given method of the given service or reference interface has intent handlers.
     *
     * @param itfName The service or reference interface name.
     * @param methodName The method name.
     * @return True if the given method of the given service or reference interface has intent handlers, false
     * otherwise.
     * @throws NoSuchInterfaceException If the service or reference interface does not exist.
     * @throws NoSuchMethodException If the method does not exist.
     */
    public boolean hasIntentHandler(String itfName, String methodName) throws NoSuchInterfaceException,
            NoSuchMethodException;

    /**
     * Returns the list of all intent handlers associated with any service and reference interfaces.
     *
     * @return The list of all intent handlers associated with any service and reference interfaces.
     */
    //    public List<IntentHandler> listAllIntentHandler();
    /**
     * Returns the list of all intent handlers associated with all service and reference interfaces.
     *
     * @return The list of all intent handlers associated with all service and reference interfaces.
     */
    public List<IntentHandler> listIntentHandler();

    /**
     * Returns the list of all intent handlers associated with the given service or reference interface.
     * Returned intent handlers must applied on every existing methods.
     *
     * @param itfName The service or reference interface name.
     * @return The list of all intent handlers associated with the given service or reference interface.
     * @throws NoSuchInterfaceException If the service or reference interface does not exist.
     */
    public List<IntentHandler> listIntentHandler(String itfName) throws NoSuchInterfaceException;

    /**
     * Returns the list of all intent handlers associated with the given method of the given service or reference
     * interface.
     *
     * @param itfName The service or reference interface name.
     * @param methodName The method name.
     * @return The list of all intent handlers associated with the given method of the given service or reference
     * interface.
     * @throws NoSuchInterfaceException If the service or reference interface does not exist.
     * @throws NoSuchMethodException If the method does not exist.
     */
    public List<IntentHandler> listIntentHandler(String itfName, String methodName)
            throws NoSuchInterfaceException, NoSuchMethodException;

    /**
     * Removes the given intent handler on all service and reference interfaces where the intent is set.
     *
     * @param intentHandler The intent handler to remove.
     * @throws NoSuchIntentHandlerException If the intent does not exist.
     * @throws NoSuchInterfaceException If there is no {@link LifeCycleController}.
     * @throws IllegalLifeCycleException If the component is not stopped.
     */
    public void removeIntentHandler(IntentHandler intentHandler) throws NoSuchIntentHandlerException,
            NoSuchInterfaceException, IllegalLifeCycleException;

    /**
     * Removes the given intent handler on the given service or reference interface.
     *
     * @param intentHandler The intent handler to remove.
     * @param itfName The service or reference interface name.
     * @throws NoSuchIntentHandlerException If the intent does not exist.
     * @throws NoSuchInterfaceException If there is no {@link LifeCycleController} or if the service or
     * reference interface does not exist.
     * @throws IllegalLifeCycleException If the component is not stopped.
     */
    public void removeIntentHandler(IntentHandler intentHandler, String itfName)
            throws NoSuchIntentHandlerException, NoSuchInterfaceException, IllegalLifeCycleException;

    /**
    * Removes the given intent handler on the given method of the given service or reference interface.
    *
    * @param intentHandler The intent handler to remove.
    * @param itfName The service or reference interface name.
    * @param methodName The method name.
    * @throws NoSuchIntentHandlerException If the intent does not exist.
    * @throws NoSuchInterfaceException If there is no {@link LifeCycleController} or if the service or
    * reference interface does not exist.
    * @throws NoSuchMethodException If the method does not exist.
    * @throws IllegalLifeCycleException If the component is not stopped.
    */
    public void removeIntentHandler(IntentHandler intentHandler, String itfName, String methodName)
            throws NoSuchIntentHandlerException, NoSuchInterfaceException, NoSuchMethodException,
            IllegalLifeCycleException;
}
