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
package org.objectweb.proactive.ic2d.timit.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Display;
import org.objectweb.proactive.ic2d.timit.data.ChartObject;
import org.objectweb.proactive.ic2d.timit.figures.ChartFigure;
import org.objectweb.proactive.ic2d.timit.figures.listeners.ChartListener;

public class ChartEditPart extends AbstractGraphicalEditPart implements
		Runnable {
	public static final int CHART_HEIGHT = 250;

	protected ChartObject chartObject;

	public ChartEditPart(ChartObject chartObject) {
		super.setModel(chartObject);
		this.chartObject = chartObject;
		this.chartObject.setEp(this);
	}

	@Override
	protected final IFigure createFigure() {
		ChartFigure chartFigure = new ChartFigure(this.chartObject
				.provideChart());
		chartFigure.setPreferredSize(
				this.getViewer().getControl().getBounds().width - 20,
				CHART_HEIGHT);
		chartFigure.setMinimumSize(new Dimension(300, CHART_HEIGHT));
		// ////////////////////////////////////////////////////
		// DONT FORGET TO REMOVE WHEN THIS EDIT PART IS DEACTIVATED
		ChartListener chartListener = new ChartListener(this);
		this.addEditPartListener(chartListener); // mouse event listening
		chartFigure.addMouseListener(chartListener); // selection handleling
		// ////////////////////////////////////////////////////

		return chartFigure;
	}

	@Override
	protected final void createEditPolicies() {
	}

	public final void asyncRefresh() {
		Display.getDefault().asyncExec(this);

	}

	public final void setSelection() {
		((ScrollingGraphicalViewer) ChartEditPart.this.getViewer())
				.reveal(ChartEditPart.this);
		this.setSelected(ChartListener.SELECTED_STATE);
	}

	public final void run() {
		ChartFigure figure = (ChartFigure) getFigure();
		figure.setChart(this.chartObject.provideChart());
		figure.setPreferredSize(
				this.getViewer().getControl().getBounds().width - 20,
				CHART_HEIGHT);
		refresh();
		figure.repaint();
	}
}