/*
 * Created on Jul 20, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.objectweb.proactive.core.body.xmlhttp;

import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.MethodCallExecutionFailedException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * @author vlegrand
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMLHTTPMethodCallMessage implements XMLHTTPMessage {
    private String methodName;
    private Object[] params;
    private Class[] classes;
    private Object dest;

    public XMLHTTPMethodCallMessage(String methodName, Object[] params,
        Class[] classes, Object dest) {
        this.methodName = methodName;
        this.params = params;
        this.classes = classes;
        this.dest = dest;
    }

    public Object processMessage() {
        Method m;
		try {
			m = dest.getClass().getMethod(methodName, classes);
			MethodCall mc = MethodCall.getMethodCall(m, params);
	        Object result = mc.execute(dest);
	        return result;
		} catch (SecurityException e) {
			
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
		
			e.printStackTrace();
		} catch (MethodCallExecutionFailedException e) {
			
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			
			e.printStackTrace();
		}
		return null;
    }
}
