/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean;
import org.objectweb.proactive.core.jmx.mbean.ComponentWrapperMBean;
import org.objectweb.proactive.core.jmx.mbean.NodeWrapperMBean;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.jmx.util.JMXNotificationManager;
import org.objectweb.proactive.core.util.URIBuilder;
import org.objectweb.proactive.ic2d.jmxmonitoring.Activator;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;


public class NodeObject extends AbstractData {
    private final RuntimeObject parent;
    private final VirtualNodeObject vnParent;
    private final String url;
    private ComponentHolderModel CHolder;

    // Warning: Don't use this variavle directly, use getProxyNodeMBean().
    private NodeWrapperMBean proxyNodeMBean;

    public NodeObject(final RuntimeObject parent, final String url, final ObjectName objectName,
            final VirtualNodeObject vnParent) {
        // Call super contructor in order to specify a TreeMap<String,
        // AbstractData> for monitored children
        super(objectName, new TreeMap<String, AbstractData>(new ActiveObject.ActiveObjectComparator()));
        this.parent = parent;
        this.vnParent = vnParent;
        this.url = FactoryName.getCompleteUrl(url);
      
        /*
         * initial the component Holder Model
         */
        this.CHolder = getWorldObject().CHolder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RuntimeObject getParent() {
        return this.parent;
    }

    /**
     * Returns the virtual node.
     * 
     * @return the virtual node.
     */
    public VirtualNodeObject getVirtualNode() {
        return this.vnParent;
    }

    /**
     * Gets a proxy for the MBean representing this Node. If the proxy does not
     * exist it creates it
     * 
     * @return
     */
    private NodeWrapperMBean getProxyNodeMBean() {
        if (proxyNodeMBean == null) {
            proxyNodeMBean = (NodeWrapperMBean) MBeanServerInvocationHandler.newProxyInstance(
                    getProActiveConnection(), getObjectName(), NodeWrapperMBean.class, false);
        }
        return proxyNodeMBean;
    }

    public void setProxyNodeMBean(NodeWrapperMBean proxyNodeMBean) {
        this.proxyNodeMBean = proxyNodeMBean;
    }

    @Override
    public void destroy() {
        for (final AbstractData child : this.getMonitoredChildrenAsList()) {
            child.destroy();
        }

        this.vnParent.removeChild(this);
        super.destroy();
    }

    @Override
    public synchronized void explore() {
        this.findActiveObjects();
    }

    @Override
    public String getKey() {
        return this.url;
    }

    @Override
    public String getType() {
        return "node object";
    }

    /**
     * Returns the url of this object.
     * 
     * @return An url.
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Finds all active objects of this node.
     */
    private void findActiveObjects() {
        List<ActiveObject> childrentoAdd = new ArrayList<ActiveObject>();
        final Map<String, AbstractData> childrenToRemoved = this.getMonitoredChildrenAsMap();

        final List<ObjectName> activeObjectNames = getProxyNodeMBean().getActiveObjects();
        
//        ComponentModel[] componentmodels = new ComponentModel[activeObjectNames.size()]; 
        int index = 0;
        
        for (final ObjectName oname : activeObjectNames) {
            final BodyWrapperMBean proxyBodyMBean = (BodyWrapperMBean) MBeanServerInvocationHandler
                    .newProxyInstance(getProActiveConnection(), oname, BodyWrapperMBean.class, false);

//            System.out.println("[YYL Test OutPut:]"+"in NodeObject "+" begin to create component model 2");
            
            
            // Since the id is already contained as a String in the ObjectName
            // this call can be avoid if the UniqueID can be built from a string
            final UniqueID id = proxyBodyMBean.getID();
            final String idString = id.toString();

            // If this child is a NOT monitored child.
            if (containsChildInNOTMonitoredChildren(idString)) {
                continue;
            }
            ActiveObject child = (ActiveObject) this.getMonitoredChild(idString);

            // If this child is not yet monitored.
            if (child == null) {
                // Get the name of the active object
                final String activeObjectName = proxyBodyMBean.getName();
                child = new ActiveObject(this, id, activeObjectName, oname, proxyBodyMBean);
                childrentoAdd.add(child);
                
                
                
               
                
            }

            // Removes from the model the not monitored or termined aos.
            childrenToRemoved.remove(idString);
            
            
            
           /**
             * create component model here
//             */
            String activeObjectName = proxyBodyMBean.getName();
            ComponentWrapperMBean proxyComponentMBean = (ComponentWrapperMBean) MBeanServerInvocationHandler
            .newProxyInstance(getProActiveConnection(), oname, ComponentWrapperMBean.class, false);
            try
            {
                ComponentModel Cchild = new ComponentModel(this,id,activeObjectName,oname,proxyComponentMBean);
                
                
                Cchild.setName(activeObjectName);
                
                //using hashmap
                if(! getWorldObject().components.containsKey(id))
                      getWorldObject().components.put(id, Cchild);
                
//                componentmodels[index++] = Cchild;
            }
            catch(Exception e)
            {
            	e.printStackTrace();
            }
            
            
        }

        // re build the relation ship of these components
//        ComponentHierarchicalRebuild(componentmodels);
        ComponentHierarchicalRebuild(getWorldObject().components);
        
        // add all children
        this.addChildren(childrentoAdd);
        // Some child have to be removed
        for (final AbstractData child : childrenToRemoved.values()) {
            org.objectweb.proactive.ic2d.console.Console.getInstance(Activator.CONSOLE_NAME).log(
                    "Active object " + child.getName() + " is no longer visible");
            if(child instanceof ActiveObject)
            ((ActiveObject) child).stopMonitoring(true); // unsubscribes
            // listener for this
            // child object
        }
        
        
    }

    @Override
    public String getName() {
        return URIBuilder.getNameFromURI(this.url);
    }

    @Override
    public String toString() {
        return "Node: " + this.url;
    }

    public void addChild(final ActiveObject child) {
        super.addChild(child);
        try {
            JMXNotificationManager.getInstance().subscribe(child.objectName, child.getListener(),
                    this.parent.getUrl());
        } catch (IOException e) {
            System.out.println("NodeObject: could not add child: " + child.getName());
            e.printStackTrace();

        }
    }

    private synchronized void addChildren(final List<ActiveObject> children) {
        final ArrayList<String> childrenKeys = new ArrayList<String>();
        for (final ActiveObject child : children) {
            if (!this.monitoredChildren.containsKey(child.getKey())) {
                this.monitoredChildren.put(child.getKey(), child);
                childrenKeys.add(child.getKey());
                ObjectName oname = child.getObjectName();
                try {
                    JMXNotificationManager.getInstance().subscribe(oname, child.getListener(),
                            getParent().getUrl());
                } catch (IOException e) {
                    System.out.println("NodeObject.addChildren(): could not add child: " + child.getName());
                    e.printStackTrace();
                }
            }
        }
        setChanged();
        notifyObservers(new MVCNotification(MVCNotificationTag.ADD_CHILDREN, childrenKeys));
    }

    /**
     * Returns the virtual node name.
     * 
     * @return the virtual node name.
     */
    public String getVirtualNodeName() {
        return this.vnParent.getName();
        // return getProxyNodeMBean().getVirtualNodeName();
    }

    /**
     * Returns the Job Id.
     * 
     * @return the Job Id.
     */
    public String getJobId() {
        return this.vnParent.getJobID();
    }

    /**
     * Used to highlight this node, in a virtual node.
     * 
     * @param highlighted
     *            true, or false
     */
    public void setHighlight(boolean highlighted) {
        this.setChanged();
        if (highlighted) {
            this.notifyObservers(new MVCNotification(MVCNotificationTag.STATE_CHANGED, State.HIGHLIGHTED));
        } else {
            this
                    .notifyObservers(new MVCNotification(MVCNotificationTag.STATE_CHANGED,
                        State.NOT_HIGHLIGHTED));
        }
    }

    public void notifyChanged() {
        this.setChanged();
        this.notifyObservers(null);
    }

    private void ComponentHierarchicalRebuild(Map<UniqueID,ComponentModel> componentmodels)
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
    			currentModel.setParent(this.CHolder);
    			this.CHolder.addChild(currentModel);
    		}
    	}
    	
    	getWorldObject().CHolder = this.CHolder;
//    	showComponentHierachical(getWorldObject().CHolder);
    }
    
    
    
    
    private void ComponentHierarchicalRebuild(ComponentModel[] componentmodels)
    {
    	for(int i=0;i<componentmodels.length;i++)
    	{
    		
    		ComponentModel currentModel = componentmodels[i];
    		ComponentWrapperMBean proxyMBean = currentModel.getComponentWrapperMBean();
    		ProActiveComponent[] subComponents = proxyMBean.getSubComponents();
    		
            if(subComponents!=null)
    		{
    			addSubComponents(currentModel,subComponents,componentmodels);
    			currentModel.setHierachical("Composite");
    		}
            else
            {
            	currentModel.setHierachical("primitive");
    		}
    	}
    	  	
    	for(ComponentModel currentModel:componentmodels)
    	{
    		if(currentModel.getParent() instanceof NodeObject)
    		{
    			currentModel.setParent(this.CHolder);
    			this.CHolder.addChild(currentModel);
    		}
    	}
    	
    	getWorldObject().CHolder = this.CHolder;
    	showComponentHierachical(getWorldObject().CHolder);
    	
//    	AbstractData parent = getParent();
//    	while(!(parent instanceof WorldObject))
//    		parent = parent.getParent();
//    	parent.addChild(this.CHolder);
//    	
    	
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
        			
        			getWorldObject().CHolder.monitoredChildren.remove(currentModel.getKey());
            		
        			break;
        		}
        	}
    	}
    	
    	// after add all the subcomponents return;
    }
    
    private void addSubComponents(ComponentModel parent,ProActiveComponent[] subcomponents,ComponentModel[] componentmodels)
    {
    	for(ProActiveComponent child:subcomponents)
    	{
    		UniqueID childID = child.getID();
    		for(ComponentModel CM:componentmodels)
    		{
    			if(CM.getComponentWrapperMBean().getID().equals(childID))
    			{
    				CM.setParent(parent);
    				parent.addChild(CM);
    				break;
    			}
    		}
    	}
    }
    
    private void showComponentHierachical(ComponentHolderModel CHolder)
    {
//    	System.out.println("[YYL Test OutPut:]"+"in NodeObject"+"this.CHolder has "+CHolder.getMonitoredChildrenSize()+" children");
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
    	return this.CHolder;
    }
   
}
