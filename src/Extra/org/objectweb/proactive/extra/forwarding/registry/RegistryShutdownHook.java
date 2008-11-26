package org.objectweb.proactive.extra.forwarding.registry;

/**
 * 
 * The StopRegistryRunnable is dedicated to be launched using a shutdown hook. It closes properly the {@link ForwardingRegistry} set as its attribute 
 * @author A.Fawaz, J.Martin
 *
 */
public class RegistryShutdownHook implements Runnable {

    ForwardingRegistry registry;

    /**
     * @param registry The registry to be stopped harshly
     */
    public RegistryShutdownHook(ForwardingRegistry registry) {
        this.registry = registry;
    }

    /**
     * stops {@link #registry} harshly.
     */
    public void run() {
        registry.stop(false);
    }

}
