//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.05 at 08:51:33 PM ART 
//


package settings;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the settings package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _ProxySettings_QNAME = new QName("", "ProxySettings");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: settings
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link DateTimes }
     * 
     */
    public DateTimes createDateTimes() {
        return new DateTimes();
    }

    /**
     * Create an instance of {@link User }
     * 
     */
    public User createUser() {
        return new User();
    }

    /**
     * Create an instance of {@link Transformation }
     * 
     */
    public Transformation createTransformation() {
        return new Transformation();
    }

    /**
     * Create an instance of {@link Settings }
     * 
     */
    public Settings createSettings() {
        return new Settings();
    }

    /**
     * Create an instance of {@link SizeRestriction }
     * 
     */
    public SizeRestriction createSizeRestriction() {
        return new SizeRestriction();
    }

    /**
     * Create an instance of {@link IPBlacklist }
     * 
     */
    public IPBlacklist createIPBlacklist() {
        return new IPBlacklist();
    }

    /**
     * Create an instance of {@link DateRestriction }
     * 
     */
    public DateRestriction createDateRestriction() {
        return new DateRestriction();
    }

    /**
     * Create an instance of {@link ScheduleRestriction }
     * 
     */
    public ScheduleRestriction createScheduleRestriction() {
        return new ScheduleRestriction();
    }

    /**
     * Create an instance of {@link EraseSettings }
     * 
     */
    public EraseSettings createEraseSettings() {
        return new EraseSettings();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Settings }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "ProxySettings")
    public JAXBElement<Settings> createProxySettings(Settings value) {
        return new JAXBElement<Settings>(_ProxySettings_QNAME, Settings.class, null, value);
    }

}