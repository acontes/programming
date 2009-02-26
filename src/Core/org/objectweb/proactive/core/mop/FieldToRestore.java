package org.objectweb.proactive.core.mop;

public interface FieldToRestore {

    public Object restore(Object modifiedObject) throws IllegalArgumentException, IllegalAccessException;

}
