package org.objectweb.proactive.examples.binarytree_multiactive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.RunActive;
import org.objectweb.proactive.Service;
import org.objectweb.proactive.annotation.multiactivity.Compatible;
import org.objectweb.proactive.annotation.multiactivity.DefineGroups;
import org.objectweb.proactive.annotation.multiactivity.DefineRules;
import org.objectweb.proactive.annotation.multiactivity.Group;
import org.objectweb.proactive.annotation.multiactivity.MemberOf;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.api.PAFuture;
import org.objectweb.proactive.extensions.annotation.Migratable;
import org.objectweb.proactive.multiactivity.MultiActiveService;

@DefineGroups({ @Group(name = "parallel", selfCompatible = true),
		@Group(name = "runtime", selfCompatible = true),
		@Group(name = "mutex", selfCompatible = false) })

@DefineRules({ 
	@Compatible(value = { "mutex", "parallel" }) 
	})

@Migratable
public class TreeBis implements Serializable , RunActive {
	private String key;
	private String value;
	private TreeBis left;
	private TreeBis right;
	private Integer graphicDepth;
	private boolean ma = false;

	private Random random = new Random();
	
	public TreeBis() {
	}

	public TreeBis(String key, String value, boolean ma) {
		this.left = null;
		this.right = null;
		this.key = key;
		this.value = value;
		this.ma = ma;
	}

	@MemberOf("parallel")
	public void insert(String key, String value, boolean AC, boolean ma) {
		this.ma=ma;
		int res = key.compareTo(this.key);
		if (res == 0) {
			// Same key --> Modify the current value
			this.value = value;
		} else if (res < 0) {
			// key < this.key --> store left
			if (left != null) {
				left.insert(key, value, AC,ma);
			} else {
				// Create the new node
				try {
					left = org.objectweb.proactive.api.PAActiveObject
							.newActive(this.getClass(), new Object[] { key,
									value, ma });
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Enabled Automatic Continuations
				if (AC) {
					try {
						org.objectweb.proactive.api.PAActiveObject
								.enableAC(org.objectweb.proactive.api.PAActiveObject
										.getStubOnThis());
					} catch (java.io.IOException e) {
					}
				}
			}
		} else {
			if (right != null) {
				right.insert(key, value, AC,ma);
			} else {
				try {
					right = org.objectweb.proactive.api.PAActiveObject
							.newActive(this.getClass(), new Object[] { key,
									value,ma });
				} catch (Exception e) {
					e.printStackTrace();
				}

				// Enabled Automatic Continuations
				if (AC) {
					try {
						org.objectweb.proactive.api.PAActiveObject
								.enableAC(org.objectweb.proactive.api.PAActiveObject
										.getStubOnThis());
					} catch (java.io.IOException e) {
					}
				}
			}
		}
	}

	@MemberOf("parallel")
	public void delete() {
		if (right != null) {
			right.delete();
		}
		if (left != null) {
			left.delete();
		}
		PAActiveObject.terminateActiveObject(true);
	}

	@MemberOf("parallel")
	public String getKey() {
		return key;
	}

	@MemberOf("parallel")
	public java.util.ArrayList<String> getKeys() {
		java.util.ArrayList<String> keys = new java.util.ArrayList<String>();
		if (key != null) {
			keys.add(key);
		}

		if (right != null) {
			java.util.ArrayList<String> ar = right.getKeys();
			PAFuture.waitFor(ar);
			keys.addAll(ar);
		}
		if (left != null) {
			ArrayList<String> al = left.getKeys();
			PAFuture.waitFor(al);
			keys.addAll(al);
		}
		return keys;
	}

	@MemberOf("parallel")
	public CustomValue getRandomLeafValue() {
		
		TreeBis first = null;
		TreeBis second = null; 
		
	//	System.out.println("TreeBis.getRandomLeafValue() " + value);
		
		int r = random.nextInt(100);
		
		if (r<50) {
			first=right;
			second = left; 
		} else {
			first=left;
			second= right; 
		}
		
		if (first != null) {
			return first.getRandomLeafValue();
		}
		if (second != null) {
			return second.getRandomLeafValue();
		}
		System.out.println("TreeBis.getRandomLeafValue() found leaf, returning value!");
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return new CustomValue(key);
	}

	@MemberOf("parallel")
	public String getValue() {
		return value;
	}

	@MemberOf("parallel")
	public TreeBis getLeft() {
		return left;
	}

	@MemberOf("parallel")
	public TreeBis getRight() {
		return right;
	}

	@MemberOf("parallel")
	public int depth() {
		int rightDepth = 0;
		int leftDepth = 0;
		if (right != null) {
			rightDepth = right.depth();
		}
		if (left != null) {
			leftDepth = left.depth();
		}
		if (leftDepth < rightDepth) {
			return ++rightDepth;
		}
		return ++leftDepth;
	}

	// Change Automatic Continuations state
	@MemberOf("runtime")
	public void enableAC() {
		try {
			org.objectweb.proactive.api.PAActiveObject
					.enableAC(org.objectweb.proactive.api.PAActiveObject
							.getStubOnThis());
			if (right != null) {
				right.enableAC();
			}
			if (left != null) {
				left.enableAC();
			}
		} catch (java.io.IOException e) {
		}
	}

	@MemberOf("runtime")
	public void disableAC() {
		try {
			org.objectweb.proactive.api.PAActiveObject
					.disableAC(org.objectweb.proactive.api.PAActiveObject
							.getStubOnThis());
			if (right != null) {
				right.disableAC();
			}
			if (left != null) {
				left.disableAC();
			}
		} catch (java.io.IOException e) {
		}
	}

//	@Override
	public void runActivity(Body body) {
		System.out.println(this.getClass().getName() + " runActivity");
		
		if (!ma) {
			System.out.println("TreeBis.runActivity() Active Object is SINGLE threaded");
		new Service(body).fifoServing();
		} else {
			System.out.println("TreeBis.runActivity() Active Object is MULTI threaded");
		(new MultiActiveService(body)).multiActiveServing(1, true, true);
		} 
	}
}
