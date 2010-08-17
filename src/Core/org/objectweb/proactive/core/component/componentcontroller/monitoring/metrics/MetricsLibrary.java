package org.objectweb.proactive.core.component.componentcontroller.monitoring.metrics;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.core.component.componentcontroller.monitoring.Metric;

public class MetricsLibrary {

	Map<String, Class<?>> library;
	
	private static MetricsLibrary instance = null;
	
	private MetricsLibrary() {
		library = new HashMap<String,Class<?>>();
		library.put("avgIncoming", AvgRespTimeIncomingMetric.class);
		library.put("avgOutgoing", AvgRespTimeOutgoingMetric.class);
	}
	
	/** Singleton
	 * 
	 * @return
	 */
	public static MetricsLibrary getInstance() {
		if(instance == null) {
			instance = new MetricsLibrary();
		}
		return instance;
	}
	
	/** Gets the class from the library, and returns and instance of that class
	 * 
	 * @param name
	 * @return
	 */
	public Metric getMetric(String name) {
		Metric metric = null;
		if(library.containsKey(name)) {
			Class<?> metricClass = library.get(name);
			/*try {
				metric = (Metric) metricClass.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			try {
				metric = (Metric) metricClass.getConstructor().newInstance();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return metric;
	}
	
	
}
