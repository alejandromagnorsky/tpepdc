//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.11 at 10:58:37 PM ART 
//


package dao.settings;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dao package. 
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

    private final static QName _XMLProxySettings_QNAME = new QName("", "XMLProxySettings");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dao
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XMLIPBlacklist }
     * 
     */
    public XMLIPBlacklist createXMLIPBlacklist() {
        return new XMLIPBlacklist();
    }

    /**
     * Create an instance of {@link XMLEraseSettings }
     * 
     */
    public XMLEraseSettings createXMLEraseSettings() {
        return new XMLEraseSettings();
    }

    /**
     * Create an instance of {@link XMLUser }
     * 
     */
    public XMLUser createXMLUser() {
        return new XMLUser();
    }

    /**
     * Create an instance of {@link XMLDateRestriction }
     * 
     */
    public XMLDateRestriction createXMLDateRestriction() {
        return new XMLDateRestriction();
    }

    /**
     * Create an instance of {@link XMLTransformation }
     * 
     */
    public XMLTransformation createXMLTransformation() {
        return new XMLTransformation();
    }

    /**
     * Create an instance of {@link XMLSettings }
     * 
     */
    public XMLSettings createXMLSettings() {
        return new XMLSettings();
    }

    /**
     * Create an instance of {@link XMLSizeRestriction }
     * 
     */
    public XMLSizeRestriction createXMLSizeRestriction() {
        return new XMLSizeRestriction();
    }

    /**
     * Create an instance of {@link XMLScheduleRestriction }
     * 
     */
    public XMLScheduleRestriction createXMLScheduleRestriction() {
        return new XMLScheduleRestriction();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLSettings }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "XMLProxySettings")
    public JAXBElement<XMLSettings> createXMLProxySettings(XMLSettings value) {
        return new JAXBElement<XMLSettings>(_XMLProxySettings_QNAME, XMLSettings.class, null, value);
    }

}
