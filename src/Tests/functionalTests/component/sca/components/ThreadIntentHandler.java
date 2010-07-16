package functionalTests.component.sca.components;

import org.objectweb.proactive.extensions.component.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.component.sca.control.IntentJoinPoint;


public class ThreadIntentHandler implements IntentHandler {

    // Default timeout if not set.
    private static final long DEFAULT_TIMEOUT = 5000;

    // Timeout for the proceed thread.
    private long timeout = DEFAULT_TIMEOUT;

    public void setTimeout(final long timeout) {
        System.out.println(this + ".setTimeout(" + timeout + ")");
        this.timeout = timeout;
    }

    @Override
    public Object invoke(IntentJoinPoint ijp) throws Throwable {
        // run the proceed in a separate thread.
        ProceedThread pt = new ProceedThread();
        // set the IntentJoinPoint to proceed.
        pt.ijp = ijp;
        // start the proceed thread.
        pt.start();

        try {
            // wait the proceed thread to return for maximum timeout value.
            pt.join(timeout);
            // timeout exceeded and the thread didn't returned yet.
            if (pt.isAlive()) {

                // don't interrupt the proceed thread because we don't know in which state it is.
                System.err.println("is alive!");
                throw new Exception("ServiceUnavailable Timeout of " + timeout + " ms exceeded");
            } else
                System.err.println("not alive!");
            //System.err.println(pt.getUncaughtExceptionHandler());
        } catch (InterruptedException e) {
            System.err.println("call interrupted");
        }

        // throw the exception catched by the proceed thread.
        if (pt.throwable != null) {
            throw pt.throwable;
        }
        // the thread returned before timeout => return the value.
        return pt.result;
    }

}

class ProceedThread extends Thread {
    // The intent join point to proceed.
    IntentJoinPoint ijp = null;

    // The result returned by proceed().
    Object result = null;

    // Exception thrown by proceed().
    Throwable throwable = null;

    public void run() {
        try {
            result = ijp.proceed();
        } catch (Throwable e) {
            throwable = e;
        }
    }
}