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
 *         &lt;element name="VolumeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="InstanceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Device" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Force" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
@XmlType(name = "", propOrder = { "volumeId", "instanceId", "device", "force" })
@XmlRootElement(name = "DetachVolumeRequest")
public class DetachVolumeRequest {

    @XmlElement(name = "VolumeId", required = true)
    protected String volumeId;
    @XmlElement(name = "InstanceId")
    protected String instanceId;
    @XmlElement(name = "Device")
    protected String device;
    @XmlElement(name = "Force")
    protected Boolean force;

    /**
     * Default constructor
     * 
     */
    public DetachVolumeRequest() {
        super();
    }

    /**
     * Value constructor
     * 
     */
    public DetachVolumeRequest(final String volumeId, final String instanceId, final String device,
            final Boolean force) {
        this.volumeId = volumeId;
        this.instanceId = instanceId;
        this.device = device;
        this.force = force;
    }

    /**
     * Gets the value of the volumeId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVolumeId() {
        return volumeId;
    }

    /**
     * Sets the value of the volumeId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVolumeId(String value) {
        this.volumeId = value;
    }

    public boolean isSetVolumeId() {
        return (this.volumeId != null);
    }

    /**
     * Gets the value of the instanceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * Sets the value of the instanceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInstanceId(String value) {
        this.instanceId = value;
    }

    public boolean isSetInstanceId() {
        return (this.instanceId != null);
    }

    /**
     * Gets the value of the device property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDevice() {
        return device;
    }

    /**
     * Sets the value of the device property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDevice(String value) {
        this.device = value;
    }

    public boolean isSetDevice() {
        return (this.device != null);
    }

    /**
     * Gets the value of the force property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isForce() {
        return force;
    }

    /**
     * Sets the value of the force property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setForce(Boolean value) {
        this.force = value;
    }

    public boolean isSetForce() {
        return (this.force != null);
    }

    /**
     * Sets the value of the VolumeId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public DetachVolumeRequest withVolumeId(String value) {
        setVolumeId(value);
        return this;
    }

    /**
     * Sets the value of the InstanceId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public DetachVolumeRequest withInstanceId(String value) {
        setInstanceId(value);
        return this;
    }

    /**
     * Sets the value of the Device property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public DetachVolumeRequest withDevice(String value) {
        setDevice(value);
        return this;
    }

    /**
     * Sets the value of the Force property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public DetachVolumeRequest withForce(Boolean value) {
        setForce(value);
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
        if (isSetVolumeId()) {
            if (!first)
                json.append(", ");
            json.append(quoteJSON("VolumeId"));
            json.append(" : ");
            json.append(quoteJSON(getVolumeId()));
            first = false;
        }
        if (isSetInstanceId()) {
            if (!first)
                json.append(", ");
            json.append(quoteJSON("InstanceId"));
            json.append(" : ");
            json.append(quoteJSON(getInstanceId()));
            first = false;
        }
        if (isSetDevice()) {
            if (!first)
                json.append(", ");
            json.append(quoteJSON("Device"));
            json.append(" : ");
            json.append(quoteJSON(getDevice()));
            first = false;
        }
        if (isSetForce()) {
            if (!first)
                json.append(", ");
            json.append(quoteJSON("Force"));
            json.append(" : ");
            json.append(quoteJSON(isForce() + ""));
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
