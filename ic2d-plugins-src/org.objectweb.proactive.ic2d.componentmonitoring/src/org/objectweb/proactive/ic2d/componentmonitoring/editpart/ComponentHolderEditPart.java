package org.objectweb.proactive.ic2d.componentmonitoring.editpart;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.objectweb.proactive.ic2d.componentmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.componentmonitoring.data.ComponentHolderModel;
import org.objectweb.proactive.ic2d.componentmonitoring.data.ComponentModel;
import org.objectweb.proactive.ic2d.componentmonitoring.view.ComponentTreeView;

public class ComponentHolderEditPart extends ComponentMonitorTreeEditPart<ComponentHolderModel>
{

	/**
	 * The contructor of this controller part.
	 * @param model The instance ComponentHolderModel model associated to this controller
	 */
	public ComponentHolderEditPart(final ComponentHolderModel model)
	{
		super(model);
	}

	public void update(Observable o, Object arg)
	{
	// TODO Auto-generated method stub

	}

	protected final void refreshVisuals()
	{
		ComponentHolderModel model = (ComponentHolderModel) getModel();
		if (getWidget() instanceof Tree)
		{
			return;
		}
		if (this.getWidget() instanceof TreeItem)
		{
			((TreeItem) this.getWidget()).setText(ComponentTreeView.NAME_COLUMN, model.getName());
			//    		 ((TreeItem)this.getWidget()).setText(ComponentTreeView.HIERARCHICAL_COLUMN, model.getHierachical());
			//    		 ((TreeItem)this.getWidget()).setText(ComponentTreeView.STATUS_COLUMN, model.getStatus());
		}
	}

	//
	// -- PROTECTED METHODS -------------------------------------------
	//

	/**
	 * @see org.eclipse.gef.editparts.AbstractEditPart#getModelChildren()
	 */
	@Override
	protected final List<ComponentModel> getModelChildren()
	{
		List<AbstractData> children = super.getCastedModel().getMonitoredChildrenAsList();
		List<ComponentModel> newchildren = new ArrayList<ComponentModel>();
		int size = children.size();
		for (int i = 0; i < size; i++)
		{
			if (children.get(i) instanceof ComponentModel)
			{
				newchildren.add((ComponentModel) children.get(i));
			}
		}
		return newchildren;
	}

}
