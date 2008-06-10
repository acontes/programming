/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import javax.media.j3d.Canvas3D;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.proearth.EarthGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;


/**
 * @author vjuresch
 *
 */
public class Test extends JFrame {
    AbstractFigure3D grid;
    /**
     *
     */
    private static final long serialVersionUID = 6327098576785371018L;

    /**
     * @param args
     */
    public Test() {
        // testMany();
        // testFew();
        testRandom();
        //testPicking();
    }

    private void testPicking() {
        setUpWindow();
        Host3D pr = new Host3D("rmi://predadab.inria.fr");
        Host3D pu = new Host3D("rmi://puravida.inria.fr");
        Host3D ch = new Host3D("rmi://cheypa.inria.fr");

        grid.addSubFigure("predadab", pr);
        grid.addSubFigure("puravida", pu);
        grid.addSubFigure("cheypa", ch);

    }

    private void testRandom() {
        ArrayList<AbstractActiveObject3D> aos = new ArrayList<AbstractActiveObject3D>();
        setUpWindow();

        for (int i = 1; i < 30; i++)
            grid.addSubFigure("Host : " + Integer.valueOf(i), new Host3D("Host : " + Integer.valueOf(i)));
        int size = grid.getSubFigures().size();
        int pick;
        double chance;
        Host3D picked;
        int cati = 0;
        while (cati < 50) {
            pick = (int) Math.round(Math.random() * (size - 1));
            //pick a host
            picked = (Host3D) grid.getSubFigures().values().toArray()[pick];
            chance = Math.random();
            //add a runtime with a probability
            if (chance > 0.351) {
                picked.addSubFigure(new Double(chance).toString(), new Runtime3D(new Double(chance)
                        .toString()));
                chance = Math.random();
                //iterate over runtiems and add a node with a probability
                for (String key : picked.getSubFigures().keySet())
                    if (chance > 0.01) {
                        Runtime3D run = (Runtime3D) picked.getSubFigure(key);
                        run.addSubFigure(new Double(chance).toString(), new Node3D(new Double(chance)
                                .toString()));
                        chance = Math.random();
                        //iterate over nodes and  add a ao with a probability
                        for (String keyNode : run.getSubFigures().keySet()) {
                            if (chance > 0.01) {
                                ActiveObject3D obiect = new ActiveObject3D(new Double(chance).toString());
                                Node3D nod = (Node3D) run.getSubFigures().get(keyNode);
                                nod.addSubFigure(new Double(chance).toString(), obiect);
                                aos.add(obiect);
                            }
                        }
                    }
            }
            cati++;
        }

        System.out.println("All generated:" + cati);
        System.out.println("Starting communication between :" + aos.size());
        final int aoNumber = aos.size();

        final ArrayList<AbstractActiveObject3D> obiecte = new ArrayList<AbstractActiveObject3D>(aos);
        while (true) {
            new Thread(new Runnable() {
                public void run() {
                    int a;
                    int b;
                    a = (int) Math.round(Math.random() * (aoNumber - 1));
                    b = (int) Math.round(Math.random() * (aoNumber - 1));

                    //                        grid.drawCommunication(UUID.randomUUID().toString(),
                    //                            new Double(a).toString(),
                    //                           Math.round(Math.random()*5), obiecte.get(a),
                    //                            obiecte.get(b));

                    //                      grid.drawSphereArrow(new Double(Math.random()).toString(),
                    //                      new Double(a).toString(),
                    //                       5000, new Vector2d(Math.random()*2*Math.PI,Math.random()*Math.PI -Math.PI/2),
                    //                       new Vector2d(Math.random()*2*Math.PI,Math.random()*Math.PI - Math.PI/2 ));
                    //                        
                }
            }).start();
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //            grid.drawSphereArrow(new Double(Math.random()).toString(),
            //                    new Double(10000).toString(),
            //                     50000, new Vector2d(0,0),
            //                     new Vector2d(Math.PI,Math.PI/2));

        }
    }

    private void testMany() {
        setUpWindow();

        for (int i = 1; i < 50; i++) {
            grid.addSubFigure("Host :" + Integer.valueOf(i), new Host3D("rmi://puravida.inria.fr"));
            for (int k = 1; k < 3; k++) {
                grid.getSubFigure("Host :" + Integer.valueOf(i)).addSubFigure("cheie" + Integer.valueOf(k),
                        new Runtime3D(Integer.valueOf(k).toString()));
                for (int j = 1; j < 3; j++) {
                    grid.getSubFigure("Host :" + new Integer(i).toString()).getSubFigure(
                            "cheie" + new Integer(k).toString()).addSubFigure("node:" + j,
                            new Node3D("nodul"));
                }
            }
        }
    }

    private void setUpWindow() {
        GridUniverse universe = new GridUniverse();
        grid = new EarthGrid3D("");
        //   grid = new Grid3D();
        universe.addGrid((AbstractGrid3D) grid);
        //three views
        Canvas3D viewOne = universe.newView();
        Canvas3D viewTwo = universe.newView();
        Canvas3D viewThree = universe.newView();

        //create layout  65/35
        GridBagConstraints constr = new GridBagConstraints();
        GridBagLayout mainLay = new GridBagLayout();
        JPanel main = new JPanel(mainLay);

        //create layout in the 35 side with three rows
        JPanel side = new JPanel(new GridLayout(2, 1, 1, 1));
        JPanel left = new JPanel(new GridLayout(1, 1, 1, 1));

        left.setBackground(new Color(0, 0, 0));
        side.setBackground(new Color(0, 0, 0));
        main.setBackground(new Color(0, 0, 0));

        //add the first view on the left and all three on the right
        constr.insets = new Insets(1, 1, 1, 1);

        constr.fill = GridBagConstraints.BOTH;
        constr.gridwidth = 2;
        constr.weightx = 1;
        constr.weighty = 1;
        main.add(left, constr);

        constr.gridwidth = 2;
        constr.weightx = 0.3;
        constr.weighty = 1;

        main.add(side, constr);

        //add views in the side pane
        left.add(viewOne);

        side.add(viewTwo);
        side.add(viewThree);
        add(main);

        pack();
        setVisible(true);
        setSize(800, 600);

        viewOne.addMouseListener(new TemporaryMouseListener(side, left));

        viewTwo.addMouseListener(new TemporaryMouseListener(side, left));
        viewThree.addMouseListener(new TemporaryMouseListener(side, left));
    }

    private void testFew() {
        setUpWindow();

        Host3D pr = new Host3D("rmi://predadab.inria.fr");
        Host3D pu = new Host3D("rmi://puravida.inria.fr");
        Host3D ch = new Host3D("rmi://cheypa.inria.fr");

        grid.addSubFigure("predadab", pr);
        grid.addSubFigure("puravida", pu);
        grid.addSubFigure("cheypa", ch);

        Runtime3D r1 = new Runtime3D("r123");
        Runtime3D r2 = new Runtime3D("r1234");
        Runtime3D r3 = new Runtime3D("r123456");
        Runtime3D r4 = new Runtime3D("r12345678");
        Runtime3D r5 = new Runtime3D("r1234567890");
        Runtime3D r6 = new Runtime3D("r123456789011");

        Node3D n1 = new Node3D("nod12345");
        Node3D n2 = new Node3D("nod1234567");
        Node3D n3 = new Node3D("nod12345678");
        Node3D n4 = new Node3D("nod123456789");
        Node3D n5 = new Node3D("nod1234567890");
        Node3D n6 = new Node3D("nod6");
        Node3D n7 = new Node3D("nod7");
        Node3D n8 = new Node3D("nod8");
        Node3D n9 = new Node3D("nod9");

        final ActiveObject3D o1 = new ActiveObject3D("01234567890");
        final ActiveObject3D o2 = new ActiveObject3D("022345678");
        final ActiveObject3D o3 = new ActiveObject3D("032345678");
        final ActiveObject3D o4 = new ActiveObject3D("042345678");
        final ActiveObject3D o5 = new ActiveObject3D("o5234567823456789");
        final ActiveObject3D o6 = new ActiveObject3D("o6234567892345678923456789");
        final ActiveObject3D o7 = new ActiveObject3D("o72345678923456789");
        final ActiveObject3D o8 = new ActiveObject3D("o8234567892345678923456789");
        final ActiveObject3D o9 = new ActiveObject3D("o2345678923456789234567899");
        final ActiveObject3D o10 = new ActiveObject3D("o102345678923456789");

        //add runtimes
        pr.addSubFigure("runtime1", r1);
        pr.addSubFigure("runtime2", r2);
        pr.addSubFigure("runtime3", r3);

        pu.addSubFigure("runtime1", r4);
        pu.addSubFigure("runtime2", r5);
        ch.addSubFigure("runtime3", r6);

        r1.addSubFigure("nod1", n1);

        r2.addSubFigure("nod2", n2);
        r2.addSubFigure("nod3", n3);

        r3.addSubFigure("nod4", n4);
        r3.addSubFigure("nod5", n5);
        r3.addSubFigure("nod6", n6);

        r4.addSubFigure("nod7", n7);
        r4.addSubFigure("nod8", n8);
        r5.addSubFigure("nod9", n9);

        n1.addSubFigure("o1", o1);
        n1.addSubFigure("o2", o2);
        n1.addSubFigure("o3", o3);
        n2.addSubFigure("o4", o4);
        n2.addSubFigure("o5", o5);
        n3.addSubFigure("o6", o6);
        n9.addSubFigure("o7", o7);

        n1.addSubFigure("o8", o8);
        n1.addSubFigure("o9", o9);
        n1.addSubFigure("o10", o10);

        //     n1.removeSubFigure("o10");
        o1.setState(State.SERVING_REQUEST);
        o2.setState(State.MIGRATING);
        o3.setState(State.WAITING_FOR_REQUEST);
        o4.setState(State.UNKNOWN);
        try {
            System.in.read();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        while (true) {
            new Thread(new Runnable() {
                public void run() {
                    int a;
                    int b;
                    ActiveObject3D start;
                    ActiveObject3D stop;
                    a = (int) Math.round(Math.random() * 7);
                    b = (int) Math.round(Math.random() * 7);

                    switch (a) {
                        case 1:
                            start = o1;
                            break;
                        case 2:
                            start = o2;
                            break;
                        case 3:
                            start = o3;
                            break;
                        case 4:
                            start = o4;
                            break;
                        case 5:
                            start = o5;
                            break;
                        case 6:
                            start = o6;
                            break;
                        case 7:
                            start = o7;
                            break;
                        default:
                            start = o1;
                            break;
                    }

                    switch (b) {
                        case 1:
                            stop = o1;
                            break;
                        case 2:
                            stop = o2;
                            break;
                        case 3:
                            stop = o3;
                            break;
                        case 4:
                            stop = o4;
                            break;
                        case 5:
                            stop = o5;
                            break;
                        case 6:
                            stop = o6;
                            break;
                        case 7:
                            stop = o7;
                            break;
                        default:
                            stop = o2;
                            break;
                    }

                    UUID cheie = UUID.randomUUID();
                    grid.drawCommunication(cheie.toString(), "", 2, start, stop);
                }
            }).start();
            //                    try {
            //						Thread.sleep(3);
            //					} catch (InterruptedException e) {
            //						// TODO Auto-generated catch block
            //						e.printStackTrace();
            //					}

            o1.setQueueSize((int) Math.round(Math.random() * 20 + 1));
            o2.setQueueSize((int) Math.round(Math.random() * 20 + 1));
            o3.setQueueSize((int) Math.round(Math.random() * 20 + 1));
            o4.setQueueSize((int) Math.round(Math.random() * 20 + 1));
            o5.setQueueSize((int) Math.round(Math.random() * 20 + 1));
            o6.setQueueSize((int) Math.round(Math.random() * 20 + 1));
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //		grid.drawArrow("puravida",
        //					"runtime2", "nod9",
        //					"o7", 
        //					"predadab", "runtime1", 
        //					"nod1", "o1");
    }

    public static void main(String[] args) {
        Test test = new Test();
        System.out.println("running test...");
    }
}
