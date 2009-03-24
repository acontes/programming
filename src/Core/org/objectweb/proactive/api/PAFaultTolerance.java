package org.objectweb.proactive.api;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.ft.servers.location.LocationServer;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcess;
import org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer;
import org.objectweb.proactive.core.body.ft.service.FaultToleranceTechnicalService;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class PAFaultTolerance {

    /* ****************************
     * Implementation specificities
     * ****************************/

    /* log */

    protected final static Logger logger = ProActiveLogger.getLogger(Loggers.CORE);

    /* Singleton pattern */

    private static PAFaultTolerance instance;

    private PAFaultTolerance() {
    }

    public static PAFaultTolerance getInstance() {
        if (instance == null)
            instance = new PAFaultTolerance();
        return instance;
    }

    /* ****************************
     * Variables
     * ****************************/

    FTServerInformation ftServer;

    /* ****************************
     * Contact server
     * ****************************/

    public void contactServer() throws ProActiveException {
        if (ftServer != null)
            return;
        Exception error = null;
        FTServerInformation info = new FTServerInformation();
        try {
            PAProperties ttcValue = PAProperties.getProperty(FaultToleranceTechnicalService.TTC);
            if (ttcValue != null) {
                info.ttc = ttcValue.getValueAsInt() * 1000;
            } else {
                info.ttc = FTManager.DEFAULT_TTC_VALUE;
            }
            PAProperties urlGlobal = PAProperties.getProperty(FaultToleranceTechnicalService.GLOBAL_SERVER);
            if (urlGlobal != null) {
                Remote server = Naming.lookup(urlGlobal.getValue());
                info.storage = (CheckpointServer) server;
                info.location = (LocationServer) server;
                info.recovery = (RecoveryProcess) server;
            } else {
                PAProperties urlCheckpoint = PAProperties
                        .getProperty(FaultToleranceTechnicalService.CKPT_SERVER);
                PAProperties urlRecovery = PAProperties
                        .getProperty(FaultToleranceTechnicalService.RECOVERY_SERVER);
                PAProperties urlLocation = PAProperties
                        .getProperty(FaultToleranceTechnicalService.LOCATION_SERVER);
                if ((urlCheckpoint != null) && (urlRecovery != null) && (urlLocation != null)) {
                    info.storage = (CheckpointServer) (Naming.lookup(urlCheckpoint.getValue()));
                    info.location = (LocationServer) (Naming.lookup(urlLocation.getValue()));
                    info.recovery = (RecoveryProcess) (Naming.lookup(urlRecovery.getValue()));
                } else {
                    error = new ProActiveException("Servers are not correctly set");
                }
            }
        } catch (MalformedURLException e) {
            error = e;
        } catch (RemoteException e) {
            error = e;
        } catch (NotBoundException e) {
            error = e;
        }
        if (error != null) {
            throw new ProActiveException("Unable to contact FTServer", error);
        } else {
            ftServer = info;
        }
    }

    /* ****************************
     * Trigger Checkpoint
     * ****************************/

    /* global */

    /**
     * Trigger a global checkpoint of the entire application
     * 
     * @return The line number of the triggered checkpoint
     */
    public int triggerGlobalCheckpoint() {
        /*
         * next_line = last_global_checkpoint + 1
         * aos = ft_server.registered_aos
         * aos.each {|ao| trigger_local_checkpoint(ao, next_line) }
         * return next_line
         */
        throw new UnsupportedOperationException();
    }

    /**
     * Asynchronously trigger a global checkpoint of the entire application
     * 
     * @param hook The hook to execute when checkpoint is stored
     */
    public void triggerGlobalCheckpoint(final CheckpointReceiver hook) {
        /*
         * Thread.new { hook.receive_checkpoint(trigger_global_checkpoint) }
         */
        new Thread() {
            @Override
            public void run() {
                hook.receiveCheckpoint(triggerGlobalCheckpoint());
            }
        }.start();
    }

    /* local */

    /**
     * Force an active object to trigger a local checkpoint
     * 
     * @param target The active object that will trigger a checkpoint
     * @return The number of the triggered checkpoint
     */
    public int triggerLocalCheckpoint(UniqueID target) {
        /*
         * ao = ft_server.registered_aos[target]
         * line = ao.ft_manager.trigger_checkpoint
         * return line
         */
        throw new UnsupportedOperationException();
    }

    /**
     * Force an active object to trigger a local checkpoint
     * 
     * Assert that the triggered checkpoint is newer than the given line
     * 
     * @param target The active object that will trigger a checkpoint
     * @param lineNumber
     */
    public void triggerLocalCheckpoint(UniqueID target, int lineNumber) {
        /*
         * next_line = last_global_checkpoint + 1
         * begin
         *   triggered = trigger_local_checkpoint(target)
         * end while triggered < next_line
         */
        throw new UnsupportedOperationException();
    }

    /* ****************************
     * Get checkpoints
     * ****************************/

    /* global */

    /**
     * Get the number of the last line
     * 
     * @return The number of last global checkpoint
     */
    public int getLastGlobalCheckpointNumber() {
        /*
         * line = ft_server.last_line
         * return line
         */
        throw new UnsupportedOperationException();
    }

    /**
     * Get the list of all stored checkpoints
     * 
     * @return The list of checkpoints
     */
    public List<Checkpoint> getCheckpointsList() {
        /*
         * list = ft_server.checkpoints
         * return list
         */
        throw new UnsupportedOperationException();
    }

    /* ****************************
     * Add checkpoints
     * ****************************/

    /**
     * Add a checkpoint to an active object
     * 
     * @param target
     * @param checkpoint
     */
    public void addCheckpoint(UniqueID target, Checkpoint checkpoint) {
        /*
         * ao = ft_server.registered_aos[target]
         * ao.ft_manager.checkpoints << checkpoint
         * # assert coherence of ao.ft.checkpoints
         */
        throw new UnsupportedOperationException();
    }

    /**
     * Add a list of checkpoint to an active object
     * 
     * @param target
     * @param checkpoints
     */
    public void addAllCheckpoint(UniqueID target, List<Checkpoint> checkpoints) {
        /*
         * checkpoints.each {|checkpoint| add_checkpoint(target, checkpoint) }
         */
        throw new UnsupportedOperationException();
    }

    /* ****************************
     * Restart from checkpoint
     * ****************************/

    /**
     * Restart application from the last global checkpoint
     */
    public void restartFromLastCheckpoint() {
        /*
         * restart_from_checkpoint_number( last_global_checkpoint )
         */
        restartFromCheckpointNumber(getLastGlobalCheckpointNumber());
    }

    /**
     * Restart the application from the given line number
     * 
     * @param lineNumber The line number to restarting from
     */
    public void restartFromCheckpointNumber(int lineNumber) {
        /*
         * assert { line_number >= last_global_checkpoint }
         * ft_server.restart_from( line_number )
         */
        if (lineNumber >= getLastGlobalCheckpointNumber()) {
            throw new UnsupportedOperationException();
        } else {
            // throw exception ?
        }
    }

    /* ****************************
     * Utilities
     * ****************************/

    /**
     * This class is used to provide an hook to the asynchronous triggerGlobalCheckpoint() method
     */
    public interface CheckpointReceiver {
        /**
         * 
         * @param lineNumber The number of the returned line
         */
        void receiveCheckpoint(int lineNumber);
    }

    public class FTServerInformation {
        public int ttc;
        public CheckpointServer storage;
        public LocationServer location;
        public RecoveryProcess recovery;
    }

}

