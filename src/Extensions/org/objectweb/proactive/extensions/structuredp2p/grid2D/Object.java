package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.EndActive;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.RunActive;

public class Object implements InitActive, RunActive, EndActive {

	private int x;
	private int y;

	private Object[] neighbors = new Object[4];
	private int index = 1;

	public Object() {

	}

	public Object(int x, int y) {

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

	public Object[] getNeighbors() {
		return neighbors;
	}

	public void setNeighbors(Object[] neighbors) {
		this.neighbors = neighbors;
	}

	public void add(Object e) {
		this.neighbors[this.index] = e;
		this.index++;
	}
}
