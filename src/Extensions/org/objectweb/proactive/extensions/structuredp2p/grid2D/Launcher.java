package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.io.IOException;
import java.util.Scanner;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
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
	private static Node node;
	private static AwareObject entryPoint;

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
		System.out.println("Grid " + cols + "x" + rows);
	}

	/**
	 * Creates the entry point. It's the required {@link AwareObject} in order
	 * to enter in the AwareObject grid network.
	 */
	public void createEntryPoint() {
		node = Deployment.getVirtualNode("Grid2D").getANode();
		try {
			Launcher.entryPoint = (AwareObject) PAActiveObject.newActive(
					AwareObject.class.getName(), new Object[] { new Integer(0),
							new Integer(0) }, node);

			// Binds the entry point to a specific URL on the RMI registry
			PAActiveObject.registerByName(Launcher.entryPoint,
					"Grid2DEntryPoint");
		} catch (ActiveObjectCreationException e) {
			e.printStackTrace();
		} catch (NodeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print app menu option on the standard output.
	 */
	private static void printOptions() {
		System.out.println("Select one option :");
		System.out
				.println("  * Enter a coordinate like '0 1' in order to see neighbor of this peer");
		System.out.println("  * Enter 'quit' in order to quit the application");
	}

	/**
	 * Creates <code>nbRows</code> <code>nbCols</code> AwareObjects by forming a
	 * grid2D. Each object is inserted while using the <code>entryPoint</code>.
	 */
	private void createsAwareObjects() {
		int nbAwareObjects = this.nbCols * this.nbRows;
		int x = 0;
		int y = 0;

		AwareObject entryPoint = null;
		AwareObject newAwareObject = null;

		// Retrieve entryPoint
		try {
			entryPoint = (AwareObject) PAActiveObject.lookupActive(
					AwareObject.class.getName(), "Grid2DEntryPoint");
			System.out.println("EntryPoint retrieved !");
		} catch (ActiveObjectCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 1; i < nbAwareObjects; i++) {
			if (i % this.nbCols == 0) {
				x = 0;
				y++;
			} else {
				x++;
			}

			try {
				newAwareObject = (AwareObject) PAActiveObject
						.newActive(AwareObject.class.getName(), new Object[] {
								new Integer(x), new Integer(y) }, Launcher.node);
				entryPoint.registerNewPeer(newAwareObject);
			} catch (ActiveObjectCreationException e) {
				e.printStackTrace();
			} catch (NodeException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Entry point of the application.
	 * 
	 * @param args
	 *            parameters given to the application when launched.
	 */
	public static void main(String args[]) {
		if (args.length != 4) {
			System.err.println("Usage : java "
					+ Launcher.class.getCanonicalName() + " " + args[0]
					+ " descriptor nbRows nbCols");
		}

		try {
			Deployment.deploy(args[1]);
		} catch (NodeException e) {
			e.printStackTrace();
		} catch (ProActiveException e) {
			e.printStackTrace();
		}

		final int nbRows = Integer.parseInt(args[2]);
		final int nbCols = Integer.parseInt(args[3]);

		Launcher launcher = new Launcher(nbRows, nbCols);
		launcher.createEntryPoint();
		launcher.createsAwareObjects();

		new Thread(new Runnable() {
			@Override
			public void run() {
				Scanner scanner = new Scanner(System.in);
				String next;

				Launcher.printOptions();
				while (true) {
					next = scanner.nextLine();
					if (next.equals("quit")) {
						Deployment.kill();
						break;
					} else if (next.matches("[0-9]+ [0-9]+")) {
						String[] coordinates = next.split("\\s");
						AwareObject founded = null;

						int x = Integer.parseInt(coordinates[0]);
						int y = Integer.parseInt(coordinates[1]);

						if (x >= nbCols || x < 0 || y >= nbRows || y < 0) {
							System.err.println("Error, x must be in [0,"
									+ (int) (nbCols - 1) + "] and y in [0,"
									+ (int) (nbRows - 1) + "]");
						} else {
							founded = Launcher.entryPoint.find(x, y);
							if(PAFuture.getFutureValue(founded) != null)
							    System.out.println(founded);
							else
							    System.out.println("AwareObject not found.");
						}
					}

					Launcher.printOptions();
				}
			}

		}).start();
	}
}
