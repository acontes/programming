package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import org.objectweb.proactive.ic2d.jmxmonitoring.data.HostObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.ModelRecorder;
import org.objectweb.proactive.ic2d.jmxmonitoring.data.WorldObject;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.controller.Host3dController;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Grid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotification;
import org.objectweb.proactive.ic2d.jmxmonitoring.util.MVCNotificationTag;


public class Root3dController implements Observer {
    private WorldObject root;
    private Grid3D rootGrid;
    private ArrayList<Host3dController> childrenControllers;

    public Root3dController(Grid3D grid) {
        Set<String> modelNames = ModelRecorder.getInstance().getNames();
        root = ModelRecorder.getInstance().getModel(modelNames.iterator().next());

        // WorldObject root = new WorldObject();
        root.addObserver(this);
        rootGrid = grid;
        childrenControllers = new ArrayList<Host3dController>();
        // final String url = URIBuilder.buildURI("macondo.inria.fr", "", "rmi",
        // 1605).toString();
        // root.addHost(url);
    }

    public void addchild(Host3dController h3dc) {
        this.childrenControllers.add(h3dc);
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
        case ADD_CHILD: // add new host event
         {
            if ((o instanceof WorldObject) && (notif.getData() != null)) {
                String hostKey = (String) notif.getData();
                HostObject host = (HostObject) ((WorldObject) o).getMonitoredChild(hostKey);

                // System.out.println("----------->> new host added: "+hostKey);
                Host3dController hostController = new Host3dController(host,
                        rootGrid);
                this.addchild(hostController);
            }
        }
        case REMOVE_CHILD_FROM_MONITORED_CHILDREN: {
            if ((o instanceof WorldObject) && (notif.getData() != null)) {
                String hostKey = (String) notif.getData();
                System.out.println(
                    "----------->> host removed from monitored hosts: " +
                    hostKey);
                // TODO
                // ((WorldObject)o).getMonitoredChild(hostKey).deleteObserver(this);
                // rootGrid.removeSubFigure(hostKey);
            }

            // else
            break;
        } // case
        } // switch
    } // update
} // class
