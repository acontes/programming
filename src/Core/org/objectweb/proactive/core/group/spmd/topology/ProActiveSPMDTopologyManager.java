package org.objectweb.proactive.core.group.spmd.topology;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PASPMD;
import org.objectweb.proactive.core.UniqueID;
import org.objectweb.proactive.core.body.Context;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.proxy.UniversalBodyProxy;
import org.objectweb.proactive.core.body.request.RequestImpl;
import org.objectweb.proactive.core.group.ExceptionListException;
import org.objectweb.proactive.core.group.Group;
import org.objectweb.proactive.core.group.ProxyForGroup;
import org.objectweb.proactive.core.mop.StubObject;

/**
 * Class managing the topology of the spmd group
 */
public class ProActiveSPMDTopologyManager implements Serializable{

	/**
	 * Leader rank of the spmd group
	 */
	public static final int LEADER_RANK = 0; 

	public int NB_NEEDED_LATENCIES = 12;

	/**
	 * Timer of the thread managing the sending of the latencies
	 */
	private static final int DEAMON_MANAGER_TIMER = 60000;

	/**
	 * Timer of the thread managing the sending of the latencies while the topology is not yet available
	 */
	private int deamonManagerStartTimer = 250;

	/**
	 * Timer of the thread managing the construction of the topology tree
	 */
	private static final int DEAMON_BUILDER_TIMER = 200000;

	private static final double THRESHOLD_SENDING = 15;

	private boolean firstSend = false;
	private int neededLatencies;
	private int nbDifferrentLatencies;
	private int threshold_nb_latencies;
	private int groupSize;
	private int myRank;
	private Body body;

	/**
	 * SPMD Group
	 */
	private Group<Object> spmdGroup;

	/**
	 * Corresponding table between UniqueID and rank of the members of the SPMD group.
	 */
	private Map<UniqueID, Integer> correspondingTable;

	/**
	 * Object to synchronize the 'addLatency' method.
	 */
	private Object synchroAdd = new Object();

	/**
	 * Latencies to reach other members of the SPMD group.
	 */
	private Latency[] communicationLatencies;

	/**
	 * Thread managing the diffusion of the latencies to the leader
	 */
	private transient Thread deamonTopologyManager;

	/**
	 * Thread managing the diffusion of the topology to all members of the spmd group
	 */
	private transient ThreadTopologySender threadTopologySender;

	/**
	 * Build and diffuse the topology (Used only by the leader of the spmd group)
	 */
	private transient ConstructionTopologyManagemement constructionTopologyManagement;

	/**
	 * Topology of clusters, view as a tree
	 */
	private TopologyNode clusterTree;

	/**
	 * Id of the tree
	 */
	private int treeId;

	private List<Integer> diffusionList;

	/** 
	 * Members of the cluster
	 */
	private List<Integer> myCluster;

	private int parent = -1;

	/**
	 * Rank of the root of the tree
	 */
	private int rootLeader = -1;


	/**
	 * Object used to wait until the tree arrive
	 */
	private Object waitTree = new Object();

	private long timeInit;

	/**
	 * Constructor, used for serialization
	 */
	public ProActiveSPMDTopologyManager(){
	}

	public ProActiveSPMDTopologyManager(Group<Object> spmdGroup) {
		this.timeInit = System.currentTimeMillis();
		this.body = PAActiveObject.getBodyOnThis();
		this.groupSize = spmdGroup.size();
		this.spmdGroup = spmdGroup;
		this.myRank = PASPMD.getMyRank();
		this.threshold_nb_latencies = (neededLatencies * 1) / 100;
		this.communicationLatencies = new Latency[groupSize];
		for (int i = 0; i < groupSize; i++) {
			communicationLatencies[i] = new Latency(PASPMD.getMyRank(), i);
		}
		this.correspondingTable = new HashMap<UniqueID, Integer>();
		for (int i = 0; i < groupSize; i++) {
			UniversalBody body = ((UniversalBodyProxy) (((StubObject) spmdGroup.get(i)).getProxy())).getBody();
			correspondingTable.put(body.getID(), i);
		}
		deamonTopologyManager = new Thread(new DeamonTopologyManager());
		deamonTopologyManager.start();

		if(myRank == LEADER_RANK)
			constructionTopologyManagement = new ConstructionTopologyManagemement();

//		System.out.println("rank = " + myRank  + " host = " + body.getNodeURL());
		File conf = new File("conf.txt");
		try{
			if(conf.exists()){
				FileInputStream fis = new FileInputStream(conf);
				DataInputStream in = new DataInputStream(fis);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while((line = br.readLine()) != null){
					String[] tab = line.split("=");
					if(tab[0].equals("latping")){
						this.deamonManagerStartTimer = Integer.parseInt(tab[1]);
					}
					else if(tab[0].equals("nbping")){
						NB_NEEDED_LATENCIES = Integer.parseInt(tab[1]);
					}
				}
			}
		}
		catch(Exception e){
		}
		this.neededLatencies = (groupSize-1) * NB_NEEDED_LATENCIES;
	}

	/**
	 * Return the rank of the AO in the spmd group
	 * @return rank of the AO in the spmd group
	 */
	public int getMyRank(){
		return myRank;
	}

	/**
	 * Return the topology tree
	 * @return the topology tree
	 */
	public TopologyNode getTopologyTree(){
		synchronized (waitTree) {
			return clusterTree;
		}
	}

	public List<Integer> getDiffusionList(){
		return diffusionList;
	}

	/** 
	 * Return the list containing all cluster members
	 */
	public List<Integer> getMyCluster(){
		return myCluster;
	}

	/**
	 * Return the rank of the root of the tree
	 * @return rank of the root of the tree
	 */
	public int getRootLeader(){
		return rootLeader;
	}

	/**
	 * Return the parent in the tree
	 * @return parent in the tree
	 */
	public int getParent(){
		return parent;
	}

	/**
	 * Return true if a topology tree is available
	 * @return true if a topology tree is available
	 */
	public boolean isTopologyAvailable(){
		return clusterTree != null;
	}

	/**
	 * Return the ID of the topology tree
	 * @return ID of the topology tree
	 */
	public int getTreeId(){
		synchronized (waitTree) {
			return treeId;
		}
	}

	public Latency[] getLatencies() {
		return communicationLatencies;
	}

	/**
	 * Set the average time between two ping of the group (in milliseconds)
	 * @param interval average time between two ping of the group (in milliseconds)
	 */
	public void setPingInterval(int interval){
		deamonManagerStartTimer = interval;
	}

	/**
	 * Wait a given amount of time until the next version of the topology tree arrive
	 * @param time time to wait
	 */
	public void waitForTree(long time){
		synchronized (waitTree) {			
			try {
				waitTree.wait(time);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Wait until the next version of the topology tree arrive
	 */
	public void waitForTree(){
		synchronized (waitTree) {			
			try {
				waitTree.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Wait for a given version of the tree
	 * @param version id of the tree
	 */
	public void waitForTree(int version){
		synchronized (waitTree) {			
			if(version < this.treeId){
				waitForTree();
			}
		}
	}

	/**
	 * Add a new value of latency to the table of latencies
	 * @param latency the new latency to add
	 * @param destBody the latency comes from a communication with this body
	 */
	public void addLatency(int latency, UniversalBody destBody) {
		Integer index = correspondingTable.get(destBody.getID());
		if (index != null && index != myRank) {
			Latency l = communicationLatencies[index];
			synchronized (synchroAdd) {
				int nbData = l.getNbData();
				if(nbData < NB_NEEDED_LATENCIES){
					neededLatencies--;
				}
				if(nbData == 0){
					nbDifferrentLatencies++;
				}
				l.add(latency);
			}
		}
	}

	/**
	 * Send the latencies to the leader of the group
	 */
	private void sendLatencies() {
		try {
			Object leader = spmdGroup.get(LEADER_RANK);
			UniversalBody bodyLeader = ((UniversalBodyProxy) (((StubObject) leader).getProxy())).getBody();
			for (Latency lat : communicationLatencies) {
				lat.calculateEcartType();
				lat.storeAverage();
			}
			MethodCallLatency mcLatency = null;
			if (myRank == LEADER_RANK) {
				Latency[] latencies = new Latency[groupSize];
				for (int i = 0; i < groupSize; i++)
					latencies[i] = communicationLatencies[i].copy();
				mcLatency = new MethodCallLatency(myRank, latencies);
			} 
			else {
				mcLatency = new MethodCallLatency(myRank, communicationLatencies);
			}
			RequestImpl req = new RequestImpl(mcLatency, true);
			bodyLeader.receiveRequest(req);
		} catch (Throwable e) {
			//			System.err.println("Unable to send the communication latencies");
			//			e.printStackTrace();
		}
	}

	/**
	 * Set the topologyTree
	 */
	protected void setTopologyTree(TopologyNode tree, int treeId) {
		synchronized (waitTree) {
			this.treeId = treeId;
			this.clusterTree = tree;
			this.myCluster = new ArrayList<Integer>();
			if(tree.getId() == getMyRank()){
				for(TopologyNode node : tree.getChildren()){
					myCluster.add(node.getId());
				}
			}
			this.diffusionList = new ArrayList<Integer>();
			TopologyNode parent = tree;
			while(parent.getParent() != null && parent.getParent().getId() == myRank){
				parent = parent.getParent();
				for(TopologyNode node : parent.getChildren()){
					int nodeId = node.getId();
					if(nodeId != myRank){
						diffusionList.add(nodeId);
					}
				}
			}
			if(myCluster.size() == 0){
				this.parent = parent.getId();
			}
			else if(parent.getParent() != null){
				this.parent = parent.getParent().getId();
			}
			while(parent.getParent() != null){
				parent = parent.getParent();
			}
			this.rootLeader = parent.getId();
			waitTree.notifyAll();
		}
	}

	/**
	 * Return true if the latencies have to be sent to the leader
	 * @return true if the latencies have to be sent to the leader
	 */
	protected boolean hasToSendLatencies() {
		if(!firstSend){
			firstSend = true;
			return true;
		}
		double totalDiff = 0;
		for (Latency lat : communicationLatencies) {
			if (lat.getDestRank() != myRank) {
				double oldAvg = lat.getOldAverage();
				double diff = Math.abs(oldAvg - lat.getAverage());
				totalDiff += (1 - ((oldAvg - diff) / oldAvg));
			}
		}
		double res = totalDiff / (groupSize - 1) * 100;
		return res > THRESHOLD_SENDING;
	}

	/**
	 * Update the latencies of one member of the spmd group
	 * This method should only be used by the leader of the spmd group
	 * @param rank member of the group whose latencies has to be updated
	 * @param latencies new latencies
	 */
	protected void setCommunicationLatencies(int rank, Latency[] latencies) {
		if(constructionTopologyManagement != null)
			constructionTopologyManagement.setCommunicationLatencies(rank, latencies);
	}

	/**
	 * Return the thread managing the diffusion of the topology
	 * @return the thread managing the diffusion of the topology
	 */
	protected ThreadTopologySender getThreadTopologySender(){
		if(threadTopologySender == null)
			threadTopologySender = new ThreadTopologySender();
		return threadTopologySender;
	}

	/**
	 * Send the topology to all members of the spmd group
	 */
	protected void diffuseTopologyTree(TopologyNode tree, int treeId) {
		try {
			ProxyForGroup<Object> leaderGroup = new ProxyForGroup<Object>(spmdGroup.getTypeName());

			for(TopologyNode node : tree.getChildren()){
				int leaderId = node.getId();
				Object hostToAdd = spmdGroup.get(leaderId);
				leaderGroup.add(hostToAdd);
			}
			LocalBodyStore.getInstance().pushContext(new Context(body, null));
			leaderGroup.reify(new MethodCallTopology(tree, treeId));
		} catch (Throwable e) { 
			System.err.println("Unable to send the topology tree");
			e.printStackTrace();
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		deamonTopologyManager = new Thread(new DeamonTopologyManager());
		deamonTopologyManager.start();

		if(myRank == LEADER_RANK)
			constructionTopologyManagement = new ConstructionTopologyManagemement();
	}

	/* 
	 *************************************************************
	 *                      PRIVATE CLASS                        *
	 ************************************************************* 
	 */

	/**
	 * Thread managing the diffusion of the latencies to the leader of the spmd group
	 */
	private class DeamonTopologyManager implements Runnable {
		private Random rand = new Random();
		int nbPing = 0;
		
		public void run() {
			try{
				//				try {
				//					Thread.sleep(1000);
				//				} catch (InterruptedException e1) {
				//					e1.printStackTrace();
				//				}
				while(true) {
					if(deamonManagerStartTimer > 1){
						Thread.sleep(rand.nextInt(deamonManagerStartTimer) + deamonManagerStartTimer/2);						
					}

					if(nbDifferrentLatencies == groupSize-1 && neededLatencies <= threshold_nb_latencies){
						break;
					}

					ProxyForGroup<Object> leaderGroup = (ProxyForGroup<Object>)spmdGroup;
					LocalBodyStore.getInstance().pushContext(new Context(body, null));
					try{
						nbPing++;
						leaderGroup.reify(new MethodCallPing());
					}
					catch(ExceptionListException e){
					}
					//					System.out.println("needed lat = " + neededLatencies);
				}
//				if(myRank == 0){
//					System.out.println("nbPing = " + nbPing);
//				}
				while (true) {
					if (hasToSendLatencies()) {
						sendLatencies();
					}
					Thread.sleep(rand.nextInt(DEAMON_MANAGER_TIMER) + DEAMON_MANAGER_TIMER/2);
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Thread managing the diffusion of the topology
	 */
	protected class ThreadTopologySender extends Thread {

		private int cpt = 0;
		private TopologyNode tree; 
		private Object synchro = new Object();

		public void diffuseTopologyTreeByThread(TopologyNode tree, int treeId) {
			this.tree = tree;
			synchronized (synchro) {
				if(isAlive()){
					cpt++;
					synchro.notifyAll();
				}
				else{
					this.start();
				}
			}
		}

		public void run() {
			try {
				while(true){
					diffuseTopologyTree(tree, treeId);
					synchronized (synchro) {
						if(cpt<=0)
							synchro.wait();
						cpt--;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Class managing the construction and the diffusion of the topology, only used by the leader of the spmd group
	 */
	private class ConstructionTopologyManagemement {

		private boolean build = false;
		private int nbDifferentLatencies;
		private double coeff = 1;
		private long time;

		/**
		 * Latencies between all members of the SPMD group.
		 */
		private Latency[][] groupCommunicationLatencies;

		/**
		 * Inferate the topology
		 */
		private TopologyInference topologyInference;

		/**
		 * Return the list of clusters view as list of their members's nodes
		 */
		private List<List<TopologyNode>> clusterNodes;

		/**
		 * Thread managing the construction of the topology
		 */
		private Thread deamonTopologyBuilder;

		/**
		 * Object to synchronize the 'setCommunicationLatencies' method.
		 */
		private Object synchroSet = new Object();

		public ConstructionTopologyManagemement(){
			groupCommunicationLatencies = new Latency[groupSize][groupSize];
			topologyInference = new TopologyInference(groupCommunicationLatencies);
		}

		/**
		 * Return true if the topology has changed and has to be diffused again
		 */
		private boolean hasToDiffuseTopologyTree(List<List<TopologyNode>> oldClusters, int[] newHostToCluster, int newNbCluster) {
			Random rand = new Random();
			double score = 0;
			int percentToCheck = 20;
			int nbCheck = 0;
			for(int i = 0; i < oldClusters.size(); i++){
				List<TopologyNode> cluster = oldClusters.get(i);
				int clustSize = cluster.size();
				for(int j = 0; j < clustSize; j++){
					List<TopologyNode> listOfNodesToCheck = new ArrayList<TopologyNode>(cluster);
					TopologyNode ref = cluster.get(j);
					int nbNodeToCheck = (int)(clustSize * (percentToCheck/100.0) + 1);
					int refCluster = newHostToCluster[ref.getId()];
					for(int k = 0 ; k < nbNodeToCheck; k++){
						TopologyNode nodeToCheck = listOfNodesToCheck.remove(rand.nextInt(listOfNodesToCheck.size()));
						if(nodeToCheck.getId() == ref.getId()) 
							nodeToCheck = listOfNodesToCheck.remove(rand.nextInt(listOfNodesToCheck.size()));
						int nodeToCheckCluster = newHostToCluster[nodeToCheck.getId()];
						if(refCluster != nodeToCheckCluster){
							score+=1.2;
						}
						nbCheck++;
					}
				}
			}
			score /= nbCheck;
			if(newNbCluster < oldClusters.size()){
				double differenceOfNumberOfClusters = oldClusters.size() - newNbCluster;
				score += (differenceOfNumberOfClusters/((oldClusters.size()+newNbCluster)/2+1));
			}
			if(score < 0.15) 
				score = 0.15;
			return score*coeff > 0.4;
		}

		/**
		 * Update the latencies of one member of the spmd group
		 * @param rank member of the group whose latencies has to be updated
		 * @param latencies new latencies
		 */
		private void setCommunicationLatencies(int rank, Latency[] latencies) {
			synchronized (synchroSet) {
				if(groupCommunicationLatencies[rank][0] == null)
					nbDifferentLatencies++;
				groupCommunicationLatencies[rank] = latencies;
				if (!build && nbDifferentLatencies == groupCommunicationLatencies.length) {
					build = true;
//					System.out
//					.println("ProActiveSPMDTopologyManager.ConstructionTopologyManagemement.setCommunicationLatencies() latences collectees tmp = " + (System.currentTimeMillis() - timeInit));
					// first construction of the topology
					topologyInference.makeTopologyInference();
//					System.out
//					.println("ProActiveSPMDTopologyManager.ConstructionTopologyManagemement.setCommunicationLatencies() Topologie construite tmp = " + (System.currentTimeMillis() - timeInit));
					// diffusion of the topology
					diffuseTopologyTree(topologyInference.getClusterTree(), treeId++);
//					System.out
//					.println("ProActiveSPMDTopologyManager.ConstructionTopologyManagemement.setCommunicationLatencies() Topologie diffuse tmp = " + (System.currentTimeMillis() - timeInit));
					// creation and launch of the thread managing the update of the topology
					deamonTopologyBuilder = new Thread(new DeamonTopologyBuilder());
					deamonTopologyBuilder.start();
				}
			}
		}

		/**
		 * Thread managing the construction and the update of the topology
		 */
		private class DeamonTopologyBuilder implements Runnable {
			private boolean running = false;

			public void run() {
				running = true;
				while (running) {
					try {
						Thread.sleep(DEAMON_BUILDER_TIMER);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//					System.out.println();
					//					System.out.println("---------------------------------------------------------");

					time = System.currentTimeMillis();
					synchronized (synchroSet) {
						// update the topology
						topologyInference.makeTopologyInference();
					}

					List<List<TopologyNode>> newClusterNodes = topologyInference.getClusterNodes();
					//					System.out.println(" TMP de calcul = " + (System.currentTimeMillis()-time));

					// if necessary diffuse the new topology
					if(clusterNodes == null || hasToDiffuseTopologyTree(clusterNodes, topologyInference.getHostsToClusters(), topologyInference.getNbClusters())){
						coeff = 1;
						time = System.currentTimeMillis();
						diffuseTopologyTree(topologyInference.getClusterTree(), treeId++);
						clusterNodes = newClusterNodes;
					}
					else{
						coeff *= 1.11;
					}
				}
			}
		}
	}
}
