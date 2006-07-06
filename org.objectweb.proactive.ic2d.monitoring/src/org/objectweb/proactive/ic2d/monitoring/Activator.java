package org.objectweb.proactive.ic2d.monitoring;

import javassist.ClassClassPath;
import javassist.ClassPool;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.objectweb.proactive.ic2d.monitoring";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		// add current classpath for javassist class pool
		ClassPool pool = ClassPool.getDefault();
		pool.insertClassPath(new ClassClassPath(this.getClass()));
		
		RuntimeFactory.getDefaultRuntime();
		
		System.out.println("################################");
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

}
