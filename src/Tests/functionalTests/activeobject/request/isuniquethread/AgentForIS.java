package functionalTests.activeobject.request.isuniquethread;

import java.util.Hashtable;
import java.util.Map;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


public class AgentForIS {

    private Thread serviceThread;
    private Map<UniqueID, Thread> threadsForISUnique;

    // IS MT
    public BooleanWrapper foo() {
        return new BooleanWrapper((!Thread.currentThread().equals(this.serviceThread)) &&
            !(threadsForISUnique.containsValue(Thread.currentThread())));
    }

    // NORMAL
    public BooleanWrapper foo(Integer a) {
        return new BooleanWrapper(Thread.currentThread().equals(this.serviceThread));
    }

    // IS UT
    public BooleanWrapper foo(Long a, Integer b) {
        UniqueID caller = PAActiveObject.getContext().getCurrentRequest().getSourceBodyID();
        if (!this.threadsForISUnique.containsKey(caller)) {
            // first call for this caller
            this.threadsForISUnique.put(caller, Thread.currentThread());
        }
        return new BooleanWrapper(Thread.currentThread().equals(this.threadsForISUnique.get(caller)));
    }

    public void nothing() {
    }

    public boolean checkAllThreadISAreDown() {
        for (Thread t : threadsForISUnique.values()) {
            //if (!t.getState().equals(State.TERMINATED)){
            if (t.isAlive()) {
                return false;
            }
        }
        return true;
    }

    public int init() {
        // avoid use of initActivity because PROACTIVE-652
        PAActiveObject.setImmediateService("nothing");
        PAActiveObject.setImmediateService("foo", false);
        PAActiveObject.removeImmediateService("foo", new Class[] { Integer.class });
        PAActiveObject.setImmediateService("foo", new Class[] { Long.class, Integer.class }, true);

        this.serviceThread = Thread.currentThread();

        this.threadsForISUnique = new Hashtable<UniqueID, Thread>();

        return 0;

    }
}
