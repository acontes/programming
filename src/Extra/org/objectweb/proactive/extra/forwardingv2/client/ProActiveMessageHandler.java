package org.objectweb.proactive.extra.forwardingv2.client;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extra.forwardingv2.protocol.DataRequestMessage;


/**
 * Execute the request received and send the response.
 *
 * @since ProActive 4.1.0
 */
public class ProActiveMessageHandler implements MessageHandler {
    public static final Logger logger = ProActiveLogger.getLogger(Loggers.FORWARDING);
    /**
     * ThreadPool options:
     * - CORE_POOL_SIZE: Initial size of the thread pool
     * - MAX_POOL_SIZE: number of request served at the same time
     * - KEEP_ALIVE_TIME: Time in seconds to keep the threads before decreasing the core pool size.
     */
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAX_POOL_SIZE = 4;
    private static final long KEEP_ALIVE_TIME = 10;

    final private ThreadPoolExecutor tpe;

    private AgentV2 agent;

    public ProActiveMessageHandler(AgentV2 agent) {
        this.agent = agent;
        tpe = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    }

    public void setAgent(AgentV2 agent) {
        this.agent = agent;
    }

    public void pushMessage(DataRequestMessage message) {
        ProActiveMessageProcessor pmp = new ProActiveMessageProcessor(message, agent);
        tpe.submit(pmp);
    }

}
