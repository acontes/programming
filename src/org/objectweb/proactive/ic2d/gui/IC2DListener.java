package org.objectweb.proactive.ic2d.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import org.objectweb.proactive.ic2d.gui.monitoring.Legend;

public class IC2DListener extends WindowAdapter implements ActionListener {

	/** The IC2D frame */
	private IC2DFrame frame;
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public IC2DListener(IC2DFrame frame){
		this.frame = frame;
	}
	
	
    //
    // -- PUBLICS METHODS -----------------------------------------------
    //
	
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == frame.getQuitItem())
			System.exit(0);
		else if(source == frame.getRMIItem()){
			HostDialogUtils.openNewRMIHostDialog(frame);
		}
		
		else if(source == frame.getLegendItem()) {
			Legend legend = Legend.getInstance();
	        legend.setVisible(!legend.isVisible());
		}
	}
	
}
