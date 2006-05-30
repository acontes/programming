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
package org.objectweb.proactive.ic2d.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.util.HashMap;
import java.util.Iterator;

import org.objectweb.proactive.ic2d.data.AbstractDataObject;

/**
 * A panel for the representation of a DataObject
 */
public abstract class AbstractDataObjectPanel  extends javax.swing.JPanel{
	
	//
	// -- CONSTANTS -----------------------------------------------
	//
	
	/** Panel size */
	protected static final int MINIMUM_WIDTH = 100;
	protected static final int MINIMUM_HEIGHT = 40;
	protected Dimension minimumSize = new Dimension(MINIMUM_WIDTH,MINIMUM_HEIGHT);
	
	/** Font size */
	protected static final Font REGULAR_FONT = new Font("SansSerif",Font.BOLD, 12);
	protected static final Font BIG_FONT = new Font("SansSerif",Font.BOLD, 14);
	protected static final Font VERY_BIG_FONT = new Font("SansSerif",Font.BOLD, 16);
	protected static final Font SMALL_FONT = new Font("SansSerif",Font.BOLD, 10);
	protected static final Font VERY_SMALL_FONT = new Font("SansSerif",Font.BOLD, 8);
	
	/** Background color */
	private static final Color INFO_PANEL_BG_COLOR = new Color(0xd0,0xd0, 0xd0);
	
	//
	// -- ATTRIBUTES -----------------------------------------------
	//
	
	/** The object's name */
	protected String name;
	
	/** The object's parent */
	protected AbstractDataObjectPanel parent;
	/** The parent's frame */
	protected Frame parentFrame;
	/** The object's childs */
	private HashMap childs;
	
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
	private AbstractDataObjectPanel(String name) {
		this.name = name;
		this.childs = new java.util.HashMap();
		setSize(minimumSize);
		setToolTipText(name);
	}
	
	/**
	 * TODO comments
	 * @param parentDataObjectPanel
	 * @param name
	 */
	public AbstractDataObjectPanel(
			AbstractDataObjectPanel parentDataObjectPanel, String name) {
		this(name);
		this.parent = parentDataObjectPanel;
		
		this.defaultFont = parentDataObjectPanel.defaultFont;
		this.actualFont = this.defaultFont;
	}
	
	/**
	 * TODO comments
	 * @param parentFrame
	 * @param name
	 */
	public AbstractDataObjectPanel(
			Frame parentFrame, String name) {
		this(name);
		this.parentFrame = parentFrame;
		
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
     * Returns an iterator over the object's childs
     * @return an iterator over the object's childs
     */
    public Iterator childsIterator() {
        return childs.values().iterator();
    }
    
    /**
     * Destroys this object
     */
    public abstract void destroyObject();
    
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
        destroyCollection(childsIterator());
        childs.clear();
        parent = null;
    }
    
    
    /**
     * Destroys all objects known by this object
     * @param iterator an iterator over all objects to destroy
     */
    protected synchronized void destroyCollection(Iterator iterator) {
        while (iterator.hasNext()) {
            AbstractDataObjectPanel o = (AbstractDataObjectPanel) iterator.next();
            o.destroyObject();
        }
    }
    
    /**
     * Get Child
     * @param key
     */
    protected synchronized AbstractDataObjectPanel getChild(
        AbstractDataObject key) {
        return (AbstractDataObjectPanel) childs.get(key);
    }
    
 
	
    
    /**
     * Add a child to this object
     * @param key 
     * @param child
     */
    public synchronized void putChild(String key, AbstractDataObjectPanel child) {
        if (isDestroyed) {
            return;
        }
        childs.put(key, child);
    }
    
    
    /**
     * Add a child to this object, and show it
     * @param key
     * @param child
     */
    protected synchronized void addChild(AbstractDataObject key,
        AbstractDataObjectPanel child) {
        putChild(key, child);
        add(child);
        revalidate();
        repaint();
    }

    /**
     * Remove a child to this object
     * @param key
     */
    protected synchronized AbstractDataObjectPanel removeChild(
        AbstractDataObject key) {
        AbstractDataObjectPanel panel = (AbstractDataObjectPanel) childs.remove(key);
        if (panel != null) {
            panel.destroy();
            remove(panel);
            revalidate();
            repaint();
        }
        return panel;
    }
    
}
