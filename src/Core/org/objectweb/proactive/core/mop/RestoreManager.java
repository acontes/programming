package org.objectweb.proactive.core.mop;

import java.util.ArrayList;
import java.util.List;


public class RestoreManager implements FieldToRestore {

    protected Object objectToRestore;
    protected List<FieldToRestore> list;

    public RestoreManager() {
        //    	this.objectToRestore = objectToRestore;
        this.list = new ArrayList<FieldToRestore>();

    }

    public void add(FieldToRestore f) {
        list.add(f);
    }

    public Object restore(Object modifiedObject) throws IllegalArgumentException, IllegalAccessException {
        Object result = modifiedObject;

        for (FieldToRestore f : list) {
            Object r = f.restore(modifiedObject);
            if (r != null) {
                result = r;
            }
        }
        return result;
    }

}
