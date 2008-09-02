package org.objectweb.proactive.core.component.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MethodStatisticsImpl implements MethodStatistics, Serializable {
    private String itfName;

    private String methodName;

    private Class<?>[] parametersTypes;

    private List<RequestStatistics> requestsStats;

    private long startTime;

    private int indexNextDepartureRequest;

    private int indexNextReply;

    private int currentLengthQueue;

    public MethodStatisticsImpl(String itfName, String methodName, Class<?>[] parametersTypes) {
        this.itfName = itfName;
        this.methodName = methodName;
        this.parametersTypes = parametersTypes;
        this.requestsStats = Collections.synchronizedList(new ArrayList<RequestStatistics>());
        this.startTime = System.currentTimeMillis();
        this.indexNextDepartureRequest = 0;
        this.indexNextReply = 0;
        this.currentLengthQueue = 0;
    }

    /*
     * Reset all the statistics for the monitored method.
     */
    public void reset() {
        requestsStats.clear();
        startTime = System.currentTimeMillis();
        indexNextDepartureRequest = 0;
        indexNextReply = 0;
        currentLengthQueue = 0;
    }

    // TODO Use this method
    private void clean() {
        int shift = requestsStats.size() - maxNbRequests;
        if (shift > 0) {
            requestsStats = requestsStats.subList(shift, requestsStats.size() - 1);
            indexNextDepartureRequest = indexNextDepartureRequest - shift;
            if (indexNextDepartureRequest < 0)
                indexNextDepartureRequest = 0;
            indexNextReply = indexNextReply - shift;
            if (indexNextReply < 0)
                indexNextReply = 0;
        }
    }

    /*
     * Notify the arrival of a new request in the incoming queue related to the monitored method.
     * 
     * @param time Time of the arrival of the request.
     */
    public void notifyArrivalOfRequest(long arrivalTime) {
        if (!requestsStats.isEmpty()) {
            requestsStats.add(new RequestStatistics(arrivalTime, requestsStats.get(requestsStats.size() - 1)
                    .getArrivalTime()));
        } else
            requestsStats.add(new RequestStatistics(arrivalTime, 0));
        currentLengthQueue++;
    }

    /*
     * Notify the departure of a request in the incoming queue related to the monitored method.
     * 
     * @param time Time of the departure of the request.
     */
    public void notifyDepartureOfRequest(long departureTime) {
        requestsStats.get(indexNextDepartureRequest).setDepartureTime(departureTime);
        indexNextDepartureRequest++;
        currentLengthQueue--;
    }

    /*
     * Notify that the reply to a request related to the monitored method has been sent.
     * 
     * @param time Time of the reply to a request has been sent.
     */
    public void notifyReplyOfRequestSent(long replyTime) {
        requestsStats.get(indexNextReply).setReplyTime(replyTime);
        indexNextReply++;
    }

    private int findNumberOfRequests(long time, int indexToStart) {
        long currentTime = System.currentTimeMillis();
        for (int i = indexToStart - 1; i >= 0; i--) {
            if ((currentTime - requestsStats.get(i).getArrivalTime()) > time)
                return indexToStart - (i + 1);
        }

        return indexToStart;
    }

    public int getCurrentLengthQueue() {
        return currentLengthQueue;
    }

    public double getAverageLengthQueue() {
        return getAverageLengthQueue(System.currentTimeMillis() - startTime);
    }

    public double getAverageLengthQueue(long pastXMilliseconds) {
        // TODO Is the correct definition of the average length of the queue?
        double div = pastXMilliseconds / 1000;
        if (div == 0)
            div = 1;
        return findNumberOfRequests(pastXMilliseconds, requestsStats.size()) / div;
    }

    public long getLatestServiceTime() {
        return requestsStats.get(indexNextReply - 1).getServiceTime();
    }

    public double getAverageServiceTime() {
        return getAverageServiceTime(indexNextReply);
    }

    public double getAverageServiceTime(int lastNRequest) {
        if (lastNRequest != 0) {
            double res = 0;
            int indexToReach = Math.max(indexNextReply - 1 - lastNRequest, 0);
            for (int i = indexNextReply - 1; i > indexToReach; i--) {
                res += requestsStats.get(i).getServiceTime();
            }

            return res / lastNRequest;
        } else
            return 0;
    }

    public double getAverageServiceTime(long pastXMilliseconds) {
        return getAverageServiceTime(findNumberOfRequests(pastXMilliseconds, indexNextReply));
    }

    public long getLatestInterArrivalTime() {
        return requestsStats.get(requestsStats.size() - 1).getInterArrivalTime();
    }

    public double getAverageInterArrivalTime() {
        return getAverageInterArrivalTime(requestsStats.size());
    }

    public double getAverageInterArrivalTime(int lastNRequest) {
        if (lastNRequest != 0) {
            double res = 0;
            int indexToReach = Math.max(requestsStats.size() - 1 - lastNRequest, 0);
            for (int i = requestsStats.size() - 1; i > indexToReach; i--) {
                res += requestsStats.get(i).getInterArrivalTime();
            }

            return res / lastNRequest;
        } else
            return 0;
    }

    public double getAverageInterArrivalTime(long pastXMilliseconds) {
        return getAverageInterArrivalTime(findNumberOfRequests(pastXMilliseconds, requestsStats.size()));
    }

    public double getAveragePermanenceTimeInQueue() {
        return getAveragePermanenceTimeInQueue(indexNextDepartureRequest);
    }

    public double getAveragePermanenceTimeInQueue(int lastNRequest) {
        if (lastNRequest != 0) {
            double res = 0;
            int indexToReach = Math.max(indexNextDepartureRequest - 1 - lastNRequest, 0);
            for (int i = indexNextDepartureRequest - 1; i > indexToReach; i--) {
                res += requestsStats.get(i).getPermanenceTimeInQueue();
            }

            return res / lastNRequest;
        } else
            return 0;
    }

    public double getAveragePermanenceTimeInQueue(long pastXMilliseconds) {
        return getAveragePermanenceTimeInQueue(findNumberOfRequests(pastXMilliseconds,
                indexNextDepartureRequest));
    }

    public List<String> getInvokedMethodList() {
        // TODO Complete this method
        return null;
    }

    public String toString() {
        String res = "Average statistics for the method " + methodName + "(";
        int nbParameters = parametersTypes.length;
        for (int i = 0; i < nbParameters; i++) {
            res += parametersTypes[i].getName();
            if (i + 1 < nbParameters)
                res += ", ";
        }
        res += ") of the interface " + itfName + ":\n";
        res += "Average length of the queue: " + getAverageLengthQueue() + "\n";
        res += "Average service time: " + getAverageServiceTime() + "\n";
        res += "Average inter-arrival time: " + getAverageInterArrivalTime() + "\n";
        res += "Average permanence time in queue: " + getAveragePermanenceTimeInQueue() + "\n";

        return res;
    }

    class RequestStatistics implements Serializable {
        private long arrivalTime;

        private long departureTime;

        private long replyTime;

        private long interArrivalTime;

        private long serviceTime;

        private long permanenceTimeInQueue;

        public RequestStatistics(long arrivalTime, long prevArrivalTime) {
            this.arrivalTime = arrivalTime;
            this.departureTime = 0;
            this.replyTime = 0;
            if (prevArrivalTime != 0)
                this.interArrivalTime = arrivalTime - prevArrivalTime;
            else
                this.interArrivalTime = 0;
            this.serviceTime = 0;
            this.permanenceTimeInQueue = 0;
        }

        public long getArrivalTime() {
            return arrivalTime;
        }

        public void setDepartureTime(long departureTime) {
            this.departureTime = departureTime;
            this.permanenceTimeInQueue = this.departureTime - this.arrivalTime;
        }

        public long getDepartureTime() {
            return departureTime;
        }

        public void setReplyTime(long replyTime) {
            this.replyTime = replyTime;
            this.serviceTime = this.replyTime - this.departureTime;
        }

        public long getReplyTime() {
            return replyTime;
        }

        public long getInterArrivalTime() {
            return interArrivalTime;
        }

        public long getServiceTime() {
            return serviceTime;
        }

        public long getPermanenceTimeInQueue() {
            return permanenceTimeInQueue;
        }
    }
}
