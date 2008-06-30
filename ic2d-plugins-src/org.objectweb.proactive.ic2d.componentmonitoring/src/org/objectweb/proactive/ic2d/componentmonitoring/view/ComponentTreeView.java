package org.objectweb.proactive.ic2d.componentmonitoring.view;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.editparts.RootTreeEditPart;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.componentmonitoring.data.ComponentHolderModel;
import org.objectweb.proactive.ic2d.componentmonitoring.data.ComponentModel;
import org.objectweb.proactive.ic2d.componentmonitoring.editpart.TreeEditPartFactory;

public class ComponentTreeView extends ViewPart
{

	// @Override
	// public void createPartControl(Composite parent) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void setFocus() {
	// // TODO Auto-generated method stub
	//
	// }
	public static final String ID = "org.objectweb.proactive.ic2d.componentmonitoring.view.ComponentTreeView";

	/**
	 * Component Metrics:
	 * 1. name
	 * 2. hierarchical
	 * 3. status
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

	public static final int NUMBER_OF_COLUMNS = 12;

	public static final int NAME_COLUMN = 0;

	public static final int HIERARCHICAL_COLUMN = 1;

	public static final int STATUS_COLUMN = 2;

	public static final int MEAN_ARRIVAL_RATE_COLUMN = 3;

	public static final int MEAN_DEPARTURE_RATE_COLUMN = 4;

	public static final int MEAN_SERVICE_RATE_COLUMN = 5;

	public static final int SAMPLE_ARRIVAL_RATE_COLUMN = 6;

	public static final int SAMPLE_DEPARTURE_RATE_COLUMN = 7;
	
	public static final int SAMPLE_SERVICE_RATE_COLUMN = 8;

	public static final int TIME_ARRIVAL_RATE_COLUMN = 9;

	public static final int TIME_DEPARTURE_RATE_COLUMN = 10;

	public static final int TIME_SERVICE_RATE_COLUMN = 11;

	protected TreeViewer treeViewer;

	public Tree tree;

	// protected SaveToXmlAction saveToXmlAction;
	// protected ExpandAllAction expandAllAction;
	// protected CollapseAllAction collapseAllAction;
	// protected DeleteTreeAction deleteTreeAction;
	// protected SwitchToTimerPieViewAction pieAction;

	protected ComponentHolderModel CHolder;

	private EditDomain editDomain;

	private ComponentModel C1;

	public ComponentTreeView()
	{
		super();
		builtComponentSample();
	}

	@Override
	public void createPartControl(Composite parent)
	{
		this.treeViewer = new TreeViewer();
		this.treeViewer.createControl(parent);
		this.editDomain = new EditDomain();

		this.treeViewer.setEditDomain(this.editDomain);
		RootTreeEditPart t = (RootTreeEditPart) this.treeViewer
				.getRootEditPart();

		tree = (Tree) t.getWidget();
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);

		/*
		 * add tree columns
		 */
		addTreeColumn(tree, "Name", 200);
		addTreeColumn(tree, "Hierarchical", 100);
		addTreeColumn(tree, "Status", 100);
		addTreeColumn(tree, "Mean Arrival Rate", 100);
		addTreeColumn(tree, "Mean Departure rate", 100);
		addTreeColumn(tree, "Mean Service rate", 100);
		addTreeColumn(tree, "Sample Arrival Rate", 100);
		addTreeColumn(tree, "Sample Departure Rate", 100);
		addTreeColumn(tree, "Sample Service Rate", 100);
		addTreeColumn(tree, "Time Arrival Rate", 100);
		addTreeColumn(tree, "Time Departure Rate", 100);
		addTreeColumn(tree, "Time Service Rate", 100);

		//////////////////////////////////////////
//		Listener sortListener = new Listener()
//		{
//			public void handleEvent(Event e)
//			{
//				if (tree.getItems().length == 0)
//				{
//					return;
//				}
//
//				// determine new sort column and direction
//				TreeColumn sortColumn = tree.getSortColumn();
//				TreeColumn currentColumn = (TreeColumn) e.widget;
//				int dir = tree.getSortDirection();
//				if (sortColumn == currentColumn)
//				{
//					dir = (dir == SWT.UP) ? SWT.DOWN : SWT.UP;
//				}
//				else
//				{
//					tree.setSortColumn(currentColumn);
//					dir = SWT.UP;
//				}
//				final int columnIndex = tree.indexOf(currentColumn);
//				boolean up = (dir == SWT.UP);
//				
//				for (final TimerTreeNodeObject t : timerTreeHolder
//						.getDummyRoots())
//				{
//					// Get first child of each dummy root and fire sort event
//					TimerTreeNodeObject target = t.getChildren().get(0);
//					target.firePropertyChange(TimerTreeNodeObject.P_SORT,
//							columnIndex, up);
//					target.firePropertyChange(
//							TimerTreeNodeObject.P_EXPAND_STATE, null, true);
//				}
//				tree.setSortDirection(dir);
//			}
//		};
//		timeColumn.addListener(SWT.Selection, sortListener);
//		totalPercentColumn.addListener(SWT.Selection, sortListener);
//		invocationsColumn.addListener(SWT.Selection, sortListener);
//		parentPercentColumn.addListener(SWT.Selection, sortListener);

//		this.treeViewer.setEditPartFactory(new TreeEditPartFactory(this));
		this.treeViewer.setEditPartFactory(new TreeEditPartFactory());
		// this.treeViewer.setContents(this.hellomodel);
		this.treeViewer.setContents(this.CHolder);

		Thread showThread = new Thread(new changeShow());
		showThread.start();

	}

	@Override
	public void setFocus()
	{}

	private class changeShow implements Runnable
	{
		public void run()
		{
			int i = 0;
			String newName = "name";
			while (true)
			{
				try
				{

					Thread.sleep(5000);
					C1.setName(newName + (i++));
					C1.setMeanArrivalRate(i);
					System.out.println("C1.setName(newName+i) = " + i);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}

		}
	}

	private void builtComponentSample()
	{
		try
		{
			CHolder = new ComponentHolderModel();

			C1 = new ComponentModel(CHolder, "Component1");
			C1.setName("Component1");
			C1.setHierachical("Composite");
			C1.setState("Start");

			ComponentModel C2 = new ComponentModel(CHolder, "Component2");
			C2.setName("Component2");
			C2.setHierachical("primitive");
			C2.setState("STOP");

			ComponentModel C11 = new ComponentModel(C1, "Component11");
			C11.setName("Component11");
			C11.setHierachical("Composite");
			C11.setState("Start");

			ComponentModel C111 = new ComponentModel(C11, "Component111");
			C111.setName("Component111");
			C111.setHierachical("primitive");
			C111.setState("Start");

			ComponentModel C12 = new ComponentModel(C1, "Component12");
			C12.setName("Component12");
			C12.setHierachical("primitive");
			C12.setState("Start");

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private TreeColumn addTreeColumn(Tree tree, String name, int width)
	{
		TreeColumn newColumn = new TreeColumn(tree, SWT.CENTER);
		newColumn.setText(name);
		newColumn.setWidth(width);
		return newColumn;
	}

	// public DeleteTreeAction getDeleteTreeAction() {
	// return deleteTreeAction;
	// }
	//
	// public TreeViewer getTreeViewer() {
	// return treeViewer;
	// }
	//
	// public SwitchToTimerPieViewAction getPieAction() {
	// return pieAction;
	// }

}
