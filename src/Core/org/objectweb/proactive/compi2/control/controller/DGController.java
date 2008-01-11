package org.objectweb.proactive.compi2.control.controller;

import org.objectweb.proactive.compi2.MPIResult;

public interface DGController {
	public void addDGController (int JobID, DGController dgController);
	public boolean PAMPIHandShake ();
	public MPIResult startMPI();
}
