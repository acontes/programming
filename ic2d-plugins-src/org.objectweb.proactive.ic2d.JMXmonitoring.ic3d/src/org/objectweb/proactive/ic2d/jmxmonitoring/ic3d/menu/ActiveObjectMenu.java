package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu;

import java.awt.MenuItem;
import java.awt.PopupMenu;

import javax.media.j3d.Canvas3D;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;

public class ActiveObjectMenu extends PopupMenu {

	/**
	 * 
	 */
	private static final long serialVersionUID = 362349558668444898L;

	private AbstractFigure3D figure;
	private Canvas3D canvas;
	
	public ActiveObjectMenu(final Canvas3D canvas, AbstractFigure3D figure) {
		this.figure = figure;
		this.canvas = canvas;
		initComponents();
		this.canvas.add(this);
		
	}

	private void initComponents() {
		this.setLabel("Active Object");
		MenuItem menuItem = new MenuItem("View with ChartIt");
		menuItem.addActionListener(new ViewActionListener(canvas, figure, this, MenuAction.AO_CHARTIT));
		this.add(menuItem);
	}
}
