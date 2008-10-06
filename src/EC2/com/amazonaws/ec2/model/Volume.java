
package com.amazonaws.ec2.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Volume complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Volume">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VolumeId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Size" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SnapshotId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Zone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Status" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CreateTime" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Attachment" type="{http://ec2.amazonaws.com/doc/2008-05-05/}Attachment" maxOccurs="unbounded"/>
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
@XmlType(name = "Volume", propOrder = {
    "volumeId",
    "size",
    "snapshotId",
    "zone",
    "status",
    "createTime",
    "attachment"
})
public class Volume {

    @XmlElement(name = "VolumeId", required = true)
    protected String volumeId;
    @XmlElement(name = "Size", required = true)
    protected String size;
    @XmlElement(name = "SnapshotId", required = true)
    protected String snapshotId;
    @XmlElement(name = "Zone", required = true)
    protected String zone;
    @XmlElement(name = "Status", required = true)
    protected String status;
    @XmlElement(name = "CreateTime", required = true)
    protected String createTime;
    @XmlElement(name = "Attachment", required = true)
    protected List<Attachment> attachment;

    /**
     * Default constructor
     * 
     */
    public Volume() {
        super();
    }

    /**
     * Value constructor
     * 
     */
    public Volume(final String volumeId, final String size, final String snapshotId, final String zone, final String status, final String createTime, final List<Attachment> attachment) {
        this.volumeId = volumeId;
        this.size = size;
        this.snapshotId = snapshotId;
        this.zone = zone;
        this.status = status;
        this.createTime = createTime;
        this.attachment = attachment;
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
        return (this.volumeId!= null);
    }

    /**
     * Gets the value of the size property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSize() {
        return size;
    }

    /**
     * Sets the value of the size property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSize(String value) {
        this.size = value;
    }

    public boolean isSetSize() {
        return (this.size!= null);
    }

    /**
     * Gets the value of the snapshotId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnapshotId() {
        return snapshotId;
    }

    /**
     * Sets the value of the snapshotId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnapshotId(String value) {
        this.snapshotId = value;
    }

    public boolean isSetSnapshotId() {
        return (this.snapshotId!= null);
    }

    /**
     * Gets the value of the zone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getZone() {
        return zone;
    }

    /**
     * Sets the value of the zone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setZone(String value) {
        this.zone = value;
    }

    public boolean isSetZone() {
        return (this.zone!= null);
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

    public boolean isSetStatus() {
        return (this.status!= null);
    }

    /**
     * Gets the value of the createTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * Sets the value of the createTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCreateTime(String value) {
        this.createTime = value;
    }

    public boolean isSetCreateTime() {
        return (this.createTime!= null);
    }

    /**
     * Gets the value of the attachment property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attachment property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttachment().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attachment }
     * 
     * 
     */
    public List<Attachment> getAttachment() {
        if (attachment == null) {
            attachment = new ArrayList<Attachment>();
        }
        return this.attachment;
    }

    public boolean isSetAttachment() {
        return ((this.attachment!= null)&&(!this.attachment.isEmpty()));
    }

    public void unsetAttachment() {
        this.attachment = null;
    }

    /**
     * Sets the value of the VolumeId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Volume withVolumeId(String value) {
        setVolumeId(value);
        return this;
    }

    /**
     * Sets the value of the Size property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Volume withSize(String value) {
        setSize(value);
        return this;
    }

    /**
     * Sets the value of the SnapshotId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Volume withSnapshotId(String value) {
        setSnapshotId(value);
        return this;
    }

    /**
     * Sets the value of the Zone property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Volume withZone(String value) {
        setZone(value);
        return this;
    }

    /**
     * Sets the value of the Status property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Volume withStatus(String value) {
        setStatus(value);
        return this;
    }

    /**
     * Sets the value of the CreateTime property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Volume withCreateTime(String value) {
        setCreateTime(value);
        return this;
    }

    /**
     * Sets the value of the Attachment property.
     * 
     * @param values
     * @return
     *     this instance
     */
    public Volume withAttachment(Attachment... values) {
        for (Attachment value: values) {
            getAttachment().add(value);
        }
        return this;
    }

    /**
     * Sets the value of the attachment property.
     * 
     * @param attachment
     *     allowed object is
     *     {@link Attachment }
     *     
     */
    public void setAttachment(List<Attachment> attachment) {
        this.attachment = attachment;
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
        if (isSetVolumeId()) {
            xml.append("<VolumeId>");
            xml.append(escapeXML(getVolumeId()));
            xml.append("</VolumeId>");
        }
        if (isSetSize()) {
            xml.append("<Size>");
            xml.append(escapeXML(getSize()));
            xml.append("</Size>");
        }
        if (isSetSnapshotId()) {
            xml.append("<SnapshotId>");
            xml.append(escapeXML(getSnapshotId()));
            xml.append("</SnapshotId>");
        }
        if (isSetZone()) {
            xml.append("<Zone>");
            xml.append(escapeXML(getZone()));
            xml.append("</Zone>");
        }
        if (isSetStatus()) {
            xml.append("<Status>");
            xml.append(escapeXML(getStatus()));
            xml.append("</Status>");
        }
        if (isSetCreateTime()) {
            xml.append("<CreateTime>");
            xml.append(escapeXML(getCreateTime()));
            xml.append("</CreateTime>");
        }
        java.util.List<Attachment> attachmentList = getAttachment();
        for (Attachment attachment : attachmentList) {
            xml.append("<Attachment>");
            xml.append(attachment.toXMLFragment());
            xml.append("</Attachment>");
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
        if (isSetVolumeId()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("VolumeId"));
            json.append(" : ");
            json.append(quoteJSON(getVolumeId()));
            first = false;
        }
        if (isSetSize()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("Size"));
            json.append(" : ");
            json.append(quoteJSON(getSize()));
            first = false;
        }
        if (isSetSnapshotId()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("SnapshotId"));
            json.append(" : ");
            json.append(quoteJSON(getSnapshotId()));
            first = false;
        }
        if (isSetZone()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("Zone"));
            json.append(" : ");
            json.append(quoteJSON(getZone()));
            first = false;
        }
        if (isSetStatus()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("Status"));
            json.append(" : ");
            json.append(quoteJSON(getStatus()));
            first = false;
        }
        if (isSetCreateTime()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("CreateTime"));
            json.append(" : ");
            json.append(quoteJSON(getCreateTime()));
            first = false;
        }
        if (isSetAttachment()) {
            if (!first) json.append(", ");
            json.append("\"Attachment\" : [");
            java.util.List<Attachment> attachmentList = getAttachment();
            for (Attachment attachment : attachmentList) {
                if (attachmentList.indexOf(attachment) > 0) json.append(", ");
                json.append("{");
                json.append("");
                json.append(attachment.toJSONFragment());
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
