package org.objectweb.proactive.examples.components.jacobi;

import java.util.List;

import org.objectweb.proactive.core.component.type.annotations.multicast.MethodDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMetadata;
import org.objectweb.proactive.core.component.type.annotations.multicast.ParamDispatchMode;

public interface MulticastDataSender {
	
	@MethodDispatchMetadata(mode = @ParamDispatchMetadata(mode =ParamDispatchMode.ONE_TO_ONE))
	public void exchangeData(List<LineData> borders);
	
}
