package org.objectweb.proactive.core.component.componentcontroller.remmos;

import java.util.ArrayList;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.component.Utils;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.EventControl;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.EventListener;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.LogHandler;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.LogStore;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControl;
import org.objectweb.proactive.core.component.componentcontroller.monitoring.MonitorControlImpl;
import org.objectweb.proactive.core.component.control.PABindingController;
import org.objectweb.proactive.core.component.control.PABindingControllerImpl;
import org.objectweb.proactive.core.component.control.PAContentController;
import org.objectweb.proactive.core.component.control.PAContentControllerImpl;
import org.objectweb.proactive.core.component.control.PAGCMLifeCycleController;
import org.objectweb.proactive.core.component.control.PAMembraneController;
import org.objectweb.proactive.core.component.control.PASuperController;
import org.objectweb.proactive.core.component.control.PASuperControllerImpl;
import org.objectweb.proactive.core.component.factory.PAGenericFactory;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.type.PAGCMInterfaceType;
import org.objectweb.proactive.core.component.type.PAGCMTypeFactory;


/**
 * This is an utility class used to instantiate "monitorable" and "manageable" components.
 * 
 * @author cruz
 *
 */
public class Remmos {

	// Monitor-related Components
	public static final String EVENT_LISTENER_COMP = "event-listener-NF";
	public static final String LOG_STORE_COMP = "log-store-NF";
	public static final String MONITOR_SERVICE_COMP = "monitor-service-NF";
	
	// Interfaces
	public static final String EVENT_CONTROL_ITF = "event-control-nf";
	public static final String LOG_HANDLER_ITF = "log-handler-nf";
	public static final String MONITOR_SERVICE_ITF = "monitor-service-nf";
	
	/**
	 * Creates the NF interfaces that will be used for the Monitoring and Management framework (implemented as components).
	 * 
	 * @param pagcmTf
	 * @param fItfType
	 * @return
	 */ 
	public static PAGCMInterfaceType[] createMonitorableNFType(PAGCMTypeFactory pagcmTf, PAGCMInterfaceType[] fItfType, String hierarchy) {

		ArrayList<PAGCMInterfaceType> typeList = new ArrayList<PAGCMInterfaceType>();
		
		PAGCMInterfaceType type[] = null;
		PAGCMInterfaceType pagcmItfType = null;
		
		// Normally, the NF interfaces mentioned here should be those that are going to be implemented by NF components,
		// and the rest of the NF interfaces (that are going to be implemented by object controller) should be in a ControllerDesc file.
		// But the PAComponentImpl ignores the NFType if there is a ControllerDesc file specified :(,
		// so I better put all the NF interfaces here.
		// That means that I need another method to add the object controllers for the not yet created controllers.
		try {
			// Object controller-managed server interfaces.
			typeList.add((PAGCMInterfaceType) pagcmTf.createGCMItfType(Constants.CONTENT_CONTROLLER, PAContentController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));
			typeList.add((PAGCMInterfaceType) pagcmTf.createGCMItfType(Constants.BINDING_CONTROLLER, PABindingController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));
			typeList.add((PAGCMInterfaceType) pagcmTf.createGCMItfType(Constants.LIFECYCLE_CONTROLLER, PAGCMLifeCycleController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));
			typeList.add((PAGCMInterfaceType) pagcmTf.createGCMItfType(Constants.SUPER_CONTROLLER, PASuperController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));
			typeList.add((PAGCMInterfaceType) pagcmTf.createGCMItfType(Constants.NAME_CONTROLLER, NameController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));
			typeList.add((PAGCMInterfaceType) pagcmTf.createGCMItfType(Constants.MEMBRANE_CONTROLLER, PAMembraneController.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));
			
			// server Monitoring interface
			typeList.add((PAGCMInterfaceType) pagcmTf.createGCMItfType(Constants.MONITOR_CONTROLLER, MonitorControl.class.getName(), TypeFactory.SERVER, TypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY));
			// TODO the NF interfaces for SLAManagement and Actions
			
			// external client Monitoring interfaces
			// add one client Monitoring interface for each client binding (maybe optional or mandatory)
			// collective and multicast/gathercast interfaces not supported (yet)
			String itfName;
			for(int i=0; i<fItfType.length; i++) {
				// only client-singleton supported ... others ignored
				if(fItfType[i].isFcClientItf() && fItfType[i].isGCMSingletonItf() && !fItfType[i].isGCMCollectiveItf()) {
					itfName = fItfType[i].getFcItfName() + "-external-"+Constants.MONITOR_CONTROLLER;
					pagcmItfType = (PAGCMInterfaceType) pagcmTf.createGCMItfType(itfName, MonitorControl.class.getName(), TypeFactory.CLIENT, TypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY);
					typeList.add(pagcmItfType);
				}
				
			}
			
			// composites have also internal client and server bindings
			if(Constants.COMPOSITE.equals(hierarchy)) {
				// one client internal Monitoring interface for each server binding
				// collective and multicast/gathercast interfaces not supported (yet)
				for(int i=0; i<fItfType.length; i++) {
					// only server-singleton supported ... others ignored
					if(!fItfType[i].isFcClientItf() && fItfType[i].isGCMSingletonItf() && !fItfType[i].isGCMCollectiveItf()) {
						itfName = fItfType[i].getFcItfName() + "-internal-"+Constants.MONITOR_CONTROLLER;
						pagcmItfType = (PAGCMInterfaceType) pagcmTf.createGCMItfType(itfName, MonitorControl.class.getName(), TypeFactory.CLIENT, TypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY, PAGCMTypeFactory.INTERNAL);
						typeList.add(pagcmItfType);
					}
				}
				// one server internal Monitoring interface in each composite
				itfName = "internal-server-"+Constants.MONITOR_CONTROLLER;
				pagcmItfType = (PAGCMInterfaceType) pagcmTf.createGCMItfType(itfName, MonitorControl.class.getName(), TypeFactory.SERVER, TypeFactory.OPTIONAL, PAGCMTypeFactory.SINGLETON_CARDINALITY, PAGCMTypeFactory.INTERNAL);
				typeList.add(pagcmItfType);
			}
			
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		type = (PAGCMInterfaceType[]) typeList.toArray(new PAGCMInterfaceType[typeList.size()]);
		return type;
	}
	
	/**
	 * Adds the controller objects to the NF interfaces that are not part of the M&M Framework.
	 * Normally, PAComponentImpl.addMandatoryControllers should have added already the mandatory MEMBRANE, LIFECYCLE and NAME controllers.
	 * Interfaces like BINDING and CONTENT, which are not supposed to be in all components, should have been removed from the component NFType in the appropriate cases.
	 * 
	 * @param component
	 */
	public static void addObjectControllers(PAComponent component) {

		PAMembraneController memb = null;
		try {
			memb = Utils.getPAMembraneController(component);
		} catch (NoSuchInterfaceException e) {
			// Non-existent interfaces have been ignored at component creation time.
		}
		// add the remaining object controllers
		try {
			 // this call is just to catch the exception. If the exception is generated in the next line, for some reason I can't catch it here.
			component.getFcInterface(Constants.CONTENT_CONTROLLER);
			memb.setControllerObject(Constants.CONTENT_CONTROLLER, PAContentControllerImpl.class.getName());
		} catch (NoSuchInterfaceException e) {
			// Non-existent interfaces have been ignored at component creation time.
		}
		try {
			component.getFcInterface(Constants.BINDING_CONTROLLER);
			memb.setControllerObject(Constants.BINDING_CONTROLLER, PABindingControllerImpl.class.getName());
		} catch (NoSuchInterfaceException e) {
			// Non-existent interfaces have been ignored at component creation time.
		}
		try {
			component.getFcInterface(Constants.SUPER_CONTROLLER);
			memb.setControllerObject(Constants.SUPER_CONTROLLER, PASuperControllerImpl.class.getName());
		} catch (NoSuchInterfaceException e) {
			// Non-existent interfaces have been ignored at component creation time.
		}
		// LIFECYCLE is mandatory and should have been added at component creation time
		// NAME      is mandatory and should have been added at component creation time
	}
	
	/**
	 * Builds the monitoring components and put them in the membrane.
	 * The functional assembly must be done before, otherwise the internal assemblies will be incomplete.
	 * 
	 * @param component
	 * @throws Exception 
	 */
	public static void addMonitoring(Component component) throws Exception {

		// bootstrapping component and factories
		Component boot = Fractal.getBootstrapComponent();
		PAGCMTypeFactory patf = null;
		PAGenericFactory pagf = null;
		patf = (PAGCMTypeFactory) Fractal.getTypeFactory(boot);
		pagf = (PAGenericFactory) Fractal.getGenericFactory(boot);

		// creates the components used for monitoring
		Component eventListener = createBasicEventListener(patf, pagf, EventListener.class.getName());
		Component logStore = createBasicLogStore(patf, pagf, LogStore.class.getName());
		Component monitorService = createMonitorService(patf, pagf, MonitorControlImpl.class.getName(), component);

		// performs the NF assembly
		PAMembraneController membrane = Utils.getPAMembraneController(component);
		// stop the membrane before making changes
		String membraneOldState = membrane.getMembraneState();
		membrane.stopMembrane();
		
		
		// add components to the membrane
		membrane.addNFSubComponent(eventListener);
		membrane.addNFSubComponent(logStore);
		membrane.addNFSubComponent(monitorService);
		// bindings between NF components
		membrane.bindNFc(MONITOR_SERVICE_COMP+"."+EVENT_CONTROL_ITF, EVENT_LISTENER_COMP+"."+EVENT_CONTROL_ITF);
		membrane.bindNFc(MONITOR_SERVICE_COMP+"."+LOG_HANDLER_ITF, LOG_STORE_COMP+"."+LOG_HANDLER_ITF);
		membrane.bindNFc(EVENT_LISTENER_COMP+"."+LOG_HANDLER_ITF, LOG_STORE_COMP+"."+LOG_HANDLER_ITF);
		// binding between the NF Monitoring Interface of the host component, and the Monitor Component
		membrane.bindNFc(Constants.MONITOR_CONTROLLER, MONITOR_SERVICE_COMP+"."+MONITOR_SERVICE_ITF);
		// bindings between the Monitor Component and the external client NF monitoring interfaces
		// one binding from MONITOR_SERVICE_COMP for each client binding (maybe optional or mandatory)
		// collective and multicast/gathercast interfaces not supported (yet)
		String itfName;
		String clientItfName;
		String serverItfName;
		InterfaceType[] fItfType = ((PAComponent) component).getComponentParameters().getInterfaceTypes();
		for(int i=0; i<fItfType.length; i++) {
			// only client-singleton supported ... others ignored
			if(fItfType[i].isFcClientItf() && ((PAGCMInterfaceType)fItfType[i]).isGCMSingletonItf() && !((PAGCMInterfaceType)fItfType[i]).isGCMCollectiveItf()) {
				itfName = fItfType[i].getFcItfName();
				clientItfName = itfName+"-external-"+MONITOR_SERVICE_ITF;
				serverItfName = itfName+"-external-"+Constants.MONITOR_CONTROLLER;
				membrane.bindNFc(MONITOR_SERVICE_COMP+"."+clientItfName, serverItfName);
			}
		}
		// the composites need additional bindings with their internal monitoring interfaces
		if(Constants.COMPOSITE.equals(((PAComponent) component).getComponentParameters().getHierarchicalType())) {
			// one client internal Monitoring interface for each server binding
			// collective and multicast/gathercast interfaces not supported (yet)
			for(int i=0; i<fItfType.length; i++) {
				// only server-singleton supported ... others ignored
				if(!fItfType[i].isFcClientItf() && ((PAGCMInterfaceType)fItfType[i]).isGCMSingletonItf() && !((PAGCMInterfaceType)fItfType[i]).isGCMCollectiveItf()) {
					itfName = fItfType[i].getFcItfName();
					clientItfName = itfName+"-internal-"+MONITOR_SERVICE_ITF;
					serverItfName = itfName+"-internal-"+Constants.MONITOR_CONTROLLER;
					membrane.bindNFc(MONITOR_SERVICE_COMP+"."+clientItfName, serverItfName);
				}
			}
			// and the binding from the internal server monitor interface, back to the NF Monitor Component
			clientItfName = "internal-server-"+Constants.MONITOR_CONTROLLER;
			serverItfName = MONITOR_SERVICE_ITF;
			membrane.bindNFc(clientItfName, MONITOR_SERVICE_COMP+"."+serverItfName);
		}
		
		
		// restore membrane state after having made changes
		if(membraneOldState.equals(PAMembraneController.MEMBRANE_STARTED)) {
			membrane.startMembrane();
		}		
		
		
	}
	
	/**
	 * Creates the NF Event Listener component.
	 * @param patf
	 * @param pagf
	 * @return
	 */
	private static Component createBasicEventListener(PAGCMTypeFactory patf, PAGenericFactory pagf, String eventListenerClass) {
		
		Component eventListener = null;
		InterfaceType[] eventListenerItfType = null;
		ComponentType eventListenerType = null;
		
		try {
			eventListenerItfType = new InterfaceType[] {
					patf.createGCMItfType(EVENT_CONTROL_ITF, EventControl.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY),
					patf.createGCMItfType(LOG_HANDLER_ITF, LogHandler.class.getName(), TypeFactory.CLIENT, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY)
			};
			eventListenerType = patf.createFcType(eventListenerItfType);
			eventListener = pagf.newNFcInstance(eventListenerType,
					new ControllerDescription(EVENT_LISTENER_COMP, Constants.PRIMITIVE, "/org/objectweb/proactive/core/component/componentcontroller/config/default-component-controller-config-basic.xml"),
					new ContentDescription(eventListenerClass)
			);
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
			
		return eventListener;
	}

	/**
	 * Creates the NF Log Store component
	 * @param patf
	 * @param pagf
	 * @param logStoreClass
	 * @return
	 */
	private static Component createBasicLogStore(PAGCMTypeFactory patf, PAGenericFactory pagf, String logStoreClass) {
		
		Component logStore = null;
		InterfaceType[] logStoreItfType = null;
		ComponentType logStoreType = null;
		
		try {
			logStoreItfType = new InterfaceType[] {
				patf.createGCMItfType(LOG_HANDLER_ITF, LogHandler.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY)
			};
		logStoreType = patf.createFcType(logStoreItfType);
		logStore = pagf.newNFcInstance(logStoreType, 
				new ControllerDescription(LOG_STORE_COMP, Constants.PRIMITIVE, "/org/objectweb/proactive/core/component/componentcontroller/config/default-component-controller-config-basic.xml"), 
				new ContentDescription(logStoreClass)
		);
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		
		return logStore;
	}
	
	/**
	 * Creates the NF Monitor Service component
	 * @param patf
	 * @param pagf
	 * @param monitorServiceClass
	 * @param component
	 * @return
	 */
	private static Component createMonitorService(PAGCMTypeFactory patf, PAGenericFactory pagf, String monitorServiceClass, Component component) {

		Component monitorService = null;
		InterfaceType[] monitorServiceItfType = null;
		ComponentType monitorServiceType = null;
		ArrayList<InterfaceType> monitorServiceItfTypeList = new ArrayList<InterfaceType>();
		
		// TODO create the interface type, according to the client/server functional interfaces on the component
		try {
			monitorServiceItfTypeList.add(patf.createGCMItfType(LOG_HANDLER_ITF, LogHandler.class.getName(), TypeFactory.CLIENT, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));
			monitorServiceItfTypeList.add(patf.createGCMItfType(EVENT_CONTROL_ITF, EventControl.class.getName(), TypeFactory.CLIENT, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));
			monitorServiceItfTypeList.add(patf.createGCMItfType(MONITOR_SERVICE_ITF, MonitorControl.class.getName(), TypeFactory.SERVER, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		 
		// external client Monitoring interfaces
		// add one client Monitoring interface for each client binding (maybe optional or mandatory)
		// collective and multicast/gathercast interfaces not supported (yet)
		String itfName;
		InterfaceType[] fItfType = ((PAComponent) component).getComponentParameters().getInterfaceTypes();
		for(int i=0; i<fItfType.length; i++) {
			// only client-singleton supported ... others ignored
			if(fItfType[i].isFcClientItf() && ((PAGCMInterfaceType)fItfType[i]).isGCMSingletonItf() && !((PAGCMInterfaceType)fItfType[i]).isGCMCollectiveItf()) {
				itfName = fItfType[i].getFcItfName() + "-external-"+MONITOR_SERVICE_ITF;
				try {
					monitorServiceItfTypeList.add(patf.createGCMItfType(itfName, MonitorControl.class.getName(), TypeFactory.CLIENT, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));
				} catch (InstantiationException e) {
					e.printStackTrace();
				}
			}
		}
		
		// composites also require client interfaces for internal bindings
		if(Constants.COMPOSITE.equals(((PAComponent) component).getComponentParameters().getHierarchicalType())) {
			// one client internal Monitoring interface for each server binding
			// collective and multicast/gathercast interfaces not supported (yet)
			for(int i=0; i<fItfType.length; i++) {
				// only server-singleton supported ... others ignored
				if(!fItfType[i].isFcClientItf() && ((PAGCMInterfaceType)fItfType[i]).isGCMSingletonItf() && !((PAGCMInterfaceType)fItfType[i]).isGCMCollectiveItf()) {
					itfName = fItfType[i].getFcItfName() + "-internal-"+MONITOR_SERVICE_ITF;
					try {
						monitorServiceItfTypeList.add(patf.createGCMItfType(itfName, MonitorControl.class.getName(), TypeFactory.CLIENT, TypeFactory.MANDATORY, PAGCMTypeFactory.SINGLETON_CARDINALITY));					
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		monitorServiceItfType = monitorServiceItfTypeList.toArray(new InterfaceType[monitorServiceItfTypeList.size()]);
		try {
			monitorServiceType = patf.createFcType(monitorServiceItfType);
			monitorService = pagf.newNFcInstance(monitorServiceType,
					new ControllerDescription(MONITOR_SERVICE_COMP, Constants.PRIMITIVE, "/org/objectweb/proactive/core/component/componentcontroller/config/default-component-controller-config-basic.xml"),
					new ContentDescription(monitorServiceClass)
			);
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		
		
		return monitorService;
	}
	
	
	/**
	 * Starts monitoring in this component and all its connections.
	 * Bindings are created if necessary.
	 * 
	 * @param component
	 */
	public static void enableMonitoring(Component component) {
		
		
		
	}
}
