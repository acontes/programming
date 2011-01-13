/**
 * 
 */
package org.objectweb.proactive.extensions.sca.representative;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.proactive.core.component.ComponentParameters;
import org.objectweb.proactive.core.component.PAInterface;
import org.objectweb.proactive.core.component.representative.PAComponentRepresentativeImpl;
import org.objectweb.proactive.extensions.sca.Constants;
import org.objectweb.proactive.extensions.sca.control.SCAIntentController;
import org.objectweb.proactive.extensions.sca.exceptions.ClassGenerationFailedException;
import org.objectweb.proactive.extensions.sca.gen.IntentServiceItfGenerator;


/**
 * @author mug
 *
 */
public class PA_SCAComponentRepresentativeImpl extends PAComponentRepresentativeImpl {

	public PA_SCAComponentRepresentativeImpl(ComponentParameters componentParam) {
		super(componentParam);
	}
	public PA_SCAComponentRepresentativeImpl(ComponentType componentType, String hierarchicalType,
            String controllersConfigFileLocation) {
		super(componentType, hierarchicalType, controllersConfigFileLocation);
	}
	
	/*
     * @see org.objectweb.proactive.core.component.representative.PAComponentRepresentativeImpl#getFcInterface(String)
     */
	public Object getFcInterface(String interfaceName) throws NoSuchInterfaceException {
		Object itfObjetct = super.getFcInterface(interfaceName);
//    	System.err.println(stubOnBaseObject.getClass().getName()+" DEBUG "+interfaceName);
//    	if(interfaceName.endsWith("intent-controller"))
//    	{
//    		try {
//				throw new Exception("shit1");
//			} catch (Exception e) {
//				System.err.println(stubOnBaseObject.getClass().getName()+" DEBUG "+interfaceName+" exception catched!");
//				e.printStackTrace();
//			}
//    	}
		
		if (fcInterfaceReferences.containsKey(interfaceName)) {
			SCAIntentController scaic = (SCAIntentController) super.getFcInterface(Constants.SCA_INTENT_CONTROLLER);
			if(scaic.intentHandlerExists(interfaceName))
			{
				//scaic.printInfo();
				PAInterface sItf = (PAInterface) itfObjetct;
				try {
                    sItf = (PAInterface) IntentServiceItfGenerator.instance().generateInterface(itfObjetct,
                            interfaceName, this);
                } catch (ClassGenerationFailedException cgfe) {
                    logger.error("could not generate intent interceptor for reference (client interface) " +
                                interfaceName + ": " + cgfe.getMessage());
                }
                return sItf;
			}
		}
        return itfObjetct;
	}
}
