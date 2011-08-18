/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.objectweb.proactive.extensions.sca.adl.xml.parsingTools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The Object which correspond to the content of Service tag 
 * @author mug
 */
public class ExposedInterface {
    
    protected String exposedInterfaceName;
    protected String role;
    protected String softLink;
    protected String implementation;
    protected String referenceComponentName;

    public ExposedInterface(String name, String role, String softLink, String implementation) {
        this.exposedInterfaceName = name;
        this.role = role;
        this.softLink = softLink;
        this.implementation = implementation;
    }

    /**
     * Get Xml element representation of this object
     * @return Fractal XML element
     */
    public Element toXmlElement(Document doc) {
        Element rootEle = doc.createElement(FRACTAL_INTERFACE_TAG);
        rootEle.setAttribute(FRACTAL_SIGNATURE_ATTRIBUTE_OF_INTERFACE_TAG, implementation);
        rootEle.setAttribute(FRACTAL_ROLE_ATTRIBUTE_OF_INTERFACE_TAG, role);
        rootEle.setAttribute(FRACTAL_NAME_ATTRIBUTE_OF_INTERFACE_TAG, exposedInterfaceName);
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

    public String getExposedInterfaceName() {
        return exposedInterfaceName;
    }

    public void setExposedInterfaceName(String name) {
        this.exposedInterfaceName = name;
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
    
     //Fractal tag and attribute name Constants
    public static final String FRACTAL_INTERFACE_TAG = "interface";
    public static final String FRACTAL_SIGNATURE_ATTRIBUTE_OF_INTERFACE_TAG = "signature";
    public static final String FRACTAL_ROLE_ATTRIBUTE_OF_INTERFACE_TAG = "role";
    public static final String FRACTAL_NAME_ATTRIBUTE_OF_INTERFACE_TAG = "name";
    
}