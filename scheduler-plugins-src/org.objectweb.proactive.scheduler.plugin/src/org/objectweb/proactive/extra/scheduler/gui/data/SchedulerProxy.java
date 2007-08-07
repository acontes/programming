package org.objectweb.proactive.extra.scheduler.gui.data;

import javax.security.auth.login.LoginException;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface;
import org.objectweb.proactive.extra.scheduler.core.Stats;
import org.objectweb.proactive.extra.scheduler.exception.SchedulerException;
import org.objectweb.proactive.extra.scheduler.gui.dialog.SelectSchedulerDialogResult;
import org.objectweb.proactive.extra.scheduler.job.Job;
import org.objectweb.proactive.extra.scheduler.job.JobId;
import org.objectweb.proactive.extra.scheduler.job.JobResult;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerAuthenticationInterface;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerConnection;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener;
import org.objectweb.proactive.extra.scheduler.userAPI.SchedulerState;
import org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface;

/**
 * 
 * 
 * @author ProActive Team
 * @version 1.0, Aug 1, 2007
 * @since ProActive 3.2
 */
public class SchedulerProxy implements AdminSchedulerInterface {

	public static final int CONNECTED = 0;
	public static final int LOGIN_OR_PASSWORD_WRONG = 1;
	public static final int COULD_NOT_CONNECT_SCHEDULER = 2;

	private static final long serialVersionUID = 3783194235036041589L;

	private static SchedulerProxy instance = null;
	private UserSchedulerInterface scheduler = null;
	private String userName = null;
	private Boolean logAsAdmin = false;

	// -------------------------------------------------------------------- //
	// --------------------------- constructor ---------------------------- //
	// -------------------------------------------------------------------- //
	public SchedulerProxy() {}

	// -------------------------------------------------------------------- //
	// ---------------- implements AdminSchedulerInterface ---------------- //
	// -------------------------------------------------------------------- //
	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#addSchedulerEventListener(org.objectweb.proactive.extra.scheduler.userAPI.SchedulerEventListener)
	 */
	@Override
	public SchedulerState addSchedulerEventListener(SchedulerEventListener listener) {
		try {
			return scheduler.addSchedulerEventListener(listener);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#disconnect()
	 */
	@Override
	public void disconnect() {
		try {
			scheduler.disconnect();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#getResult(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	@Override
	public JobResult getResult(JobId jobId) {
		try {
			return scheduler.getResult(jobId);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#getStats()
	 */
	@Override
	public Stats getStats() {
		try {
			return scheduler.getStats();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#kill(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	@Override
	public boolean kill(JobId jobId) {
		try {
			return scheduler.kill(jobId);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#listenLog(org.objectweb.proactive.extra.scheduler.job.JobId,
	 *      java.lang.String, int)
	 */
	@Override
	public void listenLog(JobId jobId, String arg1, int arg2) {
		try {
			scheduler.listenLog(jobId, arg1, arg2);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#pause(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	@Override
	public boolean pause(JobId jobId) {
		try {
			return scheduler.pause(jobId);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#resume(org.objectweb.proactive.extra.scheduler.job.JobId)
	 */
	@Override
	public boolean resume(JobId jobId) {
		try {
			return scheduler.resume(jobId);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.userAPI.UserSchedulerInterface#submit(org.objectweb.proactive.extra.scheduler.job.Job)
	 */
	@Override
	public JobId submit(Job job) {
		try {
			return scheduler.submit(job);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface#kill()
	 */
	@Override
	public boolean kill() {
		try {
			return ((AdminSchedulerInterface) scheduler).kill();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface#pause()
	 */
	@Override
	public boolean pause() {
		try {
			return ((AdminSchedulerInterface) scheduler).pause();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface#pauseImmediate()
	 */
	@Override
	public boolean pauseImmediate() {
		try {
			return ((AdminSchedulerInterface) scheduler).pauseImmediate();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface#resume()
	 */
	@Override
	public boolean resume() {
		try {
			return ((AdminSchedulerInterface) scheduler).resume();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface#shutdown()
	 */
	@Override
	public boolean shutdown() {
		try {
			return ((AdminSchedulerInterface) scheduler).shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface#start()
	 */
	@Override
	public boolean start() {
		try {
			return ((AdminSchedulerInterface) scheduler).start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * @see org.objectweb.proactive.extra.scheduler.core.AdminSchedulerInterface#stop()
	 */
	@Override
	public boolean stop() {
		try {
			return ((AdminSchedulerInterface) scheduler).stop();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		return false;
	}

	// -------------------------------------------------------------------- //
	// ------------------------------ public ------------------------------ //
	// -------------------------------------------------------------------- //
	public int connectToScheduler(SelectSchedulerDialogResult dialogResult) {
		try {
			userName = dialogResult.getLogin();
			logAsAdmin = dialogResult.isLogAsAdmin();
			SchedulerAuthenticationInterface sai = SchedulerConnection.join(dialogResult.getUrl());
			if (logAsAdmin)
				scheduler = (AdminSchedulerInterface) sai.logAsAdmin(userName, dialogResult.getPassword());
			else
				scheduler = sai.logAsUser(userName, dialogResult.getPassword());
			return CONNECTED;
		} catch (SchedulerException e) {
			userName = null;
			logAsAdmin = false;
			return COULD_NOT_CONNECT_SCHEDULER;
		} catch (LoginException e) {
			return LOGIN_OR_PASSWORD_WRONG;
		}
	}

	public Boolean isItHisJob(String userName) {
		if (logAsAdmin)
			return true;
		if ((this.userName == null) || (userName == null))
			return false;
		return this.userName.equals(userName);
	}

	public boolean isAnAdmin() {
		return logAsAdmin;
	}

	// -------------------------------------------------------------------- //
	// ------------------------------ Static ------------------------------ //
	// -------------------------------------------------------------------- //
	public static SchedulerProxy getInstance() {
		if (instance == null)
			try {
				instance = (SchedulerProxy) ProActive.newActive(SchedulerProxy.class.getName(), null);
			} catch (ActiveObjectCreationException e) {
				e.printStackTrace();
			} catch (NodeException e) {
				e.printStackTrace();
			}
		return instance;
	}

	public static void clearInstance() {
		instance = null;
	}
}