package org.objectweb.proactive.core.mop;

import java.lang.reflect.Field;


public class RestoreObject implements FieldToRestore {

    protected Object from;
    protected Object to;

    public RestoreObject(Object from, Object to) {
        this.from = from;
        this.to = to;
    }

    public Object restore(Object modifiedObject) throws IllegalArgumentException, IllegalAccessException {
        return this.from;
    }

}
