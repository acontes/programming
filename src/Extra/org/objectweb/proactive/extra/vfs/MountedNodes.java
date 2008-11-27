package org.objectweb.proactive.extra.vfs;



import java.util.ArrayList;
import java.util.Hashtable;



import org.objectweb.proactive.core.node.Node;

/*
 * this class stores the node to it's url map 
 */

public class MountedNodes {

	
	public static ArrayList<Node> mountedNodes = new ArrayList<Node>();
	public static ArrayList<String> mountedNodesURL = new ArrayList<String>();
	public static ArrayList<String> realURIs = new ArrayList();
	public static Hashtable<Node,String> nodeurlMap = new Hashtable<Node,String>();
	
	public static void addNodeToList(Node node)
	{
		MountedNodes.mountedNodes.add(node);
		
		String url = createPA_URL(node);
		
		MountedNodes.mountedNodesURL.add(url);                     //this is the PA URL
		
		MountedNodes.nodeurlMap.put(node, url);
		
		MountedNodes.realURIs.add(node.getNodeInformation().getURL());
		
	}
	
	public static ArrayList<Node> getMountedNodesList()
	{
		return MountedNodes.mountedNodes;
	}
	
	public static ArrayList<String> getMountedNodesURLList()
	{
		return MountedNodes.mountedNodesURL;
	}
	
	public static void addRealURI(String uri)
	{
		realURIs.add(uri);
	}
	
	public static ArrayList<String> getRealURIs()
	{
		return realURIs;
	}
	
	public static Hashtable<Node,String> getNodeURLMap()
	{
		return MountedNodes.nodeurlMap;
	}
	
	
	
	
	
	//---------------------------Methods to Create URLs-------------------------------------
	
	public static String createPA_URL(Node node)
	{
        String hostname = node.getVMInformation().getHostName();
		
		String protocol = node.getNodeInformation().getProtocol();
		
		String port = null;
		
		if(protocol.equalsIgnoreCase("rmi"))
		{
			port = "1099";
		}
		
		//form: protocol://machine_name:port.					
		String url = protocol + "://" + hostname + ":" + port + "/" + node.getNodeInformation().getName();
		
		return url;
	}
	
	public static String createHttpURL(Node node)
	{
		return null;
	}
	
	public static String createFtpURL(Node node)
	{
	   return null;	
	}	
	
	
}
