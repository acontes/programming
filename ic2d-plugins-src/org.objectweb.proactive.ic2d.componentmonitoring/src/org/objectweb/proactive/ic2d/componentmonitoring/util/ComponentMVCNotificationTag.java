package org.objectweb.proactive.ic2d.componentmonitoring.util;

public enum ComponentMVCNotificationTag {

    //---------- General messages ----------
    /**
     * Notification message sent when a child has been added to an
     * <code> org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData </code> object.
     * Use the key corresponding to the child as data in the MVCNotification object.
     */
    ADD_CHILD,
    /**
     * Notification message sent when a set of children (ActieObject(s)) has been added to an
     * <code> org.objectweb.proactive.ic2d.jmxmonitoring.data.NodeObject </code> object.
     * Use a list with the keys corresponding to the child as data in the MVCNotification object.
     */
    ADD_CHILDREN,
    /**
     * Notification message sent when a child has been removed from an
     * <code> org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData </code> object.
     * Use the key corresponding to the child as data in the MVCNotification object.
     */
    REMOVE_CHILD,
    /**
     * Notification message sent when a child of a
     * <code> org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData </code> object
     * is no longer monitored.
     * Use the key corresponding to the child as data in the MVCNotification object.
     */
    REMOVE_CHILD_FROM_MONITORED_CHILDREN,
    /**
     * Notification message sent when the state of a
     * <code> org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData </code> object
     * has been changed.
     * Use the a constant from <code>State</code> enum which describes the new state.
     */
    STATE_CHANGED,

    /**
     * Notification message sent when a Source connections have changed
     */
    SOURCE_CONNECTIONS_CHANGED,

    /**
     * Notification message sent when a Target connection have changed
     */
    TARGET_CONNECTIONS_CHANGED,
    /**
     * Notification message sent when the hierachical of a component has changed
     */
    HIERACHICAL_CHANGED,
    /**
     * Notification message sent when the name of a component has changed
     */
    NAME_CHANGED,
    /*
     * Notification message sent when the mean arrival rate of a component has changed
     */
    MEAN_ARRIVAL_RATE_CHANGED,
    /*
     * Notification message sent when the mean departure rate of a component has changed
     */
    MEAN_DEPARTURE_RATE_CHANGED,
    /*
     * Notification message sent when the mean service rate of a component has changed
     */
    MEAN_SERVICE_RATE_CHANGED,
    /*
     * Notification message sent when the sample arrival rate of a component has changed
     */
    SAMPLE_ARRIVAL_RATE_CHANGED,
    /*
     * Notification message sent when the sample departure rate of a component has changed
     */
    SAMPLE_DEPARTURE_RATE_CHANGED,
    /*
     * Notification message sent when the sample service rate of a component has changed
     */
    SAMPLE_SERVICE_RATE_CHANGED,
    /*
     * Notification message sent when the time arrival rate of a component has changed
     */
    TIME_ARRIVAL_RATE_CHANGED,
    /*
     * Notification message sent when the time departure rate of a component has changed
     */
    TIME_DEPARTURE_RATE_CHANGED,
    /*
     * Notification message sent when the time service rate of a component has changed
     */
    TIME_SERVICE_RATE_CHANGED;

}
