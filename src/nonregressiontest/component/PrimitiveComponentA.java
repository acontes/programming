/*
 * Created on Oct 20, 2003
 * author : Matthieu Morel
 */
package nonregressiontest.component;

import org.apache.log4j.Logger;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;


/**
 * @author Matthieu Morel
 */
public class PrimitiveComponentA implements I1, BindingController {
    static Logger logger = Logger.getLogger(PrimitiveComponentA.class.getName());
    public final static String MESSAGE = "-->PrimitiveComponentA";
    public final static String I2_ITF_NAME = "i2";
    I2 i2;

    /**
     *
     */
    public PrimitiveComponentA() {
    }

    /* (non-Javadoc)
     * @see org.objectweb.fractal.api.control.UserBindingController#addFcBinding(java.lang.String, java.lang.Object)
     */
    public void bindFc(String clientItfName, Object serverItf) {
        if (clientItfName.equals(I2_ITF_NAME)) {
            i2 = (I2) serverItf;
            //logger.debug("MotorImpl : added binding on a wheel");
        } else {
            logger.error(
                "no such binding is possible : client interface name does not match");
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.fractal.api.control.UserBindingController#getFcBindings(java.lang.String)
     */
    public Object getFcBindings(String arg0) {
        return null;
    }

    /* (non-Javadoc)
     * @see org.objectweb.fractal.api.control.UserBindingController#removeFcBinding(java.lang.String, java.lang.Object)
     */
    public void removeFcBinding(String clientItfName, Object serverItf) {
        if (clientItfName.equals(I2_ITF_NAME)) {
            if (serverItf.equals(i2)) {
                i2 = null;
                logger.debug("removed binding on i2");
            } else {
                logger.error("server object does not match");
            }
        } else {
            logger.error("client interface name does not match");
        }
    }

    /* (non-Javadoc)
     * @see nonregressiontest.component.creation.Input#processInputMessage(java.lang.String)
     */
    public Message processInputMessage(Message message) {
        //		/logger.info("transferring message :" + message.toString());
        if (i2 != null) {
            return (i2.processOutputMessage(message.append(MESSAGE))).append(MESSAGE);
        } else {
            logger.error("cannot forward message (binding missing)");
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.fractal.api.control.BindingController#listFc()
     */
    public String[] listFc() {
        return new String[] { I2_ITF_NAME };
    }

    /* (non-Javadoc)
     * @see org.objectweb.fractal.api.control.BindingController#lookupFc(java.lang.String)
     */
    public Object lookupFc(String clientItf) throws NoSuchInterfaceException {
        if (clientItf.equals(I2_ITF_NAME)) {
            return i2;
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("cannot find " + I2_ITF_NAME + " interface");
            }
            return null;
        }
    }

    /* (non-Javadoc)
     * @see org.objectweb.fractal.api.control.BindingController#unbindFc(java.lang.String)
     */
    public void unbindFc(String clientItf)
        throws NoSuchInterfaceException, IllegalBindingException, 
            IllegalLifeCycleException {
        if (clientItf.equals(I2_ITF_NAME)) {
            i2 = null;
            if (logger.isDebugEnabled()) {
                logger.debug(I2_ITF_NAME + " interface unbound");
            }
        }
    }
}
