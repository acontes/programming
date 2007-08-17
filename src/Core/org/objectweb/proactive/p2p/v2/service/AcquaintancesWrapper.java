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
package org.objectweb.proactive.p2p.v2.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.exceptions.NonFunctionalException;
import org.objectweb.proactive.core.exceptions.manager.NFEListener;
import org.objectweb.proactive.core.exceptions.proxy.FailedGroupRendezVousException;
import org.objectweb.proactive.core.group.ExceptionInGroup;
import org.objectweb.proactive.core.group.ExceptionListException;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.group.ProActiveGroup;
import org.objectweb.proactive.core.group.ProxyForGroup;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class AcquaintancesWrapper implements Serializable {
    private final static Logger logger = ProActiveLogger.getLogger(Loggers.P2P_ACQUAINTANCES);
    private P2PService acquaintances = null;
    private Group groupOfAcquaintances = null;
    private ArrayList<String> urlList = new ArrayList<String>();

    public AcquaintancesWrapper() {
        try {
            acquaintances = (P2PService) ProActiveGroup.newGroup(P2PService.class.getName());
            ProActive.addNFEListenerOnGroup(this.acquaintances,
                new AutomaticPurgeGroup());
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

        //this.groupOfAcquaintances.indexOf(p);
        if (result) {
            //            try {
            logger.info("----- Adding " + peerUrl);
            urlList.add(P2PService.getHostNameAndPortFromUrl(peerUrl));
            //            } catch (UnknownHostException e) {
            //                e.printStackTrace();
            //            }
        }

        return result;
    }

    public boolean remove(P2PService p, String peerUrl) {
        logger.info("------ Removing " + p);
        //        try {
        urlList.remove(P2PService.getHostNameAndPortFromUrl(peerUrl));
        //		} catch (UnknownHostException e) {
        //			e.printStackTrace();
        //		}
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

    //    public void dumpAcquaintances() {
    //        Iterator it = urlList.iterator();
    //        logger.info("***********************");
    //        while (it.hasNext()) {
    //            logger.info(it.next());
    //        }
    //        logger.info("***********************");
    //    }
    public String[] getAcquaintancesURLs() {
        return (String[]) urlList.toArray(new String[] {  });
    }

    class AutomaticPurgeGroup implements NFEListener {
        public boolean handleNFE(NonFunctionalException nfe) {
            Iterator exceptions;
            ProxyForGroup group;
            ExceptionListException exceptionList;

            try {
                FailedGroupRendezVousException fgrve = (FailedGroupRendezVousException) nfe;
                exceptionList = (ExceptionListException) fgrve.getCause();
                group = fgrve.getGroup();
            } catch (ClassCastException cce) {
                return false;
            }

            synchronized (exceptionList) {
                exceptions = exceptionList.iterator();

                while (exceptions.hasNext()) {
                    ExceptionInGroup eig = (ExceptionInGroup) exceptions.next();
                    group.remove(eig.getObject());

                    int index = eig.getIndex();
                    System.out.println(
                        "AutomaticPurgeGroup.handleNFE() removing " + index);
                    urlList.remove(index);
                }
            }

            return true;
        }
    }
}
