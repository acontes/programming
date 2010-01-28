/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
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
package org.objectweb.proactive.core.component.adl.types;

import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.interfaces.Interface;
import org.objectweb.fractal.adl.interfaces.InterfaceContainer;
import org.objectweb.fractal.adl.types.TypeErrors;
import org.objectweb.fractal.adl.types.TypeInterface;
import org.objectweb.fractal.adl.types.TypeLoader;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactory;


/**
 * A {@link org.objectweb.fractal.adl.Loader} to check {@link ProActiveTypeInterface}
 * nodes in definitions. This loader checks that the Java interfaces specified
 * in these nodes exist.
 */
public class ProActiveTypeLoader extends TypeLoader {
    @Override
    protected void checkInterfaceContainer(final InterfaceContainer container,
            final Map<Object, Object> context) throws ADLException {
        Interface[] itfs = container.getInterfaces();
        for (int i = 0; i < itfs.length; i++) {
            Interface itf = itfs[i];
            if (itf instanceof TypeInterface) {
                String signature = ((TypeInterface) itf).getSignature();
                if (signature == null) {
                    throw new ADLException(TypeErrors.SIGNATURE_MISSING, itf);
                } else {
                    try {
                        interfaceCodeLoaderItf.loadInterface(signature, context);
                    } catch (final ADLException e) {
                        throw e;
                    }
                }
                String role = ((TypeInterface) itf).getRole();
                if (role == null) {
                    throw new ADLException(TypeErrors.ROLE_MISSING, itf);
                } else {
                    if (!role.equals("client") && !role.equals("server")) {
                        throw new ADLException(TypeErrors.INVALID_ROLE, itf, role);
                    }
                }
                String contingency = ((TypeInterface) itf).getContingency();
                if (contingency != null) {
                    if (!contingency.equals("mandatory") && !contingency.equals("optional")) {
                        throw new ADLException(TypeErrors.INVALID_CONTINGENCY, itf, contingency);
                    }
                }

                String cardinality = ((TypeInterface) itf).getCardinality();
                if (cardinality != null) {
                    if (!cardinality.equals("singleton") && !cardinality.equals("collection") &&
                        !cardinality.equals(ProActiveTypeFactory.MULTICAST_CARDINALITY) &&
                        !cardinality.equals(ProActiveTypeFactory.GATHER_CARDINALITY)) {
                        throw new ADLException(TypeErrors.INVALID_CARDINALITY, itf, cardinality);
                    }
                }
            }
        }
    }
}
