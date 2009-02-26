package org.objectweb.proactive.core.mop;

import java.lang.reflect.Field;


public class FieldToRestoreNormalField implements FieldToRestore {

    protected Field f;
    protected Object value;
    protected Object target;

    public FieldToRestoreNormalField(Field f, Object fieldTarget, Object previousValue) {
        //        System.out.println("FieldToRestoreNormalField.FieldToRestoreNormalField() " + f + ": " + fieldTarget + "// " + previousValue);

        this.f = f;

        this.value = previousValue;
        this.target = fieldTarget;
    }

    public Object restore(Object modifiedObject) throws IllegalArgumentException, IllegalAccessException {
        f.setAccessible(true);
        f.set(target, value);
        f.setAccessible(false);
        return null;
    }

}
