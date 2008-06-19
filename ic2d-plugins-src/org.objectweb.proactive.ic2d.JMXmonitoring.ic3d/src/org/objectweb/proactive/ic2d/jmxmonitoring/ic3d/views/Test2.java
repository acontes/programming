package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.media.j3d.Canvas3D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.AppearanceBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.OrbitalCameraBehavior;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Grid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Host3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.MonitorGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.MonitorHost3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.proearth.EarthGrid3D;


public class Test2 extends JFrame {
    private static final long serialVersionUID = 6327098576785371018L;

    public Test2() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Test2");
        Test2Features();
    }

    private void Test2Features() {
        basicWindow();
    }

    private void basicWindow() {
        CustomUniverse universe = new CustomUniverse();

        AbstractGrid3D grid;

        /* Set view one */
        grid = new Grid3D();
        grid.setTranslation(new Vector3d(0, 0.5, 0));
        universe.addGrid(grid.getRootBranch());

        Host3D pr = new Host3D("rmi://predabab.inria.fr");
        Host3D pu = new Host3D("rmi://segfault.inria.fr");
        Host3D ch = new Host3D("rmi://cheypa.inria.fr");
        Host3D he = new Host3D("rmi://cheypa.inria.fr");

        grid.addSubFigure("predadab", pr);
        grid.addSubFigure("segfault", pu);
        grid.addSubFigure("cheypa", ch);
        grid.addSubFigure("heli", he);

        Canvas3D viewOne = universe.newView("one", new Point3d(0, 0.5, 0), grid.getRootBranch());

        /* Set view two */
        grid = new MonitorGrid3D();
        grid.setTranslation(new Vector3d(0, -300, 0));
        universe.addGrid(grid.getRootBranch());

        MonitorHost3D mpr = new MonitorHost3D("rmi://predadab.inria.fr");
        MonitorHost3D mpu = new MonitorHost3D("rmi://segfault.inria.fr");
        MonitorHost3D mch = new MonitorHost3D("rmi://cheypa.inria.fr");
        MonitorHost3D mhe = new MonitorHost3D("rmi://cheypa.inria.fr");

        grid.addSubFigure("1", mpr);
        grid.addSubFigure("2", mpu);
        grid.addSubFigure("3", mch);
        grid.addSubFigure("3", mhe);

        mpr.setAppearance(AppearanceBasket.defaultRuntimeAppearance);

        mpr.setLoad(0.5);
        mpu.setLoad(0.3);
        mch.setLoad(0.95);

        Canvas3D viewTwo = universe.newView("two", new Point3d(0, -300, 0), grid.getRootBranch());

        /* Set view three */
        grid = new EarthGrid3D("");
        grid.setTranslation(new Vector3d(0, 300, 0));
        universe.addGrid(grid.getRootBranch());

        pr = new Host3D("rmi://predabab.inria.fr");
        pu = new Host3D("rmi://segfault.inria.fr");
        ch = new Host3D("rmi://cheypa.inria.fr");
        he = new Host3D("rmi://heli.inria.fr");

        grid.addSubFigure("1", pr);
        grid.addSubFigure("2", pu);
        grid.addSubFigure("3", ch);
        grid.addSubFigure("4", he);

        Canvas3D viewThree = universe.newView("three", new Point3d(0, 300, 0), grid.getRootBranch(),
                new OrbitalCameraBehavior());

        //create layout  65/35
        GridBagConstraints constr = new GridBagConstraints();
        GridBagLayout mainLay = new GridBagLayout();
        JPanel main = new JPanel(mainLay);

        //create layout in the 35 side with three rows
        JPanel side = new JPanel(new GridLayout(2, 1, 1, 1));
        JPanel left = new JPanel(new GridLayout(1, 1, 1, 1));

        left.setBackground(new Color(1, 1, 1));
        side.setBackground(new Color(1, 1, 1));
        main.setBackground(new Color(1, 1, 1));

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

        while (true) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mpr.setLoad(Math.random());
            mpu.setLoad(Math.random());
            mch.setLoad(Math.random());
        }
    }

    public static void main(String[] args) {
        System.out.println("running Test2...");
        Test2 Test2 = new Test2();
    }
}