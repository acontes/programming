package org.objectweb.proactive.api;

import java.net.URI;
import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.servers.location.LocationServer;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcess;
import org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer;
import org.objectweb.proactive.core.body.ft.service.FaultToleranceTechnicalService;
import org.objectweb.proactive.core.node.Node;
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

    private FTServerInformation ftServer;

    /* ****************************
     * Contact server
     * ****************************/

    public void setServerInformation(String urlGlobal) throws ProActiveException {
        FTServerInformation info = new FTServerInformation();
        try {
            Object server = PARemoteObject.lookup(new URI(urlGlobal));
            info.storage = (CheckpointServer) server;
            info.location = (LocationServer) server;
            info.recovery = (RecoveryProcess) server;
            ftServer = info;
        } catch (Exception e) {
            throw new ProActiveException("Unable to contact FTServer at " + urlGlobal, e);
        }
    }

    public void setServerInformation(String urlCheckpoint, String urlLocation, String urlRecovery)
            throws ProActiveException {
        FTServerInformation info = new FTServerInformation();
        try {
            if ((urlCheckpoint != null) && (urlRecovery != null) && (urlLocation != null)) {
                info.storage = (CheckpointServer) (PARemoteObject.lookup(new URI(urlCheckpoint)));
                info.location = (LocationServer) (PARemoteObject.lookup(new URI(urlLocation)));
                info.recovery = (RecoveryProcess) (PARemoteObject.lookup(new URI(urlRecovery)));
            } else {
                throw new ProActiveException("Servers are not correctly set");
            }
            ftServer = info;
        } catch (Exception e) {
            throw new ProActiveException("Unable to contact FTServer", e);
        }
    }

    public void setServerInformation(Node node) throws ProActiveException {
        String urlGlobal = node.getProperty(FaultToleranceTechnicalService.GLOBAL_SERVER);
        if (urlGlobal != null) {
            setServerInformation(urlGlobal);
        } else {
            String urlCheckpoint = node.getProperty(FaultToleranceTechnicalService.CKPT_SERVER);
            String urlRecovery = node.getProperty(FaultToleranceTechnicalService.RECOVERY_SERVER);
            String urlLocation = node.getProperty(FaultToleranceTechnicalService.LOCATION_SERVER);
            setServerInformation(urlCheckpoint, urlRecovery, urlLocation);
        }
    }

    public FTServerInformation getServerInformation() throws ProActiveException {
        if (ftServer == null) {
            throw new ProActiveException("FT: no server defined");
        }
        return ftServer;
    }

    /* ****************************
     * Trigger Checkpoint
     * ****************************/

    /* global */

    /**
     * Trigger a global checkpoint of the entire application
     * 
     * @return The line number of the triggered checkpoint
     * @throws ProActiveException 
     */
    public int triggerGlobalCheckpoint() throws ProActiveException {
        /*
         * next_line = last_global_checkpoint + 1
         * aos = ft_server.registered_aos
         * aos.each {|ao| trigger_local_checkpoint(ao, next_line) }
         * return next_line
         */
        FTServerInformation server = getServerInformation();
        System.out.println("Replay: server: " + server);
        return server.storage.getLastGlobalState();
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
                try {
                    int line = triggerGlobalCheckpoint();
                    hook.receiveCheckpoint(line);
                } catch (ProActiveException e) {
                    hook.catchException(e);
                }
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

        void catchException(ProActiveException e);
    }

    public class FTServerInformation {
        public CheckpointServer storage;
        public LocationServer location;
        public RecoveryProcess recovery;
    }

}
