package org.objectweb.proactive.ic2d.jmxmonitoring.data;

import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.jmx.mbean.BodyWrapperMBean;
import org.objectweb.proactive.core.jmx.mbean.ComponentWrapperMBean;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.ComponentMVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.ComponentMVCNotificationTag;


public class ComponentModel extends AbstractData
{

	private static String ObjectNameString = "org.objectweb.proactive.ic2d.componentmonitoring:type=Component";

	private AbstractData parent;
	
	 /** ID used to identify the active object globally, even in case of migration. */
    private UniqueID id;

	private String ClassName = "";

	private String Hierachical = "";

	private String Status = "";

	private double mean_arrival_rate = -1;

	private double mean_departure_rate = -1;

	private double mean_service_rate = -1;

	private double sample_arrival_rate = -1;

	private double sample_departure_rate = -1;

	private double sample_service_rate = -1;

	private double time_arrival_rate = -1;

	private double time_departure_rate = -1;

	private double time_service_rate = -1;

	//    /** JMX Notification listener
	//     *  This listener will be subscribed to the JMXNotificationManager
	//     * */
	//    private final NotificationListener listener;
	//    
	    /** Forwards methods in an MBean's management interface through the MBean server to the BodyWrapperMBean. */
	    private ComponentWrapperMBean proxyMBean;

//	public ComponentModel(ComponentHolderModel parent, String ClassName)
//			throws MalformedObjectNameException, NullPointerException
//	{
//		super(new ObjectName(ObjectNameString));
//		this.parent = parent;
//		this.ClassName = ClassName;
//		this.parent.addChild(this);
//
//	}

//	public ComponentModel(ComponentModel parent, String ClassName)
//			throws MalformedObjectNameException, NullPointerException
//	{
//		super(new ObjectName(ObjectNameString));
//		this.parent = parent;
//		this.ClassName = ClassName;
//		this.parent.addChild(this);
//	}

	
	public ComponentModel(NodeObject parent,UniqueID id, String ClassName,ObjectName objectName,
			ComponentWrapperMBean proxyMBean)
			throws MalformedObjectNameException, NullPointerException
	{
		super(objectName);
		this.parent = parent;
		this.ClassName = ClassName;
		if(this.parent!=null)
		this.parent.addChild(this);
		
        this.id = id;
        this.ClassName = ClassName;
        
        System.out.println("ComponentModel()");

//        this.listener = new ActiveObjectListener(this);
        
        this.proxyMBean = MBeanServerInvocationHandler.newProxyInstance(
                getProActiveConnection(), getObjectName(), ComponentWrapperMBean.class, false);
        
        
        
        
        if( this.proxyMBean instanceof ComponentWrapperMBean)
        {
//        	System.out.println("[YYL Test Output:]"+"this.proxyMBean instanceof BodyWrapperMBean");
        	System.out.println("[YYL Test Output:]"+"in ComponentModel Body Name = "+this.proxyMBean.getName());
        	System.out.println("[YYL Test Output:]"+"in ComponentModel ComponentName"+" "+this.proxyMBean.getComponentName());
        	System.out.println("[YYL Test Output:]"+"in ComponentModel is Component?:"+this.proxyMBean.isComponent());
//        	System.out.println("[YYL Test Output:]"+"Node Url= "+this.proxyMBean.getNodeUrl());
        }
        else
        {
        	System.out.println("[YYL Test Output:]"+"BodyWrapperMBean");
        }
		
	}
	
	
	@Override
	public void explore()
	{
		// TODO Auto-generated method stub
		findChildren();

	}

	@Override
	public String getKey()
	{
		// TODO Auto-generated method stub
		return getName();
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return ClassName;
	}

	@Override
	public AbstractData getParent()
	{
		// TODO Auto-generated method stub
		return parent;
	}

	@Override
	public String getType()
	{
		// TODO Auto-generated method stub
		return "Component";
	}

	@Override
	public void destroy()
	{
		getParent().removeChild(this);
	}

	/* get Methods*/
	public String getHierachical()
	{
		return this.Hierachical;
	}

	public String getStatus()
	{
		return this.Status;
	}

	public double getMeanArrivalRate()
	{
		return this.mean_arrival_rate;
	}

	public double getMeanDepartureRate()
	{
		return this.mean_departure_rate;
	}

	public double getMeanServiceRate()
	{
		return this.mean_service_rate;
	}

	public double getSampleArrivalRate()
	{
		return this.sample_arrival_rate;
	}

	public double getSampleDepartureRate()
	{
		return this.sample_departure_rate;
	}

	public double getSampleServiceRate()
	{
		return this.sample_service_rate;
	}

	public double getTimeArrivalRate()
	{
		return this.time_arrival_rate;
	}

	public double getTimeDepartureRate()
	{
		return this.time_departure_rate;
	}

	public double getTimeServiceRate()
	{
		return this.time_service_rate;
	}
	
	public ComponentWrapperMBean getComponentWrapperMBean()
	{
		return this.proxyMBean;
	}

	/* Set Methods*/
	/**
	 * Change the current state
	 * @param newState
	 */
	public void setState(String newState)
	{
		if (newState.equals(this.Status))
		{
			return;
		}
		else
		{
			this.Status = newState;
		}
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.STATE_CHANGED, this.Status));
	}

	public void setHierachical(String newHierachical)
	{
		if (newHierachical.equals(this.Hierachical))
		{
			return;
		}
		else
		{
			this.Hierachical = newHierachical;
		}
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.HIERACHICAL_CHANGED,
				this.Hierachical));

	}

	public void setName(String newName)
	{
		if (newName.equals(this.ClassName))
		{
			return;
		}
		else
		{
			this.ClassName = newName;
		}
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.NAME_CHANGED, this.ClassName));

	}

	public void setMeanArrivalRate(double value)
	{
		this.mean_arrival_rate = value;
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.MEAN_ARRIVAL_RATE_CHANGED,
				this.mean_arrival_rate));
	}

	public void setMeanDepartureRate(double value)
	{
		this.mean_departure_rate = value;
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.MEAN_DEPARTURE_RATE_CHANGED,
				this.mean_departure_rate));
	}

	public void setMeanServiceRate(double value)
	{
		this.mean_service_rate = value;
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.MEAN_SERVICE_RATE_CHANGED,
				this.mean_service_rate));
	}

	public void setSampleArrivalRate(double value)
	{
		this.sample_arrival_rate = value;
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.SAMPLE_ARRIVAL_RATE_CHANGED,
				this.sample_arrival_rate));
	}

	public void setSampleDepartureRate(double value)
	{
		this.sample_departure_rate = value;
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.SAMPLE_DEPARTURE_RATE_CHANGED,
				this.sample_departure_rate));
	}

	public void setSampleServiceRate(double value)
	{
		this.sample_service_rate = value;
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.SAMPLE_SERVICE_RATE_CHANGED,
				this.sample_service_rate));
	}

	public void setTimeArrivalRate(double value)
	{
		this.time_arrival_rate = value;
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.TIME_ARRIVAL_RATE_CHANGED,
				this.time_arrival_rate));
	}

	public void setTimeDepartureRate(double value)
	{
		this.time_departure_rate = value;
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.TIME_DEPARTURE_RATE_CHANGED,
				this.time_departure_rate));
	}

	public void setTimeServiceRate(double value)
	{
		this.time_service_rate = value;
		setChanged();
		notifyObservers(new ComponentMVCNotification(
				ComponentMVCNotificationTag.TIME_SERVICE_RATE_CHANGED,
				this.time_service_rate));
	}
	
	public void setParent(AbstractData parent)
	{
		this.parent = parent;
	}

	private void findChildren()
	{

	}
}
