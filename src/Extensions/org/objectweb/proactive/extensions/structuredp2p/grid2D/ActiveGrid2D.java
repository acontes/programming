package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.util.ArrayList;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.util.Deployment;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

/**
 * Represent a grid 2D being able to be deployed on several machines. This grid
 * contains of {@link AwareObject} which knows their neighbors.
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public class ActiveGrid2D {
	private int nbRows;
	private int nbCols;

	private ArrayList<AwareObject> elements;

	/**
	 * Constructor.
	 * 
	 * @param rows
	 *            the number rows for the grid 2D.
	 * @param cols
	 *            the number of columns for the grid 2D.
	 */
	public ActiveGrid2D(int rows, int cols) {
		this.nbRows = rows;
		this.nbCols = cols;
		this.elements = new ArrayList<AwareObject>(this.nbRows * this.nbCols);
	}

	/**
	 * Creates <code>nbCols * nbRows</code> aware objects on the grid.
	 */
	public void createAwareObjects() {
		int nbElements = this.nbCols * this.nbRows;

		try {
			ArrayList<GCMVirtualNode> listVn = Deployment.getAllVirtualNodes();

			for (int i = 0; i < nbElements; i++) {
				AwareObject e = (AwareObject) PAActiveObject.newActive(
						AwareObject.class.getName(), new Object[] {}, listVn
								.get(i % listVn.size()).getANode());

				this.elements.add(e);
			}

			this.initCoordinates();

			// Sets the neighbors of each elements
			for (int i = 0; i < elements.size(); i++) {
				this.initNeighbors(elements.get(i));
			}

		} catch (NodeException e) {
			e.printStackTrace();
		} catch (ProActiveException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the coordinates of each elements in the grid.
	 */
	public void initCoordinates() {
		int index = 0;

		for (int i = 0; i < this.nbRows; i++) { // Y
			for (int j = 0; j < nbCols; j++) { // X
				this.elements.get(index).setX(j);
				this.elements.get(index++).setY(i);
			}
		}
	}

	/**
	 * Retrieves an {@link AwareObject} by his coordinates.
	 * 
	 * @param x
	 *            the x-coordinate of the object.
	 * @param y
	 *            the y-coordinate of the object.
	 * @return the AwareObject found by coordinates.
	 */
	public AwareObject getByCoordinates(int x, int y) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getX() == x && elements.get(i).getY() == y) {
				return elements.get(i);
			}
		}

		return null;
	}

	/**
	 * Set the neighbors of an given {@link AwareObject}.
	 * 
	 * @param ao
	 *            the AwareObject to set neighbors.
	 */
	public void initNeighbors(AwareObject ao) {
		int myX = ao.getX();
		int myY = ao.getY();

		// North
		if (!this.getByCoordinates(myX, myY + 1).equals(null)) {

			ao.add(this.getByCoordinates(myX, myY + 1));
		}
		// East
		if (!this.getByCoordinates(myX + 1, myY).equals(null)) {
			ao.add(this.getByCoordinates(myX + 1, myY));
		}

		// South
		if (!this.getByCoordinates(myX, myY - 1).equals(null)) {

			ao.add(this.getByCoordinates(myX, myY - 1));
		}
		// West
		if (!this.getByCoordinates(myX - 1, myY).equals(null)) {

			ao.add(this.getByCoordinates(myX - 1, myY));
		}
	}

}
