/*
 * Created on May 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.objectweb.proactive.core.body.xmlhttp;


import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.runtime.xmlhttp.RuntimeReply;
import org.objectweb.proactive.ext.webservices.utils.ProActiveXMLUtils;


/**
 * @author jerome
 *
 */
public class BodyRequest implements Serializable
{
    private static Logger logger = Logger.getLogger("XML_HTTP");
    private String methodName;
    private ArrayList parameters = new ArrayList();
    private UniqueID oaid;
    private Body body;

    public BodyRequest(String newmethodName, ArrayList newparameters, UniqueID newoaid) {
        this.methodName = newmethodName;
        this.parameters = newparameters;
        this.oaid = newoaid;
        this.body = ProActiveXMLUtils.getBody(this.oaid);
    }

    public RuntimeReply process() throws ProActiveException {
       
        		Object result = null;

                Class[] classes = new Class[parameters.size()];
                //Remplissage du tableau des types:
                for (int i = 0; i < parameters.size(); i++) {
                    classes[i] = parameters.get(i).getClass();
                }
                try {
						result = body.getClass().getMethod(methodName, classes).invoke(body,parameters.toArray());
				} catch (InvocationTargetException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            
            return new RuntimeReply(result);
      
     
    }  
}
