package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous;

/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/06/2009
 */
public interface QueryResponse extends Query {

    public abstract long getDeliveryTimestamp();

    public abstract int getLatency();

    public abstract int getNbStepsForReceipt();

    public abstract int getNbStepsForSend();

    public abstract int getTotalNumberOfSteps();

    public abstract void setDeliveryTime();

}
