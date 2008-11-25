package functionalTests.annotations.remoteobject.inputs;

import java.io.Serializable;
import java.util.List;

import org.objectweb.proactive.extra.annotation.remoteobject.RemoteObject;


@RemoteObject
public class CorrectedReject implements Serializable {

    public CorrectedReject() {
    }

    public CorrectedReject(int n) {
    }

    private List<Object> _someLocks;

    private void doNothingSynchronized() {
    }

    private int dontOverrideMe() {
        return 0;
    }

    public int counter;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }
}
