package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu;

import java.awt.MenuItem;
import java.awt.PopupMenu;

import javax.media.j3d.Canvas3D;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;

public class RuntimeMenu extends PopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3383287882736193079L;
	
	private AbstractFigure3D figure;
	private Canvas3D canvas;
	
	public RuntimeMenu(final Canvas3D canvas, AbstractFigure3D figure) {
		this.figure = figure;
		this.canvas = canvas;
		initComponents();
		this.canvas.add(this);
		
	}

	private void initComponents() {
		this.setLabel("Runtime");
		MenuItem menuItem = new MenuItem("Refresh");
		menuItem.addActionListener(new ViewActionListener(canvas, figure, this, MenuAction.RUNTIME_REFRESH));
		this.add(menuItem);
		menuItem = new MenuItem("Stop Monitoring");
		menuItem.addActionListener(new ViewActionListener(canvas, figure, this, MenuAction.RUNTIME_STOP_MONITORING));
		this.add(menuItem);
		menuItem = new MenuItem("Kill");
		menuItem.addActionListener(new ViewActionListener(canvas, figure, this, MenuAction.RUNTIME_KILL));
		this.add(menuItem);
	}
}

