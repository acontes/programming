/*
 * ################################################################
 * 
 * ProActive: The Java(TM) library for Parallel, Distributed, Concurrent
 * computing with Security and Mobility
 * 
 * Copyright (C) 1997-2002 INRIA/University of Nice-Sophia Antipolis Contact:
 * proactive-support@inria.fr
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Initial developer(s): The ProActive Team
 * http://www.inria.fr/oasis/ProActive/contacts.html Contributor(s):
 * 
 * ################################################################
 */
package org.objectweb.proactive.calcium.skeletons;

import java.util.Vector;

import org.objectweb.proactive.calcium.Task;
import org.objectweb.proactive.calcium.interfaces.Condition;
import org.objectweb.proactive.calcium.interfaces.Instruction;
import org.objectweb.proactive.calcium.interfaces.Skeleton;

/**
 * The while skeleton represents conditioned iteration.
 * The child skeleton will be executed while the Condition
 * holds true.
 * 
 * @author The ProActive Team (mleyton)
 *
 * @param <T>
 */
public class While<T> implements Instruction<T>, Skeleton<T> {

	Condition<T> cond;
	Skeleton<T> child;
	
	public While(Condition<T> cond, Skeleton<T> child){
		this.cond=cond;
		this.child=child;
	}
	
	public Vector<Instruction<T>> getInstructionStack() {
		Vector<Instruction<T>> v = new Vector<Instruction<T>>();
		v.add(this);
		return v;
	}
	
	public Task<T> compute(Task<T> task) throws Exception{
		
		if(cond.evalCondition(task.getObject())){
			//Get Child stack
			Vector<Instruction<T>> childStack=child.getInstructionStack();
			
			//Add me to evaluate while condition after executing child
			childStack.add(0,this); 
			
			//Add new elements to the task's stack
			Vector<Instruction<T>> taskStack=task.getStack();
			taskStack.addAll(childStack);
			task.setStack(taskStack);
		}
		return task;
	}
}
