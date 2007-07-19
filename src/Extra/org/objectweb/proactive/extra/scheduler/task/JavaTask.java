package org.objectweb.proactive.extra.scheduler.task;

import java.util.Map;

/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jun 4, 2007
 * @since ProActive 3.2
 */
public abstract class JavaTask implements Task {
	
	/**
	 * Initialization default method for a task.
	 * By default it puts the parameters set in the task descriptor
	 * in the class variables if their names are correctly mapped.
	 * You can override this method to make your own initialisation.
	 * 
	 * @param args a map containing the differents variables names and values.
	 */
	public void init(Map<String, Object> args){
		try{
			for (String key : args.keySet()){
				//TODO make the mapping automatically (seems not to be possible)
				//Field f = this.getClass().getDeclaredField(key);
				//f.set(this, f.getClass().cast(args.get(key)));
				//f.set(this, args.get(key));
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
