/**
 * 
 */
package org.objectweb.proactive.extensions.scheduler.task;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.Semaphore;

import javax.management.Notification;
import javax.management.NotificationListener;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.jmx.notification.GCMRuntimeRegistrationNotificationData;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.objectweb.proactive.extensions.gcmdeployment.core.StartRuntime;
import org.objectweb.proactive.extensions.scheduler.common.scripting.Script;
import org.objectweb.proactive.extensions.scheduler.common.task.Log4JTaskLogs;
import org.objectweb.proactive.extensions.scheduler.common.task.TaskId;
import org.objectweb.proactive.extensions.scheduler.common.task.TaskResult;
import org.objectweb.proactive.extensions.scheduler.common.task.ThreadReader;
import org.objectweb.proactive.extensions.scheduler.common.task.executable.Executable;
import org.objectweb.proactive.extensions.scheduler.common.task.executable.ForkedJavaExecutable;
import org.objectweb.proactive.extensions.scheduler.common.task.executable.JavaExecutable;
import org.objectweb.proactive.extensions.scheduler.core.SchedulerCore;


/**
 * @author The ProActive Team
 *
 */
public class ForkedJavaTaskLauncher extends JavaTaskLauncher {

    private Process process = null;
    private ProActiveRuntime childRuntime = null;
    private Semaphore semaphore = new Semaphore(0);
    private long deploymentID = -1;
    private String forkedNodeName = null;
    private Thread tsout = null;
    private Thread tserr = null;

    public ForkedJavaTaskLauncher() {
    }

    public ForkedJavaTaskLauncher(TaskId taskId) {
        super(taskId);
    }

    public ForkedJavaTaskLauncher(TaskId taskId, Script<?> pre) {
        super(taskId, pre);
    }

    private void init() {
        Random random = new Random((new Date()).getTime());
        deploymentID = random.nextInt(1000000);

        forkedNodeName = "//localhost/" + this.getClass().getName() + getDeploymentId();
    }

    public TaskResult doTask(SchedulerCore core, Executable executableTask, TaskResult... results) {
        try {
            init();
            currentExecutable = executableTask;

            StringBuffer command = new StringBuffer();
            command.append(" java ");
            String classPath = System.getProperty("java.class.path", ".");
            command.append(" -cp " + classPath + " ");
            command.append(" " + StartRuntime.class.getName() + " ");

            String nodeURL = RuntimeFactory.getDefaultRuntime().getURL();
            command.append(" -p " + nodeURL + " ");
            command.append(" -c 1 ");
            command.append(" -d " + getDeploymentId() + " ");

            RegistrationListener registrationListener = new RegistrationListener();
            registrationListener.subscribeJMXRuntimeEvent();

            process = Runtime.getRuntime().exec(command.toString());
            BufferedReader sout = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader serr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            tsout = new Thread(new ThreadReader(sout, System.out, executableTask));
            tserr = new Thread(new ThreadReader(serr, System.err, executableTask));
            tsout.start();
            tserr.start();

            semaphore.acquire();
            String nodeUrl = childRuntime.createLocalNode(forkedNodeName, true, null, this.getClass()
                    .getName(), null);

            JavaTaskLauncher newLauncher = null;
            if (pre == null) {
                newLauncher = (JavaTaskLauncher) PAActiveObject.newActive(JavaTaskLauncher.class.getName(),
                        new Object[] { taskId }, nodeUrl);
            } else {
                newLauncher = (JavaTaskLauncher) PAActiveObject.newActive(JavaTaskLauncher.class.getName(),
                        new Object[] { taskId, pre }, nodeUrl);
            }
            newLauncher.setWallTime(wallTime);

            ForkedJavaExecutable forkedJavaExecutable = new ForkedJavaExecutable();
            forkedJavaExecutable.setExecutable((JavaExecutable) executableTask);
            forkedJavaExecutable.setTaskLauncher(newLauncher);

            scheduleTimer(forkedJavaExecutable);
            TaskResult result = (TaskResult) forkedJavaExecutable.execute(results);
            cancelTimer();

            if (result == null) {
                return new TaskResultImpl(taskId, new Exception("Walltime exceeded"), new Log4JTaskLogs(
                    this.logBuffer.getBuffer()));
            }

            return result;

        } catch (Throwable ex) {
            return new TaskResultImpl(taskId, ex, new Log4JTaskLogs(this.logBuffer.getBuffer()));
        } finally {
            finalizeTask(core);
        }
    }

    protected void finalizeTask(SchedulerCore core) {
        clean();
        super.finalizeTask(core);
    }

    protected void clean() {
        try {
            childRuntime.killAllNodes();
            process.destroy();
            process.waitFor();
            tsout.join();
            tserr.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private long getDeploymentId() {
        return deploymentID;
    }

    class RegistrationListener implements NotificationListener {

        public void subscribeJMXRuntimeEvent() {
            ProActiveRuntimeImpl part = ProActiveRuntimeImpl.getProActiveRuntime();
            JMXNotificationManager.getInstance().subscribe(part.getMBean().getObjectName(), this);
        }

        @Override
        public void handleNotification(Notification notification, Object handback) {
            String type = notification.getType();

            if (NotificationType.GCMRuntimeRegistered.equals(type)) {
                GCMRuntimeRegistrationNotificationData data = (GCMRuntimeRegistrationNotificationData) notification
                        .getUserData();
                if (data.getDeploymentId() == getDeploymentId()) {
                    childRuntime = data.getChildRuntime();
                    semaphore.release();
                    return;
                }
            }
        }

    }
}
