package org.objectweb.proactive.extensions.scheduler.task;

import java.io.Serializable;


/**
 * Class representing a forked environment of a JVM created specifically for an execution of a Java Task.
 * A Java Task can be executed in the current JVM - then all Java Tasks are dependent on the same JVM (provider) and JVM options (like memory),
 * or can be executed in a dedicated JVM with additional options specified like javaHome, java Options, ... 
 * 
 * @author ProActive team
 *
 */
public class ForkEnvironment implements Serializable {

    /** Path to directory with Java installed, to this path '/bin/java' will be added. 
     * If the path is null only 'java' command will be called
     */
    private String javaHome = null;

    /** options passed to Java (not an application) (example: memory settings or properties) */
    private String javaOptions = null;

    /**
     * @return the javaHome
     */
    public String getJavaHome() {
        return javaHome;
    }

    /**
     * @param javaHome the javaHome to set
     */
    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    /**
     * @return the javaOptions
     */
    public String getJavaOptions() {
        return javaOptions;
    }

    /**
     * @param javaOptions the javaOptions to set
     */
    public void setJavaOptions(String javaOptions) {
        this.javaOptions = javaOptions;
    }
}
