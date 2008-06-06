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

import org.objectweb.proactive.annotation.ErrorMessages;

import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.type.ArrayType;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.EnumType;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.type.PrimitiveType;
import com.sun.mirror.type.TypeMirror;
import com.sun.mirror.type.VoidType;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import com.sun.mirror.util.SourcePosition;

/**
 * <p> This class implements the Visitor Pattern</p>
 * <p> It verifies whether a class declaration annotated with {@link org.objectweb.proactive.annotation.activeobject.ActiveObject} 
 * respects the following rules:</p>
 * <ul>
 * 		<li>must have a no-arg constructor</li>
 * 		<li>must implement the Serializable interface</li>
 * 		<li>must be subclassable
 * 			<ul>
 * 				<li>must not be final</li> 
 * 				<li>must not have final methods</li>
 * 				<li>must be public</li>
 * 			</ul>
 * 		</li>
 * 		<li>should not use standard Java synchronization primitives, eg volatile/synchronized keywords</li>
 * 		<li>must not use non-reifiable types for return values, eg instead of primitive types, just use ProActive primitive wrappers
 * 				this is because the return type also needs to be subclassed(a PAFuture will be created)</li>
 * 		<li>must use getters/setters in order to access fields of Active Objects</li>
 * </ul>
 * 		- must not use return values that can't have a meaning as a ProActive Future. 
 * 				ex: don't return null from a method! - TODO this can't actually be checked using apt 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public class ActiveObjectVisitor extends SimpleDeclarationVisitor {

	// error messages
	// TODO read from some config file outside the code
	private static final String ERROR_PREFIX_STATIC = " is annotated using the " 
		+ ActiveObject.class.getSimpleName() + " annotation.\n";
	
	private static final String ERROR_SUFFIX = "Please refer to the ProActive manual for further help on creating Active Objects.\n";
	
	private String ERROR_PREFIX;
	
	private Messager _compilerOutput;
	
	public ActiveObjectVisitor(Messager messager) {
		_compilerOutput = messager;
	}
	
	private ClassDeclaration _containingClass;


	@Override
	public void visitClassDeclaration(ClassDeclaration classDeclaration) {

		_containingClass = classDeclaration;
		
		ERROR_PREFIX = classDeclaration.getSimpleName() + ERROR_PREFIX_STATIC;
		
		testModifiers(classDeclaration);
		
		testTypes(classDeclaration);
		
		if (!hasNoArgConstructor(classDeclaration)) {
			reportError(classDeclaration, ErrorMessages.NO_NOARG_CONSTRUCTOR_ERROR_MESSAGE);
		}

		if (!implementsSerializable(classDeclaration)) {
			reportWarning(classDeclaration, ErrorMessages.NO_SERIALIZABLE_ERROR_MESSAGE);
		}
		
		super.visitClassDeclaration(classDeclaration);
		
	}

	/*
	 * test whether the types for the members of the class are reifiable - 
	 * return and parameter types for methods, types for fields 
	 */
	private void testTypes(ClassDeclaration classDeclaration) {

		// test the fields
		Collection<FieldDeclaration> fields = classDeclaration.getFields();
		for (FieldDeclaration fieldDeclaration : fields) {
			testFieldType(fieldDeclaration);
		}
		// test the methods
		
		Collection<MethodDeclaration> methods = classDeclaration.getMethods();
		for (MethodDeclaration methodDeclaration : methods) {
			testMethodTypes(methodDeclaration);
		}
		
	}


	private void testMethodTypes(MethodDeclaration methodDeclaration) {
		TypeMirror returnType = methodDeclaration.getReturnType();
		if( !isReifiable(returnType) ){
			reportError( methodDeclaration, returnType + ErrorMessages.RETURN_TYPE_NOT_REIFIABLE_ERROR_MESSAGE );
		}
	}


	/*
	 * test whether the type of the field specified is reifiable ?
	 */
	private void testFieldType(FieldDeclaration fieldDeclaration) {
		// TODO
		/*
		TypeMirror fieldType = fieldDeclaration.getType(); 
		if( !isReifiable(fieldType) ) {
			reportError( fieldDeclaration, fieldType + TYPE_NOT_REIFIABLE_ERROR_MESSAGE );
		}
		*/
	}


	/*
	 * Tests whether a given type is reifiable or not.
	 * The notion of "reifiable type" is given in the ProActive manual
	 */
	private boolean isReifiable(TypeMirror type) {
		if( type instanceof VoidType ) {
			// is ok
			return true;
		} else if( type instanceof PrimitiveType ) {
			// primitive types not reifiable
			return false;
		} else if( type instanceof EnumType ) {
			// enums not reifiable
			return false;
		} else if( type instanceof ArrayType ) {
			// TODO arrays not reifiable. right?
			return false;
		} else if( type instanceof ClassType ) {
			// must check whether it is reifiable or not
			ClassType classType = (ClassType)type;
			// must fulfill the same prereqs as an Active Object
			testModifiers(classType.getDeclaration());
		}
		return true;
	}


	/*
	 * test errors related to the modifiers applied 
	 * to the class and its members
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
				reportError(classDeclaration, ErrorMessages.IS_FINAL_ERROR_MESSAGE);
			}
			if(modifier.equals(Modifier.PRIVATE) || modifier.equals(Modifier.PROTECTED)){
				reportError(classDeclaration, ErrorMessages.IS_NOT_PUBLIC_ERROR_MESSAGE);
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
			if(modifier.equals(Modifier.VOLATILE)){
				reportError(fieldDeclaration, "The class declares the volatile field " 
						+ fieldDeclaration.getSimpleName() + ".\n" 
							+ ErrorMessages.HAS_SYNCHRONIZED_MEMBER_ERORR_MESSAGE );
			}
			if (modifier.equals(Modifier.PUBLIC)) {
				if( !checkGettersSetters(fieldDeclaration.getSimpleName()) ) {
					reportWarning( fieldDeclaration , "The class declares the public field"
							+ fieldDeclaration.getSimpleName() + ".\n" 
								+ ErrorMessages.NO_GETTERS_SETTERS_ERROR_MESSAGE );
				}
			}
		}
	}
	
	/*
	 * Test whether there are getters/setters defined for the given public field 
	 * @param fieldName -> the name of the public field  
	 */
	private boolean checkGettersSetters(String fieldName) {
		
		// remove eventual coding conventions
		// TODO more precise?
		String name;
		if (fieldName.startsWith("_")) {
			name = fieldName.substring(1).toLowerCase();
		}
		else {
			name = fieldName.toLowerCase();
		}
		
		reportWarning( _containingClass , "Checking the field:" + name);
		
		String getField = "get" + name;
		boolean foundGet = false;
		String setField = "set" + name;
		boolean foundSet = false;
		
		Collection<MethodDeclaration> methods = _containingClass.getMethods();
		for (MethodDeclaration methodDeclaration : methods) {
			if( !foundGet && methodDeclaration.getSimpleName().toLowerCase().equals(getField) ) {
				foundGet = true;
			}
			if ( !foundSet && methodDeclaration.getSimpleName().toLowerCase().equals(setField) ) {
				foundSet = true;
			}
			if( foundGet && foundSet )
				return true;
		}
		
		return false;
		
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
						+ methodDeclaration.getSimpleName() + ".\n" 
							+ ErrorMessages.HAS_FINAL_MEMBER_ERROR_MESSAGE );
			}
			if(modifier.equals(Modifier.SYNCHRONIZED)){
				reportError(methodDeclaration, "The class declares the synchronized method " 
						+ methodDeclaration.getSimpleName() + ".\n" 
							+ ErrorMessages.HAS_SYNCHRONIZED_MEMBER_ERORR_MESSAGE );
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
