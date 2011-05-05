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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objectweb.proactive.annotation.PublicAPI;


/**
 * IntentJoinPoint class definition. The class is initialized with 
 * all necessary informations for a stand alone method invocation.
 *
 * @author The ProActive Team
 * @see IntentHandler
 */
@PublicAPI
public class IntentJoinPoint {
    private Object invokeTarget;
    private Method method;
    private Object args[];

    /**
     * Default constructor.
     *
     * @param invokeTarget Invoke target object.
     * @param methodName Method name.
     * @param paramTypes The type of arguments.
     * @param args An array of parameter objects.
     */
    public IntentJoinPoint(Object invokeTarget, String methodName, Class<?>[] paramTypes, Object[] args)
            throws SecurityException, NoSuchMethodException {
        this.invokeTarget = invokeTarget;
        this.method = invokeTarget.getClass().getMethod(methodName, paramTypes);
        this.args = args;
    }

    /**
     * Invokes the method.
     *
     * @return The return object of the invoked method.
     * @throws IllegalAccessException If the underlying method is inaccessible.
     * @throws IllegalArgumentException If the invocation cannot be done (e.g. the specified object argument is not
     * an instance of the class or interface declaring the underlying method, wrong number of parameters, ...).
     * @throws InvocationTargetException If the invocation throws an exception.
     */
    public Object proceed() throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        return method.invoke(invokeTarget, args);
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

}
