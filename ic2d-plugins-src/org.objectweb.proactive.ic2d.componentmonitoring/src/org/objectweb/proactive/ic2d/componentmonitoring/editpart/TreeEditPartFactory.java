package org.objectweb.proactive.ic2d.componentmonitoring.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.objectweb.proactive.ic2d.componentmonitoring.view.ComponentTreeView;

/**
 *  Don't know what does this do (yet)
 *  It seems that it creates the EditParts for the ComponentModel, or the ComponentModelHolder.
 *   
 */
public class TreeEditPartFactory implements EditPartFactory {

	private ComponentTreeView componentTreeView;
	
	/*
    public TreeEditPartFactory(final ComponentTreeView componentTreeView) {
        this.componentTreeView = componentTreeView;
    }*/
    
    public TreeEditPartFactory() {
    	
    }
        
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;

		// don't know if it has to creates the editpart for both cases ... (wasn't it the case that one of those was not used)?
		/*
        if (model instanceof ComponentHolderModel) {
            part = new ComponentHolderEditPart((ComponentHolderModel) model);
        } else if (model instanceof ComponentModel) {
            part = new ComponentEditPart((ComponentModel) model);
        }
        if (part != null) {
            part.setModel(model);

        }*/

        return part;
	}

}
