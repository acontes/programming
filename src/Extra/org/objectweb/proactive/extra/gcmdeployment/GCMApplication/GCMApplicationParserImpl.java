package org.objectweb.proactive.extra.gcmdeployment.GCMApplication;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.objectweb.proactive.extra.gcmdeployment.GCMDeployment.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.VirtualNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


public class GCMApplicationParserImpl implements GCMApplicationParser {
    static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    public static final String DESCRIPTOR_NAMESPACE = "urn:proactive:deployment:3.3";
    protected Document document;
    private DocumentBuilderFactory domFactory;
    private XPath xpath;
    private String xmlDescriptorUrl;
    protected Set<GCMDeploymentDescriptorParams> resourceProviders;
    protected DocumentBuilder builder;

    public GCMApplicationParserImpl(File descriptor) throws IOException {
        resourceProviders = null;

        setup();

        InputSource inputSource = new InputSource(new FileInputStream(
                    descriptor));
        try {
            document = builder.parse(inputSource);
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
        // domFactory.setAttribute(JAXP_SCHEMA_SOURCE,
        // new String[] {
        // "http://www-sop.inria.fr/oasis/ProActive/schemas/DescriptorSchema.xsd"
        // });
        // this.variableContract = variableContract;
        try {
            builder = domFactory.newDocumentBuilder();
            builder.setErrorHandler(new MyDefaultHandler());

            XPathFactory factory = XPathFactory.newInstance();
            xpath = factory.newXPath();
            xpath.setNamespaceContext(new ProActiveNamespaceContext());
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Set<GCMDeploymentDescriptorParams> getResourceProviders() {
        if (resourceProviders != null) {
            return resourceProviders;
        }

        resourceProviders = new HashSet<GCMDeploymentDescriptorParams>();

        NodeList nodes;
        try {
            nodes = (NodeList) xpath.evaluate("//pa:resourceProvider",
                    document, XPathConstants.NODESET);

            for (int i = 0; i < nodes.getLength(); ++i) {
                Node node = nodes.item(i);

                // get Id
                //
                GCMDeploymentDescriptorParams resourceProviderParams = new GCMDeploymentDescriptorParams();
                String id = node.getAttributes().getNamedItem("id")
                                .getNodeValue();
                resourceProviderParams.setId(id);

                // get GCMDescriptor file
                //
                Node fileNode = (Node) xpath.evaluate("pa:file", node,
                        XPathConstants.NODE);
                if (fileNode != null) {
                    String nodeValue = fileNode.getAttributes()
                                               .getNamedItem("path")
                                               .getNodeValue();
                    resourceProviderParams.setGCMDescriptor(new File(nodeValue));
                }

                // get fileTransfers
                //
                HashSet<FileTransferBlock> fileTransferBlocks = new HashSet<FileTransferBlock>();
                NodeList fileTransferNodes = (NodeList) xpath.evaluate("pa:filetransfer",
                        node, XPathConstants.NODE);
                for (int j = 0; j < fileTransferNodes.getLength(); ++j) {
                    Node fileTransferNode = fileTransferNodes.item(j);
                    FileTransferBlock fileTransferBlock = new FileTransferBlock();
                    String source = fileTransferNode.getAttributes()
                                                    .getNamedItem("source")
                                                    .getNodeValue();
                    fileTransferBlock.setSource(source);
                    String destination = fileTransferNode.getAttributes()
                                                         .getNamedItem("destination")
                                                         .getNodeValue();
                    fileTransferBlock.setDestination(destination);
                    fileTransferBlocks.add(fileTransferBlock);
                }

                resourceProviders.add(resourceProviderParams);
            }
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return resourceProviders;
    }

    public CommandBuilder getCommandBuilder() {
        // TODO Auto-generated method stub
        return null;
    }

    public Map<String, VirtualNode> getVirtualNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    protected class MyDefaultHandler extends DefaultHandler {
        private CharArrayWriter buff = new CharArrayWriter();
        private String errMessage = "";

        /*
         * With a handler class, just override the methods you need to use
         */

        // Start Error Handler code here
        public void warning(SAXParseException e) {
            System.err.println("Warning Line " + e.getLineNumber() + ": " +
                e.getMessage() + "\n");
        }

        public void error(SAXParseException e) {
            errMessage = new String("Error Line " + e.getLineNumber() + ": " +
                    e.getMessage() + "\n");
            System.err.println(errMessage);
        }

        public void fatalError(SAXParseException e) {
            errMessage = new String("Error Line " + e.getLineNumber() + ": " +
                    e.getMessage() + "\n");
            System.err.println(errMessage);
        }
    }

    class ProActiveNamespaceContext implements NamespaceContext {
        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new NullPointerException("Null prefix");
            } else if ("pa".equals(prefix)) {
                return DESCRIPTOR_NAMESPACE;
            } else if ("xml".equals(prefix)) {
                return XMLConstants.XML_NS_URI;
            }
            return XMLConstants.NULL_NS_URI;
        }

        // This method isn't necessary for XPath processing.
        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        // This method isn't necessary for XPath processing either.
        public Iterator getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }
    }
}
