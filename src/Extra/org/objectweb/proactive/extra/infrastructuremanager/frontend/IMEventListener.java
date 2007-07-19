package org.objectweb.proactive.extra.infrastructuremanager.frontend;

public interface IMEventListener {
	
	public void nodeDown(IMNodeEvent event);
	
	public void nodeFree(IMNodeEvent event);
	
	public void nodeBusy(IMNodeEvent event);
	
	public void newNode(IMNodeEvent event);

}
