package functionalTests.component.monitoring;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.util.wrapper.DoubleWrapper;
import org.objectweb.proactive.core.util.wrapper.IntMutableWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;


public class ServerImpl implements Service1, Service2, Service3Gathercast {

    private void sleep() {
        try {
            Thread.sleep((int) (Math.random()*100));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doSomething() {
        sleep();
    }

    public IntWrapper getInt() {
        sleep();
        return new IntWrapper(1);
    }

    public StringWrapper hello() {
        sleep();
        return new StringWrapper("hello world");
    }

    public void doAnotherThing() {
        sleep();
    }

    public DoubleWrapper getDouble() {
        sleep();
        return new DoubleWrapper(1.0);
    }

    public List<StringWrapper> executeAlone() {
        sleep();
        List<StringWrapper> list = new ArrayList<StringWrapper>();
        list.add(new StringWrapper("hello"));
        list.add(new StringWrapper(" world"));
        return list;
    }

    public void foo(List<IntMutableWrapper> i) {
        sleep();
    }

}
