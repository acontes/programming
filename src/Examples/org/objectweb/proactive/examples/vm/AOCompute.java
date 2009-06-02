package org.objectweb.proactive.examples.vm;

import java.io.Serializable;


/*
 * Just a class designed for tests purposes
 */
public class AOCompute implements Serializable {

    private AOCompute remote;
    private AORandom rand;

    // empty constructor is required by Proactive
    public AOCompute() {
    }

    public void setRemote(AOCompute remote) {
        this.remote = remote;
    }

    public void setRandom(AORandom rand) {
        this.rand = rand;
    }

    public synchronized int compute(int x, int cpt) {
        System.out.println("x = " + x);
        if (cpt < 4) {
            int a = rand.getRandom();
            int b = rand.getRandom();
            return remote.compute(x + a + b, ++cpt);
        } else {
            return x;
        }
    }

}