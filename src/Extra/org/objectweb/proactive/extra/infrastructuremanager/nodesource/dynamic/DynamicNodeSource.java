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
package org.objectweb.proactive.extra.infrastructuremanager.nodesource.dynamic;

import java.util.ArrayList;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.infrastructuremanager.imnode.IMNode;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.IMNodeSource;
import org.objectweb.proactive.extra.infrastructuremanager.nodesource.frontend.DynamicNSInterface;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;

public class DynamicNodeSource extends IMNodeSource implements DynamicNSInterface {

	@Override
	public String getSourceId() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getNbMaxNodes() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getNiceTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getTimeToRelease() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setNbMaxNodes(int nb) {
		// TODO Auto-generated method stub
		
	}

	public void setNiceTime(int nice) {
		// TODO Auto-generated method stub
		
	}

	public void setTimeToRelease(int ttr) {
		// TODO Auto-generated method stub
		
	}

	public ArrayList<IMNode> getAllNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<IMNode> getBusyNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<IMNode> getDownNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<IMNode> getFreeNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public IntWrapper getNbAllNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public IntWrapper getNbBusyNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public IntWrapper getNbDownNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public IntWrapper getNbFreeNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<IMNode> getNodesByScript(VerifyingScript script, boolean ordered) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setBusy(IMNode imnode) {
		// TODO Auto-generated method stub
		
	}

	public void setDown(IMNode imnode) {
		// TODO Auto-generated method stub
		
	}

	public void setFree(IMNode imnode) {
		// TODO Auto-generated method stub
		
	}

	public void setNotVerifyingScript(IMNode imnode, VerifyingScript script) {
		// TODO Auto-generated method stub
		
	}

	public void setVerifyingScript(IMNode imnode, VerifyingScript script) {
		// TODO Auto-generated method stub
		
	}

	public BooleanWrapper shutdown() {
		// TODO Auto-generated method stub
		return null;
	}

}
