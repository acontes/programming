package org.objectweb.proactive.extra.infrastructuremanager.nodesource.frontend;


public interface DynamicNSInterface extends NodeSourceInterface{
	
	public int getNbMaxNodes();
	
	public void setNbMaxNodes(int nb);
	
	public int getTimeToRelease();
	
	public void setTimeToRelease(int ttr);
	
	public int getNiceTime();
	
	public void setNiceTime(int nice);

}
