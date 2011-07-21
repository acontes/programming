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

import org.objectweb.proactive.extra.multiactivenpb.kernel.FT;


public class EvolveThread extends FTBase {
	public int kt = 0;
	public int id;
	public boolean done = true;

	int lower_bound1, upper_bound1;
	double xtr[], xnt[];
	int ixnt, jxnt, kxnt;
	int ixtr, jxtr, kxtr;

	static final double ap = (-4.0 * alpha * pi * pi);

	public EvolveThread(FT ft, int low1, int high1) {
		Init(ft);
		lower_bound1 = low1;
		upper_bound1 = high1;
		/*setPriority(Thread.MAX_PRIORITY);
		setDaemon(true);
		master = ft;*/
	}

	void Init(FT ft) {
		// initialize shared data
		xtr = ft.xtr;
		xnt = ft.xnt;

		nx = ft.nx;
		ny = ft.ny;
		nz = ft.nz;

		ixtr = ft.isize3;
		jxtr = ft.jsize3;
		kxtr = ft.ksize3;
		ixnt = ft.isize4;
		jxnt = ft.jsize4;
		kxnt = ft.ksize4;
	}

	public void runOnce(int it) {
		kt = it;
		step();
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
				synchronized (master) {
					done = true;
					master.notify();
				}
			}
		}
	}*/

	public void step() {
		for (int i = lower_bound1; i <= upper_bound1; i++) {
			int ii = i - (i / (nx / 2)) * nx;
			int ii2 = ii * ii;
			for (int k = 0; k < nz; k++) {
				int kk = k - (k / (nz / 2)) * nz;
				int ik2 = ii2 + kk * kk;
				for (int j = 0; j < ny; j++) {
					int jj = j - (j / (ny / 2)) * ny;
					double lexp = Math.exp((ap * (jj * jj + ik2)) * (kt + 1));
					int xntidx = j * ixnt + k * jxnt + i * kxnt;
					int xtridx = j * ixtr + i * jxtr + k * kxtr;
					xnt[REAL + xntidx] = lexp * xtr[REAL + xtridx];
					xnt[IMAG + xntidx] = lexp * xtr[IMAG + xtridx];
				}
			}
		}
	}
}
