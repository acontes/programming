package org.objectweb.proactive.examples.components.jacobi;

import java.io.Serializable;

public class LineData implements Serializable {
	
	public static final Integer NORTH=0;
	public static final Integer SOUTH=1;
	public static final Integer WEST=2;
	public static final Integer EAST=3;
	
	public Integer position;
	
	public double[] data;
	
	int iteration;
	String coordinates;
	
	public LineData() {}
	
	public LineData(Integer position, double[] data) {
		this.position = position;
		this.data = data;
	}

	public LineData(Integer position, double[] data, int iteration, String coordinates) {
		this(position,data);
		this.iteration = iteration;
		this.coordinates = coordinates;
		
	}


	public double[] getData() {
		return data;
	}


	public Integer getPosition() {
		return position;
	}
	

	public int getIteration() {
		return iteration;
	}

	public String getCoordinates() {
		return coordinates;
	}
	
	
	

}
