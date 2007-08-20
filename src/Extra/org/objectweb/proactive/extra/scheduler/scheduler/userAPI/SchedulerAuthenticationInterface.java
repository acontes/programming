/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.scheduler.userAPI;

import java.io.Serializable;
import javax.security.auth.login.LoginException;
import org.objectweb.proactive.extra.scheduler.core.AdminScheduler;
import org.objectweb.proactive.extra.scheduler.core.UserScheduler;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;

/**
 * Scheduler Authentification Interface provides method to connect the scheduler.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 24, 2007
 * @since ProActive 3.2
 */
public interface SchedulerAuthenticationInterface extends Serializable {
	
	/**
	 * Connect the user interface to a scheduler with the given scheduler URL.
	 * If the login or/and password do not match an allowed one,
	 * it will throw an LoginException.
	 * If the authentification succeed, it will return a new scheduler user API to managed job.
	 * 
	 * @param user the username of the user to connect.
	 * @param password the password of the user to connect.
	 * @return The {@link userScheduler} interface if this user can access to the scheduler.
	 * @throws LoginException thrown if this user/password does not match any entries.
	 * @throws SchedulerException thrown if the connection to the scheduler cannot be established.
	 */
	public UserScheduler logAsUser(String user, String password) throws LoginException, SchedulerException;
	
	/**
	 * Connect the admin interface to a scheduler with the given scheduler URL.
	 * If the login or/and password do not match an allowed one,
	 * it will throw an LoginException.
	 * If the authentification succeed, it will return a new scheduler admin API.
	 * This authentification requires that the user has admin rights.
	 * 
	 * @param user the username of the user to connect.
	 * @param password the password of the user to connect.
	 * @return The {@link userScheduler} interface if this user can access to the scheduler.
	 * @throws LoginException thrown if this user/password does not match any entries.
	 * @throws SchedulerException thrown if the connection to the scheduler cannot be established.
	 */
	public AdminScheduler logAsAdmin(String user, String password) throws LoginException, SchedulerException;
}
