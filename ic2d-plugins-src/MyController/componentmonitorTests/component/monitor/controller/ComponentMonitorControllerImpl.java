package componentmonitorTests.component.monitor.controller;

import org.objectweb.fractal.api.Component;
import org.objectweb.proactive.core.util.wrapper.GenericTypeWrapper;

import componentmonitorTests.component.monitor.controller.data.ComponentRepresentiveObject;
import componentmonitorTests.component.monitor.controller.data.ComponentRepresentiveObjectImpl;


public class ComponentMonitorControllerImpl extends AbstractComponentMonitorController {
    private static final long serialVersionUID = -7542365150366315513L;

    private ComponentRepresentiveObject CompR = null;

    public ComponentMonitorControllerImpl(Component owner) {
        super(owner);
    }

    //	public ComponentRepresentiveObject getCompR()
    //	{
    //		if(CompR == null)
    //		{
    //			try {
    //				this.CompR = new ComponentRepresentiveObjectImpl(owner);
    //			} catch (Exception e) {
    //				e.printStackTrace();
    //			}
    //		}
    //		return CompR;
    //	}

    @Override
    public GenericTypeWrapper<?> execMonitor(String op, Object... params) {
        // TODO Auto-generated method stub
        if (CompR == null) {
            try {
                this.CompR = new ComponentRepresentiveObjectImpl(owner);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        CompR.refresh();

        if (op.equalsIgnoreCase(COMPONENT_NAME)) {
            return getComponentName();
        } else if (op.equalsIgnoreCase(COMPONENT_STATUS)) {
            return getComponentStatus();
        } else if (op.equalsIgnoreCase(COMPONENT_HIERARCHIAL_TYPE)) {
            return getComponentHierarchicalType();
        } else {
            return null;
        }
    }

    @Override
    public String[] listMetrics() {
        // TODO Auto-generated method stub
        if (CompR == null) {
            try {
                this.CompR = new ComponentRepresentiveObjectImpl(owner);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return this.CompR.listMetricName();
    }

    private GenericTypeWrapper<String> getComponentName() {
        return new GenericTypeWrapper<String>((String) this.CompR.getMetricByName(COMPONENT_NAME).getObject());
    }

    private GenericTypeWrapper<String> getComponentStatus() {
        return new GenericTypeWrapper<String>((String) this.CompR.getMetricByName(COMPONENT_STATUS)
                .getObject());
    }

    private GenericTypeWrapper<String> getComponentHierarchicalType() {
        return new GenericTypeWrapper<String>((String) this.CompR.getMetricByName(COMPONENT_HIERARCHIAL_TYPE)
                .getObject());
    }

    public Component[] getSubComponents() {
        if (CompR == null) {
            try {
                this.CompR = new ComponentRepresentiveObjectImpl(owner);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this.CompR.listSubComponents();
    }
}
