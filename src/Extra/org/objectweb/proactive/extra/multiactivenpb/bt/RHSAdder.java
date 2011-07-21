/*
	NAS PARALLEL BENCHMARKS -- MULTI-ACTIVE OBJECT IMPLEMENTATION
	
	This benchmark is based on the original multi-threaded NPB implementation.
	
!	Information on NPB 3.0, including the Technical Report NAS-02-008	  !
!    "Implementation of the NAS Parallel Benchmarks in Java",		  !
!    original specifications, source code, results and information	  !
!    on how to submit new results, is available at:			  !
!									  !
!	    http://www.nas.nasa.gov/Software/NPB/	
!

	Author: Zsolt Istvan (zsolt.istvan@gmx.net) 
 */
package org.objectweb.proactive.extra.multiactivenpb.bt;

import org.objectweb.proactive.extra.multiactivenpb.kernel.*;

public class RHSAdder extends BTBase {
	public int id;
	public boolean done = true;

	// private data
	int lower_bound, upper_bound;

	public RHSAdder(BT bt, int low, int high) {
		Init(bt);
		lower_bound = low;
		upper_bound = high;
		/*setPriority(Thread.MAX_PRIORITY);
		setDaemon(true);
		master = bt;*/
	}

	void Init(BT bt) {
		// initialize shared data
		IMAX = bt.IMAX;
		JMAX = bt.JMAX;
		KMAX = bt.KMAX;
		problem_size = bt.problem_size;
		grid_points = bt.grid_points;
		niter_default = bt.niter_default;
		dt_default = bt.dt_default;

		u = bt.u;
		rhs = bt.rhs;
		forcing = bt.forcing;
		cv = bt.cv;
		q = bt.q;
		cuf = bt.cuf;
		isize2 = bt.isize2;
		jsize2 = bt.jsize2;
		ksize2 = bt.ksize2;

		us = bt.us;
		vs = bt.vs;
		ws = bt.ws;
		qs = bt.qs;
		rho_i = bt.rho_i;
		square = bt.square;
		jsize1 = bt.jsize1;
		ksize1 = bt.ksize1;

		ue = bt.ue;
		buf = bt.buf;
		jsize3 = bt.jsize3;
	}

/*	public void run() {

		for (;;) {
			synchronized (this) {
				while (done == true) {
					try {
						wait();
						synchronized (master) {
							master.notify();
						}
					} catch (InterruptedException ie) {
					}
				}

				
				synchronized (master) {
					done = true;
					master.notify();
				}
			}
		}
	}*/
	
	public void runOnce(){
		step();
	}
	
	private void step(){
		int i, j, k, m;
		for (k = lower_bound; k <= upper_bound; k++) {
			for (j = 1; j <= grid_points[1] - 2; j++) {
				for (i = 1; i <= grid_points[0] - 2; i++) {
					for (m = 0; m <= 4; m++) {
						u[m + i * isize2 + j * jsize2 + k * ksize2] += rhs[m
								+ i * isize2 + j * jsize2 + k * ksize2];
					}
				}
			}
		}
	}
}
