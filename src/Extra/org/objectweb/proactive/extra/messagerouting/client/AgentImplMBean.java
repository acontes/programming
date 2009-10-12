/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
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
 *  Initial developer(s):               The ActiveEon Team
 *                        http://www.activeeon.com/
 *  Contributor(s):
 *
 *
 * ################################################################
 * $$ACTIVEEON_INITIAL_DEV$$
 */
package org.objectweb.proactive.extra.messagerouting.client;

public interface AgentImplMBean {

    public long getLocalAgentID();

    public String getLocalAddress();

    public int getLocalPort();

    public String getRemoteAddress();

    public int getRemotePort();

    public String[] getMailboxes();

    // indicates if Direct Connection is enabled
    public boolean isDCEnabled();

    // indicates if the Direct Connection server is started
    public boolean isDCServerStarted();

    // get the list of remote agents to which we are directly connected
    public String[] getOutboundAgents() throws IllegalStateException;

    // get the list of candidates for direct connection
    public String[] getCandidateAgents() throws IllegalStateException;

    // get the list of agents to which we tried to connect directly but failed
    public String[] getFailedAgents() throws IllegalStateException;

}
