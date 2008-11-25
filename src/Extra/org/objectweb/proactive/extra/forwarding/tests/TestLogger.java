package org.objectweb.proactive.extra.forwarding.tests;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class TestLogger {
	private static Logger log = null;
	private static Level lvl = Level.DEBUG;
	
	public static Logger getLogger() {
		if(log == null) {
			log = LogManager.getRootLogger();
			log.setLevel(lvl);
		}
		return log;
	}
}
