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


@SuppressWarnings("serial")
public class GraphicalUserInterface extends JFrame {

    private JComponent area;
    private ArrayList<CANPeer> peers;
    private CANPeer selected;

    private static int SPACE_WIDTH = 400;
    private static int SPACE_HEIGHT = 400;

    public GraphicalUserInterface(CANPeer peer) {
        this.peers = new ArrayList<CANPeer>();
        this.peers.add(peer);
        this.createAndShowGUI();
    }

    public void createAndShowGUI() {
        this.area = new Canvas(this);
        // this.area.setSize(GraphicalUserInterface.SPACE_WIDTH,
        // GraphicalUserInterface.SPACE_HEIGHT);

        this.area.setSize(Integer.parseInt(this.peers.get(0).getStructuredOverlay().getArea()
                .getCoordinatesMax(0).getValue()), Integer.parseInt(this.peers.get(0).getStructuredOverlay()
                .getArea().getCoordinatesMax(1).getValue()));

        Container contentPane = super.getContentPane();
        contentPane.add(this.createToolbar(), BorderLayout.NORTH);
        contentPane.add(this.area, BorderLayout.CENTER);

        super.setMinimumSize(new Dimension(GraphicalUserInterface.SPACE_WIDTH,
            GraphicalUserInterface.SPACE_HEIGHT));
        super.setResizable(false);
        super.setTitle("CAN Merge Algorithm");
        super.setLocationRelativeTo(null);
    }

    private JComponent createToolbar() {
        JPanel toolbar = new JPanel();
        toolbar.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        final JButton splitButton = new JButton("Split");
        final JButton mergeButton = new JButton("Merge");

        splitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CANPeer newPeer = new CANPeer();
                GraphicalUserInterface.this.peers.add(newPeer);
                newPeer.join(GraphicalUserInterface.this.selected);
            }
        });

        mergeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                GraphicalUserInterface.this.selected.leaveCAN();
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
            return new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        }
    }
}
