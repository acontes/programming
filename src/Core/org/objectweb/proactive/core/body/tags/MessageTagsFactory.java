package org.objectweb.proactive.core.body.tags;

public interface MessageTagsFactory {

    /**
     * Return a new MessageTags object
     * @return MessageTags Object
     */
    public MessageTags newMessageTags();
    
    /**
     * Start the thread checking the lease
     * value of each LocalMemory created on
     * the different Body.
     */
    public void startLeaseChecking();
    
    /**
     * Stop the thread checking the lease
     * value.
     */
    public void stopLeaseChecking();

    /**
     * To know if the thread checking the lease
     * value is already running
     * @return True if running, false otherwise.
     */
    public boolean isLeaseCheckingRunning();
    
}
