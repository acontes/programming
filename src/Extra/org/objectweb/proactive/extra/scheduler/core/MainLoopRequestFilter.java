package org.objectweb.proactive.extra.scheduler.core;

import java.io.Serializable;
import java.util.ArrayList;

import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestFilter;

/**
 * Request filter for the main loop.
 * This object is made with a list of method names and provide the acceptRequest method.
 * Each method name matching a string in the list will answer true to this method. 
 * 
 * @author ProActive Team
 * @version 1.0, Jul 17, 2007
 * @since ProActive 3.2
 */
public class MainLoopRequestFilter implements RequestFilter, Serializable {

	/** Serial version UID */
	private static final long serialVersionUID = 1218635121519401713L;
	private ArrayList<String> methodNames = new ArrayList<String>();
	
	
	/**
	 * MainLoopRequestFilter Constructor with a list of string method name to filter.
	 * When acceptRequest will be invoked,
	 * only the method that have a name matching the args list will return true;
	 * 
	 * @param args a list of method names.
	 */
	public MainLoopRequestFilter(String... args){
		for (int i=0;i<args.length;i++){
			methodNames.add(args[i]);
		}
	}
	
	
	/**
	 * @see org.objectweb.proactive.core.body.request.RequestFilter#acceptRequest(org.objectweb.proactive.core.body.request.Request)
	 */
	public boolean acceptRequest(Request request) {
		for (String s : methodNames){
			if (request.getMethodName().equals(s)){
				return true;
			}
		}
		return false;
	}

}
