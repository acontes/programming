package org.objectweb.proactive.extra.vfs;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.vfs.impl.StandardFileSystemManager;

public class VFSManager {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StandardFileSystemManager a = new StandardFileSystemManager();
		URL url;
		try {
			url = new URL("FSMconf.xml");
			a.setConfiguration(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
