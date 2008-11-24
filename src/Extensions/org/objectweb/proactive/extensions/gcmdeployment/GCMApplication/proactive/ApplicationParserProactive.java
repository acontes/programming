/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
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
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.proactive;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.objectweb.proactive.core.body.ProActiveMetaObjectFactory;
import org.objectweb.proactive.core.security.PolicyServer;
import org.objectweb.proactive.core.security.ProActiveSecurityDescriptorHandler;
import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.security.SecurityConstants.EntityType;
import org.objectweb.proactive.core.security.exceptions.InvalidPolicyFile;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extensions.gcmdeployment.PathElement;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeImpl;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeInternal;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class ApplicationParserProactive {
    private static final String XPATH_TECHNICAL_SERVICES = "app:technicalServices";
    private static final String XPATH_JAVA = "app:java";
    private static final String XPATH_JVMARG = "app:jvmarg";
    private static final String XPATH_CONFIGURATION = "app:configuration";
    private static final String XPATH_PROACTIVE_CLASSPATH = "app:proactiveClasspath";
    private static final String XPATH_APPLICATION_CLASSPATH = "app:applicationClasspath";
    private static final String XPATH_SECURITY_POLICY = "app:securityPolicy";
    private static final String XPATH_PROACTIVE_SECURITY = "app:proactiveSecurity";
    private static final String XPATH_APPLICATION_POLICY = "app:applicationPolicy";
    private static final String XPATH_RUNTIME_POLICY = "app:runtimePolicy";
    private static final String XPATH_LOG4J_PROPERTIES = "app:log4jProperties";
    private static final String XPATH_USER_PROPERTIES = "app:userProperties";
    private static final String XPATH_VIRTUAL_NODE = "app:virtualNode";
    private static final String ATTR_RP_CAPACITY = "capacity";
    private static final String XPATH_NODE_PROVIDER = "app:nodeProvider";

    final private ApplicationProActiveConfigurationBean configBean;
    final private Map<String, NodeProvider> nodeProviders;

    final private Map<String, GCMVirtualNodeInternal> virtualNodes;

    public ApplicationParserProactive(final ApplicationProActiveConfigurationBean configBean,
            final Map<String, NodeProvider> nodeProviders) {
        this.configBean = configBean;
        this.nodeProviders = Collections.unmodifiableMap(nodeProviders);

        this.virtualNodes = new HashMap<String, GCMVirtualNodeInternal>();
    }

    public ApplicationProActiveConfigurationBean getConfigBean() {
        return this.configBean;
    }

    public Map<String, ? extends GCMVirtualNodeInternal> getVirtualNodes() {
        return virtualNodes;
    }

    public void parseProActiveNode(Node paNode, XPath xpath) throws Exception {
        String relPath = GCMParserHelper.getAttributeValue(paNode, "relpath");
        String base = GCMParserHelper.getAttributeValue(paNode, "base");
        configBean.setProActivePath(relPath, base);

        try {
            // parse configuration
            Node configNode = (Node) xpath.evaluate(XPATH_CONFIGURATION, paNode, XPathConstants.NODE);
            if (configNode != null) {
                parseProActiveConfiguration(xpath, configNode);
            }

            // Parse root level technical services
            Node techServicesNode = (Node) xpath.evaluate(XPATH_TECHNICAL_SERVICES, paNode,
                    XPathConstants.NODE);
            if (techServicesNode != null) {
                TechnicalServicesProperties rootTS = GCMParserHelper.parseTechnicalServicesNode(xpath,
                        techServicesNode);
                configBean.setApplicationLevelTechnicalSerives(rootTS);
            } else {
                configBean.setApplicationLevelTechnicalSerives(new TechnicalServicesProperties());
            }

            // Parse virtual nodes
            parseVirtualNodes(xpath, paNode);
        } catch (XPathExpressionException e) {
            GCMDeploymentLoggers.GCMA_LOGGER.fatal(e.getMessage(), e);
        }
    }

    private void parseProActiveConfiguration(XPath xpath, Node configNode) throws XPathExpressionException {
        // Optional: java
        Node javaNode = (Node) xpath.evaluate(XPATH_JAVA, configNode, XPathConstants.NODE);
        if (javaNode != null) {
            PathElement pe = GCMParserHelper.parsePathElementNode(javaNode);
            configBean.setJavaPath(pe);
        }

        // Optional: proactiveClasspath
        Node classPathNode;
        classPathNode = (Node) xpath.evaluate(XPATH_PROACTIVE_CLASSPATH, configNode, XPathConstants.NODE);
        if (classPathNode != null) {
            String type = GCMParserHelper.getAttributeValue(classPathNode, "type");
            List<PathElement> proactiveClassPath = GCMParserHelper.parseClasspath(xpath, classPathNode);
            configBean.setProActiveClasspath(proactiveClassPath);
            if ("overwrite".equals(type)) {
                configBean.setOverwriteClasspath(true);
            } else {
                configBean.setOverwriteClasspath(false);
            }
        }

        // Optional: applicationClasspath
        classPathNode = (Node) xpath.evaluate(XPATH_APPLICATION_CLASSPATH, configNode, XPathConstants.NODE);
        if (classPathNode != null) {
            List<PathElement> applicationClassPath = GCMParserHelper.parseClasspath(xpath, classPathNode);
            configBean.setApplicationClasspath(applicationClassPath);
        }

        // Optional: security policy
        Node securityPolicyNode = (Node) xpath.evaluate(XPATH_SECURITY_POLICY, configNode,
                XPathConstants.NODE);
        if (securityPolicyNode != null) {
            PathElement pathElement = GCMParserHelper.parsePathElementNode(securityPolicyNode);
            configBean.setSecurityPolicy(pathElement);
        }

        Node applicationSecurityPolicyNode = (Node) xpath.evaluate(XPATH_PROACTIVE_SECURITY + "/" +
            XPATH_APPLICATION_POLICY, configNode, XPathConstants.NODE);
        if (applicationSecurityPolicyNode != null) {
            PathElement pathElement = GCMParserHelper.parsePathElementNode(applicationSecurityPolicyNode);
            configBean.setApplicationPolicy(pathElement);

            /** security rules */
            PolicyServer policyServer;
            try {
                policyServer = ProActiveSecurityDescriptorHandler
                        .createPolicyServer(pathElement.getRelPath());
                ProActiveSecurityManager psm = new ProActiveSecurityManager(EntityType.APPLICATION,
                    policyServer);
                configBean.setPsm(psm);

                // set the security policyserver to the default proactive meta object
                // by the way, the HalfBody will be associated to a security manager
                // derivated from this one.
                psm = psm.generateSiblingCertificate(EntityType.OBJECT, "HalfBody");
                ProActiveMetaObjectFactory.newInstance().setProActiveSecurityManager(psm);

            } catch (InvalidPolicyFile e) {
                e.printStackTrace();
            }
        }

        Node runtimeSecurityPolicyNode = (Node) xpath.evaluate(XPATH_PROACTIVE_SECURITY + "/" +
            XPATH_RUNTIME_POLICY, configNode, XPathConstants.NODE);
        if (runtimeSecurityPolicyNode != null) {
            PathElement pathElement = GCMParserHelper.parsePathElementNode(runtimeSecurityPolicyNode);
            configBean.setRuntimePolicy(pathElement);
        }

        // Optional: log4j properties
        Node log4jPropertiesNode = (Node) xpath.evaluate(XPATH_LOG4J_PROPERTIES, configNode,
                XPathConstants.NODE);
        if (log4jPropertiesNode != null) {
            PathElement pathElement = GCMParserHelper.parsePathElementNode(log4jPropertiesNode);
            configBean.setLog4jProperties(pathElement);
        }

        // Optional: user properties
        Node userPropertiesNode = (Node) xpath.evaluate(XPATH_USER_PROPERTIES, configNode,
                XPathConstants.NODE);
        if (userPropertiesNode != null) {
            PathElement pathElement = GCMParserHelper.parsePathElementNode(userPropertiesNode);
            configBean.setUserProperties(pathElement);
        }

        // Optional: jvmarg
        NodeList jvmargNodes = (NodeList) xpath.evaluate(XPATH_JVMARG, configNode, XPathConstants.NODESET);
        for (int i = 0; i < jvmargNodes.getLength(); i++) {
            String jvmarg = GCMParserHelper.getAttributeValue(jvmargNodes.item(i), "value");
            configBean.addJvmArg(jvmarg);
        }
    }

    private void parseVirtualNodes(XPath xpath, Node paNode) throws Exception {
        NodeList nodes = (NodeList) xpath.evaluate(XPATH_VIRTUAL_NODE, paNode, XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); ++i) {
            Node xmlNode = nodes.item(i);

            GCMVirtualNodeImpl virtualNode = new GCMVirtualNodeImpl(configBean
                    .getApplicationLevelTechnicalSerives());

            // get Id
            String id = GCMParserHelper.getAttributeValue(xmlNode, "id");
            virtualNode.setName(id);

            // get capacity
            String capacity = GCMParserHelper.getAttributeValue(xmlNode, ATTR_RP_CAPACITY);
            virtualNode.setCapacity(capacityAsLong(capacity));

            // get technical services (if any)
            Node techServices = (Node) xpath.evaluate(XPATH_TECHNICAL_SERVICES, xmlNode, XPathConstants.NODE);
            if (techServices != null) {
                TechnicalServicesProperties vnodeTechnicalServices = GCMParserHelper
                        .parseTechnicalServicesNode(xpath, techServices);
                virtualNode.setTechnicalServicesProperties(vnodeTechnicalServices);
            }

            // get resource providers references
            NodeList nodeProviderNodes = (NodeList) xpath.evaluate(XPATH_NODE_PROVIDER, xmlNode,
                    XPathConstants.NODESET);
            if (nodeProviderNodes.getLength() == 0) {
                // Add all the Node Providers to this Virtual Node
                for (NodeProvider nodeProvider : NodeProvider.getAllNodeProviders()) {
                    virtualNode.addNodeProviderContract(nodeProvider, TechnicalServicesProperties.EMPTY,
                            GCMVirtualNode.MAX_CAPACITY);
                }
            } else {
                for (int j = 0; j < nodeProviderNodes.getLength(); j++) {
                    Node nodeProv = nodeProviderNodes.item(j);

                    String refId = GCMParserHelper.getAttributeValue(nodeProv, "refid");
                    capacity = GCMParserHelper.getAttributeValue(nodeProv, ATTR_RP_CAPACITY);

                    NodeProvider nodeProvider = nodeProviders.get(refId);

                    Node nodeProviderTechServices = (Node) xpath.evaluate(XPATH_TECHNICAL_SERVICES, nodeProv,
                            XPathConstants.NODE);
                    TechnicalServicesProperties nodeProviderTechServicesProperties = TechnicalServicesProperties.EMPTY;
                    if (nodeProviderTechServices != null) {
                        nodeProviderTechServicesProperties = GCMParserHelper.parseTechnicalServicesNode(
                                xpath, nodeProviderTechServices);
                        nodeProvider.setTechnicalServicesProperties(nodeProviderTechServicesProperties);
                    }

                    virtualNode.addNodeProviderContract(nodeProvider, nodeProviderTechServicesProperties,
                            capacityAsLong(capacity));

                }
            }

            virtualNodes.put(virtualNode.getName(), virtualNode);
        }
    }

    static private long capacityAsLong(String capacity) throws NumberFormatException {
        if (capacity == null) {
            return GCMVirtualNode.MAX_CAPACITY;
        }

        try {
            return Long.parseLong(capacity);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(capacity +
                " is an invalid value for a capacity (should have been checked by the XSD)");
        }
    }
}
