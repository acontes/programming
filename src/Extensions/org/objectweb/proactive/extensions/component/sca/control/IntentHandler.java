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

import java.io.Serializable;


/**
 * <p>
 * Interface implemented by intent handlers.
 * </p>
 * 
 * <p>
 * An intent handler is a regular SCA component (primitive or composite) which
 * implements an intent policy. Intent policies are usually non functional
 * features (e.g. transaction, security, logging) which must be applied on
 * SCA business components.
 * </p>
 * <ul>
 * <li>introspecting the intercepted method (so called join point in AOP terms)
 * by retrieving the component reference, the method and the arguments,</li>
 * <li>invoking the intercepted method (method {@link
 * IntentJoinPoint#proceed()}).</li>
 * </ul>
 * 
 * <p>
 * Several intent handlers may be added on the same method. In this case, they
 * are executed in the order in which they were added.
 * </p>
 */
public interface IntentHandler extends Serializable {

    /**
     * This method defines the actions performed by this intent handler.
     * 
     * @param ijp  the join point where the interception occured
     * @return     the value returned by the intercepted method
     */
    public Object invoke(IntentJoinPoint ijp) throws Throwable;

}