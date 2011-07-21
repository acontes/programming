/*
	NAS PARALLEL BENCHMARKS -- MULTI-ACTIVE OBJECT IMPLEMENTATION
	
	This benchmark is based on the original multithreaded NPB implementation.
	
!	Information on NPB 3.0, including the Technical Report NAS-02-008	  !
!    "Implementation of the NAS Parallel Benchmarks in Java",		  !
!    original specifications, source code, results and information	  !
!    on how to submit new results, is available at:			  !
!									  !
!	    http://www.nas.nasa.gov/Software/NPB/	
!

	Author: Zsolt Istvan (zsolt.istvan@gmx.net) 
 */
package org.objectweb.proactive.extra.multiactivenpb.ft;

import java.io.*;

import org.objectweb.proactive.extra.multiactivenpb.kernel.FT;

public class FFTThread extends FTBase {
	public int id;
	public boolean done = true;

	double x[];
	double exp1[];
	double exp2[];
	double exp3[];
	int n1, n2, n3;
	int lower_bound1, upper_bound1, lower_bound2, upper_bound2;
	int lb1, ub1, lb2, ub2;

	int state;
	int sign;
	double plane[];
	double scr[];

	public FFTThread(FT ft, int low1, int high1, int low2, int high2) {
		Init(ft);
		state = 1;
		lb1 = low1;
		ub1 = high1;
		lb2 = low2;
		ub2 = high2;
		plane = new double[2 * (maxdim + 1) * maxdim];
		scr = new double[2 * (maxdim + 1) * maxdim];
		/*setPriority(Thread.MAX_PRIORITY);
		setDaemon(true);
		master = ft;
		*/
	}

	void Init(FT ft) {
		// initialize shared data
		maxdim = ft.maxdim;
	}
	
	public void runOnce(){
		step();
		state++;
		if (state == 4) {
			state = 1;
		}
	}

	/*public void run() {
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
				step();
				state++;
				if (state == 4)
					state = 1;
				synchronized (master) {
					done = true;
					master.notify();
				}
			}
		}
	}*/

	public void setVariables(int sign1, boolean tr, double x1[],
			double exp11[], double exp21[], double exp31[]) {
		sign = sign1;
		x = x1;
		exp1 = exp11;
		exp2 = exp21;
		exp3 = exp31;
		n1 = exp1.length >> 1;
		n2 = exp2.length >> 1;
		n3 = exp3.length >> 1;

		if (tr) {
			lower_bound1 = lb2;
			upper_bound1 = ub2;
			lower_bound2 = lb1;
			upper_bound2 = ub1;
		} else {
			lower_bound1 = lb1;
			upper_bound1 = ub1;
			lower_bound2 = lb2;
			upper_bound2 = ub2;
		}
	}

	public void step() {
		int log = ilog2(n2);
		int isize3 = 2, jsize3 = isize3 * (n1 + 1), ksize3 = jsize3 * n2;
		int isize1 = 2;
		int jsize1 = 2 * (n2 + 1);

		switch (state) {
		case 1:
			for (int k = lower_bound1; k <= upper_bound1; k++)
				Swarztrauber(sign, log, n1, n2, x, k * ksize3, n1, exp2, scr);
			break;
		case 2:
			log = ilog2(n1);
			for (int k = lower_bound1; k <= upper_bound1; k++) {
				for (int j = 0; j < n2; j++) {
					for (int i = 0; i < n1; i++) {
						plane[REAL + j * isize1 + i * jsize1] = x[REAL + i
								* isize3 + j * jsize3 + k * ksize3];
						plane[IMAG + j * isize1 + i * jsize1] = x[IMAG + i
								* isize3 + j * jsize3 + k * ksize3];
					}
				}
				Swarztrauber(sign, log, n2, n1, plane, 0, n2, exp1, scr);
				for (int j = 0; j < n2; j++) {
					for (int i = 0; i < n1; i++) {
						x[REAL + i * isize3 + j * jsize3 + k * ksize3] = plane[REAL
								+ j * isize1 + i * jsize1];
						x[IMAG + i * isize3 + j * jsize3 + k * ksize3] = plane[IMAG
								+ j * isize1 + i * jsize1];
					}
				}
			}
			break;
		case 3:
			log = ilog2(n3);
			jsize1 = 2 * (n1 + 1);
			for (int k = lower_bound2; k <= upper_bound2; k++) {
				for (int i = 0; i < n3; i++) {
					for (int j = 0; j < n1; j++) {
						plane[REAL + j * isize1 + i * jsize1] = x[REAL + j
								* isize3 + k * jsize3 + i * ksize3];
						plane[IMAG + j * isize1 + i * jsize1] = x[IMAG + j
								* isize3 + k * jsize3 + i * ksize3];
					}
				}
				Swarztrauber(sign, log, n1, n3, plane, 0, n1, exp3, scr);
				for (int i = 0; i < n3; i++) {
					for (int j = 0; j < n1; j++) {
						x[REAL + j * isize3 + k * jsize3 + i * ksize3] = plane[REAL
								+ j * isize1 + i * jsize1];
						x[IMAG + j * isize3 + k * jsize3 + i * ksize3] = plane[IMAG
								+ j * isize1 + i * jsize1];
					}
				}
			}
			break;
		}
	}
}
