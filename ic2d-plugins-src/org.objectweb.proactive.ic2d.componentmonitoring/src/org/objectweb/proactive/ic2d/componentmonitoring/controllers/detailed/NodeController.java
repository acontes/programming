/**
 *
 */
package org.objectweb.proactive.ic2d.componentmonitoring.controllers.detailed;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean;
import org.objectweb.proactive.core.jmx.mbean.ComponentWrapperMBean;
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
	
	public NodeController(final AbstractData modelObject,
            final StandardToComponentsController parent) {
        super(modelObject,  parent);
        // TODO Auto-generated constructor stub
         chm = (ComponentHolderModel)this.getModelObject().getWorldObject().getHolder(HolderTypes.COMPONENT_HOLDER);
    
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
        return new ActiveObjectController(modelObject,  this);
    }
    
    @Override
    public void update(final Observable o, final Object arg) {
    	System.out.println("NodeController.update()");
    	super.update(o, arg);
    	System.out.println("NodeController.update() after super.update");;
    	final MVCNotification mvcNotif = (MVCNotification) arg;
           final MVCNotificationTag mvcNotifTag = mvcNotif.getMVCNotification();
        switch (mvcNotifTag) {
        case ADD_CHILD: {
        	System.out.println("NodeController.update() in ADD_CHILD");
        	// add new controller/figure
            // get key
            final String figureKey = (String) mvcNotif.getData();

        	System.out.println("After figureKey");

            // get data on the figure
            final AbstractData childModelObject = ((AbstractData) o)
                    .getMonitoredChild(figureKey);
        	System.out.println("After childModelObject");

            this.createComponentModel((ActiveObject)childModelObject); 
        	System.out.println("After createcomponentmodel");

            break;
        }
        case ADD_CHILDREN: {
            // HostObject hostObj=this.host;
            final List<String> keys = (ArrayList<String>) mvcNotif.getData();
            System.out.println("NodeController.update() : " + mvcNotif.getData());
            for (int k = 0; k < keys.size(); k++) {
                final String modelObjectKey = keys.get(k);
                final AbstractData childModelObject = this.getModelObject().getChild(modelObjectKey);
                this.createComponentModel((ActiveObject)childModelObject); 
                
                System.out.println("NodeController.update() in ADD_CHILDREN");
            } 
            break;
        } // [case ADD_CHILDREN]
        case REMOVE_CHILD: {
            final String figureKey = (String) mvcNotif.getData();
            final StandardToComponentsController childController = this.getChildControllerByKey(figureKey);
            if (childController == null) {
                return;
            }

            //TODO: to be implemented -> update the componentModel -> remove the child
            break;
        }
        default:
        	 System.out.println("NodeController.update() in Default");
        
    	
    	
    }
    
    }
    
    private void createComponentModel(ActiveObject childModelObject)
    {
    	
    	System.out.println("createComponentModel in NodeController");
    
		    NodeObject myNode = (NodeObject)this.getModelObject();	
		    final List<ObjectName> activeObjectNames = myNode.getProxyNodeMBean().getActiveObjects(); 	
		    
		    for (final ObjectName oname : activeObjectNames) {
		        final BodyWrapperMBean proxyBodyMBean = (BodyWrapperMBean) MBeanServerInvocationHandler
		                .newProxyInstance(myNode.getProActiveConnection(), oname, BodyWrapperMBean.class, false);
		        	   final UniqueID id = proxyBodyMBean.getID();
		               final String idString = id.toString();
		        	
		        	String activeObjectName = proxyBodyMBean.getName();
		            ComponentWrapperMBean proxyComponentMBean = (ComponentWrapperMBean) MBeanServerInvocationHandler
		      .newProxyInstance(myNode.getProActiveConnection(), oname, ComponentWrapperMBean.class, false);
		      try
		      {
		    	  ComponentModel Cchild = new ComponentModel(myNode,id,activeObjectName,oname,proxyComponentMBean);
		          Cchild.setName(activeObjectName);
		          
		          // using hashmap
		          if(! chm.components.containsKey(id))
		                chm.components.put(id, Cchild);
		          
		// componentmodels[index++] = Cchild;
		      }
		      catch(Exception e)
		      {
		      	e.printStackTrace();
		      }
		      
		       }// for
		      
		  
		
		  // re build the relation ship of these components
		// ComponentHierarchicalRebuild(componentmodels);
		        componentHierarchicalRebuild(chm.components);
    }
   
    
  private void componentHierarchicalRebuild(Map<UniqueID,ComponentModel> componentmodels)
  {
  	Set<UniqueID> keys = componentmodels.keySet();
  	for(UniqueID key : keys)
  	{
  		ComponentModel currentModel = componentmodels.get(key);
  		ComponentWrapperMBean proxyMBean = currentModel.getComponentWrapperMBean();
  		ProActiveComponent[] subComponents = proxyMBean.getSubComponents();
  		if(subComponents!=null)
  		{
  			addSubComponents(currentModel,subComponents,componentmodels);
  		}
  	}
  	for(UniqueID key : keys)
  	{
  		ComponentModel currentModel = componentmodels.get(key);
  		if(currentModel.getParent() instanceof NodeObject)
  		{
  			currentModel.setParent(chm);
  			chm.addChild(currentModel);
  		}
  	}
  	
//  	getWorldObject().CHolder = this.CHolder;
//  	showComponentHierachical(getWorldObject().CHolder);
  }

  
  
  private void addSubComponents(ComponentModel parent,ProActiveComponent[] subcomponents,Map<UniqueID,ComponentModel> componentmodels)
  {
  	for(ProActiveComponent child:subcomponents)
  	{
  		UniqueID childID = child.getID();
  		Set<UniqueID> keys = componentmodels.keySet();
      	for(UniqueID key : keys)
      	{
      		if(key.equals(childID))
      		{
      			//find one child.
      			//if this child is not primitive one, first add its children..
      			ComponentModel currentModel = componentmodels.get(key);
          		ComponentWrapperMBean proxyMBean = currentModel.getComponentWrapperMBean();
          		ProActiveComponent[] subsubComponents = proxyMBean.getSubComponents();
          		if(subsubComponents!=null)
          		{
          			addSubComponents(currentModel,subsubComponents,componentmodels);
          			currentModel.setHierachical("Composite");
          		}
          		else
          		{
          			currentModel.setHierachical("primitive");
          		}
          		//then add it to the parent
      			currentModel.setParent(parent);
      			parent.addChild(currentModel);
      			componentmodels.put(parent.getID(), parent);
      			componentmodels.put(currentModel.getID(), currentModel);
      			
      			//if this one used to be the root, now it has its parent, delete from CHolder
      			
      			chm.removeChild(currentModel);
          		
      			break;
      		}
      	}
  	}
  	
  	// after add all the subcomponents return;
  }

  private void showComponentHierachical(ComponentHolderModel CHolder)
  {
//  	System.out.println("[YYL Test OutPut:]"+"in NodeObject"+"this.CHolder has "+CHolder.getMonitoredChildrenSize()+" children");
  	List<AbstractData> childrens = CHolder.getMonitoredChildrenAsList();
  	if(childrens!=null)
  	{
  		for(AbstractData child:childrens)
  		{
  			ComponentModel tmpChild = (ComponentModel)child;
  			showComponent(tmpChild,CHolder.getName());
  		}
  	}
  }


    
  private void showComponent(ComponentModel model,String parent)
  {
  	System.out.println("[YYL Test OutPut:]"+"in NodeObject "+model.getName()+" parent ="+parent);
  	List<AbstractData> childrens = model.getMonitoredChildrenAsList();
  	if(childrens!=null)
  	{
  		for(AbstractData child:childrens)
  		{
  			ComponentModel tmpChild = (ComponentModel)child;
  			showComponent(tmpChild,model.getName());
  		}
  	}
  }

  
  public ComponentHolderModel getComponentHolderModel()
  {
  	return this.chm;
  }
    
    
    
    
}
