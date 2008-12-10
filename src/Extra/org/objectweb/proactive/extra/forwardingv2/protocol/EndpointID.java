package org.objectweb.proactive.extra.forwardingv2.protocol;

import java.io.Serializable;


/**
 * An unique identifier for Agents
 */
@SuppressWarnings("serial")
public class EndpointID implements Serializable {
    final private long id;

    public EndpointID(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return Long.toString(id);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EndpointID other = (EndpointID) obj;
        if (id != other.id)
            return false;
        return true;
    }

}
