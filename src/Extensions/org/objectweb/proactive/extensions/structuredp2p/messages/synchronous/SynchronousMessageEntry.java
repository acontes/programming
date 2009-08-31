package org.objectweb.proactive.extensions.structuredp2p.messages.synchronous;

import java.io.Serializable;


/**
 * @author Laurent Pellegrino
 * @version 0.1, 08/14/2009
 */
@SuppressWarnings("serial")
public class SynchronousMessageEntry implements Serializable {

    public enum Status {
        ALL_RESPONSES_RECEIVED, RECEIPT_IN_PROGRESS
    };

    private Status status = Status.RECEIPT_IN_PROGRESS;

    private SynchronousMessage response;

    private int numberOfResponseWaiting = 0;

    private int numberOfResponseReceived = 0;

    public SynchronousMessageEntry() {

    }

    public SynchronousMessageEntry(int numberOfResponseWaiting) {
        this.numberOfResponseWaiting = numberOfResponseWaiting;
    }

    /**
     * Returns the numberOfResponseReceived
     *
     * @return the numberOfResponseReceived
     */
    public int getNumberOfResponseReceived() {
        return this.numberOfResponseReceived;
    }

    /**
     * Returns the numberOfResponseWaiting
     *
     * @return the numberOfResponseWaiting
     */
    public int getNumberOfResponseWaiting() {
        return this.numberOfResponseWaiting;
    }

    /**
     * Returns the status
     *
     * @return the status
     */
    public Status getStatus() {
        return this.status;
    }

    public SynchronousMessage getSynchronousMessage() {
        return this.response;
    }

    public void incrementNbResponsesReceived(int increment) {
        this.numberOfResponseReceived += increment;

        if (this.numberOfResponseReceived == this.numberOfResponseWaiting) {
            this.status = Status.ALL_RESPONSES_RECEIVED;
        } else if (this.numberOfResponseReceived > this.numberOfResponseWaiting) {
            throw new IllegalStateException("There are more received messages than messages waited.");
        }
    }

    public void setSynchronousMessage(SynchronousMessage response) {
        this.response = response;
    }

}
