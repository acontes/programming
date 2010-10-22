package org.objectweb.proactive.core.component.componentcontroller.remmos.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.OutgoingRequestRecord;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.PathItem;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.RequestPath;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.IncomingRequestRecord;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;

public class RemmosUtils {


	/** 
	 * Describe the F and NF interfaces of a PAComponent 
	 * @param comp
	 */
	public static void describeComponent(Component comp) {

		if(!(comp instanceof PAComponent)) {
			System.out.println("Component is not an instance of PAComponent");
			return;
		}
		PAComponent pacomp = (PAComponent) comp;
		System.out.println("Component: " + pacomp.getComponentParameters().getName());
		InterfaceType[] itfTypes = pacomp.getComponentParameters().getInterfaceTypes();
		itfTypes = pacomp.getComponentParameters().getComponentType().getFcInterfaceTypes();
		for(int i=0; i<itfTypes.length; i++) {
			System.out.println("  Interface: " + (((PAGCMInterfaceType)itfTypes[i]).isFcClientItf() ? " client ":" server ")
					+ (((PAGCMInterfaceType)itfTypes[i]).isFcOptionalItf() ? " optional  ":" mandatory ")
					+ (((PAGCMInterfaceType)itfTypes[i]).isInternal() ? " internal  ":" external  ")
					+ itfTypes[i].getFcItfName() );
		}

		System.out.println();
		InterfaceType[] itfNFTypes = pacomp.getComponentParameters().getComponentNFType().getFcInterfaceTypes();
		for(int i=0; i<itfNFTypes.length; i++) {
			System.out.println("  Interface (NF): " + (((PAGCMInterfaceType)itfNFTypes[i]).isFcClientItf() ? " client ":" server ")
					+ (((PAGCMInterfaceType)itfNFTypes[i]).isFcOptionalItf() ? " optional  ":" mandatory ")
					+ (((PAGCMInterfaceType)itfNFTypes[i]).isInternal() ? " internal  ":" external  ")
					+ itfNFTypes[i].getFcItfName() );
		}
		System.out.println();

		/*
		Object[] intfs = pacomp.getFcInterfaces();
		for(int i=0; i<intfs.length; i++) {
			System.out.println(i+":"+ ((Interface)intfs[i]).getFcItfName() );
		}*/

	}
	
	//-----------------------------------------------------------------------------------------------------------------------
	// only for testing
	public static void displayLogs(Component comp) {
		String hostComponentName = null;
		
		try {
			hostComponentName = Fractal.getNameController(comp).getFcName();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		
		Map<ComponentRequestID, IncomingRequestRecord> requestLog = null;
		Map<ComponentRequestID, OutgoingRequestRecord> callLog = null;
		
		try {
			requestLog = ((MonitorControl)comp.getFcInterface(Constants.MONITOR_CONTROLLER)).getIncomingRequestLog();
			callLog = ((MonitorControl)comp.getFcInterface(Constants.MONITOR_CONTROLLER)).getOutgoingRequestLog();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		
		System.out.println("===================== Component ["+ hostComponentName +"] =====================");
		System.out.println("===================== Incoming Log ===============================");
		displayRequestLog(requestLog);
		System.out.println("======================Outgoing Log ================================");
		displayCallLog(callLog);
		System.out.println("==================================================================");
		System.out.println();
	}

    public static void displayRequestLog(Map<ComponentRequestID, IncomingRequestRecord> requestLog) {
    	if(requestLog == null)
    		return;    	
    	Iterator<ComponentRequestID> i = requestLog.keySet().iterator();
    	ComponentRequestID crID;
    	IncomingRequestRecord rs;
    	while(i.hasNext()) {
    		crID = i.next();
    		rs = requestLog.get(crID);
    		System.out.println("ID: "+ crID + " Sender: "+ rs.getCallerComponent() +
    				" Call: "+ rs.getCalledComponent() + "." + rs.getInterfaceName()+"."+rs.getMethodName() +
    				" Arr: " + rs.getArrivalTime() + " Serv: " + rs.getServingStartTime() + " Repl: " + rs.getReplyTime() +
    				" WQ: " + (rs.getServingStartTime()-rs.getArrivalTime()) + 
    				" SRV: " + (rs.getReplyTime()-rs.getServingStartTime()) +
    				" TOT: "+ (rs.getReplyTime() - rs.getArrivalTime()));	
    	}
    }

	public static void displayCallLog(Map<ComponentRequestID, OutgoingRequestRecord> callLog) {
		if(callLog == null)
			return;
    	Iterator<ComponentRequestID> i = callLog.keySet().iterator();
    	ComponentRequestID crID;
    	OutgoingRequestRecord cs;
    	long wbnTime = 0;
    	Long start, stop;
    	Map<Long,Long> wbnStart;
    	Map<Long,Long> wbnStop;
    	while(i.hasNext()) {
    		crID = i.next();
    		cs = callLog.get(crID);
    		
    		//calculate WbN time
    		wbnStart = cs.getWbnStartTime();
    		wbnStop = cs.getWbnStopTime();
    		
    		for(Long id: wbnStart.keySet()) {
    			if(wbnStop.containsKey(id)) {
    				start = wbnStart.get(id);
    				stop = wbnStop.get(id);
    				if(stop.longValue() > cs.getReplyReceptionTime()) {
    					stop = cs.getReplyReceptionTime();
    				}
    				wbnTime += (stop - start);
    			}
    		}
    		
    		System.out.println("Parent: "+ cs.getParentID() + " ID: "+ crID + 
    				" Call: "+ cs.getCalledComponent() + "." + cs.getInterfaceName()+"."+cs.getMethodName()+ 
    				" SentTime: " + cs.getSentTime() + 
    				" RealReplyReceivedTime: " + cs.getReplyReceptionTime() +
    				" WbN: " + wbnTime + 
    				" SRV: " + (cs.getReplyReceptionTime() - cs.getSentTime()));
    	}
    }

	public static void displayNotifs(Component comp) {
		String hostComponentName = null;
		try {
			hostComponentName = Fractal.getNameController(comp).getFcName();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		
		List<String> notifs = null;
		
		try {
			notifs = ((MonitorControl)comp.getFcInterface(Constants.MONITOR_CONTROLLER)).getNotificationsReceived();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		
		System.out.println("===================== Component ["+ hostComponentName +"] =====================");
		System.out.println("===================== Notifications ================================");
		for(String s : notifs) {
			System.out.println(s);
		}
		System.out.println("==================================================================");
		System.out.println();
		
	}
	
	public static void displayReqs(Component comp) {
		String hostComponentName = null;
		try {
			hostComponentName = Fractal.getNameController(comp).getFcName();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		List<ComponentRequestID> requests = null;
		try {
			requests = ((MonitorControl)comp.getFcInterface(Constants.MONITOR_CONTROLLER)).getListOfIncomingRequestIDs();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		System.out.println("===================== Component ["+ hostComponentName +"] =====================");
		System.out.println("===================== Requests Received ============================");
		for(ComponentRequestID r: requests) {
			System.out.println(r);
		}
		System.out.println("==================================================================");
		System.out.println();
		
	}
	
	public static void displayCalls(Component comp) {
		String hostComponentName = null;
		try {
			hostComponentName = Fractal.getNameController(comp).getFcName();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		List<ComponentRequestID> calls = null;
		try {
			calls = ((MonitorControl)comp.getFcInterface(Constants.MONITOR_CONTROLLER)).getListOfOutgoingRequestIDs();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		System.out.println("===================== Component ["+ hostComponentName +"] =====================");
		System.out.println("===================== Calls Sent =================================");
		for(ComponentRequestID r: calls) {
			System.out.println(r);
		}
		System.out.println("==================================================================");
		System.out.println();
		
	}
	
	public static void displayPath(RequestPath rp) {
		List<PathItem> paths = rp.getPath();
		System.out.println("Request Path ("+ paths.size()+")");
		for(PathItem path:paths) {
			System.out.println("*" + path.toString());
		}
	}
	
	public static void displayMetrics(Component comp) throws NoSuchInterfaceException {
		String hostComponentName = null;
		try {
			hostComponentName = Fractal.getNameController(comp).getFcName();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		List<String> metricSet = null;
		try {
			metricSet = ((MonitorControl)comp.getFcInterface(Constants.MONITOR_CONTROLLER)).getMetricList();
		} catch (NoSuchInterfaceException e) {
			e.printStackTrace();
		}
		System.out.println("Metrics in component ["+ hostComponentName +"]");
		System.out.print("   ");
		for(String s : metricSet) {
			System.out.print(s+" ");
		}
		System.out.println();
	}
	
	public static String toSeg(long nseg) {
		return ""+((double)nseg)/1000000000.0;
	}
}