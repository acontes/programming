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
package org.objectweb.proactive.ic2d.dgc.editparts;

import java.util.Observable;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.objectweb.proactive.ic2d.dgc.data.ObjectGraph;
import org.objectweb.proactive.ic2d.dgc.figures.DgcAOFigure;
import org.objectweb.proactive.ic2d.monitoring.data.AOObject;
import org.objectweb.proactive.ic2d.monitoring.editparts.AOEditPart;


public class DgcAOEditPart extends AOEditPart {
    public DgcAOEditPart(AOObject model) {
        super(model);
    }

    protected IFigure createFigure() {
        return new DgcAOFigure(getCastedModel().getFullName());
    }

	protected Color getArrowColor() {
		return new Color(Display.getCurrent(), 0, 0, 255);
	}
    
    public void update(Observable o, Object arg) {
    	ObjectGraph.addObject((AOObject) o);
    	AOObject model = this.getCastedModel();
    	((DgcAOFigure) super.getCastedFigure()).updateDgcState(model);
    	super.update(o, arg);
    }
}
