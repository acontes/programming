package temp;

import org.objectweb.fractal.api.control.BindingController;


public class ConnectionImpl implements BindingController, Service{

	   private Runnable client;
	   private String header = "----] ";
	    private int count = 0;
	    
	    public ConnectionImpl() {
	        // the following instruction was removed, because ProActive requires empty no-args constructors
	        // otherwise this instruction is executed also at the construction of the stub
	        //System.err.println("CLIENT created");
	    }
	    public void print(final String msg) {
//	        new Exception() {
//	            @Override
//	            public String toString() {
//	                return "Server: print method called";
//	            }
//	        }.printStackTrace();
	        System.out.println("Server: begin printing...");
	        //for (int i = 0; i < count; ++i) {
	            System.out.println(header + msg);
	        //}
	        client.run();
	        System.out.println("Server: print done.");
	    }


	    public String[] listFc() {
	        return new String[] { "r" };
	    }

	    public Object lookupFc(final String cItf) {
	        if (cItf.equals("r")) {
	            return client;
	        }
	        return null;
	    }

	    public void bindFc(final String cItf, final Object sItf) {
	        if (cItf.equals("r")) {
	            client = (Runnable) sItf;
	        }
	    }

	    public void unbindFc(final String cItf) {
	        if (cItf.equals("r")) {
	            client = null;
	        }
	    }
}
