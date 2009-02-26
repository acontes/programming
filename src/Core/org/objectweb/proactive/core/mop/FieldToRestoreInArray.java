package org.objectweb.proactive.core.mop;

import java.lang.reflect.Field;


public class FieldToRestoreInArray extends FieldToRestoreNormalField {

    protected int indice;

    public FieldToRestoreInArray(Field f, Object targetObject, Object value, int indice) {
        super(f, targetObject, value);
        this.indice = indice;
    }

    @Override
    public Object restore(Object modifiedObject) throws IllegalArgumentException, IllegalAccessException {
        f.setAccessible(true);
        Object[] o = (Object[]) f.get(target);
        o[indice] = value;
        f.setAccessible(false);
        return null;
    }

}
