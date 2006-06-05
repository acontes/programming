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
import java.awt.Font;
import java.awt.Frame;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import org.objectweb.proactive.ic2d.controller.AbstractDataController;
import org.objectweb.proactive.ic2d.data.AbstractDataObject;

/**
 * A panel for the representation of a DataObject
 */
public abstract class AbstractDataObjectPanel  extends JPanel{
	
	//
	// -- CONSTANTS -----------------------------------------------
	//
	
	/** Panel size */
	protected static final int MINIMUM_WIDTH = 100;
	protected static final int MINIMUM_HEIGHT = 40;
	protected Dimension minimumSize = new Dimension(MINIMUM_WIDTH,MINIMUM_HEIGHT);
	
	/** Background color */
	private static final Color INFO_PANEL_BG_COLOR = new Color(0xd0,0xd0, 0xd0);
	
	/** Font size */
	public static final Font REGULAR_FONT = new Font("SansSerif",Font.BOLD, 12);
	public static final Font BIG_FONT = new Font("SansSerif",Font.BOLD, 14);
	public static final Font VERY_BIG_FONT = new Font("SansSerif",Font.BOLD, 16);
	public static final Font SMALL_FONT = new Font("SansSerif",Font.BOLD, 10);
	public static final Font VERY_SMALL_FONT = new Font("SansSerif",Font.BOLD, 8);
	
	//
	// -- ATTRIBUTES -----------------------------------------------
	//
	
	/** The object's name */
	protected String name;
	
	/** The controller (MVC) */
	protected AbstractDataController controller; //TODO !!!
	
	/** The object's parent */
	protected AbstractDataObjectPanel parent;
	/** The object's children (HashMap<String, AbstractDataObjectPanel>)*/
	private HashMap children;
	
	/** The object's font */
	protected Font defaultFont;
	protected Font actualFont;
	
	/** true if this object has been destroyed, false otherwise */
	protected boolean isDestroyed;
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	/**
	 * TODO comments
	 * @param name
	 */
	protected AbstractDataObjectPanel(String name) {
		//TODO when no parent is specified, what we have to do?
		this.name = name;
		this.children = new HashMap();
		setSize(minimumSize);
		setToolTipText(name);
	}
	
	/**
	 * TODO comments
	 * @param parent
	 * @param name
	 */
	public AbstractDataObjectPanel(AbstractDataObjectPanel parent, String name) {
		this(name);
		this.parent = parent;
		
		this.defaultFont = parent.defaultFont;
		this.actualFont = this.defaultFont;
	}
	
	/**
	 * TODO comments
	 * @param parentFrame
	 * @param name
	 */
	public AbstractDataObjectPanel(Frame parentFrame, String name) {
		this(name);
		
		this.defaultFont = REGULAR_FONT;
		this.actualFont = this.defaultFont;
	}
	
	//
	// -- PUBLICS METHODS -----------------------------------------------
	//
	
	/**
	 * Returns the panel's parent
	 * @return the panel's parent
	 */
	public AbstractDataObjectPanel getParentPanel(){
		return parent;
	}
	
	/**
	 * Returns the top level parent
	 * @return the top level parent
	 */
	public AbstractDataObjectPanel getTopLevelParent() {
		if (parent == null) {
			return this;
		} else {
			return parent.getTopLevelParent();
		}
	}
	
	/**
	 * Returns an iterator over the object's children
	 * @return an iterator over the object's children
	 */
	public Iterator childrenIterator() {
		return children.values().iterator();
	}
	
	/**
	 * Destroys this object
	 */
	public abstract void destroyObject();
	
	
	/**
	 * @return  Returns the minimumSize.
	 */
	public Dimension getMinimumSize() {
		if (children.isEmpty()) {
			Dimension d = getMinimumSizeInternal();
			if (d == null) {
				return super.getMinimumSize();
			} else {
				return d;
			}
		} else {
			return super.getMinimumSize();
		}
	}
	
	public Dimension getPreferredSize() {
		if (children.isEmpty()) {
			Dimension d = getMinimumSizeInternal();
			if (d == null) {
				return super.getPreferredSize();
			} else {
				return d;
			}
		} else {
			return super.getPreferredSize();
		}
	}
	
	public synchronized void setFontSize(Font font) {
        defaultFont = font;
        Iterator iterator = childrenIterator();
        while (iterator.hasNext()) {
            AbstractDataObjectPanel o = (AbstractDataObjectPanel) iterator.next();
            o.setFontSize(font);
        }
        revalidate();
        repaint();
    }
	
	public Font getDefaultFont (){
		return this.defaultFont;
	}
	
	/**
	 * Update the panel, with a 'revalidate' and a 'repaint'
	 */
	public void updatePanel(){
		revalidate();
		repaint();
	}
	
	//TODO !!!
	public void setController(AbstractDataController controller) {
		this.controller = controller;
	}
	
	//
	// -- PROTECTED METHODS -----------------------------------------------
	//
	
	/**
	 * Destroys this object.
	 */
	protected void destroy() {
		if (isDestroyed) {
			return;
		}
		isDestroyed = true;
		destroyCollection(childrenIterator());
		children.clear();
		parent = null;
	}
	
	
	/**
	 * Get Child
	 * @param key
	 */
	protected synchronized AbstractDataObjectPanel getChild(
			AbstractDataObject key) {
		return (AbstractDataObjectPanel) children.get(key);
	}
	
	
	/**
	 * Add a child to this object, and show it
	 * @param key
	 * @param child
	 */
	protected synchronized void addChild(String key, AbstractDataObjectPanel child) {
		putChild(key, child);
		add(child);
		updatePanel();
	}
	
	/**
	 * Remove a child to this object
	 * @param key
	 */
	protected synchronized AbstractDataObjectPanel removeChild(
			AbstractDataObject key) {
		AbstractDataObjectPanel panel = (AbstractDataObjectPanel) children.remove(key);
		if (panel != null) {
			panel.destroy();
			remove(panel);
			revalidate();
			repaint();
		}
		return panel;
	}
	
	
	protected Dimension getMinimumSizeInternal() {
		return minimumSize;
	}
	
	protected void createBorder(String name, Color color) {
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createMatteBorder(1, 1, 1, 1, color), name,
				TitledBorder.DEFAULT_JUSTIFICATION,
				TitledBorder.DEFAULT_POSITION, defaultFont));
	}
	
	
	//
	// -- PRIVATE METHODS -----------------------------------------------
	//
	
	/**
	 * Add a child to this object
	 * @param key 
	 * @param child
	 */
	private synchronized void putChild(String key, AbstractDataObjectPanel child) {
		if (isDestroyed) {
			return;
		}
		children.put(key, child);
	}
	
	/**
	 * Destroys all objects known by this object
	 * @param iterator an iterator over all objects to destroy
	 */
	private synchronized void destroyCollection(Iterator iterator) {
		while (iterator.hasNext()) {
			AbstractDataObjectPanel o = (AbstractDataObjectPanel) iterator.next();
			o.destroyObject();
		}
	}
	
	
}
