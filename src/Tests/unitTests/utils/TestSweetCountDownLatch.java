package unitTests.utils;

import java.util.concurrent.CountDownLatch;

import junit.framework.Assert;

import org.junit.Test;
import org.objectweb.proactive.core.util.SweetCountDownLatch;

import unitTests.UnitTests;


public class TestSweetCountDownLatch extends UnitTests {

    @Test
    public void test() throws InterruptedException {
        CountDownLatch latch = new SweetCountDownLatch(1);

        T t = new T(latch, Thread.currentThread());
        new Thread(t).start();
        latch.await();

        Assert.assertEquals(0, latch.getCount());
    }

    class T implements Runnable {
        private CountDownLatch latch;
        private Thread waiter;

        public T(CountDownLatch latch, Thread waiter) {
            this.latch = latch;
            this.waiter = waiter;
        }

        public void run() {
            this.waiter.interrupt();
            Thread.yield();
            this.latch.countDown();

        }
    }
}
