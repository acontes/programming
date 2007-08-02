package org.objectweb.proactive.extra.gcmdeployment.GCMApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GCMDeploymentDescriptor;
import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GCMDeploymentDescriptorFactory;
import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.GCMDeploymentDescriptorParams;
import org.objectweb.proactive.extra.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extra.gcmdeployment.PathElement;
import org.objectweb.proactive.extra.gcmdeployment.VirtualNode;
import org.objectweb.proactive.extra.gcmdeployment.VirtualNodeImpl;
import org.objectweb.proactive.extra.gcmdeployment.VirtualNodeInternal;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.process.commandbuilder.CommandBuilderProActive;
import org.objectweb.proactive.extra.gcmdeployment.process.commandbuilder.CommandBuilderScript;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/*
 * FIXME: Improvements needed
 *  - Refactoring & Cleanup
 *         - Put all "magic strings" in a warehouse
 *  - Write some comment to explain how it works
 */
public class GCMApplicationParserImpl implements GCMApplicationParser {
    public static final String APPLICATION_DESC_LOCATION = "/org/objectweb/proactive/extra/gcmdeployment/schema/ApplicationDescriptorSchema.xsd";
    public static final String DESCRIPTOR_NAMESPACE = "http://www-sop.inria.fr/oasis/ProActive/schemas/ApplicationDescriptorSchema";
    protected File descriptor;
    protected Document document;
    protected DocumentBuilderFactory domFactory;
    protected XPath xpath;
    protected DocumentBuilder documentBuilder;
    protected CommandBuilder commandBuilder;
    protected Map<String, GCMDeploymentDescriptor> resourceProvidersMap;
    protected Set<GCMDeploymentDescriptor> resourceProviders;
    protected Map<String, VirtualNodeInternal> virtualNodes;

    public GCMApplicationParserImpl(File descriptor) throws IOException {
        this.descriptor = descriptor;
        resourceProviders = null;
        resourceProvidersMap = null;
        virtualNodes = null;

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
                                      .getResource(APPLICATION_DESC_LOCATION)
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

    synchronized public Set<GCMDeploymentDescriptor> getResourceProviders() {
        if (resourceProviders != null) {
            return resourceProviders;
        }

        resourceProvidersMap = new HashMap<String, GCMDeploymentDescriptor>();

        try {
            NodeList nodes;

            nodes = (NodeList) xpath.evaluate("/pa:GCMApplication/pa:resourceProvider",
                    document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); ++i) {
                Node node = nodes.item(i);

                // get Id
                //
                GCMDeploymentDescriptorParams resourceProviderParams = new GCMDeploymentDescriptorParams();
                String id = GCMParserHelper.getAttributeValue(node, "id");
                resourceProviderParams.setId(id);

                // get GCMDescriptor file
                //
                Node fileNode = (Node) xpath.evaluate("pa:file", node,
                        XPathConstants.NODE);
                if (fileNode != null) {
                    String nodeValue = GCMParserHelper.getAttributeValue(fileNode,
                            "path");
                    if (nodeValue.startsWith(".")) {
                        // Path is relative to this descriptor
                        resourceProviderParams.setGCMDescriptor(new File(
                                descriptor.getParent(), nodeValue));
                    } else {
                        resourceProviderParams.setGCMDescriptor(new File(
                                nodeValue));
                    }
                }

                // get fileTransfers
                //
                HashSet<FileTransferBlock> fileTransferBlocks = new HashSet<FileTransferBlock>();
                NodeList fileTransferNodes = (NodeList) xpath.evaluate("pa:filetransfer",
                        node, XPathConstants.NODESET);
                for (int j = 0; j < fileTransferNodes.getLength(); ++j) {
                    Node fileTransferNode = fileTransferNodes.item(j);
                    FileTransferBlock fileTransferBlock = parseFileTransferNode(fileTransferNode);
                    fileTransferBlocks.add(fileTransferBlock);
                }

                resourceProvidersMap.put(resourceProviderParams.getId(),
                    GCMDeploymentDescriptorFactory.createDescriptor(
                        resourceProviderParams));
            }
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        resourceProviders = new HashSet<GCMDeploymentDescriptor>(resourceProvidersMap.values());
        return resourceProviders;
    }

    protected FileTransferBlock parseFileTransferNode(Node fileTransferNode) {
        FileTransferBlock fileTransferBlock = new FileTransferBlock();
        String source = GCMParserHelper.getAttributeValue(fileTransferNode,
                "source");
        fileTransferBlock.setSource(source);
        String destination = GCMParserHelper.getAttributeValue(fileTransferNode,
                "destination");
        fileTransferBlock.setDestination(destination);

        return fileTransferBlock;
    }

    public CommandBuilder getCommandBuilder() {
        if (commandBuilder != null) {
            return commandBuilder;
        }

        // FIXME: Do not switch to parseApplicationNode by default. Check it !
        try {
            Node commandNode;

            commandNode = (Node) xpath.evaluate("//pa:proactive", document,
                    XPathConstants.NODE);

            if (commandNode != null) {
                commandBuilder = parseProactiveNode(commandNode);
            } else {
                commandNode = (Node) xpath.evaluate("//pa:executable",
                        document, XPathConstants.NODE);

                if (commandNode != null) {
                    commandBuilder = parseExecutableNode(commandNode);
                }
            }
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return commandBuilder;
    }

    protected CommandBuilder parseExecutableNode(Node appNode)
        throws XPathExpressionException {
        NodeList resourceProviderNodes = (NodeList) xpath.evaluate("pa:resourceProvider",
                appNode, XPathConstants.NODESET);

        CommandBuilderScript commandBuilderScript = new CommandBuilderScript();

        getResourceProviders();

        // resource providers
        //
        for (int i = 0; i < resourceProviderNodes.getLength(); ++i) {
            Node rpNode = resourceProviderNodes.item(i);
            String refid = GCMParserHelper.getAttributeValue(rpNode, "refid");
            GCMDeploymentDescriptor deploymentDescriptor = resourceProvidersMap.get(refid);
            if (deploymentDescriptor != null) {
                commandBuilderScript.addDescriptor(deploymentDescriptor);
            } else {
                // TODO - log warning
            }
        }

        Node commandNode = (Node) xpath.evaluate("pa:command", appNode,
                XPathConstants.NODE);

        String name = GCMParserHelper.getAttributeValue(commandNode, "name");
        commandBuilderScript.setCommand(name);

        Node pathNode = (Node) xpath.evaluate("pa:path", commandNode,
                XPathConstants.NODE);
        if (pathNode != null) {
            // path tag is optional
            commandBuilderScript.setPath(parsePathElementNode(pathNode));
        }

        // command args
        //
        NodeList argNodes = (NodeList) xpath.evaluate("pa:arg", commandNode,
                XPathConstants.NODESET);
        for (int i = 0; i < argNodes.getLength(); ++i) {
            Node argNode = argNodes.item(i);
            String argVal = argNode.getFirstChild().getNodeValue();
            commandBuilderScript.addArg(argVal);
        }

        // filetransfer
        //
        NodeList fileTransferNodes = (NodeList) xpath.evaluate("pa:filetransfer",
                appNode, XPathConstants.NODESET);

        for (int i = 0; i < fileTransferNodes.getLength(); ++i) {
            Node fileTransferNode = fileTransferNodes.item(i);
            FileTransferBlock fileTransferBlock = parseFileTransferNode(fileTransferNode);
            commandBuilderScript.addFileTransferBlock(fileTransferBlock);
        }

        return commandBuilderScript;
    }

    protected CommandBuilder parseProactiveNode(Node paNode)
        throws XPathExpressionException {
        CommandBuilderProActive commandBuilderProActive = new CommandBuilderProActive();

        String relPath = GCMParserHelper.getAttributeValue(paNode, "relpath");

        // TODO - what do we do with this ?
        Node javaNode = (Node) xpath.evaluate("pa:java", paNode,
                XPathConstants.NODE);

        if (javaNode != null) {
            String javaRelPath = GCMParserHelper.getAttributeValue(javaNode,
                    "relpath");
            PathElement pathElement = new PathElement();
            pathElement.setRelPath(javaRelPath);
            commandBuilderProActive.setJavaPath(pathElement);
        }

        Node configNode = (Node) xpath.evaluate("pa:configuration", paNode,
                XPathConstants.NODE);

        if (configNode != null) {
            parseProActiveConfiguration(commandBuilderProActive, configNode);
        }

        getVirtualNodes();

        commandBuilderProActive.setVirtualNodes(virtualNodes);

        return commandBuilderProActive;
    }

    protected void parseProActiveConfiguration(
        CommandBuilderProActive commandBuilderProActive, Node configNode)
        throws XPathExpressionException {
        Node classPathNode = (Node) xpath.evaluate("pa:proactiveClasspath",
                configNode, XPathConstants.NODE);

        List<PathElement> proactiveClassPath = parseClasspath(classPathNode);

        commandBuilderProActive.setProActiveClasspath(proactiveClassPath);

        classPathNode = (Node) xpath.evaluate("pa:applicationClasspath",
                configNode, XPathConstants.NODE);

        List<PathElement> applicationClassPath = parseClasspath(classPathNode);

        commandBuilderProActive.setApplicationClasspath(applicationClassPath);

        // security policy
        //
        Node securityPolicyNode = (Node) xpath.evaluate("pa:securityPolicy",
                configNode, XPathConstants.NODE);

        if (securityPolicyNode != null) {
            PathElement pathElement = parsePathElementNode(securityPolicyNode);
            commandBuilderProActive.setSecurityPolicy(pathElement);
        }

        // log4j properties
        //
        Node log4jPropertiesNode = (Node) xpath.evaluate("pa:log4jProperties",
                configNode, XPathConstants.NODE);

        if (log4jPropertiesNode != null) {
            PathElement pathElement = parsePathElementNode(log4jPropertiesNode);
            commandBuilderProActive.setLog4jProperties(pathElement);
        }
    }

    protected List<PathElement> parseClasspath(Node classPathNode)
        throws XPathExpressionException {
        NodeList pathElementNodes = (NodeList) xpath.evaluate("pa:pathElement",
                classPathNode, XPathConstants.NODESET);

        ArrayList<PathElement> res = new ArrayList<PathElement>();

        for (int i = 0; i < pathElementNodes.getLength(); ++i) {
            Node pathElementNode = pathElementNodes.item(i);
            PathElement pathElement = parsePathElementNode(pathElementNode);
            res.add(pathElement);
        }

        return res;
    }

    protected PathElement parsePathElementNode(Node pathElementNode) {
        PathElement pathElement = new PathElement();
        String attr = GCMParserHelper.getAttributeValue(pathElementNode,
                "relpath");
        pathElement.setRelPath(attr);
        attr = GCMParserHelper.getAttributeValue(pathElementNode, "base");
        if (attr != null) {
            pathElement.setBase(attr);
        }

        return pathElement;
    }

    synchronized public Map<String, VirtualNodeInternal> getVirtualNodes() {
        if (virtualNodes != null) {
            return virtualNodes;
        }

        try {
            virtualNodes = new HashMap<String, VirtualNodeInternal>();

            // make sure these are parsed
            getResourceProviders();

            NodeList nodes = (NodeList) xpath.evaluate("/pa:GCMApplication/pa:proactive/pa:virtualNode",
                    document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); ++i) {
                Node node = nodes.item(i);

                // get Id
                //
                VirtualNodeImpl virtualNode = new VirtualNodeImpl();

                String id = GCMParserHelper.getAttributeValue(node, "id");
                virtualNode.setId(id);

                // get capacity
                //
                String capacity = GCMParserHelper.getAttributeValue(node,
                        "capacity").trim().toLowerCase();

                long capacityI = 0;
                if (capacity.equals("max")) {
                    capacityI = VirtualNode.MAX_CAPACITY;
                } else {
                    capacityI = Long.parseLong(capacity);
                }
                virtualNode.setRequiredCapacity(capacityI);

                // get resource providers references
                //
                NodeList resourceProviderNodes = (NodeList) xpath.evaluate("pa:resourceProvider",
                        node, XPathConstants.NODESET);
                List<GCMDeploymentDescriptor> providers = new ArrayList<GCMDeploymentDescriptor>();

                for (int j = 0; j < resourceProviderNodes.getLength(); ++j) {
                    Node resProv = resourceProviderNodes.item(j);
                    String refId = GCMParserHelper.getAttributeValue(resProv,
                            "refid");

                    GCMDeploymentDescriptor resourceProvider = resourceProvidersMap.get(refId);
                    providers.add(resourceProvider);
                }

                virtualNode.addProviders(providers);

                virtualNodes.put(virtualNode.getId(), virtualNode);
            }
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return virtualNodes;
    }
}
