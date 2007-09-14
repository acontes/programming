package org.objectweb.proactive.examples.scheduler;

import java.io.IOException;
import java.net.UnknownHostException;
import javax.security.auth.login.LoginException;
import org.objectweb.proactive.extra.logforwarder.SimpleLoggerServer;
import org.objectweb.proactive.extra.scheduler.common.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.common.job.JobId;
import org.objectweb.proactive.extra.scheduler.common.job.JobPriority;
import org.objectweb.proactive.extra.scheduler.common.job.JobResult;
import org.objectweb.proactive.extra.scheduler.common.scheduler.SchedulerAuthenticationInterface;
import org.objectweb.proactive.extra.scheduler.common.scheduler.SchedulerConnection;
import org.objectweb.proactive.extra.scheduler.common.scheduler.UserSchedulerInterface;
import org.objectweb.proactive.extra.scheduler.common.task.ExecutableJavaTask;
import org.objectweb.proactive.extra.scheduler.common.task.TaskResult;
import org.objectweb.proactive.extra.scheduler.job.InternalJob;
import org.objectweb.proactive.extra.scheduler.job.InternalTaskFlowJob;
import org.objectweb.proactive.extra.scheduler.task.internal.InternalJavaTask;


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
            SchedulerAuthenticationInterface auth = SchedulerConnection.join(
                    "//localhost/" +
                    SchedulerConnection.SCHEDULER_DEFAULT_NAME);

            //Now you are connected you must log on with a couple username/password matching an entry in login and group files.
            //(groups.cfg, login.cfg in the same directory)
            //you can also log on as admin if your username is in admin group. (it provides you more power ;) )
            UserSchedulerInterface scheduler = auth.logAsUser("chri", "chri");

            //if this point is reached, that's we are connected to the scheduler under "chri".

            //******************** CREATE A NEW JOB ***********************
            //params are respectively : name, priority, runtimeLimit (not yet implemented), 
            //							reUntilCancel  (not yet implemented), description.
            InternalJob job = new InternalTaskFlowJob("job name", JobPriority.NORMAL, -1,
                    false, "A simple hello world example !");

            //******************** CREATE A NEW TASK ***********************
            //creating a new task
            ExecutableJavaTask task = new ExecutableJavaTask() {
                    private static final long serialVersionUID = 1938122426482626365L;

                    public Object execute(TaskResult... results) {
                        System.out.println("Hello World !");
                        try {
                            return "HelloWorld Sample host : " +
                            java.net.InetAddress.getLocalHost().toString();
                        } catch (UnknownHostException e) {
                            return "HelloWorld Sample host : unknow host";
                        }
                    }
                };

            //adding the task to the job
            InternalJavaTask desc = new InternalJavaTask(task);
            //this task is final, it means that the job result will contain this task result.
            desc.setFinalTask(true);
            job.addTask(desc);

            //******************** SUBMIT THE JOB ***********************
            //submitting a job to the scheduler returns the attributed jobId
            //this id will be used to talk the scheduler about this job.
            JobId jobId = scheduler.submit(job);

            //******************** GET JOB OUTPUT ***********************
            SimpleLoggerServer simpleLoggerServer;
            try {
                // it will launch a listener that will listen connection on any free port
                simpleLoggerServer = SimpleLoggerServer.createLoggerServer();
                // next, this method will forward task output on the previous loggerServer
                scheduler.listenLog(jobId,
                    java.net.InetAddress.getLocalHost().getHostName(),
                    simpleLoggerServer.getPort());
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //******************** GET JOB RESULT ***********************
            // it is better to get the result when the job is terminated.
            // if you want the result as soon as possible we suggest this loop.
            // In the futur you could get the result like a futur in ProActive or with a listener.
            JobResult result = null;
            while (result == null) {
                try {
                    Thread.sleep(2000);
                    result = scheduler.getResult(jobId);
                    //the result is null if the job is not finished.
                } catch (SchedulerException se) {
                    se.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Result : " + result);
        } catch (SchedulerException e) {
            //the scheduler had a problem
            e.printStackTrace();
        } catch (LoginException e) {
            //there was a problem during scheduler authentication
            e.printStackTrace();
        }
    }
}
