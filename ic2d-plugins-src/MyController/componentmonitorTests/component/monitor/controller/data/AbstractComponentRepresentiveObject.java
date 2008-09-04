package componentmonitorTests.component.monitor.controller.data;

import java.io.Serializable;
import java.util.HashMap;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.component.ProActiveInterface;
import org.objectweb.proactive.core.util.wrapper.GenericTypeWrapper;


public abstract class AbstractComponentRepresentiveObject implements Serializable,
        ComponentRepresentiveObject {

    public abstract GenericTypeWrapper<?> getMetricByName(String MetricName);

    public abstract String[] listMetricName();

    public abstract Component[] listSubComponents();

    public abstract void refresh();

}
