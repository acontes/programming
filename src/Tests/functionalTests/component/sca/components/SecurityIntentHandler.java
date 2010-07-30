package functionalTests.component.sca.components;

import java.util.Scanner; //@snippet-start component_scauserguide_5

import org.objectweb.proactive.extensions.component.sca.control.IntentHandler;
import org.objectweb.proactive.extensions.component.sca.control.IntentJoinPoint;


public class SecurityIntentHandler implements IntentHandler {
    String msg;

    public SecurityIntentHandler(String msg) {
        this.msg = msg;
    }

    @Override
    public Object invoke(IntentJoinPoint Ijpt) throws Throwable {
        System.err.println("inside SecurityIntentHandler");
        System.out.println("enter the password: ");
        Scanner in = new Scanner(System.in);
        String pswd = in.next();
        if (pswd.equals("secret")) {
            System.out.println("password correct ! ");
            Object ret = Ijpt.proceed();
            return ret;
        } else {
            throw new Exception("pswd false");
        }
    }

}

//@snippet-end component_scauserguide_5