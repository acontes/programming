package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.util.ArrayList;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.util.Deployment;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

/**
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

	public ActiveGrid2D(int row, int col) {
		this.nbRows = row;
		this.nbCols = col;
		this.elements = new ArrayList<AwareObject>(this.nbRows * this.nbCols);
	}

	/**
	 * 
	 * @param descriptor
	 */
	public void createCoordinates(String descriptor) {
		int nbElem = this.nbCols * this.nbRows;

		try {
			ArrayList<GCMVirtualNode> listVn = Deployment.getAllVirtualNodes();

			for (int i = 0; i < nbElem; i++) {

				AwareObject e = (AwareObject) PAActiveObject.newActive(
						AwareObject.class.getName(), new Object[] {}, listVn
								.get(i % listVn.size()).getANode());

				this.elements.add(e);
			}
			// initialisation des cooordonnees
			initCordinates();
			// initialisation de la liste des voisins de chaque element
			for (int i = 0; i < elements.size(); i++) {
				this.initNeighbors(elements.get(i));
			}

		} catch (NodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void initCordinates() {
		int index = 0;

		for (int i = 0; i < this.nbRows; i++) { // Y
			for (int j = 0; j < nbCols; j++) { // X
				this.elements.get(index).setX(j);
				this.elements.get(index++).setY(i);
			}
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
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
	 * 
	 * @param e
	 */
	public void initNeighbors(AwareObject e) {

		int myX = e.getX();
		int myY = e.getY();

		if (!this.getByCoordinates(myX + 1, myY).equals(null)) {
			e.add(this.getByCoordinates(myX + 1, myY));
		}

		if (!this.getByCoordinates(myX, myY + 1).equals(null)) {

			e.add(this.getByCoordinates(myX, myY + 1));
		}

		if (!this.getByCoordinates(myX - 1, myY).equals(null)) {

			e.add(this.getByCoordinates(myX - 1, myY));
		}
		if (!this.getByCoordinates(myX, myY - 1).equals(null)) {

			e.add(this.getByCoordinates(myX, myY - 1));
		}
	}

}
