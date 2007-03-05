/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.examples.bnb.flowshop;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.branchnbound.user.BnBTask;


public class FlowShopTask extends BnBTask<FlowShopResult>
    implements Serializable {
    private static final long MAX_TIME_TO_SPLIT = 30000; // 30"
    private FlowShop fs;
    private int[] currentPerm;
    private int depth;
    private FlowShopResult fsr;
    private int[] lastPerm;
    private boolean com;
    private long lowerBound;
    private long upperBound;
    private boolean randomInit;

    public FlowShopTask() {
        // the empty no args constructor for ProActive
    }

    /**
     * Contruct a Task which search solution for all permutations to the
     * Flowshop problem. Use it to create the root Task.
     *
     * @param fs the description of the Flowshop problem
     * @param com for bench
     * @param randomInit for bench
     */
    public FlowShopTask(FlowShop fs, long lowerBound, long upperBound,
        boolean com, boolean randomInit) {
        this(fs, lowerBound, upperBound, null, null, 0, com, randomInit);
        currentPerm = new int[fs.jobs.length];
        for (int i = 0; i < currentPerm.length; i++) {
            currentPerm[i] = i;
        }
    }

    /**
     * @param fs
     * @param lowerBound
     * @param upperBound
     * @param currentPerm
     * @param lastPerm
     * @param depth
     * @param com
     * @param randomInit
     */
    public FlowShopTask(FlowShop fs, long lowerBound, long upperBound,
        int[] currentPerm, int[] lastPerm, int depth, boolean com,
        boolean randomInit) {
        this.fs = fs;
        this.fsr = new FlowShopResult();
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.currentPerm = (currentPerm == null) ? null
                                                 : (int[]) currentPerm.clone();
        this.lastPerm = (lastPerm == null) ? null : (int[]) lastPerm.clone();
        this.depth = depth;
        this.com = com;
        this.randomInit = randomInit;
    }

    public FlowShopResult explore(Object[] params) {
        int[] timeMachine = new int[fs.nbMachine];
        long time = System.currentTimeMillis();
        long nbPerm = 1;

        //        int[] cutbacks = new int[fs.jobs.length];
        int nbLoop = 0;
        int theLastJobFixed = currentPerm[depth - 1];

        //		Main.logger.info("depth  " +Permutation.string(currentPerm));
        //CHANGE HERE THE DEPTH OF SPLIT
        boolean mustSplit = ((depth < 2) && ((currentPerm.length - depth) > 2)); //Why not ?

        if (com) {
            this.bestCurrentSolution = fsr;
            r.setSolution(fsr);
            this.worker.setBestCurrentResult(r);
        } else {
            this.bestCurrentSolution = fsr;
        }
        if (!mustSplit) {
            int[][] tmpPerm = new int[currentPerm.length][];
            for (int i = 0; i < tmpPerm.length; i++) {
                tmpPerm[i] = new int[i];
            }
            while ((FlowShopTask.nextPerm(currentPerm)) != null) {
                nbLoop++;
                if ((lastPerm != null) &&
                        (currentPerm[depth - 1] != theLastJobFixed)) {
                    //					(Permutation.compareTo(currentPerm, lastPerm) >= 0)) {
                    //					Main.logger.info("depth " + depth + "  cmp  " + Permutation.string(currentPerm) + " >= " + Permutation.string(lastPerm));
                    break;
                }
                int currentMakespan;

                if (com) {
                    fsr.makespan = ((FlowShopResult) this.bestCurrentSolution).makespan;
                    fsr.permutation = ((FlowShopResult) this.bestCurrentSolution).permutation;
                }

                if ((currentMakespan = FlowShopTask.computeConditionalMakespan(
                                fs, currentPerm,
                                ((FlowShopResult) this.bestCurrentSolution).makespan,
                                timeMachine)) < 0) {
                    //bad branch
                    int n = currentPerm.length + currentMakespan;
                    FlowShopTask.jumpPerm(currentPerm, n, tmpPerm[n]);
                    //					cutbacks[-currentMakespan - 1]++;
                    if (nbLoop > 100000000) { // TODO verify
                        if (((System.currentTimeMillis() - time) > MAX_TIME_TO_SPLIT) &&
                                worker.isHungry().booleanValue()) { // avoid too tasks
                            mustSplit = true;
                            nbPerm++;
                            break;
                        } else {
                            nbLoop = 0;
                        }
                    }
                } else {
                    // better branch than previous best
                    if (com) {
                        fsr.makespan = currentMakespan;
                        System.arraycopy(currentPerm, 0, fsr.permutation, 0,
                            currentPerm.length);
                        this.worker.setBestCurrentResult(fsr);
                    } else {
                        ((FlowShopResult) this.bestCurrentSolution).makespan = currentMakespan;
                        System.arraycopy(currentPerm, 0,
                            ((FlowShopResult) this.bestCurrentSolution).permutation,
                            0, currentPerm.length);
                    }
                }

                nbPerm++;
            }
        }
        time = System.currentTimeMillis() - time;

        if (mustSplit) {
            this.worker.sendSubTasksToTheManager(((FlowShopTask) ProActive.getStubOnThis()).split());
        }

        Main.logger.info(" -- Explore " + nbPerm + " permutations in " + time +
            " ms\nBest makespan :" +
           this.bestCurrentSolution.makespan +
            " with this permutation " +
            Permutation.string(
               this.bestCurrentSolution.permutation));
        //            + ". We have cut " + Permutation.string(cutbacks));
        this.bestCurrentSolution.nbPermutationTested = nbPerm;
        this.bestCurrentSolution.time = time;
        //        ((FlowShopResult) this.bestKnownResult).makespanCut = cutbacks;
        return this.bestCurrentSolution;
    }

    public void initLowerBound() {
        if (lowerBound == -1) {
            lowerBound = FlowShop.computeLowerBound(this.fs);
            Main.logger.info("We compute a lower bound: " + lowerBound);
        }
    }

    public void initUpperBound() {
        int[] randomPerm = (int[]) currentPerm.clone();
        for (int i = depth + 1; i < randomPerm.length; i++) {
            int randomI = (int) (i +
                (Math.random() * (randomPerm.length - (i + 1))));
            int tmp = randomPerm[i];
            randomPerm[i] = randomPerm[randomI];
            randomPerm[randomI] = tmp;
        }
        Main.logger.info("initUpperBound => " +
            (randomInit
            ? ("random Perm : " + Permutation.string(randomPerm) +
            " her makespan " + FlowShop.computeMakespan(fs, randomPerm))
            : (" non random Perm " + Permutation.string(currentPerm) +
            FlowShop.computeMakespan(fs, currentPerm))));
        fsr.makespan = randomInit ? FlowShop.computeMakespan(fs, randomPerm)
                                  : FlowShop.computeMakespan(fs, currentPerm);
        fsr.permutation = randomInit ? randomPerm : (int[]) currentPerm.clone();
    }

    public ArrayList<FlowShopTask> split() {
        int nbTasks = fs.jobs.length - depth;

        ArrayList<FlowShopTask> tasks = new ArrayList<FlowShopTask>(nbTasks);

        int[] perm = (int[]) currentPerm.clone();
        int[] beginPerm = new int[perm.length];

        do {
            if ((lastPerm != null) &&
                    (Permutation.compareTo(perm, lastPerm) > 0)) {
                break;
            }
            System.arraycopy(perm, 0, beginPerm, 0, perm.length);

            Permutation.jumpPerm(perm, perm.length - (depth + 1));

            tasks.add(new FlowShopTask(fs, this.lowerBound, this.upperBound,
                    beginPerm, perm, depth + 1, com, randomInit));
        } while (Permutation.nextPerm(perm) != null);
        if (tasks.size() != 0) {
            ((FlowShopTask) tasks.get(tasks.size() - 1)).lastPerm = lastPerm;
        }
        Main.logger.info("We split in " + tasks.size() + " subtask at depth " +
            depth + " : " + Permutation.string(currentPerm) + ", " +
            Permutation.string(lastPerm));

        /*Iterator i = tasks.iterator();
           while (i.hasNext()) {
               Task t = (Task) i.next();
               Main.logger.info(t);
               Main.logger.info("");
           }*/
        return tasks;
    }

    public FlowShopResult gather(FlowShopResult[] values) {
        Result r = super.gather(results);
        long nbPerm = 0;
        long time = 0;

        //        int[] cuts = new int[currentPerm.length];
        for (int i = 0; i < values.length; i++) {
            FlowShopResult result = (values[i]);
            nbPerm += result.getNbPermutationTested();
            time += result.getTime();
            //                int[] t = result.getMakespanCut();
            //                for (int j = 0; j < t.length; j++) {
            //                    cuts[j] += t[j];
            //                }
        }
        long fact = fs.jobs.length;
        for (int i = fs.jobs.length - 1; i > 0; i--) {
            fact *= i;
        }
        double percent = (((double) nbPerm) / fact) * 100;
        Main.logger.info("We test " + nbPerm + " permutation on " + fact +
            " (" + percent + "%) in " + time + " ms.");
        //+ ". We have cut " + Permutation.string(cuts));
        return r;
    }

    private static int[] nextPerm(int[] perm) {
        int i = perm.length - 1;
        while ((i > 0) && (perm[i - 1] >= perm[i]))
            i--;
        if (i == 0) {
            // pas de permutation suivante
            return null;
        }
        int m = i - 1;
        int j = perm.length - 1;

        while (perm[m] >= perm[j])
            j--;
        int tmp = perm[m];
        perm[m] = perm[j];
        perm[j] = tmp;
        int k = m + 1;
        int lamb = perm.length - 1;
        while (k < lamb) {
            tmp = perm[k];
            perm[k] = perm[lamb];
            perm[lamb] = tmp;
            k++;
            lamb--;
        }
        return perm;
    }

    private static int computeConditionalMakespan(FlowShop fs,
        int[] permutation, long bound, int[] timeMachine) {
        //contains cumulated time by machine
        for (int i = 0; i < timeMachine.length; i++) {
            timeMachine[i] = 0;
        }
        int nbJob = permutation.length;
        long cumulateTimeOnLastMachine = fs.cumulateTimeOnLastMachine;

        for (int i = 0; i < nbJob; i++) {
            int[] currentJob = fs.jobs[permutation[i]];
            timeMachine[0] += currentJob[0];
            for (int j = 1; j < timeMachine.length; j++) {
                if (timeMachine[j] > timeMachine[j - 1]) {
                    timeMachine[j] = timeMachine[j] + currentJob[j];
                } else {
                    // the machine j is later than machine j-1 
                    timeMachine[j] = timeMachine[j - 1] + currentJob[j];
                }
            }
            cumulateTimeOnLastMachine -= currentJob[timeMachine.length - 1];
            if ((timeMachine[timeMachine.length - 1] +
                    cumulateTimeOnLastMachine) >= bound) {
                return -(i + 1);
            }
        }

        return timeMachine[timeMachine.length - 1];
    }

    private static int[] jumpPerm(int[] perm, int n, int[] tmp) {
        System.arraycopy(perm, perm.length - n, tmp, 0, n);

        /*for (int i = perm.length - n, j = 0; i < perm.length; i++, j++) {
                tmp[j] = perm[i];
        }*/
        Arrays.sort(tmp); //necessary when we jump an uncomplete branch
        for (int i = 0, srcI = perm.length - 1; i < n; i++, srcI--) {
            perm[srcI] = tmp[i];
        }
        return perm;
    }
}
