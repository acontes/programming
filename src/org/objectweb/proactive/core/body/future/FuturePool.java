/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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

import org.objectweb.proactive.Body;
import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.ProActiveRuntimeException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.reply.Reply;
import org.objectweb.proactive.core.body.reply.ReplyImpl;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.mop.Utils;
import org.objectweb.proactive.ext.security.ProActiveSecurityManager;
import org.objectweb.proactive.ext.security.exceptions.SecurityNotAvailableException;


public class FuturePool extends Object implements java.io.Serializable {
    protected boolean newState;
 
    // table of future and ACs
    private FutureMap futures;

    // ID of the body corresponding to this futurePool
    private UniqueID ownerBody;

    // Active queue of AC services
    private transient ActiveACQueue queueAC;

    // toggle for enabling or disabling automatic continuation 
    private boolean acEnabled;

    // table used for storing values which arrive in the futurePool BEFORE the registration
    // of its corresponding future.
    private java.util.HashMap valuesForFutures;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public FuturePool() {
        futures = new FutureMap();
        valuesForFutures = new java.util.HashMap();
        this.newState = false;
        if ("enable".equals(ProActiveConfiguration.getACState())) {
            this.acEnabled = true;
        } else {
            this.acEnabled = false;
        }
        if (acEnabled) {
            queueAC = new ActiveACQueue();
            queueAC.start();
        }
    }

    //
    // -- STATIC ------------------------------------------------------
    //
    // this table is used to register destination before sending.
    // So, a future could retreive its destination during serialization
    // this table indexed by the thread which perform the registration.
    static private java.util.Hashtable bodyDestination;

    // to register in the table
    static public void registerBodyDestination(UniversalBody dest) {
        bodyDestination.put(Thread.currentThread(), dest);
    }

    // to clear an entry in the table
    static public void removeBodyDestination() {
        bodyDestination.remove(Thread.currentThread());
    }

    // to get a destination
    static public UniversalBody getBodyDestination() {
        return (UniversalBody) (bodyDestination.get(Thread.currentThread()));
    }

    // this table is used to register deserialized futures after receive
    // So, futures to add in the local futurePool could be retreived
    static private java.util.Hashtable incomingFutures;

    // to register an incoming future in the table  	
    public static void registerIncomingFuture(Future f) {
        java.util.ArrayList listOfFutures = (java.util.ArrayList) incomingFutures.get(Thread.currentThread());
        if (listOfFutures != null) {
            listOfFutures.add(f);
        } else {
            java.util.ArrayList newListOfFutures = new java.util.ArrayList();
            newListOfFutures.add(f);
            incomingFutures.put(Thread.currentThread(), newListOfFutures);
        }
    }

    // to remove an entry from the table
    static public void removeIncomingFutures() {
        incomingFutures.remove(Thread.currentThread());
    }

    // to get a list of incomingFutures
    static public java.util.ArrayList getIncomingFutures() {
        return (java.util.ArrayList) (incomingFutures.get(Thread.currentThread()));
    }

    // static init block
    static {
        bodyDestination = new java.util.Hashtable();
        incomingFutures = new java.util.Hashtable();
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //

    /**
     * Setter of the ID of the body corresonding to this FuturePool
     * @param i ID of the owner body.
     */
    public void setOwnerBody(UniqueID i) {
        ownerBody = i;
    }

    /**
     * Getter of the ID of the body corresonding to this FuturePool
     */
    public UniqueID getOwnerBody() {
        return ownerBody;
    }

    /**
     * To enable the automatic continuation behaviour for all futures in
     * this FuturePool
     * */
    public void enableAC() {
        this.queueAC = new ActiveACQueue();
        this.queueAC.start();
        this.acEnabled = true;
    }

    /**
     * To disable the automatic continuation behaviour for all futures in
     * this FuturePool
     * */
    public void disableAC() {
        this.acEnabled = false;
        this.queueAC.killMe();
        this.queueAC = null;
    }

    /**
     * Method called when a reply is recevied, ie a value is available for a future.
     * This method perform local futures update, and put an ACService in the activeACqueue.
     * @param id sequence id of the future to update
     * @param creatorID ID of the body creator of the future to update
     * @param result value to update with the futures
     */
    public synchronized void receiveFutureValue(long id, UniqueID creatorID,
        Object result) throws java.io.IOException {
        // get all aiwated futures
        java.util.ArrayList futuresToUpdate = futures.getFuturesToUpdate(id,
                creatorID);

        if (futuresToUpdate != null) {
            Future future = (Future) (futuresToUpdate.get(0));
            if (future != null) {
                future.receiveReply(result);
            }

            // if there are more than one future to update, we "give" deep copy
            // of the result to the other futures to respect ProActive model
            // We use here the migration tag to perform a simple serialization (ie 
            // without continuation side-effects)
            setMigrationTag();
            for (int i = 1; i < futuresToUpdate.size(); i++) {
                Future otherFuture = (Future) (futuresToUpdate.get(i));
                otherFuture.receiveReply(Utils.makeDeepCopy(result));
            }
            unsetMigrationTag();
            stateChange();

            // 2) create and put ACservices
            if (acEnabled) {
                java.util.ArrayList bodiesToContinue = futures.getAutomaticContinuation(id,
                        creatorID);
                if ((bodiesToContinue != null) &&
                        (bodiesToContinue.size() != 0)) {
                    ProActiveSecurityManager psm = null;
                    try {
                        psm = ProActive.getBodyOnThis()
                                       .getProActiveSecurityManager();
                    } catch (SecurityNotAvailableException e) {
                        psm = null;
                    }
                    queueAC.addACRequest(new ACService(bodiesToContinue,
                            new ReplyImpl(creatorID, id, null, result, psm)));
                }
            }

            // 3) Remove futures from the futureMap
            futures.removeFutures(id, creatorID);
        } else {
            // we have to store the result until future arrive
            this.valuesForFutures.put("" + id + creatorID, result);
        }
    }

    /**
     * To put a future in the FutureMap
     * @param futureObject future to register
     */
    public synchronized void receiveFuture(Future futureObject) {
        futureObject.setSenderID(ownerBody);
        futures.receiveFuture(futureObject);
        long id = futureObject.getID();
        UniqueID creatorID = futureObject.getCreatorID();
        if (valuesForFutures.get("" + id + creatorID) != null) {
            try {
                this.receiveFutureValue(id, creatorID,
                    valuesForFutures.remove("" + id + creatorID));
            } catch (java.io.IOException e) {
            }
        }
    }

    /**
     * To add an automatic contiunation, ie a destination body, for a particular future.
     * @param id sequence id of the corresponding future
     * @param creatorID UniqueID of the body which creates futureObject
     * @param bodyDest body destination of this continuation
     */
    public void addAutomaticContinuation(long id, UniqueID creatorID,
        UniversalBody bodyDest) {
        futures.addAutomaticContinuation(id, creatorID, bodyDest);
    }

    public synchronized void waitForReply(long timeout)
        throws ProActiveException {
        this.newState = false;
        // variable used to know wether the timeout has expired or not
        int timeoutCounter = 1;
        while (!newState) {
            timeoutCounter--;
            // counter < 0 means that it is the second time we enter in the loop
            // while the state has not been changed, i.e timeout has expired
            if (timeoutCounter < 0) {
                throw new ProActiveException(
                    "Timeout expired while waiting for future update");
            }
            try {
                wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * To register a destination before sending a reques or a reply
     * Registration key is the calling thread.
     */
    public void registerDestination(UniversalBody dest) {
        if (acEnabled) {
            FuturePool.registerBodyDestination(dest);
        }
    }

    /**
     * To clear registred destination for the calling thread.
     */
    public void removeDestination() {
        if (acEnabled) {
            FuturePool.removeBodyDestination();
        }
    }

    public void setMigrationTag() {
        futures.setMigrationTag();
    }

    public void unsetMigrationTag() {
        futures.unsetMigrationTag();
    }

    //
    // -- PRIVATE METHODS -----------------------------------------------
    //
    private void stateChange() {
        this.newState = true;
        notifyAll();
    }

    //
    // -- PRIVATE METHODS FOR SERIALIZATION -----------------------------------------------
    //
    private void writeObject(java.io.ObjectOutputStream out)
        throws java.io.IOException {
        setMigrationTag();
        out.defaultWriteObject();
        if (acEnabled) {
            // send the queue of AC requests
            out.writeObject(queueAC.getQueue());
            // stop the ActiveQueue thread 
            queueAC.killMe();
        }
    }

    private void readObject(java.io.ObjectInputStream in)
        throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        unsetMigrationTag();
        if (acEnabled) {
            // create a new ActiveACQueue
            java.util.ArrayList queue = (java.util.ArrayList) (in.readObject());
            queueAC = new ActiveACQueue(queue);
            queueAC.start();
        }
    }

    //--------------------------------INNER CLASS------------------------------------//

    /**
     * Active Queue for AC. This queue has his own thread to perform ACservices
     * available in the queue. This thread is compliant with migration by using
     * the threadStore of the body correponding to this FutureMap.
     * Note that the ACServices are served in FIFO manner.
     * @see ACservice
     */
    private class ActiveACQueue extends Thread {
        private java.util.ArrayList queue;
        private int counter;
        private boolean kill;

        //
        // -- CONSTRUCTORS -----------------------------------------------
        //
        public ActiveACQueue() {
            queue = new java.util.ArrayList();
            counter = 0;
            kill = false;
            this.setName("Thread for AC");
        }

        public ActiveACQueue(java.util.ArrayList queue) {
            this.queue = queue;
            counter = queue.size();
            kill = false;
            this.setName("Thread for AC");
        }

        //
        // -- PUBLIC METHODS -----------------------------------------------
        //

        /**
         * return the current queue of ACServices to perform
         */
        public java.util.ArrayList getQueue() {
            return queue;
        }

        /**
         * Add a ACservice in the active queue.
         */
        public synchronized void addACRequest(ACService r) {
            queue.add(r);
            counter++;
            notifyAll();
        }

        /**
         * Return the oldest request in queue and remove it from the queue
         */
        public synchronized ACService removeACRequest() {
            counter--;
            return (ACService) (queue.remove(0));
        }

        /**
         * To stop the thread.
         */
        public synchronized void killMe() {
            kill = true;
            notifyAll();
        }

        public void run() {
            // get a reference on the owner body
            // try until it's not null because deserialization of the body 
            // may be not finished when we restart the thread.
            Body owner = null;
            while (owner == null) {
                owner = LocalBodyStore.getInstance().getLocalBody(ownerBody);
                // it's a halfbody...
                if (owner == null) {
                    owner = LocalBodyStore.getInstance().getLocalHalfBody(ownerBody);
                }
            }

            while (true) {
                // if there is no AC to do, wait...
                waitForAC();

                if (kill) {
                    break;
                }

                // there are ACs to do !
                try {
                    // enter in the threadStore 
                    owner.enterInThreadStore();

                    // if body has migrated, kill the thread
                    if (kill) {
                        break;
                    }

                    ACService toDo = this.removeACRequest();
                    if (toDo != null) {
                        toDo.doAutomaticContinuation();
                    }

                    // exit from the threadStore
                    owner.exitFromThreadStore();
                } catch (Exception e2) {
                    // to unblock active object
                    owner.exitFromThreadStore();
                    throw new ProActiveRuntimeException("Error while sending reply for AC ",
                        e2);
                }
            }
        }

        // synchronized wait on ACRequest queue
        private synchronized void waitForAC() {
            try {
                while ((counter == 0) && !kill) {
                    wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A simple object for a request for an automatic continuation
     * @see ActiveACQueue
     */
    private class ACService implements java.io.Serializable {
        // bodies that have to be updated	
        private java.util.ArrayList dests;

        // reply to send
        private Reply reply;

        //
        // -- CONSTRUCTORS -----------------------------------------------
        //
        public ACService(java.util.ArrayList dests, Reply reply) {
            this.dests = dests;
            this.reply = reply;
        }

        //
        // -- PUBLIC METHODS -----------------------------------------------
        //
        public void doAutomaticContinuation() throws java.io.IOException {
            if (dests != null) {
                for (int i = 0; i < dests.size(); i++) {
                    UniversalBody dest = (UniversalBody) (dests.get(i));
                    registerDestination(dest);
                    reply.send(dest);
                    removeDestination();
                }
            }
        }
    } //ACService
}
