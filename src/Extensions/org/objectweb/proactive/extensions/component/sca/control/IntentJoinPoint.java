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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * IntentJoinPoint class definition, the class initialize with all necessary informations for a standAlone method invocation
 */
public class IntentJoinPoint {
    private String medName;
    private Object invokeTarget;
    private Class paramTypes[];
    private Object args[];

    /**
     * 
     * @param obj invoke target object
     * @param m method name in string 
     * @param paramTypes the type of arguments
     * @param args an array of parameter objects
     */
    public IntentJoinPoint(String m, Object obj, Class[] paramTypes, Object[] args) {
        this.medName = m;
        this.invokeTarget = obj;
        this.paramTypes = paramTypes;
        this.args = args;
    }

    /**
     * 
     * @return get the method object by name
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    private Method getMethodByName() throws SecurityException, NoSuchMethodException {
        return invokeTarget.getClass().getMethod(medName, paramTypes);
    }

    /**
     * invoke the method
     * @return the return object of invoked method
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws SecurityException
     * @throws NoSuchMethodException
     */
    public Object proceed() throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException {
        return getMethodByName().invoke(invokeTarget, args);
    }
}
