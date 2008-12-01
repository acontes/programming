package com.amazonaws.ec2.model;

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
 *         &lt;element ref="{http://ec2.amazonaws.com/doc/2008-05-05/}ResponseMetadata"/>
 *         &lt;element ref="{http://ec2.amazonaws.com/doc/2008-05-05/}RegisterImageResult"/>
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
@XmlType(name = "", propOrder = { "responseMetadata", "registerImageResult" })
@XmlRootElement(name = "RegisterImageResponse")
public class RegisterImageResponse {

    @XmlElement(name = "ResponseMetadata", required = true)
    protected ResponseMetadata responseMetadata;
    @XmlElement(name = "RegisterImageResult", required = true)
    protected RegisterImageResult registerImageResult;

    /**
     * Default constructor
     * 
     */
    public RegisterImageResponse() {
        super();
    }

    /**
     * Value constructor
     * 
     */
    public RegisterImageResponse(final ResponseMetadata responseMetadata,
            final RegisterImageResult registerImageResult) {
        this.responseMetadata = responseMetadata;
        this.registerImageResult = registerImageResult;
    }

    /**
     * Gets the value of the responseMetadata property.
     * 
     * @return
     *     possible object is
     *     {@link ResponseMetadata }
     *     
     */
    public ResponseMetadata getResponseMetadata() {
        return responseMetadata;
    }

    /**
     * Sets the value of the responseMetadata property.
     * 
     * @param value
     *     allowed object is
     *     {@link ResponseMetadata }
     *     
     */
    public void setResponseMetadata(ResponseMetadata value) {
        this.responseMetadata = value;
    }

    public boolean isSetResponseMetadata() {
        return (this.responseMetadata != null);
    }

    /**
     * Gets the value of the registerImageResult property.
     * 
     * @return
     *     possible object is
     *     {@link RegisterImageResult }
     *     
     */
    public RegisterImageResult getRegisterImageResult() {
        return registerImageResult;
    }

    /**
     * Sets the value of the registerImageResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link RegisterImageResult }
     *     
     */
    public void setRegisterImageResult(RegisterImageResult value) {
        this.registerImageResult = value;
    }

    public boolean isSetRegisterImageResult() {
        return (this.registerImageResult != null);
    }

    /**
     * Sets the value of the ResponseMetadata property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public RegisterImageResponse withResponseMetadata(ResponseMetadata value) {
        setResponseMetadata(value);
        return this;
    }

    /**
     * Sets the value of the RegisterImageResult property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public RegisterImageResponse withRegisterImageResult(RegisterImageResult value) {
        setRegisterImageResult(value);
        return this;
    }

    /**
     * 
     * XML string representation of this object
     * 
     * @return XML String
     */
    public String toXML() {
        StringBuffer xml = new StringBuffer();
        xml.append("<RegisterImageResponse xmlns=\"http://ec2.amazonaws.com/doc/2008-05-05/\">");
        if (isSetResponseMetadata()) {
            ResponseMetadata responseMetadata = getResponseMetadata();
            xml.append("<ResponseMetadata>");
            xml.append(responseMetadata.toXMLFragment());
            xml.append("</ResponseMetadata>");
        }
        if (isSetRegisterImageResult()) {
            RegisterImageResult registerImageResult = getRegisterImageResult();
            xml.append("<RegisterImageResult>");
            xml.append(registerImageResult.toXMLFragment());
            xml.append("</RegisterImageResult>");
        }
        xml.append("</RegisterImageResponse>");
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
     * JSON string representation of this object
     * 
     * @return JSON String
     */
    public String toJSON() {
        StringBuffer json = new StringBuffer();
        json.append("{\"RegisterImageResponse\" : {");
        json.append(quoteJSON("@xmlns"));
        json.append(" : ");
        json.append(quoteJSON("http://ec2.amazonaws.com/doc/2008-05-05/"));
        boolean first = true;
        json.append(", ");
        if (isSetResponseMetadata()) {
            if (!first)
                json.append(", ");
            json.append("\"ResponseMetadata\" : {");
            ResponseMetadata responseMetadata = getResponseMetadata();

            json.append(responseMetadata.toJSONFragment());
            json.append("}");
            first = false;
        }
        if (isSetRegisterImageResult()) {
            if (!first)
                json.append(", ");
            json.append("\"RegisterImageResult\" : {");
            RegisterImageResult registerImageResult = getRegisterImageResult();

            json.append(registerImageResult.toJSONFragment());
            json.append("}");
            first = false;
        }
        json.append("}");
        json.append("}");
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
                    if (c < ' ') {
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
