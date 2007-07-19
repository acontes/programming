package org.objectweb.proactive.extra.scheduler.scripting;

import java.io.Serializable;

public class ScriptResult<E> implements Serializable {

	/**  */
	private static final long serialVersionUID = 2665277848502662458L;
	private E result = null;
	private Throwable exception = null;
	
	public ScriptResult() {}
	
	public ScriptResult(E result, Throwable exception) {
		this.result = result;
		this.exception = exception;
	}
	
	public ScriptResult(E result) {
		this(result, null);
	}
	
	public ScriptResult(Throwable exception) {
		this(null, exception);
	}
	
	public boolean errorOccured() {
		return exception != null;
	}
	
	public Throwable getException() {
		return exception;
	}
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	public E getResult() {
		return result;
	}
	public void setResult(E result) {
		this.result = result;
	}

}
