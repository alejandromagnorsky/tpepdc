//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.11 at 10:58:37 PM ART 
//


package dao.settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for XMLUser complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XMLUser">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="erase-settings" type="{}XMLEraseSettings" minOccurs="0"/>
 *         &lt;element name="transform-settings" type="{}XMLTransformation" minOccurs="0"/>
 *         &lt;element name="schedule" type="{}XMLScheduleRestriction" minOccurs="0"/>
 *         &lt;element name="server" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="max-logins" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XMLUser", propOrder = {
    "eraseSettings",
    "transformSettings",
    "schedule",
    "server"
})
public class XMLUser {

    @XmlElement(name = "erase-settings")
    protected XMLEraseSettings eraseSettings;
    @XmlElement(name = "transform-settings")
    protected XMLTransformation transformSettings;
    protected XMLScheduleRestriction schedule;
    protected String server;
    @XmlAttribute
    protected String name;
    @XmlAttribute(name = "max-logins")
    protected Integer maxLogins;

    /**
     * Gets the value of the eraseSettings property.
     * 
     * @return
     *     possible object is
     *     {@link XMLEraseSettings }
     *     
     */
    public XMLEraseSettings getEraseSettings() {
        return eraseSettings;
    }

    /**
     * Sets the value of the eraseSettings property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLEraseSettings }
     *     
     */
    public void setEraseSettings(XMLEraseSettings value) {
        this.eraseSettings = value;
    }

    /**
     * Gets the value of the transformSettings property.
     * 
     * @return
     *     possible object is
     *     {@link XMLTransformation }
     *     
     */
    public XMLTransformation getTransformSettings() {
        return transformSettings;
    }

    /**
     * Sets the value of the transformSettings property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLTransformation }
     *     
     */
    public void setTransformSettings(XMLTransformation value) {
        this.transformSettings = value;
    }

    /**
     * Gets the value of the schedule property.
     * 
     * @return
     *     possible object is
     *     {@link XMLScheduleRestriction }
     *     
     */
    public XMLScheduleRestriction getSchedule() {
        return schedule;
    }

    /**
     * Sets the value of the schedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLScheduleRestriction }
     *     
     */
    public void setSchedule(XMLScheduleRestriction value) {
        this.schedule = value;
    }

    /**
     * Gets the value of the server property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServer() {
        return server;
    }

    /**
     * Sets the value of the server property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServer(String value) {
        this.server = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the maxLogins property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxLogins() {
        return maxLogins;
    }

    /**
     * Sets the value of the maxLogins property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxLogins(Integer value) {
        this.maxLogins = value;
    }

}