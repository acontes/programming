package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d;

/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.media.j3d.Canvas3D;
import javax.swing.JPanel;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.jmxmonitoring.action.EnableDisableMonitoringAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.action.NewHostAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.action.SetDepthAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.action.SetTTRAction;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.FlatCameraBehavior;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.OrbitalCameraBehavior;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.detailed.EarthGrid3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.detailed.Grid3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.load.LoadGrid3DController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.CustomUniverse;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Grid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.Universe3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring.LoadGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.proearth.EarthGrid3D;


public class Monitoring3DView extends ViewPart {
    public static final String ID = "org.objectweb.proactive.ic2d.jmxmonitoring.view.Legend";
    /** The World */
    private final WorldObject world;
    private final String title;

    public Monitoring3DView() {
        super();
        this.world = new WorldObject();
        this.title = this.world.getName();
    }

    @Override
    public void createPartControl(final Composite parent) {
        parent.setLayout(new FillLayout());

        // **********ADD TOOLBAR BUTTONS********************
        final IToolBarManager toolBarManager = this.getViewSite().getActionBars().getToolBarManager();

        // Adds "Monitor a new Host" action to the view's toolbar
        final NewHostAction toolBarNewHost = new NewHostAction(parent.getDisplay(), this.world);
        toolBarManager.add(toolBarNewHost);

        // Adds "Set depth" action to the view's toolbar
        final SetDepthAction toolBarSetDepth = new SetDepthAction(parent.getDisplay(), this.world);
        toolBarManager.add(toolBarSetDepth);

        toolBarManager.add(new Separator());

        // Adds "Set Time to refresh" action to the view's toolbar
        final SetTTRAction toolBarTTR = new SetTTRAction(parent.getDisplay(), this.world.getMonitorThread());
        toolBarManager.add(toolBarTTR);

        // Adds enable/disable monitoring action to the view's toolbar
        final EnableDisableMonitoringAction toolBarEnableDisableMonitoring = new EnableDisableMonitoringAction(
            this.world);
        toolBarManager.add(toolBarEnableDisableMonitoring);

        toolBarManager.add(new Separator());

        // ******************************

        // create swt container:
        final Composite SWT_AWT_container = new Composite(parent, SWT.EMBEDDED);

        // set bounds (here showing bounds to cover parent's area)
        final Rectangle sh3dbnds = parent.getBounds();
        sh3dbnds.x = sh3dbnds.y = 0;
        SWT_AWT_container.setBounds(sh3dbnds);

        // ----------------
        final Frame awt = SWT_AWT.new_Frame(SWT_AWT_container);

        // -----------------
        final Rectangle bounds = SWT_AWT_container.getBounds();
        awt.setBounds(0, 0, bounds.width, bounds.height);

        awt.setLayout(new BorderLayout());

        // create your scene:
        // awt.add(newGrid());
        // TODO hacky
        // *********** ADDED FOR MULTIPLE VIEWS *********
        // uncomment above to go back to the old view

        final CustomUniverse universe = new CustomUniverse();
        final Universe3D universe3D = new Universe3D();
        universe.addGrid(universe3D.getRootBranch());
        
        // Creates the controllers
        final Grid3DController gcontroller = new Grid3DController(this.world, universe3D, null);
        final LoadGrid3DController glcontroller = new LoadGrid3DController(this.world, universe3D, null);
        final EarthGrid3DController egcontroller = new EarthGrid3DController(this.world, universe3D, null);
        
        // Fetch Grids
        final Grid3D detailedGrid = (Grid3D)gcontroller.getFigure();
        detailedGrid.setTranslation(new Vector3d(0, 0.5, 0));
        
        final LoadGrid3D loadGrid = (LoadGrid3D)glcontroller.getFigure();
        loadGrid.setTranslation(new Vector3d(0, 500, 0));
        
        final EarthGrid3D earthGrid = (EarthGrid3D)egcontroller.getFigure();
        earthGrid.setTranslation(new Vector3d(0, -500, 0));
        
        // three views
        final Canvas3D viewOne = universe.newView("one", new Point3d(0, 0.5, 0), detailedGrid.getRootBranch(),
                new FlatCameraBehavior());
        
        final Canvas3D viewTwo = universe.newView("two", new Point3d(0, 500, 0), loadGrid
                .getRootBranch(), new FlatCameraBehavior());
        
        final Canvas3D viewThree = universe.newView("three", new Point3d(0, -500, 0), earthGrid.getRootBranch(),
                new OrbitalCameraBehavior());

        // create layout 65/35
        final GridBagConstraints constr = new GridBagConstraints();
        final GridBagLayout mainLay = new GridBagLayout();
        final JPanel main = new JPanel(mainLay);

        // create layout in the 35 side with three rows
        final JPanel side = new JPanel(new GridLayout(2, 1, 1, 1));
        final JPanel left = new JPanel(new GridLayout(1, 1, 1, 1));

        left.setBackground(new Color(0, 0, 0));
        side.setBackground(new Color(0, 0, 0));
        main.setBackground(new Color(0, 0, 0));

        // add the first view on the left and all three on the right
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

        // add views in the side pane
        left.add(viewOne);

        side.add(viewTwo);
        side.add(viewThree);
        awt.add(main);

        awt.pack();
        awt.setVisible(true);
        // awt.setSize(800, 600);

    }

    private Canvas3D newGrid() {
        final CustomUniverse grid = new CustomUniverse();
        final Grid3D g = new Grid3D();
        grid.addGrid(g.getRootBranch());

        // get the world object,
        // Set<String> modelNames = ModelRecorder.getInstance().getNames();
        // WorldObject root = ModelRecorder.getInstance()
        // .getModel(modelNames.iterator().next());
        final Grid3DController controller = new Grid3DController(this.world, g, null);

        // Root3dController c = new Root3dController(g);
        return grid.newView("11111");
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }
}
