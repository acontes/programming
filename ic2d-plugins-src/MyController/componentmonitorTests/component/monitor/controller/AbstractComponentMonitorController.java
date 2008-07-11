package componentmonitorTests.component.monitor.controller;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.component.controller.AbstractProActiveController;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;
import org.objectweb.proactive.core.util.wrapper.GenericTypeWrapper;

import componentmonitorTests.component.monitor.controller.itf.ComponentMonitorController;

public abstract class AbstractComponentMonitorController extends
		AbstractProActiveController implements ComponentMonitorController {

	private static final long serialVersionUID = -7541365150366315513L;
	public static final String UNAVAILABLE = "Un-Available";
	public static final String COMPONENT_NAME = "component-name";
	public static final String COMPONENT_STATUS = "component-status";
	public static final String COMPONENT_HIERARCHIAL_TYPE = "component-hierarchical";
	
	public AbstractComponentMonitorController(Component owner) {
		super(owner);
	}

	@Override
	protected void setControllerItfType() {
		// TODO Auto-generated method stub
		try {
            setItfType(ProActiveTypeFactoryImpl.instance().createFcItfType(
            		ComponentMonitorController.CONTROLLER_NAME, ComponentMonitorController.class.getName(),
                    TypeFactory.SERVER, TypeFactory.OPTIONAL, TypeFactory.SINGLE));
        } catch (InstantiationException e) {
            throw new ProActiveRuntimeException("Cannot create controller type: " + getClass().getName());
        }

	}

	public abstract GenericTypeWrapper<?> execMonitor(String op, Object... params); 

	public abstract String[] listMetrics(); 

}
