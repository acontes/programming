/**
 *
 */
package org.objectweb.proactive.examples.scheduler;

import java.util.ArrayList;

import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


/**
 * @author jlscheef
 *
 */
public class Worker implements java.io.Serializable {
    private static final long serialVersionUID = 6479587603886940747L;

    // primeNumbers already known by the worker
    private ArrayList<Integer> primeNumbers = new ArrayList<Integer>();

    /** ProActive empty constructor */
    public Worker() {
    }

    public BooleanWrapper isPrime(int num) {
        for (Integer n : primeNumbers) {
            if ((num % n) == 0) {
                return new BooleanWrapper(false);
            }
        }
        return new BooleanWrapper(true);
    }

    public void addPrimeNumber(int num) {
        primeNumbers.add(num);
    }
}
