package org.objectweb.proactive.examples.scheduler;

import java.util.Map;
import org.objectweb.proactive.extra.scheduler.task.JavaTask;
import org.objectweb.proactive.extra.scheduler.task.TaskResult;


public class WaitAndPrint extends JavaTask { 
    /**  */
	private static final long serialVersionUID = 2518295052900092724L;
	public int sleepTime;
    public int number;

	public Object execute(TaskResult... results) {
		 String message;
	        try {
	        	System.err.println("Démarrage de la tache numero "+number);
	        	System.out.println("Parameters are : ");
	        	for(TaskResult tRes : results){
	        		if (tRes.hadException())
						System.out.println("\t "+tRes.getTaskId()+" : "+tRes.getException().getMessage());
					else
						System.out.println("\t "+tRes.getTaskId()+" : "+tRes.value());
	        	}
	            message = java.net.InetAddress.getLocalHost().toString();
	            Thread.sleep(sleepTime * 1000);
	        } catch (Exception e) {
	            message = "crashed";
	            e.printStackTrace();
	        }
	        System.out.println("Terminaison de la tache numero "+number);
	        return ("No." + this.number + " hi from " + message + "\t slept for " +
	        sleepTime + "Seconds");
	}

	@Override
	public void init(Map<String, Object> args) {
		sleepTime = Integer.parseInt((String)args.get("sleepTime"));
		number = Integer.parseInt((String)args.get("number"));
		for (String key : args.keySet()){
			System.out.println("INIT("+number+") : "+key+"="+args.get(key));
		}
	}
	
}
