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
package org.objectweb.proactive.extensions.sca.adl.types;

import java.util.Map;

import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.adl.ContextMap;
import org.objectweb.fractal.adl.util.ClassLoaderHelper;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.type.InterfaceType; //import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.adl.types.PATypeBuilder;
import org.objectweb.proactive.extensions.sca.Utils;


/**
 * @author The ProActive Team
 */
public class SCAPATypeBuilder extends PATypeBuilder {
    @Override
    public Object createComponentType(final String name, final Object[] interfaceTypes, final Object context)
            throws Exception {
        ClassLoader loader = ClassLoaderHelper.getClassLoader(this, context);

        Component bootstrap = null;
        if (context != null) {
            bootstrap = (Component) ((Map) context).get("bootstrap");
        }
        if (bootstrap == null) {
            Map ctxt = ContextMap.instance(); // new HashMap();
            ctxt.put("classloader", loader);
            //bootstrap = Utils.getBootstrapComponent(ctxt);
            bootstrap = Utils.getBootstrapComponent();
        }
        InterfaceType[] types = new InterfaceType[interfaceTypes.length];
        for (int i = 0; i < types.length; ++i) {
            types[i] = (InterfaceType) interfaceTypes[i];
        }
        return GCM.getGCMTypeFactory(bootstrap).createFcType(types);
    }
}
