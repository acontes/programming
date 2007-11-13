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
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.body.future;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.objectweb.proactive.annotation.PublicAPI;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.internalmsg.Heartbeat;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.mop.MOP;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.runtime.LocalNode;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;


public class FutureMonitoring implements Runnable {

    /** Ping one body every 21s */
    private static int TTM = 21000;

    /**
     * For each body, the list of futures to monitor. We ping the updater body,
     * so we should detect a broken automatic continuations chain.
     */
    private static final ConcurrentHashMap<UniqueID, ConcurrentLinkedQueue<FutureProxy>> futuresToMonitor =
        new ConcurrentHashMap<UniqueID, ConcurrentLinkedQueue<FutureProxy>>();

    static {

        /* Dynamically configurable to make shorter tests */
        String ttm = PAProperties.PA_FUTUREMONITORING_TTM.getValue();
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
     * The FT Message used to test the communication
     */
    private static Heartbeat HEARTBEAT_MSG = new Heartbeat();

    /**
     * @return true iff a body has been pinged
     */
    private static boolean pingBody(UniqueID bodyId) {
        boolean pinged = false;
        FutureMonitoringPingFailureException bodyException = null;
        Collection<FutureProxy> futures = futuresToMonitor.get(bodyId);
        if (futures == null) {

            /*
             * By the time we got to iterate over these futures, they have all
             * been updated, so the body entry was removed.
             */
            return false;
        }

        for (FutureProxy fp : futures) {
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
                        fp.receiveReply(new MethodCallResult(null, bodyException));
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
            for (UniqueID bodyId : futuresToMonitor.keySet()) {
                boolean pingedThisBody = pingBody(bodyId);
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

    private static UniqueID getUpdaterBodyId(FutureProxy fp) {
        if (isFTEnabled()) {
            return null;
        }
        UniversalBody body = fp.getUpdater();
        if (body == null) {
            new Exception("Cannot monitor this future, unknown updater body").printStackTrace();
            return null;
        }
        return body.getID();
    }

    public static void removeFuture(FutureProxy fp) {
        UniqueID updaterId = getUpdaterBodyId(fp);
        if (updaterId == null) {
            return;
        }
        synchronized (futuresToMonitor) {
            /*
             * Avoid a race with monitorFutureProxy(FutureProxy)
             */
            ConcurrentLinkedQueue<FutureProxy> futures = futuresToMonitor.get(updaterId);
            if (futures != null) {
                futures.remove(fp);
                if (futures.isEmpty()) {
                    futuresToMonitor.remove(updaterId);
                }
            }
        }
    }

    public static void monitorFutureProxy(FutureProxy fp) {
        if (fp.isAvailable()) {
            return;
        }
        UniqueID updaterId = getUpdaterBodyId(fp);
        if (updaterId == null) {
            return;
        }
        synchronized (futuresToMonitor) {
            /*
             * Avoid a race with the suppression in the ConcurrentHashMap when the
             * ConcurrentLinkedQueue is empty.
             */
            ConcurrentLinkedQueue<FutureProxy> futures = futuresToMonitor.get(updaterId);
            if (futures == null) {
                futures = new ConcurrentLinkedQueue<FutureProxy>();
                futuresToMonitor.put(updaterId, futures);
            }
            if (!futures.contains(fp)) {
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
    private static boolean FTEnabled = PAProperties.PA_FT.isTrue();

    private static boolean isFTEnabled() {
        if (!FTEnabled) {
            Collection<LocalNode> nodes = ProActiveRuntimeImpl.getProActiveRuntime()
                                                              .getLocalNodes();
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
