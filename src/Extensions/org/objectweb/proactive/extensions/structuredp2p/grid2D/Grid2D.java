package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.util.ArrayList;
import java.io.File; //import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject; //import org.objectweb.proactive.api.PALifeCycle;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

public class Grid2D {
	// nombre de ligne
	private int lig;
	// nombre de colonne
	private int col;
	// elements de la grille
	private ArrayList<Element> elements;

	private static GCMApplication pad;

	public Grid2D(int lig, int col) {
		this.lig = lig;
		this.col = col;
		elements = new ArrayList<Element>(this.lig * this.col);
	}

	/**
	 * 
	 * @param descriptor
	 * @return
	 * @throws NodeException
	 * @throws ProActiveException
	 */
	public ArrayList<GCMVirtualNode> deploy(String descriptor)
			throws NodeException, ProActiveException {

		ArrayList<GCMVirtualNode> listVn = new ArrayList<GCMVirtualNode>();
		// 1. Create object representation of the deployment file
		pad = PAGCMDeployment.loadApplicationDescriptor(new File(descriptor));
		// 2. Activate all Virtual Nodes
		pad.startDeployment();
		// 3. Wait for all the virtual nodes to become ready
		pad.waitReady();
		// 4. Get all Virtual Nodes specified in the descriptor file
		while (!pad.getVirtualNodes().values().iterator().next().equals(null)) {
			GCMVirtualNode vn = (GCMVirtualNode) pad.getVirtualNodes().values();
			listVn.add(vn);
		}
		// GCMVirtualNode vn = pad.getVirtualNodes().values().iterator().next();
		// 5. Return the list of virtual node
		return listVn;
	}

	/**
	 * 
	 * @param descriptor
	 */
	public void newAo(String descriptor) {

		int nbElem = this.col * this.lig;
		try {
			// recuperation des vn
			ArrayList<GCMVirtualNode> listVn = this.deploy(descriptor);
			// creation des oa
			for (int i = 0; i < nbElem; i++) {

				Element e = (Element) PAActiveObject.newActive(Element.class
						.getName(), new Object[] {}, listVn.get(
						i % listVn.size()).getANode());

				elements.add(e);
			}
			// initialisation des cooordonnees
			initCord();
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

	/**
 * 	
 */
	public void initCord() {
		int index = 0;

		for (int i = 0; i < lig; i++) { // Y
			for (int j = 0; j < col; j++) { // X
				elements.get(index).setX(j);
				elements.get(index++).setY(i);
			}
		}
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public Element getByCord(int x, int y) {
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
	public void initNeighbors(Element e) {
		
		int myX = e.getX();
		int myY = e.getY();

		if (!this.getByCord(myX + 1, myY).equals(null)) {
			
			e.getNeighbors().add(this.getByCord(myX + 1, myY));
		}

		if (!this.getByCord(myX, myY + 1).equals(null)) {
			
			e.getNeighbors().add(this.getByCord(myX, myY + 1));
		}

		if (!this.getByCord(myX - 1, myY).equals(null)) {
			
			e.getNeighbors().add(this.getByCord(myX - 1, myY));
		}
		if (!this.getByCord(myX, myY - 1).equals(null)) {
			
			e.getNeighbors().add(this.getByCord(myX, myY - 1));
		}
	}

}
