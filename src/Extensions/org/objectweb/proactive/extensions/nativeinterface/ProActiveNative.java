/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
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
 */
package org.objectweb.proactive.extensions.nativeinterface;

import java.util.List;
import java.util.Vector;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.nativeinterface.spmd.NativeSpmd;


public class ProActiveNative {
    private static ProActiveNativeManager manager;
    
    public static Vector<?> deploy(List<NativeSpmd> spmdList, boolean componentBased) {
        if (manager == null) {
            // create manager
            try {
                manager = (ProActiveNativeManager) PAActiveObject.newActive(ProActiveNativeManager.class.getName(),
                        new Object[] {});
            } catch (ActiveObjectCreationException e) {
                e.printStackTrace();
            } catch (NodeException e) {
                e.printStackTrace();
            }
        } 
        
        manager.deploy(spmdList, componentBased);
        return null;
    }

    public static ProActiveNativeManager getManager() {
        return manager;
    }

	public static boolean deploymentFinished() {
		boolean running = true;
		
		while(running) {
			running = (!manager.deploymentFinished());
			try {
				//TODO refactor in a more elegant way
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return true;
	}

	public static void waitFinished() {
		boolean running = true;
		
		while(running) {
			try {
				running = !manager.isFinished();
				//TODO refactor in a more elegant way
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
