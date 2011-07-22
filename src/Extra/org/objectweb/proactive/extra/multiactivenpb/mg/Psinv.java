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
package org.objectweb.proactive.extra.multiactivenpb.mg;

import org.objectweb.proactive.extra.multiactivenpb.kernel.*;

public class Psinv extends MGBase {
	public int id;
	public boolean done = true;

	public int n1, n2, n3;
	public int roff, uoff;

	int start, end, work;
	int state = 0;
	double r1[], r2[];

	public Psinv(MG mg) {
		Init(mg);
		r1 = new double[nm + 1];
		r2 = new double[nm + 1];
		setPriority(Thread.MAX_PRIORITY);
		setDaemon(true);
		master = mg;
	}

	void Init(MG mg) {
		// initialize shared data
		num_threads = mg.num_threads;
		r = mg.r;
		u = mg.u;
		c = mg.c;
		nm = mg.nm;
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
				GetWork();
				step();
				synchronized (master) {
					done = true;
					master.notify();
				}
			}
		}
	}*/
	
	public void runOnce() {
		GetWork();
		step();
	}

	public void step() {
		int i3, i2, i1;
		if (work == 0)
			return;
		for (i3 = start; i3 <= end; i3++) {
			for (i2 = 1; i2 < n2 - 1; i2++) {
				for (i1 = 0; i1 < n1; i1++) {
					r1[i1] = r[roff + i1 + n1 * (i2 - 1 + n2 * i3)]
							+ r[roff + i1 + n1 * (i2 + 1 + n2 * i3)]
							+ r[roff + i1 + n1 * (i2 + n2 * (i3 - 1))]
							+ r[roff + i1 + n1 * (i2 + n2 * (i3 + 1))];
					r2[i1] = r[roff + i1 + n1 * (i2 - 1 + n2 * (i3 - 1))]
							+ r[roff + i1 + n1 * (i2 + 1 + n2 * (i3 - 1))]
							+ r[roff + i1 + n1 * (i2 - 1 + n2 * (i3 + 1))]
							+ r[roff + i1 + n1 * (i2 + 1 + n2 * (i3 + 1))];
				}
				for (i1 = 1; i1 < n1 - 1; i1++) {
					u[uoff + i1 + n1 * (i2 + n2 * i3)] += c[0]
							* r[roff + i1 + n1 * (i2 + n2 * i3)]
							+ c[1]
							* (r[roff + i1 - 1 + n1 * (i2 + n2 * i3)]
									+ r[roff + i1 + 1 + n1 * (i2 + n2 * i3)] + r1[i1])
							+ c[2] * (r2[i1] + r1[i1 - 1] + r1[i1 + 1]);
					// c---------------------------------------------------------------------
					// c Assume c(3) = 0 (Enable line below if c(3) not= 0)
					// c---------------------------------------------------------------------
					// c > + c(3) * ( r2(i1-1) + r2(i1+1) )
					// c---------------------------------------------------------------------
				}
			}
		}

		// c---------------------------------------------------------------------
		// c exchange boundary points
		// c---------------------------------------------------------------------
		// System.out.println(id+" "+start+" "+end);
		for (i3 = start; i3 <= end; i3++)
			for (i2 = 1; i2 < n2 - 1; i2++) {
				u[uoff + n1 * (i2 + n2 * i3)] = u[uoff + n1 - 2 + n1
						* (i2 + n2 * i3)];
				u[uoff + n1 - 1 + n1 * (i2 + n2 * i3)] = u[uoff + 1 + n1
						* (i2 + n2 * i3)];
			}

		for (i3 = start; i3 <= end; i3++)
			for (i1 = 0; i1 < n1; i1++) {
				u[uoff + i1 + n1 * n2 * i3] = u[uoff + i1 + n1
						* (n2 - 2 + n2 * i3)];
				u[uoff + i1 + n1 * (n2 - 1 + n2 * i3)] = u[uoff + i1 + n1
						* (1 + n2 * i3)];
			}
	}

	public void GetWork() {
		int workpt = (wend - wstart) / num_threads;
		int remainder = wend - wstart - workpt * num_threads;
		if (workpt == 0) {
			if (id <= wend - wstart) {
				work = 1;
				start = end = wstart + id;
			} else {
				work = 0;
			}
		} else {
			if (id < remainder) {
				workpt++;
				start = wstart + workpt * id;
				end = start + workpt - 1;
				work = workpt;
			} else {
				start = wstart + remainder + workpt * id;
				end = start + workpt - 1;
				work = workpt;
			}
		}
	}
}
