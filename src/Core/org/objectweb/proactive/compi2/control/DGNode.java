package org.objectweb.proactive.compi2.control;

public interface DGNode {

	void register(int rank);
	boolean blockUntilReady();
	void wakeUpThread();
	int getRank();

}
