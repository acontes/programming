package org.objectweb.proactive.extra.scheduler.gui.data;

public interface EventSchedulerListener {

	/**
	 * Invoked when the scheduler has just been started.
	 */
	public void startedEvent();

	/**
	 * Invoked when the scheduler has just been stopped.
	 */
	public void stoppedEvent();

	/**
	 * Invoked when the scheduler has just been paused.
	 * 
	 * @param event the scheduler informations about the status of every tasks.
	 *            use <code>SchedulerEvent.update(Vector<<Job>>)</code> to
	 *            update your job.
	 */
	public void pausedEvent();

	/**
	 * Invoked when the scheduler has received a paused immediate signal.
	 */
	public void freezeEvent();

	/**
	 * Invoked when the scheduler has just been resumed.
	 */
	public void resumedEvent();

	/**
	 * Invoked when the scheduler shutdown sequence is initialised.
	 */
	public void shuttingDownEvent();

	/**
	 * Invoked when the scheduler has just been shutdown.
	 * 
	 * @param job the new scheduled job.
	 */
	public void shutDownEvent();

	/**
	 * Invoked when the scheduler has just been killed. Scheduler is not
	 * reachable anymore.
	 */
	public void killedEvent();
}
