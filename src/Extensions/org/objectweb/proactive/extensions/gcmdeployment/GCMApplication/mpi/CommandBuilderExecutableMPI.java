/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.mpi;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.proactive.extensions.gcmdeployment.PathElement;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.GCMApplicationInternal;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.NodeProvider;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilder;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfo;


public class CommandBuilderExecutableMPI implements CommandBuilder {
    protected static final String NODE_NAME = "mpi";
    
    /** List of providers to be used */
    private List<NodeProvider> providers;
    private String command;

    /** The path to the command */
    private PathElement path;

    /** The arguments*/
    private List<String> args;

    public CommandBuilderExecutableMPI() {
        providers = new ArrayList<NodeProvider>();
        args = new ArrayList<String>();
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setPath(PathElement pe) {
        path = pe;
    }

    public void addArg(String arg) {
        args.add(arg);
    }

    public void addDescriptor(NodeProvider nodeProvider) {
        providers.add(nodeProvider);
    }

    public String buildCommand(HostInfo hostInfo, GCMApplicationInternal gcma) {
        StringBuilder sb = new StringBuilder();
        if (path != null) {
            sb.append(PathElement.appendPath(path.getFullPath(hostInfo, this), command, hostInfo));
        } else {
            sb.append(command);
        }

        for (String arg : args) {
            sb.append(" " + arg);
        }

        return sb.toString();
    }

    public String getPath(HostInfo hostInfo) {
        if (path != null) {
            return path.getFullPath(hostInfo, this);
        } else {
            return "";
        }
    }

}