package org.objectweb.proactive.extra.forwarding.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.objectweb.proactive.extra.forwarding.exceptions.ForwardingException;
import org.objectweb.proactive.extra.forwarding.localforwarder.ForwardingAgent;
import org.objectweb.proactive.extra.forwarding.registry.ForwardingRegistry;

public class Chat {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Object uniqueID = null;
		InetAddress regAddress = null;
		int regPort = ForwardingRegistry.DEFAULT_SERVER_PORT;

		int targetID = -1;

		for (int i=0;i<args.length;i++) {
			if (args[i].equals("-regport")) {
				regPort = Integer.parseInt(args[++i]);                
			}
			else if(args[i].equals("-regaddr")) {
				try {
					regAddress = InetAddress.getByName(args[++i]);
				} catch (UnknownHostException e) {
					System.err.println("Unknown host: "+args[i]);
					System.exit(0);
				}
			}
			else if (args[i].equals("-localid")) {
				uniqueID = Integer.parseInt(args[++i]);                
			}
			else if (args[i].equals("-targetid")) {
				targetID = Integer.parseInt(args[++i]);                
			}
			else {
				System.err.println("Unknown option: " + args[i]);
				System.exit(1);
			}          
		}

		if (regAddress == null || uniqueID == null) {
			System.err.println("Registry address and Local host unique ID must be provided, exiting");
			System.exit(1);
		}

		//init the agent (connect to the registry)
		ForwardingAgent agent = ForwardingAgent.getAgent();
		try {
			agent.init(uniqueID, regAddress, regPort);
		} catch (ForwardingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("init of Forwarding agent failed at some point, exiting");
			System.exit(1);
		}
		
		// Client or Server ?
		if(targetID != -1) {
			// client
			client(targetID);
		} else {
			// server
			server();
		}
	}

	private static void server() {
		System.out.println("Starting as server on port 4444");
		try {
			ServerSocket server = new ServerSocket(4444, 1);
			start(server.accept());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void client(int id) {
		try {
			start(ForwardingAgent.getAgent().getSocket(id, 4444));
		} catch (ForwardingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void start(final Socket sock) {
		
		new Thread(new Runnable() {
			public void run() {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					String s = null;
					System.out.println("Start waiting for messages");
					while((s = br.readLine()) != null) {
						System.out.println("other: "+s);	
						System.out.flush();
					}
					System.out.println("finished waiting for messages");					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}).start();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			String s = null;
			while((s = br.readLine()) != null) {
				System.out.println("me: "+s);
				bw.write(s+"\n");
				bw.flush();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}