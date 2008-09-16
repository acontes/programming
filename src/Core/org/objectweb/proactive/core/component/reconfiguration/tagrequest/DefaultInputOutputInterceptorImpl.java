package org.objectweb.proactive.core.component.reconfiguration.tagrequest;

import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.factory.InstantiationException;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.controller.AbstractProActiveController;
import org.objectweb.proactive.core.component.controller.ProActiveLifeCycleControllerImpl;
import org.objectweb.proactive.core.component.type.ProActiveTypeFactoryImpl;
import org.objectweb.proactive.core.mop.MethodCall;

public class DefaultInputOutputInterceptorImpl extends
		AbstractProActiveController implements DefaultInputOutputInterceptor {

	@Override
	protected void setControllerItfType() {
		// TODO Auto-generated method stub
			try {
				setItfType(ProActiveTypeFactoryImpl.instance().createFcItfType(
						DefaultInputOutputInterceptor.DEFAULT_INPUT_OUTPUT_INTERCEPTOR_NAME,
						DefaultInputOutputInterceptor.class.getName(), TypeFactory.SERVER,
						TypeFactory.MANDATORY, TypeFactory.SINGLE));
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
	}
	
	public DefaultInputOutputInterceptorImpl(Component owner) {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	public void afterInputMethodInvocation(MethodCall methodCall) {
		// TODO Auto-generated method stub
//		try {
//			ComponentAOPLikeUtilities utilities = ((ProActiveAOPLikeControllerImpl) owner
//					.getFcInterface(ProActiveAOPLikeController.AOP_LIKE_CONTROLLER_NAME))
//					.getUtilities();
//			utilities.postInputProcessing(methodCall, owner);
//		} catch (NoSuchInterfaceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public void beforeInputMethodInvocation(MethodCall methodCall) {
		// TODO Auto-generated method stub
//		try {
//			ComponentAOPLikeUtilities utilities = ((ProActiveAOPLikeControllerImpl) owner
//					.getFcInterface(ProActiveAOPLikeController.AOP_LIKE_CONTROLLER_NAME))
//					.getUtilities();
//			utilities.preInputProcessing(methodCall, owner);
//		} catch (NoSuchInterfaceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public void afterOutputMethodInvocation(MethodCall methodCall) {
		// TODO Auto-generated method stub
//		try {
//			ComponentAOPLikeUtilities utilities = ((ProActiveAOPLikeControllerImpl) owner
//					.getFcInterface(ProActiveAOPLikeController.AOP_LIKE_CONTROLLER_NAME))
//					.getUtilities();
//			utilities.postOutputProcessing(methodCall, owner);
//		} catch (NoSuchInterfaceException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	public void beforeOutputMethodInvocation(MethodCall methodCall) {
		// TODO Auto-generated method stub
		try {
			ComponentRequestTagUtilities utilities = ((ProActiveAOPLikeControllerImpl) owner
					.getFcInterface(ProActiveAOPLikeController.AOP_LIKE_CONTROLLER_NAME))
					.getRequestTagUtilities();
			Object tag = utilities.tagForOutputMethod();
			methodCall.getComponentMetadata().setTag(tag);
		} catch (NoSuchInterfaceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
