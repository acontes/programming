/*
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, Concurrent
 * computing with Security and Mobility
 * 
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis Contact:
 * proactive-support@inria.fr
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Initial developer(s): The ProActive Team
 * http://www.inria.fr/oasis/ProActive/contacts.html Contributor(s):
 * 
 * ################################################################
 */
package org.objectweb.proactive.calcium;

/**
 * This class contains a snapshot of the current Skernel statistics.
 * Among others, the following information is kept:
 * 
 * Number of solved root tasks, Number of solved tasks, 
 * Length of the ready queue, Wallclock time, Computation time
 * 
 * @author mleyton
 */
public class Statistics implements java.io.Serializable{

	private int solvedNumberTasks, solvedRootTasks, readyQueueLength; //TODO add failedRootTasks stat
	private long initTime, finitTime, computationTime;
	private long maxCompTime, minCompTime;
	private long maxWallTime, minWallTime;
	
	Skernel skernel;
	ResourceManager manager;
	
	public Statistics(){
		solvedNumberTasks=solvedRootTasks=readyQueueLength=0;
		initTime=finitTime=computationTime=0;
		maxCompTime=maxWallTime=Long.MIN_VALUE;
		minCompTime=minWallTime=Long.MAX_VALUE;
	}
	
	public int getReadyQueueLength(){
		return readyQueueLength;
	}
	
	public void setReadyQueueLength(int readyQueueLength){
		this.readyQueueLength=readyQueueLength;
	}
	
	public void markStart(){
		if(initTime == 0){
			initTime=System.currentTimeMillis();
		}
	}
	
	public void markFinish(){
		if(finitTime == 0){
			finitTime=System.currentTimeMillis();
		}
	}
	
	public int getSolvedNumberOfTasks(){
		return solvedNumberTasks;
	}
	
	public int getSolvedNumberOfRootTasks(){
		return solvedRootTasks;
	}
	
	public synchronized void increaseSolvedTasks(Task<?> task){
		solvedNumberTasks++;
		computationTime+=task.getComputationTime();

		if(task.isRootTask()){
			solvedRootTasks++;
			
			maxWallTime = Math.max(maxWallTime, task.getWallTime());
			minWallTime = Math.min(minWallTime, task.getWallTime());
		}
		
		maxCompTime = Math.max(maxCompTime, task.getComputationTime());
		minCompTime = Math.min(minCompTime, task.getComputationTime());
	}

	public long getComputationTime(){
		return computationTime;
	}
	
	public long getWallclockTime(){
		if(finitTime==0){
			return System.currentTimeMillis()-initTime;
		}
		return finitTime-initTime;
	}
	
	public long getMaxTaskWallTime(){
		return maxWallTime;
	}
	
	public long getMinTaskWallTime(){
		return minWallTime;
	}
	
	public long getAverageComputationTime(){
		
		return (computationTime/solvedRootTasks);
	}
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		String lb=System.getProperty("line.separator");
		
		sb.append("Number of Root tasks solved:").append(getSolvedNumberOfRootTasks()).append(lb);
		sb.append("Total Number of Tasks solved:").append(getSolvedNumberOfTasks()).append(lb);
		sb.append("Wallclock Time:").append(getWallclockTime()).append(lb);
		sb.append("Computation Time:").append(getComputationTime()).append(lb);
		sb.append("Max Task Time:").append(getMaxTaskWallTime()).append(lb);
		sb.append("Min Task Time:").append(getMinTaskWallTime());
		
		return sb.toString();
	}
}