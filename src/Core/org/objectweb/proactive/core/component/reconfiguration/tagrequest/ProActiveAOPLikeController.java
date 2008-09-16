package org.objectweb.proactive.core.component.reconfiguration.tagrequest;

public interface ProActiveAOPLikeController {

	public static final String AOP_LIKE_CONTROLLER_NAME = "aop-like-controller";
	
	ComponentRequestTagUtilities getRequestTagUtilities();
	void setRequestTagUtilities(ComponentRequestTagUtilities utilities);
}
