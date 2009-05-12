package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.Area;
import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


@SuppressWarnings("serial")
public class GraphicalUserInterface extends JFrame {

    private JComponent area;
    private ArrayList<Peer> peers;
    private Action action = Action.SPLIT;
    public int ratio = 1;

    private enum Action {
        SPLIT, MERGE
    };

    private static int SPACE_WIDTH = 400;
    private static int SPACE_HEIGHT = 400;

    public GraphicalUserInterface(Peer peer) {
        this.peers = new ArrayList<Peer>();
        this.peers.add(peer);
        this.createAndShowGUI();
    }

    public void createAndShowGUI() {
        this.area = new Canvas(Area.MAX_COORD, Area.MAX_COORD);
        // this.area.setSize(GraphicalUserInterface.SPACE_WIDTH,
        // GraphicalUserInterface.SPACE_HEIGHT);

        JComponent toolbar = this.createToolbar();
        Container contentPane = super.getContentPane();
        contentPane.add(toolbar, BorderLayout.NORTH);
        contentPane.add(this.area, BorderLayout.CENTER);

        super.setMinimumSize(new Dimension(Area.MAX_COORD, Area.MAX_COORD + 60));

        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setTitle("CAN Merge Algorithm");
        super.setLocationRelativeTo(null);
        this.pack();
    }

    private JComponent createToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setSize(new Dimension(0, 60));
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        final JButton splitButton = new JButton("Split");
        final JButton mergeButton = new JButton("Merge");

        GraphicalUserInterface.this.action = Action.SPLIT;
        splitButton.setEnabled(false);
        mergeButton.setEnabled(true);

        splitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraphicalUserInterface.this.action = Action.SPLIT;
                splitButton.setEnabled(false);
                mergeButton.setEnabled(true);
            }
        });

        mergeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraphicalUserInterface.this.action = Action.MERGE;
                mergeButton.setEnabled(false);
                splitButton.setEnabled(true);
            }
        });

        toolbar.add(splitButton);
        toolbar.add(mergeButton);

        return toolbar;
    }

    public Collection<Peer> getPeers() {
        return this.peers;
    }

    public class Canvas extends JComponent {

        public Canvas(int height, int width) {
            super();
            super.setSize(height, width);
            super.setMinimumSize(new Dimension(height, width));

            this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Peer clickedPeer = Canvas.this.getClicked(e.getX(), e.getY());
                    System.out.println("clicked in x=" + e.getX() + ", y= " + e.getY());
                    /* Right click */
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        System.out.println("val = " + (clickedPeer));
                        Coordinate[] min = ((CANOverlay) clickedPeer.getStructuredOverlay()).getArea()
                                .getCoordinatesMin();
                        Coordinate[] max = ((CANOverlay) clickedPeer.getStructuredOverlay()).getArea()
                                .getCoordinatesMin();

                        Canvas.this.getGraphics().drawLine(Integer.parseInt(min[0].getValue()),
                                Integer.parseInt(max[0].getValue()), Integer.parseInt(min[1].getValue()),
                                Integer.parseInt(max[1].getValue()));
                        Canvas.this.repaint();

                    }
                    /* Left click */
                    else if (clickedPeer != null) {
                        if (GraphicalUserInterface.this.action == GraphicalUserInterface.Action.SPLIT) {
                            Peer newPeer = null;
                            try {
                                newPeer = (Peer) PAActiveObject.newActive(Peer.class.getCanonicalName(),
                                        new Object[] { OverlayType.CAN });
                                GraphicalUserInterface.this.peers.add(newPeer);
                                newPeer.join(clickedPeer);
                            } catch (ActiveObjectCreationException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            } catch (NodeException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        } else {
                            if (GraphicalUserInterface.this.peers.size() == 1) {
                                JOptionPane.showMessageDialog(GraphicalUserInterface.this,
                                        "You cannot merge when there is only one peer !", "Warning",
                                        JOptionPane.WARNING_MESSAGE);
                            } else {
                                clickedPeer.leave();
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(GraphicalUserInterface.this, "Please clic on a peer !",
                                "Warning", JOptionPane.WARNING_MESSAGE);
                    }
                    Canvas.this.repaint();
                }
            });
        }

        public void paintComponent(Graphics g) {
            for (Peer p : GraphicalUserInterface.this.getPeers()) {
                g.setColor(this.getRandomColor());

                Coordinate[] min = ((CANOverlay) p.getStructuredOverlay()).getArea().getCoordinatesMin();
                Coordinate[] max = ((CANOverlay) p.getStructuredOverlay()).getArea().getCoordinatesMax();

                int width = Integer.parseInt(max[0].getValue()) - Integer.parseInt(min[0].getValue());
                g.fillRect(Integer.parseInt(min[0].getValue()), Integer.parseInt(min[1].getValue()), width,
                        width);
            }

        }

        public Peer getClicked(int x, int y) {
            for (Peer p : GraphicalUserInterface.this.getPeers()) {

                if (((CANOverlay) p.getStructuredOverlay()).contains(new Coordinate[] {
                        new Coordinate("" + x), new Coordinate("" + y) })) {
                    return p;
                }
            }
            return null;
        }

        public Color getRandomColor() {
            Random rand = new Random();

            int r = rand.nextInt(256);
            int v = rand.nextInt(256);
            int b = rand.nextInt(256);

            if (r + v + b < 477) {
                return this.getRandomColor();
            }

            return new Color(r, v, b);
        }
    }
}