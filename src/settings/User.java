//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.05 at 08:51:33 PM ART 
//


package settings;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for User complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="User">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="erase-settings" type="{}EraseSettings" minOccurs="0"/>
 *         &lt;element name="transform-settings" type="{}Transformation" minOccurs="0"/>
 *         &lt;element name="schedule" type="{}ScheduleRestriction" minOccurs="0"/>
 *         &lt;element name="server" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LoginsPerDay" type="{}DateTimes"/>
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
@XmlType(name = "User", propOrder = {
    "eraseSettings",
    "transformSettings",
    "schedule",
    "server",
    "loginsPerDay"
})
public class User {

    @XmlElement(name = "erase-settings")
    protected EraseSettings eraseSettings;
    @XmlElement(name = "transform-settings")
    protected Transformation transformSettings;
    protected ScheduleRestriction schedule;
    @XmlElement(required = true)
    protected String server;
    @XmlElement(name = "LoginsPerDay", required = true)
    protected DateTimes loginsPerDay;
    @XmlAttribute
    protected String name;
    @XmlAttribute(name = "max-logins")
    protected Integer maxLogins;

    /**
     * Gets the value of the eraseSettings property.
     * 
     * @return
     *     possible object is
     *     {@link EraseSettings }
     *     
     */
    public EraseSettings getEraseSettings() {
        return eraseSettings;
    }

    /**
     * Sets the value of the eraseSettings property.
     * 
     * @param value
     *     allowed object is
     *     {@link EraseSettings }
     *     
     */
    public void setEraseSettings(EraseSettings value) {
        this.eraseSettings = value;
    }

    /**
     * Gets the value of the transformSettings property.
     * 
     * @return
     *     possible object is
     *     {@link Transformation }
     *     
     */
    public Transformation getTransformSettings() {
        return transformSettings;
    }

    /**
     * Sets the value of the transformSettings property.
     * 
     * @param value
     *     allowed object is
     *     {@link Transformation }
     *     
     */
    public void setTransformSettings(Transformation value) {
        this.transformSettings = value;
    }

    /**
     * Gets the value of the schedule property.
     * 
     * @return
     *     possible object is
     *     {@link ScheduleRestriction }
     *     
     */
    public ScheduleRestriction getSchedule() {
        return schedule;
    }

    /**
     * Sets the value of the schedule property.
     * 
     * @param value
     *     allowed object is
     *     {@link ScheduleRestriction }
     *     
     */
    public void setSchedule(ScheduleRestriction value) {
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
     * Gets the value of the loginsPerDay property.
     * 
     * @return
     *     possible object is
     *     {@link DateTimes }
     *     
     */
    public DateTimes getLoginsPerDay() {
        return loginsPerDay;
    }

    /**
     * Sets the value of the loginsPerDay property.
     * 
     * @param value
     *     allowed object is
     *     {@link DateTimes }
     *     
     */
    public void setLoginsPerDay(DateTimes value) {
        this.loginsPerDay = value;
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
