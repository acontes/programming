package org.objectweb.proactive.examples.scheduler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Random;

import org.objectweb.proactive.extra.scheduler.task.JavaTask;
import org.objectweb.proactive.extra.scheduler.task.TaskResult;


public class MonteCarlo extends JavaTask {

    /**  */
    private static final long serialVersionUID = 6803732909189957274L;
    private static final long DEFAULT_STEPS = 10;
    private static final long DEFAULT_ITERATIONS = 10000;
    private long iterations = DEFAULT_ITERATIONS;
    private long steps = DEFAULT_STEPS;
    private String file = null;

    @Override
    public void init(Map<String, Object> args) {
        if (args.containsKey("steps")) {
            try {
                steps = Long.parseLong(args.get("steps").toString());
            } catch (NumberFormatException e) { }
        }
        if (args.containsKey("iterations")) {
            try {
            	iterations = Long.parseLong(args.get("iterations").toString());
            } catch (NumberFormatException e) { }
        }
        if (args.containsKey("file")) {
            file = args.get("file").toString();
        }
    }

    public Object execute(TaskResult... results) {
        Random rand = new Random(System.currentTimeMillis());
        long n = iterations;
        long print = iterations / steps;
        int nbPrint = 0;
        double res = 0;
        while (n > 0) {
            if (print < 0) {
                System.out.println("Calcul intermediaire : Pi = " +
                    (((double) 4 * res) / (((++nbPrint) * iterations) / steps)));
                print = iterations / steps;
            }
            double x = rand.nextDouble();
            double y = rand.nextDouble();
            if (((x * x) + (y * y)) < 1) {
                res++;
            }
            print--;
            n--;
        }
        Double result = new Double((4 * res) / iterations);
        if (file != null) {
            FileOutputStream f = null;
            try {
                f = new FileOutputStream(file);
                PrintStream ps = new PrintStream(f);
                ps.println("Le resultat de Pi par Montecarlo est : " + result);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    f.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }
}
