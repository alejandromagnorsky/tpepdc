//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.11 at 11:39:58 PM ART 
//


package dao.login;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;




/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the dao.login.generated package. 
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

    private final static QName _XMLLoginLogRoot_QNAME = new QName("http://www.example.org/loginLog", "XMLLoginLogRoot");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: dao.login.generated
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link XMLLoginLog }
     * 
     */
    public XMLLoginLog createXMLLoginLog() {
        return new XMLLoginLog();
    }

    /**
     * Create an instance of {@link XMLUserLoginList }
     * 
     */
    public XMLUserLoginList createXMLUserLoginList() {
        return new XMLUserLoginList();
    }

    /**
     * Create an instance of {@link XMLLogin }
     * 
     */
    public XMLLogin createXMLLogin() {
        return new XMLLogin();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLLoginLog }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.example.org/loginLog", name = "XMLLoginLogRoot")
    public JAXBElement<XMLLoginLog> createXMLLoginLogRoot(XMLLoginLog value) {
        return new JAXBElement<XMLLoginLog>(_XMLLoginLogRoot_QNAME, XMLLoginLog.class, null, value);
    }

}