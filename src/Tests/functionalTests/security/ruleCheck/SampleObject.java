package functionalTests.security.ruleCheck;

import java.io.IOException;
import java.io.Serializable;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.body.Context;
import org.objectweb.proactive.core.body.proxy.BodyProxy;
import org.objectweb.proactive.core.body.proxy.UniversalBodyProxy;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.remoteobject.SynchronousProxy;
import org.objectweb.proactive.core.security.TypedCertificate;
import org.objectweb.proactive.core.security.exceptions.SecurityNotAvailableException;


public class SampleObject implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6487329851736398592L;
	
	private String name;

	public SampleObject() {
		// mandatory empty comstructor
    }
	
	public SampleObject(String name) {
		this.name = name;
	}

    public SerializableString doSomething() {
        System.out.println(this.name + " is doing something.");

        return new SerializableString(this.name + " did something and returned this.");
    }
    
    public SerializableString makeTargetDoSomething(SampleObject target) {
//    	TypedCertificate tc = null;
//    	try {
//			tc = ((BodyProxy) ((StubObject) target).getProxy()).getBody().getCertificate();
//		} catch (SecurityNotAvailableException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		String targetString = new String();
//		if (tc == null) {
//			targetString = "the target";
//		} else {
//			
//		}
    	System.out.println(this.name + " is asking the target to do something.");
    	
    	
    	SerializableString result = target.doSomething();
    	
    	System.out.println(this.name + " got this as result :");
    	System.out.println(">> " + result);
    	return new SerializableString(this.name + " received the result form it's target");
    }
}
