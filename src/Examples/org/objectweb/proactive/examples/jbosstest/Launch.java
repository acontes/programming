package org.objectweb.proactive.examples.jbosstest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.NodeException;


public class Launch {
	
	private static VirtualNode deploy(String descriptor , String vnName ) {
        try {
            //create object representation of the deployment file
            ProActiveDescriptor pad = PADeployment.getProactiveDescriptor(descriptor);
            //active worker nodes
            pad.activateMapping(vnName);
            //get the first Node available in the first Virtual Node 
            //specified in the descriptor file
            VirtualNode vn = pad.getVirtualNode(vnName);
            return vn;
        } catch (NodeException nodeExcep) {
            System.err.println(nodeExcep.getMessage());
        } catch (ProActiveException proExcep) {
            System.err.println(proExcep.getMessage());
        }
        return null;
    }

	public static void main(String[] args) {
		try{
			BufferedReader inputBuffer = new BufferedReader(new InputStreamReader(System.in));
			VirtualNode vn = deploy( args[0] , args[1] );

			//create the active object
			Test[] aos = (Test[]) PAActiveObject.newActiveInParallel(Test.class.getName(),
					new Object[][] {null,null}, vn.getNodes());

			System.out.println("Acuma, poti sa zici si tu ceva...");
			inputBuffer.readLine();

			for (int i = 0; i < aos.length; i++) {
				PAActiveObject.terminateActiveObject(aos[i],false);
			}
			
			//vn.killall(true);
			PALifeCycle.exitSuccess();

		}
		catch (NodeException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.err.println( "Eroare de Class Server, probabil. Mesaj:" + e.getMessage());
			e.printStackTrace();
		}

	}

}
