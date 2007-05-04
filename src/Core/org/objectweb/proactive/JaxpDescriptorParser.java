package org.objectweb.proactive;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.objectweb.proactive.core.Constants;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptorImpl;
import org.objectweb.proactive.core.descriptor.data.VirtualMachine;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.descriptor.data.VirtualNodeImpl;
import org.objectweb.proactive.core.descriptor.data.VirtualNodeLookup;
import org.objectweb.proactive.core.descriptor.xml.ProActiveDescriptorConstants;
import org.objectweb.proactive.core.process.ExternalProcess;
import org.objectweb.proactive.core.process.ExternalProcessDecorator;
import org.objectweb.proactive.core.process.JVMProcess;
import org.objectweb.proactive.core.process.filetransfer.FileTransferWorkShop;
import org.objectweb.proactive.core.process.glite.GLiteProcess;
import org.objectweb.proactive.core.process.globus.GlobusProcess;
import org.objectweb.proactive.core.process.lsf.LSFBSubProcess;
import org.objectweb.proactive.core.process.rsh.maprsh.MapRshProcess;
import org.objectweb.proactive.core.process.unicore.UnicoreProcess;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JaxpDescriptorParser implements ProActiveDescriptorConstants {

    public static final String VIRTUAL_NODES_DEFINITIONS  = "//pa:componentDefinition/pa:virtualNodesDefinition/pa:virtualNode";
    public static final String VIRTUAL_NODES_ACQUISITIONS = "//pa:componentDefinition/pa:virtualNodesAcquisition/pa:virtualNode";

    // deployment
    public static final String DEPLOYMENT         = "//pa:deployment";
    public static final String REGISTER           = "//pa:register";
    public static final String VM_MAPPING         = "//pa:map/pa:jvmSet/pa:vmName";
    public static final String CURRENT_VM_MAPPING = "//pa:map/pa:jvmSet/pa:currentJVM";
    public static final String LOOKUP             = "//pa:lookup";
    public static final String JVM_CREATION       = "//pa:creation/pa:processReference";
    public static final String JVM_ACQUISITION    = "//pa:acquisition/pa:serviceReference";

    // infrastructure
    public static final String INFRASTRUCTURE      = "//pa:infrastructure";
    public static final String PROCESS_DEFINITIONS = "//pa:processDefinition/*";
    public static final String SERVICE_DEFINITION  = "//pa:serviceDefinition";

    protected ProActiveDescriptorImpl proActiveDescriptor;

    private XPathExpression expr;
    private Object result;
    private NodeList nodes;
    private XPath xpath;
    private String xmlDescriptorUrl;

    public JaxpDescriptorParser(String xmlDescriptorUrl) {
        domFactory = DocumentBuilderFactory
                        .newInstance();
        domFactory.setNamespaceAware(true);
        this.xmlDescriptorUrl = xmlDescriptorUrl;
        
    }

    
    public void parse() throws SAXException, IOException, ProActiveException {
        DocumentBuilder builder;
        try {

            builder = domFactory.newDocumentBuilder();

            proActiveDescriptor = new ProActiveDescriptorImpl(xmlDescriptorUrl);

            System.out.println("Parsing " + xmlDescriptorUrl);
            document = builder.parse(xmlDescriptorUrl);
            XPathFactory factory = XPathFactory.newInstance();

            xpath = factory.newXPath();
            xpath.setNamespaceContext(new ProActiveNamespaceContext());

            handleComponentDefinitions();

            handleDeployment();

            handleInfrastructure();

        } catch (ParserConfigurationException e) {
            throw new ProActiveException(e);
        } catch (XPathExpressionException e) {
            throw new ProActiveException(e);
        }
    }

    private void handleComponentDefinitions() throws XPathExpressionException {
        expr = xpath.compile(VIRTUAL_NODES_DEFINITIONS);
        result = expr.evaluate(document, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            NamedNodeMap attributes = node.getAttributes();
            Node nodeName = attributes.getNamedItem("name"), nodeProperty = attributes
                    .getNamedItem("property");

//            System.out.println("Virtual node definition : "
//                    + nodeName.getNodeValue() + " - "
//                    + nodeProperty.getNodeValue());

            VirtualNodeImpl vn = (VirtualNodeImpl) proActiveDescriptor
                    .createVirtualNode(nodeName.getNodeValue(), false);
            if (checkNonEmpty(nodeProperty))
                vn.setProperty(nodeProperty.getNodeValue());

            Node timeout = attributes.getNamedItem("timeout"), waitForTimeoutN = attributes
                    .getNamedItem("waitForTimeout");
            boolean waitForTimeout = false;
            if (checkNonEmpty(waitForTimeoutN)) {
                String nodeValue = waitForTimeoutN.getNodeValue().toLowerCase();
                waitForTimeout = Boolean.parseBoolean(nodeValue); // nodeValue.equals("y")
                // ||
                // nodeValue.equals("true")
                // ||
                // nodeValue.equals("1");
            }

            if (checkNonEmpty(timeout))
                vn.setTimeout(Long.parseLong(timeout.getNodeValue()),
                        waitForTimeout);

            Node minNodeNumber = attributes.getNamedItem("minNodeNumber");
            if (minNodeNumber != null) {
                vn.setMinNumberOfNodes(Integer.parseInt(minNodeNumber
                        .getNodeValue()));
            }

            Node ftServiceId = attributes.getNamedItem("ftServiceId");
            if (checkNonEmpty(ftServiceId)) {
                proActiveDescriptor.registerService(vn, ftServiceId
                        .getNodeValue());
            }

            Node fileTransferDeploy = attributes
                    .getNamedItem(FILE_TRANSFER_DEPLOY_TAG);
            if (checkNonEmpty(fileTransferDeploy)) {
                vn.addFileTransferDeploy(proActiveDescriptor
                        .getFileTransfer(fileTransferDeploy.getNodeValue()));
            }

            Node techServiceId = attributes.getNamedItem(TECHNICAL_SERVICE_ID);
            if (checkNonEmpty(techServiceId)) {
                vn.addTechnicalService(proActiveDescriptor
                        .getTechnicalService(techServiceId.getNodeValue()));
            }

        }

        //
        // Node acquisitions
        //
        expr = xpath.compile(VIRTUAL_NODES_ACQUISITIONS);

        result = expr.evaluate(document, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node nodeName = node.getAttributes().getNamedItem("name");
            if (checkNonEmpty(nodeName))
                proActiveDescriptor.createVirtualNode(nodeName.getNodeValue(),
                        true);
        }
    }

    private void handleDeployment() throws XPathExpressionException,
            SAXException, IOException {
        //
        // register
        //
        XPathExpression deploymentExpr = xpath.compile(DEPLOYMENT);
        NodeList deploymentNodes = (NodeList) deploymentExpr.evaluate(document,
                XPathConstants.NODESET);

        expr = xpath.compile(REGISTER);
        Node deploymentContextItem = deploymentNodes.item(0);
        result = expr.evaluate(deploymentContextItem, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node virtualNodeName = node.getAttributes().getNamedItem(
                    "virtualNode");

            Node protocol = node.getAttributes().getNamedItem("protocol");
            String protocolValue = checkNonEmpty(protocol) ? protocol
                    .getNodeValue() : ProActiveConfiguration
                    .getProperty(Constants.PROPERTY_PA_COMMUNICATION_PROTOCOL);

            VirtualNodeImpl vnImpl = (VirtualNodeImpl) proActiveDescriptor
                    .createVirtualNode(virtualNodeName.getNodeValue(), false);

            vnImpl.setRegistrationProtocol(protocolValue);
        }

        //
        // mapping
        //

        // collect the mappings in a hashmap
        //
        expr = xpath.compile(VM_MAPPING);
        result = expr.evaluate(deploymentContextItem, XPathConstants.NODESET);
        nodes = (NodeList) result;

        HashMap<String, ArrayList<String>> vmMapping = new HashMap<String, ArrayList<String>>();

        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node mapParent = node.getParentNode().getParentNode();
            String virtualNodeName = mapParent.getAttributes().getNamedItem(
                    "virtualNode").getNodeValue();
            ArrayList<String> arrayList = vmMapping.get(virtualNodeName);
            if (arrayList == null) {
                arrayList = new ArrayList<String>();
                vmMapping.put(virtualNodeName, arrayList);
            }
            arrayList.add(node.getAttributes().getNamedItem("value").getNodeValue());
        }

        // set the VM mappings to each virtual node
        //
        for (String s : vmMapping.keySet()) {
            VirtualNode vn = proActiveDescriptor.createVirtualNode(s, false);
            for (String vmName : vmMapping.get(s)) {
                VirtualMachine vm = proActiveDescriptor
                        .createVirtualMachine(vmName);
                vn.addVirtualMachine(vm);
            }
        }

        // current vm mappings
        //
        expr = xpath.compile(CURRENT_VM_MAPPING);
        result = expr.evaluate(deploymentContextItem, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node mapParent = node.getParentNode().getParentNode();
            String virtualNodeName = mapParent.getAttributes().getNamedItem(
                    "virtualNode").getNodeValue();
            VirtualNode vn = proActiveDescriptor.createVirtualNode(
                    virtualNodeName, false);
            Node protocolAttr = node.getAttributes().getNamedItem("protocol");
            if (checkNonEmpty(protocolAttr))
                vn.createNodeOnCurrentJvm(protocolAttr.getNodeValue());
        }

        // vm lookup
        //
        expr = xpath.compile(LOOKUP);
        result = expr.evaluate(deploymentContextItem, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);

            String vnLookup = node.getAttributes().getNamedItem("virtualNode")
                    .getNodeValue();
            String host = node.getAttributes().getNamedItem("host")
                    .getNodeValue();
            Node namedItem = node.getAttributes().getNamedItem("protocol");
            if (namedItem == null)
                throw new org.xml.sax.SAXException(
                        "lookup Tag without any protocol defined");
            String protocol = namedItem.getNodeValue();
            Node portItem = node.getAttributes().getNamedItem("port");

            if (protocol.equals(Constants.JINI_PROTOCOL_IDENTIFIER)
                    && portItem != null)
                throw new org.xml.sax.SAXException(
                        "For a jini lookup, no port number should be specified");

            String port = portItem != null ? portItem.getNodeValue() : "1099";

            VirtualNodeLookup vn = (VirtualNodeLookup) proActiveDescriptor
                    .createVirtualNode(vnLookup, true);

            vn.setLookupInformations(host, protocol, port);
        }

        // vm creation and acquisition
        //
        expr = xpath.compile(JVM_CREATION);
        result = expr.evaluate(deploymentContextItem, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node jvmParent = node.getParentNode().getParentNode();
            String jvmName = jvmParent.getAttributes().getNamedItem("name")
                    .getNodeValue();
            Node t = jvmParent.getAttributes().getNamedItem("askedNodes");
            VirtualMachine currentVM = proActiveDescriptor
                    .createVirtualMachine(jvmName);
            if (checkNonEmpty(t))
                currentVM.setNbNodes(t.getNodeValue());
            proActiveDescriptor.registerProcess(currentVM, node.getAttributes()
                    .getNamedItem("refid").getNodeValue());
        }

        expr = xpath.compile(JVM_ACQUISITION);
        result = expr.evaluate(deploymentContextItem, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node jvmParent = node.getParentNode().getParentNode();
            String jvmName = jvmParent.getAttributes().getNamedItem("name")
                    .getNodeValue();
            Node t = jvmParent.getAttributes().getNamedItem("askedNodes");
            VirtualMachine currentVM = proActiveDescriptor
                    .createVirtualMachine(jvmName);
            if (checkNonEmpty(t))
                currentVM.setNbNodes(t.getNodeValue());
            proActiveDescriptor.registerService(currentVM, node.getAttributes()
                    .getNamedItem("refid").getNodeValue());
        }
    }

    private void handleInfrastructure() throws XPathExpressionException,
            ProActiveException, SAXException {
        XPathExpression infrastructureExpr = xpath.compile(INFRASTRUCTURE);
        NodeList t = (NodeList) infrastructureExpr.evaluate(document,
                XPathConstants.NODESET);
        Node infrastructureContext = t.item(0);
        expr = xpath.compile(PROCESS_DEFINITIONS);
        result = expr.evaluate(infrastructureContext, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);

            // get parent id
            String id = node.getParentNode().getAttributes().getNamedItem("id")
                    .getNodeValue();

            String processType = node.getNodeName();
            String processClassName = node.getAttributes()
                    .getNamedItem("class").getNodeValue();
            ExternalProcess targetProcess = proActiveDescriptor.createProcess(
                    id, processClassName);

            if (processType.equals(JVM_PROCESS_TAG)) {

                JVMProcess jvmProcess = ((JVMProcess) targetProcess);

                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); ++j) {
                    Node child = childNodes.item(j);
                    if (child.getNodeName().equals(CLASSPATH_TAG)) {
                        String classpath = getPath(child);
                        jvmProcess.setClasspath(classpath);
                    } else if (child.getNodeName().equals(BOOT_CLASSPATH_TAG)) {
                        String classpath = getPath(child);
                        jvmProcess.setBootClasspath(classpath);
                    } else if (child.getNodeName().equals(JAVA_PATH_TAG)) {
                        String path = getPath(child);
                        jvmProcess.setJavaPath(path);
                    } else if (child.getNodeName().equals(POLICY_FILE_TAG)) {
                        String path = getPath(child);
                        jvmProcess.setPolicyFile(path);
                    } else if (child.getNodeName().equals(LOG4J_FILE_TAG)) {
                        String path = getPath(child);
                        jvmProcess.setLog4jFile(path);
                    } else if (child.getNodeName().equals(
                            PROACTIVE_PROPS_FILE_TAG)) {
                        String path = getPath(child);
                        jvmProcess.setJvmOptions("-Dproactive.configuration="
                                + path);
                    } else if (child.getNodeName().equals(JVMPARAMETERS_TAG)) {
                        String params = getParameters(child);
                        jvmProcess.setParameters(params);
                    } else if (child.getNodeName().equals(EXTENDED_JVM_TAG)) {
                        Node overwriteParamsArg = child.getAttributes()
                                .getNamedItem("overwriteParameters");
                        if (overwriteParamsArg != null
                                && "yes".equals(overwriteParamsArg
                                        .getNodeValue()))
                            jvmProcess.setOverwrite(true);
                        try {
                            proActiveDescriptor.mapToExtendedJVM(
                                    (JVMProcess) targetProcess, child
                                            .getAttributes().getNamedItem(
                                                    "refid").getNodeValue());
                        } catch (ProActiveException e) {
                            throw new SAXException(e);
                        }

                    }
                }
            } else if (processType.equals(RSH_PROCESS_TAG)) {
                new RshProcessExtractor(targetProcess, node,
                        infrastructureContext);
            } else if (processType.equals(MAPRSH_PROCESS_TAG)) {
                new MapRshProcessExtractor(targetProcess, node,
                        infrastructureContext);
            } else if (processType.equals(SSH_PROCESS_TAG)) {
                new SshProcessExtractor(targetProcess, node,
                        infrastructureContext);
            } else if (processType.equals(RLOGIN_PROCESS_TAG)) {
                new RloginProcessExtractor(targetProcess, node,
                        infrastructureContext);
            } else if (processType.equals(BSUB_PROCESS_TAG)) {
                new BSubProcessExtractor(targetProcess, node,
                        infrastructureContext);
            } else if (processType.equals(GLOBUS_PROCESS_TAG)) {
                new GlobusProcessExtractor(targetProcess, node,
                        infrastructureContext);
            } else if (processType.equals(GLITE_PROCESS_TAG)) {
                new GliteProcessExtractor(targetProcess, node,
                        infrastructureContext);
            } else if (processType.equals(UNICORE_PROCESS_TAG)) {
                new UnicoreProcessExtractor(targetProcess, node,
                        infrastructureContext);
            }

        }
    }

    protected class ProcessExtractor {

        public ProcessExtractor(ExternalProcess targetProcess, Node node,
                Node context) throws XPathExpressionException, SAXException {

            Node namedItem = node.getAttributes().getNamedItem("closeStream");
            if (checkNonEmpty(namedItem)
                    && namedItem.getNodeValue().equals("yes")) {
                targetProcess.closeStream();
            }

            namedItem = node.getAttributes().getNamedItem("hostname");
            if (checkNonEmpty(namedItem)) {
                targetProcess.setHostname(namedItem.getNodeValue());
            }

            namedItem = node.getAttributes().getNamedItem("username");
            if (checkNonEmpty(namedItem)) {
                targetProcess.setUsername(namedItem.getNodeValue());
            }

            // get all env. vars
            //
            XPathExpression varExpr = xpath.compile("//pa:variable");
            NodeList vars = (NodeList) varExpr.evaluate(context,
                    XPathConstants.NODESET);

            ArrayList<String> envVars = new ArrayList<String>();

            for (int i = 0; i < vars.getLength(); ++i) {
                Node varNode = vars.item(i);
                String name = varNode.getAttributes().getNamedItem("name")
                        .getNodeValue();
                String value = varNode.getAttributes().getNamedItem("value")
                        .getNodeValue();
                if (checkNonEmpty(name))
                    envVars.add(name + "=" + value);
            }

            String[] env = new String[envVars.size()]; 
            envVars.toArray(env);
            targetProcess.setEnvironment(env);

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); ++j) {
                Node child = childNodes.item(j);

                if (child.getNodeName().equals(PROCESS_REFERENCE_TAG)) {
                    String refid = child.getAttributes().getNamedItem("refid")
                            .getNodeValue();
                    if (!(targetProcess instanceof ExternalProcessDecorator)) {
                        throw new org.xml.sax.SAXException(
                                "found a Process defined inside a non composite process");
                    }

                    ExternalProcessDecorator cep = (ExternalProcessDecorator) targetProcess;
                    proActiveDescriptor.registerProcess(cep, refid);

                } else if (child.getNodeName().equals(COMMAND_PATH_TAG)) {
                    String value = child.getAttributes().getNamedItem("value")
                            .getNodeValue();
                    targetProcess.setCommandPath(value);
                } else if (child.getNodeName().equals(FILE_TRANSFER_DEPLOY_TAG)) {
                    getFileTransfer("deploy", targetProcess, node);
                } else if (child.getNodeName().equals(
                        FILE_TRANSFER_RETRIEVE_TAG)) {
                    getFileTransfer("copy", targetProcess, node);
                }
            }
        }

        private FileTransferWorkShop getFileTransfer(String fileTransferQueue,
                ExternalProcess targetProcess, Node node) throws SAXException {

            FileTransferWorkShop fileTransferStructure;
            if (fileTransferQueue.equalsIgnoreCase("deploy")) {
                fileTransferStructure = targetProcess
                        .getFileTransferWorkShopDeploy();
            } else { // if(fileTransferQueue.equalsIgnoreCase("retrieve"))
                fileTransferStructure = targetProcess
                        .getFileTransferWorkShopRetrieve();
            }

            if (!checkNonEmpty(node.getAttributes().getNamedItem("refid"))) {
                throw new org.xml.sax.SAXException(node.getNodeName()
                        + " defined without 'refid' attribute");
            }
            String ftRefId = node.getAttributes().getNamedItem("refid")
                    .getNodeValue();

            if (ftRefId.equalsIgnoreCase(FILE_TRANSFER_IMPLICT_KEYWORD)) {
                fileTransferStructure.setImplicit(true);
            } else {
                fileTransferStructure.setImplicit(false);
                fileTransferStructure.addFileTransfer(proActiveDescriptor
                        .getFileTransfer(ftRefId));
            }

            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                Node childNode = childNodes.item(i);
                String nodeName = childNode.getNodeName();
                if (nodeName.equals(FILE_TRANSFER_COPY_PROTOCOL_TAG)) {
                    fileTransferStructure.setFileTransferCopyProtocol(childNode
                            .getTextContent());
                } else if (nodeName.equals(FILE_TRANSFER_SRC_INFO_TAG)) {
                    getTransferInfo(true, fileTransferStructure, childNode);
                } else if (nodeName.equals(FILE_TRANSFER_DST_INFO_TAG)) {
                    getTransferInfo(false, fileTransferStructure, childNode);
                }
            }

            return fileTransferStructure;
        }

        private void getTransferInfo(boolean src,
                FileTransferWorkShop fileTransferStructure, Node node) {
            String[] parameter = { "prefix", "hostname", "username", "password" };

            for (int i = 0; i < parameter.length; i++) {
                Node namedItem = node.getAttributes()
                        .getNamedItem(parameter[i]);
                if (checkNonEmpty(namedItem)) {
                    if (src)
                        fileTransferStructure.setFileTransferStructureSrcInfo(
                                parameter[i], namedItem.getNodeValue());
                    else
                        fileTransferStructure.setFileTransferStructureDstInfo(
                                parameter[i], namedItem.getNodeValue());
                }
            }
        }

    }

    protected class RshProcessExtractor extends ProcessExtractor {

        public RshProcessExtractor(ExternalProcess targetProcess, Node node,
                Node context) throws XPathExpressionException, SAXException {
            super(targetProcess, node, context);
        }

    }

    protected class MapRshProcessExtractor extends RshProcessExtractor {

        public MapRshProcessExtractor(ExternalProcess targetProcess, Node node,
                Node context) throws XPathExpressionException, SAXException {
            super(targetProcess, node, context);

            Node namedItem = node.getAttributes().getNamedItem("parallelize");
            if (!checkNonEmpty(namedItem)) {
                ((MapRshProcess) targetProcess)
                        .setParallelization("parallelize");
            }

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); ++j) {
                Node child = childNodes.item(j);

                if (child.getNodeName().equals(SCRIPT_PATH_TAG)) {
                    String path = getPath(child);
                    ((MapRshProcess) targetProcess).setScriptLocation(path);
                }
            }

        }
    }

    protected class SshProcessExtractor extends ProcessExtractor {

        public SshProcessExtractor(ExternalProcess targetProcess, Node node,
                Node context) throws XPathExpressionException, SAXException {
            super(targetProcess, node, context);
        }

    }

    protected class RloginProcessExtractor extends ProcessExtractor {

        public RloginProcessExtractor(ExternalProcess targetProcess, Node node,
                Node context) throws XPathExpressionException, SAXException {
            super(targetProcess, node, context);
        }

    }

    protected class BSubProcessExtractor extends ProcessExtractor {

        public BSubProcessExtractor(ExternalProcess targetProcess, Node node,
                Node context) throws XPathExpressionException, SAXException {
            super(targetProcess, node, context);

            Node namedItem = node.getAttributes().getNamedItem("interactive");
            if (checkNonEmpty(namedItem)) {
                ((LSFBSubProcess) targetProcess).setInteractive(namedItem
                        .getNodeValue());
            }

            namedItem = node.getAttributes().getNamedItem("queue");
            if (checkNonEmpty(namedItem)) {
                ((LSFBSubProcess) targetProcess).setQueueName(namedItem
                        .getNodeValue());
            }

            namedItem = node.getAttributes().getNamedItem("jobname");
            if (checkNonEmpty(namedItem)) {
                ((LSFBSubProcess) targetProcess).setJobname(namedItem
                        .getNodeValue());
            }

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); ++j) {
                Node child = childNodes.item(j);

                if (child.getNodeName().equals(BSUB_OPTIONS_TAG)) {
                    new BSubOptionsExtractor(targetProcess, child);
                }
            }
        }

        protected class BSubOptionsExtractor {
            public BSubOptionsExtractor(ExternalProcess targetProcess, Node node)
                    throws SAXException {
                NodeList childNodes = node.getChildNodes();
                LSFBSubProcess bSubProcess = (LSFBSubProcess) targetProcess;

                for (int i = 0; i < childNodes.getLength(); ++i) {
                    Node childNode = childNodes.item(i);
                    String nodeName = childNode.getNodeName();
                    if (nodeName.equals(HOST_LIST_TAG)) {
                        bSubProcess.setHostList(childNode.getNodeValue());
                    } else if (nodeName.equals(PROCESSOR_TAG)) {
                        bSubProcess
                                .setProcessorNumber(childNode.getNodeValue());
                    } else if (nodeName.equals(RES_REQ_TAG)) {
                        bSubProcess
                                .setRes_requirement(childNode.getNodeValue());
                    } else if (nodeName.equals(SCRIPT_PATH_TAG)) {
                        String path = getPath(childNode);
                        bSubProcess.setScriptLocation(path);
                    }
                }
            }
        }

    }

    protected class GlobusProcessExtractor extends ProcessExtractor {

        public GlobusProcessExtractor(ExternalProcess targetProcess, Node node,
                Node context) throws XPathExpressionException, SAXException {
            super(targetProcess, node, context);
            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); ++j) {
                Node child = childNodes.item(j);
                if (child.getNodeName().equals(BSUB_OPTIONS_TAG)) {
                    new GlobusOptionsExtractor(targetProcess, child);
                }
            }

        }

        protected class GlobusOptionsExtractor {

            public GlobusOptionsExtractor(ExternalProcess targetProcess,
                    Node node) {
                GlobusProcess globusProcess = (GlobusProcess) targetProcess;
                NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); ++j) {
                    Node child = childNodes.item(j);
                    if (child.getNodeName().equals(COUNT_TAG)) {
                        globusProcess.setCount(child.getNodeValue());
                    } else if (child.getNodeName().equals(GLOBUS_MAXTIME_TAG)) {
                        globusProcess.setMaxTime(child.getNodeValue());
                    } else if (child.getNodeName().equals(OUTPUT_FILE)) {
                        globusProcess.setStdout(child.getNodeValue());
                    } else if (child.getNodeName().equals(ERROR_FILE)) {
                        globusProcess.setStderr(child.getNodeValue());
                    }
                }

            }
        }
    }

    protected class GliteProcessExtractor extends ProcessExtractor {

        public GliteProcessExtractor(ExternalProcess targetProcess, Node node,
                Node context) throws XPathExpressionException, SAXException {
            super(targetProcess, node, context);

            GLiteProcess gliteProcess = ((GLiteProcess) targetProcess);

            Node namedItem = node.getAttributes().getNamedItem("Type");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobType(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("jobType");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobJobType(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("JDLFileName");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setFileName(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("hostname");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setNetServer(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("executable");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobExecutable(namedItem.getNodeValue());
                gliteProcess.setCommand_path(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("stdOutput");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobStdOutput(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("stdInput");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobStdInput(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("stdError");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobStdError(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("outputse");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobOutput_se(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes()
                    .getNamedItem("virtualOrganisation");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobVO(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("retryCount");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobRetryCount(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("myProxyServer");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobMyProxyServer(namedItem.getNodeValue());
            }
            namedItem = node.getAttributes().getNamedItem("nodeNumber");
            if (checkNonEmpty(namedItem)) {
                gliteProcess.setJobNodeNumber(Integer.parseInt(namedItem
                        .getNodeValue()));
            }

            NodeList childNodes = node.getChildNodes();
            for (int j = 0; j < childNodes.getLength(); ++j) {
                Node child = childNodes.item(j);
                if (child.getNodeName().equals(GLITE_CONFIG_TAG)) {
                    String path = getPath(child);
                    gliteProcess.setJobEnvironment(path);
                } else if (child.getNodeName().equals(GLITE_ENVIRONMENT_TAG)) {
                    gliteProcess.setJobEnvironment(child.getNodeValue());
                } else if (child.getNodeName().equals(GLITE_REQUIREMENTS_TAG)) {
                    gliteProcess.setJobRequirements(child.getNodeValue());
                } else if (child.getNodeName().equals(GLITE_RANK_TAG)) {
                    gliteProcess.setJobRank(child.getNodeValue());
                } else if (child.getNodeName().equals(GLITE_INPUTDATA_TAG)) {
                    new GliteInputExtractor(gliteProcess, child);
                } else if (child.getNodeName()
                        .equals(GLITE_PROCESS_OPTIONS_TAG)) {
                    new GliteOptionsExtractor(gliteProcess, child);
                }
            }

        }

        protected class GliteInputExtractor {
            public GliteInputExtractor(GLiteProcess gliteProcess, Node node) {
                Node namedItem = node.getAttributes().getNamedItem(
                        "dataAccessProtocol");
                if (checkNonEmpty(namedItem)) {
                    gliteProcess.setJobDataAccessProtocol(namedItem
                            .getNodeValue());
                }
                namedItem = node.getAttributes().getNamedItem("storageIndex");
                if (checkNonEmpty(namedItem)) {
                    gliteProcess.setJobStorageIndex(namedItem.getNodeValue());
                }

            }

        }

        protected class GliteOptionsExtractor {

            public GliteOptionsExtractor(GLiteProcess gliteProcess, Node node)
                    throws SAXException {
                NodeList childNodes = node.getChildNodes();
                for (int i = 0; i < childNodes.getLength(); ++i) {
                    Node childNode = childNodes.item(i);
                    if (childNode.getNodeName().equals(GLITE_PATH_TAG)) {
                        String path = getPath(childNode);
                        gliteProcess.setFilePath(path);
                    } else if (childNode.getNodeName().equals(
                            GLITE_REMOTE_PATH_TAG)) {
                        String path = getPath(childNode);
                        gliteProcess.setRemoteFilePath(path);
                        gliteProcess.setJdlRemote(true);
                    } else if (childNode.getNodeName().equals(GLITE_CONFIG_TAG)) {
                        String path = getPath(childNode);
                        gliteProcess.setConfigFile(path);
                        gliteProcess.setConfigFileOption(true);
                    } else if (childNode.getNodeName().equals(
                            GLITE_INPUTSANDBOX_TAG)) {
                        String sandbox = childNode.getNodeValue();
                        StringTokenizer st = new StringTokenizer(sandbox);
                        while (st.hasMoreTokens()) {
                            gliteProcess.addInputSBEntry(st.nextToken());
                        }
                    } else if (childNode.getNodeName().equals(
                            GLITE_OUTPUTSANDBOX_TAG)) {
                        String sandbox = childNode.getNodeValue();
                        StringTokenizer st = new StringTokenizer(sandbox);
                        while (st.hasMoreTokens()) {
                            gliteProcess.addOutputSBEntry(st.nextToken());
                        }
                    } else if (childNode.getNodeName().equals(
                            GLITE_ARGUMENTS_TAG)) {
                        gliteProcess.setJobArgument(childNode.getNodeValue());
                    }
                }
            }

        }
    }

    protected class UnicoreProcessExtractor extends ProcessExtractor {

        public UnicoreProcessExtractor(ExternalProcess targetProcess,
                Node node, Node context) throws XPathExpressionException,
                SAXException {
            super(targetProcess, node, context);
            UnicoreProcess unicoreProcess = ((UnicoreProcess) targetProcess);

            Node namedItem = node.getAttributes().getNamedItem("jobname");
            if (checkNonEmpty(namedItem)) {
                unicoreProcess.uParam.setUsiteName(namedItem.getNodeValue());
            }

            namedItem = node.getAttributes().getNamedItem("keypassword");
            if (checkNonEmpty(namedItem)) {
                unicoreProcess.uParam.setKeyPassword(namedItem.getNodeValue());
            }

            namedItem = node.getAttributes().getNamedItem("submitjob");
            if (checkNonEmpty(namedItem)) {
                unicoreProcess.uParam.setSubmitJob(namedItem.getNodeValue());
            }

            namedItem = node.getAttributes().getNamedItem("savejob");
            if (checkNonEmpty(namedItem)) {
                unicoreProcess.uParam.setSaveJob(namedItem.getNodeValue());
            }

            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); ++i) {
                Node childNode = childNodes.item(i);
                if (childNode.getNodeName().equals(UNICORE_DIR_PATH_TAG)) {
                    String path = getPath(childNode);
                    unicoreProcess.uParam.setUnicoreDir(path);
                } else if (childNode.getNodeName().equals(
                        UNICORE_KEYFILE_PATH_TAG)) {
                    String path = getPath(childNode);
                    unicoreProcess.uParam.setKeyFilePath(path);
                } else if (childNode.getNodeName().equals(UNICORE_OPTIONS_TAG)) {
                    NodeList grandChildren = childNode.getChildNodes();
                    for (int j = 0; j < grandChildren.getLength(); ++j) {
                        Node grandChildNode = grandChildren.item(j);
                        if (grandChildNode.getNodeName().equals(
                                UNICORE_USITE_TAG)) {
                            new UnicoreUSiteExtractor(grandChildNode,
                                    unicoreProcess);
                        } else if (grandChildNode.getNodeName().equals(
                                UNICORE_VSITE_TAG)) {
                            new UnicoreVSiteExtractor(grandChildNode,
                                    unicoreProcess);
                        }
                    }
                }
            }

        }

        protected class UnicoreUSiteExtractor {

            public UnicoreUSiteExtractor(Node grandChildNode,
                    UnicoreProcess unicoreProcess) {

                Node namedItem = grandChildNode.getAttributes().getNamedItem(
                        "name");

                if (checkNonEmpty(namedItem)) {
                    unicoreProcess.uParam
                            .setUsiteName(namedItem.getNodeValue());
                }

                namedItem = grandChildNode.getAttributes().getNamedItem("type");

                if (checkNonEmpty(namedItem)) {
                    unicoreProcess.uParam
                            .setUsiteType(namedItem.getNodeValue());
                }

                namedItem = grandChildNode.getAttributes().getNamedItem("url");

                if (checkNonEmpty(namedItem)) {
                    unicoreProcess.uParam.setUsiteUrl(namedItem.getNodeValue());
                }

            }
        }

        protected class UnicoreVSiteExtractor {

            public UnicoreVSiteExtractor(Node grandChildNode,
                    UnicoreProcess unicoreProcess) {

                Node namedItem = grandChildNode.getAttributes().getNamedItem(
                        "name");

                if (checkNonEmpty(namedItem)) {
                    unicoreProcess.uParam
                            .setVsiteName(namedItem.getNodeValue());
                }

                namedItem = grandChildNode.getAttributes()
                        .getNamedItem("nodes");

                if (checkNonEmpty(namedItem)) {
                    unicoreProcess.uParam.setVsiteNodes(Integer
                            .parseInt(namedItem.getNodeValue()));
                }

                namedItem = grandChildNode.getAttributes().getNamedItem(
                        "processors");

                if (checkNonEmpty(namedItem)) {
                    unicoreProcess.uParam.setVsiteProcessors(Integer
                            .parseInt(namedItem.getNodeValue()));
                }

                namedItem = grandChildNode.getAttributes().getNamedItem(
                        "memory");

                if (checkNonEmpty(namedItem)) {
                    unicoreProcess.uParam.setVsiteMemory(Integer
                            .parseInt(namedItem.getNodeValue()));
                }

                namedItem = grandChildNode.getAttributes().getNamedItem(
                        "runtime");

                if (checkNonEmpty(namedItem)) {
                    unicoreProcess.uParam.setVsiteRuntime(Integer
                            .parseInt(namedItem.getNodeValue()));
                }

                namedItem = grandChildNode.getAttributes().getNamedItem(
                        "priority");

                if (checkNonEmpty(namedItem)) {
                    unicoreProcess.uParam.setVsitePriority(namedItem
                            .getNodeValue());
                }
            }
        }

    }

    // //////////////////////////////
    // utility methods
    // //////////////////////////////

    private String getParameters(Node classpathNode) throws SAXException {
        NodeList childNodes = classpathNode.getChildNodes();

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node subNode = childNodes.item(i);
            if (subNode.getAttributes() == null)
                continue;
            Node namedItem = subNode.getAttributes().getNamedItem("value");
            if (checkNonEmpty(namedItem)) {
                String parameter = namedItem.getNodeValue();

                if (i != 0)
                    sb.append(' ');
                sb.append(parameter);
            }
        }

        return sb.toString().trim();
    }

    private String getPath(Node node) throws SAXException {
        NodeList childNodes = node.getChildNodes();

        StringBuffer sb = new StringBuffer();
        String pathSeparator = System.getProperty("path.separator");

        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node pathNode = childNodes.item(i);
            String pathElement = expandPath(pathNode);

            if (i != 0)
                sb.append(pathSeparator);
            sb.append(pathElement);
        }

        return sb.toString().trim();
    }

    private static final String ORIGIN_ATTRIBUTE = "origin";

    private static final String USER_HOME_ORIGIN = "user.home";

    private static final String WORKING_DIRECTORY_ORIGIN = "user.dir";

    private static final String FROM_CLASSPATH_ORIGIN = "user.classpath";

    // private static final String PROACTIVE_ORIGIN = "proactive.home";
    private static final String DEFAULT_ORIGIN = USER_HOME_ORIGIN;

    private static final String VALUE_ATTRIBUTE = "value";

    private static final String proActiveDir = ProActiveConfiguration
            .getProperty("proactive.home");

    private static final String userDir = System.getProperty("user.dir");

    private static final String userHome = System.getProperty("user.home");

    private static final String javaHome = System.getProperty("java.home");

    protected Document document;

    private DocumentBuilderFactory domFactory;

    private String expandPath(Node pathNode) throws SAXException {
        String name = pathNode.getNodeName();
        String value = pathNode.getAttributes().getNamedItem(VALUE_ATTRIBUTE)
                .getNodeValue();
        String origin = DEFAULT_ORIGIN;
        Node originAttr = pathNode.getAttributes().getNamedItem(
                ORIGIN_ATTRIBUTE);
        if (originAttr != null)
            origin = originAttr.getNodeValue();

        String res = null;

        if (name.equals(ABS_PATH_TAG)) {
            res = value;
        } else if (name.equals(REL_PATH_TAG)) {
            if (origin.equals(USER_HOME_ORIGIN)) {
                res = resolvePath(userHome, value);
            } else if (origin.equals(WORKING_DIRECTORY_ORIGIN)) {
                res = resolvePath(userDir, value);
                // } else if (origin.equals(PROACTIVE_ORIGIN)) {
                // setResultObject(resolvePath(proActiveDir, value));
            } else if (origin.equals(FROM_CLASSPATH_ORIGIN)) {
                res = resolvePathFromClasspath(value);
            } else {
                throw new org.xml.sax.SAXException(
                        "Relative Path element defined with an unknown origin="
                                + origin);
            }
        }

        return res;
    }

    private String resolvePath(String origin, String value) {
        java.io.File originDirectory = new java.io.File(origin);

        // in case of relative path, if the user put a / then remove it
        // transparently
        if (value.startsWith("/")) {
            value = value.substring(1);
        }
        java.io.File file = new java.io.File(originDirectory, value);
        return file.getAbsolutePath();
    }

    private String resolvePathFromClasspath(String value) {
        ClassLoader cl = this.getClass().getClassLoader();
        java.net.URL url = cl.getResource(value);
        return url.getPath();
    }

    protected boolean checkNonEmpty(String s) {
        return (s != null) && (s.length() > 0);
    }

    protected boolean checkNonEmpty(Node n) {
        return n != null && checkNonEmpty(n.getNodeValue());
    }

    class ProActiveNamespaceContext implements NamespaceContext {

        public String getNamespaceURI(String prefix) {
            if (prefix == null)
                throw new NullPointerException("Null prefix");
            else if ("pa".equals(prefix))
                return "http://www-sop.inria.fr/oasis/ProActive/schemas/DescriptorSchema.xsd";
            else if ("xml".equals(prefix))
                return XMLConstants.XML_NS_URI;
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

    private void debugDump(Node topNode) {
        NodeList childNodes = topNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            Node node = childNodes.item(i);
            System.out.println(node.getNodeName());
            debugDump(node);
        }
    }

    public ProActiveDescriptorImpl getProActiveDescriptor() {
        return proActiveDescriptor;
    }

}
