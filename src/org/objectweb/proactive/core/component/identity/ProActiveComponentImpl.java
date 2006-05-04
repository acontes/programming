/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.component.identity;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.Type;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.component.ComponentParameters;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.component.config.ComponentConfigurationHandler;
import org.objectweb.proactive.core.component.controller.AbstractProActiveController;
import org.objectweb.proactive.core.component.controller.AbstractRequestHandler;
import org.objectweb.proactive.core.component.controller.ComponentParametersController;
import org.objectweb.proactive.core.component.controller.ProActiveBindingController;
import org.objectweb.proactive.core.component.controller.RequestHandler;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.gen.MetaObjectInterfaceClassGenerator;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.component.interception.InputInterceptor;
import org.objectweb.proactive.core.component.interception.OutputInterceptor;
import org.objectweb.proactive.core.component.representative.ProActiveComponentRepresentativeFactory;
import org.objectweb.proactive.core.component.type.ProActiveInterfaceType;
import org.objectweb.proactive.core.group.ProActiveComponentGroup;
import org.objectweb.proactive.core.group.ProActiveGroup;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * The base class for managing components. It builds the "membrane" in the
 * Fractal terminology : the controllers of the components.
 *
 * @author Matthieu Morel
 */
public class ProActiveComponentImpl extends AbstractRequestHandler implements ProActiveComponent, Interface,
    Serializable {
    protected static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS);

    // private ComponentParameters componentParameters;
//    private Interface[] interfaceReferences;
    private Map<String, Interface> functionalItfs = new HashMap<String, Interface>();
    private Map<String, Interface> controlItfs = new HashMap<String, Interface>();
    private Map<String, Interface> collectionItfsMembers = new HashMap<String, Interface>();
    private Body body;
    private RequestHandler firstControllerRequestHandler;

    // need Vector-specific operations for inserting elements
    private Vector<AbstractProActiveController> inputInterceptors = new Vector<AbstractProActiveController>();
    private Vector<AbstractProActiveController> outputInterceptors = new Vector<AbstractProActiveController>();

    public ProActiveComponentImpl() {
    }

    /**
     * Constructor for ProActiveComponent.
     *
     * @param componentParameters
     * @param myBody
     *            a reference on the body (required notably to get a reference
     *            on the request queue, used to control the life cycle of the
     *            component)
     * @throws InstantiationException
     */
    public ProActiveComponentImpl(ComponentParameters componentParameters,
        Body myBody) {
        this.body = myBody;
        boolean component_is_primitive = componentParameters.getHierarchicalType()
                                                            .equals(Constants.PRIMITIVE);

        // add interface references
        ArrayList<Interface> interface_references_list = new ArrayList<Interface>(4);

        // 1. component identity
        interface_references_list.add(this);

        // 2. control interfaces
        addControllers(componentParameters,
            component_is_primitive);

        // 3. external functional interfaces
        addFunctionalInterfaces(componentParameters, component_is_primitive);
        // put all in a table
//        interfaceReferences = (Interface[]) interface_references_list.toArray(new Interface[interface_references_list.size()]);

        if (logger.isDebugEnabled()) {
            logger.debug("created component : " +
                componentParameters.getControllerDescription().getName());
        }
    }

    /**
     * @param componentParameters
     * @param component_is_primitive
     * @param interface_references_list
     * @param current_component_is_primitive
     */
    private void addFunctionalInterfaces(
        ComponentParameters componentParameters,
        boolean component_is_primitive) {
        // ProActiveInterfaceType[] interface_types =
        // (ProActiveInterfaceType[])componentParameters.getComponentType()
        // .getFcInterfaceTypes();
        InterfaceType[] tmp = componentParameters.getComponentType()
                                                 .getFcInterfaceTypes();
        ProActiveInterfaceType[] interface_types = new ProActiveInterfaceType[tmp.length];
        System.arraycopy(tmp, 0, interface_types, 0, tmp.length);

        try {
            for (int i = 0; i < interface_types.length; i++) {
                ProActiveInterface itf_ref = null;

                if (interface_types[i].isFcCollectionItf() ) {
                	// members of collection itfs are created dynamically
                	continue;
                }
                if (interface_types[i].isFcMulticastItf()) {
                    itf_ref = createInterfaceOnGroupOfDelegatees(interface_types[i]);
                    //                    itf_ref = ProActiveComponentGroup.newComponentInterfaceGroup(interface_types[i],
                    //                            getFcItfOwner());
                } else {
                    // no interface generated for client itfs of primitive
                    // components
                    if (!(interface_types[i].isFcClientItf() &&
                            component_is_primitive)) {
                        // TODO_M multicast

//                        // if we have a COLLECTION CLIENT interface, we should see
//                        // the delegatee ("impl" field) as a group
//                        if (interface_types[i].isFcClientItf() &&
//                                interface_types[i].isFcCollectionItf()) {
//                            itf_ref = createInterfaceOnGroupOfDelegatees(interface_types[i]);
//                        }
                        
                        // if we have a server port of a PARALLEL component, we

                        // also create a group proxy on the delegatee field
                         if (componentParameters.getHierarchicalType()
                                                        .equals(Constants.PARALLEL) &&
                                (!interface_types[i].isFcClientItf())) {
                            // parallel component have a collective port on their
                            // server interfaces
                            itf_ref = createInterfaceOnGroupOfDelegatees(interface_types[i]);
                        } else {
                            itf_ref = MetaObjectInterfaceClassGenerator.instance()
                                                                       .generateFunctionalInterface(interface_types[i].getFcItfName(),
                                    this, interface_types[i]);
                            // server functional interfaces are external interfaces (at
                            // least they are tagged as external)
                        }

                        // set delegation link
                        if (componentParameters.getHierarchicalType()
                                                   .equals(Constants.PRIMITIVE)) {
                            // TODO_M no group case
                            if (!interface_types[i].isFcCollectionItf()) {
                                if (!interface_types[i].isFcClientItf()) {
                                    (itf_ref).setFcItfImpl(getReferenceOnBaseObject());
                                } else if (interface_types[i].isFcClientItf()) {
                                    (itf_ref).setFcItfImpl(null);
                                }
                            }
                        } else { // we have a composite component
                        	itf_ref = createInterfaceOnGroupOfDelegatees(interface_types[i]);
                        	
                        }
                    }

                    // non multicast client itf of primitive comp : do nothing
                }
                
                functionalItfs.put(interface_types[i].getFcItfName(), itf_ref);
                
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("cannot create interface references : " +
                    e.getMessage());
            }

            throw new RuntimeException("cannot create interface references : " +
                e.getMessage());
        }
    }

    private void addControllers(ComponentParameters componentParameters, boolean isPrimitive) {
        ComponentConfigurationHandler componentConfiguration = ProActiveComponentImpl.loadControllerConfiguration(componentParameters.getControllerDescription()
                                                                                                                                     .getControllersConfigFileLocation());
        Map controllers = componentConfiguration.getControllers();
        List inputInterceptorsSignatures = componentConfiguration.getInputInterceptors();
        inputInterceptors.setSize(inputInterceptorsSignatures.size());
        List outputInterceptorsSignatures = componentConfiguration.getOutputInterceptors();
        outputInterceptors.setSize(outputInterceptorsSignatures.size());

        // Properties controllers =
        // loadControllersConfiguration(componentParameters.getControllerDescription().getControllersConfigFile());
        Iterator iteratorOnControllers = controllers.keySet().iterator();
        AbstractProActiveController lastController = null;

        while (iteratorOnControllers.hasNext()) {
            Class<?> controllerClass = null;
            AbstractProActiveController currentController;
            String controllerItfName = (String) iteratorOnControllers.next();

            try {
                Class<?> controllerItf = Class.forName(controllerItfName);
                controllerClass = Class.forName((String) controllers.get(
                            controllerItf.getName()));
                Constructor<?> controllerClassConstructor = controllerClass.getConstructor(new Class[] {
                            Component.class
                        });
                currentController = (AbstractProActiveController) controllerClassConstructor.newInstance(new Object[] {
                            this
                        });

                // add interceptor
                if (InputInterceptor.class.isAssignableFrom(controllerClass)) {
                    // keep the sequence order of the interceptors
                    inputInterceptors.setElementAt(currentController,
                        inputInterceptorsSignatures.indexOf(
                            controllerClass.getName()));
                } else if (inputInterceptorsSignatures.contains(
                            controllerClass.getName())) {
                    logger.error(controllerClass.getName() +
                        " was specified as input interceptor in the configuration file, but it is not an input interceptor since it does not implement the InputInterceptor interface");
                }

                if (OutputInterceptor.class.isAssignableFrom(controllerClass)) {
                    outputInterceptors.setElementAt(currentController,
                        outputInterceptorsSignatures.indexOf(
                            controllerClass.getName()));
                } else if (outputInterceptorsSignatures.contains(
                            controllerClass.getName())) {
                    logger.error(controllerClass.getName() +
                        " was specified as output interceptor in the configuration file, but it is not an output interceptor since it does not implement the OutputInterceptor interface");
                }
            } catch (Exception e) {
                throw new ProActiveRuntimeException(
                    "could not create controller " +
                    controllers.get(controllerItfName) + " : " +
                    e.getMessage(), e);
            }

            // there are some special cases for some controllers
            if (ComponentParametersController.class.isAssignableFrom(
                        controllerClass)) {
                ((ComponentParametersController) currentController).setComponentParameters(componentParameters);
            }

            if (BindingController.class.isAssignableFrom(controllerClass)) {
                if ((componentParameters.getHierarchicalType()
                                            .equals(Constants.PRIMITIVE) &&
                        (componentParameters.getClientInterfaceTypes().length == 0))) {
                    // bindingController = null;
                    if (logger.isDebugEnabled()) {
                        logger.debug("user component class of '" +
                            componentParameters.getName() +
                            "' does not have any client interface. It will have no BindingController");
                    }

                    continue;
                }
            }

            if (ContentController.class.isAssignableFrom(controllerClass)) {
                if (isPrimitive) {
                    // no content controller here
                    continue;
                }
            }

            if (NameController.class.isAssignableFrom(controllerClass)) {
                ((NameController) currentController).setFcName(componentParameters.getName());
            }

            if (lastController != null) {
                lastController.setNextHandler(currentController);
            } else {
                firstControllerRequestHandler = currentController;
            }

            lastController = currentController;
            controlItfs.put(currentController.getFcItfName(), currentController);
        }

        // add the "component" control itfs
        lastController.setNextHandler(this);
    }

    /**
     * @param controllerConfigFileLocation
     *            the location of the configuration file
     * @return a xml parsing handler
     */
    public static ComponentConfigurationHandler loadControllerConfiguration(
        String controllerConfigFileLocation) {
        try {
            return ComponentConfigurationHandler.createComponentConfigurationHandler(controllerConfigFileLocation);
        } catch (Exception e) {
            logger.error("could not load controller config file : " +
                controllerConfigFileLocation +
                ". Reverting to default controllers configuration.");

            try {
                return ComponentConfigurationHandler.createComponentConfigurationHandler(ControllerDescription.DEFAULT_COMPONENT_CONFIG_FILE_LOCATION);
            } catch (Exception e1) {
                logger.error(
                    "could not load default controller config file either. Check that the default controller config file is available in your classpath at : " +
                    ControllerDescription.DEFAULT_COMPONENT_CONFIG_FILE_LOCATION);
                throw new ProActiveRuntimeException(
                    "could not load default controller config file either. Check that the default controller config file is available on your system at : " +
                    ControllerDescription.DEFAULT_COMPONENT_CONFIG_FILE_LOCATION,
                    e1);
            }
        }
    }

    // returns a generated interface reference, whose impl field is a group
    // It is able to handle multiple bindings
    private ProActiveInterface createInterfaceOnGroupOfDelegatees(
        ProActiveInterfaceType itfType) throws Exception {
        ProActiveInterface itf_ref = MetaObjectInterfaceClassGenerator.instance()
                                                                      .generateFunctionalInterface(itfType.getFcItfName(),
                this, itfType);

        // create a group of impl target objects
        ProActiveInterface itf_ref_group = ProActiveComponentGroup.newComponentInterfaceGroup(itfType,
                this);
        itf_ref.setFcItfImpl(itf_ref_group);
        return itf_ref;
    }

    /*
     * see {@link org.objectweb.fractal.api.Component#getFcInterface(String)}
     */
    public Object getFcInterface(String interfaceName)
        throws NoSuchInterfaceException {
    	
    	if (interfaceName.endsWith("-controller") || interfaceName.equals("component")) {
    		if (!controlItfs.containsKey(interfaceName)) throw new NoSuchInterfaceException(interfaceName);
    		return (controlItfs.get(interfaceName));
    	}
    	if (functionalItfs.containsKey(interfaceName)) {
    		return functionalItfs.get(interfaceName);
    	}
    	
    	// a member of a collection itf?
    	InterfaceType[] itfTypes = ((ComponentType)getFcType()).getFcInterfaceTypes();
    	for (int i = 0; i < itfTypes.length; i++) {
			InterfaceType type = itfTypes[i];
			if (type.isFcCollectionItf()) {
				if ((interfaceName.startsWith(type.getFcItfName()) && !type.getFcItfName().equals(interfaceName))) {
					if (collectionItfsMembers.containsKey(interfaceName)) {
						return collectionItfsMembers.get(interfaceName);
					} else {
//					 generate a new interface and add it to the list of members of collection its
	        		try {
						Interface clientItf = MetaObjectInterfaceClassGenerator.instance()
						.generateFunctionalInterface(interfaceName,
								this, (ProActiveInterfaceType)itfTypes[i]);
						collectionItfsMembers.put(interfaceName, clientItf);
						return clientItf;
					} catch (InterfaceGenerationFailedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					}
				}
			}
    	}

        throw new NoSuchInterfaceException(interfaceName);
    }

    /*
     * see {@link org.objectweb.fractal.api.Component#getFcInterfaces()}
     */
    public Object[] getFcInterfaces() {
        return functionalItfs.values().toArray();
    }

    /*
     * see {@link org.objectweb.fractal.api.Component#getFcType()}
     */
    public Type getFcType() {
        try {
            return ((ComponentParametersController) getFcInterface(Constants.COMPONENT_PARAMETERS_CONTROLLER)).getComponentParameters()
                    .getComponentType();
        } catch (NoSuchInterfaceException nsie) {
            throw new ProActiveRuntimeException("cannot retreive the type of the component",
                nsie);
        }
    }

    /**
     * see {@link org.objectweb.fractal.api.Interface#getFcItfName()}
     */
    public String getFcItfName() {
        return Constants.COMPONENT;
    }

    /**
     * see {@link org.objectweb.fractal.api.Interface#getFcItfOwner()}
     */
    public Component getFcItfOwner() {
        return (Component) this;
    }

    /**
     * see {@link org.objectweb.fractal.api.Interface#getFcItfType()}
     */
    public Type getFcItfType() {
        return getFcType();
    }

    /**
     * see {@link org.objectweb.fractal.api.Interface#isFcInternalItf()}
     */
    public boolean isFcInternalItf() {
        return true;
    }

    /**
     * Returns the base object. If the component is a composite, a basic
     * do-nothing instance of class Composite is returned.
     *
     * @return the base object underneath
     */
    public Object getReferenceOnBaseObject() {
        return getBody().getReifiedObject();
    }

    /**
     * @return a ComponentParameters instance, corresponding to the
     *         configuration of the current component
     */
    public ComponentParameters getComponentParameters()
        throws NoSuchInterfaceException {
        // return componentParameters;
        return ((ComponentParametersController) getFcInterface(Constants.COMPONENT_PARAMETERS_CONTROLLER)).getComponentParameters();
    }

    /**
     * @return the body of the current active object
     */
    public Body getBody() {
        return body;
    }

    /**
     * see
     * {@link org.objectweb.proactive.core.component.identity.ProActiveComponent#getID()}
     */
    public UniqueID getID() {
        return getBody().getID();
    }

    /**
     * see
     * {@link org.objectweb.proactive.core.component.identity.ProActiveComponent#getRepresentativeOnThis()}
     */
    public Component getRepresentativeOnThis() {
        try {
            return ProActiveComponentRepresentativeFactory.instance()
                                                          .createComponentRepresentative((ComponentType) getFcType(),
                getComponentParameters().getHierarchicalType(),
                ((StubObject) MOP.turnReified(body.getReifiedObject().getClass()
                                                  .getName(),
                    org.objectweb.proactive.core.Constants.DEFAULT_BODY_PROXY_CLASS_NAME,
                    new Object[] { body }, body.getReifiedObject())).getProxy(),
                getComponentParameters().getControllerDescription()
                    .getControllersConfigFileLocation());
        } catch (Exception e) {
            throw new ProActiveRuntimeException("This component could not generate a reference on itself",
                e);
        }
    }

    /**
     * @return the first controller request handler in the chain of controllers
     */
    public RequestHandler getControllerRequestHandler() {
        return firstControllerRequestHandler;
    }

    public List<AbstractProActiveController> getInputInterceptors() {
        return inputInterceptors;
    }

    public List<AbstractProActiveController> getOutputInterceptors() {
        return outputInterceptors;
    }
}
