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
package org.objectweb.proactive.extra.multiactivenpb.kernel;

import java.util.LinkedList;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.annotation.multiactivity.Compatible;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineRules;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.extra.multiactivenpb.is.*;
import org.objectweb.proactive.multiactivity.MultiActiveService;

@DefineGroups({ 
	@Group(name = "runtime", selfCompatible = false),
	@Group(name = "rank", selfCompatible = true)
})
@DefineRules({ 
	@Compatible(value = { "runtime", "rank" })
})

public class IS extends ISBase implements RunActive {
	
	static IS activeInstance;
	
	public int bid = -1;
	public BMResults results;
	public boolean serial = false;

	Random rng;
	protected static final double amult = 1220703125.0;

	public IS() {
		// for PA
	}
	
	public IS(char clss, int np, boolean ser) {
		super(clss, np, ser);
		serial = ser;
		rng = new Random();
	}

	public static void main(String argv[]) {
		IS is = null;

		BMArgs.ParseCmdLineArgs(argv, BMName);
		char CLSS = BMArgs.CLASS;
		int np = BMArgs.num_threads;
		boolean serial = BMArgs.serial;

		try {
			is = new IS(CLSS, np, serial);
			
			activeInstance = PAActiveObject.turnActive(is);
		} catch (OutOfMemoryError e) {
			BMArgs.outOfMemoryMessage();
			System.exit(0);
		} catch (ActiveObjectCreationException e) {
			// TOD Auto-generated catch block
			e.printStackTrace();
		} catch (NodeException e) {
			// TOD Auto-generated catch block
			e.printStackTrace();
		}
		is.runBenchMark();
		
		System.exit(0);
	}

	public void run() {
		runBenchMark();
	}

	@MemberOf("runtime")
	public boolean runBenchMark() {
		BMArgs.Banner(BMName, CLASS, serial, num_threads);

		System.out.println(" Size:  " + TOTAL_KEYS + " Iterations:   "
				+ MAX_ITERATIONS);

		// Initialize timer
		timer = new Timer();
		timer.resetTimer(0);

		// Generate random number sequence and subsequent keys on all procs
		initKeys(amult); // Random number gen seed
							// Random number gen mult

		/*
		 * Do one interation for free (i.e., untimed) to guarantee
		 * initialization of all data and code pages and respective tables
		 */
		if (serial) {
			rank(1);
		} else {
			setupThreads(this);
			RankThread.iteration = 1;
			doSort();
			for (int i = 0; i < MAX_KEY; i++) {
				master_hist[i] = 0;
			}

			doSort();
			partial_verify(1);
		}
		/* Start verification counter */
		passed_verification = 0;
		if (CLASS != 'S')
			System.out.println("\n     iteration#");

		timer.start(0);
		/* This is the main iteration */
		for (int it = 1; it <= MAX_ITERATIONS; it++) {
			if (CLASS != 'S')
				System.out.println("	  " + it);
			if (serial) {
				rank(it);
			} else {
				RankThread.iteration = it;
				doSort();
				for (int i = 0; i < MAX_KEY; i++) {
					master_hist[i] = 0;
				}
				doSort();
				partial_verify(it);
			}
		}
		timer.stop(0);

		/*
		 * This tests that keys are in sequence: sorting of last ranked key seq
		 * occurs here, but is an untimed operation
		 */
		full_verify();
		int verified = 0;
		if (passed_verification == 5 * MAX_ITERATIONS + 1)
			verified = 1;

		BMResults.printVerificationStatus(CLASS, verified, BMName);
		double tm = timer.readTimer(0);
		BMResults res = new BMResults(BMName, CLASS, TOTAL_KEYS, 0, 0,
				MAX_ITERATIONS, tm, getMOPS(tm, MAX_ITERATIONS, TOTAL_KEYS),
				"keys ranked", verified, serial, num_threads, bid);
		res.print();
		
		return true;
	}

	public double getMOPS(double total_time, int niter, int num_keys) {
		double mops = 0.0;
		if (total_time > 0) {
			mops = (double) niter + num_keys;
			mops *= niter / (total_time * 1000000.0);
		}
		return mops;
	}

	void rank(int iteration) {
		key_array[iteration] = iteration;
		key_array[iteration + MAX_ITERATIONS] = MAX_KEY - iteration;

		for (int i = 0; i < TEST_ARRAY_SIZE; i++) {
			partial_verify_vals[i] = key_array[test_index_array[i]];
		}

		/* Clear the work array */
		for (int i = 0; i < MAX_KEY; i++)
			master_hist[i] = 0;

		/*
		 * In this section, the keys themselves are used as their own indexes to
		 * determine how many of each there are: their individual population
		 */

		for (int i = 0; i < NUM_KEYS; i++)
			master_hist[key_array[i]]++;
		/* Now they have individual key */
		/* population */

		/* Density to Distribution conversion */
		for (int i = 0; i < MAX_KEY - 1; i++) {
			master_hist[i + 1] += master_hist[i];
		}
		partial_verify(iteration);
	}

	public void partial_verify(int iteration) {
		for (int i = 0; i < TEST_ARRAY_SIZE; i++) {
			int k = partial_verify_vals[i]; /* test vals were put here */
			int offset = iteration;
			if (0 <= k && k <= NUM_KEYS - 1) {
				switch (CLASS) {
				case 'S':
					if (i <= 2)
						offset = iteration;
					else
						offset = -iteration;
					break;
				case 'W':
					if (i < 2)
						offset = iteration - 2;
					else
						offset = -iteration;
					break;
				case 'A':
					if (i <= 2)
						offset = iteration - 1;
					else
						offset = -iteration + 1;
					break;
				case 'B':
					if (i == 1 || i == 2 || i == 4)
						offset = iteration;
					else
						offset = -iteration;
					break;
				case 'C':
					if (i <= 2)
						offset = iteration;
					else
						offset = -iteration;
					break;
				}
				if (master_hist[k - 1] != test_rank_array[i] + offset) {
					System.out.println("Failed partial verification: "
							+ "iteration" + iteration + ", test key " + i);
				} else
					passed_verification++;
			}
		}
	}

	int full_verify() {
		/* To save copy and memory sorting can be done directly: */
		int key = 0, idx = 0;
		for (int i = 0; i < NUM_KEYS; i++) {
			while (idx == master_hist[key]) {
				key++;
				if (key >= MAX_KEY || idx >= NUM_KEYS)
					break;
			}
			key_array[idx] = key;
			idx++;
		}

		// Confirm keys correctly sorted: count incorrectly sorted keys, if any
		int count = 0;
		for (int i = 1; i < NUM_KEYS; i++)
			if (key_array[i - 1] > key_array[i])
				count++;

		if (count != 0) {
			System.out.println("Full_verify: number of keys out of sort: "
					+ count);
		} else
			passed_verification++;
		return passed_verification;
	}

	void initKeys(double a) {
		double x;
		int k = MAX_KEY / 4;
		for (int i = 0; i < NUM_KEYS; i++) {
			x = rng.randlc(a);
			x += rng.randlc(a);
			x += rng.randlc(a);
			x += rng.randlc(a);
			key_array[i] = (int) (x * k);
		}
	}

	/*synchronized*/ void doSort() {
		/*int m;
		for (m = 0; m < num_threads; m++)
			synchronized (rankthreads[m]) {
				rankthreads[m].done = false;
				rankthreads[m].notify();
			}
		for (m = 0; m < num_threads; m++)
			while (!rankthreads[m].done) {
				try {
					wait();
				} catch (InterruptedException e) {
				}
				notifyAll();
			}*/
		
		LinkedList<IntWrapper> result = new LinkedList<IntWrapper>();
		for (int m = 0; m < num_threads; m++) {
			result.add(activeInstance.doSort(m));
		}
		
		for (int m = 0; m < num_threads; m++) {
			if (result.get(m).equals(new IntWrapper(99))) {}
		}
	}
	
	@MemberOf("rank")
	public IntWrapper doSort(int m) {
		rankthreads[m].runOnce();
		return new IntWrapper(0);
	}

	public double getTime() {
		return timer.readTimer(0);
	}

	public void finalize() throws Throwable {
		System.out.println("IS: is about to be garbage collected");
		super.finalize();
	}

	@Override
	public void runActivity(Body body) {
		new MultiActiveService(body).multiActiveServing(num_threads+1, true, false);
	}
}
