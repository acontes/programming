package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;


@SuppressWarnings("serial")
public class GraphicalUserInterface extends JFrame {

    private JComponent area;
    private ArrayList<Zone> zones;
    private Action action = Action.SPLIT;

    private enum Action {
        SPLIT, MERGE
    };

    public GraphicalUserInterface(Zone zone) {
        this.zones = new ArrayList<Zone>();
        this.zones.add(zone);
        this.createAndShowGUI();
    }

    public void createAndShowGUI() {
        this.area = new Canvas(500, 500);

        JComponent toolbar = this.createToolbar();
        Container contentPane = super.getContentPane();

        contentPane.add(toolbar, BorderLayout.NORTH);
        contentPane.add(this.area, BorderLayout.CENTER);

        super.setSize(500, 560);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setTitle("CAN Split/Merge Algorithm");
        super.setLocationRelativeTo(null);
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

    public class Canvas extends JComponent {
        public Zone zoneClicked = null;

        public Canvas(int height, int width) {
            super();
            super.setSize(height, width);
            super.setPreferredSize(new Dimension(height, width));

            this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Zone clickedZone = Canvas.this.getClicked(e.getX(), e.getY());
                    System.out.println("clicked in x=" + e.getX() + ", y= " + e.getY());
                    /* Right click */
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        Canvas.this.zoneClicked = clickedZone;
                        System.out.println(clickedZone);
                        Canvas.this.repaint();
                    } else if (e.getButton() == MouseEvent.BUTTON1) {
                        /* Left click */
                        if (clickedZone != null) {
                            if (GraphicalUserInterface.this.action == GraphicalUserInterface.Action.SPLIT) {
                                Zone newZone = new Zone();
                                GraphicalUserInterface.this.zones.add(newZone);
                                newZone.join(clickedZone);
                            } else {
                                if (GraphicalUserInterface.this.zones.size() == 1) {
                                    JOptionPane.showMessageDialog(GraphicalUserInterface.this,
                                            "You cannot merge when there is only one peer !", "Warning",
                                            JOptionPane.WARNING_MESSAGE);
                                } else {
                                    clickedZone.leave();
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(GraphicalUserInterface.this,
                                    "Please clic on a peer !", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                    Canvas.this.repaint();
                }
            });
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (Zone zone : GraphicalUserInterface.this.zones) {
                g2d.setColor(zone.color);
                g2d.fillRect(zone.xMin, zone.yMin, zone.xMax - zone.xMin, zone.yMax - zone.yMin);
            }

            if (this.zoneClicked != null) {
                g2d.setColor(Color.black);
                g2d.drawLine(this.zoneClicked.xMin, this.zoneClicked.yMin, this.zoneClicked.xMax,
                        this.zoneClicked.yMax);

                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < 2; j++) {
                        for (Zone zone : this.zoneClicked.neighbors[i][j]) {
                            Random rand = new Random();
                            g2d.drawString("N#" + GraphicalUserInterface.this.zones.indexOf(zone) + " [" + i +
                                "][" + j + "]", zone.xMin - 25 + (zone.xMax - zone.xMin) / 2, zone.yMin + 5 +
                                rand.nextInt(20) + (zone.yMax - zone.yMin) / 2);

                        }
                    }
                }
                this.zoneClicked = null;
            }

        }

        public Zone getClicked(int x, int y) {
            for (Zone zone : GraphicalUserInterface.this.zones) {

                if (zone.contains(x, y)) {
                    return zone;
                }
            }
            return null;
        }
    }
}
