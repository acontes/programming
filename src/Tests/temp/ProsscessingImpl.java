package temp;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.NameController;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;

import org.objectweb.proactive.core.mop.MethodCall;

public class ProsscessingImpl {
	static private int i=0;
	public void execute(Component comp, MethodCall methodCall) {
		// TODO Auto-generated method stub	
		try {
			System.out.println("");
			System.out
			.println(i+" [debug] OutputProcessingImpl.execute(): component name )))))"+
					((NameController)comp.getFcInterface("name-controller")).getFcName());
		} catch (NoSuchInterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out
		.println(i+" [debug] OutputProcessingImpl.execute(): method call name )))))"+
				methodCall.getName());
		System.out.println("");
		i++;
	}

}
