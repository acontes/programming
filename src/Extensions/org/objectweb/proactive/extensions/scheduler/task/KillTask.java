package org.objectweb.proactive.extensions.scheduler.task;

import java.util.Timer;
import java.util.TimerTask;

import org.objectweb.proactive.extensions.scheduler.common.task.executable.Executable;

public class KillTask {

        private Executable executable;
        private Timer timer;
        private long miliseconds;
        
        public KillTask(Executable executable, long miliseconds) {
                this.executable = executable;
                this.miliseconds = miliseconds;
                timer = new Timer();                
        }
        public void schedule() {
        	timer.schedule(new KillProcess(), miliseconds);
        }

        synchronized public void cancel() {
                timer.cancel();
        }

        synchronized private void kill() {
        	executable.kill();
        }

        class KillProcess extends TimerTask {
                public void run() {
                        kill();
                }
        }
}
