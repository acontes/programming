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
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
package org.objectweb.proactive.core.security;

import java.io.Serializable;

import org.objectweb.proactive.core.security.securityentity.Entities;

/**
 * This classe represents a security context associated with a particular
 * session
 * 
 */
public class SecurityContext implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3210156951283073478L;

	private Entities entitiesFrom;

	private Entities entitiesTo;

	private Communication sendRequest;

	private Communication sendReply;

	private boolean migration;

	private boolean aoCreation;

	public SecurityContext() {
		// serializable
	}

	public SecurityContext(Entities entitiesFrom, Entities entitiesTo,
			Communication sendRequest, Communication sendReply,
			boolean aoCreation, boolean migration) {
		this.entitiesFrom = entitiesFrom;
		this.entitiesTo = entitiesTo;
		this.sendReply = sendReply;
		this.sendRequest = sendRequest;
		this.aoCreation = aoCreation;
		this.migration = migration;
	}
	
	/**
	 * @return entities of the 'from' objects
	 */
	public Entities getEntitiesFrom() {
		return this.entitiesFrom;
	}

	/**
	 * @return entities of the 'to' objects
	 */
	public Entities getEntitiesTo() {
		return this.entitiesTo;
	}

	/**
	 * @return true if migration is granted
	 */
	public boolean isMigration() {
		return this.migration;
	}

	/**
	 * @return true if object can send replies
	 */
	public Communication getSendReply() {
		return this.sendReply;
	}

	/**
	 * @return true if object can send requests
	 */
	public Communication getSendRequest() {
		return this.sendRequest;
	}
	
	public Communication getReceiveRequest() {
		return this.sendReply;
	}
	
	public Communication getReceiveReply() {
		return this.sendRequest;
	}

	public boolean isAoCreation() {
		return this.aoCreation;
	}

	public boolean isEverythingForbidden() {
		return !this.sendReply.getCommunication()
				&& !this.sendRequest.getCommunication() && !this.aoCreation
				&& !this.migration;
	}
	
	public SecurityContext otherSideContext() {
		return new SecurityContext(this.getEntitiesTo(), this.getEntitiesTo(),
				this.getSendReply(), this.getSendRequest(),
				this.isAoCreation(), this.isMigration());
	}
	
	public static SecurityContext computeContext(SecurityContext from,
			SecurityContext to) {
		return new SecurityContext(from.getEntitiesFrom(),
				from.getEntitiesTo(), Communication.computeCommunication(from
						.getSendRequest(), to.getReceiveRequest()),
				Communication.computeCommunication(from.getSendReply(), to
						.getReceiveReply()), from.isAoCreation()
						&& to.isAoCreation(), from.isMigration()
						&& to.isMigration());
	}
	
	public static SecurityContext mergeContexts(SecurityContext thees,
			SecurityContext that) {
		return new SecurityContext(thees.getEntitiesFrom(),
				thees.getEntitiesTo(), Communication.computeCommunication(thees
						.getSendRequest(), that.getSendRequest()),
				Communication.computeCommunication(thees.getSendReply(), that
						.getSendReply()), thees.isAoCreation()
						&& that.isAoCreation(), thees.isMigration()
						&& that.isMigration());
	}
}
