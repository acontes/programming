package org.objectweb.proactive.core.body.tags;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.proactive.ObjectForSynchro;

/**
 * Tag : Local Memory
 * This object allow to stock couple key/value on the current
 * active object usable by the Tag who create it.
 * 
 * Each LocalMemoryTag has a Lease time. When the lease is 
 * inferior to 0, the object is removed.
 * 
 * Each time there is an access on the localMemory, the lease
 * time is increased of the half value of the initial lease.
 */
public class LocalMemoryTag implements Serializable {
    
    static{
        LocalMemoryLeaseThread.start();
    }
    
    private String tagIDReferer;
    
    private int currentlease;
    private int leaseInc;
    
    private ObjectForSynchro lock;
    private Map<String, Object> memory;
    
    /**
     * Constructor
     * @param tagID - ID Of the Tag
     * @param lease - Lease time of this LocalMemory
     */
    public LocalMemoryTag(String tagID, int lease) {
        lock = new ObjectForSynchro();
        this.tagIDReferer = tagID;
        this.currentlease = lease;
        this.leaseInc = lease;
        this.memory = new HashMap<String, Object>();
    }
    
    /**
     * Add a Key/Value to this local memory
     * @param key   - the Key
     * @param value - the Value
     */
    public void put(String key, Object value){
        synchronized (lock) {
            this.currentlease += leaseInc;
        }
        this.memory.put(key, value);
    }
    
    /**
     * To get back a previous entry of this local memory
     * @param key - the Key to retrieve the value
     * @return the value
     */
    public Object get(String key){
        synchronized (lock) {
            this.currentlease += leaseInc;
        }
        return this.memory.get(key);
    }
    
    /**
     * To know if the lease time exceeded.
     * @return true it is <= 0
     */
    public boolean leaseExceeded(){
        return currentlease <= 0;
    }
    
    /**
     * Decrement the current lease value
     * @param decValue - Value of the decrement
     */
    public void decCurrentLease(int decValue){
        synchronized (lock) {
            this.currentlease -= decValue;
        }
    }
    
    /**
     * To get the Tag Id to which is attached this local memory
     * @return
     */
    public String getTagIDReferer(){
        return this.tagIDReferer;
    }
    
}
