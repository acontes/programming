package org.objectweb.proactive.extensions.nativecode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extensions.nativeinterface.Native;
import org.objectweb.proactive.extensions.nativeinterface.ProActiveNative;
import org.objectweb.proactive.extensions.nativeinterface.spmd.NativeSpmd;
import org.objectweb.proactive.extensions.nativeinterfacempi.MpiApplicationFactory;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

public class NativeStarter {
	private List<GCMApplication> gcma;

	//Active-Object Based
	public static int AOB = 0;
	
	//Component Based
	public static int CB = 1;
	
	private int type;
	
	public NativeStarter() {
		this.type = NativeStarter.AOB;
		this.gcma = new ArrayList<GCMApplication>();
	}
	
	public NativeStarter(int type) {
		this.type = type;
		this.gcma = new ArrayList<GCMApplication>();
	}
	
	public void processDescriptor(File descriptor) throws ProActiveException {
        // Access the nodes of the descriptor file
		System.out.println("[NativeStarter] Processing descriptor " + descriptor);
        GCMApplication applicationDescriptor = PAGCMDeployment.loadApplicationDescriptor(descriptor);
        System.out.println("[NativeStarter] Start deployment of " + descriptor);
        applicationDescriptor.startDeployment();

        this.gcma.add(applicationDescriptor);
        
        Map<String, GCMVirtualNode> vnMap = applicationDescriptor.getVirtualNodes();
        Collection<GCMVirtualNode> vns = vnMap.values();
        System.out.println("[NativeStarter] virtual node count " + vns.size());
        for (GCMVirtualNode virtualNode : vns) {
        	System.out.println("[NativeStarter] virtual node wait ready ");
        	virtualNode.waitReady();
        	System.out.println("[NativeStarter] virtual node wait ready --> OK");
        }
        
        // All Nodes deployed
        
        System.out.println("[NativeStarter] All nodes deployed");
        
        // Start 
        ArrayList<NativeSpmd> spmdList = new ArrayList<NativeSpmd>();
        for (GCMVirtualNode virtualNode : vns) {
        	List<Node> nodes = virtualNode.getCurrentNodes();
        	NativeSpmd nativeSpmd_01 = Native.newNativeSpmd(virtualNode.getName(), nodes, new MpiApplicationFactory());
        	spmdList.add(nativeSpmd_01);
        }
          
        
        if (!spmdList.isEmpty()) {
        	  if(this.type == NativeStarter.CB) {
        		  ProActiveNative.deploy(spmdList, true);
              }else{
            	  ProActiveNative.deploy(spmdList, false);
              }
        	
        	// active wait 
        	ProActiveNative.deploymentFinished();
        }
	}
	
	public void waitAndKill() {
		ProActiveNative.waitFinished();
		for (GCMApplication gcma : this.gcma) {
			gcma.kill();
		}
		
		System.exit(0);
	}

	
    public static void main(String[] args) throws Exception {
    	
    	NativeStarter starter; 
    	
    	if(System.getProperty("discogrid.runtime.type")!=null && 
    			System.getProperty("discogrid.runtime.type").equals("CB")){
    		starter = new NativeStarter(NativeStarter.CB);
    	}else{
    		starter = new NativeStarter(NativeStarter.AOB);
    	}
    	
    	for (int i = 0; i < args.length; i++) {
    		starter.processDescriptor(new File(args[i]));	
		}
    	
    	starter.waitAndKill();
    }
}
