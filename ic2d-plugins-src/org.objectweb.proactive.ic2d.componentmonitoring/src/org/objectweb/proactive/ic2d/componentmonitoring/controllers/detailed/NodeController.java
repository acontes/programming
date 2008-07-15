/**
 *
 */
package org.objectweb.proactive.ic2d.componentmonitoring.controllers.detailed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.component.identity.ProActiveComponentImpl;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean;
import org.objectweb.proactive.core.jmx.mbean.ComponentWrapperMBean;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;
import org.objectweb.proactive.ic2d.componentmonitoring.controllers.AbstractStandardToComponentsController;
import org.objectweb.proactive.ic2d.componentmonitoring.controllers.StandardToComponentsController;
import org.objectweb.proactive.ic2d.componentmonitoring.data.ComponentHolderModel;
import org.objectweb.proactive.ic2d.componentmonitoring.data.ComponentModel;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.HolderTypes;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.NodeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.RuntimeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;

/**
 * @author vjuresch
 * 
 */
public class NodeController extends AbstractStandardToComponentsController {

	ComponentHolderModel chm;

	public NodeController(final AbstractData modelObject, final StandardToComponentsController parent) {
		super(modelObject, parent);
		// TODO Auto-generated constructor stub
		chm = (ComponentHolderModel) this.getModelObject().getWorldObject().getHolder(HolderTypes.COMPONENT_HOLDER);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.AbstractStandardToComponentsControllerController#removeFigure(java.lang.String)
	 */
	@Override
	public void removeFigure(final String key) {
		// TODO Auto-generated method stub
	}

	@Override
	protected AbstractStandardToComponentsController createChildController(final AbstractData modelObject) {
		return new ActiveObjectController(modelObject, this);
	}

	@Override
	public void update(final Observable o, final Object arg) {
		super.update(o, arg);
		final MVCNotification mvcNotif = (MVCNotification) arg;
		final MVCNotificationTag mvcNotifTag = mvcNotif.getMVCNotification();
		switch (mvcNotifTag) {
		case ADD_CHILD: {
//			System.out.println("NodeController.update() in ADD_CHILD");
			// add new controller/figure
			// get key
			final String figureKey = (String) mvcNotif.getData();
			// get data on the figure
			final AbstractData childModelObject = ((AbstractData) o).getMonitoredChild(figureKey);
			this.createComponentModel((ActiveObject) childModelObject);
			break;
		}
		case ADD_CHILDREN: {
			// HostObject hostObj=this.host;
			final List<String> keys = (ArrayList<String>) mvcNotif.getData();

			// System.out.println("NodeController.update() in ADD_CHILDREN
			// keys.size() = "+keys.size());
			for (int k = 0; k < keys.size(); k++) {
				final String modelObjectKey = keys.get(k);
				final AbstractData childModelObject = this.getModelObject().getChild(modelObjectKey);
				this.createComponentModel((ActiveObject) childModelObject);
			}
			break;
		} // [case ADD_CHILDREN]
		case REMOVE_CHILD: {
			final String figureKey = (String) mvcNotif.getData();
			final StandardToComponentsController childController = this.getChildControllerByKey(figureKey);
			if (childController == null) {
				return;
			}

			// TODO: to be implemented -> update the componentModel -> remove
			// the child
			break;
		}
		default:
			System.out.println("NodeController.update() in Default");

		}

	}

	private void createComponentModel(ActiveObject childModelObject) {

//		System.out.println("createComponentModel in NodeController");

		NodeObject myNode = (NodeObject) this.getModelObject();
		final List<ObjectName> activeObjectNames = myNode.getProxyNodeMBean().getActiveObjects();
//		System.out.println("NodeController.createComponentModel() -> now in node ="+myNode.getName()+" this node has AO number ="+activeObjectNames.size());
		
		
		
		for (final ObjectName oname : activeObjectNames) {
			final BodyWrapperMBean proxyBodyMBean = MBeanServerInvocationHandler.newProxyInstance(myNode.getProActiveConnection(), oname,
					BodyWrapperMBean.class, false);
			
			
			final UniqueID id = proxyBodyMBean.getID();
//			System.out.println("NodeController.createComponentModel() -> ComponentModel UniqueID = ActiveObject proxyBodyMBean UniqueID = "+id );
			
			final String idString = id.toString();

			String activeObjectName = proxyBodyMBean.getName();
			ComponentWrapperMBean proxyComponentMBean = MBeanServerInvocationHandler.newProxyInstance(myNode.getProActiveConnection(), oname,
					ComponentWrapperMBean.class, false);
			try {
				ComponentModel Cchild = new ComponentModel(myNode, id, activeObjectName, oname, proxyComponentMBean);
//				System.out.println("NodeController.createComponentModel()-> create component Model "+activeObjectName+" UniqueID ="+id);
				Cchild.setName(activeObjectName);

				// using hashmap
				if (!chm.components.containsKey(id))
				{
					//if this is the first time find this component Model,now the parent of this component is the nodeObject
					// JMX register
					//JMX Notification Listener register
					try {
						
//						System.out.println("NodeController.createComponentModel() -> Cchild objectName = "+Cchild.getObjectName());
			            JMXNotificationManager.getInstance().subscribe(Cchild.getObjectName(), Cchild.getListener(),
			                    ((RuntimeObject)(Cchild.getParent().getParent())).getUrl());
			            
			            System.out.println("NodeController.createComponentModel()-> ComponentModelListener ="+Cchild.getListener().toString());
			        } catch (IOException e) {
			            System.out.println("ComponentModel: could not register ComponentListener: " + Cchild.getObjectName());
			            e.printStackTrace();
			        }
					
					// add component model in the list
					chm.components.put(id, Cchild);
					
//					System.out.println("NodeController.createComponentModel() -> compnoent Holder has componts number ="+chm.components.size());
				}

				// componentmodels[index++] = Cchild;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}// for

		// re build the relation ship of these components
		// ComponentHierarchicalRebuild(componentmodels);
		componentHierarchicalRebuild(chm.components);
	}

	private void componentHierarchicalRebuild(Map<UniqueID, ComponentModel> componentmodels) {
				
		Set<UniqueID> keys = componentmodels.keySet();
		for (UniqueID key : keys) {
			ComponentModel currentModel = componentmodels.get(key);
			ComponentWrapperMBean proxyMBean = currentModel.getComponentWrapperMBean();
			ProActiveComponent[] subComponents = proxyMBean.getSubComponents();
			if (subComponents != null) {
//				System.out.println("NodeController.componentHierarchicalRebuild()->subComponents !=null");
				addSubComponents(currentModel, subComponents, componentmodels);
			}
			else
			{
//				System.out.println("NodeController.componentHierarchicalRebuild()->subComponents ==null");
			}
			
		}
		for (UniqueID key : keys) {
			ComponentModel currentModel = componentmodels.get(key);
			if (currentModel.getParent() instanceof NodeObject) {
				currentModel.setParent(chm);
				chm.addChild(currentModel);
			}
		}

		// getWorldObject().CHolder = this.CHolder;
		// showComponentHierachical(getWorldObject().CHolder);
	}

	private void addSubComponents(ComponentModel parent, ProActiveComponent[] subcomponents, Map<UniqueID, ComponentModel> componentmodels) {
		
		parent.setHierachical("Composite");
		
		for (ProActiveComponent child : subcomponents) {
			
//			ProActiveComponentImpl Child = (ProActiveComponentImpl)child;
			UniqueID childID = child.getID();
//			System.out.println("NodeController.addSubComponents()-> subcompnent ID = "+childID);
//			System.out.println("NodeController.addSubComponents() -> Child.getBody().getMBean().getID() ="+Child.getBody().getMBean().getID());
			Set<UniqueID> keys = componentmodels.keySet();
			for (UniqueID key : keys) {
//				System.out.println("NodeController.addSubComponents() -> componentmodel id ="+key);
				
				if (key.equals(childID)) {
					// find one child.
					// if this child is not primitive one, first add its
					// children..
					ComponentModel currentModel = componentmodels.get(key);
//					System.out.println("NodeController.addSubComponents() -> children ="+currentModel.getName() +" its parent is ="+parent.getName());
					
					
					ComponentWrapperMBean proxyMBean = currentModel.getComponentWrapperMBean();
					ProActiveComponent[] subsubComponents = proxyMBean.getSubComponents();
					if (subsubComponents != null) {
						addSubComponents(currentModel, subsubComponents, componentmodels);
						currentModel.setHierachical("Composite");
					} else {
						currentModel.setHierachical("Primitive");
					}
					
					// then add it to the parent
					currentModel.setParent(parent);
					parent.addChild(currentModel);
					componentmodels.put(parent.getID(), parent);
					componentmodels.put(currentModel.getID(), currentModel);

					// if this one used to be the root, now it has its parent,
					// delete from CHolder

					chm.removeChild(currentModel);

					break;
				}
			}
		}

		// after add all the subcomponents return;
	}

	private void showComponentHierachical(ComponentHolderModel CHolder) {
		// System.out.println("[YYL Test OutPut:]"+"in NodeObject"+"this.CHolder
		// has "+CHolder.getMonitoredChildrenSize()+" children");
		List<AbstractData> childrens = CHolder.getMonitoredChildrenAsList();
		if (childrens != null) {
			for (AbstractData child : childrens) {
				ComponentModel tmpChild = (ComponentModel) child;
				showComponent(tmpChild, CHolder.getName());
			}
		}
	}

	private void showComponent(ComponentModel model, String parent) {
		System.out.println("[YYL Test OutPut:]" + "in NodeObject " + model.getName() + " parent =" + parent);
		List<AbstractData> childrens = model.getMonitoredChildrenAsList();
		if (childrens != null) {
			for (AbstractData child : childrens) {
				ComponentModel tmpChild = (ComponentModel) child;
				showComponent(tmpChild, model.getName());
			}
		}
	}

	public ComponentHolderModel getComponentHolderModel() {
		return this.chm;
	}

}
