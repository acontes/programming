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
package org.objectweb.proactive.core.component.adl.implementations;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.fractal.adl.ADLException;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.Type;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.adl.RegistryManager;
import org.objectweb.proactive.core.component.adl.nodes.ADLNodeProvider;
import org.objectweb.proactive.core.component.adl.nodes.VirtualNode;
import org.objectweb.proactive.core.component.adl.vnexportation.ExportedVirtualNodesList;
import org.objectweb.proactive.core.component.adl.vnexportation.LinkedVirtualNode;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


/**
 * @author The ProActive Team
 */
public class PAImplementationBuilderImpl implements PAImplementationBuilder, BindingController {
    public final static String REGISTRY_BINDING = "registry";
    public RegistryManager registry;
    protected static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_ADL);

    // --------------------------------------------------------------------------
    // Implementation of the BindingController interface
    // --------------------------------------------------------------------------
    public String[] listFc() {
        return new String[] { REGISTRY_BINDING };
    }

    public Object lookupFc(final String itf) {
        if (itf.equals(REGISTRY_BINDING)) {
            return registry;
        }
        return null;
    }

    public void bindFc(final String itf, final Object value) {
        if (itf.equals(REGISTRY_BINDING)) {
            registry = (RegistryManager) value;
        }
    }

    public void unbindFc(final String itf) {
        if (itf.equals(REGISTRY_BINDING)) {
            registry = null;
        }
    }

    //  --------------------------------------------------------------------------
    // Implementation of the Implementation Builder and PAImplementationBuilder interfaces
    // --------------------------------------------------------------------------
    public Object createComponent(Object arg0, String arg1, String arg2, Object arg3, Object arg4, Object arg5)
            throws Exception {
        return null;
    }

    public Object createComponent(Object type, String name, String definition,
            ControllerDescription controllerDesc, ContentDescription contentDesc, VirtualNode adlVN,
            Map<Object, Object> context) throws Exception {
        ObjectsContainer obj = commonCreation(type, name, definition, contentDesc, adlVN, context);
        return createFComponent(type, obj, controllerDesc, contentDesc, adlVN, obj.getBootstrapComponent());
    }

    protected ObjectsContainer commonCreation(Object type, String name, String definition,
            ContentDescription contentDesc, VirtualNode adlVN, Map<Object, Object> context) throws Exception {
        Object deploymentDescriptor = null;
        Component bootstrap = null;
        ObjectsContainer result = null;

        if (context != null) {
            deploymentDescriptor = context.get("deployment-descriptor");
            bootstrap = (Component) context.get("bootstrap");
        }
        if (bootstrap == null) {
            bootstrap = Utils.getBootstrapComponent();
        }
        if ((deploymentDescriptor != null) && (adlVN != null)) {
            // consider exported virtual nodes
            LinkedVirtualNode exported = ExportedVirtualNodesList.instance().getNode(name, adlVN.getName(),
                    false);
            if (exported != null) {
                adlVN.setName(exported.getExportedVirtualNodeNameAfterComposition());
                adlVN.setCardinality(exported.isMultiple() ? VirtualNode.MULTIPLE : VirtualNode.SINGLE);
            } else {
                // 	TODO add self exported virtual node ?
                // for the moment, just add a leaf to the linked vns
                ExportedVirtualNodesList.instance().addLeafVirtualNode(name, adlVN.getName(),
                        adlVN.getCardinality()); // TODO_M check this
            }
            if (deploymentDescriptor != null) {
                if (deploymentDescriptor instanceof GCMApplication) {
                    //
                    // New deployment
                    //
                    GCMApplication gcmApplication = (GCMApplication) deploymentDescriptor;
                    GCMVirtualNode virtualNode = gcmApplication.getVirtualNode(adlVN.getName());
                    result = new NewDeploymentObjectsContainer(virtualNode, bootstrap);
                } else if (deploymentDescriptor instanceof ProActiveDescriptor) {
                    //
                    // Old deployment
                    //
                    org.objectweb.proactive.core.descriptor.data.VirtualNodeInternal deploymentVN = null;
                    ProActiveDescriptor proactiveDecriptor = (ProActiveDescriptor) deploymentDescriptor;
                    org.objectweb.proactive.core.descriptor.data.VirtualNode vn = proactiveDecriptor
                            .getVirtualNode(adlVN.getName());
                    if (vn != null) {
                        deploymentVN = vn.getVirtualNodeInternal();
                    }
                    if (deploymentVN == null) {
                        if (adlVN.getName().equals("null")) {
                            logger
                                    .info(name +
                                        " will be instantiated in the current virtual machine (\"null\" was specified as the virtual node name)");
                        } else {
                            throw new ADLException(PAImplementationErrors.VIRTUAL_NODE_NOT_FOUND, adlVN
                                    .getName());
                        }
                    } else {
                        if (deploymentVN.isMultiple() && (adlVN.getCardinality().equals(VirtualNode.SINGLE))) {
                            // there will be only one instance of the component, on one node of the virtual node 
                            contentDesc.forceSingleInstance();
                        } else if (!(deploymentVN.isMultiple()) &&
                            (adlVN.getCardinality().equals(VirtualNode.MULTIPLE))) {
                            throw new ADLException(
                                PAImplementationErrors.INCOMPATIBLE_VIRTUAL_NODE_CARDINALITY, adlVN.getName());
                        }
                    }
                    result = new ObjectsContainer(deploymentVN, bootstrap);
                }
            }
        } else { // (deploymentDescriptor == null || adlVN == null)
            if (deploymentDescriptor != null && deploymentDescriptor instanceof GCMApplication) {
                result = new NewDeploymentObjectsContainer(null, bootstrap);
            } else {
                if ((deploymentDescriptor == null) && (adlVN != null)) {
                    logger
                            .info(name +
                                " will be instantiated in the current virtual machine (a virtual node name is specified but no deployment descriptor has been set in the context)");
                }
                result = new ObjectsContainer(null, bootstrap);
            }
        }

        return result;
    }

    private Component createFComponent(Object type, ObjectsContainer objectContainer,
            ControllerDescription controllerDesc, ContentDescription contentDesc, VirtualNode adlVN,
            Component bootstrap) throws Exception {
        Component result;

        result = objectContainer.createFComponent((ComponentType) type, controllerDesc, contentDesc, adlVN);
        //        registry.addComponent(result); // the registry can handle groups

        return result;
    }

    protected class ObjectsContainer {
        private org.objectweb.proactive.core.descriptor.data.VirtualNode deploymentVN;
        protected Component bootstrap;

        public ObjectsContainer(org.objectweb.proactive.core.descriptor.data.VirtualNode dVn, Component bstrp) {
            deploymentVN = dVn;
            bootstrap = bstrp;
        }

        protected ObjectsContainer(Component bstrp) {
            deploymentVN = null;
            bootstrap = bstrp;
        }

        public org.objectweb.proactive.core.descriptor.data.VirtualNode getDvn() {
            return deploymentVN;
        }

        public Component getBootstrapComponent() {
            return bootstrap;
        }

        public Component createFComponent(ComponentType type, ControllerDescription controllerDesc,
                ContentDescription contentDesc, VirtualNode adlVN) throws Exception {
            Component result = null;
            PAGenericFactory genericFactory = Utils.getPAGenericFactory(bootstrap);

            if ((deploymentVN != null) && VirtualNode.MULTIPLE.equals(adlVN.getCardinality()) &&
                controllerDesc.getHierarchicalType().equals(Constants.PRIMITIVE) &&
                !contentDesc.uniqueInstance()) {
                Group<Component> fcInstance = (Group<Component>) newFcInstanceAsList(bootstrap, type,
                        controllerDesc, contentDesc, deploymentVN);
                result = (Component) fcInstance.getGroupByType();
            } else {
                if (deploymentVN == null) {
                    result = genericFactory.newFcInstance(type, controllerDesc, contentDesc, (Node) null);
                } else {
                    try {
                        deploymentVN.activate();
                        if (deploymentVN.getNodes().length == 0) {
                            throw new InstantiationException(
                                "Cannot create component on virtual node as no node is associated with this virtual node");
                        }
                        result = genericFactory.newFcInstance(type, controllerDesc, contentDesc, deploymentVN
                                .getNode());
                    } catch (NodeException e) {
                        InstantiationException ie = new InstantiationException(
                            "could not instantiate components due to a deployment problem : " +
                                e.getMessage());
                        ie.initCause(e);
                        throw ie;
                    }
                }
            }

            return result;
        }
    }

    protected class NewDeploymentObjectsContainer extends ObjectsContainer {

        private GCMVirtualNode gcmDeploymentVN;

        public NewDeploymentObjectsContainer(GCMVirtualNode gcmDeploymentVN, Component bstrp) {
            super(bstrp);
            this.gcmDeploymentVN = gcmDeploymentVN;
        }

        public GCMVirtualNode getGCMDeploymentVN() {
            return gcmDeploymentVN;
        }

        @Override
        public Component createFComponent(ComponentType type, ControllerDescription controllerDesc,
                ContentDescription contentDesc, VirtualNode adlVN) throws Exception {
            Component result = null;

            PAGenericFactory genericFactory = Utils.getPAGenericFactory(bootstrap);
            result = genericFactory.newFcInstance(type, controllerDesc, contentDesc, ADLNodeProvider
                    .getNode(gcmDeploymentVN));

            return result;
        }
    }

    private List<Component> newFcInstanceAsList(Component bootstrap, Type type,
            ControllerDescription controllerDesc, ContentDescription contentDesc,
            org.objectweb.proactive.core.descriptor.data.VirtualNode virtualNode) throws Exception {
        PAGenericFactory genericFactory = Utils.getPAGenericFactory(bootstrap);
        if (virtualNode == null) {
            return genericFactory.newFcInstanceAsList(type, controllerDesc, contentDesc, (Node[]) null);
        }
        try {
            virtualNode.activate();
            return genericFactory.newFcInstanceAsList(type, controllerDesc, contentDesc, virtualNode
                    .getNodes());
        } catch (NodeException e) {
            InstantiationException ie = new InstantiationException(
                "could not instantiate components due to a deployment problem : " + e.getMessage());
            ie.initCause(e);
            throw ie;
        }
    }
}