package org.objectweb.proactive.examples.structuredp2p.can;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.objectweb.proactive.core.util.ProActiveRandom;
import org.objectweb.proactive.extensions.structuredp2p.core.Peer;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.CANOverlay;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Coordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.LexicographicCoordinate;
import org.objectweb.proactive.extensions.structuredp2p.core.overlay.can.Zone;


@SuppressWarnings("serial")
public class ContentAddressableNetworkGUI extends JFrame implements Observer {

    private JComponent area;
    private PeerLauncher peerLauncher;
    private int HEIGHT = 500;
    private int WIDTH = 500;

    private HashMap<Peer, Color> peersColor = new HashMap<Peer, Color>();

    public ContentAddressableNetworkGUI(PeerLauncher peerLauncher) {
        this.peerLauncher = peerLauncher;
        this.createAndShowGUI();
    }

    public void createAndShowGUI() {
        this.area = new Canvas(this.WIDTH, this.HEIGHT);

        Container contentPane = super.getContentPane();

        contentPane.add(this.area, BorderLayout.CENTER);

        super.setSize(this.WIDTH, this.HEIGHT + 22);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        super.setResizable(true);
        super.setTitle("2D Content Adressable Network");
        super.setLocationRelativeTo(null);
    }

    public class Canvas extends JComponent {
        public Peer clickedPeer = null;

        public Canvas(int height, int width) {
            super();
            super.setSize(height, width);
            super.setPreferredSize(new Dimension(height, width));

            this.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Peer clickedPeer = Canvas.this.getClicked(e.getX(), e.getY());
                    /* Right click */
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        Canvas.this.clickedPeer = clickedPeer;
                        Canvas.this.repaint();
                    } else if (e.getButton() == MouseEvent.BUTTON1) {

                    }
                }
            });
        }

        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (Peer peer : ContentAddressableNetworkGUI.this.peerLauncher.getRemotePeers()) {
                if (ContentAddressableNetworkGUI.this.peersColor.get(peer) != null) {
                    g2d.setColor(ContentAddressableNetworkGUI.this.peersColor.get(peer));
                } else {
                    Color color = this.getRandomColor();
                    ContentAddressableNetworkGUI.this.peersColor.put(peer, color);
                    g2d.setColor(color);
                }
                Zone zone = ((CANOverlay) peer.getStructuredOverlay()).getZone();

                int xMin = (int) (Double.parseDouble(zone.getCoordinateMin(0).getValue()) * ContentAddressableNetworkGUI.this.WIDTH);
                int yMin = (int) (Double.parseDouble(zone.getCoordinateMin(1).getValue()) * ContentAddressableNetworkGUI.this.HEIGHT);

                int xMax = (int) (Double.parseDouble(zone.getCoordinateMax(0).getValue()) * ContentAddressableNetworkGUI.this.WIDTH);
                int yMax = (int) (Double.parseDouble(zone.getCoordinateMax(1).getValue()) * ContentAddressableNetworkGUI.this.HEIGHT);

                g2d.fillRect(xMin, yMin, xMax - xMin, yMax - yMin);
            }

            if (this.clickedPeer != null) {
                System.out.println("clickedPeer = " +
                    ((CANOverlay) this.clickedPeer.getStructuredOverlay()).getZone());
                g2d.setColor(Color.black);

                Zone zone = ((CANOverlay) this.clickedPeer.getStructuredOverlay()).getZone();

                int xMin = (int) (Double.parseDouble(zone.getCoordinateMin(0).getValue()) * ContentAddressableNetworkGUI.this.WIDTH);
                int yMin = (int) (Double.parseDouble(zone.getCoordinateMin(1).getValue()) * ContentAddressableNetworkGUI.this.HEIGHT);

                int xMax = (int) (Double.parseDouble(zone.getCoordinateMax(0).getValue()) * ContentAddressableNetworkGUI.this.WIDTH);
                int yMax = (int) (Double.parseDouble(zone.getCoordinateMax(1).getValue()) * ContentAddressableNetworkGUI.this.HEIGHT);

                g2d.drawLine(xMin, yMin, xMax - xMin, yMax - yMin);

                this.clickedPeer = null;
            }

        }

        public Peer getClicked(int x, int y) {
            BigDecimal bigX = new BigDecimal(x);
            bigX = bigX.divide(new BigDecimal(ContentAddressableNetworkGUI.this.WIDTH));
            BigDecimal bigY = new BigDecimal(y);
            bigY = bigY.divide(new BigDecimal(ContentAddressableNetworkGUI.this.HEIGHT));

            for (Peer peer : ContentAddressableNetworkGUI.this.peerLauncher.getRemotePeers()) {
                if (((CANOverlay) peer.getStructuredOverlay()).contains(new Coordinate[] {
                        new LexicographicCoordinate("" + bigX), new LexicographicCoordinate("" + bigY) })) {
                    return peer;
                }
            }
            return null;
        }

        public Color getRandomColor() {
            int r = ProActiveRandom.nextInt(256);
            int v = ProActiveRandom.nextInt(256);
            int b = ProActiveRandom.nextInt(256);

            if (r + v + b < 350) {
                return this.getRandomColor();
            }

            return new Color(r, v, b);
        }
    }

    public synchronized void update(Observable o, Object arg) {
        this.repaint();
    }
}
