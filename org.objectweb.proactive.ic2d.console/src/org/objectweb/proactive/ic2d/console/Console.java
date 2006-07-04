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

package org.objectweb.proactive.ic2d.console;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * Used to log informations in a console view.
 */
public class Console extends MessageConsole{
	
	/**
	 * Contains all consoles.
	 */
	private static HashMap consoles = new HashMap();
	
	/**
	 * To know the date's format.
	 */
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	/**
	 * Some useful colors.
	 */
	public static final Color RED;
	public static final Color BLUE;
	public static final Color GREEN;
	public static final Color GRAY;
	public static final Color BLACK;
	
	static {
		Display device = Display.getCurrent();
		RED = new Color(device, 255, 0, 0);
		BLUE = new Color(device, 0, 0, 128);
		GREEN = new Color(device, 180, 255, 180);
		GRAY = new Color(device, 120, 120, 120);
		BLACK = new Color(device, 0, 0, 0);
	}

	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	/**
	 * Creates a new Console
	 * @param title Title of the frame console
	 */
	private Console(String title) {
		super(title, null);
		activate();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(
				new IConsole[]{ this });
	}
	
	//
	// -- PUBLICS METHODS -----------------------------------------------
	//
	
	/**
	 * Returns the console having for title 'title'
	 * @param title The console's title
	 */
	public static Console getInstance(String title){
		Console console = (Console) consoles.get(title);
		if( console == null){
			console = new Console(title);
			consoles.put(title, console);
		}
		return console;
	}
	
	
	/**
	 * Logs a message to the console
	 * @param message
	 */
	public void log(String message){
		final String text = message;
		
		printTime();
		
		// Print the message in the UI Thread in async mode
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageConsoleStream stream = newMessageStream();
				stream.setColor(Console.BLUE);
				stream.println(text);
			}});
	}
	
	
	/**
	 * Logs an warning message to the console.
	 * @param message
	 */
	public void warn(String message){
		final String text = message;
		
		printTime();
		
		// Print the message in the UI Thread in async mode
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageConsoleStream stream = newMessageStream();
				stream.setColor(Console.RED);
				stream.println(text);
			}});
	}
	
	
	/**
	 * Logs an error message to the console.
	 * @param message
	 */
	public void err(String message){
		final String text = message;
		
		printTime();
		
		// Print the message in the UI Thread in async mode
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageConsoleStream stream = newMessageStream();
				stream.setColor(Console.GRAY);
				stream.println(text);
			}});
	}
	
	//
	// -- PRIVATE METHODS -----------------------------------------------
	//
	
	/**
	 * Prints the current time in the console.
	 */
	public void printTime(){
		// Print the message in the UI Thread in async mode
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				MessageConsoleStream stream = newMessageStream();
				stream.setColor(Console.BLACK);
				stream.print(dateFormat.format(new java.util.Date())+" => ");
			}});
	}
}