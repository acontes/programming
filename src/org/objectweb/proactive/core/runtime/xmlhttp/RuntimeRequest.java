/*
 * Created on May 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.objectweb.proactive.core.runtime.xmlhttp;

import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.ext.webservices.utils.ProActiveXMLUtils;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
 
/**
 * @author vlegrand
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class RuntimeRequest implements Serializable {
    private static Logger logger = Logger.getLogger("XML_HTTP");
    private String methodName;
    private ArrayList parameters = new ArrayList();
    private ArrayList paramsTypes;
    private UniqueID oaid;

    private static HashMap hMapMethods;
    private static ProActiveRuntimeImpl runtime;
    
    static {
    	
    	// init the hashmap, that contains all the methods of  ProActiveRuntimeImpl 
        // in 'Object' (value) and the name of funtions in key 
        // (Warning two functions can t have the same name (for now)) 
        runtime = (ProActiveRuntimeImpl) ProActiveRuntimeImpl.getProActiveRuntime();
       	Method [] allmethods = runtime.getClass().getMethods();
       	int numberOfMethods = allmethods.length;
       	hMapMethods = new HashMap(numberOfMethods);
		for(int i=0;i<numberOfMethods;i++) {
			
			String methodname = allmethods[i].getName();
			if(hMapMethods.containsKey(methodname)) {
				
				Object obj = hMapMethods.get(methodname);
				
				if( ! ( obj instanceof ArrayList) ){
					ArrayList array = new ArrayList();
					array.add((Method)obj);
					array.add(allmethods[i]);
					hMapMethods.put(methodname,array);
				}
				else {
					((ArrayList)obj).add(allmethods[i]);
					hMapMethods.put(methodname,(ArrayList)obj);
				}
			} 
			else 
				hMapMethods.put(methodname,allmethods[i]);
		}     	
	
    	
    }
    
    public RuntimeRequest(String newmethodName) {
        this.methodName = newmethodName;
       	
    }
    
   
    public RuntimeRequest(String newmethodName, ArrayList newparameters) {
        this(newmethodName);
        this.parameters = newparameters;
    }

    public RuntimeRequest(String newmethodName, ArrayList newparameters,
        ArrayList mewparamsTypes) {
        this(newmethodName,newparameters);
        this.paramsTypes = mewparamsTypes;
    }

    public RuntimeRequest(String newmethodName, ArrayList newparameters, UniqueID newoaid) {
        this(newmethodName,newparameters);
        this.oaid = newoaid;
    }

    public RuntimeReply process() throws ProActiveException {
       
        		Object[] params = parameters.toArray();
        		Object result = null;

        		if (this.oaid == null) {
            
        			Method m = getProActiveRuntimeMethod(methodName,parameters);
        			try {
						result = m.invoke(runtime, parameters.toArray());
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
              
            } else {

                Body body = ProActiveXMLUtils.getBody(this.oaid);

                //Method m = body.getClass().getMethod(methodName, classes);;
                      //result = mc.execute(body);      
                Class[] classes = new Class[parameters.size()];
                //Remplissage du tableau des types:
                for (int i = 0; i < parameters.size(); i++) {
                    classes[i] = parameters.get(i).getClass();
                }
                try {
					result = body.getClass().getMethod(methodName, classes).invoke(body,parameters.toArray());
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            return new RuntimeReply(result);
      
     
    }

    public String getMethodName() {
        return this.methodName;
    }

    public ArrayList getParameters() {
        return this.parameters;
    }
    
    public Method getProActiveRuntimeMethod(String methodsearch, ArrayList paramsearch){
    	  Object mret = hMapMethods.get(methodsearch);
    	  
    	  if(mret instanceof ArrayList) {
    	  	
    	  	ArrayList allSameMethod = (ArrayList)mret;
    	  	mret = null;
    	  	for(int i=0;i<allSameMethod.size();i++) {
    	  		
    	  		Method mtest = ((Method)allSameMethod.get(i));	
    	  		if( mtest.getParameterTypes().length == paramsearch.size() ) {
    	  			if(mret != null ) {
    					logger.error("----------------------------------------------------------------------------");
    					logger.error("----- ERROR : two functions in ProActiveRuntimeImpl can t have the same name");
    					logger.error("----- ERROR : and the same number of paramters ");
    					logger.error("----------------------------------------------------------------------------");
    	  				
    	  			}
    	  			else 
    	  				mret = mtest;
    	  		}
    	  	}
    	  	
    	  }
    
    	  return (Method)mret;	
    }
    
    
   
    
}
