package org.objectweb.proactive.extra.vfs_akapur;

import java.net.URL;

public class MountNameMap implements java.io.Serializable {
	
	
	private String nodename;
	private String mountednodeurl;
	
	public MountNameMap(String nodename, String mountednodeurl)
	{
		this.nodename = nodename;
		this.mountednodeurl = mountednodeurl;
	}
	
	public String getnodename()
	{
		return nodename;
	}
	
	public String getmountednodeurl()
	{
		return mountednodeurl;
	}

}
