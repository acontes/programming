package org.objectweb.proactive.p2p.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.exceptions.proxy.FailedGroupRendezVousException;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.group.ProActiveGroup;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.util.UrlBuilder;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class AcquaintancesWrapper {
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.P2P_ACQUAINTANCES);
    private P2PService acquaintances = null;
    private Group groupOfAcquaintances = null;
    private ArrayList<String> urlList = new ArrayList<String>();

    public AcquaintancesWrapper() {
        try {
            acquaintances = (P2PService) ProActiveGroup.newGroup(P2PService.class.getName());
            ProActive.addNFEListenerOnGroup(this.acquaintances,
                FailedGroupRendezVousException.AUTO_GROUP_PURGE);
            this.groupOfAcquaintances = ProActiveGroup.getGroup(acquaintances);
        } catch (ClassNotReifiableException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean contains(P2PService p) {
        return this.groupOfAcquaintances.contains(p);
    }

    public boolean add(P2PService p, String peerUrl) {
        boolean result = this.groupOfAcquaintances.add(p);

        if (result) {
            try {
                logger.info("----- Adding " + peerUrl);
                urlList.add(UrlBuilder.getHostNameAndPortFromUrl(peerUrl));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    public boolean remove(P2PService p) {
        logger.info("------ Removing " + p);
        return this.groupOfAcquaintances.remove(p);
    }

    public P2PService get(int i) {
        return (P2PService) this.groupOfAcquaintances.get(i);
    }

    public P2PService getAcquaintances() {
        return this.acquaintances;
    }

    public Group getAcquaintancesAsGroup() {
        return groupOfAcquaintances;
    }

    public int size() {
        return this.groupOfAcquaintances.size();
    }

    public void dumpAcquaintances() {
        Iterator it = urlList.iterator();
        logger.info("***********************");
        while (it.hasNext()) {
            logger.info(it.next());
        }
        logger.info("***********************");
    }

    public String[] getAcquaintancesURLs() {
        return (String[]) urlList.toArray(new String[] {  });
    }
}
