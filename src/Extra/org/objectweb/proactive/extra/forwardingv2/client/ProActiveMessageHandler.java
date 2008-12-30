package org.objectweb.proactive.extra.forwardingv2.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.message.DataRequestMessage;


/**
 * Execute the request received and send the response.
 *
 * @since ProActive 4.1.0
 */
public class ProActiveMessageHandler implements MessageHandler {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING_CLIENT);
    /**
     * ThreadPool options:
     * - CORE_POOL_SIZE: Initial size of the thread pool
     * - MAX_POOL_SIZE: number of request served at the same time
     * - KEEP_ALIVE_TIME: Time in seconds to keep the threads before decreasing the core pool size.
     */
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;
    private static final long KEEP_ALIVE_TIME = 10;

    final private ExecutorService tpe;

    private AgentV2Internal agent;

    public ProActiveMessageHandler(AgentV2Internal agent) {
        this.agent = agent;
        tpe = Executors.newFixedThreadPool(10);
    }

    public void pushMessage(DataRequestMessage message) {
        if (logger.isTraceEnabled()) {
            logger.trace("pushing message " + message + " into the executor queue");
        }
        ProActiveMessageProcessor pmp = new ProActiveMessageProcessor(message, agent);
        tpe.submit(pmp);
    }

}
