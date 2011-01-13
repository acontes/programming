package org.objectweb.proactive.extensions.sca.representative;

import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.proactive.core.component.ComponentParameters;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.identity.PAComponent;
import org.objectweb.proactive.core.component.representative.PAComponentRepresentative;
import org.objectweb.proactive.core.component.representative.PAComponentRepresentativeFactory;
import org.objectweb.proactive.core.component.representative.PAComponentRepresentativeImpl;
import org.objectweb.proactive.core.component.request.ComponentRequest;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.Proxy;

public class PA_SCAComponentRepresentativeFactory{
	private static PA_SCAComponentRepresentativeFactory INSTANCE = null;
    private PA_SCAComponentRepresentativeFactory() {
    }

    /**
     * returns the unique instance in the jvm
     * @return the unique instance in the jvm
     */
    public static PA_SCAComponentRepresentativeFactory instance() {
        if (INSTANCE == null) {
            return (INSTANCE = new PA_SCAComponentRepresentativeFactory());
        } else {
            return INSTANCE;
        }
    }
    
    /**
     * Creates a component representative according to the type of the component
     * (it also generates the required functional interfaces), and connects the representative to
     * the given proxy. It also takes into account a controller config file for generating references to
     * the implementations of the controllers of this component.
     * @param componentType the type of the component
     * @param proxy the proxy to the active object
     * @param controllerConfigFileLocation location of a file that contains the description of the controllers for this component. null will load the default configuration
     * @return a corresponding component representative
     */
    public PAComponentRepresentative createComponentRepresentative(ComponentType componentType,
            String hierarchicalType, Proxy proxy, String controllerConfigFileLocation) {
    	PAComponentRepresentative representative = new PA_SCAComponentRepresentativeImpl(componentType,
                hierarchicalType, controllerConfigFileLocation);
            representative.setProxy(proxy);
            return representative;
    }
    
    public PAComponentRepresentative createComponentRepresentative(ComponentParameters params, Proxy proxy) {
        PAComponentRepresentative representative = new PA_SCAComponentRepresentativeImpl(params);
        representative.setProxy(proxy);
        return representative;
    }

    /**
     * The creation of a component representative from a proxy object implies a remote invocation (immediate service) for
     * getting the parameters of the component, necessary for the construction of the representative
     * @param proxy a reference on a proxy pointing to a component
     * @return a component representative for the pointed component
     * @throws Throwable an exception
     */
    public PAComponentRepresentative createComponentRepresentative(Proxy proxy) throws Throwable {
        // set immediate service for getComponentParameters
        System.out.println("PA_SCAComponentRepresentativeFactory.createComponentRepresentative()");
        proxy.reify(MethodCall.getComponentMethodCall(PAComponent.class.getDeclaredMethod(
                "setImmediateServices", new Class[] {}), new Object[] {}, null, Constants.COMPONENT, null,
                ComponentRequest.STRICT_FIFO_PRIORITY));

        ComponentParameters componentParameters = (ComponentParameters) proxy.reify(MethodCall
                .getComponentMethodCall(PAComponent.class.getDeclaredMethod("getComponentParameters",
                        new Class[] {}), new Object[] {}, null, Constants.COMPONENT, null,
                        ComponentRequest.STRICT_FIFO_PRIORITY));

        return PA_SCAComponentRepresentativeFactory.instance().createComponentRepresentative(componentParameters,
                proxy);
    }
}
