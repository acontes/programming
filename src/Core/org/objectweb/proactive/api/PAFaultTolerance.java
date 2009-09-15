package org.objectweb.proactive.api;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.ft.checkpointing.Checkpoint;
import org.objectweb.proactive.core.body.ft.internalmsg.FTMessage;
import org.objectweb.proactive.core.body.ft.protocols.FTManager;
import org.objectweb.proactive.core.body.ft.servers.location.LocationServer;
import org.objectweb.proactive.core.body.ft.servers.recovery.RecoveryProcess;
import org.objectweb.proactive.core.body.ft.servers.storage.CheckpointServer;
import org.objectweb.proactive.core.body.ft.servers.util.ActiveQueueJob;
import org.objectweb.proactive.core.body.ft.service.FaultToleranceTechnicalService;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


public class PAFaultTolerance implements Serializable {

    /* ****************************
     * Implementation specificities
     * ****************************/

    /* log */

    private static final long serialVersionUID = 318973729783053229L;

    protected final static Logger logger = ProActiveLogger.getLogger(Loggers.CORE);

    /* Singleton pattern */

    private static PAFaultTolerance instance;

    private int wantedLine = 0;

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

    public synchronized void setServerInformation(String urlGlobal) throws ProActiveException {
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

    public synchronized void setServerInformation(String urlCheckpoint, String urlLocation, String urlRecovery)
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

    public synchronized void setServerInformation(Node node) throws ProActiveException {
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

    public synchronized FTServerInformation getServerInformation() throws ProActiveException {
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
    public synchronized int triggerGlobalCheckpoint() throws ProActiveException {
        /*
         * next_line = last_global_checkpoint + 1
         * aos = ft_server.registered_aos
         * aos.each {|ao| trigger_local_checkpoint(ao, next_line) }
         * return next_line
         */
        int waitTime = 500;
        int totalTime = 10000;
        int currentTime = 0;

        FTServerInformation server = getServerInformation();
        int nextLine = getNextWantedLine();
        for (UniversalBody body : server.location.getAllLocations()) {
            System.out.println("trigger checkpoint for " + body);
            triggerLocalCheckpoint(body.getID(), nextLine);
        }
        while (getLastGlobalCheckpointNumber() < nextLine && currentTime < totalTime) {
            try {
                System.out.println("try... " + currentTime + "/" + totalTime);
                Thread.sleep(waitTime);
                currentTime += waitTime;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (getLastGlobalCheckpointNumber() < nextLine) {
            wantedLine--;
            throw new ProActiveException("FT: Error during triggering checkpoint");
        }
        return nextLine;
    }

    /**
     * Asynchronously trigger a global checkpoint of the entire application
     * 
     * @param hook The hook to execute when checkpoint is stored
     */
    public synchronized void triggerGlobalCheckpoint(final CheckpointReceiver hook) {
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
    public synchronized int triggerLocalCheckpoint(UniqueID target) throws ProActiveException {
        /*
         * ao = ft_server.registered_aos[target]
         * line = ao.ft_manager.trigger_checkpoint
         * return line
         */
        FTServerInformation server = getServerInformation();
        try {
            server.location.getLocation(target).receiveFTMessage(new FTMessage() {
                private static final long serialVersionUID = -7946796254940775983L;

                @Override
                public Object handleFTMessage(FTManager ftm) {
                    ftm.triggerNextCheckpoint();
                    return null;
                }
            });
        } catch (IOException e) {
            throw new ProActiveException(e);
        }
        return server.storage.getLastState(target);
    }

    /**
     * Force an active object to trigger a local checkpoint
     * 
     * Assert that the triggered checkpoint is newer than the given line
     * 
     * @param target The active object that will trigger a checkpoint
     * @param lineNumber
     */
    public synchronized void triggerLocalCheckpoint(UniqueID target, int lineNumber)
            throws ProActiveException {
        /*
         * begin
         *   triggered = trigger_local_checkpoint(target)
         * end while triggered < number
         */
        int lastNumber = getLastCheckpointNumber(target);
        System.out.println("trigger : " + target + " : " + lastNumber + "/" + lineNumber);
        for (int i = lastNumber; i <= lineNumber; i++) {
            System.out.println("trigger [" + i + "/" + lineNumber + "] " + target);
            triggerLocalCheckpoint(target);
        }
    }

    /* ****************************
     * Get checkpoints
     * ****************************/

    /* global */

    private synchronized int getNextWantedLine() throws ProActiveException {
        int lastLine = getLastGlobalCheckpointNumber();
        if (lastLine > wantedLine) {
            wantedLine = lastLine;
        }
        wantedLine++;
        return wantedLine;
    }

    /**
     * Get the number of the last line
     * 
     * @return The number of last global checkpoint
     */
    public synchronized int getLastGlobalCheckpointNumber() throws ProActiveException {
        /*
         * line = ft_server.last_line
         * return line
         */
        int lastLine = getServerInformation().storage.getLastGlobalState();
        return lastLine;
    }

    /**
     * Get the number of a object
     * 
     * @return The number of last global checkpoint
     */
    public synchronized int getLastCheckpointNumber(UniqueID target) throws ProActiveException {
        /*
         * line = ft_server.last_line_of target
         * return line
         */
        FTServerInformation server = getServerInformation();
        return server.storage.getLastState(target);
    }

    /**
     * Get the map of all stored checkpoints
     * 
     * @return The map of checkpoints
     */
    public synchronized Map<UniqueID, Checkpoint> getAvailableCheckpointsMap() throws ProActiveException {
        /*
         * list = ft_server.checkpoints
         * return list
         */
        throw new UnsupportedOperationException();
    }

    /**
     * Get the list of all stored checkpoints of an object
     * 
     * @return The list of checkpoints
     */
    public synchronized List<Checkpoint> getAvailableCheckpointsList(UniqueID target)
            throws ProActiveException {
        /*
         * list = ft_server.checkpoints[target]
         * return list
         */
        FTServerInformation server = getServerInformation();
        int lastState = server.storage.getLastState(target);
        List<Checkpoint> list = new ArrayList<Checkpoint>(lastState);
        for (int i = 0; i < lastState; i++) {
            list.add(server.storage.getCheckpoint(target, i));
        }
        return list;
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
    public synchronized void addCheckpoint(UniqueID target, Checkpoint checkpoint) throws ProActiveException {
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
    public synchronized void addAllCheckpoint(UniqueID target, List<Checkpoint> checkpoints)
            throws ProActiveException {
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
    public synchronized void restartFromLastCheckpoint() throws ProActiveException {
        /*
         * restart_from_checkpoint_number( last_global_checkpoint )
         */
        //restartFromCheckpointNumber(getLastGlobalCheckpointNumber());.
        FTServerInformation server = getServerInformation();
        UniversalBody body = server.location.getAllLocations().get(0);
        server.recovery.failureDetected(body.getID());
    }

    /**
     * Restart the application from the given line number
     * 
     * @param lineNumber The line number to restarting from
     */
    public synchronized void restartFromCheckpointNumber(int lineNumber) throws ProActiveException {
        /*
         * assert { line_number <= last_global_checkpoint }
         * ft_server.restart_from( line_number )
         */
        if (lineNumber <= getLastGlobalCheckpointNumber()) {
            FTServerInformation server = getServerInformation();
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

    public class FTServerInformation implements Serializable {
        private static final long serialVersionUID = 4922386730966662943L;
        public CheckpointServer storage;
        public LocationServer location;
        public RecoveryProcess recovery;
    }

}
