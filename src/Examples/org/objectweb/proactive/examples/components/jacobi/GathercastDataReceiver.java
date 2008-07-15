package org.objectweb.proactive.examples.components.jacobi;

import java.util.List;

public interface GathercastDataReceiver {
	
	public void exchangeData(List<LineData> borders);

}
