package org.objectweb.proactive.core.component.controller;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.proactive.core.util.wrapper.DoubleWrapper;

public class MethodStatistics implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private RateMetric arrMetric = new RateMetric("Arrivals rate");
    private RateMetric depMetric = new RateMetric("Departures rate");
        
    public MethodStatistics()
    {
    }
    
    public void reset()
    {
        arrMetric.reset();
        depMetric.reset();
    }
    
    public void recordArrival(long time)
    {
        arrMetric.record(time);
    }

    public void recordDeparture(long time)
    {
        depMetric.record(time);
    }

    public DoubleWrapper getMeanArrivalRate() 
    {
        return new DoubleWrapper(arrMetric.getMeanRate());
    }

    public DoubleWrapper getMeanDepartureRate() 
    {
        return new DoubleWrapper(depMetric.getMeanRate());
    }

    public DoubleWrapper getMeanServiceRate()
    {
        // NOTE: This result is unreliable; it makes sense only if the server has no idle time
        return getMeanDepartureRate();
    }

    public DoubleWrapper getSampleArrivalRate(int n)
    {
        return new DoubleWrapper(arrMetric.getSampleMeanRate(n));
    }

    public DoubleWrapper getSampleDepartureRate(int n)
    {
        return new DoubleWrapper(depMetric.getSampleMeanRate(n));
    }

    public DoubleWrapper getSampleServiceRate(int n)
    {
        // NOTE: This result is unreliable; it makes sense only if the server has no idle time
        return new DoubleWrapper(depMetric.getSampleMeanRate(n));
    }
    
    public DoubleWrapper getTimeArrivalRate(int millis)
    {
        return new DoubleWrapper(arrMetric.getTimeMeanRate(millis));
    }

    public DoubleWrapper getTimeDepartureRate(int millis)
    {
        return new DoubleWrapper(depMetric.getTimeMeanRate(millis));
    }

    public DoubleWrapper getTimeServiceRate(int millis)
    {
        // NOTE: This result is unreliable; it makes sense only if the server has no idle time
        return new DoubleWrapper(depMetric.getTimeMeanRate(millis));
    }
    
    class RateMetric implements Serializable
    {
		private static final long serialVersionUID = 1L;

		private String name;
        private List<Long> list = Collections.synchronizedList(new LinkedList<Long>());
        private long duration = 0; // in millis
        private int max_size = 1000;

        
        protected RateMetric()
        {
            this(null, 0);
        }

        protected RateMetric(String name) 
        {
            this(name, 0);
        }
        
        public RateMetric(String name, int max_size) 
        {
            this.name = name;
            if (max_size > 0)
                this.max_size = max_size;
        }
        
        public String getName()      { return name;    }
        public String getDimension() { return "1/sec"; }

        public void record(long time) 
        {
            if (list.size() > max_size)
                list.remove(0);
            list.add(time);
            duration = time - list.get(0);
            // System.err.println("****** " + name + " **** duration is " + duration);
        }

        public void reset() 
        {
            list.clear();
            duration = 0;
        }

        public long getCount() 
        {
            return list.size();
        }

        public double getDuration() 
        {
            return millisToSeconds(duration);
        }

        public double getRate()
        {
            int length = list.size();
            if (length < 2) return 0;
            return getSampleMeanRate(1);
        }

        public double getMeanRate() 
        {
            int length = list.size();
            if (length < 2) return 0;
            return (length - 1) / millisToSeconds(duration);
        }
        
        public double getSampleMeanRate(int num)
        {
            if (num < 1) return 0;
            int length = list.size();
            if (num >= length - 1)
                return getMeanRate();
            long dt  = list.get(length - 1) - list.get(length - 1 - num);
            return num / millisToSeconds(dt);
        }
        
        public double getTimeMeanRate(int millis)
        {
            long end = System.currentTimeMillis() - millis;
            int counter = 0;
            for (Long t: list) {
                if (t >= end)
                    counter++;
                else 
                    break;
            }
            return counter / millisToSeconds(millis);
        }
        
        private double millisToSeconds(long millis)
        {
            return (millis + 1000L/2)/(double)1000L;
        }

    }

}
