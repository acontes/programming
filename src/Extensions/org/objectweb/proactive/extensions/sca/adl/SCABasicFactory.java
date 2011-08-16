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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.adl.BasicFactory;
import org.objectweb.fractal.adl.arguments.ArgumentComponentLoader;
import org.objectweb.fractal.adl.arguments.ArgumentLoader;
import org.objectweb.fractal.adl.attributes.AttributeLoader;
import org.objectweb.fractal.adl.bindings.TypeBindingLoader;
import org.objectweb.fractal.adl.bindings.UnboundInterfaceDetectorLoader;
import org.objectweb.fractal.adl.implementations.ImplementationLoader;
import org.objectweb.fractal.adl.interfaces.InterfaceLoader;
import org.objectweb.fractal.adl.types.TypeLoader;
import org.objectweb.fractal.adl.xml.XMLLoader;
import org.objectweb.fractal.adl.xml.XMLNodeFactory;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.proactive.extensions.sca.adl.xml.SCAXMLLoader;
import org.objectweb.proactive.extensions.sca.control.SCAPropertyController;
import org.objectweb.proactive.extensions.sca.exceptions.IncompatiblePropertyTypeException;
import org.objectweb.proactive.extensions.sca.exceptions.NoSuchPropertyException;

/**
 *
 * A SCA featured Factory, the only change is the method to create a new component
 */
public class SCABasicFactory extends BasicFactory {
    
    protected Map propertiesValues;
    
    public SCABasicFactory() {
        propertiesValues = new HashMap();
    }
    
    public Map getPropertiesValues() {
        UnboundInterfaceDetectorLoader uidl = (UnboundInterfaceDetectorLoader) lookupFc(BasicFactory.LOADER_BINDING);        
        
        TypeBindingLoader bindl = (TypeBindingLoader) uidl.clientLoader;
        ImplementationLoader impll = (ImplementationLoader) bindl.clientLoader;
        AttributeLoader attrl = (AttributeLoader) impll.clientLoader;
        TypeLoader typl = (TypeLoader) attrl.clientLoader;
        InterfaceLoader itfl = (InterfaceLoader) typl.clientLoader;
        ArgumentComponentLoader compl = (ArgumentComponentLoader) itfl.clientLoader;
        ArgumentLoader argl = (ArgumentLoader) compl.clientLoader;
        XMLLoader xmll = (XMLLoader) argl.clientLoader;
        XMLNodeFactory nFac = xmll.nodeFactoryItf;
        
        SCAXMLLoader scaxmll = (SCAXMLLoader) argl.clientLoader;
        return scaxmll.getPropertiesValues();
    }

    // Suppress unchecked warning to avoid to change Factory interface
    @SuppressWarnings("unchecked")
    public Object newComponent(final String name, final Map context)
            throws ADLException {
        //System.err.println("use the right factory");
        Component rtr = (Component) super.newComponent(name, context);
        Map<String, String> tmp = getPropertiesValues();
        if(tmp==null){
            return rtr;
        }
        for (String key : tmp.keySet()) {
            try {
                //System.err.println("DEBUGGG === Key: " + key + ", Value: " + tmp.get(key));
                SCAPropertyController scapcClient = org.objectweb.proactive.extensions.sca.Utils.getSCAPropertyController(rtr);
                scapcClient.setValue(key, tmp.get(key));
            } catch (NoSuchPropertyException ex) {
                Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IncompatiblePropertyTypeException ex) {
                Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchInterfaceException ex) {
                Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return rtr;
    }
}
