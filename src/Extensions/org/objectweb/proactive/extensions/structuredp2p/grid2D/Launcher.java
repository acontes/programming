package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.io.IOException;
import java.util.Scanner;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
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
	private int nbCols;
	private int nbRows;

	/**
	 * Constructor.
	 * 
	 * @param rows
	 *            the number rows for the grid 2D.
	 * @param cols
	 *            the number of columns for the grid 2D.
	 */
	public Launcher(int rows, int cols) {
		this.nbRows = rows;
		this.nbCols = cols;
	}

	public static void main(String args[]) {
		try {
			Deployment.deploy(args[1]);
		} catch (NodeException e) {
			e.printStackTrace();
		} catch (ProActiveException e) {
			e.printStackTrace();
		}

		/**
		 * Creates the entry point, the required AwareObject in order to enter
		 * in the AwareObject grid network.
		 */
		AwareObject entryPoint = null;
		try {
			entryPoint = (AwareObject) PAActiveObject.newActive(
					AwareObject.class.getName(), new Object[] {}, Deployment
							.getVirtualNode("Grid2D").getANode());
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Binds the entry point to a specific URL on the RMI Registry
		try {
			PAActiveObject.registerByName(entryPoint, "Grid2DEntryPoint");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Launcher launcher = new Launcher(Integer.parseInt(args[2]), Integer
				.parseInt(args[3]));
		launcher.createsAwareObjects();

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

	private void createsAwareObjects() {
		int nbAwareObjects = this.nbCols * this.nbRows;

		// Retrieve entryPoint
		AwareObject entryPoint = null;
		try {
			entryPoint = (AwareObject) PAActiveObject.lookupActive(
					AwareObject.class.getName(), "Grid2DEntryPoint");
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < nbAwareObjects; i++) {
			AwareObject newAwareObject = null;
			AwareObject current = entryPoint;
			try {
				newAwareObject = (AwareObject) PAActiveObject.newActive(
						AwareObject.class.getName(), new Object[] {},
						Deployment.getVirtualNode("Grid2D").getANode());
			} catch (ActiveObjectCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			AwareObject lastY = entryPoint;
			for (int y = 0; y < this.nbRows; y++) {
				for (int x = 0; x < this.nbCols; x++) {
					AwareObject eastObject = current.getEastNeighbor();
					if (eastObject == null) {
						current.setEastNeighbor(newAwareObject);
						newAwareObject.setWestNeighbor(current);
						break;
					}

					current = eastObject;
				}

				current = lastY;

				if (current.getSouthNeighbor() == null) {
					current.setSouthNeighbor(newAwareObject);
					newAwareObject.setNorthNeighbor(current);
					break;
				}

				current = current.getSouthNeighbor();
				lastY = current;
			}
		}
	}
}
