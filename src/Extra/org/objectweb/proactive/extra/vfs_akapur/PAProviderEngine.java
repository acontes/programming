package org.objectweb.proactive.extra.vfs_akapur;


import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActiveInternalObject;
import org.objectweb.proactive.RunActive;



//This class will serve as an Engine which will create service active objects on the nodes involved in data space

public class PAProviderEngine implements ProActiveInternalObject, InitActive, RunActive {
	
	
	
	static PAProviderEngine firstEngine = getPAProviderEngine();

	private static PAProviderEngine getPAProviderEngine() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initActivity(Body body) {
		// TODO Auto-generated method stub
		
	}

	public void runActivity(Body body) {
		// TODO Auto-generated method stub
		
	} 

}
