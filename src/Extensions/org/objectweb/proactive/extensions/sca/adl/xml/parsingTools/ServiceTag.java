/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.objectweb.proactive.extensions.sca.adl.xml.parsingTools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ServiceTag {

    protected String serviceName;
    protected String role;
    protected String softLink;
    protected String implementation;
    protected String referenceComponentName;

    public ServiceTag(String name, String role, String softLink, String implementation) {
        this.serviceName = name;
        this.role = role;
        this.softLink = softLink;
        this.implementation = implementation;
    }

    /**
     * Get Fractal XML output
     * @return Fractal XML output
     */
    public Element toXmlElement(Document doc) {
//        if (!softLink.equals("")) {
//            return null;
//        }
        Element rootEle = doc.createElement("interface");
        rootEle.setAttribute("signature", implementation);
        rootEle.setAttribute("role", role);
        rootEle.setAttribute("name", serviceName);
        return rootEle;
    }

    /*******************************************************************************
     *          Generated getters and setters
     *******************************************************************************/
    
    public String getImplementation() {
        return implementation;
    }

    public void setImplementation(String implementation) {
        this.implementation = implementation;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String name) {
        this.serviceName = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getSoftLink() {
        return softLink;
    }

    public void setSoftLink(String softLink) {
        this.softLink = softLink;
    }

    public String getReferenceComponentName() {
        return referenceComponentName;
    }

    public void setReferenceComponentName(String referenceComponentName) {
        this.referenceComponentName = referenceComponentName;
    }
    
}