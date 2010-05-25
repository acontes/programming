package functionalTests.component.sca.components;

import org.objectweb.fractal.api.control.AttributeController;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


public interface C2Attribute extends AttributeController {
    BooleanWrapper getX1();

    void setX1(BooleanWrapper x);
}
