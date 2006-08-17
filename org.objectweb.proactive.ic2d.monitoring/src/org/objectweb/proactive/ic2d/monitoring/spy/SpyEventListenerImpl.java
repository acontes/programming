/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.ic2d.monitoring.spy;

import java.io.Serializable;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.ic2d.monitoring.data.AOObject;
import org.objectweb.proactive.ic2d.monitoring.data.State;
import org.objectweb.proactive.ic2d.monitoring.data.NodeObject;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;

public class SpyEventListenerImpl implements SpyEventListener, Serializable{

	private NodeObject nodeObject;

	public SpyEventListenerImpl(){

	}

	public SpyEventListenerImpl(NodeObject nodeObject){
		this.nodeObject = nodeObject;
	}

	public void activeObjectAdded(UniqueID id, String nodeURL, String classname, boolean isActive) {
		// TODO Auto-generated method stub
		//System.out.println("# SpyEventListener : activeObjectAdded ,id="+getName(id));
	}

	public void activeObjectChanged(UniqueID id, boolean isActive, boolean isAlive) {
		// TODO Auto-generated method stub
		//System.out.println("# SpyEventListener : activeObjectChanged ,id="+getName(id));
	}

	public void objectWaitingForRequest(UniqueID id, SpyEvent spyEvent) {
		//System.out.println("# SpyEventListener : objectWaitingForRequest ,id="+getName(id));
		AOObject ao = getActiveObject(id);
		if(ao == null)
			return;
		ao.setState(State.WAITING_FOR_REQUEST);
		ao.setRequestQueueLength(0);
	}

	public void objectWaitingByNecessity(UniqueID id, SpyEvent spyEvent) {
		//System.out.println("# SpyEventListener : objectWaitingBynecessity ,id="+getName(id));
		AOObject ao = getActiveObject(id);
		if(ao == null)
			return;
		ao.setState((ao.getState() == State.SERVING_REQUEST)
				?State.WAITING_BY_NECESSITY_WHILE_SERVING
						:State.WAITING_BY_NECESSITY_WHILE_ACTIVE);
	}

	public void objectReceivedFutureResult(UniqueID id, SpyEvent spyEvent) {
		//System.out.println("# SpyEventListener : objectReceveidFutureResult ,id="+getName(id));
		AOObject ao = getActiveObject(id);
		if(ao == null)
			return;
		switch (ao.getState()) {
		case WAITING_BY_NECESSITY_WHILE_SERVING:
			ao.setState(State.SERVING_REQUEST);
			break;
		case WAITING_BY_NECESSITY_WHILE_ACTIVE:
			ao.setState(State.ACTIVE);
			break;
		}
	}

	public void requestMessageSent(UniqueID id, SpyEvent spyEvent) {
		//System.out.println("# SpyEventListener : requestMessageSent ,id="+getName(id));
	}

	public void replyMessageSent(UniqueID id, SpyEvent spyEvent) {
		//System.out.println("# SpyEventListener : replyMessageSent ,id="+getName(id));
		AOObject ao = getActiveObject(id);
		if(ao == null)
			return;
		ao.setState(State.ACTIVE);
		ao.setRequestQueueLength(((SpyMessageEvent) spyEvent).getRequestQueueLength());
	}

	public void requestMessageReceived(UniqueID id, SpyEvent spyEvent) {
		//System.out.println("# SpyEventListener : requestMessageReceived ,id="+getName(id));
		AOObject destination = nodeObject.findActiveObjectById(id);
		if(destination == null)
			return;
		destination.setState(State.SERVING_REQUEST);
		destination.setRequestQueueLength(((SpyMessageEvent) spyEvent).getRequestQueueLength());

		UniqueID sourceId = ((SpyMessageEvent) spyEvent).getSourceBodyID();
		AOObject source = WorldObject.getInstance().findActiveObjectById(sourceId);

		// We didn't find the source
		if(source == null)
			return;

		// We didn't find the destination
		if(destination == null)
			return;

		//Communication communication = new Communication(source, destination);

		source.addCommunication(/*communication*/(SpyMessageEvent) spyEvent);
		destination.addCommunication(/*communication*/(SpyMessageEvent) spyEvent);

	}

	public void replyMessageReceived(UniqueID id, SpyEvent spyEvent) {
		// TODO Auto-generated method stub
		//System.out.println("# SpyEventListener : replyMessageReceived ,id="+getName(id));
	}

	public void voidRequestServed(UniqueID id, SpyEvent spyEvent) {
		//System.out.println("# SpyEventListener :  voidRequestServed ,id="+getName(id));
		AOObject ao = getActiveObject(id);
		if(ao == null)
			return;
		ao.setState(State.ACTIVE);
		ao.setRequestQueueLength(((SpyMessageEvent) spyEvent).getRequestQueueLength());
	}

	public void allEventsProcessed() {
		// TODO Auto-generated method stub
		//System.out.println("# SpyEventListener : allEventsProcessed");
	}

	public void servingStarted(UniqueID id, SpyEvent spyEvent) {
		//System.out.println("# SpyEventListener : servingStarted ,id="+getName(id));
		AOObject ao = getActiveObject(id);
		if(ao == null)
			return;
		ao.setState(State.SERVING_REQUEST);
		ao.setRequestQueueLength(((SpyMessageEvent) spyEvent).getRequestQueueLength());
	}

	public String getName(UniqueID id){
		AOObject ao = null;
		if(nodeObject != null)
			ao = (AOObject) nodeObject.getChild(id.toString());
		if(ao == null)
			return id.toString();
		return ao.getFullName();
	}

	public AOObject getActiveObject(UniqueID id){
		return (AOObject) nodeObject.getChild(id.toString());
	}
}
