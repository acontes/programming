package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.mpi;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.extensions.gcmdeployment.PathElement;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;

public class ApplicationMPIBean {
	 /** List of providers to be used */
    private List<NodeProvider> providers;
    private String command;

    /** The path to the command */
    private PathElement path;

    /** The arguments*/
    private List<String> args;

    public ApplicationMPIBean() {
    	this.args = new ArrayList<String>();
    	this.providers = new ArrayList<NodeProvider>();
    }
    
	public List<NodeProvider> getProviders() {
		return providers;
	}

	public void addProvider(NodeProvider provider) {
		this.providers.add(provider);
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public PathElement getPath() {
		return path;
	}

	public void setPath(PathElement path) {
		this.path = path;
	}

	public List<String> getArgs() {
		return args;
	}

	public void addArg(String arg) {
		this.args.add(arg);
	}

}
