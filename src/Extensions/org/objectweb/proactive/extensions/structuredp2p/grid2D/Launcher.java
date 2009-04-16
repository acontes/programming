package org.objectweb.proactive.extensions.structuredp2p.grid2D;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.util.Deployment;


/**
 * Launch a new grid of active objects which know their neighbors.
 * 
 * @author Kilanga Fanny
 * @author Trovato Alexandre
 * @author Pellegrino Laurent
 * 
 * @version 0.1
 */
public class Launcher {
    private int nbCols;
    private int nbRows;
    
    private String entryPointURL = "Grid2DEntryPoint";
    private List<Node> avaibleNodes;

    private static boolean running = true;
    
    /**
     * Constructor.
     * 
     * @param rows
     *            the number rows for the grid 2D.
     * 
     * @param cols
     *            the number of columns for the grid 2D.
     */
    public Launcher(String hostname, int rows, int cols) {
        this.entryPointURL = hostname;
        this.nbRows = rows;
        this.nbCols = cols;
    }

    /**
     * Creates the entry point. It's the required {@link AwareObject} in order to enter in the
     * AwareObject grid network.
     */
    public void createEntryPoint() {
        AwareObject entryPoint = null;
        this.avaibleNodes = Deployment.getVirtualNode("Grid2D").getNewNodes();

        try {
            entryPoint = (AwareObject) PAActiveObject.newActive(AwareObject.class.getName(), new Object[] {
                    new Integer(0), new Integer(0) });

            // Binds the entry point to a specific URL on the RMI registry
            PAActiveObject.registerByName(entryPoint, "Grid2DEntryPoint");
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates <code>nbRows</code> <code>nbCols</code> AwareObjects by forming a grid2D. Each object
     * is inserted while using the <code>entryPoint</code>.
     */
    private void createsAwareObjects() {
        int nbAwareObjects = this.nbCols * this.nbRows;
        int x = 0;
        int y = 0;

        AwareObject entryPoint = null;
        AwareObject newAwareObject = null;

        // Retrieve entryPoint
        try {
            entryPoint = (AwareObject) PAActiveObject.lookupActive(AwareObject.class.getName(),
                    this.entryPointURL);
        } catch (ActiveObjectCreationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int nodeIndex = 0;
        for (int i = 1; i < nbAwareObjects; i++) {
            if (i % this.nbCols == 0) {
                x = 0;
                y++;
            } else {
                x++;
            }

            try {
                newAwareObject = (AwareObject) PAActiveObject.newActive(AwareObject.class.getName(),
                        new Object[] { new Integer(x), new Integer(y) }, this.avaibleNodes.get(nodeIndex));
                Launcher.registerNewPeer(entryPoint, newAwareObject.getStub());
            } catch (ActiveObjectCreationException e) {
                e.printStackTrace();
            } catch (NodeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (nodeIndex == (this.avaibleNodes.size() - 1)) {
                    nodeIndex = 0;
                } else {
                    nodeIndex++;
                }
            }
        }
    }

    /**
     * Register a new peer as a neighbor.
     * 
     * @param entryPoint
     *            the stub of the entryPoint used in order to find the position to register.
     * 
     * @param peerStub
     *            the stub of the peer to register.
     * 
     * @throws Exception
     *             if the position is already token.
     */
    public static void registerNewPeer(AwareObject entryPoint, AwareObject peerStub) throws Exception {
        int peerX = peerStub.getX();
        int peerY = peerStub.getY();

        if (PAFuture.getFutureValue(entryPoint.find(peerX, peerY)) != null) {
            throw new Exception("This position is already used (x=" + entryPoint.getX() + ", y=" +
                entryPoint.getY() + ")");
        }

        AwareObject northObj = entryPoint.find(peerX, peerY - 1);
        AwareObject eastObj = entryPoint.find(peerX + 1, peerY);
        AwareObject southObj = entryPoint.find(peerX, peerY + 1);
        AwareObject westObj = entryPoint.find(peerX - 1, peerY);

        if (PAFuture.getFutureValue(northObj) != null) {
            northObj = northObj.getStub();

            northObj.setSouthNeighbor(peerStub);
            peerStub.setNorthNeighbor(northObj);
        }

        if (PAFuture.getFutureValue(eastObj) != null) {
            eastObj = eastObj.getStub();

            eastObj.setWestNeighbor(peerStub);
            peerStub.setEastNeighbor(eastObj);
        }

        if (PAFuture.getFutureValue(southObj) != null) {
            southObj = southObj.getStub();

            southObj.setNorthNeighbor(peerStub);
            peerStub.setSouthNeighbor(southObj);
        }

        if (PAFuture.getFutureValue(westObj) != null) {
            westObj = westObj.getStub();

            westObj.setEastNeighbor(peerStub);
            peerStub.setWestNeighbor(westObj);
        }
    }

    /**
     * Entry point of the application.
     * 
     * @param args
     *            parameters given to the application when launched.
     */
    public static void main(String args[]) {
        if (args.length != 4) {
            System.err.println("Usage : java " + Launcher.class.getCanonicalName() + " " +
                "nbRows nbCols descriptor entryPoint");
        }

        String hostname = "Grid2DEntryPoint";

        if (!args[3].equals("localhost")) {
            hostname = "//" + args[3] + "/" + hostname;
        }

        final String finalHostname = hostname;

        try {
            Deployment.deploy(args[2]);
        } catch (NodeException e) {
            e.printStackTrace();
        } catch (ProActiveException e) {
            e.printStackTrace();
        }

        final int nbRows = Integer.parseInt(args[0]);
        final int nbCols = Integer.parseInt(args[1]);

        Launcher launcher = new Launcher(hostname, nbRows, nbCols);

        launcher.createEntryPoint();
        launcher.createsAwareObjects();

        Thread inputThread = new Thread(new Runnable() {
            @Override
            public void run() {
                AwareObject founded;
                Scanner scanner = new Scanner(System.in);
                String inputLine;
                String[] coordinates;
                int x;
                int y;

                AwareObject entryPoint = null;
                try {
                    entryPoint = (AwareObject) PAActiveObject.lookupActive(AwareObject.class.getName(),
                            finalHostname);
                } catch (ActiveObjectCreationException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Launcher.printOptions();
                while (Launcher.running) {
                    inputLine = scanner.nextLine();

                    if (inputLine.equalsIgnoreCase("quit")) {
                        try {
                            Deployment.kill();
                            Launcher.running = false;
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (inputLine.matches("[0-9]+ [0-9]+")) {
                        coordinates = inputLine.split("\\s");
                        x = Integer.parseInt(coordinates[0]);
                        y = Integer.parseInt(coordinates[1]);

                        if (x >= nbCols || x < 0 || y >= nbRows || y < 0) {
                            System.err.println("Error, x must be in [0," + (int) (nbCols - 1) +
                                "] and y in [0," + (int) (nbRows - 1) + "]");
                        } else {
                            founded = entryPoint.find(x, y);
                            if (PAFuture.getFutureValue(founded) != null) {
                                System.out.println(founded);
                            } else {
                                System.out.println("AwareObject not found in (" + x + ", " + y + ").");
                            }
                        }
                    }
                    Launcher.printOptions();
                }
            }
        });
        inputThread.start();
    }

    /**
     * Print app menu option on the standard output.
     */
    private static void printOptions() {
        System.out.println("* What you can do :");
        System.out.println("  > Type in a coordinate like '0 1'");
        System.out.println("  > Type in 'quit' keyword in order to quit the application");
    }
}
