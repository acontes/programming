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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Iterator;

import org.objectweb.proactive.ic2d.data.HostObject;
import org.objectweb.proactive.ic2d.data.WorldObject;
import org.objectweb.proactive.ic2d.gui.popupmenu.WorldPopupMenu;

public class WorldPanel extends AbstractDataObjectPanel{
	
	/** The alignment of the panel's children (horizontal or vertical) */
	private String alignLayout; //keep state of layout H or V
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public WorldPanel(WorldObject worldObject){
		super("WorldObject");
		
		setBackground(Color.WHITE);
		createBorder("World Panel", Color.BLACK);
		
		this.alignLayout = "H"; //horizontal alignment
		
		setLayout(new MyFlowLayout(FlowLayout.CENTER, 25, 15));
		
		WorldPopupMenu popup = new WorldPopupMenu(this, this.name);
		addMouseListener(popup.getMenuMouseListener());
	}
	
	//
	// -- PUBLIC METHODS -----------------------------------------------
	//  
	
	public HostPanel addHostPanel(HostObject hostObject) {
		HostPanel hostPanel = new HostPanel(this, hostObject);
		this.addChild(hostObject.getKey(), hostPanel);
		return hostPanel;
	}
	
	public void destroyObject() {
		destroy();
	}
	
	public String getAlignLayout() {
		return this.alignLayout;
	}
	
	/**
     * Change the layout
     * @param align
     */
    public void alignLayoutChild(String align) {
        alignLayout = align;
        Iterator iterator = childrenIterator();
        while (iterator.hasNext()) {
            HostPanel hostchild = (HostPanel) iterator.next();
            if (hostchild.getAlignLayout() != align) {
                hostchild.alignLayout(align);
            }
        }
    }
	
	//
    // -- INNER CLASSES -----------------------------------------------
    //
    public class MyFlowLayout extends java.awt.FlowLayout {


        /**
         * Creates a new flow layout manager with the indicated alignment
         * and the indicated horizontal and vertical gaps.
         * <p>
         * The value of the alignment argument must be one of
         * <code>FlowLayout.LEFT</code>, <code>FlowLayout.RIGHT</code>,
         * or <code>FlowLayout.CENTER</code>.
         * @param      align   the alignment value.
         * @param      hgap    the horizontal gap between components.
         * @param      vgap    the vertical gap between components.
         */
        public MyFlowLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        /** TODO
         * Lays out the container. This method lets each component take
         * its preferred size by reshaping the components in the
         * target container in order to satisfy the constraints of
         * this <code>FlowLayout</code> object.
         * @param target the specified component being laid out.
         * @see java.awt.Container
         * @see java.awt.Container#doLayout
         */
        /*public void layoutContainer(java.awt.Container target) {
            if (controller.isLayoutAutomatic()) {
                super.layoutContainer(target);
            } else {
                synchronized (target.getTreeLock()) {
                    int nmembers = target.getComponentCount();
                    for (int i = 0; i < nmembers; i++) {
                        java.awt.Component m = target.getComponent(i);
                        if (m.isVisible()) {
                            java.awt.Dimension d = m.getPreferredSize();
                            m.setSize(d.width, d.height);
                        }
                    }
                }
            }
        }*/

        /**
         * Returns the preferred dimensions for this layout given the components
         * in the specified target container.
         * @param target the component which needs to be laid out
         * @return    the preferred dimensions to lay out the
         *                    subcomponents of the specified container.
         * @see java.awt.Container
         * @see #minimumLayoutSize
         * @see java.awt.Container#getPreferredSize
         */
        public Dimension preferredLayoutSize(Container target) {
            synchronized (target.getTreeLock()) {
                int maxX = 0;
                int maxY = 0;
                int nmembers = target.getComponentCount();
                for (int i = 0; i < nmembers; i++) {
                    Component m = target.getComponent(i);
                    if (m.isVisible()) {
                        int x = m.getX();
                        int y = m.getY();

                        //if (x < minX) minX = x;
                        //if (y < minY) minY = y;
                        x += m.getWidth();
                        y += m.getHeight();
                        if (x > maxX) {
                            maxX = x;
                        }
                        if (y > maxY) {
                            maxY = y;
                        }
                    }
                }
                return new Dimension(maxX, maxY);
            }
        }

        /**
         * Returns the minimum dimensions needed to layout the components
         * contained in the specified target container.
         * @param target the component which needs to be laid out
         * @return    the minimum dimensions to lay out the
         *                    subcomponents of the specified container.
         * @see #preferredLayoutSize
         * @see java.awt.Container
         * @see java.awt.Container#doLayout
         */
        public Dimension minimumLayoutSize(Container target) {
            return preferredLayoutSize(target);
        }
    }
	
}