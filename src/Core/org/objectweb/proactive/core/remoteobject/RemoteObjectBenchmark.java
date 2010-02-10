package org.objectweb.proactive.core.remoteobject;

import java.lang.ref.WeakReference;

import org.objectweb.proactive.core.util.URIBuilder;

/**
 * This class contains all benchmarks results.
 *
 */
public class RemoteObjectBenchmark {        
       
    transient private java.util.Hashtable<String, BenchmarkMonitorThread> benchmarkMonitors;
    
    private static RemoteObjectBenchmark singleInstance = null;
    
    public static RemoteObjectBenchmark getInstance() {
        if (singleInstance == null){
            singleInstance = new RemoteObjectBenchmark();
        }
        return singleInstance;
    }
    
    private RemoteObjectBenchmark (){
        this.benchmarkMonitors = new java.util.Hashtable<String, BenchmarkMonitorThread>();
    }
    
    /**
     * If needed launch benchmark or simply add RemoteObjectSet as an observer
     */
    public void subscribeAsObserver(RemoteObjectSet ros, String[] runtimeUrls) {
        String runtimeName = URIBuilder.getNameFromURI(runtimeUrls[0]);
        BenchmarkMonitorThread bmt;
        if ((bmt = this.benchmarkMonitors.get(runtimeName)) != null) {
            // Benchmark is in progress
            bmt.addObserver(ros);
            bmt.notifyAdd();
        } else {
            bmt = new BenchmarkMonitorThread(runtimeUrls);
            this.benchmarkMonitors.put(runtimeName, bmt);
            bmt.addObserver(ros);
            bmt.launchBenchmark();
        }
    }       
}
