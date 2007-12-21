package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.objectweb.proactive.core.util.URIBuilder;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.AbstractData;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ActiveObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.HostObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ModelRecorder;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.NodeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.RuntimeObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Host3dController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractHost3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractNode3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractRuntime3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.ActiveObject3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Arrow3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Grid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Host3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Node3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Runtime3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.State;


public class Host3dController implements Observer {
    private WorldObject root;
    private Grid3D rootGrid;
    private HashMap<String, ActiveObject3D> activeObjects;
    private HostObject host; // model object
    private AbstractHost3D host3d; // 3dFigure

    public Host3dController(HostObject host, Grid3D grid) {
        rootGrid = grid;

        activeObjects = new HashMap<String, ActiveObject3D>();
        host3d = new Host3D(host.getKey());
        rootGrid.addSubFigure(host.getKey(), host3d);
        host.addObserver(this);
    }

    @Override
    public void update(final Observable o, final Object arg) {
        if (!(arg instanceof MVCNotification)) {
            return;
        }
        final MVCNotification notif = (MVCNotification) arg;
        MVCNotificationTag mvcNotif = notif.getMVCNotification();

        // System.out.println("!!!!!!!!!! Notification received from :
        // "+o.toString()+" : "+mvcNotif + notif.getData());
        switch (mvcNotif) {
        case ADD_CHILD: {
            if ((o instanceof HostObject) && (notif.getData() != null)) {
                String runtimeKey = (String) notif.getData();
                String hostKey = ((AbstractData) o).getKey();
                ((HostObject) o).getMonitoredChild(runtimeKey)
                 .addObserver(Host3dController.this);
                AbstractHost3D parentHost = (AbstractHost3D) rootGrid.getSubFigure(hostKey);
                if (parentHost != null) {
                    parentHost.addSubFigure(runtimeKey,
                        new Runtime3D(runtimeKey));
                } else {
                    System.out.println("No 3d figure found for host " +
                        hostKey);
                }
            } else if ((o instanceof RuntimeObject) &&
                    (notif.getData() != null)) // New Node Object
             {
                String nodeKey = (String) notif.getData();

                // Child is a NodeObject
                RuntimeObject ro = (RuntimeObject) o;
                NodeObject nodeObj = (NodeObject) ro.getChild(nodeKey);
                nodeObj.addObserver(Host3dController.this);
                String hostKey = ro.getParent().getKey();
                // System.out.println("Node object added "+ nodeObj.toString());
                rootGrid.getSubFigure(hostKey).getSubFigure(ro.getKey())
                        .addSubFigure(nodeKey, new Node3D(nodeKey));
            } else if ((o instanceof NodeObject) && (notif.getData() != null)) // New
                                                                               // Active
                                                                               // Object
             {
                String aoKey = (String) notif.getData();
                NodeObject nodeObj = (NodeObject) o;
                RuntimeObject rtObj = nodeObj.getParent();
                HostObject hostObj = rtObj.getParent();
                ActiveObject3D ao3dFigure = new ActiveObject3D(aoKey);

                ActiveObject ao = (ActiveObject) nodeObj.getChild(aoKey);
                ao.addObserver(this);

                rootGrid.getSubFigure(hostObj.getKey())
                        .getSubFigure(rtObj.getKey()).getSubFigures()
                        .get(nodeObj.getKey()).addSubFigure(aoKey, ao3dFigure);

                activeObjects.put(aoKey, ao3dFigure);
            }
            break;
        }
        case ADD_CHILDREN: {
            if ((o instanceof NodeObject) && (notif.getData() != null)) {
                NodeObject nodeObj = (NodeObject) o;
                RuntimeObject rtObj = nodeObj.getParent();
                HostObject hostObj = rtObj.getParent();

                // HostObject hostObj=this.host;
                ArrayList<String> keys = (ArrayList) notif.getData();
                for (int k = 0; k < keys.size(); k++) {
                    String aoKey = keys.get(k);
                    ActiveObject ao = (ActiveObject) nodeObj.getChild(aoKey);
                    ao.addObserver(this);
                    ActiveObject3D ao3dFigure = new ActiveObject3D(aoKey);
                    // TODO:optimize this
                    rootGrid.getSubFigure(hostObj.getKey())
                            .getSubFigure(rtObj.getKey()).getSubFigures()
                            .get(nodeObj.getKey())
                            .addSubFigure(aoKey, ao3dFigure);
                    activeObjects.put(aoKey, ao3dFigure);
                    System.out.println("--------3d-------> add activeObject: " +
                        aoKey);
                } // [for all keys]
            } // [if ((o instanceof HostObject) && (notif.getData() != null))]
            break;
        } // [case ADD_CHILDREN]
        case REMOVE_CHILD: {
            System.out.println("Remove child " + (String) notif.getData());
            // System.out.println("child removed");
            if ((o instanceof WorldObject) && (notif.getData() != null)) {
                String hostKey = (String) notif.getData();

                // System.out.println("----------->> host removed hosts:
                // "+hostKey);
                ((WorldObject) o).getMonitoredChild(hostKey)
                 .deleteObserver(Host3dController.this);
            } else if ((o instanceof ActiveObject) &&
                    (notif.getData() != null)) // Active
                                               // Object
                                               // Deleted
             {
                String aoKey = (String) notif.getData();
                NodeObject nodeObj = (NodeObject) o;
                RuntimeObject rtObj = nodeObj.getParent();
                HostObject hostObj = rtObj.getParent();

                AbstractHost3D ahv = (AbstractHost3D) rootGrid.getSubFigure(hostObj.getKey());
                AbstractRuntime3D arv = (AbstractRuntime3D) ahv.getSubFigure(rtObj.getKey());
                AbstractNode3D anv = (AbstractNode3D) arv.getSubFigures()
                                                         .get(nodeObj.getKey());
                anv.removeSubFigure(aoKey);
                ActiveObject ao = (ActiveObject) nodeObj.getChild(aoKey);
                ao.deleteObserver(Host3dController.this);
            }
            activeObjects.remove(notif.getData());
            break;
        }
        case STATE_CHANGED: {
            new Thread() {
                    public void run() {
                        if ((o instanceof ActiveObject) &&
                                (notif.getData() != null)) {
                            // ActiveObject ao = (ActiveObject) o;
                            // NodeObject node = ao.getParent();
                            // RuntimeObject rt = node.getParent();
                            // HostObject host = rt.getParent();
                            String aoKey = ((ActiveObject) o).getKey();

                            // ActiveObject3D ao3d=(ActiveObject3D)
                            // rootGrid.getSubFigure(host.getKey()).getSubFigure(rt.getKey()).getSubFigure(node.getKey()).getSubFigure(ao.getKey());
                            ActiveObject3D ao3d = activeObjects.get(aoKey);
                            ao3d.setState((org.objectweb.proactive.ic2d.jmxmonitoring.util.State) notif.getData());
                        }
                    }
                }.start();
            break;
        }
        case REMOVE_CHILD_FROM_MONITORED_CHILDREN: {
            // where do I receive this from?
            // if ((o instanceof WorldObject) && (notif.getData() != null)) {
            // String hostKey = (String) notif.getData();
            // System.out.println(
            // "----------->> host removed from monitored hosts: " +
            // hostKey);
            // //
            // ((WorldObject)o).getMonitoredChild(hostKey).deleteObserver(this);
            // rootGrid.removeSubFigure(hostKey);
            // }

            // else
            break;
        } // case
        case ACTIVE_OBJECT_ADD_COMMUNICATION:
            // System.out.println(".");
            new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final ActiveObject aoSource = (ActiveObject) notif.getData();
                        final ActiveObject aoDestination = (ActiveObject) o;
                        if ((aoSource == null) || (aoDestination == null)) {
                            System.out.println("no object found for com");
                            return;
                        }

                        ActiveObject3D source3d = activeObjects.get(aoSource.getKey());
                        ActiveObject3D dest3d = activeObjects.get(aoDestination.getKey());

                        if (source3d == null) { // System.out.println("no figures
                                                // found for source com
                                                // "+aoSource.getKey());
                            return;
                        }

                        if (dest3d == null) {
                            // System.out.println("no figures found for dest com
                            // "+aoDestination.getKey());
                            return;
                        }

                        rootGrid.drawArrow(aoSource.getKey() +
                            aoDestination.getKey() +
                            new Double(Math.random()).toString(), "noName",
                            100, source3d, dest3d);
                    }
                }).start();
            // else
            break;
        } // switch
    }
}
