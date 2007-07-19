package org.objectweb.proactive.examples.scheduler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.objectweb.proactive.extra.scheduler.core.SchedulerFrontend;
import org.objectweb.proactive.extra.scheduler.core.UserScheduler;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobResult;
import org.objectweb.proactive.extra.scheduler.job.UserIdentification;


public class ResultRecup {
    public static void main(String[] args) {
        try {
        	//GET SCHEDULER
			UserScheduler scheduler;
			if (args.length>0){
				scheduler = UserScheduler.connectTo("//"+args[0]+"/"+SchedulerFrontend.SCHEDULER_DEFAULT_NAME);
			} else {
				scheduler = UserScheduler.connectTo(null);
			}

            InputStreamReader reader = new InputStreamReader(System.in);

            // Wrap the reader with a buffered reader.
            BufferedReader buf = new BufferedReader(reader);
            String jID;
            System.out.print("\nPlease enter the job id to get its result or 'exit' to exit :  ");
            while (!(jID = buf.readLine()).equals("exit")) {
            	
            	int begin = 0;
            	int end = 0;
            	if (jID.matches(".* to .*")){
            		String[] TjID = jID.split(" to ");
            		begin = Integer.parseInt(TjID[0]);
            		end = Integer.parseInt(TjID[1]);
            	} else {
	            	begin = Integer.parseInt(jID);
	            	end = Integer.parseInt(jID);
            	}
            	for (int i=begin;i<=end;i++){
            		try {
            			JobResult result = scheduler.getResult(new JobId(i),new UserIdentification("jl","mdp"));
            			if (result != null) {
            				if (!result.exceptionOccured()) {
            					System.out.println("Job "+i+" Result => "+result.getResult());
            				} else {
            					System.out.println("Job "+i+" Error => " + result.getException().getMessage());
            				}
            			} else {
            				System.out.println("Job "+i+" is not finished or unknown !");
            			}
            		} catch (SchedulerException e){
            			System.out.println("Error job "+i+" : "+e.getMessage());
            		}
            	}
                System.out.print("\nPlease enter the job id to get its result or 'exit' to exit :  ");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            //e.printStackTrace();
            System.exit(1);
        }

        System.exit(0);
    }
}
