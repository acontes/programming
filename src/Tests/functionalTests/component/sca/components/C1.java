package functionalTests.component.sca.components;

import org.osoa.sca.annotations.Property;

public class C1 {
	@Property
	public boolean x1;
	@Property
	protected byte x2;
	@Property
	protected char x3;
    public C1() {
		// TODO Auto-generated constructor stub
	}
    public String toString()
    {
    	return "the shits inside this class : "+ x1 + " x2 "+ x2 + " x3 " + x3 ;
    }
}
