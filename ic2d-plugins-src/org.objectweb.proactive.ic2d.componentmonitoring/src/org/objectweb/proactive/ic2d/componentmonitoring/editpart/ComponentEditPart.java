package org.objectweb.proactive.ic2d.componentmonitoring.editpart;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.objectweb.proactive.ic2d.componentmonitoring.data.ComponentModel;
import org.objectweb.proactive.ic2d.componentmonitoring.view.ComponentTreeView;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.componentmonitoring.util.ComponentMVCNotification;
import org.objectweb.proactive.ic2d.componentmonitoring.util.ComponentMVCNotificationTag;

public class ComponentEditPart extends ComponentMonitorTreeEditPart<ComponentModel>
{

	// public static final Image COMPONENT_IMAGE = new
	// Image(Display.getCurrent(),
	// ComponentEditPart.class.getResourceAsStream("component_icon.png"));

	private ComponentMVCNotificationTag mvcNotif;

	/**
	 * The contructor of this controller part.
	 * 
	 * @param model  The instance Component model associated to this controller
	 */
	public ComponentEditPart(final ComponentModel model)
	{
		super(model);
	}

	public void update(Observable o, Object arg)
	{
		// TODO Auto-generated method stub
		final ComponentMVCNotification notif = (ComponentMVCNotification) arg;
		mvcNotif = notif.getMVCNotification();
		//		Object data = notif.getData();
		
		System.out.println("in Component Edit Part, notification received!");

		if (!getViewer().getControl().getDisplay().isDisposed())
		{
			Runnable runnable = new Runnable()
			{
				public void run()
				{
					//
					//					switch (mvcNotif)
					//					{
					//						case STATE_CHANGED:
					//						{
					//							ComponentModel model = (ComponentModel) getModel();
					//							if (getWidget() instanceof Tree)
					//							{
					//								return;
					//							}
					//							if (getWidget() instanceof TreeItem)
					//							{
					//								((TreeItem) getWidget()).setText(ComponentTreeView.STATUS_COLUMN, model.getStatus());
					////								((TreeItem) getWidget()).setBackground(ComponentTreeView.STATUS_COLUMN, new Color(getViewer().getControl()
					////										.getDisplay().getCurrent(), 255, 255, 116));
					////								((TreeItem) getWidget()).setForeground(ComponentTreeView.STATUS_COLUMN, new Color(getViewer().getControl()
					//////										.getDisplay().getCurrent(), 255, 255, 116));
					//							}
					//
					//						}
					//						case NAME_CHANGED:
					//						{
					//							ComponentModel model = (ComponentModel) getModel();
					//							if (getWidget() instanceof Tree)
					//							{
					//								return;
					//							}
					//							if (getWidget() instanceof TreeItem)
					//							{
					//								((TreeItem) getWidget()).setText(ComponentTreeView.NAME_COLUMN, model.getName());
					//							}
					//						}
					//						case HIERACHICAL_CHANGED:
					//						{
					//							ComponentModel model = (ComponentModel) getModel();
					//							if (getWidget() instanceof Tree)
					//							{
					//								return;
					//							}
					//							if (getWidget() instanceof TreeItem)
					//							{
					//								((TreeItem) getWidget()).setText(ComponentTreeView.HIERARCHICAL_COLUMN, model.getHierachical());
					//							}
					//						}
					//						default:
					//							break;
					//					} // switch

					ComponentModel model = (ComponentModel) getModel();
					if (getWidget() instanceof Tree)
					{
						return;
					}
					else if (getWidget() instanceof TreeItem)
					{
						switch (mvcNotif)
						{
							case STATE_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.STATUS_COLUMN, model.getStatus());
							case NAME_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.NAME_COLUMN, model.getName());
							case HIERACHICAL_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.HIERARCHICAL_COLUMN, model.getHierachical());
							case MEAN_ARRIVAL_RATE_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.MEAN_ARRIVAL_RATE_COLUMN, String.valueOf(model
										.getMeanArrivalRate()));
							case MEAN_DEPARTURE_RATE_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.MEAN_DEPARTURE_RATE_COLUMN, String.valueOf(model
										.getMeanDepartureRate()));
							case MEAN_SERVICE_RATE_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.MEAN_SERVICE_RATE_COLUMN, String.valueOf(model
										.getMeanServiceRate()));
							case SAMPLE_ARRIVAL_RATE_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.SAMPLE_ARRIVAL_RATE_COLUMN, String.valueOf(model
										.getSampleArrivalRate()));
							case SAMPLE_DEPARTURE_RATE_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.SAMPLE_DEPARTURE_RATE_COLUMN, String.valueOf(model
										.getSampleDepartureRate()));
							case SAMPLE_SERVICE_RATE_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.SAMPLE_SERVICE_RATE_COLUMN, String.valueOf(model
										.getSampleServiceRate()));
							case TIME_ARRIVAL_RATE_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.TIME_ARRIVAL_RATE_COLUMN, String.valueOf(model
										.getTimeArrivalRate()));
							case TIME_DEPARTURE_RATE_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.TIME_DEPARTURE_RATE_COLUMN, String.valueOf(model
										.getTimeDepartureRate()));
							case TIME_SERVICE_RATE_CHANGED:
								((TreeItem) getWidget()).setText(ComponentTreeView.TIME_SERVICE_RATE_COLUMN, String.valueOf(model
										.getTimeServiceRate()));
//							case ADD_CHILD:
//								System.out.println("add child notification received in Component  Editpart");
//								new TreeItem((TreeItem)getWidget(),0);
							default:
								break;
						} // switch
					}

				}

			};

			// Both syncExec and asynExec can perform well
			//			getViewer().getControl().getDisplay().syncExec(this);

			// this asyncExec would not suspend the caller
			getViewer().getControl().getDisplay().asyncExec(runnable);

		}

	}

	protected final void refreshVisuals()
	{
		ComponentModel model = (ComponentModel) getModel();
		if (getWidget() instanceof Tree)
		{
			return;
		}
		if (this.getWidget() instanceof TreeItem)
		{
			((TreeItem) this.getWidget()).setText(ComponentTreeView.NAME_COLUMN, model.getName());
			((TreeItem) this.getWidget()).setText(ComponentTreeView.HIERARCHICAL_COLUMN, model.getHierachical());
			((TreeItem) this.getWidget()).setText(ComponentTreeView.STATUS_COLUMN, model.getStatus());
			((TreeItem) this.getWidget()).setText(ComponentTreeView.MEAN_ARRIVAL_RATE_COLUMN, String.valueOf(model.getMeanArrivalRate()));
			((TreeItem) this.getWidget()).setText(ComponentTreeView.MEAN_DEPARTURE_RATE_COLUMN, String.valueOf(model.getMeanDepartureRate()));
			((TreeItem) this.getWidget()).setText(ComponentTreeView.MEAN_SERVICE_RATE_COLUMN, String.valueOf(model.getMeanServiceRate()));
			((TreeItem) this.getWidget()).setText(ComponentTreeView.SAMPLE_ARRIVAL_RATE_COLUMN, String.valueOf(model.getSampleArrivalRate()));
			((TreeItem) this.getWidget()).setText(ComponentTreeView.SAMPLE_DEPARTURE_RATE_COLUMN, String.valueOf(model.getSampleDepartureRate()));
			((TreeItem) this.getWidget()).setText(ComponentTreeView.SAMPLE_SERVICE_RATE_COLUMN, String.valueOf(model.getSampleServiceRate()));
			((TreeItem) this.getWidget()).setText(ComponentTreeView.TIME_ARRIVAL_RATE_COLUMN, String.valueOf(model.getTimeArrivalRate()));
			((TreeItem) this.getWidget()).setText(ComponentTreeView.TIME_DEPARTURE_RATE_COLUMN, String.valueOf(model.getTimeDepartureRate()));
			((TreeItem) this.getWidget()).setText(ComponentTreeView.TIME_SERVICE_RATE_COLUMN, String.valueOf(model.getTimeServiceRate()));
		}
	}

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
