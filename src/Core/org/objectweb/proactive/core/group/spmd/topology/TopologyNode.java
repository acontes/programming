package org.objectweb.proactive.core.group.spmd.topology;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represent a node of a topology tree.
 */
public class TopologyNode implements Serializable, Comparable<TopologyNode>{

	/**
	 * id of the node (each node of the tree must have a unique id)
	 */
	private Integer id;

	/**
	 * Length to parent node or -1 if there is no parent
	 */
	private double length = -1;

	/**
	 * Index of trust of the node, lowest is the value and better is the
	 * confidence of having a good topology tree.(not available)
	 */
	private double indexOfTrust = -1;

	/**
	 * Map containing the length between the node and his children, identified
	 * by their id.
	 */
	private Map<Integer, Double> childLength;

	/**
	 * Map containing the children of the node, identified by their id.
	 */
	private Map<Integer, TopologyNode> children;

	/**
	 * The parent's node of this node.
	 */
	private TopologyNode parent;
	
	
	public TopologyNode(){
	}
	
	public TopologyNode(int id) {
		childLength = new HashMap<Integer, Double>();
		children = new HashMap<Integer, TopologyNode>();
		this.id = id;
	}

	public TopologyNode(int id, double length, TopologyNode parent) {
		this(id);
		parent.addChild(this, length);
	}

	public TopologyNode(int id, TopologyNode parent, Map<Integer, TopologyNode> children, Map<Integer, Double> childLength) {
		this.id = id;
		this.parent = parent;
		this.childLength = childLength;
		this.children = children;
		for (TopologyNode n : children.values()) {
			n.setParent(this);
			n.length = childLength.get(n.getId());
		}
	}

	public Collection<TopologyNode> getChildren() {
		return children.values();
	}

	public TopologyNode getChild(int id) {
		return children.get(id);
	}

	public double getChildLength(int childId) {
		return childLength.get(childId);
	}

	public double getParentLength(){
		return length;
	}

	public Integer getId() {
		return id;
	}

	public void setId(int newId){
		if(parent != null && newId != id){
			TopologyNode parent2 = parent;
			Double length = parent2.getChildLength(this.id);
			try{
				parent2.removeChild(this.id);
				this.id = newId;
				parent2.addChild(this, length);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		else{
			this.id = newId;
		}
	}

	public int getNbChildren() {
		return children.size();
	}

	public TopologyNode getParent() {
		return parent;
	}

	protected void setParent(TopologyNode parent) {
		this.parent = parent;
	}

	public void setLength(int childId, double length) {
		if(children.containsKey(childId)){
			childLength.put(childId, length);
			children.get(childId).length = length;
		}
	}

	public double getIndexOfTrust() {
		return indexOfTrust;
	}

	public void setIndexOfTrust(double indexOfThrust) {
		this.indexOfTrust = indexOfThrust;
	}

	public void removeChild(int id) {
		TopologyNode child = children.remove(id);
		if(child != null){
			child.length = -1;
			childLength.remove(id);
		}
	}

	/**
	 * Return the child (or sub child) with the given id, or null if no such
	 * node exist.
	 * @param id id of the child to seek
	 */
	public TopologyNode getNodeRecursively(int id){
		if(this.id == id)
			return this;
		else for(TopologyNode child : getChildren()){
			TopologyNode node = child.getNodeRecursively(id);
			if(node != null)
				return node;
		}
		return null;
	}

	/**
	 * Add a new child to the node.
	 * 
	 * @param child the new child to add.
	 * @param length the length between the node and the child.
	 */
	public void addChild(TopologyNode child, double length) {
		children.put(child.getId(), child);
		child.setParent(this);
		child.length = length;
		childLength.put(child.getId(), length);
	}

	/**
	 * Return the number of leaf present in the ramifications of the node.
	 * 
	 * @return the number of leaf present in the ramifications of the node.
	 */
	public int nbLeaf() {
		int total = 0;
		for (TopologyNode n : getChildren())
			total += n.nbLeaf();
		return total;
	}

	/**
	 * Return true if the node or one of his children has the same id.
	 * 
	 * @param id the researched id.
	 * @param recursively if true search in all the tree, else only search among the direct children
	 * @return true if the node or one of his children has the same id.
	 */
	public boolean contains(int id, boolean recursively) {
		if (this.getId() == id)
			return true;
		for (TopologyNode node : getChildren()) {
			if(recursively && node.contains(id, true))
				return true;
			else if(node.getId() == id)
				return true;
		}
		return false;
	}

	public int getMaxDepth(){
		int depth = 1;
		for(TopologyNode child : getChildren()){
			int depth2 = child.getMaxDepth() + 1;
			if(depth2 > depth){
				depth = depth2;
			}
		}
		return depth;
	}

	/**
	 * Return a String representing the tree but with the index of trust instead
	 * of the node's length.
	 * 
	 * @return a String representing the tree but with the index of trust
	 *         instead of the node's length.
	 */
	public String printIndexOfTrust() {
		String s = "(X[";
		String value = new Double(getIndexOfTrust()).toString();
		if (value.length() > 5)
			value = value.substring(0, 5);
		s += value + "]";
		for (TopologyNode n : getChildren()) {
			s += " ";
			s += n.printIndexOfTrust();
		}
		s += ")";
		return s;
	}

	@Override
	public String toString() {
		String s = "(" + id;
		if (getParent() != null) {
			String value = new Double(getParent().getChildLength(getId())).toString();
			int index = value.indexOf(".");
			if(index >= 0){
				value = value.substring(0, Math.min(index + 3, value.length()));				
			}
			s += "_" + value;
		}
		for (TopologyNode n : getChildren()) {
			s += " ";
			if(n.getClass().equals(TopologyLeaf.class))
				s += n.getId();
			else s += n.toString();
		}
		s += ")";
		return s;
	}
	
	/**
	 * Used to debug
	 */
	public String toString2() {
		String s = "(X";
		if (getParent() != null) {
			String value = new Double(getParent().getChildLength(getId())).toString();
			if (value.length() > 4)
				value = value.substring(0, 4);
			s += "_" + value;
		}
		for (TopologyNode n : getChildren()) {
			s += " ";
			s += n.toString2();
		}
		s += ")";
		return s;
	}

	/**
	 * Used to debug
	 */
	public String printDepth(Map<TopologyNode, Integer> values) {
		String s = "(X";
		if (getParent() != null) {
			int val = values.get(this);
			s += "_" + val;
		}
		for (TopologyNode n : getChildren()) {
			s += " ";
			if(n.getClass().equals(TopologyLeaf.class))
				s += n.getId();
			else s += n.printDepth(values);
		}
		s += ")";
		return s;
	}

	@Override
	public int compareTo(TopologyNode o) {
		if(o == null)
			return 1;
		if(parent == null){
			if(o.parent == null)
				return 0;
			else return -1;
		}
		if(o.getParent() == null)
			return 1;
		double myLength = parent.getChildLength(id);
		double oLength = o.getParent().getChildLength(o.getId());
		if(myLength > oLength) 
			return 1;
		if(myLength < oLength)
			return -1;
		return 0;
	}
}
