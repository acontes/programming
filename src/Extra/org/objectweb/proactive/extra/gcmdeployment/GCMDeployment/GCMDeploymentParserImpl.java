package org.objectweb.proactive.extra.gcmdeployment.GCMDeployment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.objectweb.proactive.extra.gcmdeployment.Environment;
import org.objectweb.proactive.extra.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extra.gcmdeployment.process.CommandBuilder;
import org.objectweb.proactive.extra.gcmdeployment.process.Group;
import org.objectweb.proactive.extra.gcmdeployment.process.HostInfo;
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
    protected Map<String, HostInfo> bridgesMap;
    protected Map<String, HostInfo> groupsMap;
    
    public GCMDeploymentParserImpl(File descriptor) throws IOException {

        hostsMap   = new HashMap<String, HostInfo>();
        bridgesMap = new HashMap<String, HostInfo>();
        groupsMap  = new HashMap<String, HostInfo>();
        
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
            xpath.setNamespaceContext(new GCMParserHelper.ProActiveNamespaceContext(DESCRIPTOR_NAMESPACE));
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
        
        for(int i = 0; i < descriptorVarNodes.getLength(); ++i) {
            Node descVarNode = descriptorVarNodes.item(i);
            String varName  = GCMParserHelper.getAttributeValue(descVarNode, "name");
            String varValue = GCMParserHelper.getAttributeValue(descVarNode, "value");
            // TODO
        }
        
    }
    
    protected void parseResources() throws XPathExpressionException {
        Node resourcesNode = (Node) xpath.evaluate("/pa:GCMDeployment/pa:resources",
                document, XPathConstants.NODE);        
        
        
        
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
