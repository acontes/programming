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
package org.objectweb.proactive.ic2d.dgc.figures;

import org.eclipse.draw2d.Label;
import org.objectweb.proactive.ic2d.monitoring.data.AOObject;
import org.objectweb.proactive.ic2d.monitoring.data.NodeObject;
import org.objectweb.proactive.ic2d.monitoring.figures.AOFigure;


class DgcLabel extends Label {
    private String text;

    public DgcLabel(String text) {
        super(text);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public String getSubStringText() {
        return getText();
    }

    public void setText(String text) {
        this.text = text;
    }
}


public class DgcAOFigure extends AOFigure {
    public DgcAOFigure(String text) {
        super(text);
        this.label = new DgcLabel(text + "\nDGC_STATE");
        this.initFigure();
    }

    public void updateDgcState(AOObject model) {
        NodeObject node = (NodeObject) model.getParent();
        String state = node.getSpy().getDgcState(model.getID());
        ((DgcLabel) this.label).setText(model.getFullName() + "\n" + state);
    }
}
