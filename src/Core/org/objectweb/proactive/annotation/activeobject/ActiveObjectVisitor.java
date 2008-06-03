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
package org.objectweb.proactive.annotation.activeobject;

import java.io.Serializable;
import java.util.Collection;

import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import com.sun.mirror.util.SourcePosition;

/**
 * TODO javadoc format
 * This class implements the Visitor Pattern
 * It verifies whether a class declaration annotated with @ActiveObject respects the following rules:
 * 		- must have a no-arg constructor
 * 		- must implement the Serializable interface
 * 		- must be subclassable :
 * 			- must not be final, 
 * 			- must not have final fields/methods
 * 			- must be public
 * 		- should not use standard Java synchronization primitives, eg volatile/synchronized keywords
 * TODO
 * 		- must not use non-reifiable types, eg instead of primitive types, just use ProActive primitive wrappers
 * 		- must not use return values that can't have a meaning as a ProActive Future. ex: don't return null from a method!
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public class ActiveObjectVisitor extends SimpleDeclarationVisitor {

	// error messages
	// TODO read from some config file outside the code
	private static final String ERROR_PREFIX_STATIC = " is annotated using the " 
		+ ActiveObject.class.getSimpleName() + " annotation.\n";
	private static final String NO_NOARG_CONSTRUCTOR_ERROR_MESSAGE = "This object does not define a no-arg constructor. " +
			"An active object must have a no-arg constructor.\n";
	private static final String NO_SERIALIZABLE_ERROR_MESSAGE = "An active object should implement the Serializable interface.\n";
	private static final String IS_FINAL_ERROR_MESSAGE = "An active object must be subclassable, and therefore cannot be final.\n";
	private static final String HAS_FINAL_MEMBER_ERROR_MESSAGE = "An active object must be subclassable, and therefore cannot have final members.\n";
	private static final String HAS_SYNCHRONIZED_MEMBER_ERORR_MESSAGE = "An active object already has an implicit synchronisation mechanism, wait-by-necessity. The synchronized/volatile keywords are therefore useless for a member of an active object.\n";
	private static final String IS_NOT_PUBLIC_ERROR_MESSAGE = "An active object must be public.\n";
	private static final String ERROR_SUFFIX = "Please refer to the ProActive manual for further help on creating Active Objects.\n";
	
	private String ERROR_PREFIX;
	
	private Messager _compilerOutput;
	
	public ActiveObjectVisitor(Messager messager) {
		_compilerOutput = messager;
	}


	@Override
	public void visitClassDeclaration(ClassDeclaration classDeclaration) {
		
		ERROR_PREFIX = classDeclaration.getSimpleName() + ERROR_PREFIX_STATIC;

		testModifiers(classDeclaration);
		
		if (!hasNoArgConstructor(classDeclaration)) {
			reportError(classDeclaration, NO_NOARG_CONSTRUCTOR_ERROR_MESSAGE);
		}

		if (!implementsSerializable(classDeclaration)) {
			reportWarning(classDeclaration, NO_SERIALIZABLE_ERROR_MESSAGE);
		}
		
		super.visitClassDeclaration(classDeclaration);
		
	}
	
	/*
	 * test whether a class can be subclassed
	 */
	private void testModifiers(ClassDeclaration classDeclaration) {
		// class definition modifiers
		testClassModifiers(classDeclaration);
		// test the modifiers of all members of the class
		testMemberModifiers(classDeclaration);
	}

	/*
	 * test the modifiers of the members(fields or methods)
	 * of the class represented by the given ClassDeclaration
	 * the modifiers:
	 * 		- must not be final
	 * 		- must not be synchronized
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private void testMemberModifiers(ClassDeclaration classDeclaration) {

		// test the fields
		Collection<FieldDeclaration> fields = classDeclaration.getFields();
		for (FieldDeclaration fieldDeclaration : fields) {
			testFieldModifiers(fieldDeclaration);
		}
		
		// test the methods
		Collection<MethodDeclaration> methods = classDeclaration.getMethods();
		for (MethodDeclaration methodDeclaration : methods) {
			testMethodModifiers(methodDeclaration);
		}
		
	}

	/*
	 * test the modifiers of the ClassDeclaration
	 * 		- must not be final
	 * 		- must be public
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private void testClassModifiers(ClassDeclaration classDeclaration) {
		Collection<Modifier> modifiers = classDeclaration.getModifiers();
		
		for (Modifier modifier : modifiers) {
			if (modifier.equals(Modifier.FINAL)) {
				reportError(classDeclaration, IS_FINAL_ERROR_MESSAGE);
			}
			if(modifier.equals(Modifier.PRIVATE) || modifier.equals(Modifier.PROTECTED)){
				reportError(classDeclaration, IS_NOT_PUBLIC_ERROR_MESSAGE);
			}
		}
	}
	
	/*
	 * test the modifiers of a FieldDeclaration
	 * 		- must not be final
	 * 		- should not be volatile
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private void testFieldModifiers(FieldDeclaration fieldDeclaration) {
		Collection<Modifier> modifiers = fieldDeclaration.getModifiers();
		
		for (Modifier modifier : modifiers) {
			if (modifier.equals(Modifier.FINAL)) {
				reportError(fieldDeclaration, " The class declares the final field " 
						+ fieldDeclaration.getSimpleName() + ".\n" + HAS_FINAL_MEMBER_ERROR_MESSAGE );
			}
			if(modifier.equals(Modifier.VOLATILE)){
				reportError(fieldDeclaration, "The class declares the volatile field " 
						+ fieldDeclaration.getSimpleName() + ".\n" + HAS_SYNCHRONIZED_MEMBER_ERORR_MESSAGE );
			}
		}
	}
	
	/*
	 * test the modifiers of a MethodDeclaration
	 * 		- must not be final
	 * 		- should not be synchronized
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private void testMethodModifiers(MethodDeclaration methodDeclaration) {
		Collection<Modifier> modifiers = methodDeclaration.getModifiers();
		
		for (Modifier modifier : modifiers) {
			if (modifier.equals(Modifier.FINAL)) {
				reportError(methodDeclaration, " The class declares the final method " 
						+ methodDeclaration.getSimpleName() + ".\n" + HAS_FINAL_MEMBER_ERROR_MESSAGE );
			}
			if(modifier.equals(Modifier.SYNCHRONIZED)){
				reportError(methodDeclaration, "The class declares the synchronized method " 
						+ methodDeclaration.getSimpleName() + ".\n" + HAS_SYNCHRONIZED_MEMBER_ERORR_MESSAGE );
			}
		}
	}
	
	/**
	 * test whether a class implements the serializable interface
	 */
	private boolean implementsSerializable(ClassDeclaration classDeclaration) {
		
		Collection<InterfaceType> implementedInterfaces = classDeclaration.getSuperinterfaces();
		// one of the implemented interfaces must be Serializable
		for (InterfaceType interfaceType : implementedInterfaces) {
			if (Serializable.class.getName().equals(
					interfaceType.getDeclaration().getQualifiedName()
					)) {
				return true;
			}
		}
		
		return false;
	}


	/**
	 * test whether a class has a no-arg constructor
	 */
	private boolean hasNoArgConstructor(ClassDeclaration classDeclaration) {
		
		Collection<ConstructorDeclaration> constructors = classDeclaration.getConstructors();
		
		if (constructors.isEmpty()) {
			// has no constructors at all! son of a butch!
			return false;
		}
		
		// one of the constructors must have no args
		for (ConstructorDeclaration constructorDeclaration : constructors) {
			if (constructorDeclaration.getParameters().isEmpty()) {
				// we have a no-arg constructor
				return true;
			}
		}
		
		return false;
	}
	
	private void reportError( Declaration declaration , String msg ) {
		SourcePosition classPos = declaration.getPosition();
		_compilerOutput.printError( classPos , "[ERROR]" + ERROR_PREFIX + msg + ERROR_SUFFIX);
	}
	
	private void reportWarning( Declaration declaration, String msg) {
		SourcePosition classPos = declaration.getPosition();
		_compilerOutput.printWarning( classPos , "[WARNING]" + ERROR_PREFIX + msg + ERROR_SUFFIX);
	}
}
