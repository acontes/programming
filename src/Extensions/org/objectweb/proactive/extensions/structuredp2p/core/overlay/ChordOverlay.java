/**
 * 
 */
package org.objectweb.proactive.extensions.structuredp2p.core.overlay;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay;
import org.objectweb.proactive.extensions.structuredp2p.message.AddNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LeaveMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.RemoveNeighborMessage;
import org.objectweb.proactive.extensions.structuredp2p.response.ActionResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.response.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.response.ResponseMessage;


/**
 * @author Alex
 * 
 */
@SuppressWarnings("serial")
public class ChordOverlay extends StructuredOverlay {

    public ChordOverlay(Peer peer) {
        super(peer);
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#checkNeighbors()
     */
    @Override
    public void checkNeighbors() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#handleAddNeighborMessage
     * (org.objectweb.proactive.extensions.structuredp2p.message.AddNeighborMessage)
     */
    @Override
    public ActionResponseMessage handleAddNeighborMessage(AddNeighborMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#handleJoinMessage
     * (org.objectweb.proactive.extensions.structuredp2p.message.Message)
     */
    @Override
    public ActionResponseMessage handleJoinMessage(Message msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#handleLeaveMessage
     * (org.objectweb.proactive.extensions.structuredp2p.message.LeaveMessage)
     */
    @Override
    public ActionResponseMessage handleLeaveMessage(LeaveMessage leaveMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#handleLookupMessage
     * (org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage)
     */
    @Override
    public LookupResponseMessage handleLookupMessage(LookupMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#
     * handleRemoveNeighborMessage
     * (org.objectweb.proactive.extensions.structuredp2p.message.RemoveNeighborMessage)
     */
    @Override
    public ActionResponseMessage handleRemoveNeighborMessage(RemoveNeighborMessage removeNeighborMessage) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#join(org.objectweb
     * .proactive.extensions.structuredp2p.core.Peer)
     */
    @Override
    public Boolean join(Peer remotePeer) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#leave()
     */
    @Override
    public Peer leave() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#sendMessage(org.objectweb
     * .proactive.extensions.structuredp2p.message.LookupMessage)
     */
    @Override
    public LookupResponseMessage sendMessage(LookupMessage msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#sendMessageTo(org
     * .objectweb.proactive.extensions.structuredp2p.core.Peer,
     * org.objectweb.proactive.extensions.structuredp2p.message.Message)
     */
    @Override
    public ResponseMessage sendMessageTo(Peer peer, Message msg) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.extensions.structuredp2p.core.StructuredOverlay#update()
     */
    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

}
