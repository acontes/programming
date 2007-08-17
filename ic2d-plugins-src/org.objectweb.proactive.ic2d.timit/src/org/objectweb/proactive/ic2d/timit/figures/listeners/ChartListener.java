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
package org.objectweb.proactive.ic2d.timit.figures.listeners;

import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.objectweb.proactive.ic2d.timit.data.ChartObject;
import org.objectweb.proactive.ic2d.timit.editparts.ChartEditPart;
import org.objectweb.proactive.ic2d.timit.figures.ChartFigure;
import org.objectweb.proactive.ic2d.timit.views.TimItView;


public class ChartListener extends EditPartListener.Stub
    implements MouseListener {
    public static final int SELECTED_STATE = 1;
    public static final int UNSELECTED_STATE = 0;
    public static ChartEditPart lastSelected;
    protected ChartEditPart chartEditPart;

    public ChartListener(ChartEditPart chartEditPart) {
        this.chartEditPart = chartEditPart;
    }

    public final void mouseDoubleClicked(final MouseEvent arg0) {
//		IWorkbench iworkbench = PlatformUI.getWorkbench();
//		IWorkbenchWindow currentWindow = iworkbench.getActiveWorkbenchWindow();
//		IWorkbenchPage page = currentWindow.getActivePage();		
//		try {
//			IViewPart part = page.showView("org.objectweb.proactive.ic2d.timit.views.TimerTreeView");
//			TimerTreeHolder.getInstance().addChartObject((ChartObject)this.chartEditPart.getModel());					
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }

    public final void mousePressed(final MouseEvent arg0) {
        // Left click selection
        this.chartEditPart.setSelected(SELECTED_STATE);
        //TimerTreeHolder.getInstance().setSelectedIndex((ChartObject)this.chartEditPart.getModel());
    }

    public final void mouseReleased(final MouseEvent arg0) {
    }

    @Override
    public final void selectedStateChanged(final EditPart editPart) {
        if (this.chartEditPart.getSelected() == SELECTED_STATE) {
            if (lastSelected != null) {
                // Set lastSelected Unselected
                lastSelected.setSelected(UNSELECTED_STATE);
            }
            // Save last selected editpart
            lastSelected = this.chartEditPart;
            // Perform button and figure selection
            TimItView.refreshSelectedButton.setEnabled(true);
            ChartObject model = (ChartObject)this.chartEditPart.getModel();            
            if ( model != null && model.getTimerLevel().equals("Basic") ){
            	TimItView.timerLevelButton.setText("Switch to Detailed");
            } else {
            	TimItView.timerLevelButton.setText("Switch to Basic   ");
            }
            TimItView.timerLevelButton.setEnabled(true); 
            ((ChartFigure) this.chartEditPart.getFigure()).setSelected();
        } else {
            ((ChartFigure) this.chartEditPart.getFigure()).setUnselected();
        }
    }
}
