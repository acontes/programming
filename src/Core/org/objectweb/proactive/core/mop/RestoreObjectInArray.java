package org.objectweb.proactive.core.mop;

import java.lang.reflect.Array;
import java.lang.reflect.Field;


public class RestoreObjectInArray implements FieldToRestore {

    protected int indice;
    protected Object array;
    protected Object value;

    public RestoreObjectInArray(Object targetObject, Object value, int indice) {
        this.array = targetObject;
        this.value = value;
        this.indice = indice;
    }

    public Object restore(Object modifiedObject) throws IllegalArgumentException, IllegalAccessException {
    	Array.set(array, indice, value);
        return null;
    }

}
