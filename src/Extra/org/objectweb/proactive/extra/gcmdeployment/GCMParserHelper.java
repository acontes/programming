package org.objectweb.proactive.extra.gcmdeployment;

import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Node;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class GCMParserHelper {

    static public String getAttributeValue(Node node, String attributeName) {
        Node namedItem = node.getAttributes().getNamedItem(attributeName);
        return (namedItem != null) ? namedItem.getNodeValue() : null;
    }


    static public class MyDefaultHandler extends DefaultHandler {
        private String errMessage = "";

        /*
         * With a handler class, just override the methods you need to use
         */

        // Start Error Handler code here
        public void warning(SAXParseException e) {
            System.err.println("Warning Line " + e.getLineNumber() + ": "
                    + e.getMessage() + "\n");
        }

        public void error(SAXParseException e) {
            errMessage = new String("Error Line " + e.getLineNumber() + ": "
                    + e.getMessage() + "\n");
            System.err.println(errMessage);
        }

        public void fatalError(SAXParseException e) {
            errMessage = new String("Error Line " + e.getLineNumber() + ": "
                    + e.getMessage() + "\n");
            System.err.println(errMessage);
        }
    }

    static public class ProActiveNamespaceContext implements NamespaceContext {
        
        protected String namespace;
        
        public ProActiveNamespaceContext(String namespace) {
            this.namespace = namespace;
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new NullPointerException("Null prefix");
            } else if ("pa".equals(prefix)) {
                return namespace;
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
