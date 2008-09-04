package org.objectweb.proactive.ic2d.componentmonitoring.editpart;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.objectweb.proactive.ic2d.componentmonitoring.data.ComponentHolderModel;
import org.objectweb.proactive.ic2d.componentmonitoring.data.ComponentModel;
import org.objectweb.proactive.ic2d.componentmonitoring.view.ComponentTreeView;


public class TreeEditPartFactory implements EditPartFactory {
    private ComponentTreeView ComponentTreeView;

    public TreeEditPartFactory(final ComponentTreeView ComponentTreeView) {
        this.ComponentTreeView = ComponentTreeView;
    }

    public TreeEditPartFactory() {
    }

    public final EditPart createEditPart(final EditPart context, final Object model) {
        EditPart part = null;

        if (model instanceof ComponentHolderModel) {
            part = new ComponentHolderEditPart((ComponentHolderModel) model);
        } else if (model instanceof ComponentModel) {
            part = new ComponentEditPart((ComponentModel) model);
        }
        if (part != null) {
            part.setModel(model);

        }

        return part;
    }
}
