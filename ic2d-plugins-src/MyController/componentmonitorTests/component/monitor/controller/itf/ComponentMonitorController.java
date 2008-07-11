package componentmonitorTests.component.monitor.controller.itf;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.util.wrapper.GenericTypeWrapper;

import componentmonitorTests.component.monitor.controller.data.ComponentRepresentiveObject;

public interface ComponentMonitorController {

	 public static final String CONTROLLER_NAME = "monitor-controller";

	 public String[] listMetrics();

	 public GenericTypeWrapper<?> execMonitor(String op, Object... params);
	 
//	 public ComponentRepresentiveObject getCompR();
	 
	 public Component[] getSubComponents();
}
