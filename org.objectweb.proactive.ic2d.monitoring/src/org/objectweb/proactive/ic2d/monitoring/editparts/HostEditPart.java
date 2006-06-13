package org.objectweb.proactive.ic2d.monitoring.editparts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.objectweb.proactive.ic2d.monitoring.data.HostObject;
import org.objectweb.proactive.ic2d.monitoring.figures.HostFigure;

public class HostEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener{
	
	
	
	/**
	 * Must return a new view associated
	 * with the type of model object the
	 * EditPart is associated with
	 */
	protected IFigure createFigure() {
		return new HostFigure("Host undefined", 10, 80);
	}
	
	/**
	 * Must fill the view with data extracted
	 * from the model object associated with
	 * the EditPart, it will be called just
	 * after the creation of the figure
	 */
	//public void refreshVisuals(){
	/*HostFigure figure = (HostFigure)getFigure();
	 HostObject model = (HostObject)getModel();
	 figure.setTitle(model.getFullName());*/
	//}
	
	
	
	/** 
	 * To update the children EditParts according
	 * to the getModelChildren() method of the EditPart
	 * (changes in the model which need this kind of
	 * refreshing are called structural changes).
	 */
	/*public void refreshChildren(){
	 
	 }*/
	
	/**
	 * Must return the list of model objects that
	 * should be represented as children of the
	 * content pane of the view (if you don't override
	 * this method, the empty list is returned).
	 */
	protected List getModelChildren(){
		System.out.println("#### getModelChildren call");
		System.out.println(getCastedModel().getChildren());
		return getCastedModel().getChildren();
	}
	
	/**
	 * When an EditPart is added to the EditParts tree
	 * and when its figure is added to the figure tree,
	 * the method EditPart.activate() is called.
	 */
	public void activate(){
		if (!isActive())
			getCastedModel().addPropertyChangeListener(this);
		super.activate();
	}
	
	/**
	 * When an EditPart is removed from the EditParts
	 * tree, the method deactivate() is called.
	 */
	public void deactivate(){
		if (isActive())
			getCastedModel().removePropertyChangeListener(this);
		super.deactivate();
	}
	
	/**
	 * Convert the result of EditPart.getModel()
	 * to the actual class.
	 */
	public HostObject getCastedModel(){
		return (HostObject)getModel();
	}
	
	/**
	 * 
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		// TODO Auto-generated method stub
		
	}
	
	protected void createEditPolicies() {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
