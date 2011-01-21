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
package org.objectweb.proactive.extensions.sca.representative;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.proactive.core.component.ComponentParameters;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.representative.PAComponentRepresentativeImpl;
import org.objectweb.proactive.extensions.sca.Constants;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;
import org.objectweb.proactive.extensions.sca.gen.IntentServiceItfGenerator;


/**
 * @author The ProActive Team
 */
public class SCAPAComponentRepresentativeImpl extends PAComponentRepresentativeImpl {

    public SCAPAComponentRepresentativeImpl(ComponentParameters componentParam) {
        super(componentParam);
    }

    public SCAPAComponentRepresentativeImpl(ComponentType componentType, String hierarchicalType,
            String controllersConfigFileLocation) {
        super(componentType, hierarchicalType, controllersConfigFileLocation);
    }

    /*
     * @see org.objectweb.proactive.core.component.representative.PAComponentRepresentativeImpl#getFcInterface(String)
     */
    public Object getFcInterface(String interfaceName) throws NoSuchInterfaceException {
        Object itfObjetct = super.getFcInterface(interfaceName);
        //    	System.err.println(stubOnBaseObject.getClass().getName()+" DEBUG "+interfaceName);
        //    	if(interfaceName.endsWith("intent-controller"))
        //    	{
        //    		try {
        //				throw new Exception("shit1");
        //			} catch (Exception e) {
        //				System.err.println(stubOnBaseObject.getClass().getName()+" DEBUG "+interfaceName+" exception catched!");
        //				e.printStackTrace();
        //			}
        //    	}

        if (fcInterfaceReferences.containsKey(interfaceName)) {
            SCAIntentController scaic = (SCAIntentController) super
                    .getFcInterface(Constants.SCA_INTENT_CONTROLLER);
            if (scaic.intentHandlerExists(interfaceName)) {
                //scaic.printInfo();
                PAInterface sItf = (PAInterface) itfObjetct;
                try {
                    sItf = (PAInterface) IntentServiceItfGenerator.instance().generateInterface(itfObjetct,
                            interfaceName, this);
                } catch (ClassGenerationFailedException cgfe) {
                    logger.error("could not generate intent interceptor for reference (client interface) " +
                        interfaceName + ": " + cgfe.getMessage());
                }
                return sItf;
            }
        }
        return itfObjetct;
    }
}
