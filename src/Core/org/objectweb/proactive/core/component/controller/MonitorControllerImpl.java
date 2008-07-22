package org.objectweb.proactive.core.component.controller;


import java.lang.reflect.Method;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;
import org.objectweb.proactive.core.event.MessageEvent;
import org.objectweb.proactive.core.event.MessageEventListener;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.api.PAActiveObject;

public class MonitorControllerImpl extends AbstractProActiveController implements MonitorController, MessageEventListener 
{
	private static final long serialVersionUID = 1L;

	private static final Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_CONTROLLERS);
	
	private Map<String, MethodStatistics> serverMethodsToMonitor = null;
    private Map<String, MethodStatistics> clientMethodsToMonitor = null;
    
    private String name = null;
    
    public MonitorControllerImpl(Component owner)
    {
        super(owner);
        serverMethodsToMonitor = Collections.synchronizedMap(new HashMap<String, MethodStatistics>());
        clientMethodsToMonitor = Collections.synchronizedMap(new HashMap<String, MethodStatistics>());
    }

    public void monitorInit()
    {    	
        PAActiveObject.getBodyOnThis().addMessageEventListener(this);
        try {
			name = ((NameController) owner.getFcInterface( Constants.NAME_CONTROLLER)).getFcName();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		registerMethods();
    }
    
    protected void setControllerItfType()
    {
        try {
            setItfType(ProActiveTypeFactoryImpl.instance().createFcItfType(Constants.MONITOR_CONTROLLER,
                    MonitorController.class.getName(), TypeFactory.SERVER,
                    TypeFactory.MANDATORY, TypeFactory.SINGLE));
        } catch (InstantiationException e) {
            throw new ProActiveRuntimeException("cannot create controller " + this.getClass().getName());
        }
    }
    
    public MethodStatistics getStatistics(String methodName, String type) throws Exception
    {
    	if (type.equals("client"))
    		return clientMethodsToMonitor.get(methodName);
    	else if (type.equals("server"))
            return serverMethodsToMonitor.get(methodName);
    	else
    		return null;
    }
    
    private void registerMethods()
    {
    	NameController nc = null;
    	try {
			nc = (NameController)owner.getFcInterface(Constants.NAME_CONTROLLER);
		} catch (NoSuchInterfaceException e1) {
			e1.printStackTrace();
		}
		String name = nc.getFcName();
		InterfaceType itfTypes[] = ((ComponentType)owner.getFcType()).getFcInterfaceTypes();
        for (InterfaceType itfType: itfTypes) { 
            try {
                Class<?> klass = ClassLoader.getSystemClassLoader().loadClass(itfType.getFcItfSignature());
                Method[] methods = klass.getDeclaredMethods();
                if (!itfType.getFcItfName().endsWith("-controller") && !itfType.getFcItfName().equals("component")) {
                		if (!itfType.isFcClientItf()) {
                			for (Method met: methods) {
                				serverMethodsToMonitor.put(met.getName(), new MethodStatistics());
                				logger.debug(met.getName() + " (server) added to monitoring on component " + name + "!!!");
                			}
                		} else {
                			for (Method met: methods) {
                				clientMethodsToMonitor.put(met.getName(), new MethodStatistics());
                				logger.debug(met.getName() + " (client) added to monitoring on component " + name + "!!!");
                			}                	
                		}
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }                   
    }
    
    // ServerRequest Arrival
    public void requestReceived(MessageEvent event)
    {
        if (serverMethodsToMonitor.containsKey(event.getMethodName())) {
        	// String date = new SimpleDateFormat("mm:ss").format(new Date (event.getTimeStamp()));
        	String date = "" + (event.getTimeStamp() % 10000);
        	logger.debug(name + " component (" + date + "): service request with ID " + event.getSequenceNumber() + " of (server) method " + event.getMethodName() + " received");
        	serverMethodsToMonitor.get(event.getMethodName()).recordArrival(event.getTimeStamp());
        }
    }

    // ServerRequest Served (with future)
    public void replySent(MessageEvent event)
    {
    	// TODO: Should not happen
    }

    // ServerRequest Served (without future)
    public void voidRequestServed(MessageEvent event)
    {
        if (serverMethodsToMonitor.containsKey(event.getMethodName())) {
        	logger.debug(name + " component (" + event.getTimeStamp() + "): service request with ID " + event.getSequenceNumber() + " of (server) method " + event.getMethodName() + " served");
        	serverMethodsToMonitor.get(event.getMethodName()).recordDeparture(event.getTimeStamp());
        }
    }

    // ServerRequest Out of Queue
    public void servingStarted(MessageEvent event)
    {
        if (serverMethodsToMonitor.containsKey(event.getMethodName())) {
        	logger.debug(name + " component (" + event.getTimeStamp() + "): service request with ID " + event.getSequenceNumber() + " of (server) method " + event.getMethodName() + " removed from waiting queue");
        	// TODO: This measure seems to be very unreliable... Needs more investigation
        }
    }
    
    // Client reply arrival
    public void replyReceived(MessageEvent event)
    {
        if (clientMethodsToMonitor.containsKey(event.getMethodName())) {
        	logger.debug(name + " component (" + event.getTimeStamp() + "): client service reply with ID " + event.getSequenceNumber() + " of (client) method " + event.getMethodName() + " received");
        	clientMethodsToMonitor.get(event.getMethodName()).recordArrival(event.getTimeStamp());
        }
    }
    
    // Client request departure
    public void requestSent(MessageEvent event)
    {
    	if (clientMethodsToMonitor.containsKey(event.getMethodName())) {
    		logger.debug(name + " component (" + event.getTimeStamp() + "): client service request with ID " + event.getSequenceNumber() + " of (client) method " + event.getMethodName() + " sent");
    		clientMethodsToMonitor.get(event.getMethodName()).recordDeparture(event.getTimeStamp());
    	}
    }
}
