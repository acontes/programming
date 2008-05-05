package org.objectweb.proactive.extensions.scheduler.common.task.executable;

import java.util.Map;

import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.ProActiveTimeoutException;
import org.objectweb.proactive.core.body.future.FutureMonitoring;
import org.objectweb.proactive.core.body.future.FutureProxy;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.extensions.scheduler.common.task.TaskResult;
import org.objectweb.proactive.extensions.scheduler.task.TaskLauncher;


public class ForkedJavaExecutable extends JavaExecutable {

    private JavaExecutable executable = null;
    private TaskLauncher taskLauncher = null;

    @Override
    public Object execute(TaskResult... results) throws Throwable {
        TaskResult result = taskLauncher.doTask(null, executable, results);
        while (!isKilled()) {
            try {
                PAFuture.waitFor(result, 1000);
                break;
            } catch (ProActiveTimeoutException e) {
            }
        }
        if (isKilled()) {
            FutureMonitoring.removeFuture(((FutureProxy) ((StubObject) result).getProxy()));
            return null;
        }
        return result;
    }

    @Override
    public void init(Map<String, Object> args) throws Exception {
    }

    @Override
    public void kill() {
        executable.kill();
        super.kill();
    }

    /**
     * @return the executable
     */
    public JavaExecutable getExecutable() {
        return executable;
    }

    /**
     * @param executable the executable to set
     */
    public void setExecutable(JavaExecutable executable) {
        this.executable = executable;
    }

    /**
     * @return the taskLauncher
     */
    public TaskLauncher getTaskLauncher() {
        return taskLauncher;
    }

    /**
     * @param taskLauncher the taskLauncher to set
     */
    public void setTaskLauncher(TaskLauncher taskLauncher) {
        this.taskLauncher = taskLauncher;
    }
}
