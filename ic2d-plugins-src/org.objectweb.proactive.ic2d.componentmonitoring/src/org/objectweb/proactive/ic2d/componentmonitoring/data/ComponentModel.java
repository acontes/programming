package org.objectweb.proactive.ic2d.componentmonitoring.data;

import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.jmx.mbean.ComponentWrapperMBean;
import org.objectweb.proactive.ic2d.componentmonitoring.data.listener.ComponentModelListener;
import org.objectweb.proactive.ic2d.componentmonitoring.util.ComponentMVCNotification;
import org.objectweb.proactive.ic2d.componentmonitoring.util.ComponentMVCNotificationTag;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.NodeObject;


public class ComponentModel extends AbstractData {

    private static String ObjectNameString = "org.objectweb.proactive.ic2d.componentmonitoring:type=Component";

    private AbstractData parent;

    /**
     * metric recorders
     */
    private long ComponentStartTime;

    private double SampleSize;
    private double ArrivalSampleCount;
    private double DepartureSampleCount;
    private double ServiceSampleCount;
    private long FirstArrivalSampleTime;
    private long FirstDepartureSampleTime;
    private long FirstServiceSampleTime;

    private long TimeSize;
    private double ArrivalTimeCount;
    private double DepartureTimeCount;
    private double ServiceTimeCount;
    private long FirstArrivalTimeTime;
    private long FirstDepartureTimeTime;
    private long FirstServiceTimeTime;

    private double TotalArrivalCount;
    private double TotalDepartureCount;
    private double TotalServiceCount;

    private Thread timerThread;
    /**
     * ID used to identify the active object globally, even in case of
     * migration.
     */
    private UniqueID id;

    private String ClassName = "";

    private String Hierachical = "";

    private String Status = "Stopped";

    private double mean_arrival_rate = -1;

    private double mean_departure_rate = -1;

    private double mean_service_rate = -1;

    private double sample_arrival_rate = -1;

    private double sample_departure_rate = -1;

    private double sample_service_rate = -1;

    private int sampleSize = 10;

    private double time_arrival_rate = -1;

    private double time_departure_rate = -1;

    private double time_service_rate = -1;

    // /** JMX Notification listener
    // * This listener will be subscribed to the JMXNotificationManager
    // * */
    private final NotificationListener listener;
    //    
    /**
     * Forwards methods in an MBean's management interface through the MBean
     * server to the BodyWrapperMBean.
     */
    private ComponentWrapperMBean proxyMBean;

    public ComponentModel(ComponentHolderModel parent, String ClassName) throws MalformedObjectNameException,
            NullPointerException {
        super(new ObjectName(ObjectNameString));
        this.parent = parent;
        this.ClassName = ClassName;
        this.parent.addChild(this);
        initialMetricRecorders();
        this.listener = new ComponentModelListener(this);
        startTimer();
    }

    public ComponentModel(ComponentModel parent, String ClassName) throws MalformedObjectNameException,
            NullPointerException {
        super(new ObjectName(ObjectNameString));
        this.parent = parent;
        this.ClassName = ClassName;
        this.parent.addChild(this);
        initialMetricRecorders();
        this.listener = new ComponentModelListener(this);
        startTimer();
    }

    public ComponentModel(NodeObject parent, UniqueID id, String ClassName, ObjectName objectName,
            ComponentWrapperMBean proxyMBean) throws MalformedObjectNameException, NullPointerException {
        super(objectName);
        this.parent = parent;
        this.ClassName = ClassName;

        /**
         * delete this because ComponentModel is the logic unit, do not depend on any NodeOject, Node Object is just used as a temp parent
         */
        //		if (this.parent != null)
        //			this.parent.addChild(this);
        this.id = id;
        initialMetricRecorders();
        // System.out.println("ComponentModel()");

        this.listener = new ComponentModelListener(this);

        this.proxyMBean = MBeanServerInvocationHandler.newProxyInstance(getProActiveConnection(),
                getObjectName(), ComponentWrapperMBean.class, false);

        if (this.proxyMBean instanceof ComponentWrapperMBean) {
            // System.out.println("[YYL Test Output:]"+"this.proxyMBean
            // instanceof BodyWrapperMBean");
            // System.out.println("[YYL Test Output:]"+"in ComponentModel Body
            // Name = "+this.proxyMBean.getName());
            // System.out.println("[YYL Test Output:]"+"in ComponentModel
            // ComponentName"+" "+this.proxyMBean.getComponentName());
            // System.out.println("[YYL Test Output:]"+"in ComponentModel is
            // Component?:"+this.proxyMBean.isComponent());
            // // System.out.println("[YYL Test Output:]"+"Node Url=
            // "+this.proxyMBean.getNodeUrl());
        } else {
            // System.out.println("[YYL Test Output:]"+"BodyWrapperMBean");
        }
        startTimer();
    }

    /**
     * Returns a JMX Notification listener.
     * @return a JMX Notification listener.
     */
    public NotificationListener getListener() {
        return this.listener;
    }

    @Override
    public void explore() {
        // TODO Auto-generated method stub
        findChildren();

    }

    @Override
    public String getKey() {
        // TODO Auto-generated method stub
        return getName();
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return ClassName;
    }

    @Override
    public AbstractData getParent() {
        // TODO Auto-generated method stub
        return parent;
    }

    @Override
    public String getType() {
        // TODO Auto-generated method stub
        return "Component";
    }

    @Override
    public void destroy() {
        getParent().removeChild(this);
    }

    /* get Methods */
    public UniqueID getID() {
        return this.id;
    }

    public String getHierachical() {
        return this.Hierachical;
    }

    public String getStatus() {
        return this.Status;
    }

    public double getMeanArrivalRate() {
        return this.mean_arrival_rate;
    }

    public double getMeanDepartureRate() {
        return this.mean_departure_rate;
    }

    public double getMeanServiceRate() {
        return this.mean_service_rate;
    }

    public double getSampleArrivalRate() {
        return this.sample_arrival_rate;
    }

    public double getSampleDepartureRate() {
        return this.sample_departure_rate;
    }

    public double getSampleServiceRate() {
        return this.sample_service_rate;
    }

    public double getTimeArrivalRate() {
        return this.time_arrival_rate;
    }

    public double getTimeDepartureRate() {
        return this.time_departure_rate;
    }

    public double getTimeServiceRate() {
        return this.time_service_rate;
    }

    public ComponentWrapperMBean getComponentWrapperMBean() {
        return this.proxyMBean;
    }

    /* Set Methods */
    /**
     * Change the current state
     * 
     * @param newState
     */
    public void setState(String newState) {
        //        if (newState.equals(this.Status)) {
        //            return;
        //        } else {
        //            this.Status = newState;
        //        }
        this.Status = newState;
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.STATE_CHANGED, this.Status));
    }

    public void setHierachical(String newHierachical) {
        if (newHierachical.equals(this.Hierachical)) {
            return;
        } else {
            this.Hierachical = newHierachical;
        }
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.HIERACHICAL_CHANGED,
            this.Hierachical));

    }

    public void setName(String newName) {
        if (newName.equals(this.ClassName)) {
            return;
        } else {
            this.ClassName = newName;
        }
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.NAME_CHANGED, this.ClassName));

    }

    public void setMeanArrivalRate(double value) {
        this.mean_arrival_rate = value;
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.MEAN_ARRIVAL_RATE_CHANGED,
            this.mean_arrival_rate));
    }

    public void setMeanDepartureRate(double value) {
        this.mean_departure_rate = value;
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.MEAN_DEPARTURE_RATE_CHANGED,
            this.mean_departure_rate));
    }

    public void setMeanServiceRate(double value) {
        this.mean_service_rate = value;
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.MEAN_SERVICE_RATE_CHANGED,
            this.mean_service_rate));
    }

    public void setSampleArrivalRate(double value) {
        this.sample_arrival_rate = value;
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.SAMPLE_ARRIVAL_RATE_CHANGED,
            this.sample_arrival_rate));
    }

    public void setSampleDepartureRate(double value) {
        this.sample_departure_rate = value;
        setChanged();
        notifyObservers(new ComponentMVCNotification(
            ComponentMVCNotificationTag.SAMPLE_DEPARTURE_RATE_CHANGED, this.sample_departure_rate));
    }

    public void setSampleServiceRate(double value) {
        this.sample_service_rate = value;
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.SAMPLE_SERVICE_RATE_CHANGED,
            this.sample_service_rate));
    }

    public void setTimeArrivalRate(double value) {
        this.time_arrival_rate = value;
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.TIME_ARRIVAL_RATE_CHANGED,
            this.time_arrival_rate));
    }

    public void setTimeDepartureRate(double value) {
        this.time_departure_rate = value;
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.TIME_DEPARTURE_RATE_CHANGED,
            this.time_departure_rate));
    }

    public void setTimeServiceRate(double value) {
        this.time_service_rate = value;
        setChanged();
        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.TIME_SERVICE_RATE_CHANGED,
            this.time_service_rate));
    }

    public void setParent(AbstractData parent) {
        this.parent = parent;
    }

    private void findChildren() {

    }

    /**
     * Adds a child to this object, and explore this one.
     * 
     * @param <T>
     * @param child
     *            The child to explore
     */
    @Override
    public synchronized void addChild(AbstractData child) {
        if (!this.monitoredChildren.containsKey(child.getKey())) {
            this.monitoredChildren.put(child.getKey(), child);
            setChanged();
            notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.ADD_CHILD, child
                    .getKey()));

            child.explore();
        }
    }

    /**
     * Deletes a child from all recorded data.
     * 
     * @param child
     *            The child to delete.
     */
    @Override
    public void removeChild(AbstractData child) {
        if (child == null) {
            return;
        }

        child.removeAllConnections();

        String key = child.getKey();
        monitoredChildren.remove(key);
        notMonitoredChildren.remove(key);
        setChanged();

        notifyObservers(new ComponentMVCNotification(ComponentMVCNotificationTag.REMOVE_CHILD, key));
    }

    /**
     * Operations on metric recorders
     *  private long ComponentStartTime;
    private double SampleSize;
    private long TimeSize;
    private double TotalArrivalCount;
     */
    private void initialMetricRecorders() {
        this.ComponentStartTime = System.currentTimeMillis();
        this.SampleSize = 5;
        this.TimeSize = 60 * 1000;
        this.TotalArrivalCount = 0;
        this.TotalDepartureCount = 0;
        this.TotalServiceCount = 0;

        this.ArrivalSampleCount = 0;
        this.DepartureSampleCount = 0;
        this.ServiceSampleCount = 0;
        this.FirstArrivalSampleTime = this.ComponentStartTime;
        this.FirstDepartureSampleTime = this.ComponentStartTime;
        this.FirstServiceSampleTime = this.ComponentStartTime;

        this.ArrivalTimeCount = 0;
        this.DepartureTimeCount = 0;
        this.ServiceTimeCount = 0;
        this.FirstArrivalTimeTime = this.ComponentStartTime;
        this.FirstDepartureTimeTime = this.ComponentStartTime;
        this.FirstServiceTimeTime = this.ComponentStartTime;

    }

    public void setComponentStartTime(long componentStartTime) {
        this.ComponentStartTime = componentStartTime;
    }

    public void setSampleSize(double SampleSize) {
        this.SampleSize = SampleSize;
    }

    public void setTimeSize(long TimeSize) {
        this.TimeSize = TimeSize;
    }

    public void setTotalArrivalCount(double TotalArrivalCount) {
        this.TotalArrivalCount = TotalArrivalCount;
    }

    public long getComponentStartTime() {
        return this.ComponentStartTime;
    }

    public double getSampleSize() {
        return this.SampleSize;
    }

    public long getTimeSize() {
        return this.TimeSize;
    }

    public double getTotalArrivalCount() {
        return this.TotalArrivalCount;
    }

    /**
     * add events
     */
    public void getRequestEvent(long time) {
        this.TotalArrivalCount++;
        this.ArrivalSampleCount++;
        // Sample Arrival rate
        if (this.ArrivalSampleCount == 1) {
            this.FirstArrivalSampleTime = time;
        } else if (this.ArrivalSampleCount == this.SampleSize) {
            long CurrentSampleTime = time - this.FirstArrivalSampleTime;
            double CurrentArrivalSampleRate = this.SampleSize / (double) (CurrentSampleTime / 1000);
            setSampleArrivalRate(CurrentArrivalSampleRate);
        } else if (this.ArrivalSampleCount == (this.sampleSize + 1)) {
            this.FirstArrivalSampleTime = time;
            this.ArrivalSampleCount = 1;
        }
        // Time arrival rate
        this.ArrivalTimeCount++;
        // Mean Arrival rate
        long CurrentLifeTime = time - this.ComponentStartTime;
        double CurrentMeanArrivalRate = this.TotalArrivalCount / (double) (CurrentLifeTime / 1000);
        setMeanArrivalRate(CurrentMeanArrivalRate);
    }

    public void getDepartureEvent(long time) {
        this.TotalDepartureCount++;
        this.DepartureSampleCount++;

        // 
        if (this.DepartureSampleCount == 1) {
            this.FirstDepartureSampleTime = time;
        } else if (this.DepartureSampleCount == this.SampleSize) {
            long CurrentSampleTime = time - this.FirstDepartureSampleTime;
            double CurrentDepartureSampleRate = this.SampleSize / (double) (CurrentSampleTime / 1000);
            setSampleDepartureRate(CurrentDepartureSampleRate);
        } else if (this.DepartureSampleCount == (this.sampleSize + 1)) {
            this.FirstDepartureSampleTime = time;
            this.DepartureSampleCount = 1;
        }
        // Time Departure rate
        this.DepartureTimeCount++;
        //
        long CurrentLifeTime = time - this.ComponentStartTime;
        double CurrentMeanDepartureRate = this.TotalDepartureCount / (double) (CurrentLifeTime / 1000);
        setMeanDepartureRate(CurrentMeanDepartureRate);
    }

    public void getServiceEvent(long time) {
        this.TotalServiceCount++;
        this.ServiceSampleCount++;

        // 
        if (this.ServiceSampleCount == 1) {
            this.FirstServiceSampleTime = time;
        } else if (this.ServiceSampleCount == this.SampleSize) {
            long CurrentSampleTime = time - this.FirstServiceSampleTime;
            double CurrentServiceSampleRate = this.SampleSize / (double) (CurrentSampleTime / 1000);
            setSampleServiceRate(CurrentServiceSampleRate);
        } else if (this.ServiceSampleCount == (this.sampleSize + 1)) {
            this.FirstServiceSampleTime = time;
            this.ServiceSampleCount = 1;
        }
        // Time Service rate 
        this.ServiceTimeCount++;
        //
        long CurrentLifeTime = time - this.ComponentStartTime;
        double CurrentMeanServiceRate = this.TotalServiceCount / (double) (CurrentLifeTime / 1000);
        setMeanServiceRate(CurrentMeanServiceRate);
    }

    private void startTimer() {
        Timer timer = new Timer(this.TimeSize);
        if (this.timerThread == null) {
            this.timerThread = new Thread(timer);
            this.timerThread.start();
        }
    }

    private class Timer implements Runnable {
        private long interval;

        public Timer() {
            this.interval = 0;
        }

        public Timer(long interval) {
            this.interval = interval;
        }

        public void setInterval(long interval) {
            this.interval = interval;
        }

        public long getInterval() {
            return this.interval;
        }

        public void run() {
            while (true) {
                timeMetricsUpdate(this.interval);
                resetTimeMetrics();
                try {
                    System.out.println("Sleep Begin!");
                    Thread.sleep(this.interval);
                    System.out.println("Now this.interval=" + this.interval + " and ArrivalTimeCount = " +
                        ArrivalTimeCount);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private void timeMetricsUpdate(long interval) {
            setTimeArrivalRate(ArrivalTimeCount / (interval / (double) 1000));
            setTimeDepartureRate(DepartureTimeCount / (interval / (double) 1000));
            setTimeServiceRate(ServiceTimeCount / (interval / (double) 1000));
        }

        private void resetTimeMetrics() {
            ArrivalTimeCount = 0;
            DepartureTimeCount = 0;
            ServiceTimeCount = 0;
        }
    }

}
