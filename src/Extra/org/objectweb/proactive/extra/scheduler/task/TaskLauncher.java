package org.objectweb.proactive.extra.scheduler.task;

import java.io.PrintStream;
import java.io.Serializable;

import org.apache.log4j.Appender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.net.SocketAppender;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.logforwarder.LoggingOutputStream;
import org.objectweb.proactive.extra.scheduler.core.SchedulerCore;
import org.objectweb.proactive.extra.scheduler.exception.UserException;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.scripting.Script;
import org.objectweb.proactive.extra.scheduler.scripting.ScriptHandler;
import org.objectweb.proactive.extra.scheduler.scripting.ScriptLoader;
import org.objectweb.proactive.extra.scheduler.scripting.ScriptResult;


/**
 * Task Launcher.
 * 
 * @author ProActive Team
 * @version 1.0, Jul 10, 2007
 * @since ProActive 3.2
 */
public class TaskLauncher implements InitActive, Serializable {

	private static final long serialVersionUID = -9159607482957244049L;
	private TaskId taskId;
	private JobId jobId;
	private Script<?> pre;
	private String host;
	private Integer port; 

	/**
	 * ProActive empty constructor.
	 * 
	 */
	public TaskLauncher() {}

	
	/**
	 * Constructor with task identification
	 * 
	 * @param taskId represents the task the launcher will execute.
	 * @param jobId represents the job where the task is located.
	 */
	public TaskLauncher(TaskId taskId, JobId jobId, String host, Integer port) {
		this.taskId = taskId;
		this.jobId = jobId;
		this.host = host;
		this.port = port;
	}
	
	
	/**
	 * Constructor with task identification
	 * 
	 * @param taskId represents the task the launcher will execute.
	 * @param jobId represents the job where the task is located.
	 */
	public TaskLauncher(TaskId taskId, JobId jobId, Script<?> pre, String host, Integer port) {
		this(taskId,jobId,host,port);
		this.pre = pre;
	}
	

	/**
     * Initializes the activity of the active object.
     * @param body the body of the active object being initialized
     */
	public void initActivity(Body body) {
		ProActive.setImmediateService("getNode");
	}
	
	
	/**
	 * Execute the user task as an active object.
	 * 
	 * @param core The scheduler core to be notify
	 * @param task the task to execute
	 * @param results the possible results from parent tasks.(if task flow)
	 * @return a task result representing the result of this task execution.
	 */
	@SuppressWarnings("unchecked")
	public TaskResult doTask(SchedulerCore core, Task task, TaskResult... results) {
		//handle loggers
       	Appender out = new SocketAppender(host,port);
       	// store stdout and err
       	PrintStream stdout = System.out;
        PrintStream stderr = System.err;
       	// create logger
       	Logger l = Logger.getLogger(SchedulerCore.LOGGER_PREFIX+jobId);
        l.removeAllAppenders();
        l.addAppender(out);
        // redirect stdout and err
        System.setOut(new PrintStream(new LoggingOutputStream(l,Level.INFO), true));
        System.setErr(new PrintStream(new LoggingOutputStream(l,Level.ERROR), true));
		try {
			//launch pre script
			if (pre != null){
	        	ScriptHandler handler = ScriptLoader.createHandler(null);
	        	ScriptResult<Object> res = handler.handle(pre);
	        	if(res.errorOccured()){
	        		System.err.println("Error on pre-script occured : ");
	        		res.getException().printStackTrace();
	        		throw new UserException("PreTask script has failed on the current node");
	        	}
        	}
			//launch task
            TaskResult result = new TaskResult(taskId, task.execute(results));
            //return result
            return result;
		} catch (Exception ex) {
			return new TaskResult(taskId, ex);
		} finally {
			//Unhandle loggers
            LogManager.shutdown();
            System.setOut(stdout);
            System.setErr(stderr);
			core.terminate(taskId, jobId);	
		}
	}
	
	
	/**
	 * To get the node on which this active object has been launched.
	 * 
	 * @return the node of this active object.
	 * @throws NodeException
	 */
	public Node getNode() throws NodeException{
		return ProActive.getNode();
	}
	
}
