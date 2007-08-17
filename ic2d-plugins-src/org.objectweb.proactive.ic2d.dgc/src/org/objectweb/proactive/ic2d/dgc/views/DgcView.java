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
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.ic2d.dgc.views;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.EditPartFactory;
import org.eclipse.swt.widgets.Composite;
import org.objectweb.proactive.ic2d.dgc.data.ObjectGraph;
import org.objectweb.proactive.ic2d.dgc.editparts.DgcIC2DEditPartFactory;
import org.objectweb.proactive.ic2d.monitoring.data.AOObject;
import org.objectweb.proactive.ic2d.monitoring.views.MonitoringView;


public class DgcView extends MonitoringView implements Runnable {
    public static final String ID = "org.objectweb.proactive.ic2d.dgc.views.DgcView";

    public DgcView() {
        new Thread(this, "DgcView Updater").start();
    }

    protected EditPartFactory getEditPartFactory() {
        return new DgcIC2DEditPartFactory(this);
    }

    private void drawGraph(Map<AOObject, Collection<AOObject>> graph) {
        Set<Map.Entry<AOObject, Collection<AOObject>>> s = graph.entrySet();
        for (Map.Entry<AOObject, Collection<AOObject>> e : s) {
            AOObject ao = e.getKey();
            ao.resetCommunications();
        }
        for (Map.Entry<AOObject, Collection<AOObject>> e : s) {
            AOObject srcAO = e.getKey();
            for (AOObject destAO : e.getValue()) {
                srcAO.addCommunication(destAO);
            }
        }
    }

    public void createPartControl(Composite parent) {
    	super.createPartControl(parent);
		this.setPartName("DGC View");
    }

    public void run() {
        for (;;) {
            Map<AOObject, Collection<AOObject>> graph = ObjectGraph.getObjectGraph(this.getWorld());
            drawGraph(graph);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
