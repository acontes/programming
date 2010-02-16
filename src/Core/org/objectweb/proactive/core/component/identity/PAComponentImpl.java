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
package org.objectweb.proactive.core.component.identity;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.etsi.uri.gcm.api.type.GCMTypeFactory;
import org.etsi.uri.gcm.util.GCM;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Interface;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.Type;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.migration.MigrationException;
import org.objectweb.proactive.core.component.ComponentParameters;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.PAInterfaceImpl;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.config.ComponentConfigurationHandler;
import org.objectweb.proactive.core.component.controller.AbstractPAController;
import org.objectweb.proactive.core.component.controller.ControllerState;
import org.objectweb.proactive.core.component.controller.ControllerStateDuplication;
import org.objectweb.proactive.core.component.controller.PAController;
import org.objectweb.proactive.core.component.controller.PAGCMLifeCycleController;
import org.objectweb.proactive.core.component.controller.PAGCMLifeCycleControllerImpl;
import org.objectweb.proactive.core.component.controller.PAMembraneController;
import org.objectweb.proactive.core.component.controller.PAMembraneControllerImpl;
import org.objectweb.proactive.core.component.controller.PANameController;
import org.objectweb.proactive.core.component.exceptions.InterfaceGenerationFailedException;
import org.objectweb.proactive.core.component.gen.MetaObjectInterfaceClassGenerator;
import org.objectweb.proactive.core.component.group.PAComponentGroup;
import org.objectweb.proactive.core.component.interception.InputInterceptor;
import org.objectweb.proactive.core.component.interception.OutputInterceptor;
import org.objectweb.proactive.core.component.representative.PAComponentRepresentativeFactory;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * The base class for managing components. It builds the "membrane" in the
 * Fractal terminology : the controllers of the components.
 *
 * @author The ProActive Team
 */
public class PAComponentImpl implements PAComponent, Serializable {
    protected static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS);
    private transient PAComponent representativeOnMyself = null;
    private ComponentParameters componentParameters;
    private Map<String, Interface> serverItfs = new HashMap<String, Interface>();
    private Map<String, Interface> clientItfs = new HashMap<String, Interface>();
    private Map<String, Interface> controlItfs = new HashMap<String, Interface>();
    private Map<String, Interface> collectionItfsMembers = new HashMap<String, Interface>();
    private Body body;

    // need Vector-specific operations for inserting elements
    private Vector<Interface> inputInterceptors = new Vector<Interface>();
    private Vector<Interface> outputInterceptors = new Vector<Interface>();

    public PAComponentImpl() {
    }

    /**
     * Constructor for PAComponent.
     *
     * @param componentParameters
     * @param myBody
     *            a reference on the body (required notably to get a reference
     *            on the request queue, used to control the life cycle of the
     *            component)
     * @throws InstantiationException
     */
    public PAComponentImpl(ComponentParameters componentParameters, Body myBody) {
        this.body = myBody;
        this.componentParameters = componentParameters;
        boolean component_is_primitive = componentParameters.getHierarchicalType()
                .equals(Constants.PRIMITIVE);

        // add interface references
        // ArrayList<Interface> interface_references_list = new ArrayList<Interface>(4);

        // 1. component identity
        //interface_references_list.add(this);
        ComponentType nfType = componentParameters.getComponentNFType();
        ControllerDescription ctrlDesc = componentParameters.getControllerDescription();

        // 2. control interfaces
        if (nfType != null) { // The nf type is specified
            if (!ctrlDesc.configFileIsSpecified()) { // There is no file containing the nf configuration
                generateNfType(nfType, component_is_primitive);
            } else { // The nfType and a config file is specified. We have to check that the specified nfType corresponds to the interfaces defined in the config file
                checkCompatibility();
                addControllers(component_is_primitive);
            }
        } else { // No NFTYpe, means that it has to be generated from the config file
            addControllers(component_is_primitive);
        }

        // 3. external functional interfaces
        addFunctionalInterfaces(component_is_primitive);

        for (Interface itf : controlItfs.values()) {
            // TODO Check with component controller
            PAController itfImpl = (PAController) ((PAInterfaceImpl) itf).getFcItfImpl();
            if (itfImpl != null) { // due to non-functional interface implemented using component
                itfImpl.initController();
            }
        }

        // put all in a table
        // interfaceReferences = interface_references_list.toArray(new Interface[interface_references_list.size()]);
        if (logger.isDebugEnabled()) {
            logger.debug("created component : " + componentParameters.getControllerDescription().getName());
        }

        // PAActiveObject.setImmediateService("getComponentParameters");
    }

    private void addMandatoryControllers(Vector<InterfaceType> nftype) throws Exception {
        Component boot = GCM.getBootstrapComponent(); // Getting the Fractal-GCM-ProActive bootstrap component
        GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);

        PAGCMInterfaceType itfType = (PAGCMInterfaceType) type_factory.createFcItfType(
                Constants.LIFECYCLE_CONTROLLER,
                /* LIFECYCLE CONTROLLER */PAGCMLifeCycleController.class.getName(), TypeFactory.SERVER,
                TypeFactory.MANDATORY, TypeFactory.SINGLE);
        PAInterface controller = createController(itfType, PAGCMLifeCycleControllerImpl.class);
        controlItfs.put(controller.getFcItfName(), controller);
        nftype.add((InterfaceType) controller.getFcItfType());

        itfType = (PAGCMInterfaceType) type_factory.createFcItfType(Constants.NAME_CONTROLLER,
        /* NAME CONTROLLER */NameController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY,
                TypeFactory.SINGLE);
        controller = createController(itfType, PANameController.class);
        ((NameController) controller).setFcName(componentParameters.getName());
        controlItfs.put(controller.getFcItfName(), controller);
        nftype.add((InterfaceType) controller.getFcItfType());
    }

    private void generateNfType(ComponentType nfType, boolean isPrimitive) {
        InterfaceType[] tmp = nfType.getFcInterfaceTypes();
        PAGCMInterfaceType[] interface_types = new PAGCMInterfaceType[tmp.length];
        System.arraycopy(tmp, 0, interface_types, 0, tmp.length);
        PAInterface itf_ref = null;
        Class<?> controllerItf = null;

        try {
            Vector<InterfaceType> nftype = new Vector<InterfaceType>();
            addMandatoryControllers(nftype);
            for (int i = 0; i < interface_types.length; i++) {
                // Component parameters and name controller have to be managed when the controller is actually added
                controllerItf = Class.forName(interface_types[i].getFcItfSignature());

                if (!specialCasesForNfType(controllerItf, isPrimitive, interface_types[i],
                        this.componentParameters, nftype)) {
                    if (interface_types[i].isFcCollectionItf()) {
                        // members of collection itfs are created dynamically
                        continue;
                    }
                    if (interface_types[i].isGCMMulticastItf() && interface_types[i].isFcClientItf()) {//TODO : This piece of code has to be tested
                        itf_ref = createInterfaceOnGroupOfDelegatees(interface_types[i]);
                    } else {
                        itf_ref = MetaObjectInterfaceClassGenerator.instance().generateInterface(
                                interface_types[i].getFcItfName(), this, interface_types[i],
                                interface_types[i].isInternal(), false);
                    }

                    controlItfs.put(interface_types[i].getFcItfName(), itf_ref);
                    nftype.add((InterfaceType) itf_ref.getFcItfType());
                }
            }

            try {//Setting the real NF type, as some controllers may not be generated
                Component boot = GCM.getBootstrapComponent();
                GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);
                InterfaceType[] nf = new InterfaceType[nftype.size()];
                nftype.toArray(nf);
                this.componentParameters.setComponentNFType(type_factory.createFcType(nf));
            } catch (Exception e) {
                e.printStackTrace();
                logger.warn("NF type could not be set");
            }

        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("cannot create interface references : " + e.getMessage());
            }
            throw new RuntimeException("cannot create interface references : " + e.getMessage());
        }
    }

    private boolean specialCasesForNfType(Class<?> controllerItf, boolean isPrimitive,
            PAGCMInterfaceType itfType, ComponentParameters componentParam, Vector<InterfaceType> nftype)
            throws Exception {

        if (PAMembraneController.class.isAssignableFrom(controllerItf) && !itfType.isFcClientItf() &&
            !itfType.isInternal()) {
            PAInterface controller = createController(itfType, PAMembraneControllerImpl.class);
            controlItfs.put(controller.getFcItfName(), controller);
            nftype.add((InterfaceType) controller.getFcItfType());
            return true; // The membrane controller is a very special case. As soon as the interface is included inside the non-functional type, 
            //the controller has to be generated.Indeed, the membrane controller is the default controller for manipulating the membrane. 
        }

        if (ContentController.class.isAssignableFrom(controllerItf) && !itfType.isFcClientItf() &&
            !itfType.isInternal()) {
            if (isPrimitive) {
                return true; // No external server content controller for primitive component
            }

            return false;
        }

        if (BindingController.class.isAssignableFrom(controllerItf) && !itfType.isFcClientItf() &&
            !itfType.isInternal()) {
            if (isPrimitive && (Utils.getClientItfTypes(componentParam.getComponentType()).length == 0)) {
                // The binding controller is not generated for a component without client interfaces
                if (logger.isDebugEnabled()) {
                    logger.debug("user component class of '" + componentParam.getName() +
                        "' does not have any client interface. It will have no BindingController");
                }
                return true;//The BindingController is ignored
            }
            return false;//The BindingController is generated
        }

        if (NameController.class.isAssignableFrom(controllerItf) && !itfType.isFcClientItf() &&
            !itfType.isInternal()) { /* Mandatory controller, we don't have to recreate it */
            return true;
        }

        if (LifeCycleController.class.isAssignableFrom(controllerItf) && !itfType.isFcClientItf() &&
            !itfType.isInternal()) { /* Mandatory controller, we don't have to recreate it */
            return true;
        }
        return false;
    }

    private PAInterface createController(PAGCMInterfaceType itfType, Class<?> controllerClass)
            throws Exception {
        PAInterface itfToReturn = null;
        AbstractPAController currentController = null;
        Constructor<?> controllerClassConstructor = controllerClass
                .getConstructor(new Class[] { Component.class });
        currentController = (AbstractPAController) controllerClassConstructor
                .newInstance(new Object[] { this });
        itfToReturn = MetaObjectInterfaceClassGenerator.instance().generateInterface(itfType.getFcItfName(),
                this, itfType, itfType.isInternal(), false);
        itfToReturn.setFcItfImpl(currentController);
        return itfToReturn;
    }

    private void checkCompatibility() { // TODO : This method is called when a nfType is given with a config file. It has to check that both correspond
    }

    /**
     * @param componentParameters
     * @param component_is_primitive
     */
    private void addFunctionalInterfaces(boolean component_is_primitive) {
        InterfaceType[] tmp = componentParameters.getComponentType().getFcInterfaceTypes();
        PAGCMInterfaceType[] interface_types = new PAGCMInterfaceType[tmp.length];
        System.arraycopy(tmp, 0, interface_types, 0, tmp.length);

        try {
            for (int i = 0; i < interface_types.length; i++) {
                PAInterface itf_ref = null;

                if (interface_types[i].isFcCollectionItf()) {
                    // members of collection itfs are created dynamically
                    continue;
                }
                if (interface_types[i].isGCMMulticastItf()) {
                    itf_ref = createInterfaceOnGroupOfDelegatees(interface_types[i]);
                } else {
                    // no interface generated for client itfs of primitive
                    // components
                    if (!(interface_types[i].isFcClientItf() && component_is_primitive)) {
                        itf_ref = MetaObjectInterfaceClassGenerator.instance().generateFunctionalInterface(
                                interface_types[i].getFcItfName(), this, interface_types[i]);
                        // server functional interfaces are external interfaces (at
                        // least they are tagged as external)

                        // set delegation link
                        if (componentParameters.getHierarchicalType().equals(Constants.PRIMITIVE)) {
                            if (!interface_types[i].isFcCollectionItf()) {
                                if (!interface_types[i].isFcClientItf()) {
                                    (itf_ref).setFcItfImpl(getReferenceOnBaseObject());
                                } else {
                                    (itf_ref).setFcItfImpl(null);
                                }
                            }
                        }
                    }

                    // non multicast client itf of primitive comp : do nothing
                }

                if (!interface_types[i].isFcClientItf()) {
                    //System.err.println("SERVER" + interface_types[i].getFcItfName() + itf_ref );
                    serverItfs.put(interface_types[i].getFcItfName(), itf_ref);
                } else if (itf_ref != null) {
                    //System.err.println("CLIENT" + interface_types[i].getFcItfName() + itf_ref );
                    clientItfs.put(interface_types[i].getFcItfName(), itf_ref);
                }
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("cannot create interface references : " + e.getMessage());
            }
            e.printStackTrace();
            throw new RuntimeException("cannot create interface references : " + e.getMessage(), e);
        }
    }

    private void addControllers(boolean isPrimitive) {
        ComponentConfigurationHandler componentConfiguration = PAComponentImpl
                .loadControllerConfiguration(componentParameters.getControllerDescription()
                        .getControllersConfigFileLocation());
        Map<String, String> controllers = componentConfiguration.getControllers();
        List<String> inputInterceptorsSignatures = componentConfiguration.getInputInterceptors();
        inputInterceptors.setSize(inputInterceptorsSignatures.size());
        List<String> outputInterceptorsSignatures = componentConfiguration.getOutputInterceptors();
        outputInterceptors.setSize(outputInterceptorsSignatures.size());
        Vector<InterfaceType> nfType = new Vector<InterfaceType>();

        // Properties controllers =
        // loadControllersConfiguration(componentParameters.getControllerDescription().getControllersConfigFile());
        Iterator<String> iteratorOnControllers = controllers.keySet().iterator();

        while (iteratorOnControllers.hasNext()) {
            Class<?> controllerClass = null;
            AbstractPAController currentController = null;
            PAInterface theController = null;
            String controllerItfName = iteratorOnControllers.next();

            try {
                if (null == controllerItfName) {
                    throw new Exception("You must specify the java interface of a controller.");
                }
                Class<?> controllerItf = Class.forName(controllerItfName);
                if (null == controllers.get(controllerItf.getName())) {
                    throw new Exception(
                        "You must specify the java implementation for the controller describe by the interface " +
                            controllerItfName + ".");
                }

                controllerClass = Class.forName(controllers.get(controllerItf.getName()));

                //create Binding and Content controllers only if necessary
                if (BindingController.class.isAssignableFrom(controllerClass) &&
                    componentParameters.getHierarchicalType().equals(Constants.PRIMITIVE) &&
                    (componentParameters.getClientInterfaceTypes().length == 0)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Component '" + componentParameters.getName() +
                            "' does not have any client interface, cancel the BindingController creation");
                    }
                    continue;
                }
                if (isPrimitive && ContentController.class.isAssignableFrom(controllerClass)) {
                    // no content controller for a primitive component
                    continue;
                }

                Constructor<?> controllerClassConstructor = controllerClass
                        .getConstructor(new Class[] { Component.class });
                currentController = (AbstractPAController) controllerClassConstructor
                        .newInstance(new Object[] { this });
                theController = createController((PAGCMInterfaceType) currentController.getFcItfType(),
                        controllerClass);

                // add interceptor
                if (InputInterceptor.class.isAssignableFrom(controllerClass)) {
                    // keep the sequence order of the interceptors
                    inputInterceptors.setElementAt(theController, inputInterceptorsSignatures
                            .indexOf(controllerClass.getName()));
                } else if (inputInterceptorsSignatures.contains(controllerClass.getName())) {
                    logger
                            .error(controllerClass.getName() +
                                " was specified as input interceptor in the configuration file, but it is not an input interceptor since it does not implement the " +
                                InputInterceptor.class.getName() + " interface");
                }

                if (OutputInterceptor.class.isAssignableFrom(controllerClass)) {
                    outputInterceptors.setElementAt(theController, outputInterceptorsSignatures
                            .indexOf(controllerClass.getName()));
                } else if (outputInterceptorsSignatures.contains(controllerClass.getName())) {
                    logger
                            .error(controllerClass.getName() +
                                " was specified as output interceptor in the configuration file, but it is not an output interceptor since it does not implement the " +
                                OutputInterceptor.class.getName() + " interface");
                }
            } catch (Exception e) {
                throw new ProActiveRuntimeException("Could not create controller '" +
                    controllers.get(controllerItfName) + "' while instantiating '" +
                    componentParameters.getName() + "' component (please check your configuration file " +
                    componentParameters.getControllerDescription().getControllersConfigFileLocation() +
                    ") : " + e.getMessage(), e);
            }

            if (NameController.class.isAssignableFrom(controllerClass)) {
                ((NameController) theController.getFcItfImpl()).setFcName(componentParameters.getName());
            }

            controlItfs.put(currentController.getFcItfName(), theController);
            nfType.add((InterfaceType) theController.getFcItfType());
        }

        try {//Setting the real NF type, as some controllers may not be generated
            Component boot = GCM.getBootstrapComponent();
            GCMTypeFactory type_factory = GCM.getGCMTypeFactory(boot);
            InterfaceType[] nf = new InterfaceType[nfType.size()];
            nfType.toArray(nf);
            this.componentParameters.setComponentNFType(type_factory.createFcType(nf));
        } catch (Exception e) {
            e.printStackTrace();
            logger.warn("NF type could not be set");
        }
        // add the "component" control itfs
    }

    /**
     * @param controllerConfigFileLocation
     *            the location of the configuration file
     * @return a xml parsing handler
     */
    public static ComponentConfigurationHandler loadControllerConfiguration(
            String controllerConfigFileLocation) {
        try {
            return ComponentConfigurationHandler
                    .createComponentConfigurationHandler(controllerConfigFileLocation);
        } catch (Exception e) {
            logger.error("could not load controller config file : " + controllerConfigFileLocation +
                ". Reverting to default controllers configuration.");

            try {
                return ComponentConfigurationHandler
                        .createComponentConfigurationHandler(ControllerDescription.DEFAULT_COMPONENT_CONFIG_FILE_LOCATION);
            } catch (Exception e1) {
                logger
                        .error("could not load default controller config file either. Check that the default controller config file is available in your classpath at : " +
                            ControllerDescription.DEFAULT_COMPONENT_CONFIG_FILE_LOCATION);
                throw new ProActiveRuntimeException(
                    "could not load default controller config file either. Check that the default controller config file is available on your system at : " +
                        ControllerDescription.DEFAULT_COMPONENT_CONFIG_FILE_LOCATION, e1);
            }
        }
    }

    // returns a generated interface reference, whose impl field is a group
    // It is able to handle multiple bindings
    private PAInterface createInterfaceOnGroupOfDelegatees(PAGCMInterfaceType itfType) throws Exception {
        PAInterface itf_ref = MetaObjectInterfaceClassGenerator.instance().generateFunctionalInterface(
                itfType.getFcItfName(), this, itfType);

        // create a group of impl target objects
        PAInterface itf_ref_group = PAComponentGroup.newComponentInterfaceGroup(itfType, this);
        itf_ref.setFcItfImpl(itf_ref_group);
        return itf_ref;
    }

    /*
     * see {@link org.objectweb.fractal.api.Component#getFcInterface(String)}
     */
    public Object getFcInterface(String interfaceName) throws NoSuchInterfaceException {
        if (!(Constants.ATTRIBUTE_CONTROLLER.equals(interfaceName)) &&
            (interfaceName.endsWith("-controller"))) {
            if (!controlItfs.containsKey(interfaceName)) {
                throw new NoSuchInterfaceException(interfaceName);
            }
            return (controlItfs.get(interfaceName));
        }
        if (interfaceName.equals(Constants.COMPONENT)) {
            return this;
        }
        if (serverItfs.containsKey(interfaceName)) {
            return serverItfs.get(interfaceName);
        }
        if (clientItfs.containsKey(interfaceName)) {
            return clientItfs.get(interfaceName);
        }

        // a member of a collection itf?
        InterfaceType[] itfTypes = ((ComponentType) getFcType()).getFcInterfaceTypes();
        for (int i = 0; i < itfTypes.length; i++) {
            InterfaceType type = itfTypes[i];
            if (type.isFcCollectionItf()) {
                if ((interfaceName.startsWith(type.getFcItfName()) && !type.getFcItfName().equals(
                        interfaceName))) {
                    if (collectionItfsMembers.containsKey(interfaceName)) {
                        return collectionItfsMembers.get(interfaceName);
                    } else {
                        // generate a new interface and add it to the list of members of collection its
                        try {
                            Interface clientItf = MetaObjectInterfaceClassGenerator.instance()
                                    .generateFunctionalInterface(interfaceName, this,
                                            (PAGCMInterfaceType) itfTypes[i]);
                            collectionItfsMembers.put(interfaceName, clientItf);
                            return clientItf;
                        } catch (InterfaceGenerationFailedException e1) {
                            logger.info("Generation of the interface '" + interfaceName + "' failed.", e1);
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
        List<Object> itfs = new ArrayList<Object>(15); //we have at least 10 control itfs

        // add interface component
        itfs.add(this);
        // add controller interface
        for (Object object : controlItfs.values()) {
            itfs.add(object);
        }

        //add server interface
        for (Object object : serverItfs.values()) {
            itfs.add(object);
        }

        //add client interface
        for (Object object : clientItfs.values()) {
            itfs.add(object);
        }

        return itfs.toArray(new Interface[itfs.size()]);
    }

    /*
     * see {@link org.objectweb.fractal.api.Component#getFcType()}
     */
    public Type getFcType() {
        return this.componentParameters.getComponentType();
    }

    /**
     * Returns the non-functional type of the component (interfaces exposed by the membrane)
     * @return The non-functional type of the component
     */
    public Type getNFType() {
        return this.componentParameters.getComponentNFType();
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
        return this;
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
    public ComponentParameters getComponentParameters() {
        return this.componentParameters;
    }

    /**
     * @return the body of the current active object
     */
    public Body getBody() {
        return body;
    }

    /**
     * see
     * {@link org.objectweb.proactive.core.component.identity.PAComponent#getID()}
     */
    public UniqueID getID() {
        return getBody().getID();
    }

    public void setControllerObject(String itfName, Object classToCreate) throws Exception {
        PAInterface itf_ref = (PAInterface) controlItfs.get(itfName);
        if (itf_ref == null) {
            throw new NoSuchInterfaceException("The requested interface :" + itfName + " doesn't exist");
        } else {
            Class<?> controllerClass = Class.forName((String) classToCreate);

            PAGCMInterfaceType itf_type = (PAGCMInterfaceType) itf_ref.getFcItfType();
            Class<?> interfaceClass = Class.forName(itf_type.getFcItfSignature());
            if (interfaceClass.isAssignableFrom(controllerClass)) { //Check that the class implements the specified interface
                PAInterface controller = createController(itf_type, controllerClass);

                if (itf_ref.getFcItfImpl() != null) { // Dealing with first initialization
                    if (itf_ref.getFcItfImpl() instanceof AbstractPAController) { //In this case, itf_ref is implemented by a controller object
                        if (controller.getFcItfImpl() instanceof ControllerStateDuplication) { //Duplicate the state of the existing controller
                            Object ob = itf_ref.getFcItfImpl();
                            if (ob instanceof ControllerStateDuplication) {
                                ((ControllerStateDuplication) controller.getFcItfImpl())
                                        .duplicateController(((ControllerStateDuplication) ob).getState()
                                                .getStateObject());
                            }
                        }
                    } else { //In this case, the object controller will replace an interface of a non-functional component
                        if (controller.getFcItfImpl() instanceof ControllerStateDuplication) {
                            try {
                                Component theOwner = ((PAInterface) itf_ref.getFcItfImpl()).getFcItfOwner();
                                ControllerStateDuplication dup = (ControllerStateDuplication) theOwner
                                        .getFcInterface(Constants.CONTROLLER_STATE_DUPLICATION);
                                ControllerState cs = dup.getState();

                                ((ControllerStateDuplication) controller.getFcItfImpl())
                                        .duplicateController(cs.getStateObject());
                            } catch (NoSuchInterfaceException e) {
                                //Here nothing to do, if the component controller doesnt have a ControllerDuplication, then no duplication can be done
                            }
                        }
                    }
                }

                controlItfs.put(controller.getFcItfName(), controller);
            } else { /* The controller class does not implement the specified interface */
                throw new IllegalBindingException("The class " + classToCreate + " does not implement the " +
                    itf_type.getFcItfSignature() + " interface");
            }
        }
    }

    /**
     * see
     * {@link org.objectweb.proactive.core.component.identity.PAComponent#getRepresentativeOnThis()}
     * There should be no generic return type in the methods of the representative class
     */
    public PAComponent getRepresentativeOnThis() {
        // optimization : cache self reference
        if (representativeOnMyself != null) {
            return representativeOnMyself;
        }

        try {
            return representativeOnMyself = PAComponentRepresentativeFactory.instance()
                    .createComponentRepresentative(
                            getComponentParameters(),
                            ((StubObject) MOP.turnReified(body.getReifiedObject().getClass().getName(),
                                    org.objectweb.proactive.core.Constants.DEFAULT_BODY_PROXY_CLASS_NAME,
                                    new Object[] { body }, body.getReifiedObject(), null)).getProxy());
        } catch (Exception e) {
            throw new ProActiveRuntimeException("This component could not generate a reference on itself", e);
        }
    }

    @Override
    public String toString() {
        String string = "name : " + getFcItfName() + "\n" + getFcItfType() + "\n" + "isInternal : " +
            isFcInternalItf() + "\n";
        return string;
    }

    public List<Interface> getInputInterceptors() {
        return inputInterceptors;
    }

    public List<Interface> getOutputInterceptors() {
        return outputInterceptors;
    }

    public void migrateControllersDependentActiveObjectsTo(Node node) throws MigrationException {
        /*
         * for (PAController controller : controlItfs.values()) {
         * controller.migrateDependentActiveObjectsTo(node); }
         */
        for (Interface controller : controlItfs.values()) {
            Object c = ((PAInterface) controller).getFcItfImpl();
            if (c instanceof PAController) { // Object Controller
                ((PAController) c).migrateDependentActiveObjectsTo(node);
            } else {
                //TODO : Case of migration of component controllers
            }
        }

    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        //        System.out.println("writing PAComponentImpl");
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        //        System.out.println("reading PAComponentImpl");
        in.defaultReadObject();
    }

    public void setImmediateServices() {
        PAActiveObject.setImmediateService("getComponentParameters");
    }
}