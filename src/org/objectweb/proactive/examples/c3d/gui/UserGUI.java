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
package org.objectweb.proactive.examples.c3d.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.objectweb.proactive.examples.c3d.Image2D;


/**
 * The GUI class, which once extended gives a nice graphical frontend.
 * The actionPerformed method needs to be overloaded, to handle the protected field events.
 */
public abstract class UserGUI implements ActionListener {
    protected JButton sendMessageButton;
    protected JButton addSphereButton;
    protected JButton resetSceneButton;
    protected ArrowButton upButton;
    protected ArrowButton leftButton;
    protected ArrowButton rightButton;
    protected ArrowButton downButton;
    protected ArrowButton spinRight;
    protected ArrowButton spinLeft;
    protected JTextArea logArea;
    protected JTextArea messageLogArea;
    protected JComboBox sendToComboBox;
    protected JTextField localMessageField;
    protected JFrame mainFrame;
    protected JMenuItem exitMenuItem;
    protected JMenuItem aboutMenuItem;
    protected JMenuItem clearMenuItem;
    protected JMenuItem listUsersMenuItem;
    protected JMenuItem userInfoItem;
    protected ImageCanvas imageComponent;
    protected String userInfoText;

    /** this is a Frame that shows when userInfoItem is pressed */
    protected JFrame userInfoFrame;

    /** The constructor - you may want to extend this, adding a windowClosing event listener*/
    public UserGUI(String title, int imageWidth, int imageHeight) {
        mainFrame = new JFrame(title);
        mainFrame.setContentPane(createMainPanel(imageWidth, imageHeight));
        mainFrame.setJMenuBar(createMenuBar());

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    /** Generates the menu bar which contains list, clear, quit and about items*/
    private JMenuBar createMenuBar() {
        //      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.ALT_MASK));
        //First, the menu items 
        userInfoItem = new JMenuItem("User info", KeyEvent.VK_U);
        listUsersMenuItem = new JMenuItem("List Users", KeyEvent.VK_L);
        clearMenuItem = new JMenuItem("Clear Log", KeyEvent.VK_C);
        exitMenuItem = new JMenuItem("Quit", KeyEvent.VK_Q);
        aboutMenuItem = new JMenuItem("About ProActive", KeyEvent.VK_A);

        // make them responsive
        userInfoItem.addActionListener(this);
        listUsersMenuItem.addActionListener(this);
        clearMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);
        aboutMenuItem.addActionListener(this);

        // The left menu in the menu Bar.
        JMenu menu = new JMenu("Menu");
        menu.setMnemonic(KeyEvent.VK_M);
        menu.add(userInfoItem);
        menu.add(listUsersMenuItem);
        menu.add(clearMenuItem);
        menu.add(new JSeparator());
        menu.add(exitMenuItem);

        // The right menu in the menu bar : "about"
        JMenu about = new JMenu("About");
        about.setMnemonic(KeyEvent.VK_A);
        about.add(aboutMenuItem);

        // Create the wrapping menuBar object
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);
        menuBar.add(Box.createHorizontalGlue());
        menuBar.add(about);
        return menuBar;
    }

    /** The whole panel, which is made up of the 3 different panels : message, scene control & log
     * It is made up of two splitpanes, one for the upper vertical split of left/right, one to split top/bottom*/
    private JComponent createMainPanel(int imageWidth, int imageHeight) {
        JSplitPane vertSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                true, createMessagePanel(),
                createSceneControlPanel(imageWidth, imageHeight));
        vertSplitPane.setOneTouchExpandable(true);

        JSplitPane horizSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                true, vertSplitPane, createLogPanel());
        horizSplitPane.setOneTouchExpandable(true);

        return horizSplitPane;
    }

    /** The text component which displays the log of the application (log strings are generated by the dispatcher)*/
    private JComponent createLogPanel() {
        this.logArea = new JTextArea();
        this.logArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(this.logArea);
        scroll.setPreferredSize(new Dimension(200, 200));
        scroll.setBorder(new TitledBorder("Log Panel"));
        return scroll;
    }

    /** The component which allows communication between users : send message & message trace */
    private JComponent createMessagePanel() {
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder(new TitledBorder("messagePanel"));
        messagePanel.setLayout(new BorderLayout());

        // The message Log Scroll
        messageLogArea = new JTextArea("Users exchange messages here\n");
        messageLogArea.setEditable(false);
        JScrollPane messageLogScroll = new JScrollPane(this.messageLogArea);
        messageLogScroll.setPreferredSize(new Dimension(200, 180));
        messagePanel.add(messageLogScroll, BorderLayout.CENTER);

        // The typetext area, the combo, and the Send button
        localMessageField = new JTextField("users type text here");
        localMessageField.addActionListener(this);

        sendToComboBox = new JComboBox(new String[] { "BROADCAST" });

        sendMessageButton = new JButton("Send");
        sendMessageButton.addActionListener(this);

        // these 3 are on 2 rows, text alone, but combo & button on same line   
        Box receiveTextBox = Box.createVerticalBox();
        receiveTextBox.add(localMessageField);

        Box comboAndSendButtonBox = Box.createHorizontalBox();
        comboAndSendButtonBox.add(sendToComboBox);
        comboAndSendButtonBox.add(sendMessageButton);
        receiveTextBox.add(comboAndSendButtonBox);

        messagePanel.add(receiveTextBox, BorderLayout.SOUTH);
        return messagePanel;
    }

    /** the component which contains some information on the machines used, and own user name */
    private JTextArea createUserInfoArea(String text) {
        JTextArea userInfoArea = new JTextArea("User information");
        userInfoArea.setText(text);
        userInfoArea.setEditable(false);
        return userInfoArea;
    }

    /** The panel which allows the users to act on the image generated */
    private JComponent createSceneControlPanel(int imageWidth, int imageHeight) {
        // TODO : check passing width & height to constructor is *really* needed.
        JPanel sceneControlPanel = new JPanel();
        sceneControlPanel.setBorder(new TitledBorder("Scene Control"));
        sceneControlPanel.setLayout(new BorderLayout());

        upButton = new ArrowButton("up");
        upButton.addActionListener(this);
        sceneControlPanel.add(upButton.getJPanel(), BorderLayout.NORTH);
        rightButton = new ArrowButton("right");
        rightButton.addActionListener(this);
        sceneControlPanel.add(rightButton.getJPanel(), BorderLayout.EAST);
        leftButton = new ArrowButton("left");
        leftButton.addActionListener(this);
        sceneControlPanel.add(leftButton.getJPanel(), BorderLayout.WEST);
        sceneControlPanel.add(createFiveButtonPanel(), BorderLayout.SOUTH);
        imageComponent = new ImageCanvas(imageWidth, imageHeight);
        sceneControlPanel.add(imageComponent, BorderLayout.CENTER);
        sceneControlPanel.setPreferredSize(new Dimension(250, 250));
        return sceneControlPanel;
    }

    /** Creates the panel which contains the spin & down buttons, as well as addsphere & reset scene     */
    private JComponent createFiveButtonPanel() {
        Box upperBox = Box.createHorizontalBox();
        spinLeft = new ArrowButton("spinleft");
        spinLeft.addActionListener(this);
        upperBox.add(spinLeft);
        upperBox.add(Box.createGlue());
        downButton = new ArrowButton("down");
        downButton.addActionListener(this);
        upperBox.add(downButton);
        upperBox.add(Box.createGlue());
        spinRight = new ArrowButton("spinright");
        spinRight.addActionListener(this);
        upperBox.add(spinRight);

        Box lowerBox = Box.createHorizontalBox();
        lowerBox.add(Box.createGlue());
        addSphereButton = new JButton("Add Sphere");
        addSphereButton.setMinimumSize(addSphereButton.getPreferredSize());
        addSphereButton.addActionListener(this);
        lowerBox.add(addSphereButton);
        lowerBox.add(Box.createGlue());
        resetSceneButton = new JButton("Reset Scene");
        resetSceneButton.addActionListener(this);
        resetSceneButton.setMinimumSize(resetSceneButton.getPreferredSize());
        lowerBox.add(resetSceneButton);
        lowerBox.add(Box.createGlue());

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(upperBox);
        panel.add(lowerBox);

        return panel;
    }

    /**
     * Destroy the graphical window
     */
    public void trash() {
        mainFrame.setVisible(false);
        mainFrame.dispose();
    }

    /**
     * Should implement the response to the events generated by the
     * protected field of the GUI class.
     */
    public abstract void actionPerformed(ActionEvent e);

    /**
     * Display on the screen an interval of newly calculated pixels
     * @param image The new pixels, as int array with positionning information
     */
    public void setPixels(Image2D image) {
        this.imageComponent.setPixels(image);
    }

    /** Display in the log area a message */
    public void log(String log) {
        logArea.append(log);
        //      logArea.setCaretPosition(logArea.getText().length());
    }

    /** Shows the user a message coming from another user */
    public void writeMessage(String message) {
        this.messageLogArea.append(message);
        //      int end = this.messageLogArea.getText().length() ;
        //      this.messageLogArea.setCaretPosition(end);
    }

    /** Adds this new name to the possible recipients of messages */
    public void addUser(String name) {
        sendToComboBox.addItem(name);
    }

    /** Removes this name from the list of possible recipients of messages */
    public void removeUser(String name) {
        sendToComboBox.removeItem(name);
    }

    /** Returns the important values which where shown on screen, like the log, the messages...*/
    public String[] getValues() {
        Dimension displayAreaSize = imageComponent.getPreferedSize();
        return new String[] {
            localMessageField.getText(), messageLogArea.getText(),
            String.valueOf(displayAreaSize.width), // need them for the constructor, which makes  
            String.valueOf(displayAreaSize.height)
        };
        // no need to store the users, as the dispatcher informs of them again!
    }

    /** Sets the given values to the different elements of the GUI */
    public void setValues(String[] userInfo, String[] values) {
        int counter = 0;
        this.userInfoText = "User\n    " + userInfo[counter++] + " (" +
            userInfo[counter++] + ")" + "\nDispatcher\n    " +
            userInfo[counter++] + "\n    " + userInfo[counter++];
        counter = 0;
        localMessageField.setText(values[counter++]);
        messageLogArea.setText(values[counter++]);
        imageComponent.setPreferredSize(new Dimension(Integer.parseInt(
                    values[counter++]), Integer.parseInt(values[counter++])));
    }

    public void showUserInfo() {
        if (userInfoFrame == null) {
            userInfoFrame = new JFrame("Information");
            userInfoFrame.getContentPane().add(createUserInfoArea(
                    this.userInfoText));
            userInfoFrame.pack();
        }
        userInfoFrame.setLocation(mainFrame.getLocation());
        userInfoFrame.setVisible(true);
        //userInfoFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE); not needed, is default
    }
}
