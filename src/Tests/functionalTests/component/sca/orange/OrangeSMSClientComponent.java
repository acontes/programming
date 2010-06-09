package functionalTests.component.sca.orange;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;


public class OrangeSMSClientComponent implements OrangeSMSClientAttributes, BindingController, Runner {

    private String id;
    private String from;
    private String to;
    private String content;
    private OrangeSMS orangeService;

    public static final String SERVICES_NAME = "Services";

    public String getContent() {
        return content;
    }

    public String getFrom() {
        return from;
    }

    public String getId() {
        return id;
    }

    public String getTo() {
        return to;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if (SERVICES_NAME.equals(clientItfName)) {
            orangeService = (OrangeSMS) serverItf;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public String[] listFc() {
        return new String[] { SERVICES_NAME };
    }

    public Object lookupFc(String clientItfName) throws NoSuchInterfaceException {
        if (SERVICES_NAME.equals(clientItfName)) {
            return orangeService;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public void unbindFc(String clientItfName) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if (SERVICES_NAME.equals(clientItfName)) {
            orangeService = null;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public boolean execute() {

        return orangeService.sendSMS(id, from, to, content);
    }
}
