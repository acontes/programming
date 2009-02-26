package org.objectweb.proactive.core.mop;

import java.lang.reflect.Field;

public class FieldToRestoreNormalObject implements FieldToRestore{
    
    protected Field f;
    protected Object value;
    protected Object target;
    
    public FieldToRestoreNormalObject(Field f, Object fieldTarget, Object previousValue) {
           this.f = f;
           f.setAccessible(true);
           this.value = previousValue;
           this.target = fieldTarget;
    }

    public void restore () throws IllegalArgumentException, IllegalAccessException{
          f.set(target, value);
    }
    
}
