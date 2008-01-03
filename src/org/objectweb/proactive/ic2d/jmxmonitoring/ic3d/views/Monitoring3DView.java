package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

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
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Grid3DController;

public class Monitoring3DView extends ViewPart {
	public static final String ID = "org.objectweb.proactive.ic2d.jmxmonitoring.view.Legend";
	/** The World */
	private WorldObject world;
	private String title;

	public Monitoring3DView() {
		super();
		world = new WorldObject();
		title = world.getName();
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());

		// **********ADD TOOLBAR BUTTONS********************
		IToolBarManager toolBarManager = getViewSite().getActionBars()
				.getToolBarManager();

		// Adds "Monitor a new Host" action to the view's toolbar
		NewHostAction toolBarNewHost = new NewHostAction(parent.getDisplay(),
				world);
		toolBarManager.add(toolBarNewHost);

		// Adds "Set depth" action to the view's toolbar
		SetDepthAction toolBarSetDepth = new SetDepthAction(
				parent.getDisplay(), world);
		toolBarManager.add(toolBarSetDepth);

		toolBarManager.add(new Separator());

		// Adds "Set Time to refresh" action to the view's toolbar
		SetTTRAction toolBarTTR = new SetTTRAction(parent.getDisplay(), world
				.getMonitorThread());
		toolBarManager.add(toolBarTTR);

		// Adds enable/disable monitoring action to the view's toolbar
		EnableDisableMonitoringAction toolBarEnableDisableMonitoring = new EnableDisableMonitoringAction(
				world);
		toolBarManager.add(toolBarEnableDisableMonitoring);

		toolBarManager.add(new Separator());

		// ******************************

		// create swt container:
		Composite SWT_AWT_container = new Composite(parent, SWT.EMBEDDED);

		// set bounds (here showing bounds to cover parent's area)
		final Rectangle sh3dbnds = parent.getBounds();
		sh3dbnds.x = sh3dbnds.y = 0;
		SWT_AWT_container.setBounds(sh3dbnds);

		// ----------------
		Frame awt = SWT_AWT.new_Frame(SWT_AWT_container);

		// -----------------
		final Rectangle bounds = SWT_AWT_container.getBounds();
		awt.setBounds(0, 0, bounds.width, bounds.height);

		awt.setLayout(new BorderLayout());

		// create your scene:
		// awt.add(newGrid());
		// TODO hacky
		// *********** ADDED FOR MULTIPLE VIEWS *********
		// uncomment above to go back to the old view

		GridUniverse universe = new GridUniverse();
		Grid3D g = new Grid3D();
		universe.addGrid(g);

		// create the grid controller,
		// the grid controller will be responsible with creating/removing
		// the host controllers and so on
		Grid3DController controller = new Grid3DController(world, g, null);

		// three views
		Canvas3D viewOne = universe.newView();
		Canvas3D viewTwo = universe.newView();
		Canvas3D viewThree = universe.newView();

		// create layout 65/35
		GridBagConstraints constr = new GridBagConstraints();
		GridBagLayout mainLay = new GridBagLayout();
		JPanel main = new JPanel(mainLay);

		// create layout in the 35 side with three rows
		JPanel side = new JPanel(new GridLayout(2, 1, 1, 1));
		JPanel left = new JPanel(new GridLayout(1, 1, 1, 1));

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

		viewOne.addMouseListener(new TemporaryMouseListener(side, left));

		viewTwo.addMouseListener(new TemporaryMouseListener(side, left));
		viewThree.addMouseListener(new TemporaryMouseListener(side, left));
	}

	private Canvas3D newGrid() {
		GridUniverse grid = new GridUniverse();
		Grid3D g = new Grid3D();
		grid.addGrid(g);

		// get the world object,
		// Set<String> modelNames = ModelRecorder.getInstance().getNames();
		// WorldObject root = ModelRecorder.getInstance()
		// .getModel(modelNames.iterator().next());
		Grid3DController controller = new Grid3DController(world, g, null);

		// Root3dController c = new Root3dController(g);
		return grid.newView();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
	}
}
