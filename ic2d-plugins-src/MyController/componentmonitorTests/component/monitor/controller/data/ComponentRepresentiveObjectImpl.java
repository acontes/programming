package componentmonitorTests.component.monitor.controller.data;

import java.io.Serializable;
import java.util.HashMap;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.ContentController;
import org.objectweb.fractal.api.control.IllegalContentException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ProActiveInterface;
//import org.objectweb.proactive.core.component.controller.ComponentParametersController;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.util.wrapper.GenericTypeWrapper;

import componentmonitorTests.component.monitor.controller.AbstractComponentMonitorController;
import componentmonitorTests.component.monitor.controller.itf.ComponentMonitorController;

public class ComponentRepresentiveObjectImpl extends
		AbstractComponentRepresentiveObject implements NotificationListener,
		Serializable {

	private static final long serialVersionUID = -7541362340216315513L;

	public HashMap<String, GenericTypeWrapper> MnameToMvalue = new HashMap<String, GenericTypeWrapper>();

	private HashMap<String, ProActiveInterface> itfNameToItf = new HashMap<String, ProActiveInterface>();

	private HashMap<Component, ComponentMonitorController> CompToMonitorCtr = new HashMap<Component, ComponentMonitorController>();

	private boolean isControllerInitialized = false;

	private ContentController ownerCC = null;

	private LifeCycleController ownerLCC = null;

	private Component owner = null;

	public ComponentRepresentiveObjectImpl(Component owner)
			throws NoSuchInterfaceException, IllegalLifeCycleException,
			IllegalContentException {
		// initial these hash maps by owner, use the other controllers to get
		// the information
		// infuture, use Jmx to monitor more metrics
		this.owner =  owner;
		initializeController();

		// just test
	}

	/**
	 * initialize the some controller and the monitor controller of subcomponent
	 * 
	 */
	private void initializeController() throws 
			IllegalLifeCycleException, IllegalContentException {
		// Initialize the controller: ContentController and LifeCycleController
		// Maybe infuture for other controllers
		try {
			ownerLCC = (LifeCycleController) owner
					.getFcInterface(Constants.LIFECYCLE_CONTROLLER);
		} 
		catch (NoSuchInterfaceException e) {
//			e.printStackTrace();
		}
		

		try {
			ownerCC = (ContentController) owner
					.getFcInterface(Constants.CONTENT_CONTROLLER);
		} catch (NoSuchInterfaceException e) {
//			e.printStackTrace();
		}

		isControllerInitialized = true;

		// Component[] cmps = ownerCC.getFcSubComponents();
		// for (Component c : cmps) {
		//
		// if (c instanceof ProActiveComponent) {
		// ProActiveComponentRepresentative cRep;
		// cRep = (ProActiveComponentRepresentative) ((ProActiveComponent) c)
		// .getRepresentativeOnThis();
		//
		// try {
		// ComponentMonitorController cmc;
		// cmc = (ComponentMonitorController) cRep
		// .getFcInterface(ComponentMonitorController.CONTROLLER_NAME);
		// CompToMonitorCtr.put(c, cmc);
		// } catch (NoSuchInterfaceException e) {
		//
		// }
		// }
		// }
	}

	private void setFcName() throws NoSuchInterfaceException {
		NameController NameC = (NameController) this.owner
				.getFcInterface(Constants.NAME_CONTROLLER);
		MnameToMvalue.put(AbstractComponentMonitorController.COMPONENT_NAME,
				new GenericTypeWrapper<String>(NameC.getFcName()));
	}

	private void setFcStatus() {
		try {
			if (!isControllerInitialized)
				initializeController();
			if (ownerLCC != null) {
				MnameToMvalue.put(
						AbstractComponentMonitorController.COMPONENT_STATUS,
						new GenericTypeWrapper<String>(ownerLCC.getFcState()));
			} else {
				MnameToMvalue.put(
								AbstractComponentMonitorController.COMPONENT_STATUS,
								new GenericTypeWrapper<String>(
										AbstractComponentMonitorController.UNAVAILABLE));
			}
		} catch (Exception e) {
			MnameToMvalue.put(
					AbstractComponentMonitorController.COMPONENT_STATUS,
					new GenericTypeWrapper<String>(
							AbstractComponentMonitorController.UNAVAILABLE));
		}
	}

//	private void setHierarchicalType() throws NoSuchInterfaceException {
//		String HierarchicalType = ((ComponentParametersController) this.owner
//				.getFcInterface(Constants.COMPONENT_PARAMETERS_CONTROLLER))
//				.getComponentParameters().getHierarchicalType();
//		MnameToMvalue.put(
//				AbstractComponentMonitorController.COMPONENT_HIERARCHIAL_TYPE,
//				new GenericTypeWrapper<String>(HierarchicalType));
//	}

	@Override
	public GenericTypeWrapper<?> getMetricByName(String MetricName) {
		// TODO Auto-generated method stub
		return MnameToMvalue.get(MetricName);
	}

	@Override
	public Component[] listSubComponents() {
		// TODO Auto-generated method stub
		try {
			if (!isControllerInitialized)
				initializeController();
		} catch (Exception e) {
			return null;
		}
		if (this.ownerCC == null) {
			// this is primitive component
			return null;
		}
		return this.ownerCC.getFcSubComponents();
	}

	@Override
	public String[] listMetricName() {
		// TODO Auto-generated method stub
		return this.MnameToMvalue.keySet().toArray(
				new String[MnameToMvalue.size()]);
	}

	@Override
	/**
	 * refresh the metric values
	 */
	public void refresh() {
		// TODO Auto-generated method stub
		
		try
		{
			setFcName();
			setFcStatus();
//			setHierarchicalType();
		}
		catch(Exception e)
		{
			
		}
		
	}

	/**
	 * handle the notification from the MBean, parser the information and store
	 * them in the MnameToMvalue
	 */
	public void handleNotification(Notification noti, Object handback) {

	}

}
