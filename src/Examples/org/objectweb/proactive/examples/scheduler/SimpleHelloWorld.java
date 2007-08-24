package org.objectweb.proactive.examples.scheduler;

import java.net.UnknownHostException;
import javax.security.auth.login.LoginException;
import org.objectweb.proactive.extra.logforwarder.SimpleLoggerServer;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobPriority;
import org.objectweb.proactive.extra.scheduler.job.JobResult;
import org.objectweb.proactive.extra.scheduler.job.TaskFlowJob;
import org.objectweb.proactive.extra.scheduler.task.JavaTask;
import org.objectweb.proactive.extra.scheduler.task.TaskResult;
import org.objectweb.proactive.extra.scheduler.task.descriptor.JavaTaskDescriptor;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerAuthenticationInterface;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerConnection;
import org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface;


/**
 * Here is a class that explains how to simply use the scheduler.
 * You'll create a one task job that will print "HelloWorld !".
 * You'll be able to get the output of the task and/or get the result.
 * According that the API is not yet full implmented,
 * adding task and job creation are not on their final implementation.
 * TaskDescriptor may also be removed from user view.
 * 
 * @author jlscheef
 *
 */
public class SimpleHelloWorld {

	public static void main(String[] args) {
		try {
			//*********************** GET SCHEDULER *************************
			//get authentication interface from existing scheduler based on scheduler host URL
			//(localhost) followed by the scheduler name (here the default one)
			SchedulerAuthenticationInterface auth = SchedulerConnection.join("//localhost/"+SchedulerConnection.SCHEDULER_DEFAULT_NAME);
			//Now you are connected you must log on with a couple username/password matching an entry in login and group files.
			//(groups.cfg, login.cfg in the same directory)
			//you can also log on as admin if your username is in admin group. (it provides you more power ;) )
			UserSchedulerInterface scheduler = auth.logAsUser("chri", "chri");
			//if this point is reached, that's we are connected to the scheduler under "chri".
			
			//******************** CREATE A NEW JOB ***********************
			//params are respectively : name, priority, runtimeLimit (not yet implemented), 
			//							reUntilCancel  (not yet implemented), description.
			Job job = new TaskFlowJob("job name",JobPriority.NORMAL,-1,false,"A simple hello world example !");
			
			//******************** CREATE A NEW TASK ***********************
			//creating a new task
			JavaTask task = new JavaTask(){
				private static final long serialVersionUID = 1938122426482626365L;
				public Object execute(TaskResult... results) {
					System.out.println("Hello World !");
					try {
						return "HelloWorld Sample host : "+java.net.InetAddress.getLocalHost().toString();
					} catch (UnknownHostException e) {
						return "HelloWorld Sample host : unknow host";
					}
				}
			};
			//adding the task to the job
			JavaTaskDescriptor desc = new JavaTaskDescriptor(task);
			//this task is final, it means that the job result will contain this task result.
			desc.setFinalTask(true);
			job.addTask(desc);
			
			//******************** SUBMIT THE JOB ***********************
			//submitting a job to the scheduler returns the attributed jobId
			//this id will be used to talk the scheduler about this job.
			JobId jobId = scheduler.submit(job);
			
			//******************** GET JOB OUTPUT ***********************
			// start the log server (should be simplify later ... ;))
			// it will launch a listener that will listen connection on port 2987
			final int port = 2987;
			SimpleLoggerServer simpleLoggerServer = new SimpleLoggerServer(port);
			Thread simpleLoggerServerThread = new Thread(simpleLoggerServer);
			simpleLoggerServerThread.start();
			// next, this method will forward task output on the previous loggerServer
			scheduler.listenLog(jobId, java.net.InetAddress.getLocalHost().getHostName(), port);
			
			//******************** GET JOB RESULT ***********************
			// it is better to get the result when the job is terminated.
			// if you want the result as soon as possible we suggest this loop.
			// In the futur you could get the result like a futur in ProActive or with a listener.
			JobResult result = null;
			while (result == null){
				try {
					Thread.sleep(2000);
					result = scheduler.getResult(jobId);
					//the result is null if the job is not finished.
				} catch(SchedulerException se){
					se.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.out.println("Result : "+result);
		} catch (SchedulerException e) {
			//the scheduler had a problem
			e.printStackTrace();
		} catch (LoginException e) {
			//there was a problem during scheduler authentication
			e.printStackTrace();
		} catch (UnknownHostException e) {
			//due to "java.net.InetAddress.getLocalHost().getHostName()"
			e.printStackTrace();
		}
	}

}
