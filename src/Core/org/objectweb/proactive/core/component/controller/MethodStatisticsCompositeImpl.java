package org.objectweb.proactive.core.component.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;


public class MethodStatisticsCompositeImpl extends MethodStatisticsAbstract implements Serializable {
    private MonitorController monitorSubcomponent;
    
    public MethodStatisticsCompositeImpl(String itfName, String methodName, Class<?>[] parametersTypes, MonitorController monitorSubcomponent) {
        this.itfName = itfName;
        this.methodName = methodName;
        this.parametersTypes = parametersTypes;
        this.monitorSubcomponent = monitorSubcomponent;
        this.requestsStats = Collections.synchronizedList(new ArrayList<RequestStatistics>());
        reset();
    }

    public long getLatestServiceTime() {
        return monitorSubcomponent.getStatistics(itfName, methodName, parametersTypes).getLatestServiceTime();
    }

    public double getAverageServiceTime() {
        return monitorSubcomponent.getStatistics(itfName, methodName, parametersTypes).getAverageServiceTime();
    }

    public double getAverageServiceTime(int lastNRequest) {
        if (lastNRequest != 0) {
            return monitorSubcomponent.getStatistics(itfName, methodName, parametersTypes).getAverageServiceTime(lastNRequest);
        } else
            return 0;
    }

    public double getAverageServiceTime(long pastXMilliseconds) {
        return monitorSubcomponent.getStatistics(itfName, methodName, parametersTypes).getAverageServiceTime(pastXMilliseconds);
    }
}