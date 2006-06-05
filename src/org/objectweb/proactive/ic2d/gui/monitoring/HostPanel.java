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
package org.objectweb.proactive.ic2d.gui.monitoring;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Iterator;

import javax.swing.BoxLayout;

import org.objectweb.proactive.ic2d.data.HostObject;
import org.objectweb.proactive.ic2d.gui.popupmenu.HostPopupMenu;

public class HostPanel extends AbstractDataObjectPanel {
	
	/** Panel size */
	protected static final int MINIMUM_WIDTH = 150;
	protected static final int MINIMUM_HEIGHT = 80;
	protected Dimension minimumSize = new Dimension(MINIMUM_WIDTH,MINIMUM_HEIGHT);
	
	/** Panel color **/
	private static final Color color = new Color(0xd0, 0xd0, 0xd0);
	
	/** The alignment of the panel's children (horizontal or vertical) */
	private String alignLayout;
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public HostPanel(WorldPanel parentPanel, HostObject hostObject) {
		super(parentPanel, hostObject.getKey());
	
		alignLayout(((WorldPanel)parent).getAlignLayout());
		setBackground(color);
		
		createBorder(hostObject.getFullName() , new Color(0, 0, 128));
		
		HostPopupMenu popup = new HostPopupMenu(this, this.name);
		addMouseListener(popup.getMenuMouseListener());
	}
	
	//
	// -- PUBLIC METHODS -----------------------------------------------
	//
	
	public void destroyObject() {
		destroy();
	}
	
	/**
	 * Return the panel's color
	 * @return the panel's color
	 */
	public static Color getColor() {
		return color;
	}
	
	/**
	 * TODO comment
	 * @return
	 */
	public String getAlignLayout() {
		return this.alignLayout;
	}
	
	/**
	 * TODO comment
	 * @param align
	 */
	public void alignLayout(String align) {
		this.alignLayout = align;
		setPreferredSize(null); //TODO ???
		if (align.compareTo("H") == 0) {
            setLayout(new FlowLayout(FlowLayout.CENTER, 9, 5));
        } else {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        }
		this.alignLayoutChild(align);
        revalidate();
        repaint();
        this.parent.revalidate();
        this.parent.repaint();
	}
	
	//
	// -- PROTECTED METHODS -----------------------------------------------
	//
	
	protected Dimension getMinimumSizeInternal() {
        return minimumSize;
    }
	
	//
	// -- PRIVATE METHODS -----------------------------------------------
	//
	
	/**
	 * TODO comment
	 * @param align
	 */
    private void alignLayoutChild(String align) {
        Iterator iterator = childrenIterator();
        while (iterator.hasNext()) {
            ((VMPanel) iterator.next()).alignLayout(align);
        }
    }
	
}
