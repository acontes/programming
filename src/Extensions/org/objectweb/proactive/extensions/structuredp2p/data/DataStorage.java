package org.objectweb.proactive.extensions.structuredp2p.data;

import java.io.Serializable;
import java.util.HashMap;

import org.objectweb.proactive.extensions.structuredp2p.core.can.Zone;


/**
 * DataStorage manages that can be stored in a peer.
 * 
 * @author Kilanga Fanny
 * @author Pellegrino Laurent
 * @author Trovato Alexandre
 * 
 * @version 0.1
 */
@SuppressWarnings("serial")
public class DataStorage implements Serializable {
    private final HashMap<String, Object> data;

    /**
     * Constructor.
     */
    public DataStorage() {
        this.data = new HashMap<String, Object>();
    }

    /**
     * Return all the data in the storage.
     * 
     * @return data
     */
    public HashMap<String, Object> getData() {
        return this.data;
    }

    /**
     * Return the data for a key.
     * 
     * @param key
     *            id of the data
     * @return the data
     */
    public Object getData(String key) {
        return this.data.get(key);
    }

    /**
     * Return the data included in the zone.
     * 
     * @param zone
     *            the zone
     * @return the data storage
     */
    public DataStorage getDataFromZone(Zone zone) {
        // FIXME comment definir la position d'une donnee dans la zone ?
        return new DataStorage();
    }

    /**
     * Insert all the data from an other DataStorage.
     * 
     * @param other
     *            the other DataStorage
     */
    public void addData(DataStorage other) {
        this.data.putAll(other.getData());
    }

    /**
     * Insert a new object in the storage.
     * 
     * @param key
     *            id of the object
     * @param value
     *            the datas
     */
    // FIXME la key est un hash ou non ?
    public void addData(String key, Object value) {
        this.data.put(key, value);
    }

    /**
     * Remove the object with the current key.
     * 
     * @param key
     *            the current key.
     */
    public void removeData(String key) {
        this.data.remove(key);
    }
}
