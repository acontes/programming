package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.executable;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.extensions.gcmdeployment.PathElement;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;

public class ApplicationExecutableBean {

    /** List of providers to be used */
    final private List<NodeProvider> nodeProviders;
   
    private String command;

    /** The path to the command */
    private PathElement path;

    /** The arguments */
    final private List<String> args;

   
    private Instances instances;

    public ApplicationExecutableBean() {
        this.nodeProviders = new ArrayList<NodeProvider>();
        this.args = new ArrayList<String>();
    }
    
    
    public List<NodeProvider> getProviders() {
        return nodeProviders;
    }


    public void addProvider(NodeProvider provider) {
        this.nodeProviders.add(provider);
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


    public Instances getInstances() {
        return instances;
    }


    public void setInstances(Instances instances) {
        this.instances = instances;
    }


    public enum Instances {
        onePerHost, onePerVM, onePerCapacity;
    }
}
