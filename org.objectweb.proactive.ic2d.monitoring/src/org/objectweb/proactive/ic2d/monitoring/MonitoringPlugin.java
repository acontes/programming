package org.objectweb.proactive.ic2d.monitoring;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.osgi.framework.BundleContext;

public class MonitoringPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static MonitoringPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public MonitoringPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		System.out.println("MonitoringPlugin : createInitialLayout");
		try{
			RuntimeFactory.getDefaultRuntime();
		}
		catch(ProActiveException e) {
			//TODO log?
			e.printStackTrace();
		}
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static MonitoringPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.objectweb.proactive.ic2d.monitoring", path);
	}
}