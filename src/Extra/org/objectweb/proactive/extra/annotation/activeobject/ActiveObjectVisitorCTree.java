/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version
 * 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.extra.annotation.activeobject;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import org.objectweb.proactive.extra.annotation.ErrorMessages;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.Tree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

/**
 * <p>This class implements a visitor for the ProActiveProcessor, according to the Pluggable Annotation Processing API(jsr269) specification</p>
 * <p> It verifies whether a class declaration annotated with {@link org.objectweb.proactive.extra.annotation.activeobject.ActiveObject}</p>
 * <ul>
 * 	<li> 
 * has no methods that return null. 
 * This is because null cannot be checked on the caller-side - the caller will have a reference to a future, which most probably will not be null.
 *  </li>
 *  <li> TODO does not use <b>this</b> to reference the current active object; must use {@link org.objectweb.proactive.api.PAActiveObject.getStubOnThis()} instead </li>
 * </ul>
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public class ActiveObjectVisitorCTree extends TreePathScanner<Void,Trees> {
	
	private Messager _compilerOutput;
	
	public ActiveObjectVisitorCTree(Messager messager) {
		_compilerOutput = messager;
	}
	
	@Override
	public Void visitClass(ClassTree clazzTree, Trees trees) {
		
		// we have to do something in order not to visit the inner classes twice
		Void ret=null;
		List<? extends Tree> clazzMembers = clazzTree.getMembers();
		for (Tree clazzMember : clazzMembers) {
			if ( clazzMember instanceof ClassTree ) {
				// inner class detected, skipping
				continue;
			}
			ret = scan(clazzMember, trees);
		}
		
		return ret;		
	}
	
	@Override
	public Void visitReturn(ReturnTree returnNode, Trees trees) {
		
		ExpressionTree returnExpression = returnNode.getExpression();
		
		if ( returnExpression == null) {
			// no return value. good.
			return super.visitReturn(returnNode	, trees);
		}
		
		if (returnExpression.getKind().equals(Tree.Kind.NULL_LITERAL)) {
			_compilerOutput.printMessage(Diagnostic.Kind.ERROR , 
					ErrorMessages.NO_NULL_RETURN_ERROR_MSG , trees.getElement(getCurrentPath()) ); 
		}
		return super.visitReturn(returnNode	, trees);
	}
	
	// since we have no ConstructorTree in the API, we must resort to this...
	@Override
	public Void visitMethod(MethodTree methodNode, Trees trees) {
		
		// if it is a constructor
		if(isConstructor(methodNode)){
			// with no parameters
			if( methodNode.getParameters().isEmpty() ){
				// then it must have an empty body!
				if(!methodNode.getBody().getStatements().isEmpty()) {
					// report error
					_compilerOutput.printMessage(Diagnostic.Kind.ERROR , 
							"I've found a constructor with no arguments, but that does not have an empty body!" 
							+ ErrorMessages.NO_NOARG_CONSTRUCTOR_ERROR_MESSAGE , trees.getElement(getCurrentPath()) ); 
				}
			}
		}
		
		return super.visitMethod(methodNode, trees);
	}

	// hack
	private boolean isConstructor(MethodTree methodNode) {
		// TypeElement enclosingClass = trees.getScope(getCurrentPath()).getEnclosingClass();
		return methodNode.getName().toString().equals("<init>");
	}
	
}

