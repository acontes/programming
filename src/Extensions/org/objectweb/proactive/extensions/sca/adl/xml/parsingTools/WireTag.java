/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.objectweb.proactive.extensions.sca.adl.xml.parsingTools;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author mug
 */
public class WireTag {
    
    protected String source;
    protected String target;

    public WireTag(String source, String target) {
        this.source = source;
        this.target = target;
    }
    
    /**
     * Get Fractal XML Element output of the component
//     */
//    public Element toXmlElement(Document doc) {
//
//        Element rootEle = doc.createElement(FRACTAL_BINDING_TAG);
//        rootEle.setAttribute(FRACTAL_NAME_ATTRIBUTE_OF_COMPONENT_TAG, ComponentName);
//        for (ExposedInterface service : services) {
//            Element tmp = service.toXmlElement(doc);
//            if (tmp != null) {
//                rootEle.appendChild(tmp);
//            }
//        }
//        Element content = doc.createElement(FRACTAL_CONTENT_TAG);
//        content.setAttribute(CLASS_ATTRIBUTE_OF_CONTENT_TAG, contentClassName);
//        Element controller = doc.createElement(FRACTAL_CONTROLLER_TAG);
//        controller.setAttribute(FRACTAL_DESC_ATTRIBUTE_OF_CONTROLLER_TAG, "primitive");
//        rootEle.appendChild(content);
//        rootEle.appendChild(controller);
//        return rootEle;
//    }
    
    public static final String FRACTAL_BINDING_TAG = "binding";
    public static final String FRACTAL_CLIENT_ATTRIBUTE_OF_BINDING_TAG = "client";
    public static final String FRACTAL_SERVER_ATTRIBUTE_OF_BINDING_TAG = "server";
    
}
