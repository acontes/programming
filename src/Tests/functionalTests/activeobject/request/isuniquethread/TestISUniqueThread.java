package functionalTests.activeobject.request.isuniquethread;

import java.util.Vector;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.body.request.RequestReceiverImpl;
import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.wrapper.BooleanWrapper;

import functionalTests.GCMFunctionalTestDefaultNodes;


public class TestISUniqueThread extends GCMFunctionalTestDefaultNodes {

    private final static int NB_CALL = 100;
    private final static int NB_CALLER = 10;

    public TestISUniqueThread(int hostCapacity, int vmCapacity) {
        super(hostCapacity, vmCapacity);
    }

    public TestISUniqueThread() {
        super(2, 1);
    }

    public static class Caller {

        public Caller() {
        }

        public Vector<BooleanWrapper> call(AgentForIS a, int nbCall, MethodSelector ms) {
            Vector<BooleanWrapper> results = new Vector<BooleanWrapper>(nbCall);
            // create RMI threads on callee side
            if (ms.equals(MethodSelector.FOO_VOID)) {
                for (int i = 0; i < nbCall; i++)
                    results.add(a.foo());
            } else if (ms.equals(MethodSelector.FOO_INT)) {
                for (int i = 0; i < nbCall; i++)
                    results.add(a.foo(new Integer(1)));
            } else if (ms.equals(MethodSelector.FOO_LONG_INT)) {
                for (int i = 0; i < nbCall; i++)
                    results.add(a.foo(new Long(1), new Integer(1)));
            } else if (ms.equals(MethodSelector.NOTHING)) {
                for (int i = 0; i < nbCall; i++)
                    a.nothing();
            }
            return results;
        }

        public int synchronousBarrier() {
            return 0;
        }
    }

    protected enum MethodSelector {
        FOO_VOID, FOO_INT, FOO_LONG_INT, NOTHING();
    }

    @Test
    public void test() throws ActiveObjectCreationException, NodeException {

        Node n1 = super.getANode();
        Node n2 = super.getANode();

        AgentForIS a1 = (AgentForIS) PAActiveObject.newActive(AgentForIS.class.getName(), null, n1);
        a1.init();

        Caller[] callers = new Caller[NB_CALLER];
        for (int i = 0; i < NB_CALLER; i++) {
            callers[i] = (Caller) PAActiveObject.newActive(Caller.class.getName(), new Object[] {}, n2);
        }

        // create RMI threads
        for (int i = 0; i < NB_CALLER; i++) {
            callers[i].call(a1, NB_CALL, MethodSelector.NOTHING);
        }

        Vector<BooleanWrapper>[] foo_void = new Vector[NB_CALLER];
        Vector<BooleanWrapper>[] foo_int = new Vector[NB_CALLER];
        Vector<BooleanWrapper>[] foo_long_int = new Vector[NB_CALLER];

        for (int i = 0; i < NB_CALLER; i++) {
            foo_void[i] = callers[i].call(a1, NB_CALL, MethodSelector.FOO_VOID);
        }

        for (int i = 0; i < NB_CALLER; i++) {
            foo_int[i] = callers[i].call(a1, NB_CALL, MethodSelector.FOO_INT);
        }

        for (int i = 0; i < NB_CALLER; i++) {
            foo_long_int[i] = callers[i].call(a1, NB_CALL, MethodSelector.FOO_LONG_INT);
        }

        for (int i = 0; i < NB_CALLER; i++) {
            callers[i].synchronousBarrier();
        }
        //checks
        for (int i = 0; i < NB_CALLER; i++) {
            for (int j = 0; j < NB_CALL; j++) {
                Assert.assertTrue(foo_void[i].get(j).booleanValue());
                Assert.assertTrue(foo_int[i].get(j).booleanValue());
                Assert.assertTrue(foo_long_int[i].get(j).booleanValue());
            }
        }

        // terminate all the callers
        for (int i = 0; i < NB_CALLER; i++) {
            PAActiveObject.terminateActiveObject(callers[i], true);
        }

        // wait for ping period 
        try {
            Thread.sleep(RequestReceiverImpl.THREAD_FOR_IS_PING_PERIOD * 1000 + 3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Assert.assertTrue(a1.checkAllThreadISAreDown());

    }

}
