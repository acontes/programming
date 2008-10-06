
package com.amazonaws.ec2.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TerminatingInstance" type="{http://ec2.amazonaws.com/doc/2008-05-05/}TerminatingInstance" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * Generated by AWS Code Generator
 * <p/>
 * Thu Aug 28 20:50:29 PDT 2008
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "terminatingInstance"
})
@XmlRootElement(name = "TerminateInstancesResult")
public class TerminateInstancesResult {

    @XmlElement(name = "TerminatingInstance", required = true)
    protected List<TerminatingInstance> terminatingInstance;

    /**
     * Default constructor
     * 
     */
    public TerminateInstancesResult() {
        super();
    }

    /**
     * Value constructor
     * 
     */
    public TerminateInstancesResult(final List<TerminatingInstance> terminatingInstance) {
        this.terminatingInstance = terminatingInstance;
    }

    /**
     * Gets the value of the terminatingInstance property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the terminatingInstance property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTerminatingInstance().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TerminatingInstance }
     * 
     * 
     */
    public List<TerminatingInstance> getTerminatingInstance() {
        if (terminatingInstance == null) {
            terminatingInstance = new ArrayList<TerminatingInstance>();
        }
        return this.terminatingInstance;
    }

    public boolean isSetTerminatingInstance() {
        return ((this.terminatingInstance!= null)&&(!this.terminatingInstance.isEmpty()));
    }

    public void unsetTerminatingInstance() {
        this.terminatingInstance = null;
    }

    /**
     * Sets the value of the TerminatingInstance property.
     * 
     * @param values
     * @return
     *     this instance
     */
    public TerminateInstancesResult withTerminatingInstance(TerminatingInstance... values) {
        for (TerminatingInstance value: values) {
            getTerminatingInstance().add(value);
        }
        return this;
    }

    /**
     * Sets the value of the terminatingInstance property.
     * 
     * @param terminatingInstance
     *     allowed object is
     *     {@link TerminatingInstance }
     *     
     */
    public void setTerminatingInstance(List<TerminatingInstance> terminatingInstance) {
        this.terminatingInstance = terminatingInstance;
    }
    

    /**
     * 
     * XML fragment representation of this object
     * 
     * @return XML fragment for this object. Name for outer
     * tag expected to be set by calling method. This fragment
     * returns inner properties representation only
     */
    protected String toXMLFragment() {
        StringBuffer xml = new StringBuffer();
        java.util.List<TerminatingInstance> terminatingInstanceList = getTerminatingInstance();
        for (TerminatingInstance terminatingInstance : terminatingInstanceList) {
            xml.append("<TerminatingInstance>");
            xml.append(terminatingInstance.toXMLFragment());
            xml.append("</TerminatingInstance>");
        }
        return xml.toString();
    }

    /**
     * 
     * Escape XML special characters
     */
    private String escapeXML(String string) {
        StringBuffer sb = new StringBuffer();
        int length = string.length();
        for (int i = 0; i < length; ++i) {
            char c = string.charAt(i);
            switch (c) {
            case '&':
                sb.append("&amp;");
                break;
            case '<':
                sb.append("&lt;");
                break;
            case '>':
                sb.append("&gt;");
                break;
            case '\'':
                sb.append("&#039;");
                break;
            case '"':
                sb.append("&quot;");
                break;
            default:
                sb.append(c);
            }
        }
        return sb.toString();
    }



    /**
     *
     * JSON fragment representation of this object
     *
     * @return JSON fragment for this object. Name for outer
     * object expected to be set by calling method. This fragment
     * returns inner properties representation only
     *
     */
    protected String toJSONFragment() {
        StringBuffer json = new StringBuffer();
        boolean first = true;
        if (isSetTerminatingInstance()) {
            if (!first) json.append(", ");
            json.append("\"TerminatingInstance\" : [");
            java.util.List<TerminatingInstance> terminatingInstanceList = getTerminatingInstance();
            for (TerminatingInstance terminatingInstance : terminatingInstanceList) {
                if (terminatingInstanceList.indexOf(terminatingInstance) > 0) json.append(", ");
                json.append("{");
                json.append("");
                json.append(terminatingInstance.toJSONFragment());
                json.append("}");
                first = false;
            }
            json.append("]");
        }
        return json.toString();
    }

    /**
     *
     * Quote JSON string
     */
    private String quoteJSON(String string) {
        StringBuffer sb = new StringBuffer();
        sb.append("\"");
        int length = string.length();
        for (int i = 0; i < length; ++i) {
            char c = string.charAt(i);
            switch (c) {
            case '"':
                sb.append("\\\"");
                break;
            case '\\':
                sb.append("\\\\");
                break;
            case '/':
                sb.append("\\/");
                break;
            case '\b':
                sb.append("\\b");
                break;
            case '\f':
                sb.append("\\f");
                break;
            case '\n':
                sb.append("\\n");
                break;
            case '\r':
                sb.append("\\r");
                break;
            case '\t':
                sb.append("\\t");
                break;
            default:
                if (c <  ' ') {
                    sb.append("\\u" + String.format("%03x", Integer.valueOf(c)));
                } else {
                sb.append(c);
            }
        }
        }
        sb.append("\"");
        return sb.toString();
    }


}
