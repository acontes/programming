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
package org.objectweb.proactive.ic2d.controller;

import java.awt.event.ActionListener;

import org.objectweb.proactive.ic2d.data.AbstractDataObject;
import org.objectweb.proactive.ic2d.gui.monitoring.AbstractDataObjectPanel;

public abstract class AbstractDataController implements ActionListener {

	// The model
	AbstractDataObject model;
	
	// The view
	AbstractDataObjectPanel view;
	
	
	//
    // -- PUBLICS METHODS -----------------------------------------------
    //
	
	/**
	 * Set to controller a model
	 * @param model
	 */
	public void setModel(AbstractDataObject model){
		this.model = model;
	}
	
	/**
	 * Set to controller a view
	 * @param view
	 */
	public void setView(AbstractDataObjectPanel view){
		this.view = view;
	}
	
	/**
	 * Returns the model
	 * @return the model
	 */
	public AbstractDataObject getModel(){
		return this.model;
	}
}
