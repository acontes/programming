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
package org.objectweb.proactive.core.component.body;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.LifeCycleController;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.Active;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.body.MetaObjectFactory;
import org.objectweb.proactive.core.body.ProActiveMetaObjectFactory;
import org.objectweb.proactive.core.body.migration.MigratableBody;
import org.objectweb.proactive.core.component.ComponentParameters;
import org.objectweb.proactive.core.component.identity.ProActiveComponent;
import org.objectweb.proactive.core.component.identity.ProActiveComponentImpl;
import org.objectweb.proactive.core.component.request.Shortcut;
import org.objectweb.proactive.core.jmx.mbean.ComponentWrapper;
import org.objectweb.proactive.core.jmx.naming.FactoryName;
import org.objectweb.proactive.core.jmx.notification.NotificationType;
import org.objectweb.proactive.core.mop.ConstructorCallExecutionFailedException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;

import com.vladium.emma.runCommand;


/**
 * This class has been inserted into the bodies hierarchy in order to instantiate the
 * component metaobject (ProActiveComponent).
 */
public class ComponentBodyImpl extends MigratableBody implements ComponentBody {
	
	static final long serialVersionUID = -5412084782552441752L;
	
    private ProActiveComponent componentIdentity = null;
    private Map<String, Shortcut> shortcutsOnThis = null; // key = functionalItfName, value = shortcut
    private static Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS);
    private boolean insideFunctionalActivity = false;

//    private ComponentWrapper cmbean;
    
    public ComponentBodyImpl() {
        super();
    }

    /**
     * Constructor for ComponentBodyImpl.
     *
     * It creates the component metaobject only if the MetaObjectFactory is parameterized
     * with ComponentParameters (thus implicitly constructing components).
     *
     * It may also modify the activity to be compatible with the life cycle of the component and
     * the management of non functional invocations.
     * @param reifiedObject a reference on the reified object
     * @param nodeURL node url
     * @param factory factory for the corresponding metaobjects
     */
    public ComponentBodyImpl(Object reifiedObject, String nodeURL, Active activity,
            MetaObjectFactory factory, String jobID) throws java.lang.reflect.InvocationTargetException,
            ConstructorCallExecutionFailedException, ActiveObjectCreationException {
        super(reifiedObject, nodeURL, factory, jobID);
        //        filterOnNFRequests = new RequestFilterOnPrioritizedNFRequests();
        // create the component metaobject if necessary
        // --> check the value of the "parameters" field       
        Map<String, Object> factory_parameters = factory.getParameters();
        if ((null != factory_parameters)) {
            if (null != factory_parameters.get(ProActiveMetaObjectFactory.COMPONENT_PARAMETERS_KEY)) {
            	
                if (factory_parameters.get(ProActiveMetaObjectFactory.COMPONENT_PARAMETERS_KEY) instanceof ComponentParameters) {
                	
                    if (logger.isDebugEnabled()) {
                        logger.debug("creating metaobject component identity");
                    }
                    this.componentIdentity = factory.newComponentFactory().newProActiveComponent(this);

                    /**
                     * change the super.mbean into ComponentWrapperMBean if this is a component
                     */
                 // JMX registration  
                    
                    
                   
                        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                        System.out.println("ComponentBodyImpl.ComponentBodyImpl() -> mbs has "+mbs.getMBeanCount()+" mbean before register");
                        ObjectName oname = FactoryName.createActiveObjectName(this.bodyID);
                        if (!mbs.isRegistered(oname)) {
                        	
//                        	System.out.println("ComponentBodyImpl.ComponentBodyImpl() -> mbs does not have oname:"+oname.toString());
//                            this.cmbean = new ComponentWrapper(oname, this);
                        	super.mbean = new ComponentWrapper(oname, this);
                            try {
//                                mbs.registerMBean(cmbean, oname);
                                mbs.registerMBean(super.mbean, oname);
                            } catch (InstanceAlreadyExistsException e) {
                                bodyLogger.error("A MBean with the object name " + oname + " already exists", e);
                            } catch (MBeanRegistrationException e) {
                                bodyLogger.error("Can't register the MBean of the body", e);
                            } catch (NotCompliantMBeanException e) {
                                bodyLogger.error("The MBean of the body is not JMX compliant", e);
                            }
                        }
                        else
                        {
                        	try
                        	{
                        		mbs.unregisterMBean(oname);
                        	}catch(InstanceNotFoundException e)
                        	{
                        		bodyLogger.error("The specified MBean does not exist in the repository. The specified MBean does not exist in the repository.", e);
                        	}catch (MBeanRegistrationException e) {
                                bodyLogger.error("Can't register the MBean of the body", e);
                            }
                        	
//                        	System.out.println("ComponentBodyImpl.ComponentBodyImpl() -> mbs has already has oname:"+oname.toString());
                        	
//                        	this.cmbean = new ComponentWrapper(oname, this);
                        	super.mbean = new ComponentWrapper(oname, this);
                            try {
//                                mbs.registerMBean(cmbean, oname);
                            	mbs.registerMBean(super.mbean, oname);
                            } catch (InstanceAlreadyExistsException e) {
                                bodyLogger.error("A MBean with the object name " + oname + " already exists", e);
                            } catch (MBeanRegistrationException e) {
                                bodyLogger.error("Can't register the MBean of the body", e);
                            } catch (NotCompliantMBeanException e) {
                                bodyLogger.error("The MBean of the body is not JMX compliant", e);
                            }
                        	
                        }
                        
//                        System.out.println("ComponentBodyImpl.ComponentBodyImpl() -> mbs has "+mbs.getMBeanCount()+" mbean after register");
                    
                    
                    // change activity into a component activity
                    // activity = new ComponentActivity(activity, reifiedObject);
                        
//                        new Thread(){
//                        	public void run(){
//                        		while(true)
//                        		{
//                        		try {
//                					Thread.sleep(3000);
//                				} catch (InterruptedException e1) {
//                					// TODO Auto-generated catch block
//                					e1.printStackTrace();
//                				}
////                        		cmbean.sendNotification(NotificationType.waitForRequest);
////                        		System.out.println("ComponentBodyImpl.ComponentBodyImpl() cmbean"+cmbean.toString()+" send Notification->" +NotificationType.waitForRequest);
//                        		
//                				mbean.sendNotification(NotificationType.waitForRequest+" in ComponentBodyImpl");
//                        		System.out.println("ComponentBodyImpl.ComponentBodyImpl() cmbean"+mbean.toString()+" send Notification->" +NotificationType.waitForRequest);
//                        		
//                        	}
//                        	}
//                        }.start();
                        
                        
                        
                        
                } else {
                    logger
                            .error("component parameters for the components factory are not of type ComponentParameters");
                }
                
                
                
            }
        }
        
        
        
    }

    /**
     * Returns the a reference on the Component meta object
     * @return the ProActiveComponent meta-object
     */
    public ProActiveComponentImpl getProActiveComponentImpl() {
        return (ProActiveComponentImpl) this.componentIdentity;
    }

    /**
     * overrides the @link{Body#isActive()} method :
     * when the process flow is inside a functional activity of a component,
     * isActive corresponds to the started state in the lifecycle of the component, while
     * !isActive corresponds to the stopped state.
     * If the process flow is outside of the functional activity of a component, then return the
     * default result for isActive() (unoverriden)
     *
     */
    @Override
    public boolean isActive() {
        if (this.insideFunctionalActivity) {
            try {
                return LifeCycleController.STARTED.equals(Fractal.getLifeCycleController(
                        getProActiveComponentImpl()).getFcState());
            } catch (NoSuchInterfaceException e) {
                logger.error("could not find the life cycle controller of this component");
                return false;
            }
        } else {
            return super.isActive();
        }
    }

    /*
     * @see org.objectweb.proactive.core.component.body.ComponentBody#isComponent()
     */
    public boolean isComponent() {
        return (getProActiveComponentImpl() != null);
    }

    /*
     * @see org.objectweb.proactive.core.component.body.ComponentBody#finishedFunctionalActivity()
     */
    public void finishedFunctionalActivity() {
        this.insideFunctionalActivity = false;
    }

    /*
     * @see org.objectweb.proactive.core.component.body.ComponentBody#startingFunctionalActivity()
     */
    public void startingFunctionalActivity() {
        this.insideFunctionalActivity = true;
    }

    public void keepShortcut(Shortcut shortcut) {
        if (this.shortcutsOnThis == null) {
            this.shortcutsOnThis = new HashMap<String, Shortcut>();
        }
        this.shortcutsOnThis.put(shortcut.getFcFunctionalInterfaceName(), shortcut);
    }
}
