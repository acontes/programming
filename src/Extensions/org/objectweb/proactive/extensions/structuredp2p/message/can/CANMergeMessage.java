package org.objectweb.proactive.extensions.structuredp2p.message.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.response.ResponseMessage;


/**
 * A CANMergeMessage is a concrete message to merge two CAN peers.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CANMergeMessage extends Message {

    /**
     * The area to merge with.
     */
    private final Area remoteArea;

    /**
     * The resources to merge with.
     */
    private final DataStorage remoteResources;

    /**
     * Constructor.
     * 
     * @param remoteArea
     *            the area to merge with.
     * @param remoteResources
     *            the resources to merge with.
     */
    public CANMergeMessage(Area remoteArea, DataStorage remoteResources) {
        this.remoteArea = remoteArea;
        this.remoteResources = remoteResources;
    }

    /**
     * {@inheritDoc}
     */
    public ResponseMessage handle(StructuredOverlay overlay) {
        return ((CANOverlay) overlay).handleMergeMessage(this);
    }

    /**
     * Returns the area to merge with.
     * 
     * @return the area to merge with.
     */
    public Area getRemoteArea() {
        return this.remoteArea;
    }

    /**
     * Returns the resources to merge with.
     * 
     * @return the resources to merge with.
     */
    public DataStorage getRemoteResources() {
        return this.remoteResources;
    }
}
