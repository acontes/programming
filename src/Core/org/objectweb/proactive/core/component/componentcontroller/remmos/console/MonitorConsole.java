package org.objectweb.proactive.core.component.componentcontroller.remmos.console;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.Fractive;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.ComponentRequestID;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.RequestPath;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.metrics.MetricsLibrary;
import org.objectweb.proactive.core.component.componentcontroller.remmos.Remmos;
import org.objectweb.proactive.core.component.componentcontroller.remmos.utils.RemmosUtils;
import org.objectweb.proactive.core.component.componentcontroller.sla.SLAService;
import org.objectweb.proactive.core.component.control.PABindingController;
import org.objectweb.proactive.core.component.control.PAMulticastController;
import org.objectweb.proactive.core.component.control.PASuperController;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.representative.PAComponentRepresentative;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;

/**
 * A textual monitor console to get information of components, and interact with the Monitoring and Management features.
 * 
 * @author cruz
 *
 */

public class MonitorConsole {

	// Logger
	private static final Logger logger = ProActiveLogger.getLogger(Loggers.REMMOS);
	
	final String PROMPT = "> ";
	
	// Commands
	final String COM_HELP = "h";
	final String COM_LOGS = "l";
	final String COM_MON = "m";
	final String COM_CLEAR = "c";
	
	final String COM_ADD_COMP = "a";
	final String COM_LIST = "ls";
	final String COM_DESCRIBE = "desc";
	final String COM_DISC = "disc";
	final String COM_CURRENT = "use";
	
	
	final String COM_ADD_MON = "am";
	final String COM_ENABLE_MON = "em";
	final String COM_ADD_SLA = "asla";
	final String COM_ADD_RECONF = "ar";
	
	final String COM_NOTIF = "n";
	final String COM_INC_REQ_LIST = "incr";
	final String COM_OUT_REQ_LIST = "outr";
	final String COM_PATH = "p";
	
	final String COM_ADD_METRIC = "addMetric";
	final String COM_GET_METRIC = "getMetric";
	final String COM_GET_METRICS = "getMetrics";
	final String COM_RUN_METRIC = "runMetric";
	final String COM_RUN_METRICS = "runMetrics";
	final String COM_LS_METRIC = "lsMetric";
	final String COM_LS_LIB_METRIC = "metrics";
	final String COM_RM_METRIC = "rmMetric";
	
	final String COM_ADD_SLO = "addSLO";
	
	final String COM_QUIT = "q";
	
	final String commandDescriptions[][] = {
			{COM_HELP, "help"}, 
			{COM_LOGS, "display logs"},
			{COM_MON, "start/stop monitoring in all reachable components"},
			{COM_CLEAR, "clear (reset) the logs"},
			{COM_NOTIF, "show notifications received (DEBUG)"},
			{COM_INC_REQ_LIST, "list IDs of Request that have been received by the specified component"},
			{COM_OUT_REQ_LIST, "list IDs of Request that have been sent by the specified component"},
			{COM_ADD_COMP, "add component url to the list of managed components"},
			{COM_LIST, "list the names of all managed components"},
			{COM_DESCRIBE, "describe all managed components"},
			{COM_DISC, "discover more components starting from the current"},
			{COM_CURRENT, "show current component / set current component"},
			{COM_ADD_MON, "add monitoring capabilities (NF components) to a component"},
			{COM_ENABLE_MON, "enable monitoring by connecting monitoring interfaces of related components"},
			{COM_ADD_METRIC, "add metric to specified component"},
			{COM_GET_METRIC, "get current value of the metric (does not recalculate it)"},
			{COM_GET_METRICS, "get current value of all metrics (does not recalculate them)"},
			{COM_RUN_METRIC, "run the specified metric on the specified component"},
			{COM_RUN_METRICS, "run all the metrics added to the specified component"},
			{COM_RM_METRIC, "remove metric from the specified component"},
			{COM_ADD_SLA, "add SLA monitoring capabilities (NF components) to a component"},
			{COM_ADD_SLO, "add an SLO to the SLA Monitor, that will be checked"},
			{COM_ADD_RECONF, "add reconfiguration capabilities (NF components) to a component"},
			{COM_PATH, "get path for a request"},
			{COM_QUIT, "quit"}
	};

	long appStartTime, appFinishTime;
	boolean running;
	Scanner sc;
	String input[];
	String command;
	String args;
	StringWrapper res;
	int m=1;
	String msg = "Message"; 
	boolean monitoring=false;
	boolean palogging=false;
	Component current;
	PAComponentRepresentative currentRep;
	
	Map<String, Component> managedComponents;
	
	
	public MonitorConsole() {
		sc = new Scanner(System.in);
		running = false;
		current = null;
		managedComponents = new HashMap<String,Component>();
	}
	
	public MonitorConsole(Component[] receivedComponents) {
		sc = new Scanner(System.in);
		running = false;
		current = null;
		managedComponents = new HashMap<String,Component>(receivedComponents.length);
		for(Component c : receivedComponents) {
			PAComponentRepresentative pacr = (PAComponentRepresentative) c;
			managedComponents.put(pacr.getComponentParameters().getName(),c);
		}
	}

	public void run() throws Exception {

		printBanner();
		running = true;
		
		while (running) {
			
			displayPrompt();
			input = readCommand(); // would like to have a way to execute an script here
			command = input[0];
			if(input.length < 2) {
				args = null;
			}
			else {
				args = input[1];
			}
			
			if(command.equals(COM_QUIT)) {
				stopConsole();
			}
			else if(command.equals(COM_HELP)) {
				displayHelp();
			}
			// add a component
			else if(command.equals(COM_ADD_COMP)) {
				if(args == null) {
					System.out.println("Usage: "+ COM_ADD_COMP + " <url>");
					continue;
				}
				String url = input[1];
				Component c = Fractive.lookup(url);
				String name = ((PAComponent)c).getComponentParameters().getName();
				System.out.println("Looked-up ["+name+"] @ ["+url+"]");
				managedComponents.put(name, c);
			}
			// list comopnents
			else if(command.equals(COM_LIST)) {
				for(String name : managedComponents.keySet()) {
					System.out.println("   "+ name);
				}
			}
			// set current component
			else if(command.equals(COM_CURRENT)) {
				// args supplied --> set current component
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found == null) {
						System.out.println("Component "+name+" not found.");
						continue;
					}
					current = found;
				}
				// show current component
				if(current == null) {
					System.out.println("No component selected.");
					continue;
				}
				currentRep = (PAComponentRepresentative) current;
				System.out.println("Current: "+ currentRep.getComponentParameters().getName());
			}
			// Discovers all components connected to the current one, and add them to the managed map.
			else if(command.equals(COM_DISC)) {
				if(current == null) {
					System.out.println("No component seleted.");
					continue;
				}
				// starting from the current component, find all those that are connected to him and add them to the managed components map
				discoverComponents(current);
			}
			else if(command.equals(COM_DESCRIBE)) {
				for(Component c : managedComponents.values()) {
					RemmosUtils.describeComponent(c);
				}
			}
			// Add monitoring capabilities to all the managed components or, if specified, only to one
			else if(command.equals(COM_ADD_MON)) {
				// args supplied --> add monitoring to specified component
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found == null) {
						System.out.println("Component "+name+" not found.");
						continue;
					}
					System.out.println("Adding monitoring components to "+ name +" ...");
					Remmos.addMonitoring(found);
				}
				// no args ... add monitoring to all components
				else {
					for(String name : managedComponents.keySet()) {
						System.out.println("Adding monitoring components to "+ name +" ...");
						Remmos.addMonitoring(managedComponents.get(name));
					}
				}
			}
			// Enables monitoring to all the components connected from the current.
			else if(command.equals(COM_ENABLE_MON)) {
				System.out.println("Enabling monitoring from component "+ currentRep.getComponentParameters().getName());
				Remmos.enableMonitoring(current);
			}
			// Starts/stop the monitoring activity in all managed components
			else if(command.equals(COM_MON)) {
				if(!monitoring) {
					System.out.println("Starting monitoring");
					for(Component comp : managedComponents.values()) {
						((MonitorControl)comp.getFcInterface(Constants.MONITOR_CONTROLLER)).startMonitoring();
					}
					System.out.println("Monitoring Framework started.");
					monitoring = true;
				}
				else {
					System.out.println("Stopping monitoring");
					for(Component comp : managedComponents.values()) {
						((MonitorControl)comp.getFcInterface(Constants.MONITOR_CONTROLLER)).stopMonitoring();
					}
					System.out.println("Monitoring Framework stopped.");
					monitoring = false;
				}
			}
			// Display the logs in all managed components, or in the component specified
			else if(command.equals(COM_LOGS)) {
				// args supplied --> logs of specified component
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found == null) {
						System.out.println("Component "+name+" not found.");
						continue;
					}
					RemmosUtils.displayLogs(found);
				}
				// no args ... logs of all components
				else {
					for(Component comp : managedComponents.values()) {
						RemmosUtils.displayLogs(comp);
					}
				}
					
			}
			// Clear the logs in all managed components, or in the component specified
			else if(command.equals(COM_CLEAR)) {
				// args supplied --> clear specified component
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found == null) {
						System.out.println("Cmponent "+name+" not found.");
						continue;
					}
					((MonitorControl)found.getFcInterface(Constants.MONITOR_CONTROLLER)).resetMonitoring();
					System.out.println("Logs cleared on "+ name);
				}
				else {
					for(Component comp : managedComponents.values()) {
						((MonitorControl)comp.getFcInterface(Constants.MONITOR_CONTROLLER)).resetMonitoring();
					}
				}
				System.out.println("Logs cleared");
			}
			// List IDs of incoming requests from all components, or in the component specified
			else if(command.equals(COM_INC_REQ_LIST)) {
				// args supplied --> reqs of specified component
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found == null) {
						System.out.println("Component "+name+" not found.");
						continue;
					}
					RemmosUtils.displayReqs(found);
				}
				// no args ... request on all components
				else {
					for(Component comp : managedComponents.values()) {
						RemmosUtils.displayReqs(comp);
					}
				}	
			}
			// List IDs of outgoing requests from all components, or in the component specified
			else if(command.equals(COM_OUT_REQ_LIST)) {
				// args supplied --> calls on specified component
				if(args != null) {
					String name = args.split("[ ]+", 2)[0];
					Component found = managedComponents.get(name);
					if(found == null) {
						System.out.println("Component "+name+" not found.");
						continue;
					}
					RemmosUtils.displayCalls(found);
				}
				// no args ... calls on all components
				else {
					for(Component comp : managedComponents.values()) {
						RemmosUtils.displayCalls(comp);
					}
				}
			}
			// Get the request path followed by an outgoing request from the current component
			else if(command.equals(COM_PATH)) {
				if(args != null) {
					String name = args.split("[ ]+", 2)[0];
					long id = Long.parseLong(name);
					System.out.println("Path from "+ currentRep.getComponentParameters().getName()+ ", for request "+ id);
					RequestPath rp = ((MonitorControl)current.getFcInterface(Constants.MONITOR_CONTROLLER)).getPathForID(new ComponentRequestID(id));
					RemmosUtils.displayPath(rp);
				}
				else {
					System.out.println("Usage: "+ COM_PATH +" <reqID>. Use command '"+ COM_OUT_REQ_LIST +"' to see a list of IDs.");
				}
			}
			// Add metrics from the metric library
			else if(command.equals(COM_ADD_METRIC)) {
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found != null) {
						if(input.length > 2) {
							String metricLibraryName = input[2];
							Metric<?> metric = MetricsLibrary.getInstance().getMetric(metricLibraryName);
							if(metric != null) {
								if(input.length > 3) {
									String metricName = input[3];
									String[] args;
									if(input.length > 4) {
										args = new String[input.length-4];
										for(int i=0; i<args.length; i++) {
											args[i] = input[i+4];
										}
									}
									else {
										args = null;
									}
									metric.setArgs(args);
									((MonitorControl)found.getFcInterface(Constants.MONITOR_CONTROLLER)).addMetric(metricName, metric);
									System.out.println("Metric "+metricName+" (type: "+metricLibraryName+") added to "+ name +".");
								}
								else {
									System.out.println("No name specified for the metric");	
									System.out.println("Usage: "+ COM_ADD_METRIC +" <component_name> <metric_library_name> <instanced_metric_name> <arguments>");
								}
							}
							else {
								System.out.println("Metric "+metricLibraryName+" not found on Metrics Library.");	
							}

						}
						else {
							System.out.println("No metric type specified.");
							System.out.println("Usage: "+ COM_ADD_METRIC +" <component_name> <metric_library_name> <instanced_metric_name> <arguments>");
						}
					}
					else {
						System.out.println("Component "+name+" not found.");
					}
				}
				else {
					System.out.println("Usage: "+ COM_ADD_METRIC +" <component> <metric_library_name> <instanced_metric_name> <arguments>");
				}
			}
			// Get the value of a metric on the specified component, without recalculate it
			else if(command.equals(COM_GET_METRIC)) {
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found != null) {
						if(input.length > 2) {
							String metricName = input[2];
							Object result = ((MonitorControl)found.getFcInterface(Constants.MONITOR_CONTROLLER)).getMetricValue(metricName);
							System.out.println(name+"."+metricName+" = "+ result + " ("+result.getClass()+")");
						}
						else {
							System.out.println("No metric specified.");
						}
					}
					else {
						System.out.println("Component "+name+" not found.");
					}
				}
				else {
					System.out.println("Usage: "+ COM_RUN_METRIC +" <component> <metric_name>");
				}
			}
			// Get value of all metrics on the specified component
			else if(command.equals(COM_GET_METRICS)) {
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found == null) {
						System.out.println("Component "+name+" not found.");
						continue;
					}
					List<String> metrics = ((MonitorControl)found.getFcInterface(Constants.MONITOR_CONTROLLER)).getMetricList();
					for(String metricName : metrics) {
						Object result = ((MonitorControl)found.getFcInterface(Constants.MONITOR_CONTROLLER)).getMetricValue(metricName);
						System.out.println(name+"."+metricName+" = "+ result + " ("+result.getClass()+")");
					}
				}
				else {
					System.out.println("Usage: "+ COM_RUN_METRICS +" <component>");
				}
			}
			// Calculate a metric on the specified component
			else if(command.equals(COM_RUN_METRIC)) {
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found != null) {
						if(input.length > 2) {
							String metricName = input[2];
							String args[] = null;
							// optional arguments are supplied for the metric (if there are arguments, the metric will be called with them)
							if(input.length > 3) {
								args = new String[input.length-3];
								for(int i=3; i<input.length; i++) {
									args[i-3] = input[i];
								}
								Object result = ((MonitorControl)found.getFcInterface(Constants.MONITOR_CONTROLLER)).runMetric(metricName, args);
								System.out.println(name+"."+metricName+" = "+ result + " ("+result.getClass()+")");
							}
							// no args supplied for the metric
							else {
								args = null;
								Object result = ((MonitorControl)found.getFcInterface(Constants.MONITOR_CONTROLLER)).runMetric(metricName);
								System.out.println(name+"."+metricName+" = "+ result + " ("+result.getClass()+")");
							}
						}
						else {
							System.out.println("No metric specified.");
						}
					}
					else {
						System.out.println("Component "+name+" not found.");
					}
				}
				else {
					System.out.println("Usage: "+ COM_RUN_METRIC +" <component> <metric_name>");
				}
			}
			// Calculate all metrics on the specified component
			else if(command.equals(COM_RUN_METRICS)) {
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found == null) {
						System.out.println("Component "+name+" not found.");
						continue;
					}
					List<String> metrics = ((MonitorControl)found.getFcInterface(Constants.MONITOR_CONTROLLER)).getMetricList();
					for(String metricName : metrics) {
						Object result = ((MonitorControl)found.getFcInterface(Constants.MONITOR_CONTROLLER)).runMetric(metricName);
						System.out.println(name+"."+metricName+" = "+ result + " ("+result.getClass()+")");
					}
				}
				else {
					System.out.println("Usage: "+ COM_RUN_METRICS +" <component>");
				}
			}
			// List metrics available from the metrics library			
			else if(command.equals(COM_LS_LIB_METRIC)) {
				System.out.println("Available metrics:");
				Set<String> list = MetricsLibrary.getInstance().getMetricList();
				for(String s : list) {
					System.out.println("   "+ s);
				}

			}
			// List metrics available in all components, or in the specified component
			else if(command.equals(COM_LS_METRIC)) {
				// args supplied --> calls on specified component
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found == null) {
						System.out.println("Component "+name+" not found.");
						continue;
					}
					RemmosUtils.displayMetrics(found);
				}
				// no args ... calls on all components
				else {
					for(Component comp : managedComponents.values()) {
						RemmosUtils.displayMetrics(comp);
					}
				}
			}
			// Add SLA Monitoring components 
			else if(command.equals(COM_ADD_SLA)) {
				// args supplied --> add monitoring to specified component
				if(args != null) {
					String name = input[1];
					Component found = managedComponents.get(name);
					if(found == null) {
						System.out.println("Component "+name+" not found.");
						continue;
					}
					System.out.println("Adding SLA monitoring components to "+ name +" ...");
					Remmos.addSLAMonitoring(found);
				}
				// no args ... add monitoring to all components
				else {
					for(String name : managedComponents.keySet()) {
						System.out.println("Adding SLA monitoring components to "+ name +" ...");
						Remmos.addSLAMonitoring(managedComponents.get(name));
					}
				}
			}
			// Add an SLO to the SLA Monitor
			else if(command.equals(COM_ADD_SLO)) {
				if(args != null) {
					if(input.length >= 6) {
						String name = input[1];
						Component found = managedComponents.get(name);
						if(found == null) {
							System.out.println("Component "+name+" not found.");
							continue;
						}
						String sloName = input[2];
						String[] sloArgs = new String[input.length-3];
						for(int i=3; i<input.length; i++) {
							sloArgs[i-3] = input[i];
						}
						((SLAService)found.getFcInterface(Constants.SLA_CONTROLLER)).addSLO(sloName, sloArgs);
					}
					else {
						System.out.println("Usage: "+ COM_ADD_SLO + " <componentName> <sloName> <metricType> [args]* <conditionName> <threshold>");
					}
				}
				else {
					System.out.println("Usage: "+ COM_ADD_SLO + " <componentName> <sloName> <metricType> [args]* <conditionName> <threshold>");
				}
			}
		}

		// Finish
		if(monitoring) {
			System.out.println("Stopping monitoring");
			for(Component comp : managedComponents.values()) {
				((MonitorControl)comp.getFcInterface(Constants.MONITOR_CONTROLLER)).stopMonitoring();
			}
			System.out.println("Monitoring Framework stopped.");
		}

		System.out.println("Console stoppped");
	}
	
	
	
	//--------------------------------- UTILS --------------------------------------------------------
	private void displayPrompt() {
		System.out.print(PROMPT);
	}
	
	private String[] readCommand() {
		String line = sc.nextLine();
		String[] result = line.split("[ ]+");
		return result;
	}
	
	private void displayHelp() {
		for(String[] command : commandDescriptions) {
			System.out.println("   "+command[0]+"     : "+command[1]);
		}
	}
	
	private void stopConsole() {
		running = false;
	}
	
	private void printBanner() {
		System.out.println();
		System.out.println("+-------------------------------------------------------------------------+");
		System.out.println("|   Monitoring and Management Console (but for now it's only monitoring)  |");
		System.out.println("+-------------------------------------------------------------------------+");
		System.out.println();
	}
	
	private void printNotImplemented() {
		System.out.println("This command is not implemented :(");
	}

	/**
	 * Finds components connected from this one, and adds them to the managedComponents map.
	 * 
	 * @param c
	 * @throws NoSuchInterfaceException
	 */
	private void discoverComponents(Component c) throws NoSuchInterfaceException {
		
		PASuperController pasc;
		PABindingController pabc = null;
		PAMulticastController pamc = null;
		PAComponentRepresentative pacr;
		PAComponent parent = null;
		InterfaceType[] interfaceTypes = null;
		String interfaceName;
		PAComponentRepresentative componentDest = null;
		String componentDestName = null;
		String hierarchicalType = null;
		
		if(!(c instanceof PAComponentRepresentative)) {
			System.out.println("Found component that is not an instance of PAComponentRepresentative");
			return;
		}
		pacr = (PAComponentRepresentative) c;
		logger.debug("Discovering from component "+ pacr.getComponentParameters().getName());
		
		// Get the Super Controller and the name of the parent component (if any)
		try {
			pasc = Utils.getPASuperController(c);
		} catch (NoSuchInterfaceException e) {
			pasc = null;
		}
		if(pasc == null) {
			return;
		}
		Component parents[] = pasc.getFcSuperComponents();
		if(parents.length > 0) {
			parent = ((PAComponent)parents[0]);
			logger.debug("   My parent is "+ parent.getComponentParameters().getName() );
		}
		else {
			logger.debug("   No parent");
		}
		
		// Get the Binding Controller of the component
		try {
			pabc = Utils.getPABindingController(pacr);
		} catch (NoSuchInterfaceException e) {
			pabc = null;
		}
		
		hierarchicalType = pacr.getComponentParameters().getHierarchicalType();

		// first, it checks the internal components
		if(hierarchicalType.equals(Constants.COMPOSITE)) {
			
			// a composite should have a Binding Controller
			if(pabc == null) {
				logger.debug("A composite without Binding Controller?");
				return;
			}
			
			// get all the server interfaces, and tries to find the connected internal components.
			interfaceTypes = pacr.getComponentParameters().getComponentType().getFcInterfaceTypes();
			
			for(InterfaceType itf : interfaceTypes) {
				// only for single server interfaces. Does not consider collective ones (yet)
				if( !itf.isFcClientItf() && ((PAGCMInterfaceType)itf).isGCMSingletonItf() && !((PAGCMInterfaceType)itf).isGCMCollectiveItf() ) {
					try {
						interfaceName = itf.getFcItfName();
						componentDest = (PAComponentRepresentative)((PAInterface) pabc.lookupFc(interfaceName)).getFcItfOwner();
						componentDestName = componentDest.getComponentParameters().getName();
						logger.debug("   Server interface (internal): "+ interfaceName + ", bound to "+ componentDestName);
					} catch (NoSuchInterfaceException e) {
						e.printStackTrace();
					}

					if(componentDest != null) {
						// if the component is not already added
						if(!managedComponents.containsKey(componentDestName)) {
							logger.debug("Adding component "+ componentDestName);
							managedComponents.put(componentDestName, componentDest);
						}
						// now it should continue with the destination component ...
						discoverComponents(componentDest);
					}
				}
			}
		}
		
		// then, it looks at the components bound through a client interface
		
		// if there is no Binding Controller, then we're in a Primitive without client interfaces, so we don't need to continue
		if(pabc == null) {
			return;
		}
		
		// Takes all the external client interfaces of the component, which are bound, and calls discover on each of the bound components.
		// If the bound component is the parent, then we don't call it.
		interfaceTypes = pacr.getComponentParameters().getComponentType().getFcInterfaceTypes();
		boolean foundParent;
		for(InterfaceType itf : interfaceTypes) {
			foundParent = false;
			// client singleton/multicast supported
			// TODO: support the others
			if(itf.isFcClientItf() /*&& ((PAGCMInterfaceType)itf).isGCMSingletonItf() && !((PAGCMInterfaceType)itf).isGCMCollectiveItf() */ ) {
				interfaceName = itf.getFcItfName();
				// get the component(s) bound to this interface ....
				Component destItfOwner = null;
				
				// For Multicast (at least client) interfaces
				if( ((PAGCMInterfaceType)itf).isGCMMulticastItf()) {
					logger.debug("   Client interface: "+ interfaceName + " is MULTICAST");
					pamc = Utils.getPAMulticastController(c);
					// get all the components bound to this Multicast interface
					Object[] destinations = pamc.lookupGCMMulticast(interfaceName);
					for(Object destination : destinations) {
						foundParent = false;
						destItfOwner = ((PAInterface)destination).getFcItfOwner();
						componentDest = (PAComponentRepresentative) destItfOwner;
						componentDestName = componentDest.getComponentParameters().getName();
						logger.debug("   Client MC interface: "+ interfaceName + ", bound to "+ componentDestName);
						// if the component destination is the same as the parent of the current component, 
						// then I don't need to continue from this interface
						if(componentDest.equals(parent)) {
							foundParent = true;
						}
						// if I find the internal interface of the parent, then I don't continue with him, otherwise I'll get into a cycle
						if(!foundParent) {
							if(componentDest != null) {
								// if the component is not already added
								if(!managedComponents.containsKey(componentDestName)) {
									logger.debug("Adding component "+ componentDestName);
									managedComponents.put(componentDestName, componentDest);
								}
								// now it should continue with the destination component ...
								discoverComponents(componentDest);
							}
						}
					}
				}
				
				// For Singleton interfaces
				if( ((PAGCMInterfaceType)itf).isGCMSingletonItf() ) {

					destItfOwner = ((PAInterface) pabc.lookupFc(interfaceName)).getFcItfOwner();
					// if the component is bound to a WSComponent (which is not a PAComponentRepresentative), we cannot monitor it.
					if(destItfOwner instanceof PAComponentRepresentative) {
						componentDest = (PAComponentRepresentative) destItfOwner;
						componentDestName = componentDest.getComponentParameters().getName();
						logger.debug("   Client interface: "+ interfaceName + ", bound to "+ componentDestName);
						// if the component destination is the same as the parent of the current component, 
						// then I don't need to continue from this interface
						if(componentDest.equals(parent)) {
							foundParent = true;
						}
						// if I find the internal interface of the parent, then I don't continue with him, otherwise I'll get into a cycle
						if(!foundParent) {
							if(componentDest != null) {
								// if the component is not already added
								if(!managedComponents.containsKey(componentDestName)) {
									logger.debug("Adding component "+ componentDestName);
									managedComponents.put(componentDestName, componentDest);
								}
								// now it should continue with the destination component ...
								discoverComponents(componentDest);
							}
						}
					}
				}
			}
		}

	}

	
}
