/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.examples.webservices.c3dWS;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;
import org.objectweb.proactive.Body;
import org.objectweb.proactive.InitActive;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.migration.MigrationStrategyManagerImpl;
import org.objectweb.proactive.core.remoteobject.http.util.HttpMarshaller;
import org.objectweb.proactive.core.util.ProActiveInet;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.examples.webservices.c3dWS.geom.Vec;
import org.objectweb.proactive.examples.webservices.c3dWS.gui.UserGUI;
import org.objectweb.proactive.examples.webservices.c3dWS.gui.WSNameAndHostDialog;
import org.objectweb.proactive.examples.webservices.c3dWS.gui.WaitFrame;
import org.objectweb.proactive.extensions.annotation.ActiveObject;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;


/**
 * The user logic of the C3D application. This class does not do gui related work, which is cast
 * back to UserGUI. It only handles the logic parts, ie specifies the behavior.
 */
@ActiveObject
public class WSUser implements InitActive, java.io.Serializable, User, UserLogic {

    /** useful for showing information, if no GUI is available, or for error messages */
    private static Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

    /** url of the host where the C3DDispatcher is deployed */
    private String dispatcherUrl;

    /** AsyncRefto self, needed to add method on own queue */
    private User me;

    public User getMe() {
        return me;
    }

    /** reference to the dispatcher logic, for image generation and message forwarding */
    private Dispatcher c3ddispatcher;

    /** The chosen name of the user */
    private String userName;

    /**
     * Number of this user in the set of users registered at the <code>c3ddispatcher</Code>, used
     * to distinguish the action requests of several users
     */
    private int i_user;

    /** List of users. Used for private messaging */
    private Map<String, Integer> h_users = new Hashtable<String, Integer>();

    /** The GUI which makes a nice front-end to th logic-centric class C3DUser */
    private transient UserGUI gui;

    /** The values stored in the GUI are saved here when the GUI needs to migrate */
    private String[] savedGuiValues;
    private String dispMachineAndOS;

    /** ProActive requirement : empty no-arg constructor */
    public WSUser() {
    }

    public WSUser(String dispatcherUrl, String name, Dispatcher disp) {
        this.dispatcherUrl = dispatcherUrl;
        this.userName = name;
        this.c3ddispatcher = disp;
    }

    /** Returns the C3DUser constructor arguments, after a dialog has popped up */
    public static Object[] getDispatcherAndUserName() {
        // ask user through Dialog for userName & host 
        WSNameAndHostDialog userAndHostNameDialog = new WSNameAndHostDialog();
        String dispUrl = userAndHostNameDialog.getValidatedDispatcherService();

        if (dispUrl == null) {
            logger.error("Could not find a dispatcher. Closing.");
            System.exit(-1);
        }

        return new Object[] { dispUrl, userAndHostNameDialog.getValidatedUserName(),
                userAndHostNameDialog.getValidatedDispatcher() };
    }

    /**
     * called after migration, to reconstruct the logic. In the initActivity :
     * myStrategyManager.onArrival("rebuild");
     */

    // shouldn't be called from outside the class. 
    public void rebuild() {
        this.me = (User) org.objectweb.proactive.api.PAActiveObject.getStubOnThis();
        try {
            WSDispatcherCaller.call(this.dispatcherUrl, "wsRegisterMigratedUser", new Object[] { i_user });
        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        createGUI();
    }

    /**
     * Called just before migration, as specified in the initActivity :
     * myStrategyManager.onDeparture("leaveHost");
     */

    // shouldn't be called from outside the class. 
    public void leaveHost() {
        this.savedGuiValues = this.gui.getValues();
        this.gui.trash();
    }

    /**
     * Tells what are the operations to perform before starting the activity of the AO. Here, we
     * state that if migration asked, procedure is : saveData, migrate, rebuild. We also set some
     * other variables.
     * @throws AxisFault 
     */
    public void initActivity(Body body) {
        if (body != null) { // FIXME: this is a component bug: sometimes body is null!    
            MigrationStrategyManagerImpl myStrategyManager = new MigrationStrategyManagerImpl(
                (org.objectweb.proactive.core.body.migration.Migratable) body);
            myStrategyManager.onArrival("rebuild");
            myStrategyManager.onDeparture("leaveHost");
        }

        // register user to dispatcher, while asking user to be patient
        WaitFrame wait = new WaitFrame("C3D : please wait!", "Please wait...",
            "Waiting for information from Dispatcher");
        // get the stub, which is a long operation, while the wait window is displayed 
        this.me = (User) org.objectweb.proactive.api.PAActiveObject.getStubOnThis();

        Object[] callReturn = null;
        try {
            /**
             * The following lines should work but it seems that there is
             * a bug in axis2. To get round this problem, we serialize the object and
             * deserialize it at the reception.
             */
            callReturn = WSDispatcherCaller.call(this.dispatcherUrl, "wsRegisterUser", new Object[] {
                    HttpMarshaller.marshallObject(this.getMe()), this.getUserName() },
                    new Class<?>[] { int.class });
        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int user_id = (Integer) callReturn[0];
        //        int user_id = this.c3ddispatcher.registerUser(this.me, this.userName);
        this.i_user = user_id;

        wait.destroy();

        this.savedGuiValues = null;
        // Create user Frame  
        createGUI();
    }

    /** shows a String as a log */
    public void log(String s_message) {
        if (this.gui == null) {
            logger.info(s_message);
        } else {
            this.gui.log(s_message + "\n");
        }
    }

    /** Shows a String as a message to this user */
    public void message(String s_message) {
        if (this.gui == null) {
            logger.info(s_message);
        } else {
            this.gui.writeMessage(s_message + "\n");
        }
    }

    /**
     * Informs the user that a new user has joined the party!!
     * 
     * @param nUser
     *            The new user's ID
     * @param sName
     *            The new user's name
     */
    public void informNewUser(int nUser, String sName) {
        this.gui.addUser(sName);
        this.h_users.put(sName, new Integer(nUser));
    }

    /**
     * Informs the user that another user left
     * 
     * @param nUser
     *            The id of the old user
     */
    public void informUserLeft(String sName) {
        //  remove the user from the users list in the GUI
        this.gui.removeUser(sName);

        // Remove the user from the hash table
        this.h_users.remove(sName);
    }

    /**
     * Display an interval of newly calculated pixels
     * 
     * @param newpix
     *            The pixels as int array
     * @param interval
     *            The interval
     */
    public void setPixels(Image2D image) {
        this.gui.setPixels(image);
    }

    /**
     * Exit the application
     */
    public void terminate() {
        try {
            WSDispatcherCaller.call(this.dispatcherUrl, "wsUnregisterConsumer", new Object[] { i_user });
        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.gui.trash();
        PAActiveObject.terminateActiveObject(true);
    }

    /**
     * Entry point of the program
     */
    public static void main(String[] argv) {
        GCMApplication proActiveDescriptor = null;

        ProActiveConfiguration.load();

        try {
            proActiveDescriptor = PAGCMDeployment.loadApplicationDescriptor(new File(argv[0]));
        } catch (Exception e) {
            logger.error("Trouble loading descriptor file");
            e.printStackTrace();
            System.exit(-1);
        }

        proActiveDescriptor.startDeployment();
        GCMVirtualNode user = proActiveDescriptor.getVirtualNode("User");
        Object[] params = WSUser.getDispatcherAndUserName();

        try {
            //C3DUser c3duser = (C3DUser)
            user.waitReady();
            org.objectweb.proactive.api.PAActiveObject.newActive(WSUser.class.getName(), params, user
                    .getANode());
        } catch (Exception e) {
            logger.error("Problemn with C3DUser Active Object creation:");
            e.printStackTrace();
        }
    }

    /** Ask the dispatcher to revert to original scene */
    public void resetScene() {
        try {
            WSDispatcherCaller.call(this.dispatcherUrl, "wsResetScene", new Object[] {});
        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** Ask the dispatcher to add a sphere */
    public void addSphere() {
        try {
            WSDispatcherCaller.call(this.dispatcherUrl, "wsAddSphere", new Object[] {});
        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** Displays the list of users connected to the dispatcher */
    public void getUserList() {
        Object[] callReturn = null;
        try {
            callReturn = WSDispatcherCaller.call(this.dispatcherUrl, "wsGetUserList", new Object[] {},
                    new Class<?>[] { String.class });
        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String list = (String) callReturn[0];
        this.gui.log("List of current users:\n" + list.toString());
    }

    /** Send a message to a given other user, or to all */
    public void sendMessage(String message, String recipientName) {
        Integer talkId = (Integer) h_users.get(recipientName);

        if (talkId == null) {
            // BroadCast
            gui.writeMessage("<to all> " + message + '\n');
            try {
                WSDispatcherCaller.call(this.dispatcherUrl, "wsUserWriteMessageExcept", new Object[] {
                        this.i_user, "[from " + this.userName + "] " + message });
            } catch (AxisFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            // Private message
            gui.writeMessage("<to " + recipientName + "> " + message + '\n');
            try {
                WSDispatcherCaller.call(this.dispatcherUrl, "wsUserWriteMessage", new Object[] {
                        talkId.intValue(), "[Private from " + this.userName + "] " + message });
            } catch (AxisFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    /**
     * ask for the scene to be rotated by some angle
     * 
     * @param rotationAngle =
     *            <x y z> means rotate x radians along the x axis, then y radians along the y axis,
     *            and finally z radians along the z axis
     */
    public void rotateScene(Vec rotationAngle) {
        try {
            WSDispatcherCaller.call(this.dispatcherUrl, "wsRotateScene",
                    new Object[] { i_user, rotationAngle });
        } catch (AxisFault e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setUserName(String newName) {
        this.userName = newName;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setDispatcherMachine(String machine, String os) {
        this.dispMachineAndOS = "    " + machine + "\n    " + os;

        String guiInfoText = this.gui.getUserInfo();
        int index = guiInfoText.lastIndexOf('\n');

        if (index != -1) {
            index = guiInfoText.lastIndexOf('\n', index);
        }

        if (index != -1) {
            guiInfoText = guiInfoText.substring(0, index + 1) + this.dispMachineAndOS;
        } else {
            guiInfoText += this.dispMachineAndOS;
        }

        this.gui.setUserInfo(guiInfoText);
    }

    /** returns the name of the machine on which this active object is currently */
    private String getMachineRelatedValues() {
        String hostName = "unknown";

        hostName = ProActiveInet.getInstance().getInetAddress().getHostName();

        return "User\n   " + this.userName + " (" + hostName + ")" + "\nDispatcher\n" + this.dispMachineAndOS;
    }

    private void createGUI() {
        this.gui = new UserGUI("C3D user display", (UserLogic) this.me);
        this.gui.setValues(this.savedGuiValues);
        this.gui.setUserInfo(getMachineRelatedValues());
    }
}
