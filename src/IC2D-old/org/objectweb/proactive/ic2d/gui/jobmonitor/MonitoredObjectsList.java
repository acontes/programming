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
package org.objectweb.proactive.ic2d.gui.jobmonitor;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import org.objectweb.proactive.ic2d.gui.IC2DFrame;
import org.objectweb.proactive.ic2d.gui.jobmonitor.data.BasicMonitoredObject;
import org.objectweb.proactive.ic2d.gui.jobmonitor.data.MonitoredHost;
import org.objectweb.proactive.ic2d.util.MonitorThread;


public class MonitoredObjectsList extends JDialog {
    private DefaultListModel monitoredListModel;
    private DefaultListModel skippedListModel;

    /* For IC2D */
    private MonitorThread monitor;

    /* Job Monitor */
    private JobMonitorPanel jobPanel;

    /* For IC2D */
    public MonitoredObjectsList(IC2DFrame owner, MonitorThread mon) {
        super(owner, true);

        this.monitor = mon;
        this.jobPanel = null;
        this.monitoredListModel = null;
        this.skippedListModel = mon.getSkippedObjects(JobMonitorConstants.HOST);

        JTabbedPane tabbedPane = init();

        addPane(tabbedPane, "Skipped hosts", "Remove host", skippedListModel);

        pack();
    }

    /* For the Job Monitor */
    public MonitoredObjectsList(JobMonitorFrame owner, JobMonitorPanel jobPanel) {
        super(owner, true);

        this.monitor = null;
        this.jobPanel = jobPanel;
        monitoredListModel = copyList(null, jobPanel.getMonitoredHosts());
        skippedListModel = copyList(null, jobPanel.getSkippedObjects());

        JTabbedPane tabbedPane = init();

        addPane(tabbedPane, "Monitored Hosts", "Remove host", monitoredListModel);
        addPane(tabbedPane, "Skipped objects", "Remove object", skippedListModel);

        pack();
    }

    private JButton createRemoveButton(String name, final JList list) {
        JButton button = new JButton(new AbstractAction(name) {
                    public void actionPerformed(ActionEvent e) {
                        int[] selected = list.getSelectedIndices();
                        DefaultListModel model = (DefaultListModel) list.getModel();
                        for (int i = selected.length - 1; i >= 0; i--)
                            model.remove(selected[i]);
                    }
                });

        return button;
    }

    private static DefaultListModel copyList(DefaultListModel dest,
        DefaultListModel src) {
        if (dest == null) {
            dest = new DefaultListModel();
        } else {
            dest.clear();
        }

        for (int i = 0; i < src.getSize(); i++) {
            dest.addElement(src.get(i));
        }

        return dest;
    }

    private JTabbedPane init() {
        setTitle("Monitored objects");
        getContentPane()
            .setLayout(new javax.swing.BoxLayout(getContentPane(),
                javax.swing.BoxLayout.Y_AXIS));

        addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent evt) {
                    cancel();
                }
            });

        JTabbedPane tabbedPane = new javax.swing.JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        getContentPane().add(tabbedPane);

        JPanel buttons = new JPanel();
        buttons.setLayout(new javax.swing.BoxLayout(buttons,
                javax.swing.BoxLayout.X_AXIS));

        JButton ok = new JButton(new AbstractAction("OK") {
                    public void actionPerformed(ActionEvent e) {
                        ok();
                    }
                });

        JButton cancel = new JButton(new AbstractAction("Cancel") {
                    public void actionPerformed(ActionEvent e) {
                        cancel();
                    }
                });

        buttons.add(Box.createHorizontalGlue());
        buttons.add(cancel);
        buttons.add(Box.createHorizontalGlue());
        buttons.add(ok);
        buttons.add(Box.createHorizontalGlue());

        getContentPane().add(buttons);

        return tabbedPane;
    }

    private static Box horizontalCenter(JComponent component) {
        Box centeredContainer = new Box(BoxLayout.X_AXIS);
        centeredContainer.add(Box.createHorizontalGlue());
        centeredContainer.add(component);
        centeredContainer.add(Box.createHorizontalGlue());

        return centeredContainer;
    }

    private void addPane(JTabbedPane tabbedPane, String name,
        String removeButtonName, DefaultListModel listModel) {
        Box container = new Box(BoxLayout.Y_AXIS);
        JList list = new JList(listModel);
        JButton removeButton = createRemoveButton(removeButtonName, list);

        list.setCellRenderer(new MonitoredObjectsListCellRenderer());

        container.add(Box.createVerticalGlue());
        container.add(horizontalCenter(new JScrollPane(list)));

        container.add(Box.createVerticalGlue());
        container.add(horizontalCenter(removeButton));

        container.add(Box.createVerticalGlue());

        tabbedPane.addTab(name, container);
    }

    void cancel() {
        setVisible(false);
    }

    void ok() {
        if (monitoredListModel == null) {

            /* IC2D */
            DefaultListModel origSkippedHosts = monitor.getSkippedObjects(JobMonitorConstants.HOST);
            for (int i = 0, size = origSkippedHosts.getSize(); i < size; i++) {
                MonitoredHost h = (MonitoredHost) origSkippedHosts.get(i);
                if (!skippedListModel.contains(h)) {
                    monitor.removeObjectToSkip(h);
                }
            }
        } else {
            /* Job Monitor */
            copyList(jobPanel.getMonitoredHosts(), monitoredListModel);
            copyList(jobPanel.getSkippedObjects(), skippedListModel);
        }
        setVisible(false);
    }
}


class MonitoredObjectsListCellRenderer extends DefaultListCellRenderer
    implements JobMonitorConstants {
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected,
            cellHasFocus);

        BasicMonitoredObject object = (BasicMonitoredObject) value;
        int key = object.getKey();
        Icon icon = Icons.getIconForKey(key);
        if (icon != null) {
            setIcon(icon);
        }

        setText(object.getPrettyName());
        return this;
    }
}
