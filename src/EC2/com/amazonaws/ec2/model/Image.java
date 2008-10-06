
package com.amazonaws.ec2.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Image complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Image">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ImageId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ImageLocation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ImageState" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="OwnerId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Visibility" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProductCode" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Architecture" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ImageType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="KernelId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="RamdiskId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
@XmlType(name = "Image", propOrder = {
    "imageId",
    "imageLocation",
    "imageState",
    "ownerId",
    "visibility",
    "productCode",
    "architecture",
    "imageType",
    "kernelId",
    "ramdiskId"
})
public class Image {

    @XmlElement(name = "ImageId", required = true)
    protected String imageId;
    @XmlElement(name = "ImageLocation", required = true)
    protected String imageLocation;
    @XmlElement(name = "ImageState", required = true)
    protected String imageState;
    @XmlElement(name = "OwnerId", required = true)
    protected String ownerId;
    @XmlElement(name = "Visibility", required = true)
    protected String visibility;
    @XmlElement(name = "ProductCode")
    protected List<String> productCode;
    @XmlElement(name = "Architecture")
    protected String architecture;
    @XmlElement(name = "ImageType")
    protected String imageType;
    @XmlElement(name = "KernelId")
    protected String kernelId;
    @XmlElement(name = "RamdiskId")
    protected String ramdiskId;

    /**
     * Default constructor
     * 
     */
    public Image() {
        super();
    }

    /**
     * Value constructor
     * 
     */
    public Image(final String imageId, final String imageLocation, final String imageState, final String ownerId, final String visibility, final List<String> productCode, final String architecture, final String imageType, final String kernelId, final String ramdiskId) {
        this.imageId = imageId;
        this.imageLocation = imageLocation;
        this.imageState = imageState;
        this.ownerId = ownerId;
        this.visibility = visibility;
        this.productCode = productCode;
        this.architecture = architecture;
        this.imageType = imageType;
        this.kernelId = kernelId;
        this.ramdiskId = ramdiskId;
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
     * Gets the value of the imageLocation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageLocation() {
        return imageLocation;
    }

    /**
     * Sets the value of the imageLocation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageLocation(String value) {
        this.imageLocation = value;
    }

    public boolean isSetImageLocation() {
        return (this.imageLocation!= null);
    }

    /**
     * Gets the value of the imageState property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageState() {
        return imageState;
    }

    /**
     * Sets the value of the imageState property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageState(String value) {
        this.imageState = value;
    }

    public boolean isSetImageState() {
        return (this.imageState!= null);
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
     * Gets the value of the visibility property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVisibility() {
        return visibility;
    }

    /**
     * Sets the value of the visibility property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVisibility(String value) {
        this.visibility = value;
    }

    public boolean isSetVisibility() {
        return (this.visibility!= null);
    }

    /**
     * Gets the value of the productCode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the productCode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getProductCode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getProductCode() {
        if (productCode == null) {
            productCode = new ArrayList<String>();
        }
        return this.productCode;
    }

    public boolean isSetProductCode() {
        return ((this.productCode!= null)&&(!this.productCode.isEmpty()));
    }

    public void unsetProductCode() {
        this.productCode = null;
    }

    /**
     * Gets the value of the architecture property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArchitecture() {
        return architecture;
    }

    /**
     * Sets the value of the architecture property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArchitecture(String value) {
        this.architecture = value;
    }

    public boolean isSetArchitecture() {
        return (this.architecture!= null);
    }

    /**
     * Gets the value of the imageType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImageType() {
        return imageType;
    }

    /**
     * Sets the value of the imageType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImageType(String value) {
        this.imageType = value;
    }

    public boolean isSetImageType() {
        return (this.imageType!= null);
    }

    /**
     * Gets the value of the kernelId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKernelId() {
        return kernelId;
    }

    /**
     * Sets the value of the kernelId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKernelId(String value) {
        this.kernelId = value;
    }

    public boolean isSetKernelId() {
        return (this.kernelId!= null);
    }

    /**
     * Gets the value of the ramdiskId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRamdiskId() {
        return ramdiskId;
    }

    /**
     * Sets the value of the ramdiskId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRamdiskId(String value) {
        this.ramdiskId = value;
    }

    public boolean isSetRamdiskId() {
        return (this.ramdiskId!= null);
    }

    /**
     * Sets the value of the ImageId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Image withImageId(String value) {
        setImageId(value);
        return this;
    }

    /**
     * Sets the value of the ImageLocation property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Image withImageLocation(String value) {
        setImageLocation(value);
        return this;
    }

    /**
     * Sets the value of the ImageState property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Image withImageState(String value) {
        setImageState(value);
        return this;
    }

    /**
     * Sets the value of the OwnerId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Image withOwnerId(String value) {
        setOwnerId(value);
        return this;
    }

    /**
     * Sets the value of the Visibility property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Image withVisibility(String value) {
        setVisibility(value);
        return this;
    }

    /**
     * Sets the value of the ProductCode property.
     * 
     * @param values
     * @return
     *     this instance
     */
    public Image withProductCode(String... values) {
        for (String value: values) {
            getProductCode().add(value);
        }
        return this;
    }

    /**
     * Sets the value of the Architecture property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Image withArchitecture(String value) {
        setArchitecture(value);
        return this;
    }

    /**
     * Sets the value of the ImageType property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Image withImageType(String value) {
        setImageType(value);
        return this;
    }

    /**
     * Sets the value of the KernelId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Image withKernelId(String value) {
        setKernelId(value);
        return this;
    }

    /**
     * Sets the value of the RamdiskId property.
     * 
     * @param value
     * @return
     *     this instance
     */
    public Image withRamdiskId(String value) {
        setRamdiskId(value);
        return this;
    }

    /**
     * Sets the value of the productCode property.
     * 
     * @param productCode
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductCode(List<String> productCode) {
        this.productCode = productCode;
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
        if (isSetImageId()) {
            xml.append("<ImageId>");
            xml.append(escapeXML(getImageId()));
            xml.append("</ImageId>");
        }
        if (isSetImageLocation()) {
            xml.append("<ImageLocation>");
            xml.append(escapeXML(getImageLocation()));
            xml.append("</ImageLocation>");
        }
        if (isSetImageState()) {
            xml.append("<ImageState>");
            xml.append(escapeXML(getImageState()));
            xml.append("</ImageState>");
        }
        if (isSetOwnerId()) {
            xml.append("<OwnerId>");
            xml.append(escapeXML(getOwnerId()));
            xml.append("</OwnerId>");
        }
        if (isSetVisibility()) {
            xml.append("<Visibility>");
            xml.append(escapeXML(getVisibility()));
            xml.append("</Visibility>");
        }
        java.util.List<String> productCodeList  =  getProductCode();
        for (String productCode : productCodeList) { 
            xml.append("<ProductCode>");
            xml.append(escapeXML(productCode));
            xml.append("</ProductCode>");
        }	
        if (isSetArchitecture()) {
            xml.append("<Architecture>");
            xml.append(escapeXML(getArchitecture()));
            xml.append("</Architecture>");
        }
        if (isSetImageType()) {
            xml.append("<ImageType>");
            xml.append(escapeXML(getImageType()));
            xml.append("</ImageType>");
        }
        if (isSetKernelId()) {
            xml.append("<KernelId>");
            xml.append(escapeXML(getKernelId()));
            xml.append("</KernelId>");
        }
        if (isSetRamdiskId()) {
            xml.append("<RamdiskId>");
            xml.append(escapeXML(getRamdiskId()));
            xml.append("</RamdiskId>");
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
        if (isSetImageId()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("ImageId"));
            json.append(" : ");
            json.append(quoteJSON(getImageId()));
            first = false;
        }
        if (isSetImageLocation()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("ImageLocation"));
            json.append(" : ");
            json.append(quoteJSON(getImageLocation()));
            first = false;
        }
        if (isSetImageState()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("ImageState"));
            json.append(" : ");
            json.append(quoteJSON(getImageState()));
            first = false;
        }
        if (isSetOwnerId()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("OwnerId"));
            json.append(" : ");
            json.append(quoteJSON(getOwnerId()));
            first = false;
        }
        if (isSetVisibility()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("Visibility"));
            json.append(" : ");
            json.append(quoteJSON(getVisibility()));
            first = false;
        }
        if (isSetProductCode()) {
            if (!first) json.append(", ");
            json.append("\"ProductCode\" : [");
            java.util.List<String> productCodeList  =  getProductCode();
            for (String productCode : productCodeList) {
                if (productCodeList.indexOf(productCode) > 0) json.append(", ");
                    json.append(quoteJSON(productCode));
            }
            json.append("]");
            first = false;
        }
        if (isSetArchitecture()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("Architecture"));
            json.append(" : ");
            json.append(quoteJSON(getArchitecture()));
            first = false;
        }
        if (isSetImageType()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("ImageType"));
            json.append(" : ");
            json.append(quoteJSON(getImageType()));
            first = false;
        }
        if (isSetKernelId()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("KernelId"));
            json.append(" : ");
            json.append(quoteJSON(getKernelId()));
            first = false;
        }
        if (isSetRamdiskId()) {
            if (!first) json.append(", ");
            json.append(quoteJSON("RamdiskId"));
            json.append(" : ");
            json.append(quoteJSON(getRamdiskId()));
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
