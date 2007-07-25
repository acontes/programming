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

import org.objectweb.proactive.core.mop.Utils;
import org.objectweb.proactive.core.util.OperatingSystem;
import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GroupParsers.GroupParser;
import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GroupParsers.SSHGroupParser;
import org.objectweb.proactive.extra.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extra.gcmdeployment.process.Bridge;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;
import org.objectweb.proactive.extra.gcmdeployment.process.hostinfo.HostInfoImpl;
import org.objectweb.proactive.extra.gcmdeployment.process.hostinfo.Tool;
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
    protected Map<String, GroupParser> groupParserMap;
    protected GCMDeploymentInfrastructure infrastructure;
    protected GCMDeploymentEnvironment environment;

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

    protected GCMDeploymentResources resources;

    public GCMDeploymentParserImpl(File descriptor) throws IOException {
        infrastructure = new GCMDeploymentInfrastructure();
        resources = new GCMDeploymentResources();
        groupParserMap = new HashMap<String, GroupParser>();
        environment = new GCMDeploymentEnvironment();

        setup();
        registerDefaultGroupParsers();
        registerUserGroupParsers();

        InputSource inputSource = new InputSource(new FileInputStream(
                    descriptor));
        try {
            document = documentBuilder.parse(inputSource);
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    protected void registerDefaultGroupParsers() {
        registerGroupParser("sshGroup", new SSHGroupParser());
        // TODO add other group parsers here 
    }

    /**
     * Override this
     */
    protected void registerUserGroupParsers() {
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

    public void parseEnvironment() throws XPathExpressionException {
        Node environmentNode = (Node) xpath.evaluate("/pa:GCMDeployment/pa:environment",
                document, XPathConstants.NODE);

        NodeList descriptorVarNodes = (NodeList) xpath.evaluate("pa:descriptorVariable",
                environmentNode, XPathConstants.NODESET);

        for (int i = 0; i < descriptorVarNodes.getLength(); ++i) {
            Node descVarNode = descriptorVarNodes.item(i);
            String varName = GCMParserHelper.getAttributeValue(descVarNode,
                    "name");
            String varValue = GCMParserHelper.getAttributeValue(descVarNode,
                    "value");

            environment.addValue(varName, varValue);
        }
    }

    public void parseResources() throws XPathExpressionException, IOException {
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

    protected void parseResourceNode(Node resourceNode)
        throws XPathExpressionException, IOException {
        String refid = GCMParserHelper.getAttributeValue(resourceNode, "refid");

        String nodeName = resourceNode.getNodeName();

        if (nodeName.equals("bridge")) {
            Bridge bridge = getBridge(refid);

            NodeList childNodes = resourceNode.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }
                String childNodeName = childNode.getNodeName();
                String childRefId = GCMParserHelper.getAttributeValue(childNode,
                        "refid");

                if (childNodeName.equals("group")) {
                    Group group = getGroup(childRefId);

                    parseGroupResource(childNode, group);
                    bridge.addGroup(group);
                } else if (childNodeName.equals("host")) {
                    HostInfo hostInfo = getHostInfo(refid);
                    bridge.setHostInfo(hostInfo);
                }
            }

            resources.addBridge(bridge);
        } else if (nodeName.equals("group")) {
            Group group = getGroup(refid);

            parseGroupResource(resourceNode, group);
            resources.addGroup(group);
        } else if (nodeName.equals("host")) {
            HostInfo hostInfo = getHostInfo(refid);
            resources.setHostInfo(hostInfo);
        }
    }

    protected HostInfo getHostInfo(String refid) throws IOException {
        HostInfo hostInfo = infrastructure.getHosts().get(refid);

        try {
            return (HostInfo) ((hostInfo != null) ? hostInfo.clone() : null);
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    protected Group getGroup(String refid) throws IOException {
        Group group = infrastructure.getGroups().get(refid);

        try {
            return (Group) ((group != null) ? group.clone() : null);
        } catch (CloneNotSupportedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    protected Bridge getBridge(String refid) throws IOException {
        Bridge bridge = infrastructure.getBridges().get(refid);

        return (Bridge) ((bridge != null) ? Utils.makeDeepCopy(bridge) : null);
    }

    protected void parseGroupResource(Node resourceNode, Group group)
        throws XPathExpressionException, IOException {
        Node hostNode = (Node) xpath.evaluate("pa:host", resourceNode,
                XPathConstants.NODE);

        String refid = GCMParserHelper.getAttributeValue(hostNode, "refid");
        HostInfo hostInfo = getHostInfo(refid);
        group.setHostInfo(hostInfo);
    }

    public void parseInfrastructure() throws XPathExpressionException {
        Node infrastructureNode = (Node) xpath.evaluate("/pa:GCMDeployment/pa:infrastructure",
                document, XPathConstants.NODE);

        NodeList hosts = (NodeList) xpath.evaluate("pa:hosts/pa:host",
                infrastructureNode, XPathConstants.NODESET);

        for (int i = 0; i < hosts.getLength(); ++i) {
            HostInfo hostInfo = parseHostNode(hosts.item(i));
            infrastructure.addHost(hostInfo);
        }

        NodeList groups = (NodeList) xpath.evaluate("pa:groups/*",
                infrastructureNode, XPathConstants.NODESET);

        for (int i = 0; i < groups.getLength(); ++i) {
            Node groupNode = groups.item(i);
            GroupParser groupParser = groupParserMap.get(groupNode.getNodeName());
            groupParser.parseGroupNode(groupNode, xpath);
            infrastructure.addGroup(groupParser.getGroup());
        }

        NodeList bridges = (NodeList) xpath.evaluate("pa:bridges/*",
                infrastructureNode, XPathConstants.NODESET);

        for (int i = 0; i < bridges.getLength(); ++i) {
            Node bridgeNode = bridges.item(i);

            // TODO
        }
    }

    public void registerGroupParser(String groupNodeName,
        GroupParser groupParser) {
        groupParserMap.put(groupNodeName, groupParser);
    }

    protected HostInfo parseHostNode(Node hostNode)
        throws XPathExpressionException {
        HostInfoImpl hostInfo = new HostInfoImpl();

        String id = GCMParserHelper.getAttributeValue(hostNode, "id");
        hostInfo.setId(id);

        String os = GCMParserHelper.getAttributeValue(hostNode, "os");
        if (os.equals("unix")) {
            hostInfo.setOs(OperatingSystem.unix);
        } else if (os.equals("windows")) {
            hostInfo.setOs(OperatingSystem.windows);
        }

        String hostCapacityStr = GCMParserHelper.getAttributeValue(hostNode,
                "hostCapacity");
        if (hostCapacityStr != null) {
            hostInfo.setHostCapacity(Integer.parseInt(hostCapacityStr));
        }

        String vmCapacityStr = GCMParserHelper.getAttributeValue(hostNode,
                "vmCapacity");
        if (vmCapacityStr != null) {
            hostInfo.setVmCapacity(Integer.parseInt(vmCapacityStr));
        }

        String username = GCMParserHelper.getAttributeValue(hostNode, "username");
        if (username != null) {
            hostInfo.setUsername(username);
        }

        Node homeDirectoryNode = (Node) xpath.evaluate("pa:homeDirectory",
                hostNode, XPathConstants.NODE);

        if (homeDirectoryNode != null) {
            hostInfo.setHomeDirectory(homeDirectoryNode.getFirstChild()
                                                       .getNodeValue());
        }

        NodeList toolNodes = (NodeList) xpath.evaluate("pa:tool", hostNode,
                XPathConstants.NODESET);

        for (int i = 0; i < toolNodes.getLength(); ++i) {
            Node toolNode = toolNodes.item(i);
            Tool tool = new Tool(GCMParserHelper.getAttributeValue(toolNode,
                        "id"),
                    GCMParserHelper.getAttributeValue(toolNode, "path"));
            hostInfo.addTool(tool);
        }

        return hostInfo;
    }

    public GCMDeploymentEnvironment getEnvironment() {
        // TODO Auto-generated method stub
        return environment;
    }

    public GCMDeploymentInfrastructure getInfrastructure() {
        return infrastructure;
    }
}
