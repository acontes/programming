/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */

package org.objectweb.proactive.extensions.jee;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Hierarchy;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.spi.LoggerRepository;
import org.apache.log4j.spi.RepositorySelector;
import org.apache.log4j.spi.RootLogger;

/**
 * Context repository selector, to use for log4j logging
 * see http://wiki.jboss.org/wiki/Log4jRepositorySelector
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public class ContextRepositorySelector implements RepositorySelector
{

	// we are expecting to find the log4j config file in the resource archive
	// in a known place : META-INF/proactive-log4j
	public static final String LOG4J_CONFIG_FILE = "/META-INF/proactive-log4j";
		
	private static boolean _alreadyStarted = false;

	private static Object _guardObj = new Object();

	private static Map<ClassLoader,LoggerRepository> _knownRepositories = 
		new HashMap<ClassLoader,LoggerRepository>();

	private static LoggerRepository _defaultRepository;

	/**
	 * This method initializes a new log4j hierarchy for ProActive Logging.
	 * @throws Exception 
	 */
	public static synchronized void init(ProActiveConnectorBean caller) throws Exception 
	{
		if( !_alreadyStarted ) // set the global RepositorySelector
		{
			_defaultRepository = LogManager.getLoggerRepository();
			RepositorySelector theSelector = new ContextRepositorySelector();
			LogManager.setRepositorySelector(theSelector, _guardObj);
			_alreadyStarted = true;
		}

		Hierarchy hierarchy = loadLog4JConfig(caller);
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		_knownRepositories.put(loader, hierarchy);
	}

	// this will create && initialize a new Log4j Hierarchy with the configuration options
	// specified in the LOG4J_CONFIG_FILE
	// caller is needed in order to have the path where to search for the log4j config file
	// this file is LOG4J_CONFIG_FILE, placed relative to the caller class's path
	private static Hierarchy loadLog4JConfig(ProActiveConnectorBean caller) 
		throws Exception
	{
		
		// create a new hierarchy of loggers
		Hierarchy hierarchy = new Hierarchy(new RootLogger(Level.DEBUG));
		
		URL log4jConfigFile = caller.getClass().getResource(LOG4J_CONFIG_FILE);
		PropertyConfigurator conf = new PropertyConfigurator();
		
		// clear old config
		hierarchy.resetConfiguration();
		conf.doConfigure(log4jConfigFile, hierarchy);
		return hierarchy; 
		
	}

	private ContextRepositorySelector() 
	{
		// constructor private - no access allowed
	}

	// this is called by LogManager, when it receives a request for a Logger
	@Override
	public LoggerRepository getLoggerRepository() 
	{
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		LoggerRepository repository = (LoggerRepository)_knownRepositories.get(loader);

		if (repository == null) 
		{
			return _defaultRepository;
		} 
		else 
		{
			return repository;
		}
	}
}
