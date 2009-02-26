package org.objectweb.proactive.core.mop;

import java.lang.reflect.Field;

public class FieldToRestoreInArray extends FieldToRestoreNormalObject{
    
    protected int indice ;
    
    public FieldToRestoreInArray(Field f, Object targetObject, Object value, int indice) {
        super(f,targetObject,value);
        this.indice = indice;
    }

    @Override
    public void restore() throws IllegalArgumentException, IllegalAccessException {
        Object[] o = (Object[]) f.get(target);
        o[indice] = value;
    }
    
}
