package org.objectweb.proactive.examples.scheduler;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobFactory;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerAuthentificationInterface;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerConnection;
import org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface;

public class JobLauncher {

	public static Logger logger = ProActiveLogger.getLogger(Loggers.SCHEDULER);

    public static void main(String[] args) {
		try {
			//GET SCHEDULER
			String jobUrl = null;
			int nbJob = 1;
			SchedulerAuthentificationInterface auth = null;
			if (args.length > 2) {
				jobUrl = args[0];
				nbJob = Integer.parseInt(args[1]);
				auth = SchedulerConnection.join("//" + args[2] + "/" + SchedulerConnection.SCHEDULER_DEFAULT_NAME);
			} else if (args.length > 1) {
				jobUrl = args[0];
				nbJob = Integer.parseInt(args[1]);
				auth = SchedulerConnection.join(null);
			} else if (args.length > 0) {
				jobUrl = args[0];
				auth = SchedulerConnection.join(null);
			} else {
				System.err.println("You must enter a job descriptor");
				System.exit(0);
			}
			UserSchedulerInterface scheduler = auth.logAsUser("chri", "chri");

			//CREATE JOB
			Job j = JobFactory.getFactory().createJob(jobUrl);
			for (int i = 0; i < nbJob; i++) {
				// SUBMIT JOB
				j.setId(scheduler.submit(j));
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}
}
