package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import java.util.ArrayList;

import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


@SuppressWarnings("serial")
public class CANPeer extends Peer {
    private Area area;
    private ArrayList<int[]> splitHistory;
    private CANOverlay overlay;

    public CANPeer() {
        super(OverlayType.CAN);
        this.area = new Area();
        this.overlay = (CANOverlay) this.getStructuredOverlay();
    }

    public void update(Area area, ArrayList<int[]> history) {
        this.setArea(area);
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
        Area[] newArea = this.area.split(dimension);

        // Actions on remotePeer
        ArrayList<int[]> history = this.splitHistory;
        history.add(new int[] { dimension, directionInv });
        remotePeer.update(newArea[directionInv], history);
        remotePeer.addNeighbor(remotePeer, dimension, directionInv);

        // Actions on local peer
        this.area = newArea[direction];
        this.splitHistory.add(new int[] { dimension, direction });
        this.addNeighbor(remotePeer, dimension, direction);
    }

    public void leave(CANPeer remotePeer) {
        int[] lastOP = this.splitHistory.get(this.splitHistory.size() - 1);
        int dimension = lastOP[0];
        int direction = lastOP[1];
        Group<Peer> neighbors = this.overlay.getNeighborsForDimensionAndDirection(dimension, direction);

        if (neighbors.size() == 1) {

        }

    }

    public void setArea(Area newArea) {
        this.area = newArea;
    }

    public void setHistory(ArrayList<int[]> history) {
        this.splitHistory = history;
    }

    public void addNeighbor(CANPeer remotePeer, int dimension, int direction) {
        this.overlay.addNeighbor(remotePeer, dimension, direction);
    }
}
