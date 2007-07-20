package org.objectweb.proactive.examples.scheduler;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.scheduler.core.SchedulerFrontend;
import org.objectweb.proactive.extra.scheduler.core.UserScheduler;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobFactory;
import org.objectweb.proactive.extra.scheduler.job.UserIdentification;

public class JobLauncher {

	public static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);

    public static void main(String[] arg) {

    	final String[] args = arg; 

    	Thread thread = new Thread(){
    		@Override
    		public void run(){
    			try {
    				//GET SCHEDULER
    				UserScheduler scheduler = null;
    				String jobUrl = null;
    				int nbJob = 1;			
    				if (args.length>2){
    					jobUrl = args[0];
    					nbJob = Integer.parseInt(args[1]);
    					scheduler = UserScheduler.connectTo("//"+args[2]+"/"+SchedulerFrontend.SCHEDULER_DEFAULT_NAME);
    				} else if (args.length>1){
    					jobUrl = args[0];
    					nbJob = Integer.parseInt(args[1]);
    					scheduler = UserScheduler.connectTo(null);
    				} else if (args.length>0){
    					jobUrl = args[0];
    					scheduler = UserScheduler.connectTo(null);
    				} else {
    					System.err.println("You must enter a job descriptor");
    					System.exit(0);
    				}
    				
    				//CREATE JOB
    				Job j = JobFactory.getFactory().createJob(jobUrl);
    				for (int i=0;i<nbJob;i++){
    					// SUBMIT JOB
    					j.setId(scheduler.submit(j,new UserIdentification("jl","mdp")));
    				}
    				

    			} catch (Exception e) {
    				System.out.println("Error:" + e.getMessage() + " will exit");
    				e.printStackTrace();
    				System.exit(1);
    			}
    		}
    		
    	};
    	
    	thread.start();

    }
}
