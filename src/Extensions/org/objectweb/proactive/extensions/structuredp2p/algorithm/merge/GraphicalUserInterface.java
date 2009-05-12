package org.objectweb.proactive.extensions.structuredp2p.algorithm.merge;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

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
    public int ratio = 1;

    private enum Action {
        SPLIT, MERGE
    };

    public GraphicalUserInterface(Zone zone) {
        this.zones = new ArrayList<Zone>();
        this.zones.add(zone);
        this.createAndShowGUI();
    }

    public void createAndShowGUI() {
        this.area = new Canvas(512, 512);
        // this.area.setSize(GraphicalUserInterface.SPACE_WIDTH,
        // GraphicalUserInterface.SPACE_HEIGHT);

        JComponent toolbar = this.createToolbar();
        Container contentPane = super.getContentPane();
        contentPane.add(toolbar, BorderLayout.NORTH);
        contentPane.add(this.area, BorderLayout.CENTER);

        super.setMinimumSize(new Dimension(512, 512 + 60));

        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(true);
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

    public class Canvas extends JComponent {

        public Canvas(int height, int width) {
            super();
            super.setSize(height, width);
            super.setMinimumSize(new Dimension(height, width));

            this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Zone clickedZone = Canvas.this.getClicked(e.getX(), e.getY());
                    System.out.println("clicked in x=" + e.getX() + ", y= " + e.getY());
                    /* Right click */
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        System.out.println("val = " + (clickedZone));

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
            for (Zone zone : GraphicalUserInterface.this.zones) {
                g.setColor(zone.color);

                g.fillRect(zone.xMin, zone.yMin, zone.xMax - zone.xMin, zone.yMax - zone.yMin);
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
