package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu;

import java.awt.MenuItem;
import java.awt.PopupMenu;

import javax.media.j3d.Canvas3D;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractFigure3D;

public class HostMenu extends PopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4493764569414539932L;
	private AbstractFigure3D figure;
	private Canvas3D canvas;
	
	public HostMenu(final Canvas3D canvas, AbstractFigure3D figure) {
		this.figure = figure;
		this.canvas = canvas;
		initComponents();
		this.canvas.add(this);
		
	}

	private void initComponents() {
		this.setLabel("Host");
		MenuItem menuItem = new MenuItem("Refresh");
		menuItem.addActionListener(new ViewActionListener(canvas, figure, this, MenuAction.HOST_REFRESH));
		this.add(menuItem);
		menuItem = new MenuItem("Stop Monitoring");
		menuItem.addActionListener(new ViewActionListener(canvas, figure, this, MenuAction.HOST_STOP_MONITORING));
		this.add(menuItem);
		menuItem = new MenuItem("View with chartit");
		menuItem.addActionListener(new ViewActionListener(canvas, figure, this, MenuAction.HOST_CHARTIT));
		this.add(menuItem);
	}
}
