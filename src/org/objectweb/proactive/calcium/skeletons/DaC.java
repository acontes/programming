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
import org.objectweb.proactive.calcium.exceptions.ParameterException;
import org.objectweb.proactive.calcium.exceptions.SchedulingException;
import org.objectweb.proactive.calcium.interfaces.Condition;
import org.objectweb.proactive.calcium.interfaces.Conquer;
import org.objectweb.proactive.calcium.interfaces.Divide;
import org.objectweb.proactive.calcium.interfaces.Instruction;
import org.objectweb.proactive.calcium.interfaces.Skeleton;

/**
 * This skeleton represents Divide and Conquer parallelism (data parallelism).
 * To function, a Divide, Condition, and Conquer objects must
 * be passed as parameter.
 * 
 * If the Condition is met, a Task will be divided using the Divide object. 
 * If the Condition is not met, the child skeleton will be executed.
 * If the task has subchilds, then the Conquer object will be used to conquer
 * the child tasks into the parent task.
 * 
 * @author The ProActive Team (mleyton)
 *
 * @param <T>
 */
public class DaC<T> implements Skeleton<T>, Instruction<T> {

	Divide<T> div;
	Conquer<T> conq;
	Condition<T> cond;
	Skeleton<T> child;
	
	/**
	 * Creates a Divide and Conquer skeleton structure
	 * @param div Divides a task into subtasks
	 * @param cond True if divide should be applied to the task. False if it should be solved. 
	 * @param child The skeleton that should be applied to the subtasks.
	 * @param conq Conqueres the computed subtasks into a single task.
	 */
	public DaC(Divide<T> div, Condition<T> cond, Skeleton<T> child, Conquer<T> conq){
	
		this.div=div;
		this.cond=cond;
		this.child=child;
		this.conq=conq;
	}
	
	public Vector<Instruction<T>> getInstructionStack() {

		Vector<Instruction<T>> v= new Vector<Instruction<T>>();
		v.add(this);
		
		return v;
	}

	public Task<T> compute(Task<T> t) throws SchedulingException{
		
		//Conquer if children are present
		if(t.hasFinishedChild()){
			return conquer(t);
		}
		
		//Else no children are present

		//Split the task if required
		if(cond.evalCondition(t.getObject())){
			return divide(t);
		}
		//else solve the tasks
		else{
			return execute(t);
		}
	}
	
	protected Task<T> execute(Task<T> t){
		//Append the child skeleton code to the stack
		Vector<Instruction<T>> currentStack = t.getStack();
		currentStack.addAll(child.getInstructionStack());
		t.setStack(currentStack);
		return t;
	}
	
	protected Task<T> divide(Task<T> parent) throws SchedulingException{
		
		/*
		 * We pass the t.object to the div. Each result
		 * of divide will be then encapsulated by a Task, and 
		 * the Tasks will be held in an array  
		 */	
		Vector<T> childObjects=div.divide(parent.getObject());
		
		if(childObjects.size()<=0){
			String msg="Parameter was divided into less than 1 part.";
			logger.debug(msg);
			throw new ParameterException(msg);
		}
		
		for(T o:childObjects){
			Task<T> child = new Task<T>(o);
			child.pushInstruction(this); //To do divide or execute in the child
			parent.addReadyChild(child); //parent holds it's children
		}

		//Now we put a DaC (conquer) on the instruction stack of the parent 
		parent.pushInstruction(this);
		
		return parent;
	}
	
	protected Task<T> conquer(Task<T> parent) throws SchedulingException {
		
		/**
		 * We get the result objects from the child
		 * and then we execute the conquer.
		 * Finally, we create a rebirth task
		 * of the parent with the result of the conquer.
		 */
		Vector<T> childObjects = new Vector<T>();
		
		while(parent.hasFinishedChild()){
			
			Task<T> child = parent.getFinishedChild();
			childObjects.add(child.getObject());
		}

		if(childObjects.size() <=0 ){
			String msg="Can't conquer less than one child parameter!";
			logger.error(msg);
			throw new ParameterException(msg);
		}
		
 		T resultObject=conq.conquer(parent.getObject(), childObjects);
 		Task<T> resultTask=parent.reBirth(resultObject);
 		
		return resultTask;
	}

	public String toString(){
		return "D&C";
	}
}
