package org.objectweb.proactive.extensions.structuredp2p.messages.can;

import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.data.DataStorage;
import org.objectweb.proactive.extensions.structuredp2p.messages.Message;
import org.objectweb.proactive.extensions.structuredp2p.responses.ResponseMessage;


/**
 * A {@code CANMergeMessage} is used when a {@link Peer} must merge with an another {@link Peer}.
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
    private final Area area;

    /**
     * The resources to merge with.
     */
    private final DataStorage resources;

    /**
     * Constructor.
     * 
     * @param remoteArea
     *            the area to merge with.
     * @param remoteResources
     *            the resources to merge with.
     */
    public CANMergeMessage(Area remoteArea, DataStorage remoteResources) {
        this.area = remoteArea;
        this.resources = remoteResources;
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
    public Area getArea() {
        return this.area;
    }

    /**
     * Returns the resources to merge with.
     * 
     * @return the resources to merge with.
     */
    public DataStorage getResources() {
        return this.resources;
    }
}
