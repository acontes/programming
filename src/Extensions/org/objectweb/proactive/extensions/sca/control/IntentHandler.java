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

import java.io.Serializable;
import java.util.UUID;

import org.objectweb.proactive.annotation.PublicAPI;


/**
 * Abstract class implemented by intent handlers.
 * <br>
 * Intent handlers are useful for non functional features (e.g. security, logging, ...) which must be applied on
 * SCA/GCM components. Intent handlers intercepts calls on service methods or from reference methods. When the method
 * is invoked, the invocation is intercepted and the {@link #invoke(IntentJoinPoint)} method of the intent handler is
 * called. The given {@link IntentJoinPoint} instance allows to introspect the intercepted method (e.g. which
 * interface, which method, ...) and allows to resume the intercepted invocation method by calling the method
 * {@link IntentJoinPoint#proceed()}. If several intent handlers are added on the same method, they are executed in
 * the order in which they were added.
 *
 * @author The ProActive Team
 * @see IntentJoinPoint
 */
@PublicAPI
public abstract class IntentHandler implements Serializable {
    /**
     * Defines the actions performed by the intent handler.
     *
     * @param ijp The join point where the interception occurred.
     * @return The value returned by the intercepted method.
     */
    public abstract Object invoke(IntentJoinPoint ijp) throws Throwable;

    private String uuid = UUID.randomUUID().toString();

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof IntentHandler)) {
            return false;
        }
        IntentHandler ih = (IntentHandler) obj;
        return ih.uuid.equals(this.uuid);
    }

    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + uuid.hashCode();
        return hash;
    }
}