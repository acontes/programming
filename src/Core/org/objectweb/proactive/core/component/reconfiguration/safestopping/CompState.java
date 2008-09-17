package org.objectweb.proactive.core.component.reconfiguration.safestopping;

import java.io.Serializable;

public class CompState implements Serializable {
	private static final long serialVersionUID = -8693948560981426314L;
	public static final int START=0;
	public static final int WAITFORSTOPPING=1;
	public static final int STOPPING=2;
	public static final int READY2STOP=3;
	public static final int STOPPED=4;
	private int state;
	
	public CompState(){
	}
	
	public void start(){
		state = START;
	}
	public void waitForStop(){
		state = WAITFORSTOPPING ;
	}
	public void stopping(){
		state = STOPPING;
	}
	public void readyToStop (){
		state = READY2STOP;
	}
	public void stopped(){
		state = STOPPED;
	}

	public boolean isStart(){
		return state==START;
	}
	public boolean isWaitForStop(){
		return state==WAITFORSTOPPING;
	}
	public boolean isStopping(){
		return state==STOPPING;
	}
	
	public boolean isReadyToStop(){
		return state==READY2STOP;
	}
	public boolean isStopped(){
		return state==STOPPED;
	}

	public int getState() {
		return state;
	}
	
	
}
