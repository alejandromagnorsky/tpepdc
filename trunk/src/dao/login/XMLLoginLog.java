//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.06.11 at 11:39:58 PM ART 
//


package dao.login;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;




/**
 * <p>Java class for XMLLoginLog complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="XMLLoginLog">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="userLoginList" type="{http://www.example.org/loginLog}XMLUserLoginList" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */

@XmlRootElement( name = "XMLLoginLogRoot")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XMLLoginLog", propOrder = {
    "userLoginList"
})
public class XMLLoginLog {

    protected List<XMLUserLoginList> userLoginList;

    /**
     * Gets the value of the userLoginList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userLoginList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserLoginList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLUserLoginList }
     * 
     * 
     */
    public List<XMLUserLoginList> getUserLoginList() {
        if (userLoginList == null) {
            userLoginList = new ArrayList<XMLUserLoginList>();
        }
        return this.userLoginList;
    }

}
