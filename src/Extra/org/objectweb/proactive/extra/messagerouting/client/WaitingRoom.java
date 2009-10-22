/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.extra.messagerouting.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.messagerouting.exceptions.MessageRoutingException;
import org.objectweb.proactive.extra.messagerouting.protocol.AgentID;


/** All the clients waiting for a response
 * 
 * Add thread sending a message is a "patient". It will wait in the waiting room
 * until the response is available.
 * 
 * Patient must be created by using
 * {@link WaitingRoom#enter(AgentID, long)} and not by calling its
 * constructor.
 * 
 * It allows to group mailboxes by recipient. When a remote client crash or
 * disconnect, the agent must unblock all the threads waiting for a response
 * from this remote client.
 * 
 * Methods are made package-accessible by default; 
 * only methods that are accessed from other packages are made public
 * 
 */
public class WaitingRoom {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT);

    final private Map<AgentID, Map<Long, Patient>> byRemoteAgent;

    /** Must be hold for each addition or removal */
    final private Object lock = new Object();

    WaitingRoom() {
        this.byRemoteAgent = new HashMap<AgentID, Map<Long, Patient>>();
    }

    /** Add a new patient into the waiting room
     * 
     * @param remoteAgentId
     *            Message recipient
     * @param messageId
     *            Message ID
     * @return a newly created mailbox
     */
    public Patient enter(AgentID remoteAgentId, long messageId) {
        Patient mb = new Patient(remoteAgentId, messageId);
        synchronized (this.lock) {
            Map<Long, Patient> byMessageId;
            byMessageId = this.byRemoteAgent.get(remoteAgentId);
            if (byMessageId == null) {
                byMessageId = new HashMap<Long, Patient>();
                this.byRemoteAgent.put(remoteAgentId, byMessageId);
            }
            byMessageId.put(messageId, mb);
        }

        return mb;
    }

    /**
     * Unblock all the threads waiting for a response from a given remote
     * agent
     * 
     * @param agentID
     *            the remote Agent ID
     */
    void unlockDueToDisconnection(AgentID agentID) {
        synchronized (this.lock) {
            MessageRoutingException e = new MessageRoutingException("Remote agent disconnected");

            Map<Long, Patient> map = this.byRemoteAgent.get(agentID);
            if (map != null) {
                for (Patient patient : map.values()) {
                    if (logger.isTraceEnabled()) {
                        logger.trace("Unlocked request " + patient.getRequestID() + " because remote agent" +
                            patient.getRecipient() + " disconnected");
                    }
                    patient.setAndUnlock(e);
                }
            }
        }
    }

    /**
     * Unblock the Patient waiting on a particular messageID
     * @param agentId
     */
    Patient unlockDueToCorruption(Long messageId) {
        AgentID agent = null;
        for (Map.Entry<AgentID, Map<Long, Patient>> entry : this.byRemoteAgent.entrySet()) {
            if (entry.getValue().containsKey(messageId)) {
                agent = entry.getKey();
                break;
            }
        }
        if (agent == null)
            return null;
        return remove(agent, messageId);
    }

    /** Remove a patient on response arrival */
    Patient remove(AgentID agentId, long messageId) {
        Patient patient = null;
        synchronized (this.lock) {
            Map<Long, Patient> map;
            map = this.byRemoteAgent.get(agentId);
            if (map != null) {
                patient = map.remove(messageId);
            }
        }

        return patient;
    }

    String[] getBlockedCallers() {
        List<String> ret = new LinkedList<String>();

        synchronized (this.lock) {
            for (AgentID recipient : this.byRemoteAgent.keySet()) {
                Map<Long, Patient> m = this.byRemoteAgent.get(recipient);
                for (Long messageId : m.keySet()) {
                    ret.add("recipient: " + recipient + " messageId: " + messageId);
                }
            }
        }

        return ret.toArray(new String[0]);
    }
}