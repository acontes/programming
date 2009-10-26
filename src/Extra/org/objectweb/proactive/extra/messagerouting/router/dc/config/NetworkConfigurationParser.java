/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
 */
package org.objectweb.proactive.extra.messagerouting.router.dc.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.gcmdeployment.ListGenerator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * Parse the network configuration XML file
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class NetworkConfigurationParser {
    private static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_ROUTER_DC);
    private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
    private static final String SCHEMA_RELPATH = "/org/objectweb/proactive/extra/messagerouting/router/dc/config/NetworkConfiguration.xsd";
    private static final String XPATH_SEGMENTS = "//NetworkSegment";
    private static final String XPATH_ENDPOINTS = "//PointToPoint";
    private static final String XPATH_ENDPOINT = "endpoint";
    private static final String ATTR_SEGMENT_HOSTLIST = "hostList";
    private static final String ATTR_ENDPOINT_DIRECTION = "type";
    private static final String ATTR_ENDPOINT_HOST = "host";
    private static final String ATTR_ENDPOINT_PORT = "port";
    private static final String BIDIRECTIONAL_FLAG = "bidirectional";

    private DocumentBuilder builder;
    private XPath navigator;

    private final NetworkConfigurationRegistry networkInfo;

    public NetworkConfigurationParser(NetworkConfigurationRegistry registry) {
        this.networkInfo = registry;
    }

    public void init() throws ParserConfigurationException {
        DocumentBuilderFactory domFactory;

        domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(false);
        domFactory.setValidating(false);
        domFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);

        URL url = NetworkConfigurationParser.class.getClass().getResource(SCHEMA_RELPATH);

        if ((url != null) && (!url.toString().startsWith("bundle"))) {
            String schema = url.toString();
            domFactory.setValidating(true);
            domFactory.setAttribute(JAXP_SCHEMA_SOURCE, new Object[] { schema });
        }

        XPathFactory factory = XPathFactory.newInstance();

        navigator = factory.newXPath();
        navigator.setNamespaceContext(new RouterNetworkConfigNamespaceContext());
        builder = domFactory.newDocumentBuilder();
        builder.setErrorHandler(new LogErrorsHandler());
    }

    public void parse(File inputFile) throws IOException {
        InputSource source = null;
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(inputFile));
            source = new InputSource(in);
            Document document = builder.parse(source);

            readSegments(document);
            readEndpoints(document);

        } catch (SAXException e) {
            throw new IllegalArgumentException("Invalid network configuration file: " + source, e);
        } catch (XPathExpressionException e) {
            throw new IllegalArgumentException("Invalid network configuration file: " + source, e);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File " + inputFile.getAbsolutePath() +
                " could not be opened for reading", e);
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (IOException e) {
                logger.warn("Error encountered while closing the input file " + inputFile.getAbsolutePath(),
                        e);
            }
        }

    }

    private void readEndpoints(Document document) throws XPathExpressionException, SAXException {

        NodeList nodes = (NodeList) navigator.evaluate(XPATH_ENDPOINTS, document, XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String direction = getAttributeValue(node, ATTR_ENDPOINT_DIRECTION);
            NodeList endpoints = (NodeList) navigator.evaluate(XPATH_ENDPOINT, node, XPathConstants.NODESET);
            if (endpoints.getLength() != 2)
                throw new SAXException(new IllegalArgumentException(
                    "The current implementation assumes that a " + node.getNodeName() +
                        " element should have exactly two child elements"));
            InetSocketAddress srcEndpoint = getEndpointInfo(endpoints.item(0));
            InetSocketAddress dstEndpoint = getEndpointInfo(endpoints.item(1));
            addEndpoints(srcEndpoint, dstEndpoint, direction);
        }

    }

    private InetSocketAddress getEndpointInfo(Node endpointNode) throws SAXException {
        try {
            String host = getAttributeValue(endpointNode, ATTR_ENDPOINT_HOST);
            int port = Integer.parseInt(getAttributeValue(endpointNode, ATTR_ENDPOINT_PORT));
            if (!(0 <= port && port <= 65535))
                throw new IllegalArgumentException("Invalid value for the " + ATTR_ENDPOINT_PORT +
                    " attribute : " + port);
            return InetSocketAddress.createUnresolved(host, port);
        } catch (Exception e) {
            logger.warn("Invalid element:" + endpointNode.getNodeName(), e);
            throw new SAXException(e);
        }
    }

    private String getAttributeValue(Node node, String attributeName) {
        Node namedItem = node.getAttributes().getNamedItem(attributeName);
        if (namedItem == null)
            throw new IllegalArgumentException("element " + node.getNodeName() + " does not have attribute " +
                attributeName);
        else
            return namedItem.getNodeValue();
    }

    private void readSegments(Document document) throws XPathExpressionException {

        NodeList nodes = (NodeList) navigator.evaluate(XPATH_SEGMENTS, document, XPathConstants.NODESET);

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            String hostList = getAttributeValue(node, ATTR_SEGMENT_HOSTLIST);
            addHostList(hostList);
        }

    }

    private void addEndpoints(InetSocketAddress srcEndpoint, InetSocketAddress dstEndpoint, String direction) {
        try {
            this.networkInfo.addAllowed(srcEndpoint.getHostName(), dstEndpoint.getHostName(), dstEndpoint
                    .getPort());
            if (direction.equals(BIDIRECTIONAL_FLAG))
                this.networkInfo.addAllowed(dstEndpoint.getHostName(), srcEndpoint.getHostName(), srcEndpoint
                        .getPort());
        } catch (UnknownHostException e) {
            logger.error("Discarding endpoint rule concerning " + srcEndpoint + " - " + dstEndpoint +
                " due to unresolved hostname: " + e.getMessage() +
                ". Maybe you should consider to use the IP address directly for that hostname.");
            if (logger.isDebugEnabled())
                logger.debug("Stacktrace:", e);
        }
    }

    private void addHostList(String hostList) {
        // use already-existing impl
        List<String> hosts = ListGenerator.generateNames(hostList);
        // add all-to-all mapping
        for (String srcHost : hosts) {
            for (String dstHost : hosts) {
                if (!srcHost.equals(dstHost))
                    try {
                        this.networkInfo.addAllowedHosts(srcHost, dstHost);
                    } catch (UnknownHostException e) {
                        logger.error("Attempt to add information about unresolvable hostname " +
                            e.getMessage() +
                            ". Maybe you should consider to use the IP address directly for that hostname.");
                        if (logger.isDebugEnabled())
                            logger.debug("Stacktrace:", e);
                    }
            }
        }
    }

    private static class RouterNetworkConfigNamespaceContext implements NamespaceContext {
        public String getNamespaceURI(String prefix) {
            return XMLConstants.NULL_NS_URI;
        }

        // This method isn't necessary for XPath processing.
        public String getPrefix(String uri) {
            throw new UnsupportedOperationException();
        }

        // This method isn't necessary for XPath processing either.
        public Iterator<String> getPrefixes(String uri) {
            throw new UnsupportedOperationException();
        }
    }

    private static class LogErrorsHandler extends DefaultHandler {
        @Override
        public void warning(SAXParseException e) {
            logger.warn("Warning Line " + e.getLineNumber() + ": " + e.getMessage() + "\n");
        }

        @Override
        public void error(SAXParseException e) throws SAXParseException {
            String errMessage = new String("Error Line " + e.getLineNumber() + ": " + e.getMessage() + "\n");
            logger.error(errMessage);
            throw e;
        }

        @Override
        public void fatalError(SAXParseException e) throws SAXParseException {
            String errMessage = new String("Error Line " + e.getLineNumber() + ": " + e.getMessage() + "\n");
            logger.fatal(errMessage);
            throw e;
        }
    }

}
