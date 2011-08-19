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

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.etsi.uri.gcm.util.GCM;
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
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.proactive.core.component.control.PAContentController;
import org.objectweb.proactive.extensions.sca.adl.xml.SCAXMLLoader;
import org.objectweb.proactive.extensions.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;
import org.objectweb.proactive.extensions.sca.control.SCAPropertyController;
import org.objectweb.proactive.extensions.sca.exceptions.IncompatiblePropertyTypeException;
import org.objectweb.proactive.extensions.sca.exceptions.NoSuchPropertyException;


/**
 *
 * A SCA featured Factory, the only change is the method to create a new component
 */
public class SCABasicFactory extends BasicFactory {

    /**
     * Get the properties values in the the SCA composite file 
     */
    public List<String[]> getPropertiesValues() {
        UnboundInterfaceDetectorLoader uidl = (UnboundInterfaceDetectorLoader) lookupFc(BasicFactory.LOADER_BINDING);

        TypeBindingLoader bindl = (TypeBindingLoader) uidl.clientLoader;
        ImplementationLoader impll = (ImplementationLoader) bindl.clientLoader;
        AttributeLoader attrl = (AttributeLoader) impll.clientLoader;
        TypeLoader typl = (TypeLoader) attrl.clientLoader;
        InterfaceLoader itfl = (InterfaceLoader) typl.clientLoader;
        ArgumentComponentLoader compl = (ArgumentComponentLoader) itfl.clientLoader;
        ArgumentLoader argl = (ArgumentLoader) compl.clientLoader;

        SCAXMLLoader scaxmll = (SCAXMLLoader) argl.clientLoader;
        return scaxmll.getPropertiesValues();
    }

    /**
     * Get the intents values in the the SCA composite file 
     */
    public List<String[]> getIntents() {
        UnboundInterfaceDetectorLoader uidl = (UnboundInterfaceDetectorLoader) lookupFc(BasicFactory.LOADER_BINDING);

        TypeBindingLoader bindl = (TypeBindingLoader) uidl.clientLoader;
        ImplementationLoader impll = (ImplementationLoader) bindl.clientLoader;
        AttributeLoader attrl = (AttributeLoader) impll.clientLoader;
        TypeLoader typl = (TypeLoader) attrl.clientLoader;
        InterfaceLoader itfl = (InterfaceLoader) typl.clientLoader;
        ArgumentComponentLoader compl = (ArgumentComponentLoader) itfl.clientLoader;
        ArgumentLoader argl = (ArgumentLoader) compl.clientLoader;

        SCAXMLLoader scaxmll = (SCAXMLLoader) argl.clientLoader;
        return scaxmll.getIntents();
    }

    /**
     * Get the sub component of a composite by its name   
     */
    private Component getSubComponentByName(Component comp, String name) {
        Component subComp = null;
        try {
            PAContentController conttr = org.objectweb.proactive.core.component.Utils
                    .getPAContentController(comp);
            Component[] subComps = conttr.getFcSubComponents();

            for (Component component : subComps) {
                String compName = GCM.getNameController(component).getFcName();
                if (compName.equalsIgnoreCase(name)) {
                    subComp = component;
                    break;
                }
            }
        } catch (NoSuchInterfaceException ex) {
            Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return subComp;
    }

    /**
     * Add the properties in SCA composite into their corresponding components
     */
    protected void addPropertiesIntoComponent(Component comp) {
        List<String[]> properties = getPropertiesValues();
        if (properties != null) {
            try {
                for (String[] strings : properties) {
                    Component subComp = getSubComponentByName(comp, strings[0]);
                    if (subComp != null) {
                        SCAPropertyController scapcClient = org.objectweb.proactive.extensions.sca.Utils
                                .getSCAPropertyController(subComp);
                        scapcClient.setValue(strings[1], strings[2]);
                    } else {
                        System.err.println("problem on initialize property, sub-component :" + strings[0] +
                            " doesn't exist!");
                    }

                }
            } catch (NoSuchPropertyException ex) {
                Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IncompatiblePropertyTypeException ex) {
                Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchInterfaceException e) {
                Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Add the intents in SCA composite into their corresponding components
     */
    protected void addIntentsIntoComponent(Component comp) {
        List<String[]> intents = getIntents();
        if (intents != null) {
            try {
                for (String[] strings : intents) {
                    Component subComp = getSubComponentByName(comp, strings[0]);
                    if (subComp != null) {
                        SCAIntentController scaIntCtr = org.objectweb.proactive.extensions.sca.Utils
                                .getSCAIntentController(subComp);
                        try {
                            Class contentClass = Class.forName(strings[2]);
                            IntentHandler ih = (IntentHandler) contentClass.newInstance();
                            scaIntCtr.addIntentHandler(ih);
                        } catch (IllegalLifeCycleException ex) {
                            Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InstantiationException ex) {
                            Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IllegalAccessException ex) {
                            Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ClassNotFoundException e) {
                            Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, e);
                        }
                    } else {
                        System.err.println("problem on initialize intents, sub-component :" + strings[0] +
                            " doesn't exist!");
                    }
                }
            } catch (NoSuchInterfaceException ex) {
                Logger.getLogger(SCABasicFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    // Suppress unchecked warning to avoid to change Factory interface
    @SuppressWarnings("unchecked")
    @Override
    public Object newComponent(final String name, final Map context) throws ADLException {
        //System.err.println("use the right factory");
        Component comp = (Component) super.newComponent(name, context);
        addPropertiesIntoComponent(comp);
        addIntentsIntoComponent(comp);
        return comp;
    }
}
