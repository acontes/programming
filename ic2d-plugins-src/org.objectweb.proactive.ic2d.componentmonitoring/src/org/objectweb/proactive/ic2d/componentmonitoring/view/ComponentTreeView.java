package org.objectweb.proactive.ic2d.componentmonitoring.view;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.RootTreeEditPart;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.componentmonitoring.actions.CollapseAllAction;
import org.objectweb.proactive.ic2d.componentmonitoring.actions.ExpandAllAction;
import org.objectweb.proactive.ic2d.componentmonitoring.actions.NewHostAction;
import org.objectweb.proactive.ic2d.componentmonitoring.editpart.TreeEditPartFactory;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;

/** 
 * The ComponentTree view keeps the hierarchical list of components
 * in the monitored application.
 * 
 *
 */
public class ComponentTreeView extends ViewPart {

	public Logger logger = Logger.getLogger("ComponentTreeView");
	/**
	 * View ID
	 */
	public static final String ID = "org.objectweb.proactive.ic2d.componentmonitoring.view.ComponentTreeView";

    /**
     * Component Metrics:
     * 1. name
     * 2. hierarchical
     * 3. status
     * ... sooner than later ... to be reviewed
     * 4. mean arrival rate
     * 5. mean departure rate
     * 6. mean service rate
     * 7. Sample Arrival Rate (n sample)
     * 8. Sample Departure Rate (n sample)
     * 9. Sample Service Rate (n sample)
     * 10. Time Arrival Rate(millis)
     * 11. Time Departure Rate(millis)
     * 12. Time Service Rate(millis)
     */
	
	public static final int NUMBER_OF_COLUMNS = 3;
    public static final int NAME_COLUMN = 0;
    public static final int HIERARCHICAL_COLUMN = 1;
    public static final int STATUS_COLUMN = 2;
//    public static final int MEAN_ARRIVAL_RATE_COLUMN = 3;
//    public static final int MEAN_DEPARTURE_RATE_COLUMN = 4;
//    public static final int MEAN_SERVICE_RATE_COLUMN = 5;
//    public static final int SAMPLE_ARRIVAL_RATE_COLUMN = 6;
//    public static final int SAMPLE_DEPARTURE_RATE_COLUMN = 7;
//    public static final int SAMPLE_SERVICE_RATE_COLUMN = 8;
//    public static final int TIME_ARRIVAL_RATE_COLUMN = 9;
//    public static final int TIME_DEPARTURE_RATE_COLUMN = 10;
//    public static final int TIME_SERVICE_RATE_COLUMN = 11;

    // graphical stuff
    protected TreeViewer treeViewer;
    public Tree tree;
    private EditDomain editDomain;

    /**
     * The model of the application to be monitored 
     */
    private WorldObject worldObject;
    
    /**
     * The controller that will observe the worldObject
     */
    //private WorldController gcontroller;
    
    /**
     * The holder of all the monitored components
     */
    //protected ComponentModelHolder cmh;
    
    // There's another version of this constructor, that receives the worldObject to observe
    public ComponentTreeView() {
        super();
        logger.setLevel(Level.DEBUG);
        logger.debug("constructor");
        
        // this view has the ComponentModelHolder, which contains the list of all monitored components
        /* 
        try {
            this.CHolder = new ComponentHolderModel();
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        // doesn't need to create a new WorldObject. Instead, it receives one from the AO Monitoring View
        //this.worldObject = new WorldObject();

        // the secondline should be released ... the first, don't know
        /*
        //this.CHolder = (ComponentHolderModel)this.world.getHolder(HolderTypes.COMPONENT_HOLDER);
        this.worldObject.addHolder(this.CHolder);
        */
    }
    
    public void setWorldObject(WorldObject world) {
    	logger.debug("setWorldObject: " + world.getName());
        this.worldObject = world;
        this.setPartName("Components: "+ world.getName());

        /*
        this.worldObject.addHolder(this.CHolder);
        //		gcontroller = new WorldController(this.world,  null);
        //		this.CHolder = (ComponentHolderModel)this.world.getHolder(HolderTypes.COMPONENT_HOLDER);
        */
    }
    
    
	@Override
	public void createPartControl(Composite parent) {
		logger.debug("createPartControl");
        this.treeViewer = new TreeViewer();
        this.treeViewer.createControl(parent);
        this.editDomain = new EditDomain();
        this.treeViewer.setEditDomain(this.editDomain);
        RootTreeEditPart t = (RootTreeEditPart) this.treeViewer.getRootEditPart();

        tree = (Tree) t.getWidget();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        addTreeColumn(tree, "Name", 200);
        addTreeColumn(tree, "Hierarchical", 100);
        addTreeColumn(tree, "Status", 100);
        // TODO: which of these we really want/need
//        addTreeColumn(tree, "Mean Arrival Rate (/s)", 100);
//        addTreeColumn(tree, "Mean Departure rate (/s)", 100);
//        addTreeColumn(tree, "Mean Service rate (/s)", 100);
//        addTreeColumn(tree, "Sample Arrival Rate (/s)", 100);
//        addTreeColumn(tree, "Sample Departure Rate (/s)", 100);
//        addTreeColumn(tree, "Sample Service Rate (/s)", 100);
//        addTreeColumn(tree, "Time Arrival Rate (/s)", 100);
//        addTreeColumn(tree, "Time Departure Rate (/s)", 100);
//        addTreeColumn(tree, "Time Service Rate (/s)", 100);

        IToolBarManager toolBarManager = getViewSite().getActionBars().getToolBarManager();

        toolBarManager.add(new ExpandAllAction(treeViewer));
        toolBarManager.add(new CollapseAllAction(treeViewer));
        toolBarManager.add(new NewHostAction(parent.getDisplay(), this.worldObject));
        
        this.treeViewer.setEditPartFactory(new TreeEditPartFactory());
        //this.treeViewer.setContents(this.CHolder);
        
        //gcontroller = new WorldController(this.world, null);

	}

	@Override
	public void setFocus() {

	}
	
    private TreeColumn addTreeColumn(Tree tree, String name, int width) {
        TreeColumn newColumn = new TreeColumn(tree, SWT.CENTER);
        newColumn.setText(name);
        newColumn.setWidth(width);
        return newColumn;
    }

}
