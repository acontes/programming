package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring;

/**
 * List what can be monitored through a monitor figure.
 * This is needed since for example, a runtime  figure {@link MonitorRuntime3D}
 * can display different information on a particular runtime. 
 * 
 * @version $Id$
 * @since 3.9
 * @author vjuresch
 */
public enum MonitoringTypes {
	RUNTIME_THREADS,
	RUNTIME_HEAP_MEMORY_USED,
	HOST_LOAD;
}
