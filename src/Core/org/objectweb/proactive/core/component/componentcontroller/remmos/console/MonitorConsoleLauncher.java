package org.objectweb.proactive.core.component.componentcontroller.remmos.console;

import java.io.IOException;

import javax.naming.NamingException;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.proactive.api.PALifeCycle;

public class MonitorConsoleLauncher {

	/**
	 * @param args
	 * @throws NamingException 
	 * @throws IOException 
	 * @throws NoSuchInterfaceException 
	 */
	public static void main(String[] args) throws Exception {

		MonitorConsole mc = new MonitorConsole();
		mc.run();
		PALifeCycle.exitSuccess();
	}

}
