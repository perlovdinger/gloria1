//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.09 at 04:05:23 em CEST 
//


package com.volvo.gloria.common.c.baldo;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
 *         &lt;sequence maxOccurs="unbounded">
 *           &lt;element name="GMFVerb" type="{http://userandorganisation.parts.volvo.com/1_0}GMFVerbType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="UserOrganisation" type="{http://userandorganisation.parts.volvo.com/1_0}UserOrganisationType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="UserRoleDef" type="{http://userandorganisation.parts.volvo.com/1_0}UserRoleDefType" maxOccurs="unbounded" minOccurs="0"/>
 *           &lt;element name="OrganisationInfo" type="{http://userandorganisation.parts.volvo.com/1_0}OrganisationInfoType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "gmfVerbAndUserOrganisationAndUserRoleDef"
})
@XmlRootElement(name = "UserAndOrganisation")
public class UserAndOrganisation {

    @XmlElements({
        @XmlElement(name = "OrganisationInfo", type = OrganisationInfoType.class),
        @XmlElement(name = "GMFVerb", type = GMFVerbType.class),
        @XmlElement(name = "UserRoleDef", type = UserRoleDefType.class),
        @XmlElement(name = "UserOrganisation", type = UserOrganisationType.class)
    })
    protected List<Object> gmfVerbAndUserOrganisationAndUserRoleDef;

    /**
     * Gets the value of the gmfVerbAndUserOrganisationAndUserRoleDef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the gmfVerbAndUserOrganisationAndUserRoleDef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGMFVerbAndUserOrganisationAndUserRoleDef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OrganisationInfoType }
     * {@link GMFVerbType }
     * {@link UserRoleDefType }
     * {@link UserOrganisationType }
     * 
     * 
     */
    public List<Object> getGMFVerbAndUserOrganisationAndUserRoleDef() {
        if (gmfVerbAndUserOrganisationAndUserRoleDef == null) {
            gmfVerbAndUserOrganisationAndUserRoleDef = new ArrayList<Object>();
        }
        return this.gmfVerbAndUserOrganisationAndUserRoleDef;
    }

}
