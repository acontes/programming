/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d;


import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.runtime.RuntimeFactory;
import org.objectweb.proactive.ic2d.controller.WorldController;
import org.objectweb.proactive.ic2d.data.WorldObject;
import org.objectweb.proactive.ic2d.gui.IC2DFrame;
import org.objectweb.proactive.ic2d.gui.monitoring.WorldPanel;
import org.objectweb.proactive.ic2d.logger.IC2DLoggers;
import org.objectweb.proactive.ic2d.logger.gui.MessagePanel;

/**
 * <p>
 * This class is the main entry to the application IC2D allowing to start it with
 * a new JVM.
 * </p><p>
 * This class has a main method and can be used directly from the java command.<br>
 * &nbsp;&nbsp;&nbsp;java org.objectweb.proactive.ic2d.IC2D
 * </p>
 *
 * @author  ProActive Team
 * @version 1.0,  2002/03/21
 * @since   ProActive 0.9
 *
 */
public class IC2D {
	
	/** List of AbstractWorldController*/
	private static List worldControllers;
	
	/**
	 * Returns the list of WorldControllers
	 * @return the liste of WorldControllers
	 */
	public static List getWorldControllerList(){
		return worldControllers;
	}
	
	/**
	 * The starting point
	 * @param args
	 */
	public static void main(String[] args) {
		
		//TODO get the default ProActiveRuntime associated with the local JVM
		try {
			RuntimeFactory.getDefaultRuntime();
		} catch (ProActiveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// The main frame
		IC2DFrame frame = new IC2DFrame();
		
		// The main panel
		JPanel ic2dPanel = new JPanel();
		ic2dPanel.setLayout(new BorderLayout());
		
		// The monitoring panel
		JPanel monitoringPanel = new JPanel();
		monitoringPanel.setLayout(new BorderLayout());
		
		/*----- Model View Controller -----*/
		
		// The model
		WorldObject model = new WorldObject();

		// Controller
		WorldController controller = new WorldController();
		
		IC2D.worldControllers = new ArrayList();
		IC2D.worldControllers.add(controller);
				
		// The view
		WorldPanel view = new WorldPanel(model);
		view.setController(controller);
		
		controller.setModel(model);
		controller.setView(view);
		
		JScrollPane scrollableWorldPanel = new JScrollPane(view,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		monitoringPanel.add(scrollableWorldPanel, BorderLayout.CENTER);
		
		/*---------------------------------*/
		
		ic2dPanel.add(monitoringPanel, BorderLayout.CENTER);
		
		// Message panel (log)
		MessagePanel messagePanel = new MessagePanel("Messages");
		
		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                false, monitoringPanel, messagePanel);
		
		splitPanel.setDividerLocation(IC2DFrame.getDefaultHeight() - 200);
        splitPanel.setOneTouchExpandable(true);
        ic2dPanel.add(splitPanel, BorderLayout.CENTER);
		
		frame.add(ic2dPanel);
		
		frame.setVisible(true);
		
		IC2DLoggers.getInstance().log("IC2D ready !");
	}
}
