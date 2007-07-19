package org.objectweb.proactive.examples.scheduler;

import java.io.File;
import java.net.URI;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.IMAdmin;
import org.objectweb.proactive.extra.infrastructuremanager.frontend.NodeSet;
import org.objectweb.proactive.extra.scheduler.resourcemanager.InfrastructureManagerProxy;
import org.objectweb.proactive.extra.scheduler.scripting.VerifyingScript;

public class TestIMProxy {
	
	
	private IMAdmin admin = null;
	private InfrastructureManagerProxy proxy = null;

	protected void setUp() throws Exception {
		// Creating Infrastructure Manager
//		System.err.println("Creating resource manager...");
//		IMFactory.startLocal();
//		admin  = IMFactory.getAdmin();
//		
//		// Deploying nodes
//		System.err.println("Deploying nodes...");
//		admin.deployAllVirtualNodes(new File("descriptors/scheduler/deployment/Descriptor_SSH_List.xml"), null);
//		Thread.sleep(10000);
		
		// Launching IMProxy
		System.err.println("Launching Infrastructure Manager Proxy");
		
		proxy = InfrastructureManagerProxy.getProxy(new URI("rmi://localhost:1099/"));
		
		System.err.println("Starting tests");
	}

	public void testPrincipal() {
		try {
			// Verifying script
			VerifyingScript verif = new VerifyingScript(new File("/user/jmartin/home/scripts/test.js"));
			VerifyingScript post1 = new VerifyingScript(new File("/user/jmartin/home/scripts/clean2.js"));
			VerifyingScript post2 = new VerifyingScript(new File("/user/jmartin/home/scripts/clean5.js"));

			// Dispay total nodes
			int total = proxy.getNumberOfAllResources().intValue();
			System.err.println("total nodes = "+total);

			// Get Exactly 2 nodes not on fiacre
			NodeSet nodes = proxy.getExactlyNodes(2, verif);
			//Vector<Node> nodes = (Vector<Node>) ProActive.getFutureValue(rm.getExactlyNodes(2, verif, null));
			if(!nodes.isEmpty()) {
				System.err.println("nodes obtained = "+nodes.size());
				for( Node n : nodes)
					System.err.println(n.getNodeInformation().getURL());

				// Release thoose nodes
				proxy.freeNodes(nodes);
				Thread.sleep(5000);
			}

//			URL url = new URL("http://localhost:10080/test.js");
//			verif = new VerifyingScript(url);
//			Get At Most 3 nodes not on fiacre (but there is only 2 nodes corresponding"
			nodes = proxy.getAtMostNodes(2, verif);
			ProActive.waitFor((Object) nodes);
			if(!nodes.isEmpty()) {
				System.err.println("nodes obtained = "+nodes.size());
				for( Node n : nodes)
					System.err.println(n.getNodeInformation().getURL());
				ProActive.waitFor(nodes);
				System.err.println("Free nodes = "+ proxy.getNumberOfFreeResource());
				// Release thoose nodes
				if(nodes.size() > 1) {
					proxy.freeNode(nodes.remove(0), post1);
					proxy.freeNodes(nodes, post2);
				} else {
					proxy.freeNodes(nodes, post2);
				}
				int tot;
				int free;
				int max = 5;
				while(( max-- > 0) && (tot = proxy.getNumberOfAllResources().intValue()) != (free = proxy.getNumberOfFreeResource().intValue())) {
					System.out.println("Waiting for freeing resources ("+free+"/"+tot+"available)");
					Thread.sleep(2000);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	protected void tearDown() throws Exception {
		System.err.println("Stopping test :");
		System.err.println("-> Stopping Proxy:");
		proxy.shutdownProxy();
//		System.err.println("-> Stopping IM :");
//		admin.shutdown();
		ProActive.exitSuccess();
	}

	public static void main(String[] args) throws Exception{
		TestIMProxy test = new TestIMProxy();
		test.setUp();
		test.testPrincipal();
		test.tearDown();
	}

}
