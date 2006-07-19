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
package org.objectweb.proactive.ic2d.monitoring.editparts;

import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;

public class WorldEditPart extends AbstractIC2DEditPart {


	private FreeformLayer layer;

	//
	// -- CONSTRUCTORS -----------------------------------------------
	//

	public WorldEditPart(WorldObject model) {
		super(model);
	}

	//
	// -- PUBLICS METHODS -----------------------------------------------
	//

	/**
	 * Convert the result of EditPart.getModel()
	 * to WorldObject (the real type of the model).
	 * @return the casted model
	 */
	public WorldObject getCastedModel() {
		return (WorldObject)getModel();
	}

	public IFigure getContentPane() {
		return layer;
	}

	//
	// -- PROTECTED METHODS -----------------------------------------------
	//

	/**
	 * Returns a new view associated
	 * with the type of model object the
	 * EditPart is associated with. So here, it returns a new FreeFormLayer.
	 * @return a new FreeFormLayer view associated with the WorldObject model.
	 */
	protected IFigure createFigure() {
		layer = new FreeformLayer();
		ToolbarLayout layout = new ToolbarLayout(true);
		layout.setSpacing(50);
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		//layout.setStretchMinorAxis(false);
		layer.setLayoutManager(/*new FreeformLayout()*/layout);
		//layer.setBorder(new LineBorder(1));
		return layer;
	}


	/**
	 * Returns a List containing the children model objects.
	 * @return the List of children
	 */
	protected List getModelChildren() {
		return getCastedModel().getMonitoredChildren();
	}

	/**
	 * Creates the initial EditPolicies and/or reserves slots for dynamic ones.
	 */
	protected void createEditPolicies() {
		// TODO Auto-generated method stub

	}

	/*@Override
	public Object getAdapter(Class adapter) {
		if(adapter == MouseWheelHelper.class) {
			return new ViewportMouseWheelHelper(this) { // Classe disponible dans GEF exprès pour cela
				public void handleMouseWheelScrolled(Event event) {
					System.out.println("MouseWheel WorldEditPart");
					super.handleMouseWheelScrolled(event);
				}
			};
		}
		return super.getAdapter(adapter);
	}*/

}
