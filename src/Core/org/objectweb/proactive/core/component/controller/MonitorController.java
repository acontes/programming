package org.objectweb.proactive.core.component.controller;

public interface MonitorController
{        
	public static final String CONTROLLER_NAME = "monitor-controller";
	
    public MethodStatistics getStatistics(String methodName, String type) throws Exception;
    
    public void monitorInit();
}
