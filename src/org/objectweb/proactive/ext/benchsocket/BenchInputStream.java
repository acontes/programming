package org.objectweb.proactive.ext.benchsocket;

import java.io.IOException;
import java.io.InputStream;


public class BenchInputStream extends InputStream implements BenchStream {
    private InputStream realInputStream;
    private int number;
    private int total;
    private BenchClientSocket parent;
    private ShutdownThread shThread;


    public BenchInputStream(InputStream real, int number) {
        this.realInputStream = real;
        this.number = number;
       // ShutdownThread.addStream(this);
        //when the JVM is killed
        try {
        	shThread = new ShutdownThread(this);
        	Runtime.getRuntime().addShutdownHook(shThread);
        } catch(Exception e) {
        	//e.printStackTrace();
        }
    }

    public BenchInputStream(InputStream stream, int number,
        BenchClientSocket parent) {
        this(stream, number);
        this.parent = parent;
      
    }

    public synchronized void displayTotal() {
       display("=== Total Input for socket ");
        total = 0;
    }

    public synchronized void dumpIntermediateResults() {
    	display("---- Intermediate input for socket ");
    }
    
    protected void display(String s) {
    	if (parent != null) {
    		System.out.println( s+""+number + " = " +
    				total + " real " + parent);
    	} else {
    		System.out.println( s+"" + number + " = " +
    				total);
    	}
    }
    
    /* (non-Javadoc)
     * @see java.io.InputStream#available()
     */
    public int available() throws IOException {
        return this.realInputStream.available();
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#close()
     */
    public void close() throws IOException {
//    		if (ShutdownThread.removeStream(this)){
//    			this.realInputStream.close();
//    			//System.out.println("BenchOutputStream.close() on " + this.number);
//    			this.displayTotal();
//    		}
    		
//	if (ShutdownThread.removeStream(this)){
    	if (this.realInputStream!=null) {
    		this.realInputStream.close();
    	}
    		//System.out.println("BenchOutputStream.close() on " + this.number);
    		this.displayTotal();
    		//	}
    		//no only we remove the thread, but we also fire it
    		//because of java bug #4533
    		try {
    		Runtime.getRuntime().removeShutdownHook(shThread);
    		} catch (Exception e) {
    			//e.printStackTrace();
    		}
    		if (shThread!=null) {
    		shThread.fakeRun();
    		}
    		shThread=null;
    		this.parent=null;
    }
    
    

    /* (non-Javadoc)
     * @see java.io.InputStream#mark(int)
     */
    public synchronized void mark(int readlimit) {
        this.realInputStream.mark(readlimit);
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#markSupported()
     */
    public boolean markSupported() {
        return this.realInputStream.markSupported();
    }

    public int read() throws IOException {
        int tmp = this.realInputStream.read();
      //  System.out.println("BenchInputStream.read() on " + this.number +" " + tmp);
        if (BenchSocketFactory.measure) {
            total += 1;
        }
       // total += tmp;
        return tmp;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[], int, int)
     */
    public int read(byte[] b, int off, int len) throws IOException {
        int tmp = this.realInputStream.read(b, off, len);
     //   System.out.println("BenchInputStream.read(byte[] b, int off, int len) on " + this.number +" " + tmp);
        if (BenchSocketFactory.measure) {
            total += tmp;
        }
        return tmp;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#read(byte[])
     */
    public int read(byte[] b) throws IOException {
        int tmp = this.realInputStream.read(b);
       // System.out.println("BenchInputStream.read(byte[] b) on " + this.number +" " + tmp);
        if (BenchSocketFactory.measure) {
            total += tmp;
        }
        return tmp;
    }

    /* (non-Javadoc)
     * @see java.io.InputStream#reset()
     */
    public synchronized void reset() throws IOException {
        this.realInputStream.reset();
    }

    
    public long skip(long n) throws IOException {
        return this.realInputStream.skip(n);
    }
}
