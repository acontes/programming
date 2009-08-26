package functionalTests.activeobject.request.isuniquethread;

import java.util.Hashtable;
import java.util.Map;

import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;


public class AgentForIS {

    // static for all active objects Agent <Owner -> <CallerID, ThreadforIS>
    private static Map<UniqueID, Map<UniqueID, Thread>> threadsForISUnique = new Hashtable<UniqueID, Map<UniqueID, Thread>>();

    private Thread serviceThread;

    private UniqueID myID;

    // IS MT
    public BooleanWrapper foo() {
        return new BooleanWrapper((!Thread.currentThread().equals(this.serviceThread)) &&
            !(threadsForISUnique.get(myID).containsValue(Thread.currentThread())));
    }

    // NORMAL
    public BooleanWrapper foo(Integer a) {
        return new BooleanWrapper(Thread.currentThread().equals(this.serviceThread));
    }

    // IS UT
    public BooleanWrapper foo(Long a, Integer b) {
        UniqueID caller = PAActiveObject.getContext().getCurrentRequest().getSourceBodyID();
        if (!this.threadsForISUnique.get(myID).containsKey(caller)) {
            // first call for this caller
            this.threadsForISUnique.get(myID).put(caller, Thread.currentThread());
        }
        return new BooleanWrapper(Thread.currentThread()
                .equals(this.threadsForISUnique.get(myID).get(caller)));
    }

    public void nothing() {
    }

    public boolean checkAllThreadISAreDown(UniqueID id) {
        for (Thread t : threadsForISUnique.get(id == null ? myID : id).values()) {
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

        this.myID = PAActiveObject.getBodyOnThis().getID();

        threadsForISUnique.put(myID, new Hashtable<UniqueID, Thread>());

        return 0;

    }

    public UniqueID getID() {
        return this.myID;
    }
}
