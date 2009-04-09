package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.util.ArrayList;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.EndActive;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;

public class Element implements InitActive, RunActive, EndActive {

	private int x;
	private int y;
	private ArrayList<Element> neighbors = new ArrayList<Element>();

	public Element() {

	}

	public Element(int x, int y) {

	}

	@Override
	public void initActivity(Body arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void runActivity(Body arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void endActivity(Body arg0) {
		// TODO Auto-generated method stub

	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public ArrayList<Element> getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(ArrayList<Element> neighbors) {
		this.neighbors = neighbors;
	}

}
