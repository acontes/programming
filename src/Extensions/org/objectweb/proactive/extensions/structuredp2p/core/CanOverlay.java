package org.objectweb.proactive.extensions.structuredp2p.core;

import java.util.Iterator;
import java.util.Random;

import org.objectweb.proactive.api.PAGroup;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.mop.ClassNotReifiableException;
import org.objectweb.proactive.extensions.structuredp2p.message.CanJoinMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.CanLookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LoadBalancingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.LookupMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.Message;
import org.objectweb.proactive.extensions.structuredp2p.message.PingMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanJoinResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.CanLookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.LookupResponseMessage;
import org.objectweb.proactive.extensions.structuredp2p.message.response.ResponseMessage;


/**
 * Content-Addressable Network (CAN) as a distributed infrastructure that provides hash table-like
 * functionality on Internet-like scales. The CAN is scalable, fault-tolerant and completely
 * self-organizing.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class CanOverlay extends StructuredOverlay {
    /**
     * The number of dimensions which is equals to the number of axes.
     */
    public static final int NB_DIMENSIONS = 2;

    /**
     * Neighbors of the current area. The neighbors are an array of ProActive groups.
     */
    private final Group<Peer>[][] neighbors;

    /**
     * The area which is currently managed.
     */
    private Area area;

    /**
     * Constructor.
     */
    public CanOverlay(Peer peer) {
        super(peer);
        this.neighbors = new Group[NB_DIMENSIONS][2];

        try {
            int i;
            for (i = 0; i < NB_DIMENSIONS; i++) {
                this.neighbors[i][0] = PAGroup.getGroup((Peer) PAGroup.newGroup(Peer.class.getName()));
                this.neighbors[i][1] = PAGroup.getGroup((Peer) PAGroup.newGroup(Peer.class.getName()));
            }
        } catch (ClassNotReifiableException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Splits the current area in two. The axe which is used in order to split is choose randomly
     * with {@link #getRandomAxe()}.
     * 
     * @param peer
     *            the new peer which want to join the area.
     */
    public void split(Peer peer) {
        // Verify the availability of the peer.
        ResponseMessage response = this.sendMessageTo(peer, new PingMessage());

        if (!response.isNull()) {
            // TODO How to split data ?
            // FIXME Split the data in two parts (basic method)
            /*
             * Coordinate[] middle = this.getMiddleArea(this.getRandomDimension()); Area newArea =
             * new Area(middle, this.area.getCoordinatesMax()); this.area = new
             * Area(this.area.getCoordinatesMin(), middle);
             */
        }

    }

    /**
     * Gets a random axe number.
     * 
     * @return the random axe number.
     */
    private int getRandomDimension() {
        Random rand = new Random();
        return rand.nextInt(CanOverlay.NB_DIMENSIONS);
    }

    /**
     * Verify if the coordinates in arguments are in the managed area.
     * 
     * @param coordinates
     *            the coordinates to check.
     * @return true if the coordinates are in the area, else otherwise.
     */
    public boolean contains(Coordinate[] coordinates) {
        int i = 0;
        Coordinate[] minArea = this.area.getCoordinatesMin();
     
        Coordinate[] maxArea = this.area.getCoordinatesMax();

        for (Coordinate coord : coordinates) {
            if (coord != null) {
                // if the current coordinates aren't in the peer area.
                if (minArea[i].getValue().compareTo(coord.getValue()) <= 0 &&
                    maxArea[i].getValue().compareTo(coord.getValue()) > 0)
                    return false;
                
            }

            i++;
        }

        return true;
    }

    /**
     * Check if the axe index of the current area contains the given coordinate.
     * 
     * @param axeIndex
     *            the axe index to check.
     * @param coordinate
     *            the coordinate to check.
     * @return 0 if the coordinate is contained by the area on the given axe, -1 if the coordinate
     *         is smaller than the line which is managed by the given axe, 1 otherwise.
     */
    public int contains(int axeIndex, Coordinate coordinate) {
        Coordinate[] minArea = this.area.getCoordinatesMin();
        Coordinate[] maxArea = this.area.getCoordinatesMax();

        if (minArea[axeIndex].getValue().compareTo(coordinate.getValue()) > 0 &&
            maxArea[axeIndex].getValue().compareTo(coordinate.getValue()) <= 0) {
            return 0;
        } else if (minArea[axeIndex].getValue().compareTo(coordinate.getValue()) > 0) {
            return -1;
        } else {
            return 1;
        }
    }

    /**
     * Merge two area when a peer leave the network. The split consists to give the data that are
     * managed by the peer which left the network to his neighbors and after to merge this area with
     * its closest neighbors.
     * 
     * @param peer
     *            the peer which left the network.
     */
    public void merge(Peer peer) {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkNeighbors() {
        for (Group<Peer>[] groupArray : neighbors) {
            for (Group<Peer> group : groupArray) {
                Group<ResponseMessage> groupFutures = (Group<ResponseMessage>) ((Peer) group.getGroupByType())
                        .receiveMessage(new PingMessage());
                PAGroup.waitAll(groupFutures);
                Iterator<ResponseMessage> it = groupFutures.listIterator();

                while (it.hasNext()) {
                    try {
                        // FIXME
                    } catch (Exception e) {
                        // FIXME
                    }
                }

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void join(Peer peer) {
        // FIXME
        int dim = this.getRandomDimension();

        this.addNeighbor(peer, dim, 1);
        this.sendMessageTo(peer, new CanJoinMessage(this.getPeer().getStub(), dim, 0));
        //((CanOverlay) peer.getStructuredOverlay()).addNeighbor(this.getPeer(), dim, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void leave() {
        // TODO Auto-generated method stub
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LookupResponseMessage sendMessage(LookupMessage msg) {
        CanLookupMessage msgCan = (CanLookupMessage) msg;
        if (this.contains(msgCan.getCoordinates())) {
            return msgCan.handle(this);
        } else {
          
            Group<Peer>[][] neighbors = this.neighbors;
            int pos;
            int neighborIndex;

            for (Group<Peer>[] neighborsGroup : neighbors) {
                for (int i = 0; i < CanOverlay.NB_DIMENSIONS; i++) {
                    pos = this.contains(i, msgCan.getCoordinates()[i]);

                    if (pos == -1) {
                        this.sendMessageTo(((Peer) neighborsGroup[0].getGroupByType()),
                                new LoadBalancingMessage());
                        neighborIndex = neighborsGroup[0].waitOneAndGetIndex();
                        return neighborsGroup[0].get(neighborIndex).sendMessage(msg);
                    } else if (pos == 1) {
                        this.sendMessageTo(((Peer) neighborsGroup[1].getGroupByType()),
                                new LoadBalancingMessage());
                        neighborIndex = neighborsGroup[1].waitOneAndGetIndex();
                        return neighborsGroup[1].get(neighborIndex).sendMessage(msg);
                    }
                }
            }
        }

        throw new IllegalArgumentException("The searched position doesn't exist.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ResponseMessage sendMessageTo(Peer peer, Message msg) {
        return peer.receiveMessage(msg);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        // TODO Auto-generated method stub
    }

    /**
     * FIXME
     * 
     * @return
     */
    public Area getArea() {
        return this.area;
    }

    /**
     * Set the new area covered.
     * 
     * @param area
     *            the new area covered.
     */
    public void setArea(Area area) {
        this.area = area;
    }

    /**
     * Returns the neighbors of the managed area.
     * 
     * @return the neighbors of the managed area.
     */
    public Group<Peer>[][] getNeighbors() {
        return this.neighbors;
    }

    /**
     * Add a new neighbor with a dimension and an order.
     * 
     * @param peer
     *            the new neighbor.
     * @param dimension
     *            the dimension.
     * @param order
     *            the order.
     */
    public void addNeighbor(Peer peer, int dimension, int order) {
        System.out.println(this.getPeer() + " add neighbor = " + peer + "; dim = " + dimension +
            "; order = " + order);
        this.neighbors[dimension][order].add(peer);
    }

    /**
     * Indicates if the given peer is the neighbor of the current area.
     * 
     * @param peer
     *            the peer which is used to check.
     * @return true if the peer is a neighbor, false otherwise.
     */
    public boolean hasNeighbor(Peer peer) {
        for (Group<Peer>[] neighborsAxe : this.neighbors) {
            for (Group<Peer> neighbor : neighborsAxe) {
                if (neighbor.contains(peer)) {
                    System.out.println("peer = " + peer + " neighbor = " +
                        neighbor.get(neighbor.indexOf(peer)));
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CanLookupResponseMessage handleLookupMessage(LookupMessage msg) {
        return new CanLookupResponseMessage(this.getPeer(), ((CanLookupMessage) msg).getCoordinates());
    }

    public ResponseMessage handleCanJoinMessage(Message msg) {
        CanJoinMessage message = (CanJoinMessage) msg;
        System.out.println("handle can join message");
        this.addNeighbor(message.getPeer(), message.getDimesion(), message.getOrder());
        return new CanJoinResponseMessage();
    }
}
