package componentmonitorTests.component.monitor.controller.data;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.util.wrapper.GenericTypeWrapper;

public interface ComponentRepresentiveObject {
	public GenericTypeWrapper<?> getMetricByName(String MetricName);
	public String[] listMetricName();
	public Component[] listSubComponents();
	public void refresh();
}
