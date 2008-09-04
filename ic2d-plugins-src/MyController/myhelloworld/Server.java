package myhelloworld;

import org.objectweb.proactive.core.util.wrapper.StringWrapper;


public interface Server {
    void setName(String name);

    void setLocation(String location);

    StringWrapper getName();

    StringWrapper getLocation();
}
