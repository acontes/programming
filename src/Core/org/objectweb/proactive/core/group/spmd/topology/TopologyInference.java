package org.objectweb.proactive.core.group.spmd.topology;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class trying to inferate the topology of a group a nodes,
 * by analyzing their latencies.
 */
public class TopologyInference implements Serializable{

	/**
	 * Current topology tree of hosts
	 */
	private TopologyNode topologyTree;

	/**
	 * List of all the peers of the tree.
	 */
	private Map<Integer, TopologyLeaf> hosts;

	/**
	 * List of all nodes representing a virtual switch.
	 */
	private Map<Integer, TopologyNode> nodes;

	/**
	 * Latencies between all peers, used to create the topology tree.
	 */
	private Latency[][] groupCommunicationLatencies;

	private int[] hostsToCluster;
	private List<List<TopologyNode>> clusterNodes;
	private List<TopologyNode> clusters;
	private TopologyNode clusterTree;
	private int groupSize;
	private int nextNodeId = -100000;
 
	/**
	 * Constructor, used for serialization
	 */
	public TopologyInference(){
	}
	
	public TopologyInference(Latency[][] groupCommunicationLatencies){
		this.groupCommunicationLatencies = groupCommunicationLatencies;
		this.groupSize = groupCommunicationLatencies.length;
	}

	/**
	 * First method to call, make the host topology inference and build the cluster's topology
	 * This method should be called before using the getters
	 */
	public void makeTopologyInference(){
		inferateTopologie();
//		System.out.println("Tree : " + topologyTree);
		simplifyTopologyTree(1.0/6);
//		System.out.println("Apres clean : Tree : " + topologyTree);
		makeClusterTree(true);
		System.out.println("Topology tree : " + clusterTree);
	}

	/* 
	 ********************************************************
	 *                      GETTERS                         *
	 ******************************************************** 
	 */


	/**
	 * Return the cluster topology tree (refinement of the host topology tree)
	 */
	public TopologyNode getClusterTree(){
		if(topologyTree == null)
			makeTopologyInference();
		return clusterTree;
	}

	/**
	 * Return the corresponding map <nodeId  ->  ClusterId>
	 */
	public int[] getHostsToClusters(){
		if(topologyTree == null)
			makeTopologyInference();
		return hostsToCluster;
	}

	/**
	 * Return the list of clusters, view as leafs of a topology tree
	 */
	public List<TopologyNode> getListClusters(){
		if(topologyTree == null)
			makeTopologyInference();
		return clusters;
	}
	
	public int getNbClusters(){
		return clusters.size();
	}

	/**
	 * Return the list of clusters view as list of their members's nodes
	 */
	public List<List<TopologyNode>> getClusterNodes(){
		if(topologyTree == null)
			makeTopologyInference();
		return clusterNodes;
	}

	/**
	 * Return the table of latencies used to build the topology
	 */
	public Latency[][] getCommunicationLatencies(){
		if(topologyTree == null)
			makeTopologyInference();
		return groupCommunicationLatencies;
	}


	/* 
	 ***********************************************************
	 *                    Intern Methods                       *
	 *********************************************************** 
	 */

	/**
	 * Return a simplified version of the current topology tree where negligible
	 * nodes have been removed.
	 * 
	 * @param tree The topology tree to simplifies.
	 * @return A simplified version of the current topology tree where
	 *         negligible nodes have been removed.
	 */
	private void simplifyTopologyTree(double coeff) {
		double[] res = new double[6];
		this.averageWeight(topologyTree, res);
		double averageNodeLength = res[0] / res[3];
		double averageLeafLength = res[2] / res[5];
		if(averageLeafLength > 1)
			averageLeafLength = 1;
		cleanNegligibleNodes(topologyTree, averageNodeLength*coeff);
		cleanNegativeValues(topologyTree, averageLeafLength);
	}

	/**
	 * Return a tree representing the topology of the hosts.
	 * @param groupCommunicationLatencies latencies between all peers.
	 * @return A tree representing the topology.
	 */
	private TopologyNode inferateTopologie() {
		this.hosts = new HashMap<Integer, TopologyLeaf>();
		this.nodes = new HashMap<Integer, TopologyNode>();
		this.nextNodeId = -100000;
		Latency[] tabLatency = new Latency[groupSize];
		for (int j = 0; j < groupSize; j++) {
			double total = 0;
			int nbRes = 0;
			Arrays.sort(groupCommunicationLatencies[j]);
			Latency[] tab = groupCommunicationLatencies[j];
			for (int i = 0; i < groupSize; i++) {
				Double average = tab[i].getAverage();
				if (average >= 0) {
					total += average;
					nbRes++;
				}
			}
			tabLatency[j] = new Latency(-1, j);
			if (nbRes > 0)
				tabLatency[j].add(total / nbRes);
		}
		Arrays.sort(tabLatency);

		topologyTree = new TopologyNode(getNextNodeId());
		TopologyLeaf l0 = new TopologyLeaf(tabLatency[0].getDestRank());
		TopologyLeaf l1 = null;
		for (int i = groupSize - 1; i >= 0; i--) {
			Latency l = groupCommunicationLatencies[l0.getId()][i];
			if (l.getAverage() >= 0 && l.getDestRank() != l0.getId()) {
				l1 = new TopologyLeaf(l.getDestRank());
				break;
			}
		}
		TopologyLeaf l2 = null;
		l2 = new TopologyLeaf(groupCommunicationLatencies[l0.getId()][groupSize / 2].getDestRank());

		for (int i = groupSize/2; i >= 0; i--) {
			Latency l = groupCommunicationLatencies[l0.getId()][i];
			if (l.getAverage() >= 0 && l.getDestRank() != l0.getId() && l.getDestRank() != l1.getId()) {
				l2 = new TopologyLeaf(l.getDestRank());
				break;
			}
		}

		nodes.put(topologyTree.getId(), topologyTree);
		topologyTree.addChild(l0, -1);
		topologyTree.addChild(l1, -1);
		hosts.put(l0.getId(), l0);
		hosts.put(l1.getId(), l1);
		this.add(l2);
		hosts.put(l2.getId(), l2);

		for (int i = 0; i < groupSize; i++) {
			int r = groupCommunicationLatencies[0][i].getDestRank();
			if (r != l0.getId() && r != l1.getId() && r != l2.getId()) {
				TopologyLeaf li = new TopologyLeaf(r);
				add(li);
				hosts.put(li.getId(), li);
			}
		}
		return topologyTree;
	}

	/**
	 * Add a host to the topology tree
	 * @param host the host to add to the topology tree
	 */
	private void add(TopologyLeaf host) {
		Latency[] latencies = groupCommunicationLatencies[host.getId()];
		// special case : insertion of the third host
		if (hosts.size() == 2) {
			Iterator<TopologyLeaf> it = hosts.values().iterator();
			Latency AH = Latency.search(latencies, it.next().getId());
			Latency BH = Latency.search(latencies, it.next().getId());
			Latency AB = Latency.search(groupCommunicationLatencies[AH.getDestRank()], BH.getDestRank());
			double AHavg = getRealAverage(AH.getSourceRank(), AH.getDestRank());
			double BHavg = getRealAverage(BH.getSourceRank(), BH.getDestRank());
			double ABavg = getRealAverage(AB.getSourceRank(), AB.getDestRank());
			double x = ((AHavg + ABavg - BHavg) / 2.0);
			double y = ((ABavg + BHavg - AHavg) / 2.0);
			double z = ((BHavg + AHavg - ABavg) / 2.0);
			topologyTree.addChild(host, z);
			topologyTree.setLength(AH.getDestRank(), x);
			topologyTree.setLength(BH.getDestRank(), y);
		}
		// normal case
		else {
			this.addInternal(host, new HashMap<Integer, TopologyLeaf>(hosts), null, null);
		}
	}

	/**
	 * Used by the add() method above
	 */
	private void addInternal(TopologyLeaf host, Map<Integer, TopologyLeaf> leafs, TopologyNode previouslySelectedNode, Latency AH) {
		Latency BH = null, AB = null;
		// selection of the two closest hosts of the inserting host, that already are in the tree
		for (int i = 0; i < groupSize && BH == null; i++) {
			Latency la = groupCommunicationLatencies[host.getId()][i];
			if (contains(leafs, la.getDestRank())) {
				if (AH == null)
					AH = la;
				else {
					Latency ab = Latency.search(groupCommunicationLatencies[AH.getDestRank()], la.getDestRank());
					if (BH == null) {
						BH = la;
						AB = ab;
					}
				}
			}
		}
		// determination of the branching point to insert the new host
		double AHavg = getRealAverage(AH.getSourceRank(), AH.getDestRank());
		double BHavg = getRealAverage(BH.getSourceRank(), BH.getDestRank());
		double ABavg = getRealAverage(AB.getSourceRank(), AB.getDestRank());

		double x = ((AHavg + ABavg - BHavg) / 2.0);
		double y = ((ABavg + BHavg - AHavg) / 2.0);
		double z = ((BHavg + AHavg - ABavg) / 2.0);
		TopologyNode branchingPoint = null;
		if (x <= 0) {
			branchingPoint = addNegativeValue(x, getNode(hosts, AH.getDestRank()));
		} else if (y <= 0) {
			branchingPoint = addNegativeValue(y, getNode(hosts, BH.getDestRank()));
		} else {
			double xMin = x - x / 10 - 0.01;
			double xMax = x + x / 10 + 0.01;
			branchingPoint = findBranchingPoint(x, xMin, xMax, getNode(hosts, AH.getDestRank()), getNode(hosts, BH.getDestRank()));
		}

		if (contains(nodes, branchingPoint.getId())){
			for (TopologyNode child : branchingPoint.getChildren()) {
				if (child.contains(BH.getDestRank(), true)) {
					removeLeafs(leafs, child);
					if (previouslySelectedNode != null)
						break;
				} else if (previouslySelectedNode == null && child.contains(AH.getDestRank(), true)) {
					removeLeafs(leafs, child);
				}
			}
			if (leafs.size() > 0) {
				addInternal(host, leafs, branchingPoint, AH);
				return;
			}
		}

		if (TopologyLeaf.class.isAssignableFrom(branchingPoint.getClass())) {
			double length = branchingPoint.getParentLength();
			System.out.println("node insert = " + host.getId() + " z = " + z);
			branchingPoint = insertNodeDown(length, length, branchingPoint, branchingPoint.getParent());
		}

		// add of the new host
		branchingPoint.addChild(host, z);
	}

	/**
	 * Used by addInternal() method
	 * Return the branching node of the new node to insert
	 */	
	private TopologyNode findBranchingPoint(double averageLength, double minLength, double maxLength, TopologyNode n1, TopologyNode n2) {
		if (minLength < 0)
			minLength = 0;
		return findBranchingPoints(averageLength, maxLength + 1, minLength, maxLength, n1, n2, null);
	}

	/**
	 * Used by findBranchingPoint() method
	 */
	private TopologyNode findBranchingPoints(double averageLength, double nodeLength, double minLength, double maxLength, TopologyNode n1,
			TopologyNode n2, TopologyNode res) {
		if (n1.getId() == n2.getId()) {
			if (res == null) {
				TopologyNode parent = n1.getParent();
				double n1Length = parent.getChildLength(n1.getId());
				return insertNodeDown(Math.abs(averageLength + n1Length), n1Length, n1, parent);
			} else
				return res;
		}
		if (maxLength > -0.001 && maxLength < 0.001) {
			return n1;
		} else {
			for (TopologyNode n : n1.getChildren()) {
				if (n.contains(n2.getId(), true)) {
					double n1Length = n1.getChildLength(n.getId());
					return findBranchingPointsInternal(false, averageLength, nodeLength, minLength, maxLength, n1Length, n, n1, n2, res);
				}
			}
			TopologyNode parent = n1.getParent();
			double parentLength = parent.getChildLength(n1.getId());
			return findBranchingPointsInternal(true, averageLength, nodeLength, minLength, maxLength, parentLength, parent, n1, n2, res);
		}
	}

	/**
	 * Used by findBrancingPoints() method
	 */
	private TopologyNode findBranchingPointsInternal(boolean up, double averageLength, double nodeLength, double minLength,
			double maxLength, double n1Length, TopologyNode n, TopologyNode n1, TopologyNode n2, TopologyNode res) {
		double newMaxLength = maxLength - n1Length;
		double nLength = Math.abs(averageLength);
		averageLength -= n1Length;
		double absAvgLength = Math.abs(averageLength);
		// No node has been found yet
		if (minLength >= 0) {
			double newMinLength = minLength - n1Length;
			if (newMinLength < -0.001) {
				if (newMaxLength < -0.001) {
					if (up)
						return insertNodeUp(nLength, n1Length, n, n1);
					else
						return insertNodeDown(nLength, n1Length, n, n1);
				}
				nodeLength = absAvgLength;
				if (newMaxLength < 0.001) {
					return n;
				}
				return findBranchingPoints(averageLength, nodeLength, -1, newMaxLength, n, n2, n);
			}
			if (newMinLength < 0.001) {
				nodeLength = absAvgLength;
				if (newMaxLength < 0.001 && newMaxLength > -0.001) {
					return n;
				}
				return findBranchingPoints(averageLength, nodeLength, -1, newMaxLength, n, n2, n);
			}
			return findBranchingPoints(averageLength, nodeLength, newMinLength, newMaxLength, n, n2, res);
		}
		// At least one node has been previously found
		else {
			if (newMaxLength < -0.001) {
				return res;
			}
			if (absAvgLength < nodeLength) {
				nodeLength = absAvgLength;
			} else {
				return res;
			}
			if (newMaxLength < 0.001 && newMaxLength > -0.001) {
				return n;
			}
			return findBranchingPoints(averageLength, nodeLength, -1, newMaxLength, n, n2, n);
		}
	}

	/**
	 * Merge similar nodes of the tree and remove nodes that only have one child.
	 * @param tree the tree to clean.
	 * @param negligibleValue value of reference to detect similar nodes.
	 */
	private void cleanNegativeValues(TopologyNode tree, double negligibleValue) {
		if (tree.getClass().equals(TopologyNode.class)) {
			double nodeLength = 0;
			TopologyNode parent = tree.getParent();
			if (parent != null)
				nodeLength = parent.getChildLength(tree.getId());
			ArrayList<TopologyNode> children = new ArrayList<TopologyNode>(tree.getChildren());
			for (int i = 0; i < children.size(); i++) {
				TopologyNode child = children.get(i);
				double childLength = tree.getChildLength(child.getId());
				for (int j = i + 1; j < children.size(); j++) {
					TopologyNode child2 = children.get(j);
					double childLength2 = tree.getChildLength(child2.getId());
					if ((childLength < 0 || childLength2 < 0) && Math.abs(childLength + childLength2) < negligibleValue) {
						if (childLength < 0 && child2.getClass().equals(TopologyNode.class)) {
							tree.removeChild(child.getId());
							for (TopologyNode n : child.getChildren())
								child2.addChild(n, child.getChildLength(n.getId()));
							if (TopologyLeaf.class.isAssignableFrom(child.getClass())) {
								child2.addChild(child, childLength + childLength2);
							}
							children.remove(i);
							child = child2;
							childLength = childLength2;
							i--;
						} else if (child.getClass().equals(TopologyNode.class)) {
							tree.removeChild(child2.getId());
							for (TopologyNode n : child2.getChildren())
								child.addChild(n, child2.getChildLength(n.getId()));
							if (TopologyLeaf.class.isAssignableFrom(child2.getClass())) {
								child.addChild(child2, childLength + childLength2);
							}
							children.remove(j);
						} else
							continue;
						if (tree.getNbChildren() == 1 && parent != null) {
							parent.addChild(child, childLength + nodeLength);
							parent.removeChild(tree.getId());
						}
						break;
					}
				}
				if (i > 0 && childLength < 0 && Math.abs(childLength + nodeLength) < negligibleValue && parent != null) {
					children.remove(i);
					tree.removeChild(child.getId());
					parent.addChild(child, childLength + nodeLength);
					i--;
					if (tree.getNbChildren() == 1) {
						TopologyNode n = tree.getChildren().iterator().next();
						parent.addChild(n, tree.getChildLength(n.getId()) + nodeLength);
						parent.removeChild(tree.getId());
						break;
					}
				}
			}
			for (int i = 0; i < children.size(); i++)
				cleanNegativeValues(children.get(i), negligibleValue);
		}
	}

	/**
	 * Remove nodes of the tree which h.ave a length value lower than the
	 * specified length.
	 * @param tree the tree to clean
	 * @param negligibleValue the value of reference.
	 */
	private void cleanNegligibleNodes(TopologyNode tree, double negligibleValue) {
		if (tree.getClass().equals(TopologyNode.class)) {
			ArrayList<TopologyNode> children = new ArrayList<TopologyNode>(tree.getChildren());
			TopologyNode parent = tree.getParent();
			if (parent != null && Math.abs(parent.getChildLength(tree.getId())) < negligibleValue) {
				parent.removeChild(tree.getId());
				for (TopologyNode n2 : children) {
					parent.addChild(n2, tree.getChildLength(n2.getId()));
				}
			}
			for (int i = 0; i < children.size(); i++) {
				TopologyNode n = children.get(i);
				cleanNegligibleNodes(n, negligibleValue);
			}
		}
	}

	/**
	 * Calculate the average weight of nodes and store the results in a table
	 * @param tree tree to evaluate
	 * @param res table where the results are stored
	 */
	private void averageWeight(TopologyNode tree, double[] res) {
		for (TopologyNode n : tree.getChildren()) {
			double length = tree.getChildLength(n.getId());
			res[0] += Math.abs(length);
			res[3]++;
			if (n.getClass().equals(TopologyNode.class)) {
				res[1] += Math.abs(length);
				res[4]++;
			} else {
				res[2] += Math.abs(length);
				res[5]++;
			}
			averageWeight(n, res);
		}
	}

	/**
	 * Give an index of trust of the tree, lower is the value
	 * better is the index of trust. (don't work well)
	 * @param tree the tree to estimate.
	 */
	private void calculateIndexOfTrust(TopologyNode tree) {
		double trust = 0;
		// if tree if a leaf calculate his index of trust with his table of latencies
		if (TopologyLeaf.class.isAssignableFrom(tree.getClass())) {
			TopologyLeaf leaf = (TopologyLeaf) tree;
			trust = calculateIndexOfTrustInternal(groupCommunicationLatencies[leaf.getId()]);
			tree.setIndexOfTrust(trust);
		} 
		// is tree is a node his index of trust is the average of the index of trust of his children, balanced by their size
		else {
			double nb = 0;
			for (TopologyNode child : tree.getChildren()) {
				int size = child.nbLeaf();
				calculateIndexOfTrust(child);
				trust += child.getIndexOfTrust() * size;
				nb += size;
			}
			tree.setIndexOfTrust(trust / nb);
		}
	}

	/**
	 * Used by calculateIndexOfTrust() method
	 * Calculate the index of trust of a leaf
	 * @param latencies table of latencies of the leaf
	 * @return index of trust of the leaf
	 */
	private double calculateIndexOfTrustInternal(Latency[] latencies) {
		int rank = latencies[0].getSourceRank();
		if (latencies.length == groupSize) {
			double total = 0;
			double nb = 0;
			for (int i = 0; i < groupSize; i++) {
				Latency lat = latencies[i];
				if (lat.getDestRank() != rank) {
					int nbData = lat.getNbData();
					if (nbData < 5)
						nb += (1.0 / 5) * nbData;
					else
						nb++;
					if (nbData > 0) {
						total += lat.getEcartType();
					}
				}
			}
			return (total / nb);
		} else
			return -1;
	}

	/**
	 * Used by MakeClusterTree() method
	 * Construct the clusters's topology as a List of cluster, each cluster is represented by a list of TopologyNode
	 */
	private List<List<TopologyNode>> getClusters(){
		double[] tab = new double[6];
		averageWeight(topologyTree, tab);
		double avgLeaf = tab[2]/tab[5];
		List<List<TopologyNode>> clusters = new ArrayList<List<TopologyNode>>();
		Map<Integer, TopologyLeaf> leafs = new HashMap<Integer, TopologyLeaf>(hosts);
		while(leafs.size() > 0){ // while all nodes have not yet been added to a cluster
			TopologyLeaf leaf = null;
			double minLength = 0;
			// select a node
			for(TopologyLeaf l : leafs.values()){
				double lLength = Math.abs(l.getParentLength());
				if(leaf == null || lLength < minLength){
					leaf = l;
					minLength = lLength;
				}
			}
			List<TopologyNode> cluster = new ArrayList<TopologyNode>();
			removeLeafs(leafs, leaf);
			cluster.add(leaf);
			// construct the cluster of the selected node
			cluster(cluster, leafs, leaf.getParentLength(), avgLeaf*2.25, leaf.getParent(), null);
			clusters.add(cluster);
		}
		return clusters;
	}

	/**
	 * Used by getClusters() method, construct a cluster by starting on a node and adding
	 * to the cluster all nodes which are close to the referenced node.
	 * @param cluster the constructing cluster
	 * @param leafs leaf that are not yet in another cluster and are thus eligible to be added in the new cluster
	 */
	private double cluster(List<TopologyNode> cluster, Map<Integer, TopologyLeaf> leafs, double actualDist, double avgDist, TopologyNode ref, TopologyNode previous){
		List<TopologyNode> childrenNodes = new ArrayList<TopologyNode>();
		List<TopologyLeaf> childrenLeafs = new ArrayList<TopologyLeaf>();
		TopologyNode parent = ref.getParent();
		for(TopologyNode child : ref.getChildren()){
			if(child.getClass().equals(TopologyLeaf.class))
				childrenLeafs.add((TopologyLeaf)child);
			else
				childrenNodes.add(child);
		}
		if(parent != null)
			childrenNodes.add(ref);

		if(childrenLeafs.size() > 0){
			Collections.sort(childrenLeafs);
			for(TopologyLeaf leaf : childrenLeafs){  
				if(contains(leafs, leaf.getId())){
					double length = leaf.getParentLength() + actualDist;
					if(length < 1.3*avgDist+0.1){
						double avgDist2 = (avgDist * (cluster.size()-1) + length) / cluster.size();
						avgDist = avgDist2;
						if(avgDist < 0.2) avgDist = 0.2;
						removeNode(leafs, leaf.getId());
						cluster.add(leaf);
					}
					else break;
				}
			}
		}
		if(childrenNodes.size() > 0){
			Collections.sort(childrenNodes);
			for(TopologyNode node : childrenNodes){
				double nodeLength;
				if(node.getId() == ref.getId()){
					nodeLength = parent.getChildLength(ref.getId());
					node = parent;
				}
				else nodeLength = ref.getChildLength(node.getId());
				if(previous == null || previous.getId() != node.getId()){
					if(actualDist < avgDist){
						double newAvg = cluster(cluster, leafs, actualDist+nodeLength, avgDist, node, ref);
						avgDist = newAvg;
					}
					else break;
				}
			}
		}
		return avgDist;
	}

	/**
	 * Construct the cluster's topology tree
	 * @return List of clusters, each cluster is the list of his nodes
	 */
	private List<List<TopologyNode>> makeClusterTree(boolean recursive){
		clusterNodes = getClusters();
		int nbClusters = clusterNodes.size();
		hostsToCluster = new int[groupSize]; 

		// corresponding table node -> cluster
		for(int i = 0; i < nbClusters; i++){
			List<TopologyNode> clust = clusterNodes.get(i);
			for(int j = 0; j < clust.size(); j++){
				hostsToCluster[clust.get(j).getId()] = i;
			}
		}

		// merge of the clusters of size 1 to the closest cluster
		for(int i = 0; i < nbClusters; i++){
			List<TopologyNode> clust = clusterNodes.get(i);
			if(clust.size() == 1){
				Latency[] latencies = new Latency[nbClusters];
				int rankRef = clust.get(0).getId();
				for(int k = 0; k < nbClusters; k++){
					latencies[k] = new Latency(i, k);
				}
				for(int j = 0; j < groupCommunicationLatencies[rankRef].length; j++){
					Latency lat = groupCommunicationLatencies[rankRef][j];
					int cl = hostsToCluster[lat.getDestRank()];
					if(cl != i){
						latencies[cl].add(lat.getAverage());
					}
				}

				double minlatency = 0;
				int newCluster = -1;
				for(int k = 0; k < latencies.length; k++){
					double lat = latencies[k].getAverage();
					if(newCluster == -1 || (lat >= 0 && lat < minlatency)){
						minlatency = lat;
						newCluster = k;
					}
				}
				clusterNodes.get(newCluster).add(clust.get(0));
				hostsToCluster[clust.get(0).getId()] = newCluster;
				clust.remove(0);
			}
		}

		// update of the list of clusters
		for(int i = 0; i < clusterNodes.size(); i++){
			List<TopologyNode> clust = clusterNodes.get(i);
			if(clust.size() == 0){
				clusterNodes.remove(i);
				nbClusters--;
				i--;
			}
		}

		// update of the correcponding table node -> cluster
		for(int i = 0; i < nbClusters; i++){
			List<TopologyNode> clust = clusterNodes.get(i);
			for(int j = 0; j < clust.size(); j++){
				hostsToCluster[clust.get(j).getId()] = i;
			}
		}

//		if(recursive){
//			System.out.println("===============================");
//			// affichage
//			System.out.println("Clusters : ");
//			for(int i = 0; i < nbClusters; i++){
//				List<TopologyNode> ltn = clusterNodes.get(i);
//				for(int j = 0; j < ltn.size(); j++){
//					TopologyNode node = ltn.get(j);
//					System.out.print(node.getId() + " ");
//				}
//				System.out.println();
//			}
//		}

		// construction of the table of latencies between clusters
		Latency[][] latencies = new Latency[nbClusters][nbClusters];
		for(int i = 0; i < nbClusters; i++){
			List<TopologyNode> cluster = clusterNodes.get(i);
			Latency[] values = new Latency[nbClusters];
			latencies[i] =  values;
			for(int k = 0; k < cluster.size(); k++){
				TopologyNode ref = cluster.get(k);
				int rankRef = ref.getId();
				for(int j = 0; j < groupSize; j++){
					Latency lat = groupCommunicationLatencies[rankRef][j];
					int cl = hostsToCluster[lat.getDestRank()];
					if(latencies[i][cl] == null)
						latencies[i][cl] = new Latency(i, cl);
					if(cl != i){
						latencies[i][cl].add(lat.getAverage());
					}
				}
			}
		}
//		for(Latency[] tabLat : latencies){
//			for(Latency lat : tabLat){
//				System.out.print(lat.getAverage() + "   ");
//			}
//			System.out.println();
//		}

		// construction of the clusters's topology
		if(nbClusters >= 3){
			TopologyInference ti = new TopologyInference(latencies);
			ti.inferateTopologie();
			ti.simplifyTopologyTree(1.0/4);
			clusterTree = ti.topologyTree;

			//			if(recursive && nbClusters > groupSize/8 && groupSize >= 24 && nbClusters > 8){
			//				List<List<TopologyNode>> lltn = ti.makeClusterTree(false);
			//				List<TopologyLeaf> ltl = ti.getListClusters();
			//				if(ltl.size() > 1){
			//					System.out.println("accepte");
			//					int[] hostToCluster2 = ti.getHostsToClusters();
			//					this.clusterTree = ti.getClusterTree();
			//					this.clusters = ltl;
			//					clusterNodes = lltn;
			//					for(int i = 0; i < clusterNodes.size(); i++){
			//						clusterNodes.set(i, new ArrayList<TopologyNode>());
			//					}
			//					for(int i = 0; i < hostsToCluster.length; i++){
			//						int oldCluster = hostsToCluster[i];
			//						int newCluster = hostToCluster2[oldCluster];
			//						hostsToCluster[i] = newCluster;
			//						clusterNodes.get(newCluster).add(hosts.get(i));
			//					}
			//
			//					System.out.println("===============================");
			//					// affichage
			//					System.out.println("Clusters2 : ");
			//					for(int i = 0; i < clusterNodes.size(); i++){
			//						List<TopologyNode> ltn = clusterNodes.get(i);
			//						for(int j = 0; j < ltn.size(); j++){
			//							TopologyNode node = ltn.get(j);
			//							System.out.print(node.getId() + " ");
			//						}
			//						System.out.println();
			//					}
			//					//					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			//					//					System.out.println("hostsToCluster : ");
			//					//					for(Entry<Integer, Integer> entry : hostsToCluster.entrySet()){
			//					//						System.out.println(entry.getKey() + " -> " + entry.getValue());
			//					//					}
			//					//					for(List<TopologyNode> clust : clusterNodes){
			//					//						for(TopologyNode n : clust){
			//					//							System.out.print(n.getId() + " ");
			//					//						}
			//					//						System.out.println();
			//					//					}
			//					//					System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			//				}
			//				else{
			//					clusters = new ArrayList<TopologyLeaf>(ti.hosts.values());
			//					System.out.println("rejete");
			//				}
			//			}
			//			else{
			clusters = new ArrayList<TopologyNode>(ti.hosts.values());
			Collections.sort(clusters, new Comparator<TopologyNode>(){
				public int compare(TopologyNode o1, TopologyNode o2) {
					if(o1.getId() < o2.getId()) return -1;
					if(o1.getId() > o2.getId()) return 1;
					return 0;
				}
			});
			
			
			//			}
		}
		else{
			clusters = new ArrayList<TopologyNode>();
			if(nbClusters == 1){
				clusterTree = new TopologyNode(0);
				clusters.add(clusterTree);
			}
			else if(nbClusters == 2){
				clusterTree = new TopologyNode(-1);
				clusterTree.addChild(new TopologyNode(0), latencies[0][1].getAverage());
				clusterTree.addChild(new TopologyNode(1), latencies[1][0].getAverage());
				clusters.add(clusterTree.getChild(0));
				clusters.add(clusterTree.getChild(1));
			}
		}
		
		for(int i = 0; i < nbClusters; i++){
			TopologyNode cluster = clusters.remove(0);
			TopologyNode parent = cluster.getParent();
			double length = cluster.getParentLength();
			parent.removeChild(cluster.getId());
			TopologyNode newCluster = new TopologyNode(((cluster.getId()+1)*-1), length, parent);
			clusters.add(newCluster);
			List<TopologyNode> clusterMembers = clusterNodes.get(i);
			for(TopologyNode host : clusterMembers){
				newCluster.addChild(host, 0);
			}
		}
		
//		System.out.println("ClusterTree : " + clusterTree) ;
		//	System.out.println("Profondeur de l'arbre : " + clusterTree.getMaxDepth());
		if(recursive){
			clusterTree = balanceTopologyTree(clusterTree);
//			System.out.println("Apres balance de l'arbre : " + clusterTree);
			electClusterLeaders(clusterNodes);
		}

		return clusterNodes;
	}


	/**
	 * Reorganize the tree to have a more balanced tree
	 * @param tree the topology tree to reorganize
	 */
	public TopologyNode balanceTopologyTree(TopologyNode tree){
		Map<TopologyNode, Integer> map = new HashMap<TopologyNode, Integer>();

		// calculate the depth of each subtree
		setMaxDepth(tree, map);

		TopologyNode[] result = new TopologyNode[1];

		// find the new root of the tree
		balanceTopologyTreeInternal(result, tree, 0, -1, map);

		TopologyNode newRoot = result[0];
		TopologyNode node = newRoot;
		if(node != null){
			TopologyNode parent = node.getParent();
			TopologyNode tmp;
			// reorganize the tree
			if(parent != null){
				while(parent.getParent() != null){
					tmp = parent.getParent();
					double length = node.getParentLength();
					parent.removeChild(node.getId());
					node.addChild(parent, length);
					node = parent;
					parent = tmp;
				}
				if(parent.getNbChildren() == 2){
					double length = parent.getChildLength(node.getId());
					for(TopologyNode child : parent.getChildren()){
						if(child != node){
							node.addChild(child, child.getParentLength()+length);
						}
					}
					parent.removeChild(node.getId());
				}
				else{
					double length = parent.getChildLength(node.getId());
					parent.removeChild(node.getId());
					node.addChild(parent, length);
				}
			}
			newRoot.setParent(null);
			return newRoot;
		}
		return tree;
	}

	/**
	 * Used by balanceTopologyTree() method
	 */
	private int balanceTopologyTreeInternal(TopologyNode[] result, TopologyNode tree, int currentVal, int bestVal, Map<TopologyNode, Integer> values){
		int max2 = 0;
		int nbNodes = 0;
		for(TopologyNode child : tree.getChildren()){
			if(child.getClass().equals(TopologyNode.class)){
				nbNodes++;
				int childVal = values.get(child);
				if(childVal > currentVal){
					max2 = currentVal;
					currentVal = childVal;
				}
				else if(childVal > max2)
					max2 = childVal;
			}
		}
		if(nbNodes > 0){
			if(currentVal < bestVal || bestVal == -1){
				bestVal = currentVal;
				result[0] = tree;
			}
			for(TopologyNode child : tree.getChildren()){
				if(child.getClass().equals(TopologyNode.class)){
					int val = 0;
					if(values.get(child) == currentVal){
						if(tree.getParent() == null && tree.getNbChildren() == 2)
							val = balanceTopologyTreeInternal(result, child, max2, bestVal, values);
						else val = balanceTopologyTreeInternal(result, child, max2+1, bestVal, values);
					}
					else {
						if(tree.getParent() == null && tree.getNbChildren() == 2)
							val = balanceTopologyTreeInternal(result, child, currentVal, bestVal, values);
						else val = balanceTopologyTreeInternal(result, child, currentVal+1, bestVal, values);
					}
					if(val < bestVal)
						bestVal = val;
				}
			}
		}
		return bestVal;
	}

	/**
	 * Design the leader of each cluster
	 * @param listClusters clusters
	 */
	private void electClusterLeaders(List<List<TopologyNode>> listClusters){
		int nbClusters = listClusters.size();
		//clustersLeaders = new int[nbClusters];

		// select leader of each cluster
		for(int i = 0; i < nbClusters; i++){
			List<TopologyNode> cluster = listClusters.get(i);
			double bestAvg = -1;
			for(TopologyNode host : cluster){
				int rankRef = host.getId();
				if (rankRef != 0) {
					double avg = 0;
					for(int k = 0; k < groupSize; k++){
						Latency lat = groupCommunicationLatencies[rankRef][k];
						int cl = hostsToCluster[lat.getDestRank()];
						if(cl == i && lat.getAverage() >= 0) // same cluster
							avg += lat.getAverage();
					}
					if(avg < bestAvg || bestAvg < 0){
						bestAvg = avg;
						clusters.get(i).setId(rankRef);
//						clustersLeaders[i] = rankRef;
					}
				}
			}
		}

//		System.out.println("TopologyInference.electClusterLeaders() " + clusterTree);
//
//		System.out.println("TopologyInference.electClusterLeaders() AFFICHAGE de -CLUSTERS-");
//		for(int i = 0; i< clusters.size(); i++){
//			System.out.println(" cluster " + i + " : " + clusters.get(i) + " leader = " + clusters.get(i).getId());
//		}
//		System.out.println("--------------------------------------------------");
		
		// select leaders of the clusters'leaders
		Map<TopologyNode, Double> mapLength = new HashMap<TopologyNode, Double>();
		for(int i = 0; i < nbClusters; i++){
			TopologyNode node = clusters.get(i);
			int id = clusters.get(i).getId();
			double length = 0;
			while(node.getParent() != null){
				length += node.getParentLength();
				node = node.getParent();
				Double length2 = mapLength.get(node);
				if(length2 == null || length < length2){
					mapLength.put(node, length);
					node.setId(id);
				}
				else break;
			}
		}
//		System.out.println("Leaders :");
//		for(int i = 0; i < clustersLeaders.length; i++)
//			System.out.print(clustersLeaders[i] + " ");
//		System.out.println();
		//		System.out.println("normal : " + clusterTree.toString());
	}


	/* 
	 ********************************************************
	 *                     UTILITIES                        *
	 ******************************************************** 
	 */

	/** 
	 * Return a node from a map
	 */
	private TopologyNode getNode(Map<Integer, ? extends TopologyNode> lNodes, int id) {
		return lNodes.get(id);
	}

	/**
	 * Remove a node from a map
	 */
	private boolean removeNode(Map<Integer, ? extends TopologyNode> lNodes, int id) {
		return lNodes.remove(id) != null;
	}

	/**
	 * Return a unique node ID.
	 */
	private int getNextNodeId() {
		return --nextNodeId;
	}

	/** 
	 * Return a more realistic latency between 2 nodes by merging their respective latencies
	 */
	private double getRealAverage(int rank1, int rank2){
		Latency lat1 = search(groupCommunicationLatencies[rank1], rank2);
		Latency lat2 = search(groupCommunicationLatencies[rank2], rank1);
		int nbData1 = lat1.getNbData();
		int nbData2 = lat2.getNbData();
		return (lat1.getAverage() * nbData1 + lat2.getAverage() * nbData2) / (nbData2 + nbData1);
	}

	/**
	 * Search the desired latency in a table and return it
	 */
	private Latency search(Latency[] latencies, int rank){
		for(Latency lat : latencies){
			if(lat.getDestRank() == rank)
				return lat;
		}
		return null;
	}

	/**
	 * Return true if the collection of nodes contains the desired one.
	 */
	private boolean contains(Map<Integer, ? extends TopologyNode> listOfNodes, int id) {
		return listOfNodes.get(id) != null;
	}

	/**
	 * Remove from the collection of leafs, all those which are in the tree
	 */
	private void removeLeafs(Map<Integer, TopologyLeaf> leafs, TopologyNode tree) {
		if (tree.getClass().equals(TopologyLeaf.class)) {
			removeNode(leafs, tree.getId());
		} else {
			for (TopologyNode node : tree.getChildren()) {
				removeLeafs(leafs, node);
			}
		}
	}

	/**
	 * Insert a node in the tree
	 */
	private TopologyNode addNegativeValue(double value, TopologyNode l) {
		TopologyNode tn = new TopologyNode(getNextNodeId());
		TopologyNode parent = l.getParent();
		double previousLength = parent.getChildLength(l.getId());
		parent.removeChild(l.getId());
		parent.addChild(tn, previousLength - value);
		tn.addChild(l, value);
		return tn;
	}

	/**
	 * Insert a node in the tree
	 */
	private TopologyNode insertNodeDown(double length, double n1Length, TopologyNode n, TopologyNode n1) {
		TopologyNode tn = new TopologyNode(getNextNodeId());
		n1.removeChild(n.getId());
		n1.addChild(tn, length);
		tn.addChild(n, n1Length - length);
		return tn;
	}

	/**
	 * Insert a node in the tree
	 */
	private TopologyNode insertNodeUp(double length, double parentLength, TopologyNode parent, TopologyNode n1) {
		TopologyNode tn = new TopologyNode(getNextNodeId());
		parent.removeChild(n1.getId());
		parent.addChild(tn, parentLength - length);
		tn.addChild(n1, length);
		return tn;
	}

	/**
	 * For each subtree of the topology tree, insert a value in the map equals to the depth of the subtree.
	 * @param node the topology tree
	 * @param values the map which store the values
	 * @return depth of the topology tree
	 */
	private int setMaxDepth(TopologyNode node, Map<TopologyNode, Integer> values){
		int depth = 0;
		for(TopologyNode child : node.getChildren()){
			if(child.getClass().equals(TopologyNode.class)){
				int depth2 = setMaxDepth(child, values) + 1;
				if(depth2 > depth){
					depth = depth2;
				}
				values.put(child, depth2);
			}
		}
		return depth;
	}	
}
