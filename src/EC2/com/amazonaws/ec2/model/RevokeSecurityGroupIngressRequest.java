
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
 *         &lt;element name="GroupName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="SourceSecurityGroupName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SourceSecurityGroupOwnerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IpProtocol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FromPort" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="ToPort" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="CidrIp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "groupName",
    "sourceSecurityGroupName",
    "sourceSecurityGroupOwnerId",
    "ipProtocol",
    "fromPort",
    "toPort",
    "cidrIp"
})
@XmlRootElement(name = "RevokeSecurityGroupIngressRequest")
public class RevokeSecurityGroupIngressRequest {

    @XmlElement(name = "GroupName", required = true)
    protected String groupName;
    @XmlElement(name = "SourceSecurityGroupName")
    protected String sourceSecurityGroupName;
    @XmlElement(name = "SourceSecurityGroupOwnerId")
    protected String sourceSecurityGroupOwnerId;
    @XmlElement(name = "IpProtocol")
    protected String ipProtocol;
    @XmlElement(name = "FromPort")
    protected Integer fromPort;
    @XmlElement(name = "ToPort")
    protected Integer toPort;
    @XmlElement(name = "CidrIp")
    protected String cidrIp;

    /**
     * Default constructor
     * 
     */
    public RevokeSecurityGroupIngressRequest() {
        super();
    }

    /**
     * Value constructor
     * 
     */
    public RevokeSecurityGroupIngressRequest(final String groupName, final String sourceSecurityGroupName, final String sourceSecurityGroupOwnerId, final String ipProtocol, final Integer fromPort, final Integer toPort, final String cidrIp) {
        this.groupName = groupName;
        this.sourceSecurityGroupName = sourceSecurityGroupName;
        this.sourceSecurityGroupOwnerId = sourceSecurityGroupOwnerId;
        this.ipProtocol = ipProtocol;
        this.fromPort = fromPort;
        this.toPort = toPort;
        this.cidrIp = cidrIp;
    }

    /**
     * Gets the value of the groupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Sets the value of the groupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupName(String value) {
        this.groupName = value;
    }

    public boolean isSetGroupName() {
        return (this.groupName!= null);
    }

    /**
     * Gets the value of the sourceSecurityGroupName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceSecurityGroupName() {
        return sourceSecurityGroupName;
    }

    /**
     * Sets the value of the sourceSecurityGroupName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceSecurityGroupName(String value) {
        this.sourceSecurityGroupName = value;
    }

    public boolean isSetSourceSecurityGroupName() {
        return (this.sourceSecurityGroupName!= null);
    }

    /**
     * Gets the value of the sourceSecurityGroupOwnerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceSecurityGroupOwnerId() {
        return sourceSecurityGroupOwnerId;
    }

    /**
     * Sets the value of the sourceSecurityGroupOwnerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceSecurityGroupOwnerId(String value) {
        this.sourceSecurityGroupOwnerId = value;
    }

    public boolean isSetSourceSecurityGroupOwnerId() {
        return (this.sourceSecurityGroupOwnerId!= null);
    }

    /**
     * Gets the value of the ipProtocol property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIpProtocol() {
        return ipProtocol;
    }

    /**
     * Sets the value of the ipProtocol property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIpProtocol(String value) {
        this.ipProtocol = value;
    }

    public boolean isSetIpProtocol() {
        return (this.ipProtocol!= null);
    }

    /**
     * Gets the value of the fromPort property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getFromPort() {
        return fromPort;
    }

    /**
     * Sets the value of the fromPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setFromPort(Integer value) {
        this.fromPort = value;
    }

    public boolean isSetFromPort() {
        return (this.fromPort!= null);
    }

    /**
     * Gets the value of the toPort property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getToPort() {
        return toPort;
    }

    /**
     * Sets the value of the toPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setToPort(Integer value) {
        this.toPort = value;
    }

    public boolean isSetToPort() {
        return (this.toPort!= null);
    }

    /**
     * Gets the value of the cidrIp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCidrIp() {
        return cidrIp;
    }

    /**
     * Sets the value of the cidrIp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCidrIp(String value) {
        this.cidrIp = value;
    }

    public boolean isSetCidrIp() {
        return (this.cidrIp!= null);
    }

    /**
     * Sets the value of the GroupName property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public RevokeSecurityGroupIngressRequest withGroupName(String value) {
        setGroupName(value);
        return this;
    }

    /**
     * Sets the value of the SourceSecurityGroupName property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public RevokeSecurityGroupIngressRequest withSourceSecurityGroupName(String value) {
        setSourceSecurityGroupName(value);
        return this;
    }

    /**
     * Sets the value of the SourceSecurityGroupOwnerId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public RevokeSecurityGroupIngressRequest withSourceSecurityGroupOwnerId(String value) {
        setSourceSecurityGroupOwnerId(value);
        return this;
    }

    /**
     * Sets the value of the IpProtocol property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public RevokeSecurityGroupIngressRequest withIpProtocol(String value) {
        setIpProtocol(value);
        return this;
    }

    /**
     * Sets the value of the FromPort property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public RevokeSecurityGroupIngressRequest withFromPort(Integer value) {
        setFromPort(value);
        return this;
    }

    /**
     * Sets the value of the ToPort property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public RevokeSecurityGroupIngressRequest withToPort(Integer value) {
        setToPort(value);
        return this;
    }

    /**
     * Sets the value of the CidrIp property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public RevokeSecurityGroupIngressRequest withCidrIp(String value) {
        setCidrIp(value);
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
        if (isSetGroupName()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("GroupName"));
            json.append(" : ");
            json.append(quoteJSON(getGroupName()));
            first = false;
        }
        if (isSetSourceSecurityGroupName()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("SourceSecurityGroupName"));
            json.append(" : ");
            json.append(quoteJSON(getSourceSecurityGroupName()));
            first = false;
        }
        if (isSetSourceSecurityGroupOwnerId()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("SourceSecurityGroupOwnerId"));
            json.append(" : ");
            json.append(quoteJSON(getSourceSecurityGroupOwnerId()));
            first = false;
        }
        if (isSetIpProtocol()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("IpProtocol"));
            json.append(" : ");
            json.append(quoteJSON(getIpProtocol()));
            first = false;
        }
        if (isSetFromPort()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("FromPort"));
            json.append(" : ");
            json.append(quoteJSON(getFromPort() + ""));
            first = false;
        }
        if (isSetToPort()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("ToPort"));
            json.append(" : ");
            json.append(quoteJSON(getToPort() + ""));
            first = false;
        }
        if (isSetCidrIp()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("CidrIp"));
            json.append(" : ");
            json.append(quoteJSON(getCidrIp()));
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
