package nonregressiontest.component;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;

import testsuite.test.Assertions;

public class PrimitiveComponentE implements I1, BindingController {
	
	public static final String MESSAGE="pE";
	
	Map<String, I2> clientItfs = new HashMap<String, I2>();

	public Message processInputMessage(Message message) {
		for (Iterator iter = clientItfs.keySet().iterator(); iter.hasNext();) {
			I2 itf = (I2) iter.next();
			System.out.println("E: transferring message :" + message.toString());
			Assertions.assertEquals(PrimitiveComponentE.MESSAGE+PrimitiveComponentB.MESSAGE, itf.processOutputMessage(new Message(PrimitiveComponentE.MESSAGE)).toString());
			
		}
		return null;
	}

	public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException {
		if (clientItfName.startsWith("i2") && !clientItfName.equals("i2")) {
			clientItfs.put(clientItfName, (I2)serverItf);
		} else {
			throw new NoSuchInterfaceException (clientItfName);
		}
		
		
	}

	public String[] listFc() {
		// TODO Auto-generated method stub
		return null;
	}

	public Object lookupFc(String arg0) throws NoSuchInterfaceException {
		// TODO Auto-generated method stub
		return null;
	}

	public void unbindFc(String arg0) throws NoSuchInterfaceException, IllegalBindingException, IllegalLifeCycleException {
		// TODO Auto-generated method stub
		
	}
	
	

}
