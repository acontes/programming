package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.globus.ogce.beans.console.gui.commands.grid.GRUNCommand;
import org.objectweb.proactive.extra.gcmdeployment.Environment;
import org.objectweb.proactive.extra.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;
import org.objectweb.proactive.extra.gcmdeployment.process.bridge.AbstractBridge;
import org.objectweb.proactive.extra.gcmdeployment.process.group.AbstractGroup;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class GCMDeploymentParserImpl implements GCMDeploymentParser {
    public static final String DESCRIPTOR_NAMESPACE = "http://www-sop.inria.fr/oasis/ProActive/schemas/DeploymentDescriptorSchema";
    protected Document document;
    protected DocumentBuilderFactory domFactory;
    protected XPath xpath;
    protected DocumentBuilder documentBuilder;
    protected CommandBuilder commandBuilder;
    protected Map<String, HostInfo> hostsMap;
    protected Map<String, AbstractBridge> bridgesMap;
    protected Map<String, AbstractGroup> groupsMap;

    static protected class Resource {
        protected String refid;

        public Resource(String refid) {
            super();
            this.refid = refid;
        }
    }

    static protected class HostResource extends Resource {
        public HostResource(String refid) {
            super(refid);
        }
    }

    static protected class GroupResource extends Resource {
        protected List<String> hostRefids;

        public GroupResource(String refid) {
            super(refid);
            hostRefids = new ArrayList<String>();
        }

        public void addHost(String refid) {
            hostRefids.add(refid);
        }
    }

    static protected class BridgeResource extends Resource {
        protected Map<String, GroupResource> groupRefids;
        protected List<String> hostRefids;

        public BridgeResource(String refid) {
            super(refid);
            groupRefids = new HashMap<String, GroupResource>();
            hostRefids = new ArrayList<String>();
        }

        public void addHost(String refid) {
            hostRefids.add(refid);
        }

        public void addGroup(String refid, GroupResource group) {
            groupRefids.put(refid, group);
        }
    }

    static protected class Resources {
        List<HostResource> hosts;
        Map<String, GroupResource> groups;
        Map<String, BridgeResource> bridges;

        Resources() {
            hosts = new ArrayList<HostResource>();
            groups = new HashMap<String, GroupResource>();
            bridges = new HashMap<String, BridgeResource>();
        }

        public void addHost(String refid) {
            hosts.add(new HostResource(refid));
        }

        public void addGroup(String refid, GroupResource group) {
            groups.put(refid, group);
        }

        public void addBridge(String refid, BridgeResource bridge) {
            bridges.put(refid, bridge);
        }
    }

    protected Resources resources;

    public GCMDeploymentParserImpl(File descriptor) throws IOException {
        hostsMap = new HashMap<String, HostInfo>();
        bridgesMap = new HashMap<String, AbstractBridge>();
        groupsMap = new HashMap<String, AbstractGroup>();
        resources = new Resources();

        setup();

        InputSource inputSource = new InputSource(new FileInputStream(
                    descriptor));
        try {
            document = documentBuilder.parse(inputSource);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setup() throws IOException {
        domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        domFactory.setValidating(true);
        domFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

        String deploymentSchema = getClass()
                                      .getResource("/org/objectweb/proactive/extra/ressourceallocator/schema/DeploymentDescriptorSchema.xsd")
                                      .toString();

        domFactory.setAttribute(JAXP_SCHEMA_SOURCE,
            new Object[] { deploymentSchema });

        try {
            documentBuilder = domFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(new GCMParserHelper.MyDefaultHandler());

            XPathFactory factory = XPathFactory.newInstance();
            xpath = factory.newXPath();
            xpath.setNamespaceContext(new GCMParserHelper.ProActiveNamespaceContext(
                    DESCRIPTOR_NAMESPACE));
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void parseEnvironment() throws XPathExpressionException {
        Node environmentNode = (Node) xpath.evaluate("/pa:GCMDeployment/pa:environment",
                document, XPathConstants.NODE);

        NodeList descriptorVarNodes = (NodeList) xpath.evaluate("/pa:descriptorVariable",
                environmentNode, XPathConstants.NODESET);

        for (int i = 0; i < descriptorVarNodes.getLength(); ++i) {
            Node descVarNode = descriptorVarNodes.item(i);
            String varName = GCMParserHelper.getAttributeValue(descVarNode,
                    "name");
            String varValue = GCMParserHelper.getAttributeValue(descVarNode,
                    "value");

            // TODO
        }
    }

    protected void parseResources() throws XPathExpressionException {
        Node resourcesNode = (Node) xpath.evaluate("/pa:GCMDeployment/pa:resources",
                document, XPathConstants.NODE);

        NodeList childNodes = resourcesNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node childNode = childNodes.item(i);
            if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                parseResourceNode(childNode);
            }
        }
    }

    protected void parseResourceNode(Node resourceNode) {
        String refid = GCMParserHelper.getAttributeValue(resourceNode, "refid");

        String nodeName = resourceNode.getNodeName();

        if (nodeName.equals("bridge")) {
            BridgeResource bridgeResource = new BridgeResource(refid);

            NodeList childNodes = resourceNode.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                Node childNode = childNodes.item(i);
                String childNodeName = childNode.getNodeName();
                String childRefId = GCMParserHelper.getAttributeValue(childNode,
                        "refid");

                if (childNodeName.equals("group")) {
                    GroupResource groupResource = new GroupResource(childRefId);

                    parseGroupResource(childNode, groupResource);
                    bridgeResource.addGroup(refid, groupResource);
                } else if (childNodeName.equals("host")) {
                    bridgeResource.addHost(childRefId);
                }
            }

            resources.addBridge(refid, bridgeResource);
        } else if (nodeName.equals("group")) {
            GroupResource groupResource = new GroupResource(refid);

            parseGroupResource(resourceNode, groupResource);
            resources.addGroup(refid, groupResource);
        } else if (nodeName.equals("host")) {
            addHostResource(refid);
        }
    }

    protected void parseGroupResource(Node resourceNode,
        GroupResource groupResource) {
        NodeList childNodes = resourceNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            String hostRefid = GCMParserHelper.getAttributeValue(childNodes.item(
                        i), "refid");
            groupResource.addHost(hostRefid);
        }
    }

    protected void addHostResource(String refid) {
        resources.addHost(refid);
    }

    protected void addGroupResource(String refid, GroupResource group) {
        resources.addGroup(refid, group);
    }

    public Environment getEnvironment() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, Group> getGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, HostInfo> getHosts() {
        // TODO Auto-generated method stub
        return null;
    }
}
