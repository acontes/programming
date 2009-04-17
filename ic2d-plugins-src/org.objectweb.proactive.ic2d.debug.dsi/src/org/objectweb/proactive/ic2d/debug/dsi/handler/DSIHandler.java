package org.objectweb.proactive.ic2d.debug.dsi.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.dsi.RequestTags;


public class DSIHandler implements Serializable {

    private List<Message> services;
    private Object lock = new Object();

    static private DSIHandler _singleton;

    private DSIHandler() {
        services = new ArrayList<Message>();
    }

    static public synchronized DSIHandler getInstance() {
        if (_singleton == null)
            _singleton = new DSIHandler();
        return _singleton;
    }

    public void addService(UniqueID from, UniqueID to, String type, String method, long seq, RequestTags tags) {
        synchronized (lock) {
            this.services.add(new Message(from, to, type, method, seq, tags));
        }
    }

    public String toString() {
        synchronized (lock) {
            Collections.sort(services);
        }
        String res = "\n";
        res += "----------------+---------------+---------------+------------------\n";
        res += "|SequenceNumber | UID <-> UID   | Method        | MessageType\n";
        res += "|---------------+---------------+---------------+------------------\n";
        for (Message s : services) {
            res += s;
        }
        return res;
    }

    public int messagesNumber() {
        synchronized (lock) {
            return services.size();
        }
    }

    public Set<UniqueID> getAOs() {
        Set<UniqueID> res = new HashSet<UniqueID>();
        synchronized (lock) {
            for (Message m : services) {
                res.add(m.sender);
                res.add(m.destinator);
            }
        }
        return res;
    }

    public Map<UniqueID, Set<RequestDSI>> getDSI() {
        Map<UniqueID, Set<RequestDSI>> res = new HashMap<UniqueID, Set<RequestDSI>>();
        synchronized (lock) {
            //Collections.sort(services);
            for (Message m : services) {
                if (m.tags.getTagValue("DSI") != null) {
                    UniqueID dsi = m.tags.getTagValue("DSI");
                    if (res.get(dsi) == null) {
                        res.put(dsi, new HashSet<RequestDSI>());
                    }
                    res.get(dsi).add(new RequestDSI(m.sender, m.destinator));
                }
            }
        }
        return res;
    }

    private class Message implements Comparable<Message> {

        long sequenceID;
        UniqueID sender;
        UniqueID destinator;
        String type;
        String method;
        RequestTags tags;

        public Message(UniqueID sender, UniqueID destinator, String type, String method, long seq,
                RequestTags tags) {
            this.sender = sender;
            this.destinator = destinator;
            this.type = type;
            this.method = method;
            this.sequenceID = seq;
            this.tags = tags;
        }

        public String toString() {
            String method = (this.method.length() > 10) ? this.method.substring(0, 7) + "..." : this.method;
            String res = "| " + sequenceID + "\t| " + sender.shortString();
            if (type.equals("replySent") || type.equals("requestReceived"))
                res += " <- ";
            else
                res += " -> ";
            res += destinator.shortString() + "\t| {" + method + "}" + "\t| " + type + " // " + tags;
            if (tags == null)
                res += "\n";
            return res;
        }

        public int compareTo(Message m) {
            if (this.sequenceID < m.sequenceID)
                return -1;
            if (this.sequenceID > m.sequenceID)
                return 1;
            if (this.sequenceID == m.sequenceID)
                return this.type.compareTo(m.type);
            return 0;
        }
    }
}
