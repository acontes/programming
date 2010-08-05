/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of
 *              Nice-Sophia Antipolis/ActiveEon
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

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.control.PABindingController;
import org.objectweb.proactive.core.component.control.PABindingControllerImpl;
import org.objectweb.proactive.extensions.component.sca.Utils;
import org.objectweb.proactive.extensions.component.sca.exceptions.ClassGenerationFailedException;
import org.objectweb.proactive.extensions.component.sca.gen.IntentServiceItfGenerator;


/**
 * Extension of the {@link PABindingController} interface to take care of SCA intents.
 *
 * @author The ProActive Team
 */
public class SCAPABindingControllerImpl extends PABindingControllerImpl {
    public SCAPABindingControllerImpl(Component owner) {
        super(owner);
    }

    protected void primitiveBindFc(String clientItfName, PAInterface serverItf)
            throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException {
        serverItf = PAFuture.getFutureValue(serverItf);
        PAInterface sItf = serverItf;
        try {
            Component owner = getFcItfOwner();
            if (Utils.getSCAIntentController(owner).hasIntentHandler(clientItfName)) {
                try {
                    sItf = (PAInterface) IntentServiceItfGenerator.instance().generateInterface(sItf, owner);
                } catch (ClassGenerationFailedException cgfe) {
                    controllerLogger
                            .error("could not generate intent interceptor for reference (client interface) " +
                                clientItfName + ": " + cgfe.getMessage());
                    IllegalBindingException ibe = new IllegalBindingException(
                        "could not generate intent interceptor for reference (client interface) " +
                            clientItfName + ": " + cgfe.getMessage());
                    ibe.initCause(cgfe);
                    throw ibe;
                }
            }
        } catch (NoSuchInterfaceException nsie) {
            // No SCAIntentController, nothing to do
        }
        super.primitiveBindFc(clientItfName, sItf);
    }
}
