/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive-support@inria.fr
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
package org.objectweb.proactive.core.rmi;

import org.apache.log4j.Logger;

import java.io.*;

import java.net.Socket;

import java.util.Hashtable;


/**
 *
 * @author vlegrand
 *
 * This class is used to make a new Thread in the Class server when a request incomes.
 * It calls the right service (or "module") to perform the request and send back the appropriate response.
 * For example, when a request for a class file incomes, the thread calls the FileProcess.
 */
public class ProActiveService extends Thread {
    protected static Logger logger = Logger.getLogger(ClassServer.class.getName());
    private final Socket socket;
    private String paths;

    public ProActiveService(Socket socket, String paths) {
        this.socket = socket;
        this.paths = paths;
    }

    public void run() {
        Hashtable table = new Hashtable();
        java.io.DataOutputStream out = null;
        RequestInfo info = null;
        
        String headers = "";
        String statusLine;
        String contentType;
        byte[] bytes = null;

        try {
            out = new java.io.DataOutputStream(socket.getOutputStream());

            // get the headers information in order to determine what is the service requested
            HTTPInputStream in = new HTTPInputStream(new BufferedInputStream(
                        socket.getInputStream()));
            info = getInfo(in);

            //If  there is no field application then it is a call to the 
            if ((info.application != null) &&
                    (info.application.indexOf("xml") > -1)) {
                // ProActive Request via HTTP
                XMLHTTPProcess process = new XMLHTTPProcess(in, info);
                MSG msg = process.getBytes();
                bytes = msg.getMessage();

                statusLine = "HTTP/1.1 200 OK";
                contentType = "text/xml";
                headers = "ProActive-Action: " + msg.getAction() + "\r\n";
            } else if (info.path != null) {
                // ClassServer request
                FileProcess fp = new FileProcess(paths, info);
                bytes = fp.getBytes();
                statusLine = "HTTP/1.1 200 OK";
                contentType = "application/java";
            } else {
                throw new ClassNotFoundException("No path specified");
            }
        } catch (Exception e) { // IOException and ClassNotFoundException
            if (info != null && info.path != null) {
                logger.info("!!! ClassServer failed to load class " +
                    info.path);
            }

            statusLine = "HTTP/1.1 400 " + e.getMessage();
            contentType = "text/plain";

            // Time-consuming and not very useful:
            // StringBuffer buf = new StringBuffer();
            // StackTraceElement[] trace = e.getStackTrace();
            // for (int i = 0; i < trace.length; i++) {
            // 	buf.append(trace[i].toString());
            // 	buf.append("\n");
            // }
            // bytes = buf.toString().getBytes();
            bytes = new byte[0];
        }

        try {
            out.writeBytes(statusLine + "\r\n");
            out.writeBytes("Content-Length: " + bytes.length + "\r\n");
            int a = bytes.length;
            String b = "Content-Length: " + bytes.length + "\r\n";
            out.writeBytes("Content-Type: " + contentType + "\r\n");
            out.writeBytes(headers);
            out.writeBytes("\r\n");
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            // If there is an error when writing the reply,
        	// nothing can be told to the caller...
            e.printStackTrace();
        }
        return;

        //} catch (java.io.IOException e) {
        //} finally {
        //    try {
        //        if (logger.isDebugEnabled()) {
        //            logger.debug("Fermeture de la socket " + this.socket);
        //        }
        // 	
        //        socket.close();
        //    } catch (java.io.IOException e) {
        //        //e.printStackTrace();
        //    }
        //}
    }

    /**
     * Returns the path to the class file obtained from
     * parsing the HTML header.
     */
    private static RequestInfo getInfo(HTTPInputStream in)
        throws java.io.IOException {
        RequestInfo info = new RequestInfo();
        String line = null;

        do {
            line = in.getLine();

            if (line.startsWith("GET /")) {
                info.path = getPath(line);
            } else if (line.startsWith("Host:")) {
                info.host = getHost(line);
            } else if (line.startsWith("Content-Type:")) {
                info.application = getApplication(line);
            } else if (line.startsWith("ProActive-Action:")) {
                info.action = getAction(line);
            } else if (line.startsWith("Content-Length:")) {
                info.contentLength = Integer.parseInt(getContentLength(line));
            }
        } while ((line.length() != 0) && (line.charAt(0) != '\r') &&
                (line.charAt(0) != '\n'));

        if (info.path != null) {
            return info;
        }

        if (info.application.equals("application/soap+xml") ||
                info.application.substring(0, 8).equals("text/xml")) {
            return info;
        } else {
            info.application = null;

            // --End FL
            throw new java.io.IOException("Malformed Header");
        }
    }

    /**
     * Returns an array of bytes containing the bytecodes for
     * the class represented by the argument <b>path</b>.
     * The <b>path</b> is a dot separated class name with
     * the ".class" extension removed.
     *
     * @return the bytecodes for the class
     * @exception ClassNotFoundException if the class corresponding
     * to <b>path</b> could not be loaded.
     * @exception java.io.IOException if error occurs reading the class
     */

    //protected  byte[] getBytes(String path)
    //  throws java.io.IOException, ClassNotFoundException;

    /**
     * Returns the path to the class file obtained from
     * parsing the HTML header.
     * @param line the GET item starting by "GET /"
     */
    private static String getPath(String line) {
        // extract class from GET line
        line = line.substring(5, line.length() - 1).trim();

        int index = line.indexOf(".class ");

        if (index != -1) {
            return line.substring(0, index).replace('/', '.');
        } else {
            return null;
        }
    }

    /**
     * Returns the application type obtained from
     * parsing the HTTP header.
     * @param line the GET item starting by "Content-Type:"
     */
    private static String getApplication(String line) {
        return line.substring(13, line.length()).trim();
    }

    /**
     * Returns the application type obtained from
     * parsing the HTTP header.
     * @param line the GET item starting by "Content-Type:"
     */
    private static String getAction(String line) {
        return line.substring(17, line.length()).trim();
    }

    /**
     * Returns the application type obtained from
     * parsing the HTTP header.
     * @param line the GET item starting by "Content-Type:"
     */
    private static String getContentLength(String line) {
        return line.substring(15, line.length()).trim();
    }

    /**
     * Returns the path to the class file obtained from
     * parsing the HTML header.
     * @param line the GET item starting by "Host:"
     */
    private static String getHost(String line) {
        return line.substring(5, line.length() - 1).trim();
    }
}
