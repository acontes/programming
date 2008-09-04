package org.objectweb.proactive.ic2d.componentmonitoring.extensions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.objectweb.proactive.ic2d.componentmonitoring.controllers.AbstractStandardToComponentsController;
import org.objectweb.proactive.ic2d.componentmonitoring.controllers.detailed.HostController;
import org.objectweb.proactive.ic2d.componentmonitoring.controllers.detailed.NodeController;
import org.objectweb.proactive.ic2d.componentmonitoring.controllers.detailed.RuntimeController;
import org.objectweb.proactive.ic2d.componentmonitoring.controllers.detailed.WorldController;
import org.objectweb.proactive.ic2d.componentmonitoring.view.ComponentTreeView;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.HostObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.NodeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.RuntimeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.extpoint.IActionExtPoint;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;


public class ShowComponentViewAction extends Action implements IActionExtPoint {

    public static final String SHOW_COMP_VIEW_ACTION = "Show components view";
    public static final String SHOW_COMP_TOOL_TIP = "Show the component view of the monitored applications";

    private AbstractData target;
    private boolean created;

    public ShowComponentViewAction() {
        super.setId(SHOW_COMP_VIEW_ACTION);
        //TODO: add image 
        //		 super.setImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(
        //	                org.objectweb.proactive.ic2d.chartit.Activator.getDefault().getBundle(), new Path(
        //	                    "icons/graph.gif"), null)));
        //		
        super.setToolTipText(SHOW_COMP_TOOL_TIP);
        super.setEnabled(false);
    }

    public void setAbstractDataObject(final AbstractData object) {
        //  if (object.getClass() != HostObject.class) 
        //  	return;

        //TODO: could use this to show the comp model only for one host
        //|| object.getClass() == HostObject.class)
        //return;
        if (!(object instanceof WorldObject))
            return;
        this.target = object;
        super.setText("Show component view");
        super.setEnabled(true);
    }

    @Override
    public void run() {
        this.handleData(target);
    }

    public void setActiveSelect(final AbstractData ref) {
        //   this.handleData(ref);
    }

    private void handleData(final AbstractData abstractData) {
        if (abstractData == null)
            return;

        WorldObject wo = (WorldObject) abstractData;
        //	        System.out.println("ShowComponentViewAction.handleData() -> showing component view of "+wo.toString());

        try {
            ComponentTreeView view = (ComponentTreeView) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage().showView(ComponentTreeView.ID, null, IWorkbenchPage.VIEW_ACTIVATE);
            view.setWorldObject(wo);
        } catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //	        
        AbstractStandardToComponentsController wc = new WorldController(wo, null);
        if (!created) {
            register(wo, wc);
            created = true;
        }
        //	        System.out.println("ShowComponentViewAction.handleData() -> will show comp view for " + target.toString());
    }

    /**
    * @param parent
    */
    public void register(AbstractData parent, AbstractStandardToComponentsController parentController) {

        for (AbstractData child : parent.getMonitoredChildrenAsList()) {
            if (child instanceof HostObject) {
                HostController wc = new HostController(child, parentController);
                parentController.addChildController(wc);
                register(child, wc);
                //					System.out.println("ShowComponentViewAction.register() -> register hostController");
            } else if (child instanceof RuntimeObject) {
                RuntimeController wc = new RuntimeController(child, parentController);
                parentController.addChildController(wc);

                register(child, wc);

                //					System.out.println("ShowComponentViewAction.register() -> register RunTimeController");
            } else if (child instanceof NodeObject) {
                NodeController wc = new NodeController(child, parentController);
                parentController.addChildController(wc);

                register(child, wc);

                //	                child.notifyObservers(
                //	                		new MVCNotification(MVCNotificationTag.ADD_CHILD, child));

                //	                System.out.println("ShowComponentViewAction.register() -> register NodeController");
            } else if (child instanceof ActiveObject) {
                parentController.update(child.getParent(), new MVCNotification(MVCNotificationTag.ADD_CHILD,
                    child.getKey()));
                //					ActiveObjectController wc = new ActiveObjectController(child,parentController);
                //					System.out.println("ShowComponentViewAction.register() -> register ActiveObjectController");
                //					parentController.addChildController(wc);

                //                child.getParent().notifyObservers(new MVCNotification(MVCNotificationTag.ADD_CHILD, child.getParent()));
                //
            }
        }

    }

}
