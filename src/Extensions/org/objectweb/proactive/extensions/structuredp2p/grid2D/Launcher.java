package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.util.Scanner;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.util.Deployment;

/**
 * Launch a new grid of active objects which know their neighbors.
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public class Launcher {
	public static void main(String args[]) {
		try {
			Deployment.deploy(args[1]);
		} catch (NodeException e) {
			e.printStackTrace();
		} catch (ProActiveException e) {
			e.printStackTrace();
		}
		
		new ActiveGrid2D(Integer.parseInt(args[2]), Integer.parseInt(args[3]));

		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNext()) {
			if (scanner.next().equals("quit")) {
				// Terminates the deployment on the grid.
				Deployment.kill();
				break;
			}
			Thread.yield();
		}

	}
}
