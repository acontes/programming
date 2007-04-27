package org.objectweb.proactive.examples.basic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
import org.objectweb.proactive.core.descriptor.xml.ProcessReferenceHandler;
import org.objectweb.proactive.core.process.ExternalProcess;
import org.objectweb.proactive.core.process.ExternalProcessDecorator;
import org.objectweb.proactive.core.process.JVMProcess;
import org.objectweb.proactive.core.process.filetransfer.FileTransferWorkShop;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class JaxpTest implements ProActiveDescriptorConstants {

    public static final String VIRTUAL_NODES_DEFINITIONS  = "//pa:componentDefinition/pa:virtualNodesDefinition/pa:virtualNode";
    public static final String VIRTUAL_NODES_ACQUISITIONS = "//pa:componentDefinition/pa:virtualNodesAcquisition/pa:virtualNode";

    // deployment
    public static final String DEPLOYMENT                 = "//pa:deployment";
    public static final String REGISTER                   = "//pa:register";
    public static final String VM_MAPPING                 = "//pa:map/pa:jvmset/pa:vmname/@value";
    public static final String CURRENT_VM_MAPPING         = "//pa:map/pa:jvmset/pa:currentJVM";
    public static final String LOOKUP                     = "//pa:lookup";
    public static final String JVM_CREATION               = "//pa:creation/pa:processReference/@refid";
    public static final String JVM_ACQUISITION            = "//pa:acquisition/pa:serviceReference/@refid";

    // infrastructure
    public static final String INFRASTRUCTURE             = "//pa:infrastructure";
    public static final String PROCESS_DEFINITIONS        = "//pa:processDefinition/*";
    public static final String SERVICE_DEFINITION         = "//pa:serviceDefinition";
    

    protected ProActiveDescriptorImpl proActiveDescriptor;
    private XPathExpression expr;
    private Object result;
    private NodeList nodes;
    private XPath xpath;
    private Document document;

    
    public static void main(String[] args) {
        
        JaxpTest jaxptest = new JaxpTest();
        
        File f = new File(args[0]);
        if (f.canRead()) {        
            jaxptest.parserTest(args[0]);
        } else
            throw new RuntimeException("can't read " + args[0]);
    }

    
    public void parserTest(String filename) {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder;
        try {
            
            proActiveDescriptor = new ProActiveDescriptorImpl(filename);
            
            builder = domFactory.newDocumentBuilder();
            System.out.println("Parsing " + filename);
            document = builder.parse(filename);
            XPathFactory factory = XPathFactory.newInstance();

            xpath = factory.newXPath();
            xpath.setNamespaceContext(new ProActiveNamespaceContext());
            
            handleComponentDefinitions();            
            
            handleDeployment();
            
            
            
            
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleInfrastructure() throws XPathExpressionException, ProActiveException, SAXException {
        XPathExpression infrastructureContext = xpath.compile(INFRASTRUCTURE);
        expr = xpath.compile(PROCESS_DEFINITIONS);
        result = expr.evaluate(infrastructureContext, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            
            // get parent id
            String id = node.getParentNode().getAttributes().getNamedItem("id").getNodeValue();
            
            String processType = node.getNodeName();
            String processClassName = node.getAttributes().getNamedItem("class").getNodeValue();
            ExternalProcess targetProcess = proActiveDescriptor.createProcess(id, processClassName);            
            
            if (processType.equals(JVM_PROCESS_TAG)) {
                
                JVMProcess jvmProcess = ((JVMProcess)targetProcess);
                
                NodeList childNodes = node.getChildNodes();
                for(int j = 0; j < childNodes.getLength(); ++j) {
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
                    } else if (child.getNodeName().equals(PROACTIVE_PROPS_FILE_TAG)) {
                        String path = getPath(child);
                        jvmProcess.setJvmOptions("-Dproactive.configuration=" + path);
                    } else if (child.getNodeName().equals(JVMPARAMETERS_TAG)) {
                        String params = getParameters(child);
                        jvmProcess.setParameters(params);
                    } else if (child.getNodeName().equals(EXTENDED_JVM_TAG)) {
                        Node overwriteParamsArg = child.getAttributes().getNamedItem("overwriteParameters");
                        if (overwriteParamsArg != null && "yes".equals(overwriteParamsArg.getNodeValue()))
                            jvmProcess.setOverwrite(true);                        
                        try {
                            proActiveDescriptor.mapToExtendedJVM((JVMProcess) targetProcess,
                                child.getAttributes().getNamedItem("refid").getNodeValue());
                        } catch (ProActiveException e) {
                            throw new SAXException(e);
                        }
                        
                            
                    }
                }
            } else if (processType.equals(RSH_PROCESS_TAG)) {
                getProcess(targetProcess, node, infrastructureContext);
            }
            
        }
    }

    private void getProcess(ExternalProcess targetProcess, Node node, XPathExpression context) throws XPathExpressionException, SAXException {
        
        Node namedItem = node.getAttributes().getNamedItem("closeStream");
        if (checkNonEmpty(namedItem) && namedItem.getNodeValue().equals("yes")) {
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
        NodeList vars = (NodeList) varExpr.evaluate(context, XPathConstants.NODESET);
        
        ArrayList<String> envVars = new ArrayList<String>();
        
        for(int i = 0; i < vars.getLength(); ++i) {
            Node varNode = vars.item(i);
            String name  = varNode.getAttributes().getNamedItem("name").getNodeValue();
            String value = varNode.getAttributes().getNamedItem("value").getNodeValue();
            if (checkNonEmpty(name))
                envVars.add(name + "=" + value);
        }
        
        String[] env = (String[]) envVars.toArray();
        targetProcess.setEnvironment(env);
        
        
        
        NodeList childNodes = node.getChildNodes();
        for(int j = 0; j < childNodes.getLength(); ++j) {
            Node child = childNodes.item(j);
            
            if (child.getNodeName().equals(PROCESS_REFERENCE_TAG)) {
                String refid = child.getAttributes().getNamedItem("refid").getNodeValue();
                if (!(targetProcess instanceof ExternalProcessDecorator)) {
                    throw new org.xml.sax.SAXException(
                        "found a Process defined inside a non composite process");
                }

                ExternalProcessDecorator cep = (ExternalProcessDecorator) targetProcess;
                proActiveDescriptor.registerProcess(cep, refid);

            } else if (child.getNodeName().equals(COMMAND_PATH_TAG)) {
                String value = child.getAttributes().getNamedItem("value").getNodeValue();
                targetProcess.setCommandPath(value);
            } else if (child.getNodeName().equals(FILE_TRANSFER_DEPLOY_TAG)) {
                getFileTransfer("deploy", targetProcess, node);
            } else if (child.getNodeName().equals(FILE_TRANSFER_RETRIEVE_TAG)) {
                getFileTransfer("copy", targetProcess, node);                
            }
        }
    }

    private FileTransferWorkShop getFileTransfer(String fileTransferQueue, ExternalProcess targetProcess, Node node) throws SAXException {

        FileTransferWorkShop fileTransferStructure;
        if (fileTransferQueue.equalsIgnoreCase("deploy")) {
            fileTransferStructure = targetProcess.getFileTransferWorkShopDeploy();
        } else { //if(fileTransferQueue.equalsIgnoreCase("retrieve"))
            fileTransferStructure = targetProcess.getFileTransferWorkShopRetrieve();
        }
        
        if(!checkNonEmpty(node.getAttributes().getNamedItem("refid"))) {
            throw new org.xml.sax.SAXException(node.getNodeName() +
            " defined without 'refid' attribute");
        }
        String ftRefId = node.getAttributes().getNamedItem("refid").getNodeValue();
        
        if (ftRefId.equalsIgnoreCase(FILE_TRANSFER_IMPLICT_KEYWORD)) {
            fileTransferStructure.setImplicit(true);
        } else {
            fileTransferStructure.setImplicit(false);
            fileTransferStructure.addFileTransfer(proActiveDescriptor.getFileTransfer(ftRefId));
        }
        
        NodeList childNodes = node.getChildNodes();
        for(int i = 0; i < childNodes.getLength(); ++i) {
            Node childNode = childNodes.item(i);
            String nodeName = childNode.getNodeName();
            if (nodeName.equals(FILE_TRANSFER_COPY_PROTOCOL_TAG)) {
                fileTransferStructure.setFileTransferCopyProtocol(childNode.getTextContent());
            } else if (nodeName.equals(FILE_TRANSFER_SRC_INFO_TAG)) {
                getTransferInfo(true, fileTransferStructure, childNode);
            } else if (nodeName.equals(FILE_TRANSFER_DST_INFO_TAG)) {
                getTransferInfo(false, fileTransferStructure, childNode);                
            }
        }
        
        return fileTransferStructure;
    }

    private void getTransferInfo(boolean src, FileTransferWorkShop fileTransferStructure, Node node) {
        String[] parameter = {
                "prefix", "hostname", "username", "password"
            };

        for (int i = 0; i < parameter.length; i++) {
            Node namedItem = node.getAttributes().getNamedItem(parameter[i]);
            if (checkNonEmpty(namedItem)) {
                if (src)
                    fileTransferStructure.setFileTransferStructureSrcInfo(parameter[i], namedItem.getNodeValue());
                else
                    fileTransferStructure.setFileTransferStructureDstInfo(parameter[i], namedItem.getNodeValue());
            }
        }
    }


    private String getParameters(Node classpathNode) throws SAXException {
        NodeList childNodes = classpathNode.getChildNodes();
        
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < childNodes.getLength(); ++i) {
            Node subNode = childNodes.item(i);
            String parameter = subNode.getAttributes().getNamedItem("value").getNodeValue();
        
            if (i != 0)
                sb.append(' ');
            sb.append(parameter);
        }

        return sb.toString().trim();        
    }
    
    
    private String getPath(Node classpathNode) throws SAXException {
        NodeList childNodes = classpathNode.getChildNodes();
        
        StringBuffer sb = new StringBuffer();
        String pathSeparator = System.getProperty("path.separator");

        for(int i = 0; i < childNodes.getLength(); ++i) {
            Node pathNode = childNodes.item(i);
            String pathElement = getPathFromNode(pathNode);
        
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
    private static final String proActiveDir = ProActiveConfiguration.getProperty(
            "proactive.home");
    private static final String userDir = System.getProperty("user.dir");
    private static final String userHome = System.getProperty("user.home");
    private static final String javaHome = System.getProperty("java.home");

    private String getPathFromNode(Node pathNode) throws SAXException {
        String name = pathNode.getNodeName();
        String value = pathNode.getAttributes().getNamedItem(VALUE_ATTRIBUTE).getNodeValue();
        String origin = DEFAULT_ORIGIN;
        Node originAttr = pathNode.getAttributes().getNamedItem(ORIGIN_ATTRIBUTE);
        if(originAttr != null)
            origin = originAttr.getNodeValue();
        
        String res = null;
        
        if (name.equals(ABS_PATH_TAG)) {
            res = value;
        } else if (name.equals(REL_PATH_TAG)) {
            if (origin.equals(USER_HOME_ORIGIN)) {
                res = resolvePath(userHome, value);
            } else if (origin.equals(WORKING_DIRECTORY_ORIGIN)) {
                res = resolvePath(userDir, value);
//            } else if (origin.equals(PROACTIVE_ORIGIN)) {
//                setResultObject(resolvePath(proActiveDir, value));
            } else if (origin.equals(FROM_CLASSPATH_ORIGIN)) {
                res = resolvePathFromClasspath(value);
            } else {
                throw new org.xml.sax.SAXException(
                    "Relative Path element defined with an unknown origin=" +
                    origin);
            }
        }
        
        return res;
    }

    private String resolvePath(String origin, String value) {
        java.io.File originDirectory = new java.io.File(origin);

        // in case of relative path, if the user put a / then remove it transparently
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


    private void handleDeployment() throws XPathExpressionException, SAXException, IOException {
        //
        // register
        //
        XPathExpression deploymentContext = xpath.compile(DEPLOYMENT);
        expr = xpath.compile(REGISTER);
        result = expr.evaluate(deploymentContext, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node virtualNodeName = node.getAttributes().getNamedItem("virtualNode");
            
            Node protocol = node.getAttributes().getNamedItem("protocol");
            String protocolValue = checkNonEmpty(protocol) ? protocol.getNodeValue()
                    : ProActiveConfiguration.getProperty(Constants.PROPERTY_PA_COMMUNICATION_PROTOCOL);
            
            VirtualNodeImpl vnImpl = (VirtualNodeImpl) proActiveDescriptor.createVirtualNode(virtualNodeName.getNodeValue(),
                    false);

            vnImpl.setRegistrationProtocol(protocolValue);
        }

        //
        // mapping
        //
        
        // collect the mappings in a hashmap
        //
        expr = xpath.compile(VM_MAPPING);
        result = expr.evaluate(deploymentContext, XPathConstants.NODESET);
        nodes = (NodeList) result;
        
        HashMap<String, ArrayList<String> > vmMapping = new HashMap<String, ArrayList<String> >();
                    
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node mapParent = node.getParentNode().getParentNode().getParentNode();
            String virtualNodeName = mapParent.getAttributes().getNamedItem("virtualNode").getNodeValue();
            ArrayList<String> arrayList = vmMapping.get(virtualNodeName);
            if (arrayList == null) {
                arrayList = new ArrayList<String>();
                vmMapping.put(virtualNodeName, arrayList);
            }
            arrayList.add(node.getNodeValue());                
        }
        
        // set the VM mappings to each virtual node
        //
        for(String s : vmMapping.keySet()) {
            VirtualNode vn = proActiveDescriptor.createVirtualNode(s, false);
            for (String vmName : vmMapping.get(s)) {
                VirtualMachine vm = proActiveDescriptor.createVirtualMachine(vmName);
                vn.addVirtualMachine(vm);
            }
        }

        // current vm mappings
        //
        expr = xpath.compile(CURRENT_VM_MAPPING);
        result = expr.evaluate(deploymentContext, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node mapParent = node.getParentNode().getParentNode();
            String virtualNodeName = mapParent.getAttributes().getNamedItem("virtualNode").getNodeValue();                
            VirtualNode vn = proActiveDescriptor.createVirtualNode(virtualNodeName, false);
            vn.createNodeOnCurrentJvm(node.getAttributes().getNamedItem("protocol").getNodeValue());                
        }

        // vm lookup
        //
        expr = xpath.compile(LOOKUP);
        result = expr.evaluate(deploymentContext, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            
            String vnLookup = node.getAttributes().getNamedItem("virtualNode").getNodeValue();
            String host     = node.getAttributes().getNamedItem("host").getNodeValue();
            Node namedItem  = node.getAttributes().getNamedItem("protocol");
            if (namedItem == null)
                throw new org.xml.sax.SAXException("lookup Tag without any protocol defined");
            String protocol = namedItem.getNodeValue();
            Node portItem = node.getAttributes().getNamedItem("port");

            if (protocol.equals(Constants.JINI_PROTOCOL_IDENTIFIER) && portItem != null)
                throw new org.xml.sax.SAXException("For a jini lookup, no port number should be specified");
            
            String port = portItem != null ? portItem.getNodeValue() : "1099";

            VirtualNodeLookup vn = (VirtualNodeLookup) proActiveDescriptor.createVirtualNode(vnLookup,
                    true);

            vn.setLookupInformations(host, protocol, port);
        }
        
        
        // vm creation and acquisition
        //
        expr = xpath.compile(JVM_CREATION);
        result = expr.evaluate(deploymentContext, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node jvmParent = node.getParentNode().getParentNode().getParentNode();
            String jvmName = jvmParent.getAttributes().getNamedItem("name").getNodeValue();
            Node t = jvmParent.getAttributes().getNamedItem("askedNodes");
            VirtualMachine currentVM = proActiveDescriptor.createVirtualMachine(jvmName);
            if (checkNonEmpty(t))
                currentVM.setNbNodes(t.getNodeValue());
            proActiveDescriptor.registerProcess(currentVM, node.getNodeValue());
        }
        
        expr = xpath.compile(JVM_ACQUISITION);
        result = expr.evaluate(deploymentContext, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            Node jvmParent = node.getParentNode().getParentNode().getParentNode();
            String jvmName = jvmParent.getAttributes().getNamedItem("name").getNodeValue();
            Node t = jvmParent.getAttributes().getNamedItem("askedNodes");
            VirtualMachine currentVM = proActiveDescriptor.createVirtualMachine(jvmName);
            if (checkNonEmpty(t))
                currentVM.setNbNodes(t.getNodeValue());
            proActiveDescriptor.registerService(currentVM, node.getNodeValue());
        }
    }


    private void handleComponentDefinitions() throws XPathExpressionException {
        expr = xpath.compile(VIRTUAL_NODES_DEFINITIONS);
        result = expr.evaluate(document, XPathConstants.NODESET);
        nodes = (NodeList) result;
        for (int i = 0; i < nodes.getLength(); ++i) {
            Node node = nodes.item(i);
            NamedNodeMap attributes = node.getAttributes();
            Node nodeName = attributes.getNamedItem("name"),
            nodeProperty = attributes.getNamedItem("property");
            
            System.out.println("Virtual node definition : " + nodeName.getNodeValue() + " - " + nodeProperty.getNodeValue()); 

            VirtualNodeImpl vn = (VirtualNodeImpl) proActiveDescriptor.createVirtualNode(nodeName.getNodeValue(), false);
            if (checkNonEmpty(nodeProperty))
                vn.setProperty(nodeProperty.getNodeValue());
            
            Node timeout = attributes.getNamedItem("timeout"),
            waitForTimeoutN = attributes.getNamedItem("waitForTimeout");
            boolean waitForTimeout = false;
            if (checkNonEmpty(waitForTimeoutN)) {
                String nodeValue = waitForTimeoutN.getNodeValue().toLowerCase();
                waitForTimeout = Boolean.parseBoolean(nodeValue); // nodeValue.equals("y") ||  nodeValue.equals("true") || nodeValue.equals("1");
            }
                
            if (checkNonEmpty(timeout))
                vn.setTimeout(Long.parseLong(timeout.getNodeValue()), waitForTimeout);
            
            Node minNodeNumber = attributes.getNamedItem("minNodeNumber");
            if (minNodeNumber != null) {
                vn.setMinNumberOfNodes(Integer.parseInt(minNodeNumber.getNodeValue()));                    
            }
            
            Node ftServiceId = attributes.getNamedItem("ftServiceId");
            if (checkNonEmpty(ftServiceId)) {
                proActiveDescriptor.registerService(vn, ftServiceId.getNodeValue());
            }
            
            Node fileTransferDeploy = attributes.getNamedItem(FILE_TRANSFER_DEPLOY_TAG);
            if (checkNonEmpty(fileTransferDeploy)) {
                vn.addFileTransferDeploy(proActiveDescriptor.getFileTransfer(fileTransferDeploy.getNodeValue()));
            }
            
            Node techServiceId = attributes.getNamedItem(TECHNICAL_SERVICE_ID);
            if (checkNonEmpty(techServiceId)) {
                vn.addTechnicalService(proActiveDescriptor.getTechnicalService(techServiceId.getNodeValue()));
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
                proActiveDescriptor.createVirtualNode(nodeName.getNodeValue(), true);
        }
    }

    protected boolean checkNonEmpty(String s) {
        return (s != null) && (s.length() > 0);
    }

    protected boolean checkNonEmpty(Node n) {
        return n != null && checkNonEmpty(n.getNodeValue());
    }
 
    class ProActiveNamespaceContext implements NamespaceContext {

        public String getNamespaceURI(String prefix) {
            if (prefix == null) throw new NullPointerException("Null prefix");
            else if ("pa".equals(prefix)) return "http://www-sop.inria.fr/oasis/ProActive/schemas/DescriptorSchema.xsd";
            else if ("xml".equals(prefix)) return XMLConstants.XML_NS_URI;
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
        for(int i = 0; i < childNodes.getLength(); ++i) {            
            Node node = childNodes.item(i);
            System.out.println(node.getNodeName());
            debugDump(node);
        }
    }
    
}
