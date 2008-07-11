package myhelloworld;

import java.io.Serializable;

import org.objectweb.proactive.core.util.wrapper.StringWrapper;

public class ServerImpl implements Server, Serializable {

	private static final long serialVersionUID = 1L;

	private String name;

	private String location;

	public ServerImpl() {
		this.name = "NaN";
		this.location = "NaN";
	}

	public StringWrapper getLocation() {
		// TODO Auto-generated method stub
		return new StringWrapper(location);
	}

	public StringWrapper getName() {
		// TODO Auto-generated method stub
		return new StringWrapper(name);
	}

	public void setLocation(String location) {
		// TODO Auto-generated method stub
		this.location = location;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

}
