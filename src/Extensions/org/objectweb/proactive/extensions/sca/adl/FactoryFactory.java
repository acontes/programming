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
package org.objectweb.proactive.extensions.sca.adl;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.BasicFactory;
import org.objectweb.fractal.adl.Factory;
import org.objectweb.fractal.adl.arguments.ArgumentComponentLoader;
import org.objectweb.fractal.adl.arguments.ArgumentLoader;
import org.objectweb.fractal.adl.attributes.AttributeLoader;
import org.objectweb.fractal.adl.bindings.TypeBindingLoader;
import org.objectweb.fractal.adl.bindings.UnboundInterfaceDetectorLoader;
import org.objectweb.fractal.adl.implementations.ImplementationLoader;
import org.objectweb.fractal.adl.interfaces.InterfaceLoader;
import org.objectweb.fractal.adl.xml.XMLLoader;
import org.objectweb.proactive.extensions.sca.adl.xml.SCAXMLLoader;


public class FactoryFactory {

    private static Factory newGetFactory() {
        System.err.println("use SCA factory");
        BasicFactory bf = (BasicFactory) org.objectweb.fractal.adl.FactoryFactory.getFactory();
//
//        final NodeMerger nm = new NodeMergerImpl();
//        final XMLNodeFactory nFact = new XMLNodeFactoryImpl();
//        final XMLLoader xmll = new XMLLoader();
//        final ArgumentLoader argl = new ArgumentLoader();
//        final ArgumentComponentLoader compl = new ArgumentComponentLoader();
//        final InterfaceLoader itfl = new InterfaceLoader();
//        final TypeLoader typl = new TypeLoader();
//        final AttributeLoader attrl = new AttributeLoader();
//        final ImplementationLoader impll = new ImplementationLoader();
//        final TypeBindingLoader bindl = new TypeBindingLoader();
//        // necessary to check inherited/overriden attributes
//        final UnboundInterfaceDetectorLoader uidl = new UnboundInterfaceDetectorLoader();
//        
        UnboundInterfaceDetectorLoader uidl = (UnboundInterfaceDetectorLoader) bf
                .lookupFc(BasicFactory.LOADER_BINDING);
        bf.unbindFc(BasicFactory.LOADER_BINDING);
        TypeBindingLoader bindl = (TypeBindingLoader) uidl.clientLoader;
        ImplementationLoader impll = (ImplementationLoader) bindl.clientLoader;
        AttributeLoader attrl = (AttributeLoader) impll.clientLoader;
        InterfaceLoader itfl = (InterfaceLoader) attrl.clientLoader;
        ArgumentComponentLoader compl = (ArgumentComponentLoader) itfl.clientLoader;
        ArgumentLoader argl = (ArgumentLoader) compl.clientLoader;
        argl.clientLoader = new SCAXMLLoader();
        bf.bindFc(BasicFactory.LOADER_BINDING, uidl);
        return bf;
    }

    public static Factory getFactory(final String factory, final String backend,
            final Map<Object, Object> context) throws ADLException {
        final Factory f = newGetFactory();
        context.put(org.objectweb.fractal.adl.FactoryFactory.BACKEND_PROPERTY_NAME, backend);
        final Map<?, ?> c = (Map<?, ?>) f.newComponent(factory, context);
        return (Factory) c.get("factory");
    }

    public static Factory getFactory() throws ADLException {
        System.err.println("use SCA factory");
        return getFactory(org.objectweb.proactive.core.component.adl.FactoryFactory.PROACTIVE_FACTORY,
                org.objectweb.proactive.core.component.adl.FactoryFactory.PROACTIVE_BACKEND, new HashMap());
    }

    public static Factory getNFFactory() throws ADLException {
        return getFactory(org.objectweb.proactive.core.component.adl.FactoryFactory.PROACTIVE_NFFACTORY,
                org.objectweb.proactive.core.component.adl.FactoryFactory.PROACTIVE_NFBACKEND, new HashMap());
    }
}
