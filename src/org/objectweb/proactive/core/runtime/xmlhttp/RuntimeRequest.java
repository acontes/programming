/*
 * Created on May 12, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.objectweb.proactive.core.runtime.xmlhttp;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
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

    public RuntimeReply process() throws ProActiveException {
       
        		Object[] params = parameters.toArray();
        		Object result = null;
            
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
                          
            return new RuntimeReply(result);
      
     
    }

    public String getMethodName() {
        return this.methodName;
    }

    public ArrayList getParameters() {
        return this.parameters;
    }
    
    private Method getProActiveRuntimeMethod(String methodsearch, ArrayList paramsearch){
    	  Object mret = hMapMethods.get(methodsearch);
    	  
    	  if(mret instanceof ArrayList) {
    	  	
    	  	ArrayList allSameMethod = (ArrayList) ((ArrayList)mret).clone();
    	  	
    	  	int sameMethodSize = allSameMethod.size();
    	  	int paramsearchsize = paramsearch.size();
    	 	for(int i=sameMethodSize-1;i>=0;i--) {
 
    	  		if( ((Method)allSameMethod.get(i)).getParameterTypes().length != paramsearchsize) 
    	  			allSameMethod.remove(i);
    	 	}	

      		sameMethodSize = allSameMethod.size();
    	 	if( sameMethodSize == 1)
    	 		mret=allSameMethod.get(0);
    	  	else { 

    	  		Class [] paramtypes = null;
    	  		boolean isgood = true, ispossible = true;
    	  		for(int i=sameMethodSize-1;i>=0;i--)	{
    	  			paramtypes = ((Method)allSameMethod.get(i)).getParameterTypes();
    	  			
    	  			for(int j=0;j<paramsearchsize;j++) {
    	  				
    	  				Class classtest = paramsearch.get(j).getClass();
    	  				if( paramtypes[j] != classtest ){
    	  					isgood = false;
    	  					if( classtest.isAssignableFrom(paramtypes[j]) == false){
    	  						ispossible = false;
    	  						break;
    	  					}
    	  				}
    	  			}
    	  			if( isgood == true ){
    	  				mret=allSameMethod.get(i);
      					break;
    				}
    	  			else if( ispossible == false )
    	  				allSameMethod.remove(i);
    	  				
    	  			isgood = true;
    	  			ispossible = true;
    	  			}
    	  		
    	  		}		
    	  		
    	 	if( allSameMethod.size() == 1 )
    	 		mret=allSameMethod.get(0);
    	 	else {
	  				
				logger.error("----------------------------------------------------------------------------");
				logger.error("----- ERROR : two functions in ProActiveRuntimeImpl can t have the same name");
				logger.error("----- ERROR : and the same type of paramters (Extends Implements)");
				logger.error("----- search   : "+methodsearch+" nb param "+paramsearch.size());
				logger.error("----------------------------------------------------------------------------");
  			
    	 	}
    	 		
    	  	/*mret = null;
    	  	for(int i=0;i<allSameMethod.size();i++) {
    	  	
    	  		Method mtest = ((Method)allSameMethod.get(i));	
    	  		if( mtest.getParameterTypes().length == paramsearch.size() ) {
    	  			if(mret != null ) {
    	  				
    	  				
    					logger.error("----------------------------------------------------------------------------");
    					logger.error("----- ERROR : two functions in ProActiveRuntimeImpl can t have the same name");
    					logger.error("----- ERROR : and the same number of paramters ");
    					logger.error("----- search   : "+methodsearch+" nb param "+paramsearch.size());
    					logger.error("----- conflict : "+mtest.getName()+" "+((Method)mret).getName());
    					logger.error("----------------------------------------------------------------------------");
    	  				
    	  			}
    	  			else 
    	  				mret = mtest;
    	  		}
    	  	}
    	  	*/
    	  	
    	  }
    
    	  return (Method)mret;	
    }
    
    
   
    
}
