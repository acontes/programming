package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu;

import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.media.j3d.Canvas3D;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;

public class ViewActionListener implements ActionListener {
	
	Canvas3D canvas;
	AbstractFigure3D figure;
	PopupMenu popup;
	MenuAction action;
	
	public ViewActionListener(Canvas3D canvas, AbstractFigure3D figure, PopupMenu popup, MenuAction action) {
		this.canvas = canvas;
		this.figure = figure;
		this.popup  = popup;
		this.action = action;
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		canvas.remove(popup);
		figure.notifyObservers(action);
	}

}
