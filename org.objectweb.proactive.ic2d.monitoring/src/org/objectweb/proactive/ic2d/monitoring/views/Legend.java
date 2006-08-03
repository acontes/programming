/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
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
package org.objectweb.proactive.ic2d.monitoring.views;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.objectweb.proactive.ic2d.monitoring.data.Protocol;
import org.objectweb.proactive.ic2d.monitoring.data.State;
import org.objectweb.proactive.ic2d.monitoring.figures.AOFigure;
import org.objectweb.proactive.ic2d.monitoring.figures.HostFigure;
import org.objectweb.proactive.ic2d.monitoring.figures.NodeFigure;
import org.objectweb.proactive.ic2d.monitoring.figures.VMFigure;

public class Legend extends ViewPart {

	public static final String ID = "org.objectweb.proactive.ic2d.monitoring.views.Legend";

	public Legend() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
			
		parent.setLayout(new FillLayout());
				
		 // Create the ScrolledComposite to scroll horizontally and vertically
	    ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		
	    Composite child = new Composite(sc,SWT.NONE);
	    
	    FormLayout generalLayout = new FormLayout();
	    generalLayout.marginHeight = 5;
	    generalLayout.marginWidth = 5;
	    
//	    GridLayout generalLayout = new GridLayout();
//		generalLayout.numColumns = 1;
		child.setLayout(generalLayout);
	    
		/*--------- Active objects ---------*/

		Group aoDef = new Group(child, 0);
		GridLayout aoLayout = new GridLayout();
		aoLayout.numColumns = 2;
		aoDef.setLayout(/*new FillLayout(SWT.VERTICAL)*/aoLayout);
		aoDef.setText("Active objects");
		FormData aoDefFormData = new FormData();
		aoDefFormData.left = new FormAttachment(0, 0);
		aoDefFormData.right = new FormAttachment(100, 0);
		aoDef.setLayoutData(aoDefFormData);

		// Active by itself
		
		FigureCanvas ao1Container = new FigureCanvas(aoDef);
		ao1Container.setContents(new AOFigure(State.ACTIVE));

		Label ao1Text = new Label(aoDef, 0);
		ao1Text.setText("Active by itself");

		// Serving request
		
		FigureCanvas ao2Container = new FigureCanvas(aoDef);
		ao2Container.setContents(new AOFigure(State.SERVING_REQUEST));
	
		Label ao2Text = new Label(aoDef, 0);
		ao2Text.setText("Serving request");

		// Waiting for request
		
		FigureCanvas ao3Container = new FigureCanvas(aoDef);
		ao3Container.setContents(new AOFigure(State.WAITING_FOR_REQUEST));

		Label ao3Text = new Label(aoDef, 0);
		ao3Text.setText("Waiting for request");

		// Waiting for result (wait by necessity)
		
		FigureCanvas ao4Container = new FigureCanvas(aoDef);
		ao4Container.setContents(new AOFigure(State.WAITING_BY_NECESSITY_WHILE_ACTIVE));

		Label ao4Text = new Label(aoDef, 0);
		ao4Text.setText("Waiting for result\n(wait by necessity)");
		ao4Text.setSize(ao4Text.getSize().x/2, ao4Text.getSize().y);

		// Migrating
		
		FigureCanvas ao5Container = new FigureCanvas(aoDef);
		ao5Container.setContents(new AOFigure(State.MIGRATING));

		Label ao5Text = new Label(aoDef, 0);
		ao5Text.setText("Migrating");


		/*--------- Pendings Request ---------*/

		Group requestDef = new Group(child, 0);
		GridLayout requestLayout = new GridLayout();
		requestLayout.numColumns = 2;
		requestDef.setLayout(requestLayout);
		requestDef.setText("Pending Requests");		
		//requestDef.setBackground(ColorConstants.white);
		FormData requestDefFormData = new FormData();
		requestDefFormData.top = new FormAttachment(aoDef, 0);
		requestDefFormData.left = new FormAttachment(0, 0);
		requestDefFormData.right = new FormAttachment(100, 0);
		requestDef.setLayoutData(requestDefFormData);
		
		/*--------- Nodes ---------*/

		Group nodeDef = new Group(child, 0);
		GridLayout nodeLayout = new GridLayout();
		nodeLayout.numColumns = 2;
		nodeDef.setLayout(nodeLayout);
		nodeDef.setText("Nodes");
		FormData nodeDefFormData = new FormData();
		nodeDefFormData.top = new FormAttachment(requestDef, 0);
		nodeDefFormData.left = new FormAttachment(0, 0);
		nodeDefFormData.right = new FormAttachment(100, 0);
		nodeDef.setLayoutData(nodeDefFormData);
		
		// RMI Node
		
		FigureCanvas node1Container = new FigureCanvas(nodeDef);
		node1Container.setContents(new NodeFigure(Protocol.RMI));

		Label node1Text = new Label(nodeDef, 0);
		node1Text.setText("RMI Node");
		
		// HTTP Node
		
		FigureCanvas node2Container = new FigureCanvas(nodeDef);
		node2Container.setContents(new NodeFigure(Protocol.HTTP));

		Label node2Text = new Label(nodeDef, 0);
		node2Text.setText("HTTP Node");
		
		// RMI/SSH Node
		
		FigureCanvas node3Container = new FigureCanvas(nodeDef);
		node3Container.setContents(new NodeFigure(Protocol.RMISSH));

		Label node3Text = new Label(nodeDef, 0);
		node3Text.setText("RMI/SSH Node");
		
		// JINI Node
		
		FigureCanvas node4Container = new FigureCanvas(nodeDef);
		node4Container.setContents(new NodeFigure(Protocol.JINI));

		Label node4Text = new Label(nodeDef, 0);
		node4Text.setText("JINI Node");
		
		/*--------- JVMs ---------*/

		Group jvmDef = new Group(child, 0);
		GridLayout jvmLayout = new GridLayout();
		jvmLayout.numColumns = 2;
		jvmDef.setLayout(jvmLayout);
		jvmDef.setText("JVMs");
		FormData jvmDefFormData = new FormData();
		jvmDefFormData.top = new FormAttachment(nodeDef, 0);
		jvmDefFormData.left = new FormAttachment(0, 0);
		jvmDefFormData.right = new FormAttachment(100, 0);
		jvmDef.setLayoutData(jvmDefFormData);
		
		// Standard JVM
		
		FigureCanvas jvm1Container = new FigureCanvas(jvmDef);
		jvm1Container.setContents(new VMFigure());

		Label jvm1Text = new Label(jvmDef, 0);
		jvm1Text.setText("Standard JVM");
		
		// JVM started with Globus
		
		FigureCanvas jvm2Container = new FigureCanvas(jvmDef);
		VMFigure jvm2Figure = new VMFigure();
		jvm2Figure.withGlobus();
		jvm2Container.setContents(jvm2Figure);

		Label jvm2Text = new Label(jvmDef, 0);
		jvm2Text.setText("JVM started with Globus");
		
		/*--------- Hosts ---------*/

		Group hostDef = new Group(child, 0);
		GridLayout hostLayout = new GridLayout();
		hostLayout.numColumns = 2;
		hostDef.setLayout(hostLayout);
		hostDef.setText("Hosts");
		FormData hostDefFormData = new FormData();
		hostDefFormData.top = new FormAttachment(jvmDef, 0);
		hostDefFormData.left = new FormAttachment(0, 0);
		hostDefFormData.right = new FormAttachment(100, 0);
		hostDef.setLayoutData(hostDefFormData);
		
		// Standard Host
		
		FigureCanvas hostContainer = new FigureCanvas(hostDef);
		hostContainer.setContents(new HostFigure());

		Label hostText = new Label(hostDef, 0);
		hostText.setText("Standard Host");
		
		/*--------- Hosts ---------*/

		Group noRespondingDef = new Group(child, 0);
		GridLayout noRespondingLayout = new GridLayout();
		noRespondingLayout.numColumns = 2;
		noRespondingDef.setLayout(nodeLayout);
		noRespondingDef.setText("Not Responding");	
		FormData noRespondingDefFormData = new FormData();
		noRespondingDefFormData.top = new FormAttachment(hostDef, 0);
		noRespondingDefFormData.left = new FormAttachment(0, 0);
		noRespondingDefFormData.right = new FormAttachment(100, 0);
		noRespondingDef.setLayoutData(noRespondingDefFormData);
		
		// Active Object
		
		FigureCanvas aoNoRespondingContainer = new FigureCanvas(noRespondingDef);
		aoNoRespondingContainer.setContents(new AOFigure(State.NOT_RESPONDING));

		Label aoNoRespondingText = new Label(noRespondingDef, 0);
		aoNoRespondingText.setText("Active Object");
		
		// JVM
		
		FigureCanvas jvmNoRespondingContainer = new FigureCanvas(noRespondingDef);
		VMFigure jvmFigure = new VMFigure();
		jvmFigure.notResponding();
		jvmNoRespondingContainer.setContents(jvmFigure);

		Label jvmNoRespondingText = new Label(noRespondingDef, 0);
		jvmNoRespondingText.setText("JVM");
		
		
		
		/* --------------------------------*/
		
	    // Set the child as the scrolled content of the ScrolledComposite
	    sc.setContent(child);

	    // Set the minimum size
	    child.setSize(child.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	    sc.setMinSize(child.getSize().x, child.getSize().y);

	    // Expand both horizontally and vertically
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
		
	}


	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
