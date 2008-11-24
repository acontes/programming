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
package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.objectweb.proactive.core.security.ProActiveSecurityManager;
import org.objectweb.proactive.core.xml.VariableContractImpl;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeploymentLoggers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMParserHelper;
import org.objectweb.proactive.extensions.gcmdeployment.Helpers;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.Application;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.Application;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.Application;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.Application;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilder;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentDescriptor;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentDescriptorImpl;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.GCMDeploymentDescriptorParams;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeImpl;
import org.objectweb.proactive.extensions.gcmdeployment.core.GCMVirtualNodeInternal;
import org.objectweb.proactive.extensions.gcmdeployment.environment.Environment;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/*
 * TODO: Write some comment to explain how it works
 * 
 * Exceptions are not catch but always thrown to the caller. If an error occurs, we want to abort the
 * parsing in progress, wrap the Exception inside a ProActiveException and give it to the user.
 */
public class GCMApplicationParserImpl implements GCMApplicationParser {
    private static final ApplicationFactory applicationFactory = new ApplicationFactory();
    
    private static final String OLD_DESCRIPTOR_SCHEMA = "http://www-sop.inria.fr/oasis/proactive/schema/3.2/DescriptorSchema.xsd";
    public static final String XPATH_GCMAPP = "/app:GCMApplication/";
 
    private static final String XPATH_NODE_PROVIDERS = XPATH_GCMAPP + "app:resources/app:nodeProvider";
    private static final String XPATH_APPLICATION = XPATH_GCMAPP + "app:application";

    private static final String XPATH_TECHNICAL_SERVICES = "app:technicalServices";
    private static final String XPATH_FILE = "app:file";

    private static final String[] SUPPORTED_PROTOCOLS = { "file", "http", "http", "https", "jar", "ftp" };

    protected URL descriptor;
    protected VariableContractImpl vContract;

    protected Document document;
    protected DocumentBuilderFactory domFactory;
    protected XPath xpath;

    protected List<String> schemas;
    protected Application application;
    protected Map<String, NodeProvider> nodeProvidersMap;
//    protected Map<String, GCMVirtualNodeInternal> virtualNodes;

//    protected TechnicalServicesProperties appTechnicalServices;
//    protected ProActiveSecurityManager proactiveApplicationSecurityManager;

    public GCMApplicationParserImpl(URL descriptor, VariableContractImpl vContract) throws Exception {
        this(descriptor, vContract, null);
    }

    public GCMApplicationParserImpl(URL descriptor, VariableContractImpl vContract, List<String> userSchemas)
            throws Exception {
        this.descriptor = descriptor;
        this.vContract = vContract;
//        this.appTechnicalServices = TechnicalServicesProperties.EMPTY;

        this.nodeProvidersMap = null;
//        this.virtualNodes = null;
        this.schemas = (userSchemas != null) ? new ArrayList<String>(userSchemas) : new ArrayList<String>();


        setupJAXP();

        try {
            InputSource processedInputSource = Environment.replaceVariables(descriptor, vContract, xpath,
                    GCM_APPLICATION_NAMESPACE_PREFIX);
            DocumentBuilder documentBuilder = GCMParserHelper.getNewDocumentBuilder(domFactory);
            document = documentBuilder.parse(processedInputSource);

            // sanity check : make sure there isn't a ref to an old schema in the document
            String noNamespaceSchema = document.getDocumentElement().getAttribute(
                    "xsi:noNamespaceSchemaLocation");
            if (noNamespaceSchema != null && noNamespaceSchema.contains(OLD_DESCRIPTOR_SCHEMA)) {
                throw new SAXException("Trying to parse a descriptor using the legacy Deployment Descriptor Schema");
            }
        } catch (SAXException e) {
            String msg = "parsing problem with document " + descriptor.toExternalForm();
            throw new SAXException(msg, e);
        } catch (TransformerException e) {
            String msg = "problem when evaluating variables with document " + descriptor.toExternalForm();
            throw new TransformerException(msg, e);
        } catch (XPathExpressionException e) {
            throw e;
        }

    }

  

    public void setupJAXP() throws IOException, ParserConfigurationException, SAXException {
        domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        domFactory.setIgnoringComments(true);

        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        URL applicationSchema = getClass().getResource(APPLICATION_DESC_LOCATION);

        schemas.add(0, applicationSchema.toString());
        Source[] schemaSources = new Source[schemas.size()];

        int idx = 0;
        for (String s : schemas) {
            schemaSources[idx++] = new StreamSource(s);
        }

        Schema extensionSchema = schemaFactory.newSchema(schemaSources);

        domFactory.setSchema(extensionSchema);

        XPathFactory factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        xpath.setNamespaceContext(new GCMParserHelper.ProActiveNamespaceContext());
    }

    synchronized public Map<String, NodeProvider> getNodeProviders() throws Exception {
        if (nodeProvidersMap != null) {
            return nodeProvidersMap;
        }

        nodeProvidersMap = new HashMap<String, NodeProvider>();

        NodeList nodeProviderNodes;

        nodeProviderNodes = (NodeList) xpath.evaluate(XPATH_NODE_PROVIDERS, document, XPathConstants.NODESET);

        for (int i = 0; i < nodeProviderNodes.getLength(); ++i) {
            Node nodeProviderNode = nodeProviderNodes.item(i);

            String id = GCMParserHelper.getAttributeValue(nodeProviderNode, "id");
            NodeProvider nodeProvider = new NodeProvider(id);

            NodeList gcmdNodes;
            gcmdNodes = (NodeList) xpath.evaluate(XPATH_FILE, nodeProviderNode, XPathConstants.NODESET);
            for (int j = 0; j < gcmdNodes.getLength(); j++) {
                GCMDeploymentDescriptorParams gcmdParams = new GCMDeploymentDescriptorParams();
                gcmdParams.setId(id);
                String path = GCMParserHelper.getAttributeValue(gcmdNodes.item(j), "path");

                URL fullURL = null;

                // We determine wether we have a Path or a URL
                boolean schemeFound = false;
                String protocolFound = null;
                if (path.indexOf(':') >= 0) {
                    for (String scheme : SUPPORTED_PROTOCOLS) {
                        if (path.toLowerCase().startsWith(scheme + ":")) {
                            schemeFound = true;
                            protocolFound = scheme;
                            break;
                        }
                    }
                }

                if (schemeFound && !protocolFound.equals("file")) {
                    // In case we have an url other than file:
                    fullURL = new URL(path);
                } else {

                    // in case we have a filepath or a url starting with file:

                    if (schemeFound) {
                        // if it's an url starting with file: we remove the protocol
                        URL urlWithFile = new URL(path);
                        if (urlWithFile.getHost().length() != 0) {
                            throw new IOException(
                                urlWithFile +
                                    " is using the form <host>/<path> which is not supported. Other possibility is that the url is of the form file:/<path> and it should be file://<path>");
                        }
                        path = urlWithFile.getPath();
                    }
                    File file = new File(path);

                    // If this path is absolute, no problem
                    if (file.isAbsolute()) {
                        fullURL = Helpers.fileToURL(file);
                    } else if (descriptor.getProtocol().equals("jar")) {
                        // If this File path is relative and the base descriptor URL protocol is jar,
                        // we need to handle ourselves how we resolve the relative path against the jar
                        JarURLConnection jconn = (JarURLConnection) descriptor.openConnection();
                        URI base = new URI(jconn.getEntryName());
                        URI resolved = base.resolve(new URI(file.getPath()));
                        fullURL = new URL("jar:" + jconn.getJarFileURL().toExternalForm() + "!/" + resolved);
                    } else if (descriptor.toURI().isOpaque()) {
                        // This is very unlikely, but : ff this path is relative and the base url is not hierarchical (and differs from jar)
                        // we just can't handle it
                        throw new IOException(descriptor.toExternalForm() +
                            " is not a hierarchical uri and can't be resolved against the relative path " +
                            path);
                    } else {
                        // We can handle the last case by using URI resolve method
                        URI uriDescriptor = descriptor.toURI();

                        // This ugly code is here for the following reasons:
                        // 1) We need to escape illegal characters which appear in File paths
                        // 2) The toURI() method returns an absolute path and here the File path is relative, so it will prepent to the path the current directory
                        // 3) we need to remove this prepended directory to have the final relative path as an relative URI
                        File basef = new File("");
                        URI messedup = file.toURI();
                        URI baseuri = basef.toURI();
                        String cleaner = messedup.toString().substring(baseuri.toString().length());
                        URI cleaneruri = new URI(cleaner);

                        if (cleaneruri.isAbsolute()) {
                            throw new IOException("Internal error: " + cleaneruri +
                                " is absolute and should be relative");
                        }
                        // now that we have a clean relative, we can resolve it against the base url
                        URI fullUri = uriDescriptor.resolve(cleaneruri);
                        fullURL = fullUri.toURL();
                    }
                }

                gcmdParams.setGCMDescriptor(fullURL);
                gcmdParams.setVContract(vContract);

                GCMDeploymentDescriptor gcmd = new GCMDeploymentDescriptorImpl(fullURL, vContract);
                nodeProvider.addGCMDeploymentDescriptor(gcmd);
            }

            // get fileTransfers
            /*
             * HashSet<FileTransferBlock> fileTransferBlocks = new HashSet<FileTransferBlock>();
             * NodeList fileTransferNodes = (NodeList) xpath.evaluate(XPATH_FILETRANSFER, node,
             * XPathConstants.NODESET); for (int j = 0; j < fileTransferNodes.getLength(); ++j) {
             * Node fileTransferNode = fileTransferNodes.item(j); FileTransferBlock
             * fileTransferBlock = GCMParserHelper.parseFileTransferNode(fileTransferNode);
             * fileTransferBlocks.add(fileTransferBlock); }
             */
            nodeProvidersMap.put(nodeProvider.getId(), nodeProvider);
        }

        return nodeProvidersMap;
    }

    public Application getApplication() throws Exception {
        if (application != null) {
            return application;
        }

        Node applicationNode = (Node) xpath.evaluate(XPATH_APPLICATION, document, XPathConstants.NODE);
        Node childNode = applicationNode.getFirstChild();

        Application application = applicationFactory.getApplicationParser(childNode.getNodeName());
        application.parse(childNode, xpath, getNodeProviders());

        return application;
    }
}
