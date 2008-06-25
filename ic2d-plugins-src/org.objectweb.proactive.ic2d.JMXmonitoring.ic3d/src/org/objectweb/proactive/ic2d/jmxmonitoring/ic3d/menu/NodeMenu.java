package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu;

import java.awt.MenuItem;
import java.awt.PopupMenu;

import javax.media.j3d.Canvas3D;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;

public class NodeMenu extends PopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6808568549484096273L;
	private AbstractFigure3D figure;
	private Canvas3D canvas;
	
	public NodeMenu(final Canvas3D canvas, AbstractFigure3D figure) {
		this.figure = figure;
		this.canvas = canvas;
		initComponents();
		this.canvas.add(this);
		
	}

	private void initComponents() {
		this.setLabel("Node");
		MenuItem menuItem = new MenuItem("Refresh");
		menuItem.addActionListener(new ViewActionListener(canvas, figure, this, MenuAction.NODE_REFRESH));
		this.add(menuItem);
		menuItem = new MenuItem("Stop Monitoring");
		menuItem.addActionListener(new ViewActionListener(canvas, figure, this, MenuAction.NODE_STOP_MONITORING));
		this.add(menuItem);
	}
}