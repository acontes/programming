package org.objectweb.proactive.core.group.spmd.topology;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represent the latency between two Active Objects
 */
public class Latency implements Comparable<Latency>, Serializable {

	/**
	 * Max number of stored latencies
	 */
	private static final int MAX_SIZE = 40;
	
	private int destRank;
	private int sourceRank;
	private int nbData = 0;
	private double average = -1;
	private double min = -1;
	private double max = -1;
	private double ecartType = -1;
	private transient double oldAverage = -1;
	
	/**
	 * Stored latencies
	 */
	private transient List<Short> values;

	public Latency(){
	}
	
	public Latency(int sourceRank, int destRank) {
		this.destRank = destRank;
		this.sourceRank = sourceRank;
		this.values = new ArrayList<Short>(MAX_SIZE);
	}

public int total = 0;
	/**
	 * Add a measure of latency
	 * 
	 * @param latency measure of latency
	 */
	public void add(double latency) {
		total ++;
		if(nbData >= MAX_SIZE){
			average = (average * nbData - values.get(0)) / (nbData-1);
			nbData--;
			values.remove(0);
		}
		add(latency, 1);
		values.add((short)latency);
	}

	/**
	 * Add 'nb' measure of latency which have the same value 'latency'
	 * 
	 * @param latency value of the measurements
	 * @param nb number of measurements
	 */
	public void add(double latency, int nb) {
		if (latency >= 0) {
			if (nbData == 0) {
				average = latency;
				min = latency;
				max = latency;
			} else {
				average = (average * nbData + latency * nb) / (nbData + nb);
				if (latency < min)
					min = (int) latency;
				if (latency > max)
					max = (int) latency;
			}
			nbData += nb;
		}
	}

	public void reset() {
		nbData = 0;
		average = 0;
		min = 0;
		max = 0;
		ecartType = 0;
		values.clear();
	}

	public int getNbData() {
		return nbData;
	}

	public double getAverage() {
		return average;
	}

	public double getEcartType() {
		if (values.size() > 0)
			calculateEcartType();
		return ecartType;
	}

	public void setEcartType(double ecartType) {
		this.ecartType = ecartType;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public int getSourceRank() {
		return sourceRank;
	}

	public int getDestRank() {
		return destRank;
	}

	public void calculateEcartType() {
		double res = 0;
		for(int i = 0; i < values.size(); i++){
			Short val = values.get(i);
			if(val != null)
				res += (val - average) * (val - average);
		}
		res /= getNbData();
		ecartType = Math.sqrt(res);
	}

	@Override
	public int compareTo(Latency o) {
		if(o == null) return -1;
		double average = o.getAverage();
		double myAverage = getAverage();
		if (myAverage < 0) {
			if (average < 0)
				return 0;
			return 1;
		}
		if (average < 0)
			return -1;
		if (myAverage > average)
			return 1;
		if (myAverage < average)
			return -1;
		return 0;
	}

	/**
	 * Return the first Latency of the array whose sourceRank equals rank
	 * 
	 * @param latencies array of Latency
	 * @param rank rank to match
	 * @return the first Latency of the array whose sourceRank equals rank
	 */
	public static Latency search(Latency[] latencies, int rank) {
		for (int i = 0; i < latencies.length; i++) {
			if (latencies[i].getDestRank() == rank)
				return latencies[i];
		}
		return null;
	}

	/**
	 * Return a copy of the object
	 * @return a copy of the object
	 */
	public Latency copy(){
		Latency copy = new Latency(sourceRank, destRank);
		copy.average = average;
		copy.max = max;
		copy.min = min;
		copy.nbData = nbData;
		copy.ecartType = ecartType;
		return copy;
	}


	public void storeAverage(){
		this.oldAverage = average;
	}

	public double getOldAverage(){
		return oldAverage;
	}
	
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		values = new ArrayList<Short>(MAX_SIZE);
	}
}
