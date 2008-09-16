package org.objectweb.proactive.core.component.reconfiguration.tagrequest;

import org.objectweb.proactive.core.component.interception.InputInterceptor;
import org.objectweb.proactive.core.component.interception.OutputInterceptor;

public interface DefaultInputOutputInterceptor extends InputInterceptor, OutputInterceptor{
	
	public static final String DEFAULT_INPUT_OUTPUT_INTERCEPTOR_NAME = "default-input-output-interceptor";
}
