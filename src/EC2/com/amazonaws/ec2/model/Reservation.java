
package com.amazonaws.ec2.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Reservation complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Reservation">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ReservationId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OwnerId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="GroupName" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="RunningInstance" type="{http://ec2.amazonaws.com/doc/2008-05-05/}RunningInstance" maxOccurs="unbounded"/>
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
@XmlType(name = "Reservation", propOrder = {
    "reservationId",
    "ownerId",
    "groupName",
    "runningInstance"
})
public class Reservation {

    @XmlElement(name = "ReservationId", required = true)
    protected String reservationId;
    @XmlElement(name = "OwnerId", required = true)
    protected String ownerId;
    @XmlElement(name = "GroupName", required = true)
    protected List<String> groupName;
    @XmlElement(name = "RunningInstance", required = true)
    protected List<RunningInstance> runningInstance;

    /**
     * Default constructor
     * 
     */
    public Reservation() {
        super();
    }

    /**
     * Value constructor
     * 
     */
    public Reservation(final String reservationId, final String ownerId, final List<String> groupName, final List<RunningInstance> runningInstance) {
        this.reservationId = reservationId;
        this.ownerId = ownerId;
        this.groupName = groupName;
        this.runningInstance = runningInstance;
    }

    /**
     * Gets the value of the reservationId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReservationId() {
        return reservationId;
    }

    /**
     * Sets the value of the reservationId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReservationId(String value) {
        this.reservationId = value;
    }

    public boolean isSetReservationId() {
        return (this.reservationId!= null);
    }

    /**
     * Gets the value of the ownerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * Sets the value of the ownerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOwnerId(String value) {
        this.ownerId = value;
    }

    public boolean isSetOwnerId() {
        return (this.ownerId!= null);
    }

    /**
     * Gets the value of the groupName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getGroupName() {
        if (groupName == null) {
            groupName = new ArrayList<String>();
        }
        return this.groupName;
    }

    public boolean isSetGroupName() {
        return ((this.groupName!= null)&&(!this.groupName.isEmpty()));
    }

    public void unsetGroupName() {
        this.groupName = null;
    }

    /**
     * Gets the value of the runningInstance property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the runningInstance property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRunningInstance().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RunningInstance }
     * 
     * 
     */
    public List<RunningInstance> getRunningInstance() {
        if (runningInstance == null) {
            runningInstance = new ArrayList<RunningInstance>();
        }
        return this.runningInstance;
    }

    public boolean isSetRunningInstance() {
        return ((this.runningInstance!= null)&&(!this.runningInstance.isEmpty()));
    }

    public void unsetRunningInstance() {
        this.runningInstance = null;
    }

    /**
     * Sets the value of the ReservationId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Reservation withReservationId(String value) {
        setReservationId(value);
        return this;
    }

    /**
     * Sets the value of the OwnerId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Reservation withOwnerId(String value) {
        setOwnerId(value);
        return this;
    }

    /**
     * Sets the value of the GroupName property.
     * 
     * @param values
     * @return
     *     this instance
     */
    public Reservation withGroupName(String... values) {
        for (String value: values) {
            getGroupName().add(value);
        }
        return this;
    }

    /**
     * Sets the value of the RunningInstance property.
     * 
     * @param values
     * @return
     *     this instance
     */
    public Reservation withRunningInstance(RunningInstance... values) {
        for (RunningInstance value: values) {
            getRunningInstance().add(value);
        }
        return this;
    }

    /**
     * Sets the value of the groupName property.
     * 
     * @param groupName
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroupName(List<String> groupName) {
        this.groupName = groupName;
    }

    /**
     * Sets the value of the runningInstance property.
     * 
     * @param runningInstance
     *     allowed object is
     *     {@link RunningInstance }
     *     
     */
    public void setRunningInstance(List<RunningInstance> runningInstance) {
        this.runningInstance = runningInstance;
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
        if (isSetReservationId()) {
            xml.append("<ReservationId>");
            xml.append(escapeXML(getReservationId()));
            xml.append("</ReservationId>");
        }
        if (isSetOwnerId()) {
            xml.append("<OwnerId>");
            xml.append(escapeXML(getOwnerId()));
            xml.append("</OwnerId>");
        }
        java.util.List<String> groupNameList  =  getGroupName();
        for (String groupName : groupNameList) { 
            xml.append("<GroupName>");
            xml.append(escapeXML(groupName));
            xml.append("</GroupName>");
        }	
        java.util.List<RunningInstance> runningInstanceList = getRunningInstance();
        for (RunningInstance runningInstance : runningInstanceList) {
            xml.append("<RunningInstance>");
            xml.append(runningInstance.toXMLFragment());
            xml.append("</RunningInstance>");
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
        if (isSetReservationId()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("ReservationId"));
            json.append(" : ");
            json.append(quoteJSON(getReservationId()));
            first = false;
        }
        if (isSetOwnerId()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("OwnerId"));
            json.append(" : ");
            json.append(quoteJSON(getOwnerId()));
            first = false;
        }
        if (isSetGroupName()) {
            if (!first) json.append(", ");
            json.append("\"GroupName\" : [");
            java.util.List<String> groupNameList  =  getGroupName();
            for (String groupName : groupNameList) {
                if (groupNameList.indexOf(groupName) > 0) json.append(", ");
                    json.append(quoteJSON(groupName));
            }
            json.append("]");
            first = false;
        }
        if (isSetRunningInstance()) {
            if (!first) json.append(", ");
            json.append("\"RunningInstance\" : [");
            java.util.List<RunningInstance> runningInstanceList = getRunningInstance();
            for (RunningInstance runningInstance : runningInstanceList) {
                if (runningInstanceList.indexOf(runningInstance) > 0) json.append(", ");
                json.append("{");
                json.append("");
                json.append(runningInstance.toJSONFragment());
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