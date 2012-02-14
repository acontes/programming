package org.objectweb.proactive.examples.binarytree_multiactive;

import java.util.ArrayList;
import java.util.Iterator;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.config.CentralPAPropertyRepository;
import org.objectweb.proactive.core.node.NodeException;

public class TestTree {

	public static void main(String[] args) {

		String initial_ca_setting = CentralPAPropertyRepository.PA_FUTURE_AC
				.getValueAsString();
		System.out.println("Automatic Continuation activated "
				+ initial_ca_setting);

		boolean ma = false;

		TreeBis abt;
		try {
			abt = (TreeBis) org.objectweb.proactive.api.PAActiveObject
					.newActive(TreeBis.class.getName(), new Object[] { "0",
							"0", ma });

			for (int i = 1; i < 30; i++) {

				abt.insert(i + "", i + "", true, ma);
			}

			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			long t0 = System.currentTimeMillis();
			CustomValue[] va = new CustomValue[2000];
			for (int i = 0; i < va.length; i++) {
				va[i]=abt.getRandomLeafValue();
			}
			
			for (int i = 0; i < va.length; i++) {
				System.out.println("Result of call "+ i + " : " + va[i]) ;
			}
			long t1=System.currentTimeMillis();
			
			System.out.println("TestTree.main() finished, took " + (t1-t0));
			

//			CustomValue v = ;
//			CustomValue v2 = abt.getRandomLeafValue();
//			CustomValue v3 = abt.getRandomLeafValue();
//			PAFuture.waitFor(v, true);
//			System.out.println("Got leaf value 1 " + v);
//			PAFuture.waitFor(v2, true);
//			System.out.println("Got leaf value 2 " + v2);
//			PAFuture.waitFor(v3, true);
//			System.out.println("Got leaf value 3 " + v3);
//			// ArrayList<String> al = abt.getKeys();

			// for(String s : al){
			// System.out.println(s);
			// }
			//
			//
			//
			// System.out.println("Et la taille est :" + al.size());
			//

		} catch (ActiveObjectCreationException e) {
			e.printStackTrace();
		} catch (NodeException e) {
			e.printStackTrace();
		}
	}
}
