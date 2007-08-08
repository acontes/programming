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

import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.ApplicationParsers.ApplicationParser;
import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.ApplicationParsers.ApplicationParserExecutable;
import org.objectweb.proactive.extra.gcmdeployment.GCMApplication.ApplicationParsers.ApplicationParserProactive;
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
    protected Map<String, VirtualNodeInternal> virtualNodes;
    protected Map<String, ApplicationParser> applicationParsersMap;

    public GCMApplicationParserImpl(File descriptor) throws IOException {
        this.descriptor = descriptor;
        resourceProvidersMap = null;
        virtualNodes = null;

        applicationParsersMap = new HashMap<String, ApplicationParser>();
        
        registerDefaultApplicationParsers();
        registerUserApplicationParsers();
        
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

    /**
     * override me
     */
    protected void registerUserApplicationParsers() {
        // TODO Auto-generated method stub
        
    }

    public void registerApplicationParser(ApplicationParser applicationParser) {
        applicationParsersMap.put(applicationParser.getNodeName(), applicationParser);
    }
    
    private void registerDefaultApplicationParsers() {
        registerApplicationParser(new ApplicationParserProactive());        
        registerApplicationParser(new ApplicationParserExecutable());
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

    synchronized public Map<String, GCMDeploymentDescriptor> getResourceProviders() {
        if (resourceProvidersMap != null) {
            return resourceProvidersMap;
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
                    FileTransferBlock fileTransferBlock = GCMParserHelper.parseFileTransferNode(fileTransferNode);
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

        return resourceProvidersMap;
    }

    public CommandBuilder getCommandBuilder() {
        if (commandBuilder != null) {
            return commandBuilder;
        }

        try {
            Node applicationNode = (Node) xpath.evaluate("/pa:GCMApplication/pa:application",
                    document, XPathConstants.NODE);

            NodeList appNodes = applicationNode.getChildNodes();

            for (int i = 0; i < appNodes.getLength(); ++i) {
                Node commandNode = appNodes.item(i);
                if (commandNode.getNodeType() != Node.ELEMENT_NODE) {
                    continue;
                }

                ApplicationParser applicationParser = getApplicationParserForNode(commandNode);
                applicationParser.parseApplicationNode(commandNode, this, xpath);
                commandBuilder = applicationParser.getCommandBuilder();  
                
            }
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return commandBuilder;
    }

    private ApplicationParser getApplicationParserForNode(Node commandNode) {
        ApplicationParser applicationParser = applicationParsersMap.get(commandNode.getNodeName());
        return applicationParser;
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
