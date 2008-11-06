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

import java.io.Serializable;
import java.util.List;

import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

import org.objectweb.proactive.extra.annotation.ErrorMessages;
import org.objectweb.proactive.extra.annotation.migration.signal.MigrationSignal;

import com.sun.source.tree.ClassTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.VariableTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

/**
 * <p>This class implements a visitor for the ProActiveProcessor, according to the Pluggable Annotation Processing API(jsr269) specification</p>
 * <p> It verifies whether a class declaration annotated with {@link org.objectweb.proactive.extra.annotation.activeobject.ActiveObject}</p>
 * <ul>
 *	<li>
 * has no methods that return null. 
 * This is because null cannot be checked on the caller-side - the caller will have a reference to a future, which most probably will not be null.
 *  </li>
 * </ul>
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public class ActiveObjectVisitorCTree extends TreePathScanner<Void,Trees> {

	private Messager compilerOutput;
	private Types typesUtil;

	public ActiveObjectVisitorCTree(ProcessingEnvironment procEnv) {
		compilerOutput = procEnv.getMessager();
		typesUtil = procEnv.getTypeUtils();
	}

	@Override
	public Void visitClass(ClassTree clazzTree, Trees trees) {

		testClassModifiers(clazzTree, trees);
		testClassConstructors(clazzTree, trees);

		// we have to do something in order not to visit the inner classes twice
		Void ret = null;
		List<? extends Tree> clazzMembers = clazzTree.getMembers();
		for (Tree clazzMember : clazzMembers) {
			if (clazzMember instanceof ClassTree) {
				// inner class detected, skipping
				continue;
			}

			// it's not clear how to visit class fields
			// so do it from here
			// TODO change it!
			if (clazzMember.getKind().equals(Kind.VARIABLE)) {
				VariableTree fieldNode = (VariableTree) clazzMember;

				if (fieldNode.getModifiers().getFlags().contains(Modifier.FINAL) && 
					! (fieldNode.getModifiers().getFlags().contains(Modifier.PRIVATE) ||
					   fieldNode.getModifiers().getFlags().contains(Modifier.PROTECTED))) 
				{
					reportError("The class declares the final field "
							+ fieldNode.getName() + ".\n"
							+ ErrorMessages.IS_FINAL_ERROR_MESSAGE, trees
							.getElement(getCurrentPath()));
				}

				if (fieldNode.getModifiers().getFlags().contains(Modifier.PUBLIC) &&
					!fieldNode.getModifiers().getFlags().contains(Modifier.FINAL) &&
					!hasAccessors(fieldNode.getName().toString(),clazzMembers)) 
				{
					reportWarning("The class declares the public field "
							+ fieldNode.getName() + ".\n"
							+ ErrorMessages.NO_GETTERS_SETTERS_ERROR_MESSAGE,
							trees.getElement(getCurrentPath()));
				}

			} else {

				ret = scan(clazzMember, trees);

			}
		}

		return ret;
	}

	private boolean hasAccessors(String fieldName,
			List<? extends Tree> clazzMembers) {
		boolean hasSetter = false;
		boolean hasGetter = false;
		String getterName = GenerateGettersSetters.getterName(fieldName);
		String setterName = GenerateGettersSetters.setterName(fieldName);

		for (Tree member : clazzMembers) {
			if (member.getKind().equals(Kind.METHOD)) {
				if (((MethodTree) member).getName().toString().equals(
						getterName)) {
					hasGetter = true;
				}
				if (((MethodTree) member).getName().toString().equals(
						setterName)) {
					hasSetter = true;
				}

				if (hasGetter && hasSetter) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public Void visitMethod(MethodTree methodNode, Trees trees) {

		// test modifiers
		if (methodNode.getModifiers().getFlags().contains(Modifier.FINAL)
				&& !methodNode.getModifiers().getFlags().contains(
						Modifier.PRIVATE)) {
			reportError(" The class declares the final method "
						+ methodNode.getName() + ".\n"+ ErrorMessages.HAS_FINAL_MEMBER_ERROR_MESSAGE, 
						trees.getElement(getCurrentPath()));
		}

		// test serializable args
		Element methodElement = trees.getElement(getCurrentPath());
		if (methodElement instanceof ExecutableElement) {
			ExecutableElement methodElem = (ExecutableElement) methodElement;
			
			// a migration signal can have non-serializable parameters - for instance, the ProActive Node!
			if(methodElem.getAnnotation(MigrationSignal.class)!=null){
				return super.visitMethod(methodNode, trees);
			}
			
			if (!methodElem.getModifiers().contains(Modifier.PRIVATE)
					&& !paramsSerializable(methodElem.getParameters())) {
				if (!isConstructor(methodNode))
					reportError(" The class declares the method "
							+ methodNode.getName()
							+ ".\n"
							+ ErrorMessages.NO_SERIALIZABLE_METHOD_ARG_ERROR_MESSAGE,
							trees.getElement(getCurrentPath()));
				else
					reportError(" The class declares the constructor "
							+ methodNode.getName()
							+ ".\n"
							+ ErrorMessages.NO_SERIALIZABLE_ARG_CONSTRUCTOR_ERROR_MESSAGE,
							trees.getElement(getCurrentPath()));
			}
		}

		return super.visitMethod(methodNode, trees);
	}

	/*
	 * Test for a MethodTree to see if it is a constructor or not
	 */
	private final boolean isConstructor(MethodTree executable) {
		// a constructor is a method that has returns nothing
		return executable.getReturnType() == null;
	}

	// all the constructor parameters must implement Serializable interface
	private boolean paramsSerializable(List<? extends VariableElement> params) {
		for (VariableElement param : params) {
			if (param.asType().getKind().equals(TypeKind.DECLARED)) {
				DeclaredType paramType = (DeclaredType) param.asType();
				if (!implementsSerializable(paramType)) {
					return false;
				}
			}
		}
		return true;
	}

	// check if the given type implements Serializable
	private boolean implementsSerializable(DeclaredType paramType) {
		// System.out.println("Verifying if " + paramType.toString() +
		// " is Serializable");
		boolean isSerializable = false;
		for (TypeMirror m : typesUtil.directSupertypes(paramType)) {
			isSerializable = isSerializable |
			// the type is Serializable
					Serializable.class.getName().equals(m.toString()) |
					// the type implements Serializable
					implementsSerializable((DeclaredType) m);

		}

		return isSerializable;
	}

	@Override
	public Void visitReturn(ReturnTree returnNode, Trees trees) {

		ExpressionTree returnExpression = returnNode.getExpression();

		if (returnExpression == null) {
			// no return value. good.
			return super.visitReturn(returnNode, trees);
		}

		if (returnExpression.getKind().equals(Tree.Kind.NULL_LITERAL)) {
			reportError(ErrorMessages.NO_NULL_RETURN_ERROR_MSG,trees.getElement(getCurrentPath()));
		}
		return super.visitReturn(returnNode, trees);
	}

	private void testClassModifiers(ClassTree clazzTree, Trees trees) {
		ModifiersTree modifiers = clazzTree.getModifiers();

		boolean isPublic = false;
		for (Modifier modifier : modifiers.getFlags()) {
			if (modifier.equals(Modifier.FINAL)) {

				reportError(ErrorMessages.IS_FINAL_ERROR_MESSAGE,trees.getElement(getCurrentPath()));
			}

			if (modifier.equals(Modifier.PUBLIC)) {
				isPublic = true;
			}
		}

		if (!isPublic) {
			reportError(ErrorMessages.IS_NOT_PUBLIC_ERROR_MESSAGE,trees.getElement(getCurrentPath()));
		}
	}

	/**
	 * 
	 * Checks that an empty no-argument constructor is defined
	 * 
	 */
	private void testClassConstructors(ClassTree clazzTree, Trees trees) {

		boolean hasNonArgsPublicConstructor = false;
		for (Tree member : clazzTree.getMembers()) {
			if (member instanceof MethodTree) {
				MethodTree constructor = (MethodTree) member;
				if (isConstructor(constructor)) {
					// it is constructor
					if (constructor.getParameters().size() == 0) {
						hasNonArgsPublicConstructor = true;

						if (constructor.getModifiers().getFlags().contains(
								Modifier.PRIVATE)) {
							reportError(ErrorMessages.NO_NOARG_CONSTRUCTOR_CANNOT_BE_PRIVATE_MESSAGE,trees.getElement(getCurrentPath()));
							return;
						}

						if (constructor.getBody().getStatements().size() > 0) {

							// process gracefully "super" statement in the
							// constructor
							boolean onlySuperInside = false;
							if (constructor.getBody().getStatements().size() == 1) {
								StatementTree statement = constructor.getBody()
										.getStatements().get(0);

								// TODO check it using Compiler Tree API
								if (statement.toString().startsWith("super")) {
									onlySuperInside = true;
								}
							}

							if (!onlySuperInside) {
								reportError(ErrorMessages.EMPTY_CONSTRUCTOR,trees.getElement(getCurrentPath()));
							}
						}

					}
				}
			}
		}

		if (!hasNonArgsPublicConstructor) {
			reportError(ErrorMessages.NO_NOARG_CONSTRUCTOR_ERROR_MESSAGE,trees.getElement(getCurrentPath()));
		}

	}

	protected void reportError(String msg, Element element) {
		compilerOutput.printMessage(Diagnostic.Kind.ERROR, msg, element);
	}

	protected void reportWarning(String msg, Element element) {
		compilerOutput.printMessage(Diagnostic.Kind.WARNING, msg, element);
	}

}
