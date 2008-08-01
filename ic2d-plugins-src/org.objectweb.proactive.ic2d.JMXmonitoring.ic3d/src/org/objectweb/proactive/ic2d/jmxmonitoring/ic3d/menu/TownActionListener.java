package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.SiteBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.behavior.CameraBehavior;

public class TownActionListener implements ActionListener {
	
	String town;
	CameraBehavior camera;
	
	public TownActionListener(CameraBehavior camera, String town) {
		this.town = town;
		this.camera = camera;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		camera.setTarget(SiteBasket.getTownLocation(town));
	}
}
