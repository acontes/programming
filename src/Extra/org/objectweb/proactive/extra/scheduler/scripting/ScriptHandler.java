package org.objectweb.proactive.extra.scheduler.scripting;

import java.io.Serializable;

public class ScriptHandler implements Serializable{

	/**  */
	private static final long serialVersionUID = 6321493405892656541L;
	
	/**
	 * ProActive Constructor
	 */
	public ScriptHandler() {
	}

	public ScriptResult handle(Script script) {
		try {
			return script.execute();
		} catch (Throwable t) {
			return new ScriptResult(t);
		}
	}
	
	public void destroy() {
		
	}
}
