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
