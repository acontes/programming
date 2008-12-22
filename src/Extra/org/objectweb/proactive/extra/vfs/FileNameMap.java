package org.objectweb.proactive.extra.vfs;


public class FileNameMap implements java.io.Serializable{
	
	private String realURI;
	private String mountingPoint;
	
	
	public FileNameMap(String realURI, String mountName) {
		this.realURI = realURI;
		this.mountingPoint = mountName;
	}

	public String getRealURI() {
		return realURI;
	}

	public String getMountingPoint() {
		return mountingPoint;
	}
}
