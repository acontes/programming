package org.objectweb.proactive.core.body.tags;

import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.body.AbstractBody;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

public class LocalMemoryLeaseThread implements Runnable {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.MESSAGE_TAGGING_LOCALMEMORY_LEASING);
    
    private static final Thread singleton = new Thread(new LocalMemoryLeaseThread(), "ProActive LocalMemoryLeasing");
    
    public void run() {
        final int period = PAProperties.PA_MEMORY_TAG_LEASE_PERIOD.getValueAsInt();
        
        for(;;){
            logger.debug("LEASING THREAD RUNNING - " + this);
            try {
                Thread.sleep(period * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Iterator<UniversalBody> iter = LocalBodyStore.getInstance().getLocalBodies().bodiesIterator();
            while(iter.hasNext()){
                UniversalBody body = iter.next();
                if (body instanceof AbstractBody){
                    Map<String, LocalMemoryTag> memories = ((AbstractBody) body).getLocalMemoryTags();
                    for(LocalMemoryTag memory : memories.values()){
                        memory.decCurrentLease(period);
                        if (memory.leaseExceeded()){
                            logger.debug("Remove local memory of the Tag \"" + memory.getTagIDReferer() + "\"");
                            memories.remove(memory.getTagIDReferer());
                        }
                    }
                }
            }
        }
    }
    
    static public void start() {
        singleton.setDaemon(true);
        singleton.start();
    }

}
