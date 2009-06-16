package org.objectweb.proactive.examples.structuredp2p.can;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.can.Zone;


@SuppressWarnings("serial")
public class ContentAddressableNetworkGUI extends JFrame implements Observer {

    private JComponent area;
    private PeerLauncher peerLauncher;
    private int HEIGHT = 500;
    private int WIDTH = 500;

    public ContentAddressableNetworkGUI(PeerLauncher peerLauncher) {
        this.peerLauncher = peerLauncher;
        this.createAndShowGUI();
    }

    public void createAndShowGUI() {
        this.area = new Canvas(this.WIDTH, this.HEIGHT);

        Container contentPane = super.getContentPane();

        contentPane.add(this.area, BorderLayout.CENTER);

        super.setSize(this.WIDTH, this.HEIGHT);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(false);
        super.setTitle("2D Content Adressable Network");
        super.setLocationRelativeTo(null);
    }

    public class Canvas extends JComponent {
        public Peer clickedPeer = null;

        public Canvas(int height, int width) {
            super();
            super.setSize(height, width);
            super.setPreferredSize(new Dimension(height, width));

            // this.addMouseListener(new MouseAdapter() {
            // public void mouseClicked(MouseEvent e) {
            // Peer clickedPeer = Canvas.this.getClicked(e.getX(), e.getY());
            // System.out.println("clicked in x=" + e.getX() + ", y= " + e.getY());
            // /* Right click */
            // if (e.getButton() == MouseEvent.BUTTON3) {
            // Canvas.this.zoneClicked = clickedZone;
            // System.out.println(clickedZone);
            // Canvas.this.repaint();
            // } else if (e.getButton() == MouseEvent.BUTTON1) {
            // /* Left click */
            // if (clickedZone != null) {
            // if (GraphicalUserInterface.this.action == GraphicalUserInterface.Action.SPLIT) {
            // Zone newZone = new Zone();
            // GraphicalUserInterface.this.zones.add(newZone);
            // newZone.join(clickedZone);
            // } else {
            // if (GraphicalUserInterface.this.zones.size() == 1) {
            // JOptionPane.showMessageDialog(GraphicalUserInterface.this,
            // "You cannot merge when there is only one peer !", "Warning",
            // JOptionPane.WARNING_MESSAGE);
            // } else {
            // clickedZone.leave();
            // GraphicalUserInterface.this.zones.remove(clickedZone);
            // }
            // }
            // } else {
            // JOptionPane.showMessageDialog(GraphicalUserInterface.this,
            // "Please clic on a peer !", "Warning", JOptionPane.WARNING_MESSAGE);
            // }
            // }
            // Canvas.this.repaint();
            // }
            // });
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (Peer peer : ContentAddressableNetworkGUI.this.peerLauncher.getRemotePeers()) {
                g2d.setColor(this.getRandomColor());
                Zone zone = ((CANOverlay) peer.getStructuredOverlay()).getZone();

                int xMin = (int) (Double.parseDouble(zone.getCoordinateMin(0).getValue()) * ContentAddressableNetworkGUI.this.WIDTH);
                int yMin = (int) (Double.parseDouble(zone.getCoordinateMin(1).getValue()) * ContentAddressableNetworkGUI.this.HEIGHT);
                ;
                int xMax = (int) (Double.parseDouble(zone.getCoordinateMax(0).getValue()) * ContentAddressableNetworkGUI.this.WIDTH);
                ;
                int yMax = (int) (Double.parseDouble(zone.getCoordinateMax(1).getValue()) * ContentAddressableNetworkGUI.this.HEIGHT);
                ;

                g2d.fillRect(xMin, yMin, xMax - xMin, yMax - yMin);
            }

            /**
             * if (this.zoneClicked != null) { g2d.setColor(Color.black);
             * g2d.drawLine(this.zoneClicked.xMin, this.zoneClicked.yMin, this.zoneClicked.xMax,
             * this.zoneClicked.yMax);
             * 
             * for (int i = 0; i < 2; i++) { for (int j = 0; j < 2; j++) { for (Zone zone :
             * this.zoneClicked.neighbors[i][j]) { Random rand = new Random(); g2d.drawString("N#" +
             * GraphicalUserInterface.this.zones.indexOf(zone) + " [" + i + "][" + j + "]",
             * zone.xMin - 25 + (zone.xMax - zone.xMin) / 2, zone.yMin + 5 + rand.nextInt(20) +
             * (zone.yMax - zone.yMin) / 2);
             * 
             * } } } this.zoneClicked = null; }
             **/

        }

        public Peer getClicked(int x, int y) {
            BigDecimal bigX = new BigDecimal(x);
            bigX = bigX.divide(new BigDecimal(ContentAddressableNetworkGUI.this.WIDTH));
            BigDecimal bigY = new BigDecimal(y);
            bigY = bigY.divide(new BigDecimal(ContentAddressableNetworkGUI.this.HEIGHT));

            for (Peer peer : ContentAddressableNetworkGUI.this.peerLauncher.getRemotePeers()) {
                if (((CANOverlay) peer.getStructuredOverlay()).contains(new Coordinate[] {
                        new Coordinate("" + bigX), new Coordinate("" + bigY) })) {
                    return peer;
                }
            }
            return null;
        }

        public Color getRandomColor() {
            Random rand = new Random();
            int r = rand.nextInt(256);
            int v = rand.nextInt(256);
            int b = rand.nextInt(256);

            if (r + v + b < 420) {
                return this.getRandomColor();
            }

            return new Color(r, v, b);
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        this.repaint();
    }
}
