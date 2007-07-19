package org.objectweb.proactive.extra.scheduler.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Jun 29, 2007
 * @since ProActive 3.2
 */
public class NativeShellTaskDescriptor extends NativeTaskDescriptor {

	private static final long serialVersionUID = 2587936204570926300L;
	private String cmd;

	/**
	 * @see org.objectweb.proactive.extra.scheduler.task.TaskDescriptor#getTask()
	 */
	@Override
	public Task getTask() {
		NativeTask nativeTask = new NativeTask() {
			private static final long serialVersionUID = 0L;

			public Object execute(TaskResult... results) {
				try {
					// TODO lire la sortie, recup le code de retour
					// TODO Gerer les différents exec (windows, linux, etc...)
					Process p = Runtime.getRuntime().exec(cmd);
					StreamConsumer stdout = new StreamConsumer(p.getInputStream(), "stdout");
					StreamConsumer stderr = new StreamConsumer(p.getInputStream(), "stderr");
					stdout.start();
					stderr.start();
					return p.exitValue();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		return nativeTask;
	}
	
	class StreamConsumer extends Thread {
        InputStream is;
        String type;

        /**
         *
         */
        public StreamConsumer(InputStream inputStream, String type) {
            this.is = inputStream;
            this.type = type;
        }

        /**
         * Runs this object as a separate thread, printing the contents of the InputStream
         * supplied during instantiation, to either stdout or stderr
         */
        public void run() {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(is));
                String line = null;

                while ((line = br.readLine()) != null) {
                    if(type.equalsIgnoreCase("stderr")) {
                        System.err.println(line);
                    } else {
                        System.out.println(line);
                    }
                }
            } catch (IOException ioe) {
                System.err.println("Error consuming " + type + " stream of spawned process : " + ioe.getMessage());
            } finally {
                if(br != null) {
                    try { br.close(); } catch(Exception ignore) {}
                }
            }
        }
    }
}
