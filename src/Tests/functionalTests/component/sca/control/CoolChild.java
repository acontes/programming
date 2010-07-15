package functionalTests.component.sca.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objectweb.proactive.extensions.component.sca.control.IntentJoinPoint;


public class CoolChild extends CoolParent {

    public void fooWrapper() {
        super.foo();
    }

    public int barWrapper() {
        return super.bar();
    }

    public void foo() {
        try {
            IntentJoinPoint test = new IntentJoinPoint("fooWrapper", this, new Class[0], new Object[0]);
            test.proceed();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public int bar() {
        IntentJoinPoint test = new IntentJoinPoint("barWrapper", this, new Class[0], new Object[0]);
        Object x = null;
        try {
            x = test.proceed();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ((Integer) x).intValue();
    }

    protected void cool() {
        System.err.println("cool de child");
    }
}
