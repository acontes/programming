package org.objectweb.proactive.core.util.log.remote;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.spi.LoggingEvent;


/**
 * The default logging event sender service provider
 * 
 * This provider use a simple but flexible flushing policy. Logging events are
 * send to the collector as soon as one of this condition is met:
 * <ul>
 * <li>the number of buffered message reach a threshold</li>
 * <li>logging events have not been flushed since a given period (in
 * milliseconds)</li>
 * </ul>
 * 
 * The threshold and the period can be configured by setting the following java
 * properties:
 * <ul>
 * <li>org.objectweb.proactive.core.util.log.remote.LoggingEventSenderProvider.
 * threshold</li>
 * <li>org.objectweb.proactive.core.util.log.remote.LoggingEventSenderProvider.
 * period</li>
 * </ul>
 * 
 */
public class LoggingEventSenderProvider extends LoggingEventSenderSPI {
    final static int DEFAULT_PERIOD = 10000; // ms
    final static int DEFAULT_THRESHOLD = 5;

    /**
     * A logging event cannot be buffered more than this amount of time
     * (milliseconds) on this runtime
     */
    final int period;
    final String periodProperty = "org.objectweb.proactive.core.util.log.remote.LoggingEventSenderProvider.period";

    /**
     * Send the events to the collector as soon as THRESHOLD logging events are
     * available
     */
    final int threshold;
    final String thresholdProperty = "org.objectweb.proactive.core.util.log.remote.LoggingEventSenderProvider.threshold";

    /** Logging Events to be send to the collector */
    final private ConcurrentLinkedQueue<LoggingEvent> buffer;

    /** True if the thread should terminate */
    final private AtomicBoolean terminate;

    /**
     * A mutex to ensure correct synchronization between the main thread and the
     * shutdown hook
     */
    final private Object gatherAndSendMutex = new Object();

    public LoggingEventSenderProvider() {
        this.buffer = new ConcurrentLinkedQueue<LoggingEvent>();
        this.terminate = new AtomicBoolean(false);

        String prop;
        int value;

        value = DEFAULT_PERIOD;
        prop = System.getProperty(periodProperty);
        if (prop != null) {
            try {
                int i = Integer.parseInt(prop);
                if (i > 0) {
                    value = i;
                } else {
                    throw new NumberFormatException("Must be positive");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid parameter for property " + periodProperty +
                    ". Must be a positive integer");
            }
        }
        this.period = value;

        value = DEFAULT_THRESHOLD;
        prop = System.getProperty(thresholdProperty);
        if (prop != null) {
            try {
                int i = Integer.parseInt(prop);
                if (i > 0) {
                    value = i;
                } else {
                    throw new NumberFormatException("Must be positive");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid parameter for property " + thresholdProperty +
                    ". Must be a positive integer");
            }
        }
        this.threshold = value;
    }

    @Override
    public void run() {
        do {
            gatherAndSend();

            synchronized (this) {
                try {
                    this.wait(this.period);
                } catch (InterruptedException e) {
                    // Miam miam miam
                }
            }

        } while (!this.terminate.get());
    }

    private void gatherAndSend() {
        synchronized (this.gatherAndSendMutex) {
            ProActiveLogCollector collector = getCollector();
            if (collector != null) {
                /* Since there is at most one reader at the same time
                 * We know that at least buffer.size() element are available.
                 * 
                 * This iteration send _exactly_ buffer.size() event to the collector.
                 * If any event is added concurrently, then they will be send by the
                 * next iteration
                 */
                int size = buffer.size();
                ArrayList<LoggingEvent> events = new ArrayList<LoggingEvent>(size);
                for (int i = 0; i < size; i++) {
                    events.add(buffer.poll());
                }

                if (events.size() != 0) {
                    collector.sendEvent(events);
                }
            }
        }
    }

    @Override
    public void append(LoggingEvent event) {
        buffer.add(event);

        // Wake up the flusher thread if the THRESHOLD is reached
        if (buffer.size() >= this.threshold) {
            synchronized (this) {
                this.notifyAll();
            }
        }
    }

    @Override
    public void terminate() {
        this.terminate.set(true);
        this.gatherAndSend();
    }
}
