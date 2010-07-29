package functionalTests.component.sca.components;

import org.osoa.sca.annotations.Property;
//@snippet-start component_scauserguide_1

public class PropertyControllerTestComp {
    @Property
    public boolean x1;
    @Property
    protected byte x2;
    @Property
    private char x3;

    public PropertyControllerTestComp() {
        // TODO Auto-generated constructor stub
    }

    public String toString() {
        return "the properties inside this class : " + x1 + " x2 " + x2 + " x3 " + x3;
    }
}

//@snippet-end component_scauserguide_1