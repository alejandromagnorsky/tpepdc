//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.12 at 02:45:25 PM ART 
//


package dao.admin;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dao.admin package. 
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

    private final static QName _XMLAdminRoot_QNAME = new QName("http://www.example.org/admin", "XMLAdminRoot");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dao.admin
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XMLAdminRoot }
     * 
     */
    public XMLAdminRoot createXMLAdminRoot() {
        return new XMLAdminRoot();
    }

    /**
     * Create an instance of {@link XMLAdmin }
     * 
     */
    public XMLAdmin createXMLAdmin() {
        return new XMLAdmin();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLAdminRoot }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.org/admin", name = "XMLAdminRoot")
    public JAXBElement<XMLAdminRoot> createXMLAdminRoot(XMLAdminRoot value) {
        return new JAXBElement<XMLAdminRoot>(_XMLAdminRoot_QNAME, XMLAdminRoot.class, null, value);
    }

}
