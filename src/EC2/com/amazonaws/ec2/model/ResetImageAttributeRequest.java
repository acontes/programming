
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
 *         &lt;element name="ImageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Attribute" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "imageId",
    "attribute"
})
@XmlRootElement(name = "ResetImageAttributeRequest")
public class ResetImageAttributeRequest {

    @XmlElement(name = "ImageId", required = true)
    protected String imageId;
    @XmlElement(name = "Attribute", required = true)
    protected String attribute;

    /**
     * Default constructor
     * 
     */
    public ResetImageAttributeRequest() {
        super();
    }

    /**
     * Value constructor
     * 
     */
    public ResetImageAttributeRequest(final String imageId, final String attribute) {
        this.imageId = imageId;
        this.attribute = attribute;
    }

    /**
     * Gets the value of the imageId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageId() {
        return imageId;
    }

    /**
     * Sets the value of the imageId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageId(String value) {
        this.imageId = value;
    }

    public boolean isSetImageId() {
        return (this.imageId!= null);
    }

    /**
     * Gets the value of the attribute property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * Sets the value of the attribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttribute(String value) {
        this.attribute = value;
    }

    public boolean isSetAttribute() {
        return (this.attribute!= null);
    }

    /**
     * Sets the value of the ImageId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public ResetImageAttributeRequest withImageId(String value) {
        setImageId(value);
        return this;
    }

    /**
     * Sets the value of the Attribute property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public ResetImageAttributeRequest withAttribute(String value) {
        setAttribute(value);
        return this;
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
        if (isSetImageId()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("ImageId"));
            json.append(" : ");
            json.append(quoteJSON(getImageId()));
            first = false;
        }
        if (isSetAttribute()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("Attribute"));
            json.append(" : ");
            json.append(quoteJSON(getAttribute()));
            first = false;
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