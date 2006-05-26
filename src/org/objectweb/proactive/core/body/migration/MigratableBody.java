/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
package org.objectweb.proactive.core.body.migration;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.BodyImpl;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.MetaObjectFactory;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.event.MigrationEventListener;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.runtime.ProActiveRuntime;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.ext.security.InternalBodySecurity;
import org.objectweb.proactive.ext.security.SecurityContext;
import org.objectweb.proactive.ext.security.exceptions.SecurityNotAvailableException;


public class MigratableBody extends BodyImpl implements Migratable,
    java.io.Serializable {
    protected static Logger bodyLogger = ProActiveLogger.getLogger(Loggers.BODY);
    protected static Logger migrationLogger = ProActiveLogger.getLogger(Loggers.MIGRATION);

    //
    // -- PROTECTED MEMBERS -----------------------------------------------
    //
    protected MigrationManager migrationManager;

    /** signal that the body has just migrated */
    protected transient boolean hasJustMigrated;

    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    public MigratableBody() {
    }

    public MigratableBody(Object reifiedObject, String nodeURL,
        MetaObjectFactory factory, String jobID) {
        super(reifiedObject, nodeURL, factory, jobID);
        this.migrationManager = factory.newMigrationManagerFactory()
                                       .newMigrationManager();
    }

    //
    // -- PUBLIC METHODS -----------------------------------------------
    //
    //
    // -- implements Migratable -----------------------------------------------
    //
    public UniversalBody migrateTo(Node node) throws MigrationException {
        return internalMigrateTo(node, false);
    }

    public UniversalBody cloneTo(Node node) throws MigrationException {
        return internalMigrateTo(node, true);
    }

    public void addMigrationEventListener(MigrationEventListener listener) {
        if (migrationManager != null) {
            migrationManager.addMigrationEventListener(listener);
        }
    }

    public void removeMigrationEventListener(MigrationEventListener listener) {
        if (migrationManager != null) {
            migrationManager.removeMigrationEventListener(listener);
        }
    }

    //
    // -- PROTECTED METHODS -----------------------------------------------
    //
    protected void activityStarted() {
        super.activityStarted();

        if (migrationLogger.isDebugEnabled()) {
            migrationLogger.debug("Body run on node " + nodeURL +
                " migration=" + hasJustMigrated);
        }
        if (bodyLogger.isDebugEnabled()) {
            bodyLogger.debug("Body run on node " + nodeURL + " migration=" +
                hasJustMigrated);
        }
        if (hasJustMigrated) {
            if (migrationManager != null) {
                migrationManager.startingAfterMigration(this);
            }
            hasJustMigrated = false;
        }
    }

    //
    // -- PRIVATE METHODS -----------------------------------------------
    //
    protected UniversalBody internalMigrateTo(Node node, boolean byCopy)
        throws MigrationException {
        UniqueID savedID = null;
        UniversalBody migratedBody = null;

        if (!isAlive()) {
            throw new MigrationException(
                "Attempt to migrate a dead body that has been terminated");
        }

        if (!isActive()) {
            throw new MigrationException("Attempt to migrate a non active body");
        }

        // check node with Manager
        node = migrationManager.checkNode(node);

        // get the name of the node
        String saveNodeURL = nodeURL;
        nodeURL = node.getNodeInformation().getURL();

        try {
            if (this.isSecurityOn) {
                // security checks
                try {
                    ProActiveRuntime runtimeDestination = node.getProActiveRuntime();

                    ArrayList entitiesFrom = null;
                    ArrayList entitiesTo = null;

                    entitiesFrom = this.getEntities();
                    entitiesTo = runtimeDestination.getEntities();

                    SecurityContext sc = new SecurityContext(SecurityContext.MIGRATION_TO,
                            entitiesFrom, entitiesTo);

                    SecurityContext result = null;

                    if (isSecurityOn) {
                        result = psm.getPolicy(sc);

                        if (!result.isMigration()) {
                            ProActiveLogger.getLogger(Loggers.SECURITY)
                                           .info("NOTE : Security manager forbids the migration");
                            return this;
                        }
                    } else {
                        // no local security but need to check if distant runtime accepts migration
                        result = runtimeDestination.getPolicy(sc);

                        if (!result.isMigration()) {
                            ProActiveLogger.getLogger(Loggers.SECURITY)
                                           .info("NOTE : Security manager forbids the migration");
                            return this;
                        }
                    }
                } catch (SecurityNotAvailableException e1) {
                    bodyLogger.debug("Security not available");
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            nodeURL = node.getNodeInformation().getURL();

            // stop accepting communication
            blockCommunication();

            // save the id
            savedID = bodyID;
            if (byCopy) {
                // if moving by copy we have to create a new unique ID
                // the bodyID will be automatically recreate when deserialized
                bodyID = null;
            }

            // security
            // save opened sessions
            if (this.isSecurityOn) {
                openedSessions = psm.getOpenedConnexion();
            }

            // try to migrate
            migratedBody = migrationManager.migrateTo(node, this);
            if (isSecurityOn) {
                this.internalBodySecurity.setDistantBody(migratedBody);
            }
        } catch (MigrationException e) {
            openedSessions = null;
            nodeURL = saveNodeURL;
            bodyID = savedID;
            localBodyStrategy.getFuturePool().unsetMigrationTag();
            if (this.isSecurityOn) {
                this.internalBodySecurity.setDistantBody(null);
            }
            acceptCommunication();
            throw e;
        }

        if (!byCopy) {
            changeBodyAfterMigration(migratedBody);
        } else {
            bodyID = savedID;
            nodeURL = saveNodeURL;
        }
        acceptCommunication();

        return migratedBody;
    }

    protected void changeBodyAfterMigration(UniversalBody migratedBody) {
        // cleanup after migration
        requestReceiver = migrationManager.createRequestReceiver(migratedBody,
                requestReceiver);
        replyReceiver = migrationManager.createReplyReceiver(migratedBody,
                replyReceiver);
        activityStopped();
        migrationManager = null;

        // signal that this body (remaining after migration) has just migrated
        hasJustMigrated = true;
        LocalBodyStore.getInstance().registerForwarder(this);
    }

    //
    // -- SERIALIZATION METHODS -----------------------------------------------
    //
    private void writeObject(java.io.ObjectOutputStream out)
        throws java.io.IOException {
        if (migrationLogger.isDebugEnabled()) {
            migrationLogger.debug("stream =  " + out);
        }
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in)
        throws java.io.IOException, ClassNotFoundException {
        if (migrationLogger.isDebugEnabled()) {
            migrationLogger.debug("stream =  " + in);
        }
        in.defaultReadObject();
        hasJustMigrated = true;
        if (this.isSecurityOn) {
            internalBodySecurity = new InternalBodySecurity(null);
            psm.setBody(this);
        }
    }

    /*
     * @see org.objectweb.proactive.core.body.LocalBodyStrategy#getNextSequenceID()
     */
    public long getNextSequenceID() {
        return localBodyStrategy.getNextSequenceID();
    }
}
