/*
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2006 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */

/**
 * 
 *
 * @author walzouab
 *
 */

package org.objectweb.proactive.taskscheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


public class AdminCommunicator {

	
	
	private static AdminScheduler scheduler;
	
	 
	 	
   
  
    private static final String STAT_CMD = "stat";
    private static final String STAT_ADV_CMD = "stat_adv";
    
    private static final String DEL_CMD = "del";
    private static final String START_CMD = "start";
    private static final String STOP_CMD = "stop";
    private static final String KILLALL_CMD = "killallrunning";
    private static final String FLUSH_CMD = "flush_queue";
    private static final String SHUTDOWN_IMMEDIATE_CMD="shutdown_immediate";
    private static final String SHUTDOWN_SOFT_CMD="shutdown";
    	
    private static final String EXIT_CMD = "exit";
   
    
       
	private static boolean stopCommunicator;
	/**
	 * @param args
	 */
	private static void output(String message)
	{
		System.out.print(message);
	}
	private static void error(String message)
	{
		System.err.print(message);
	}
	
	public static void main(String[] args) {
		
		if (args.length != 1) {		
			output("Usage <java_command> Scheduler_URL\n");
    	}
		else
		{
		try
			{
				scheduler=AdminScheduler.connectTo(args[0]);
				stopCommunicator=false;
				output("<----Communicator connected to "+args[0]+" ---->\n");
				startCommandListener();
								
			}
			catch(Exception e)
			{
			error("A fatal error has occured:"+ e.getMessage()+ "\n Will shut down communicator.\n");
			System.exit(1);
				
			}
			
			
			
		}
	    
		
		// if exxecution reaches this point this means it must exit
		System.exit(0);
	}
	
	 private static void  handleCommand(String command) {
	        
	        if (command.equals("")) {
	            
	        }
	        else if (command.equals(EXIT_CMD)) {
	            output("Communicator will exit.\n");
	        	stopCommunicator=true;
	        }
	        else if (command.equals("?")) {
	        	
	        	helpScreen();
	            
	        }
	        else if (command.equals(START_CMD))
	        {
	        	boolean success=scheduler.start().booleanValue();
	        	if(success)
	        		output("Scheduler started.\n");
	        	else output("Scheduler already started!!\n");
	        }

	        else if (command.equals(STOP_CMD))
	        {
	        	boolean success=scheduler.stop().booleanValue();
	        	if(success)
	        		output("Scheduler stopped.\n");
	        	else output("Scheduler already stopped!!\n");
	        }
	        else if (command.equals(FLUSH_CMD))
	        {
	        	scheduler.flushqueue();
	        	output("Scheduler queue flushed\n");
	        }
	        else if (command.equals(KILLALL_CMD))
	        {
	        	scheduler.killAllRunning();
	        	
	        	output("Running tasks killed\nWARNING, scheduler is still scheduling.\n ");
	        }
	        else if (command.equals(SHUTDOWN_IMMEDIATE_CMD))
	        {
	        	scheduler.shutdown(new BooleanWrapper(true));
	        	 output("Scheduler is shutdown, communicator will exit.\n");
		        	stopCommunicator=true;
	        }
	        else if (command.equals(SHUTDOWN_SOFT_CMD))
	        {
	        	scheduler.shutdown(new BooleanWrapper(false));
	        	 output("A Soft shutdown intiated, it might take a while to finish all executions, communicator will exit.\n");
		        	stopCommunicator=true;
	        }
	        	
	        else if (command.equals(STAT_ADV_CMD)) {
	        	
	        	
	        	String out="";
	        	Vector<Info> info=scheduler.info_all();
	        	Info tempInfo;
	        	long tempTime;
	        
	        	while(!info.isEmpty())
	        	{
	        		tempInfo=info.remove(0);
	        		
	        		
	        		out+=String.format("%1$-6s\t",tempInfo.getTaskID());
	        		out+=String.format("%1$-8s\t",tempInfo.getStatus().toString());
	        		out+=String.format("%1$-8s\t",tempInfo.getUserName());
	        		
	        		
	        		
	        		tempTime=tempInfo.getTimeCreated();
	        		if (tempTime<0)//indicates an unvalid Time
	        			out+=String.format("%1$-20s\t","Not Available yet");
	        		else
	        		out+=String.format("%1$td/%1$tb/%1$tY %1$tH:%1$tM:%1$tS\t", Long.valueOf(tempTime));
	        		
	        		tempTime=tempInfo.getTimeScheduled();
	        		if (tempTime<0)//indicates an unvalid Time
	        			out+=String.format("%1$-20s\t","Not Available yet");
			  		else
			  		out+=String.format("%1$td/%1$tb/%1$tY %1$tH:%1$tM:%1$tS\t", Long.valueOf(tempTime));
			  		
	        		
	        		tempTime=tempInfo.getTimeFinished();
	        		if (tempTime<0)//indicates an unvalid Time
  					  out+=String.format("%1$-20s\t","Not Available yet");
			  		else
			  		out+=String.format("%1$td/%1$tb/%1$tY %1$tH:%1$tM:%1$tS\t", Long.valueOf(tempTime));
			  		

	        		
	        		
	        	
	        		out+=tempInfo.getNodeURL()+"\n";
	        		
	        		
	        	}
	        	
	        	
	        	
	            
	        	if(out.length()==0)
	        		output("Scheduler is Empty\n");
	        		
	        		else			
	        		output(String.format("%1$-6s\t%2$-8s\t%3$-8s\t%4$-20s\t%5$-20s\t%6$-20s\t%7$-5s\n","TaskID","Status","UserName","Submitted","Started","Finished","NodeURL")+out);
	        }
	        else if (command.equals(STAT_CMD)) {
	        	
	        	
	        	String out="";
	        	Vector<Info> info=scheduler.info_all();
	        	Info tempInfo;
	        	long tempTime;
	        
	        	while(!info.isEmpty())
	        	{
	        		tempInfo=info.remove(0);
	        		
	        		
	        		out+=String.format("%1$-6s\t",tempInfo.getTaskID());
	        		out+=String.format("%1$-8s\t",tempInfo.getStatus().toString());
	        		out+=String.format("%1$-8s\t",tempInfo.getUserName());
	        		
	        		
	        		
	        		tempTime=tempInfo.getTimeCreated();
	        		if (tempTime<0)//indicates an unvalid Time
	        			out+=String.format("%1$-20s\t","Not Available yet");
	        		else
	        		out+=String.format("%1$td/%1$tb/%1$tY %1$tH:%1$tM:%1$tS\t", Long.valueOf(tempTime));
	        		
	        		
	        		out+="\n";
	        		
	        	}
	        	
	        	
	        	
	            
	        	if(out.length()==0)
	        		output("Scheduler is Empty\n");
	        		
	        		else			
	        		output(String.format("%1$-6s\t%2$-8s\t%3$-8s\t%4$-20s\n","TaskID","Status","UserName","Submitted")+out);
	        }

	        else if(command.startsWith(STAT_CMD))
	        {
	        	String taskID = command.substring(command.indexOf(' ') + 1);
	        	
	        	
	        	Status status=scheduler.status(taskID);
	        	        	
	        	
	        	
	        	output("Task "+taskID+" is "+status.toString()+"\n");
	        	
	        }
	        else if (command.startsWith(DEL_CMD)) {
	           	String taskID = command.substring(command.indexOf(' ') + 1);
	           	boolean deleted=scheduler.del(taskID).booleanValue();
	           	if(deleted)
	           		output("Task "+taskID+" deleted\n");
	           	else
	           		output("Task "+taskID+" cannot be deleted, please check its status, it must be either running or queued\n");
	           				
		        	
		   
	        }
	        else
	        {
	        	 output("UNKNOWN COMMAND!!... Please type \'?\' to see a list of commands\n");
	        }
	        
	    }	
	 private static void startCommandListener() throws Exception{
	        
	        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	        while(!stopCommunicator) {
	          
	                output(" > ");
	                String line = reader.readLine();
	                handleCommand(line);
	            
	        }
	    }

	 
	 private static void helpScreen()
	 {
		 output("Communicator Commands are:\n\n");
		 output(STAT_CMD+ ": gets the status of all tasks\n\n");
		 output(STAT_CMD+" <taskID> : gets the status of a specific tasks\n\n");   
		 output(DEL_CMD+" <taskID> : deletes a specific tasks, must be either queued or running\n\n");
		 output(START_CMD+ ": starts scheduling\n\n");		    
		 output(STOP_CMD+ ": stops scheduling\n\n");
		 output(KILLALL_CMD+ ": kills all running tasks\n\n");
		 output(FLUSH_CMD+ ": deletes all queued tasks\n\n"); 
		 output(SHUTDOWN_IMMEDIATE_CMD+ ": kills all running tasks and shutsdown immediately\n\n");
		 output(SHUTDOWN_SOFT_CMD+ ": waits for running tasks to finish and shutsdown immediately\n\n");
		 output(EXIT_CMD+ ": exits communicator\n\n");
		    
		    
		    
		 
		    	
		    
		 
	 }
}
