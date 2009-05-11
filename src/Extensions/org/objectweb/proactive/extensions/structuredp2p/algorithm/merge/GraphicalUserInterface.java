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
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.structuredp2p.core.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.OverlayType;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;


@SuppressWarnings("serial")
public class GraphicalUserInterface extends JFrame {

    private JComponent area;
    private ArrayList<Peer> peers;
    private Peer selected;

    private static int SPACE_WIDTH = 400;
    private static int SPACE_HEIGHT = 400;

    public GraphicalUserInterface(Peer peer) {
        this.peers = new ArrayList<Peer>();
        this.peers.add(peer);
        this.selected = peer;
        this.createAndShowGUI();
    }

    public void createAndShowGUI() {
        this.area = new Canvas(this);
        // this.area.setSize(GraphicalUserInterface.SPACE_WIDTH,
        // GraphicalUserInterface.SPACE_HEIGHT);

        this.area.setSize(Integer.parseInt(((CANOverlay) this.peers.get(0).getStructuredOverlay()).getArea()
                .getCoordinatesMax(0).getValue()), Integer.parseInt(((CANOverlay) this.peers.get(0)
                .getStructuredOverlay()).getArea().getCoordinatesMax(1).getValue()));

        Container contentPane = super.getContentPane();
        contentPane.add(this.createToolbar(), BorderLayout.NORTH);
        contentPane.add(this.area, BorderLayout.CENTER);

        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setMinimumSize(new Dimension(GraphicalUserInterface.SPACE_WIDTH,
            GraphicalUserInterface.SPACE_HEIGHT));
        super.setResizable(false);
        super.setTitle("CAN Merge Algorithm");
        super.setLocationRelativeTo(null);
        this.pack();
    }

    private JComponent createToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        final JButton splitButton = new JButton("Split");
        final JButton mergeButton = new JButton("Merge");

        splitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Peer newPeer;
                try {
                    newPeer = (Peer) PAActiveObject.newActive(Peer.class.getCanonicalName(),
                            new Object[] { OverlayType.CAN });
                    GraphicalUserInterface.this.peers.add(newPeer);
                    newPeer.join(GraphicalUserInterface.this.selected);
                } catch (ActiveObjectCreationException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (NodeException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });

        mergeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraphicalUserInterface.this.selected.leave();
            }
        });

        toolbar.add(splitButton);
        toolbar.add(mergeButton);

        return toolbar;
    }

    public class Canvas extends JComponent {
        public Canvas(JFrame frame) {
            super.setSize(GraphicalUserInterface.SPACE_WIDTH, GraphicalUserInterface.SPACE_HEIGHT);
            super.setMinimumSize(new Dimension(GraphicalUserInterface.SPACE_WIDTH,
                GraphicalUserInterface.SPACE_HEIGHT));
            this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    super.mouseClicked(e);
                    System.out.println("Click in (" + e.getX() + "," + e.getY() + ")");
                }
            });
        }

        public void paintComponent(Graphics g) {
            g.setColor(this.getRandomColor());
            g.fillRect(0, 0, GraphicalUserInterface.SPACE_WIDTH, GraphicalUserInterface.SPACE_HEIGHT);
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
