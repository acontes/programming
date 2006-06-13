package org.objectweb.proactive.ic2d.monitoring.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.objectweb.proactive.ic2d.monitoring.data.HostObject;
import org.objectweb.proactive.ic2d.monitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.monitoring.editparts.IC2DEditPartFactory;


/**
 * Sample GEF editor.
 */
public class IC2DEditor extends EditorPart{
	
	private EditDomain editDomain;
	
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException {
		setSite(site);
		setInput(input);
		
		editDomain = new DefaultEditDomain(this);
	}
	
	public void createPartControl(Composite parent) {
		ScrollingGraphicalViewer viewer = new ScrollingGraphicalViewer();
		viewer.createControl(parent);
		viewer.setRootEditPart(new ScalableFreeformRootEditPart());
		viewer.getControl().setBackground(ColorConstants.white);
		viewer.setEditPartFactory(new IC2DEditPartFactory());
		viewer.setContents(new HostObject(new WorldObject(), "Essai", 0, 0));
		
		editDomain.addViewer(viewer);
	}

	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setFocus() {
		// TODO Auto-generated method stub
		
	}
}

