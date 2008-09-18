package temp;

import java.io.Serializable;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.proactive.core.mop.MethodCall;

public class TraceProcessing implements Serializable {

	public void execute(MethodCall methodCall, Component source,
			Component target) {
		// TODO Auto-generated method stub
		
		try {
			if (source != null){
				System.err.println("[debug] TraceProcessing.execute(): source "+source.getClass()+"destination "+target.getClass());
			System.err.println("[debug] TraceProcessing.execute(): **source** "
					+ org.objectweb.fractal.util.Fractal
							.getNameController(source).getFcName());
			}
			if (methodCall != null)
				System.err.println("[debug] TraceProcessing.execute(): **method Call** "
						+ methodCall.getName());
				if (target != null)
				System.err.println("[debug] TraceProcessing.execute(): **target** "
						+ org.objectweb.fractal.util.Fractal.getNameController(
								target).getFcName());
		} catch (NoSuchInterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
