package org.objectweb.proactive.extensions.ejb;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;
import org.jboss.annotation.ejb.LocalBinding;
import org.jboss.annotation.ejb.RemoteBinding;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.extensions.ejb.ProActiveBeanInterface;

@Stateless
@Local ({ProActiveBeanInterface.class})
@LocalBinding (jndiBinding="ProActive/StupidBean/Local")
@Remote ({ProActiveBeanInterface.class})
@RemoteBinding (jndiBinding="ProActive/StupidBean/Remote")
public class ProActiveBean implements ProActiveBeanInterface

{
	private static final String LOG_NAME = Loggers.JBOSS;
	private static final Logger _anotherLogger = Logger.getLogger(LOG_NAME);
	
	@Override
	public String test() {
		_anotherLogger.debug("the business method is being called");
		return LOG_NAME;
	}
	
	@PostConstruct
	public void startService() {
		_anotherLogger.debug("The _other_ bean started");
	}
	
	@PreDestroy
	public void stopService() {
		_anotherLogger.debug("The _other_ bean stopped ");
	}

}
