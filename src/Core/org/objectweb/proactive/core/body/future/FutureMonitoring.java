/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
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
package org.objectweb.proactive.core.body.future;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.internalmsg.Heartbeat;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.runtime.LocalNode;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;


public class FutureMonitoring implements Runnable {

    /** Ping one body every 21s */
    private static int TTM = 21000;

    /**
     * For each node, the list of futures to monitor. We ping a single body in
     * each node for all of the futures from the node. This implies that the
     * outcome of the ping for a single body will be replicated on the other
     * bodies. We ping the updater, so we should detect a broken automatic
     * continuations chain.
     */
    private static final ConcurrentHashMap<String, ConcurrentLinkedQueue<FutureProxy>> futuresToMonitor =
        new ConcurrentHashMap<String, ConcurrentLinkedQueue<FutureProxy>>();

    static {

        /* Dynamically configurable to make shorter tests */
        String ttm = ProActiveConfiguration.getInstance()
                                           .getProperty("proactive.futuremonitoring.ttm");
        if (ttm != null) {
            TTM = Integer.parseInt(ttm);
        }
        Thread t = new Thread(new FutureMonitoring(), "Monitoring the Futures");
        t.setDaemon(true);
        t.start();
    }

    /** To avoid copy-pasting */
    private static void monitoringDelay() {
        try {
            Thread.sleep(TTM);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove available futures, and nodes with no more futures to monitor
     * @param url of the node
     */
    private static void cleanUpFutures(String url) {
        ConcurrentLinkedQueue<FutureProxy> futures = futuresToMonitor.get(url);
        for (FutureProxy fp : futures) {
            if (fp.isAvailable()) {
                futures.remove(fp);
            }
        }
        synchronized (futuresToMonitor) {

            /*
             * Manual synchronization here of the ConcurrentXXX to avoid a race
             * with an insertion between the test and the removal
             */
            if (futures.isEmpty()) {
                futuresToMonitor.remove(url);
            }
        }
    }

    /**
     * The FT Message used to test the communication
     */
    private static Heartbeat HEARTBEAT_MSG = new Heartbeat();

    /**
     * @return true iff a body has been pinged
     */
    private static boolean pingBody(String url) {
        boolean pinged = false;
        FutureMonitoringPingFailureException bodyException = null;
        for (FutureProxy fp : futuresToMonitor.get(url)) {
            if (!pinged) {

                /* Not yet pinged somebody */
                UniversalBody body = null;

                synchronized (fp) {
                    if (fp.isAwaited()) {
                        body = fp.getUpdater();
                    }
                }
                if (body != null) {
                    /* OK, Found somebody to ping */
                    pinged = true;
                    try {
                        body.receiveFTMessage(HEARTBEAT_MSG);

                        /* Successful ping, nothing more to do */
                        return true;
                    } catch (Exception e) {
                        /* Ping failure, update all awaited futures on this node with the exception */
                        bodyException = new FutureMonitoringPingFailureException(e);
                    }
                }
            }

            if (bodyException != null) {
                synchronized (fp) {
                    if (fp.isAwaited()) {
                        fp.receiveReply(new FutureResult(null, bodyException,
                                null));
                    }
                }
            }
        }
        return pinged;
    }

    /* Arrange to ping a single body every TTM */
    public void run() {
        for (;;) {
            boolean pingedOneBody = false;
            for (String url : futuresToMonitor.keySet()) {
                boolean pingedThisBody = pingBody(url);
                cleanUpFutures(url);
                if (pingedThisBody) {
                    pingedOneBody = true;
                    monitoringDelay();
                }
            }
            if (!pingedOneBody) {
                monitoringDelay();
            }
        }
    }

    public static void monitorFutureProxy(FutureProxy fp) {
        if (isFTEnabled()) {
            return;
        }
        UniversalBody body = fp.getUpdater();
        if (body == null) {
            new Exception("Cannot monitor this future, unknown updater body").printStackTrace();
            return;
        }
        String url = body.getNodeURL();
        synchronized (futuresToMonitor) {
            /*
             * Avoid a race with the suppression in the ConcurrentHashMap when the
             * ConcurrentLinkedQueue is empty.
             */
            ConcurrentLinkedQueue<FutureProxy> futures = futuresToMonitor.get(url);
            if (futures == null) {
                futures = new ConcurrentLinkedQueue<FutureProxy>();
                futuresToMonitor.put(url, futures);
            }
            if (fp.isAwaited() && !futures.contains(fp)) {
                futures.add(fp);
            }
        }
    }

    /**
     * Add a future to the list of monitored future. This is automatically done
     * when waiting a future. If the active object serving the method for this
     * future cannot be pinged, the future is updated with a RuntimeException.
     * @param future the future object to monitor
     */
    @PublicAPI
    public static void monitorFuture(Object future) {
        if (!MOP.isReifiedObject(future)) {
            throw new IllegalArgumentException(
                "Parameter is not a future object (actual type is " +
                future.getClass().getName() + ")");
        }
        FutureProxy fp = (FutureProxy) ((StubObject) future).getProxy();
        monitorFutureProxy(fp);
    }

    /**
     * Heuristic to detect if the Fault Tolerance is enabled, in order to
     * disable the monitoring if FT is enabled.
     */
    private static int lastNumberOfNodes = 0;
    private static boolean FTEnabled = "enable".equals(ProActiveConfiguration.getInstance()
                                                                             .getFTState());

    private static boolean isFTEnabled() {
        if (!FTEnabled) {
            Collection<LocalNode> nodes = ((ProActiveRuntimeImpl) ProActiveRuntimeImpl.getProActiveRuntime()).getLocalNodes();
            if (nodes.size() != lastNumberOfNodes) {
                lastNumberOfNodes = nodes.size();
                for (LocalNode node : nodes) {
                    if ("enable".equals(node.getProperty("proactive.ft"))) {
                        FTEnabled = true;
                        break;
                    }
                }
            }
        }
        return FTEnabled;
    }
}
