package functionalTests.security.ruleCheck;

import java.io.IOException;
import java.io.Serializable;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.body.proxy.BodyProxy;
import org.objectweb.proactive.core.mop.StubObject;
import org.objectweb.proactive.core.security.exceptions.RuntimeSecurityException;
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

        return new SerializableString(this.name +
            " did something and returned this.");
    }

    public SerializableString sayhello(SampleObject target) {
        return target.doSomething();
    }

    public void makeTargetDoSomething(SampleObject target) {
        String targetString = "the target";
        try {
            targetString = ((BodyProxy) ((StubObject) target).getProxy()).getBody()
                            .getCertificate().getCert().getIssuerDN().getName();
        } catch (SecurityNotAvailableException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(this.name + " is asking " + targetString +
            " to do something.");

        ProActive.setImmediateService("doSomething");
        SerializableString result = null;
        try {
            result = target.doSomething();
        } catch (RuntimeSecurityException e) {
            System.out.println("-- Security Exception " + e.getMessage());
        }
        System.out.println(this.name + " got a result from " + targetString +
            " >> " + result);
    }
}
