package myhelloworld;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;

public class ClientImpl implements BindingController, Client {

	private static final long serialVersionUID = 2731042145983043067L;

	private Server MyServer = null;

	public ClientImpl() {

	}

	

	public void bindFc(String arg0, Object arg1)
			throws NoSuchInterfaceException, IllegalBindingException,
			IllegalLifeCycleException {
		// TODO Auto-generated method stub

		if (arg0.equals("myhelloworld-client-client")) {
			MyServer = (Server) arg1;
		} else
			throw new NoSuchInterfaceException("No such interface named: "
					+ arg0);

	}

	public String[] listFc() {
		// TODO Auto-generated method stub
		return new String[] { "myhelloworld-client-client" };
	}

	public Object lookupFc(String arg0) throws NoSuchInterfaceException {
		// TODO Auto-generated method stub
		if (arg0.equals("myhelloworld-client-client")) {
			return MyServer;
		} else
			throw new NoSuchInterfaceException("No such interface named: "
					+ arg0);
	}

	public void unbindFc(String arg0) throws NoSuchInterfaceException,
			IllegalBindingException, IllegalLifeCycleException {
		// TODO Auto-generated method stub
		if (arg0.equals("myhelloworld-client-client")) {
			MyServer = null;
		} else
			throw new NoSuchInterfaceException("No such interface named: "
					+ arg0);
	}

	public String getLocation() {
		// TODO Auto-generated method stub
		return MyServer.getLocation().stringValue();
	}

	public String getName() {
		// TODO Auto-generated method stub
		return MyServer.getName().stringValue();
	}

	public void setLocation(String location) {
		// TODO Auto-generated method stub
		MyServer.setLocation(location);
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
		MyServer.setName(name);
	}

}
