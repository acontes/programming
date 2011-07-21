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

package org.objectweb.proactive.extra.multiactivenpb.cg;

import org.objectweb.proactive.extra.multiactivenpb.kernel.*;

public class CGWorker extends CGBase {
	public boolean done = true;
	public int id;
	public int TaskOrder;

	int start1, end1;
	public double alpha, beta;
	
	private int state = 0;

	public CGWorker(CG cg, int st, int end) {
		Init(cg);
		start1 = st;
		end1 = end;
		done = true;
		setDaemon(true);
		setPriority(Thread.MAX_PRIORITY);
		master = cg;
	}

	void Init(CG cg) {
		// initialize shared data
		dmaster = cg.dmaster;
		rhomaster = cg.rhomaster;
		rnormmaster = cg.rnormmaster;
		colidx = cg.colidx;
		rowstr = cg.rowstr;
		a = cg.a;
		p = cg.p;
		q = cg.q;
		r = cg.r;
		x = cg.x;
		z = cg.z;
	}

/*	public void run() {
		int state = 0;
		for (;;) {
			synchronized (this) {
				while (done) {
					try {
						wait();
						synchronized (master) {
							master.notify();
							// alpha=master.alpha;
							// beta=master.beta;
						}
					} catch (InterruptedException ie) {
					}
				}
				switch (TaskOrder) {
				case 0:
					step0();
					break;
				case 1:
					step1();
					break;
				case 2:
					step2();
					break;
				case 3:
					step3();
					break;
				case 4:
					endWork();
					break;
				}
				synchronized (master) {
					done = true;
					master.notify();
				}
			}
		}
	}*/

	public void runOnce(int orderNum, double alpha, double beta) {
		
		this.alpha = alpha;
		this.beta = beta;
		
		switch (orderNum) {
		case 0:
			step0();
			break;
		case 1:
			step1();
			break;
		case 2:
			step2();
			break;
		case 3:
			step3();
			break;
		case 4:
			endWork();
			break;
		}
		
	}

	void step0() {
		for (int j = start1; j <= end1; j++) {
			double sum = 0.0;
			for (int k = rowstr[j]; k < rowstr[j + 1]; k++) {
				sum = sum + a[k] * p[colidx[k]];
			}
			q[j] = sum;
		}
		double sum = 0.0;
		for (int j = start1; j <= end1; j++)
			sum += p[j] * q[j];
		dmaster[id] = sum;
	}

	void step1() {
		for (int j = start1; j <= end1; j++) {
			z[j] = z[j] + alpha * p[j];
			r[j] = r[j] - alpha * q[j];
		}
		// ---------------------------------------------------------------------
		// rho = r.r
		// Now, obtain the norm of r: First, sum squares of r elements
		// locally...
		// ---------------------------------------------------------------------
		double rho = 0.0;
		for (int j = start1; j <= end1; j++)
			rho += r[j] * r[j];
		rhomaster[id] = rho;
	}

	void step2() {
		for (int j = start1; j <= end1; j++)
			p[j] = r[j] + beta * p[j];
	}

	void step3() {
		double rho = 0.0;
		for (int j = start1; j <= end1; j++) {
			q[j] = 0.0;
			z[j] = 0.0;
			r[j] = x[j];
			p[j] = x[j];
			rho += x[j] * x[j];
		}
		rhomaster[id] = rho;
	}

	void endWork() {
		// ---------------------------------------------------------------------
		// Compute residual norm explicitly: ||r|| = ||x - A.z||
		// First, form A.z
		// The partition submatrix-vector multiply
		// ---------------------------------------------------------------------
		for (int j = start1; j <= end1; j++) {
			double sum = 0.0;
			for (int k = rowstr[j]; k <= rowstr[j + 1] - 1; k++) {
				sum += a[k] * z[colidx[k]];
			}
			r[j] = sum;
		}
		// ---------------------------------------------------------------------
		// At this point, r contains A.z
		// ---------------------------------------------------------------------
		double sum = 0.0;
		for (int j = start1; j <= end1; j++)
			sum += (x[j] - r[j]) * (x[j] - r[j]);
		rnormmaster[id] = sum;
	}
}
