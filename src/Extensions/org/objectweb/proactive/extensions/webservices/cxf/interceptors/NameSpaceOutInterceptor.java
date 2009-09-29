package org.objectweb.proactive.extensions.webservices.cxf.interceptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.binding.soap.interceptor.AbstractSoapInterceptor;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.staxutils.StaxUtils;


public class NameSpaceOutInterceptor extends AbstractSoapInterceptor {

    public NameSpaceOutInterceptor() {
        super(Phase.WRITE);
    }

    public void handleMessage(SoapMessage message) throws Fault {

        System.err.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv");
        System.err.println("=====> ENTRIES <=====");
        Set<Entry<String, Object>> entrySet = message.entrySet();
        for (Entry<String, Object> entry : entrySet) {
            System.err.println(entry.getKey());
        }
        System.err.println("=====> CLASSES <=====");
        Set<Class<?>> classes = message.getContentFormats();
        for (Class<?> class1 : classes) {
            System.err.println(class1.getName());
        }

        OutputStream outputStream = message.getContent(OutputStream.class);

        XMLStreamWriter xmlStreamWriter = message.getContent(XMLStreamWriter.class);

        //        byte[] b = new byte[1024];
        //        ByteArrayInputStream inputStream = null;
        //        try {
        //            outputStream.write(b);
        //            inputStream = new ByteArrayInputStream(b);
        //            ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //            baos.write(b);
        //
        //        } catch (IOException e1) {
        //            // TODO Auto-generated catch block
        //            e1.printStackTrace();
        //        }
        //
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLStreamWriter writer = StaxUtils.createXMLStreamWriter(out);
        InputStream inputStream = null;
        try {
            XMLStreamReader reader = StaxUtils.createXMLStreamReader(inputStream);
            int eventType = reader.getEventType();
            //            if (eventType == XMLStreamConstants.START_DOCUMENT) {
            //                System.err.println("START_DOCUMENT");
            //                writer.writeStartDocument(reader.getEncoding(), reader.getVersion());
            //            }
            String lastPrefix = "";
            String lastNameSpace = "";
            while (reader.hasNext()) {
                eventType = reader.next();
                System.err.println("EVENTTYPE = " + eventType);
                if (eventType == XMLStreamConstants.START_ELEMENT) {
                    System.err.println("START_ELEMENT = " + eventType);
                    System.err.println("Number of namespace = " + reader.getNamespaceCount());
                    if (!reader.getName().getPrefix().isEmpty() &&
                        !reader.getName().getNamespaceURI().isEmpty()) {
                        writer.writeStartElement(reader.getName().getPrefix(), reader.getName()
                                .getLocalPart(), reader.getName().getNamespaceURI());
                        lastPrefix = reader.getName().getPrefix();
                        lastNameSpace = reader.getName().getNamespaceURI();
                    } else {
                        writer.writeStartElement(lastPrefix, reader.getName().getLocalPart(), lastNameSpace);
                        //                        writer.writeStartElement(reader.getName().getLocalPart());
                    }
                    int nbNameSpace = reader.getNamespaceCount();
                    if (nbNameSpace > 0) {
                        for (int j = 0; j < nbNameSpace; j++) {
                            writer.writeNamespace(reader.getNamespacePrefix(j), reader.getNamespaceURI(j));
                        }
                    }
                    int nbAttribute = reader.getAttributeCount();
                    System.err.println("This element has " + nbAttribute + " attributes");
                    for (int i = 0; i < nbAttribute; i++) {
                        System.err.println("Attribute " + i + " = " + reader.getAttributeLocalName(i));
                        writer.writeAttribute(reader.getAttributePrefix(i), reader.getAttributeNamespace(i),
                                reader.getAttributeLocalName(i), reader.getAttributeValue(i));
                    }
                }
                if (eventType == XMLStreamConstants.CHARACTERS) {
                    System.err.println("START_CHARACTERS = " + eventType);
                    System.err.println("element characters = " + reader.getText());
                    writer.writeCharacters(reader.getText());
                }
                if (eventType == XMLStreamConstants.CDATA) {
                    System.err.println("START_CDATA = " + eventType);
                    System.err.println("element CDATA = " + reader.getText());
                    writer.writeCData(reader.getText());
                }
                if (eventType == XMLStreamConstants.ENTITY_REFERENCE) {
                    System.err.println("START_ENTITY_REFERENCE = " + eventType);
                    System.err.println("element entity ref = " + reader.getText());
                    writer.writeEntityRef(reader.getText());
                }
                if (eventType == XMLStreamConstants.END_ELEMENT) {
                    System.err.println("END_ELEMENT = " + eventType);
                    writer.writeEndElement();
                }
            }
            //            eventType = reader.getEventType();
            //            if (eventType == XMLStreamConstants.END_DOCUMENT) {
            //                System.err.println("END_DOCUMENT");
            //                writer.writeEndDocument();
            //            }
            out.close();
            writer.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.err.println(out.toString());
        System.err.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

    }

}
