package org.objectweb.proactive.ic2d.monitoring.editparts;

import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;

public class WorldEditPart extends AbstractIC2DEditPart {

	
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
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());
		layer.setBorder(new LineBorder(1));
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
	 * Fills the view with data extracted from the model object 
	 * associated with the EditPart.
	 * This method will be called just after the creation of 
	 * the figure, and may also be called in response to 
	 * notifications from the model. 
	 */
/*	protected void refreshVisuals(){ 
		//TODO
	}
	*/
	
	/**
	 * Creates the initial EditPolicies and/or reserves slots for dynamic ones.
	 */
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}
	
}
