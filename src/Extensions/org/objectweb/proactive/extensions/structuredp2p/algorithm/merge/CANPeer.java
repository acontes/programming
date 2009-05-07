package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import java.util.ArrayList;

import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.exception.AreaException;


@SuppressWarnings("serial")
public class CANPeer extends Peer {
    private ArrayList<int[]> splitHistory;

    public CANPeer() {
        super(OverlayType.CAN);
    }

    public void update(Area area, ArrayList<int[]> history) {
        this.getStructuredOverlay().setArea(area);
        this.setHistory(history);
    }

    public void join(CANPeer remotePeer) {
        int dimension = 0;
        int direction = 0;
        int directionInv = (direction + 1) % 2;

        // Get the next dimension to split onto
        if (this.splitHistory != null) {
            dimension = (this.splitHistory.get(this.splitHistory.size() - 1)[0] + 1) %
                CANOverlay.NB_DIMENSIONS;
        }

        // Create split areas
        Area[] newArea = this.getStructuredOverlay().getArea().split(dimension);

        // Actions on remotePeer
        ArrayList<int[]> history = this.splitHistory;
        history.add(new int[] { dimension, directionInv });
        remotePeer.update(newArea[directionInv], history);
        remotePeer.addNeighbor(remotePeer, dimension, directionInv);

        CANPeer neighbors = (CANPeer) remotePeer.getStructuredOverlay().getNeighborsForDimensionAndDirection(
                dimension, direction).getGroupByType();
        remotePeer.addNeighbor(neighbors, dimension, direction);

        // Actions on local peer
        this.getStructuredOverlay().setArea(newArea[direction]);
        this.splitHistory.add(new int[] { dimension, direction });
        this.removeNeighbor(neighbors);
        this.addNeighbor(remotePeer, dimension, direction);

    }

    public CANPeer leaveCAN() {
        int[] lastOP = this.splitHistory.get(this.splitHistory.size() - 1);
        int dimension = lastOP[0];
        int direction = lastOP[1];
        Group<Peer> neighbors = this.getStructuredOverlay().getNeighborsForDimensionAndDirection(dimension,
                direction);
        int nbNeigbors = neighbors.size();

        // If there is just one neighbor, easy
        if (nbNeigbors == 1) {
            ((CANPeer) neighbors.get(0)).merge(this);
        }
        // Else, do the same thing recursively (it's a little heavy with data transfer)
        else if (nbNeigbors > 1) {
            this.switchWith(((CANPeer) neighbors.get(0)).leaveCAN());
        }

        return this;
    }

    public void setHistory(ArrayList<int[]> history) {
        this.splitHistory = history;
    }

    public void addNeighbor(CANPeer remotePeer, int dimension, int direction) {
        this.getStructuredOverlay().addNeighbor(remotePeer, dimension, direction);
    }

    public void removeNeighbor(CANPeer remotePeer) {
        this.getStructuredOverlay().removeNeighbor(remotePeer);
    }

    public void merge(CANPeer remotePeer) {
        try {
            this.getStructuredOverlay().getArea().merge(remotePeer.getStructuredOverlay().getArea());
            this.splitHistory.remove(this.splitHistory.size() - 1);
        } catch (AreaException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public CANOverlay getStructuredOverlay() {
        return (CANOverlay) super.getStructuredOverlay();
    }

    public CANPeer switchWith(CANPeer remotePeer) {
        remotePeer.update(this.getStructuredOverlay().getArea(), this.splitHistory);
        this.getStructuredOverlay().setArea(null);
        this.setHistory(null);

        return this;
    }
}
