package functionalTests.component.monitoring;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.core.util.wrapper.DoubleWrapper;
import org.objectweb.proactive.core.util.wrapper.IntMutableWrapper;
import org.objectweb.proactive.core.util.wrapper.IntWrapper;
import org.objectweb.proactive.core.util.wrapper.StringWrapper;

public class ServerImpl implements Service1, Service2, Service3Gathercast {

    public void doSomething() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public IntWrapper getInt() {
        return new IntWrapper(1);
    }

    public StringWrapper hello() {
        return new StringWrapper("hello world");
    }

    public void doAnotherThing() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public DoubleWrapper getDouble() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new DoubleWrapper(1.0);
    }

    public List<StringWrapper> executeAlone() {
        List<StringWrapper> list = new ArrayList<StringWrapper>();
        list.add(new StringWrapper("hello"));
        list.add(new StringWrapper(" world"));
        return list;
    }

    public void foo(List<IntMutableWrapper> i) {
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
