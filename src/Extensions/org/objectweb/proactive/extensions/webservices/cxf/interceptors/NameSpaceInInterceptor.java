package org.objectweb.proactive.extensions.webservices.cxf.interceptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.Bus;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.endpoint.Endpoint;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.staxutils.FragmentStreamReader;
import org.apache.cxf.staxutils.StaxUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class NameSpaceInInterceptor extends AbstractSoapInterceptor {

    private XMLStreamWriter writer;

    public NameSpaceInInterceptor() {
        super(Phase.RECEIVE);
    }

    public void displayNode(Node node) {
        System.err.print("<" + node.getLocalName() + ">");
        NodeList childList = node.getChildNodes();
        int nbChildren = childList.getLength();
        for (int k = 0; k < nbChildren; k++) {
            displayNode(childList.item(k));
        }
    }

    public void treatNode(Node node, int level) {

        NodeList childList = node.getChildNodes();
        int nbChildren = childList.getLength();
        if (level > 3) {
            System.err.println("node '" + node.getNodeName() + "' is on level " + level);
        }
        for (int k = 0; k < nbChildren; k++) {
            treatNode(childList.item(k), level + 1);
        }
    }

    public void handleMessage(SoapMessage message) throws Fault {

        final Exchange exchange = message.getExchange();
        final Endpoint endpoint = exchange.get(Endpoint.class);
        final Service service = endpoint.getService();
        List<ServiceInfo> infos = service.getServiceInfos();

        System.err.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        System.err.println("=====> EXCHANGE <=====");
        Set<Entry<String, Object>> entrySet = message.getExchange().entrySet();
        for (Entry<String, Object> entry : entrySet) {
            System.err.println(entry.getKey());
        }
        System.err.println("=====> CLASSES <=====");
        Set<Class<?>> classes = message.getContentFormats();
        for (Class<?> class1 : classes) {
            System.err.println(class1.getName());
        }

        InputStream inputStream = message.getContent(InputStream.class);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writer = StaxUtils.createXMLStreamWriter(out);

        try {
            XMLStreamReader reader = StaxUtils.createXMLStreamReader(inputStream);
            Document doc = StaxUtils.read(reader);
            //System.err.println("----------------------------------------------------------");
            //this.displayNode(doc);
            //System.err.println("----------------------------------------------------------");

            Node node = doc.getFirstChild();
            System.err.println("----------------------------------------------------------");
            treatNode(node, 1);
            StaxUtils.writeNode(node, this.writer, false);
            System.err.println("----------------------------------------------------------");
            //            int eventType = reader.getEventType();
            //            if (eventType == XMLStreamConstants.START_DOCUMENT) {
            //                System.err.println("START_DOCUMENT");
            //                writer.writeStartDocument(reader.getEncoding(), reader.getVersion());
            //            }
            //            String lastPrefix = "";
            //            String lastNameSpace = "";
            //            while (reader.hasNext()) {
            //                eventType = reader.next();
            //                System.err.println("EVENTTYPE = " + eventType);
            //                if (eventType == XMLStreamConstants.START_ELEMENT) {
            //                    System.err.println("START_ELEMENT = " + eventType);
            //                    System.err.println("Number of namespace = " + reader.getNamespaceCount());
            //                    if (!reader.getName().getPrefix().isEmpty() &&
            //                        !reader.getName().getNamespaceURI().isEmpty()) {
            //                        writer.writeStartElement(reader.getName().getPrefix(), reader.getName().getLocalPart(), reader.getName().getNamespaceURI());
            //                        lastPrefix = reader.getName().getPrefix();
            //                        lastNameSpace = reader.getName().getNamespaceURI();
            //                    } else if (!reader.getName().getNamespaceURI().isEmpty()) {
            //                        writer.writeStartElement(reader.getName().getNamespaceURI(), reader.getName().getLocalPart());
            //                        lastPrefix = "";
            //                        lastNameSpace = reader.getName().getNamespaceURI();
            //                    } else {
            ////                        writer.writeStartElement(lastPrefix, reader.getName().getLocalPart(), lastNameSpace);
            ////                        writer.writeStartElement(reader.getName().getLocalPart());
            //                        writer.writeStartElement(lastNameSpace, reader.getName().getLocalPart());
            //                    }
            //                    int nbNameSpace = reader.getNamespaceCount();
            //                    if ( nbNameSpace > 0) {
            //                        for (int j = 0 ; j < nbNameSpace ; j++) {
            //                            writer.writeNamespace(reader.getNamespacePrefix(j), reader.getNamespaceURI(j));
            //                        }
            //                    }
            //                    int nbAttribute = reader.getAttributeCount();
            //                    System.err.println("This element has " + nbAttribute + " attributes");
            //                    for (int i = 0 ; i < nbAttribute ; i++) {
            //                        System.err.println("Attribute " + i + " = " + reader.getAttributeLocalName(i));
            //                        writer.writeAttribute(reader.getAttributePrefix(i), reader.getAttributeNamespace(i), reader.getAttributeLocalName(i), reader.getAttributeValue(i));
            //                    }
            //                }
            //                if(eventType == XMLStreamConstants.CHARACTERS) {
            //                    System.err.println("START_CHARACTERS = " + eventType);
            //                    System.err.println("element characters = "+ reader.getText());
            //                    writer.writeCharacters(reader.getText());
            //                }
            //                if(eventType == XMLStreamConstants.CDATA) {
            //                    System.err.println("START_CDATA = " + eventType);
            //                    System.err.println("element CDATA = "+ reader.getText());
            //                    writer.writeCData(reader.getText());
            //                }
            //                if(eventType == XMLStreamConstants.ENTITY_REFERENCE) {
            //                    System.err.println("START_ENTITY_REFERENCE = " + eventType);
            //                    System.err.println("element entity ref = "+ reader.getText());
            //                    writer.writeEntityRef(reader.getText());
            //                }
            //                if (eventType == XMLStreamConstants.END_ELEMENT) {
            //                    System.err.println("END_ELEMENT = " + eventType);
            //                    writer.writeEndElement();
            //                }
            //            }
            //            eventType = reader.getEventType();
            //            if (eventType == XMLStreamConstants.END_DOCUMENT) {
            //                System.err.println("END_DOCUMENT");
            //                writer.writeEndDocument();
            //            }
            out.close();
            writer.close();

            byte[] bout = out.toByteArray();
            ByteArrayInputStream is = new ByteArrayInputStream(bout);
            message.setContent(InputStream.class, is);
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println(out.toString());
        System.err.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

    }

}
