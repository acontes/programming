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
package org.objectweb.proactive.ic2d.gui.popupmenu;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import org.objectweb.proactive.ic2d.gui.monitoring.AbstractDataObjectPanel;

public abstract class AbstractPopupMenu extends JPopupMenu{

	//
	// -- ATTRIBUTES -----------------------------------------------
	//	
	AbstractDataObjectPanel parent;
	FontSizeMenu fontSizeMenu;	
	JMenuItem titleItem;
	
    //
    // -- CONSTRUCTORS -----------------------------------------------
    //
    protected AbstractPopupMenu(AbstractDataObjectPanel parent, String name) {
        super(name);
        this.parent = parent;
        titleItem = add(name + " Menu");
        titleItem.setEnabled(false);
        
        addSeparator();
        
        this.fontSizeMenu = new FontSizeMenu();
        /* 'Change font size' item */
        add(this.fontSizeMenu);
        
        /* 'Monitor events' item */
        //TODO Do this item
        
        /* 'Informations' item */
        add(new AbstractAction("Informations", null) {
            public void actionPerformed(ActionEvent e) {
                //TODO
            }
        });
        
        addSeparator();
    }
    
    
    public MouseListener getMenuMouseListener() {
        return new PopupMenuMouseListener();
    }
    
    //
    // -- INNER CLASSES -------------------------------------------------
    //
    
    private class FontSizeMenu extends JMenu {
    	
        private ButtonGroup group = new ButtonGroup();
        private HashMap hashMenuFontSize = new HashMap();

        //
        // -- CONSTRUCTORS -----------------------------------------------
        //
        
        public FontSizeMenu() {
            super("Change font size");
            
            /* Initialize the hashMap */
            hashMenuFontSize.put(AbstractDataObjectPanel.VERY_SMALL_FONT,new String("Very small font size"));
            hashMenuFontSize.put(AbstractDataObjectPanel.SMALL_FONT, new String("Small font size"));
            hashMenuFontSize.put(AbstractDataObjectPanel.REGULAR_FONT,new String("Regular font size"));
            hashMenuFontSize.put(AbstractDataObjectPanel.BIG_FONT, new String("Large font size"));
            hashMenuFontSize.put(AbstractDataObjectPanel.VERY_BIG_FONT,new String("Very large font size"));            
            
            /* Create all items*/
            add(createItem(AbstractDataObjectPanel.VERY_SMALL_FONT, false));
            add(createItem(AbstractDataObjectPanel.SMALL_FONT, false));
            add(createItem(AbstractDataObjectPanel.REGULAR_FONT, false));
            add(createItem(AbstractDataObjectPanel.BIG_FONT, false));
            add(createItem(AbstractDataObjectPanel.VERY_BIG_FONT, false));
        }

        private JMenuItem createItem(final Font font,final boolean stat) {
            String fontName = (String) hashMenuFontSize.get(font);
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(fontName,stat);
            menuItem.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        parent.setFontSize(font);
                    }
                });
            group.add(menuItem);
            return menuItem;
        }
        
        // select the right rb button of the right font
        public void coherentMenu() {
            String txtvalue = (String) hashMenuFontSize.get(parent.getDefaultFont());
            Enumeration e = this.group.getElements();
            while (e.hasMoreElements()) {
                JRadioButtonMenuItem rb = (JRadioButtonMenuItem) e.nextElement();
                if (rb.getText() == txtvalue) {
                    rb.setSelected(true);
                }
            }
        }
    }
    
    private class PopupMenuMouseListener extends MouseAdapter {
        public void mousePressed(java.awt.event.MouseEvent e) {
            if (e.isPopupTrigger()) {
                coherentMenu();
                show(e.getComponent(), e.getX(), e.getY());
            }
        }

        public void mouseReleased(java.awt.event.MouseEvent e) {
            if (e.isPopupTrigger()) {
                coherentMenu();
                show(e.getComponent(), e.getX(), e.getY());
            }
        }

        // call for the menu cohesion
        private void coherentMenu() {
            fontSizeMenu.coherentMenu();
        }
    } // end inner class MyMouseListener
}
