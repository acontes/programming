package org.objectweb.proactive.core.remoteobject;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestImpl;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.runtime.ProActiveRuntimeImpl;
import org.objectweb.proactive.core.security.exceptions.RenegotiateSessionException;
import org.objectweb.proactive.core.util.URIBuilder;


public class BenchmarkMonitorThread extends Observable {
    private String[] remoteRTurls;
    private ArrayList<Pair> orderedProtocols;
    private boolean finished = false;
    private Thread thread;

    public BenchmarkMonitorThread(String PARTurls[]) {
        this.remoteRTurls = PARTurls;
        this.orderedProtocols = new ArrayList<Pair>();
    }

    private void addAndSort(Pair pair) {
        this.orderedProtocols.add(pair);
        Collections.sort(orderedProtocols);
    }

    private String[] getArray() {
        String[] ret = new String[orderedProtocols.size()];
        for (int i = 0; i < orderedProtocols.size(); i++) {
            ret[i] = orderedProtocols.get(i).protocol;
        }
        return ret;
    }

    public class BenchmarkThread implements Runnable {
        public void run() {
            finished = false;            
            for (String partUrl : remoteRTurls) {
                try {                    
                    Request bmoMessage = new BenchmarkRequest();
                    RemoteObjectAdapter bmoAdapter = (RemoteObjectAdapter) RemoteObjectHelper.lookup(new URI(
                        partUrl));

                    int count = 0;
                    long time = System.currentTimeMillis();
                    long limit = PAProperties.PA_BENCHMARK_PROTOCOL_DURATION.getValueAsInt();
                    while (System.currentTimeMillis() - time < limit) {
                        count += (Integer) bmoAdapter.receiveMessage(bmoMessage).getResult().getResult();
                    }
                    System.out.println("Benchmark for "+partUrl+ " is "+count);
                    Pair pair = new Pair(URIBuilder.getProtocol(partUrl), count);
                    addAndSort(pair);
                } catch (NullPointerException npe) {
                    // it seems that the PA runtime is down, so benchmarking is useless
                    npe.printStackTrace();
                    return;
                } catch (ActiveObjectCreationException e) {
                    e.printStackTrace();
                } catch (ProActiveException e) {
                    e.printStackTrace();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (RenegotiateSessionException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            finished = true;
            setChanged();
            notifyObservers(getArray());
            deleteObservers();
        }
    }

    private class Pair implements Comparable<Pair> {
        public int throughput;
        public String protocol;

        public Pair(String protocol, int throughput) {
            this.throughput = throughput;
            this.protocol = protocol;
        }

        // Sorted from faster (highest throughput at rank 0) to slower
        // Inverse order, considering throughput
        public int compareTo(Pair o) {
            return o.throughput - this.throughput;
        }
    }

    public void launchBenchmark() {
        this.thread = new Thread(new BenchmarkThread());
        this.thread.start();
    }

    public void notifyAdd() {
        if (finished) {
            this.setChanged();
            this.notifyObservers(getArray());
            deleteObservers();
        }
    }

}
