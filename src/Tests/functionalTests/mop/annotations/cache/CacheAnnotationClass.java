package functionalTests.mop.annotations.cache;

import java.io.Serializable;

import org.objectweb.proactive.annotation.Cache;


public class CacheAnnotationClass implements Serializable {

    private int i;
    private Object o;

    public CacheAnnotationClass() {
    }

    public CacheAnnotationClass(int i, Object o) {
        this.i = i;
        this.o = o;
    }

    public int getI() {
        return i;
    }

    public void setI(int i) {
        this.i = i;
    }

    @Cache
    public Object getO() {
        return o;
    }

    public void setO(Object o) {
        this.o = o;
    }

}
