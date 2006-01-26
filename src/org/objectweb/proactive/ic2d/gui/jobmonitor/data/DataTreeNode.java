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
package org.objectweb.proactive.ic2d.gui.jobmonitor.data;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.tree.DefaultMutableTreeNode;

import org.objectweb.proactive.ic2d.gui.jobmonitor.JobMonitorConstants;


/*
 * A DataTreeNode is an element in a displayed tree.
 * It is associated with a BasicMonitoredObject.
 */
public class DataTreeNode extends DefaultMutableTreeNode
    implements JobMonitorConstants {
    private static final int STATE_NEW = 0;
    private static final int STATE_REMOVED = 1;
    private static final int STATE_KEPT = 2;
    private int state = STATE_NEW;
    private BasicMonitoredObject object;

    /* Root element */
    public DataTreeNode(DataModelTraversal traversal) {
        int key = traversal.getFollowingKey(NO_KEY);
        object = BasicMonitoredObject.createRoot(key);
    }

    /* Normal element */
    public DataTreeNode(DataTreeModel model, BasicMonitoredObject value,
        Set constraints) {
        rebuild(model, value, constraints);
    }

    public void setAllRemovedStates() {
        this.state = STATE_REMOVED;
        for (int i = 0, length = getChildCount(); i < length; i++) {
            DataTreeNode child = (DataTreeNode) getChildAt(i);
            child.setAllRemovedStates();
        }
    }

    /* Find a child equal to the param */
    public DataTreeNode getChild(BasicMonitoredObject value) {
        int length = getChildCount();

        if (length == 0) {
            return null;
        }

        DataTreeNode firstChild = (DataTreeNode) getChildAt(0);
        if (firstChild.getKey() != value.getKey()) {

            /* On a level, every child is of the same key */
            return null;
        }

        for (int i = 0; i < length; i++) {
            DataTreeNode child = (DataTreeNode) getChildAt(i);
            if (child.getObject().equals(value)) {
                return child;
            }
        }
        return null;
    }

    /* Did we remove all children and read none ? */
    private boolean isEverythingRemoved() {
        for (int i = 0, length = getChildCount(); i < length; i++) {
            DataTreeNode child = (DataTreeNode) getChildAt(i);
            if (child.state != STATE_REMOVED) {
                return false;
            }
        }

        return true;
    }

    /* key : la cle de cette branche, les fils sont donc des traversal.getFollowingKey(key) */
    public void rebuild(DataTreeModel model, BasicMonitoredObject value,
        Set constraints) {
        DataModelTraversal traversal = model.getTraversal();
        int nextKey;

        if (value == null) {
            return;
        }

        if (value.isRoot()) {
            object = BasicMonitoredObject.createRoot(traversal.getFollowingKey(
                        NO_KEY));
            nextKey = NO_KEY;
        } else {
            object = value;
            nextKey = object.getKey();
        }

        DataAssociation asso = model.getAssociations();
        MonitoredObjectSet children = null;

        do {
            nextKey = traversal.getFollowingKey(nextKey);
            if (nextKey == NO_KEY) {
                children = null;
                break;
            }
            if (object.isRoot()) {
                int rootKey = object.getKey();
                object.setKey(NO_KEY);
                children = asso.getValues(object, nextKey, constraints);
                object.setKey(rootKey);
            } else {
                children = asso.getValues(object, nextKey, constraints);
            }
        } while (children.isEmpty());

        if (children != null) {
            Iterator iter = children.iterator();
            while (iter.hasNext()) {
                BasicMonitoredObject childValue = (BasicMonitoredObject) iter.next();
                DataTreeNode child = getChild(childValue);

                if (!object.isRoot()) {
                    constraints.add(object);
                }

                if (child != null) {
                    child.state = STATE_KEPT;
                    child.rebuild(model, childValue, constraints);
                } else {
                    DataTreeNode newChild = new DataTreeNode(model, childValue,
                            constraints);
                    model.insertNodeInto(newChild, this, getChildCount());
                }

                if (!object.isRoot()) {
                    constraints.remove(object);
                }
            }
        }

        boolean empty = isEverythingRemoved();

        for (int i = 0; i < getChildCount(); i++) {
            DataTreeNode child = (DataTreeNode) getChildAt(i);

            if (((child.getKey() != nextKey) && empty) ||
                    (child.state == STATE_REMOVED)) {
                model.removeNodeFromParent(child);
                i--;
            }
        }

        model.nodeChanged(this);
    }

    public void keyDisplayChanged(DataTreeModel model, int key) {
        if (getKey() == key) {
            model.nodeChanged(this);
        } else {
            int length = getChildCount();
            for (int i = 0; i < length; i++) {
                DataTreeNode child = (DataTreeNode) getChildAt(i);
                child.keyDisplayChanged(model, key);
            }
        }
    }

    public int getKey() {
        return object.getKey();
    }

    public String getName() {
        return object.getPrettyName();
    }

    public String toString() {
        if (object == null) {
            return null;
        }

        if (isRoot()) {
            if (getKey() == NO_KEY) {
                return "Empty";
            }

            return NAMES[KEY2INDEX[getKey()]];
        }

        return getName();
    }

    public BasicMonitoredObject getObject() {
        return object;
    }

    public Set makeConstraints() {
        if (isRoot()) {
            return new TreeSet();
        }

        DataTreeNode parent = (DataTreeNode) getParent();
        Set constraints = parent.makeConstraints();
        constraints.add(object);

        return constraints;
    }
}
