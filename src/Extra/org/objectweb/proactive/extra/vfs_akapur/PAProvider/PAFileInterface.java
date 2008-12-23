package org.objectweb.proactive.extra.vfs_akapur.PAProvider;

import java.io.IOException;
import java.io.Serializable;

import org.objectweb.proactive.core.node.Node;
import org.objectweb.proactive.core.node.NodeException;

public interface PAFileInterface extends Serializable {

	
	public boolean exists() throws IOException;
	
	public boolean isDirectory() throws IOException;
	
	public boolean isFile() throws IOException;
	
	public Node getNode() throws NodeException;
	
	public String getURL();
}
