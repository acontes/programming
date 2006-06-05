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
package org.objectweb.proactive.ic2d.logger.gui;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.objectweb.proactive.ic2d.logger.IC2DMessageLogger;
import org.objectweb.proactive.ic2d.logger.IC2DLoggers;

public class TextPaneMessageLogger extends JTextPane implements IC2DMessageLogger {
	
	private static final int MAX_LENGTH = 100000;
	
	private Style regularStyle;
	private Style errorStyle;
	private Style stackTraceStyle;
	private Style threadNameStyle;
	private Style timeStampStyle;
	
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	//
	// -- CONSTRUCTORS -----------------------------------------------
	//
	
	public TextPaneMessageLogger() {
		// that prohibits to the user to type text into the text pane
		setEditable(false);
		
		Style style = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		
		// regular Style
		regularStyle = addStyle("regular", style);
		StyleConstants.setFontFamily(regularStyle, "SansSerif");
		StyleConstants.setFontSize(regularStyle, 10);
		// error Style
		errorStyle = addStyle("error", regularStyle);
		StyleConstants.setForeground(errorStyle, Color.red);
		// stacktrace Style
		stackTraceStyle = addStyle("stackTrace", regularStyle);
		StyleConstants.setForeground(stackTraceStyle, Color.lightGray);
		// threadName Style
		threadNameStyle = addStyle("threadName", regularStyle);
		StyleConstants.setForeground(threadNameStyle, Color.darkGray);
		StyleConstants.setItalic(threadNameStyle, true);
		// timeStamp Style
		timeStampStyle = addStyle("timeStamp", regularStyle);
		StyleConstants.setForeground(timeStampStyle, Color.blue);
		StyleConstants.setItalic(timeStampStyle, true);
		
		IC2DLoggers.getInstance().addLogger(this);
	}
	
	//
	// -- PUBLIC METHODS ---------------------------------------------
	//
	
	//
	// -- implements IC2DMessageLogger -------------------------------
	//
	
	public void warn(String message) {
		logInternal(message, errorStyle);
	}
	
	public void log(String message, Throwable e, boolean dialog) {
		logInternal(message, errorStyle);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintWriter pw = new PrintWriter(baos, false);
		e.printStackTrace(pw);
		pw.flush();
		logInternal(baos.toString(), stackTraceStyle);
		if (dialog) {
			//TODO invokeDialog(message);
		}
	}
	
	public void log(Throwable e, boolean dialog) {
		log(e.getMessage(), e, dialog);
	}
	
	public void log(String message) {
		logInternal(message, regularStyle);
	}
	
	public void log(String message, Throwable e) {
		log(message, e, true);
	}
	
	public void log(Throwable e) {
		log(e, true);
	}
	
	//
	// -- PRIVATE METHODS ---------------------------------------------
	//
	
	private void logInternal (final String message, final AttributeSet style) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				append(dateFormat.format(new java.util.Date()),
						timeStampStyle);
				append(" (", threadNameStyle);
				append(Thread.currentThread().getName(), threadNameStyle);
				append(") => ", threadNameStyle);
				append(message, style);
				append("\n", style);
				setCaretPosition(getDocument().getLength());
			}
		});
	}
	
	private void append(String str, AttributeSet style) {
		Document document = getDocument();
		try {
			document.insertString(document.getLength(), str, style);
			int tooMuch = document.getLength() - MAX_LENGTH;
			if (tooMuch > 0) {
				document.remove(0, tooMuch);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
}
