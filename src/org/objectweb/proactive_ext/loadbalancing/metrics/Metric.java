package org.objectweb.proactive_ext.loadbalancing.metrics;


import org.objectweb.proactive_ext.loadbalancing.LoadBalancer;


public interface Metric{

	/**
	 * This method has to call the methods :
	 * 	- stealWork 
	 * 	or
	 *  - startBalancing
	 *  according to the load 
	 * @param lb the LoadBalancer
	 */
	public void takeDecision(LoadBalancer lb);
	
	public double getRanking();
	
	public double getLoad();
}
