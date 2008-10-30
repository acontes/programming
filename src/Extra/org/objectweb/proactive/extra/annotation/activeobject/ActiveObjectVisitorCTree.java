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
import javax.lang.model.element.Modifier;
import javax.tools.Diagnostic;

import org.objectweb.proactive.extra.annotation.ErrorMessages;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
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
	
	private Messager compilerOutput;
	
	public ActiveObjectVisitorCTree(Messager messager) {
		compilerOutput = messager;
	}
	
	@Override
	public Void visitClass(ClassTree clazzTree, Trees trees) {
		
		testClassModifiers(clazzTree, trees);

		if (!hasNoArgEmptyConstructor(clazzTree)) {
			compilerOutput.printMessage(
					Diagnostic.Kind.ERROR ,
					ErrorMessages.NO_NOARG_CONSTRUCTOR_ERROR_MESSAGE,
					trees.getElement(getCurrentPath())
			);
		}

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
	public Void visitMethod(MethodTree methodNode, Trees trees) {

		testMethodModifiers(methodNode, trees);

		return super.visitMethod(methodNode	, trees);
	}

	
	@Override
	public Void visitReturn(ReturnTree returnNode, Trees trees) {
		
		ExpressionTree returnExpression = returnNode.getExpression();
		
		if ( returnExpression == null) {
			// no return value. good.
			return super.visitReturn(returnNode	, trees);
		}
		
		if (returnExpression.getKind().equals(Tree.Kind.NULL_LITERAL)) {
			compilerOutput.printMessage(Diagnostic.Kind.ERROR ,
					ErrorMessages.NO_NULL_RETURN_ERROR_MSG , trees.getElement(getCurrentPath()) ); 
		}
		return super.visitReturn(returnNode	, trees);
	}

	private void testClassModifiers(ClassTree clazzTree, Trees trees) {
		ModifiersTree modifiers = clazzTree.getModifiers();

		boolean isPublic = false;
		for (Modifier modifier : modifiers.getFlags()) {
			if (modifier.equals(Modifier.FINAL)) {

				compilerOutput.printMessage(
						Diagnostic.Kind.ERROR ,
						ErrorMessages.IS_FINAL_ERROR_MESSAGE,
						trees.getElement(getCurrentPath())
				);
			}

			if(modifier.equals(Modifier.PUBLIC)){
				isPublic = true;
			}
		}

		if(!isPublic){
			compilerOutput.printMessage(
					Diagnostic.Kind.ERROR ,
					ErrorMessages.IS_NOT_PUBLIC_ERROR_MESSAGE,
					trees.getElement(getCurrentPath())
			);
		}
	}


	private boolean hasNoArgEmptyConstructor(ClassTree clazzTree) {

		boolean hasAnyConstructors = false;
		for (Tree member: clazzTree.getMembers()) {
			if (member instanceof MethodTree) {
				MethodTree potentialEmptyConstructor = (MethodTree)member;
				if (potentialEmptyConstructor.getReturnType()==null) {
					// it is constructor
					hasAnyConstructors = true;
					if (potentialEmptyConstructor.getParameters().size()==0 &&
						potentialEmptyConstructor.getBody().getStatements().size() == 0	)
					{
						// found it
						return true;
					}
				}
			}
		}
		
		if (hasAnyConstructors == false) {
			// class does not have any constructors
			// it means that default empty constructor will be generated
			return true;
		}
		
		return false;
	}

	private void testMethodModifiers(MethodTree methodNode, Trees trees) {
		ModifiersTree modifiers = methodNode.getModifiers();

		for (Modifier modifier : modifiers.getFlags()) {
			if (modifier.equals(Modifier.FINAL)) {
				compilerOutput.printMessage(
						Diagnostic.Kind.ERROR ,
						" The class declares the final method "+ methodNode.getName() + ".\n"
						+ ErrorMessages.HAS_FINAL_MEMBER_ERROR_MESSAGE ,
						trees.getElement(getCurrentPath())
				);
			}
		}

	}
}

