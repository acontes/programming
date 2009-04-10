/**
 * 
 */
package org.objectweb.proactive.ic2d.componentmonitoring.extensions;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.objectweb.proactive.ic2d.componentmonitoring.view.ComponentTreeView;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.extpoint.IActionExtPoint;

/**
 * 
 */
public class ShowComponentViewAction extends Action implements IActionExtPoint {

    public static final String SHOW_COMP_VIEW_ACTION = "Show component view";
    public static final String SHOW_COMP_TOOL_TIP = "Show the component view of the monitored applications";
    public Logger logger = Logger.getLogger("ShowComponentViewAction");
    
    /** 
     * The object representing the data to be observed.
     */
    private AbstractData<?, ?> abstractData = null;
    
    // do I need this? (Yulai's code)
    private boolean created;
    
    public ShowComponentViewAction() {
    	
    	logger.setLevel(Level.DEBUG);
        super.setId(SHOW_COMP_VIEW_ACTION);
        //TODO: add image 
        //		 super.setImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(
        //	                org.objectweb.proactive.ic2d.chartit.Activator.getDefault().getBundle(), new Path(
        //	                    "icons/graph.gif"), null)));
        super.setToolTipText(SHOW_COMP_TOOL_TIP);
        super.setEnabled(false);
    }
    
	/* (non-Javadoc)
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.extpoint.IActionExtPoint#setAbstractDataObject(org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData)
	 */
	@Override
	public void setAbstractDataObject(AbstractData<?, ?> object) {
		logger.debug("setAbstractDataObject: "+ object.getName() + ", class: "+ object.getClass().getName());
		if (!(object instanceof WorldObject))
            return;
        this.abstractData = object;
        super.setText("Show component view");
        super.setEnabled(true);
	}
	
	/**
	 * Creates/updates the ComponentTree View from the observed object (abstractData)
	 */
    @Override
    public void run() {

    	logger.debug("run");
    	logger.debug("abstractData: "+ abstractData.getName() + ", class: "+ abstractData.getClass().getName());
        if(abstractData==null) {
        	return;
        }
        
        // The data to observe must be of type WorldObject 
        if(!(abstractData instanceof WorldObject)) {
        	// TODO An exception, or a useful message should be nice here, if it REALLY has to be of type WorldObject.
        	//      So far it was just implicitly assumed (and it never generated a cast exception).
        	return;
        }

        WorldObject wo = (WorldObject) abstractData;
        logger.debug("worldObject: "+ wo.getName());
        
        // Gets the ComponentTreeView that will be the component view, and gives it the WorldObject to observe
        ComponentTreeView ctview;
        try {
			ctview = (ComponentTreeView) PlatformUI
					.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.showView(ComponentTreeView.ID, null,
							IWorkbenchPage.VIEW_ACTIVATE);
			ctview.setWorldObject(wo);
		} catch (PartInitException e) {
			e.printStackTrace();
		}

        /*        
        AbstractStandardToComponentsController wc = new WorldController(wo, null);
        if (!created) {
            register(wo, wc);
            created = true;
        }*/
    }
    
	/* (non-Javadoc)
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.extpoint.IActionExtPoint#setActiveSelect(org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData)
	 */
	@Override
	public void setActiveSelect(AbstractData<?, ?> ref) {
		logger.debug("setActiveSelect: " + ref.getName() + ", class: "+ ref.getClass().getName());
	}
	

}
