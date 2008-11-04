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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.objectweb.proactive.extra.annotation.ErrorMessages;

import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.InterfaceDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;
import com.sun.mirror.declaration.Modifier;
import com.sun.mirror.declaration.ParameterDeclaration;
import com.sun.mirror.declaration.TypeDeclaration;
import com.sun.mirror.type.ArrayType;
import com.sun.mirror.type.ClassType;
import com.sun.mirror.type.DeclaredType;
import com.sun.mirror.type.EnumType;
import com.sun.mirror.type.InterfaceType;
import com.sun.mirror.type.PrimitiveType;
import com.sun.mirror.type.TypeMirror;
import com.sun.mirror.type.VoidType;
import com.sun.mirror.util.SimpleDeclarationVisitor;
import com.sun.mirror.util.SourcePosition;

/**
 * <p> This class implements a visitor for the ActiveObjectAnnotationProcessor, conforming to the Mirror API(jsr199)</p>
 * <p> It verifies whether a class declaration annotated with {@link org.objectweb.proactive.extra.annotation.activeobject.ActiveObject} 
 * respects the rules specified <a href="http://confluence.activeeon.com/display/PROG/Feature+Compile+time+annotations">here</a></p>
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public class ActiveObjectVisitorAPT extends SimpleDeclarationVisitor {
	
	// error messages
	private static final String ERROR_PREFIX_STATIC = " is annotated using the " 
		+ ActiveObject.class.getSimpleName() + " annotation.\n";
	
	private static final String ERROR_SUFFIX = "Please refer to the ProActive manual for further help on creating Active Objects.\n";
	
	private transient String ERROR_PREFIX;
	
	private final Messager _compilerOutput;
	
	public ActiveObjectVisitorAPT(final Messager messager) {
		super();
		_compilerOutput = messager;
	}
	
	private transient ClassDeclaration _containingClass;


	@Override
	public void visitClassDeclaration(ClassDeclaration classDeclaration) {

		_containingClass = classDeclaration;
		
		ERROR_PREFIX = classDeclaration.getSimpleName() + ERROR_PREFIX_STATIC;
		
		testClassModifiers(classDeclaration);
		
		ConstructorCheckResult ccr = verifyConstructors(classDeclaration);
		
		if (!ccr.hasNoArgConstructor) {
			reportError(classDeclaration, ErrorMessages.NO_NOARG_CONSTRUCTOR_ERROR_MESSAGE);
		}
		
		if(ccr.isFinalNoArgConstructor){
			reportError(classDeclaration, ErrorMessages.NO_NOARG_CONSTRUCTOR_CANNOT_BE_PRIVATE_MESSAGE);
		}
		
		if(!ccr.allParamsSerializable){
			for (ConstructorDeclaration offendingConstructor : ccr.offendingConstructors) {
				reportError(offendingConstructor, ErrorMessages.NO_SERIALIZABLE_ARG_CONSTRUCTOR_ERROR_MESSAGE);
			}
		}
		
//		if (!implementsSerializable(classDeclaration)) {
//			reportWarning(classDeclaration, ErrorMessages.NO_SERIALIZABLE_ERROR_MESSAGE);
//		}
		
		// super.visitClassDeclaration(classDeclaration);
		// visit the subcomponents of this class
		// this should have been already provided by the MirrorAPI. bad API, bad! :P
		final Collection<MethodDeclaration> methods = classDeclaration.getMethods();
		for (MethodDeclaration methodDeclaration : methods) {
			methodDeclaration.accept(this);
		}
		
		final Collection<FieldDeclaration> fields = classDeclaration.getFields();
		for (FieldDeclaration fieldDeclaration : fields) {
			fieldDeclaration.accept(this);
		}
		
	}


	@Override
	public void visitMethodDeclaration(MethodDeclaration methodDeclaration) {
		
		testMethodModifiers(methodDeclaration);
		testParameterTypes(methodDeclaration);
		
		//checkReturnType(methodDeclaration); - REMOVED
		
		super.visitMethodDeclaration(methodDeclaration);
	}
	
	private void testParameterTypes(MethodDeclaration methodDeclaration) {
		if(!methodDeclaration.getModifiers().contains(Modifier.PRIVATE) && 
				!paramsSerializable(methodDeclaration.getParameters())){
			reportError( methodDeclaration, ErrorMessages.NO_SERIALIZABLE_METHOD_ARG_ERROR_MESSAGE);
		}
	}


	@Override
	public void visitFieldDeclaration(FieldDeclaration fieldDeclaration) {
		
		testFieldModifiers(fieldDeclaration);
		
		super.visitFieldDeclaration(fieldDeclaration);
	}
	
	/*
	 * Test if the return type of the method can be made a Future Object
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private boolean checkReturnType( final MethodDeclaration methodDeclaration ) {
		// check the return type
		final TypeMirror returnType = methodDeclaration.getReturnType();
		if( !isReifiable(returnType) ){
			reportError( methodDeclaration, returnType + ErrorMessages.RETURN_TYPE_NOT_REIFIABLE_ERROR_MESSAGE );
			return false;
		}
		return true;
	}

	/*
	 * Tests whether a given type is reifiable or not.
	 * The notion of "reifiable type" is given in the ProActive manual
	 * @return : true if reifiable, false else
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
			testFutureObject(classType.getDeclaration());
		}
		return true;
	}
	
	/*
	 * Test whether the object defined by the given class declaration 
	 * can be the type of a ProActive Future
	 * TODO what are the prereqs?
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private boolean testFutureObject(ClassDeclaration classDeclaration) {
		// test the modifiers
		boolean ret = testClassModifiers(classDeclaration);
		// test the contained fields and methods
		super.visitClassDeclaration(classDeclaration);
		
		return ret;
	}

	/*
	 * test the modifiers of the ClassDeclaration
	 * 		- must not be final
	 * 		- must be public
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private boolean testClassModifiers(ClassDeclaration classDeclaration) {
		Collection<Modifier> modifiers = classDeclaration.getModifiers();
		
		boolean isPublic = false;
		for (Modifier modifier : modifiers) {
			if (modifier.equals(Modifier.FINAL)) {
				reportError(classDeclaration, ErrorMessages.IS_FINAL_ERROR_MESSAGE);
				return false;
			}
			if(modifier.equals(Modifier.PUBLIC)){
				isPublic = true;
			}
		}
		
		if(!isPublic){
			reportError(classDeclaration, ErrorMessages.IS_NOT_PUBLIC_ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}
	
	/*
	 * test the modifiers of a FieldDeclaration
	 * 		- must not be final
	 * 		- if public, should have getters/setters for accessing the value
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private boolean testFieldModifiers(FieldDeclaration fieldDeclaration) {
		Collection<Modifier> modifiers = fieldDeclaration.getModifiers();
		
		for (Modifier modifier : modifiers) {
			if (modifier.equals(Modifier.FINAL)) {
				reportError(fieldDeclaration, "The class declares the final field "
						+ fieldDeclaration.getSimpleName() + ".\n" 
						+ ErrorMessages.IS_FINAL_ERROR_MESSAGE);
				return false;
			}

			if (modifier.equals(Modifier.PUBLIC)) {
				if( !checkGettersSetters(fieldDeclaration.getSimpleName()) ) {
					reportWarning( fieldDeclaration , "The class declares the public field"
							+ fieldDeclaration.getSimpleName() + ".\n" 
								+ ErrorMessages.NO_GETTERS_SETTERS_ERROR_MESSAGE );
				}
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * Test whether there are getters/setters defined for the given public field 
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private boolean checkGettersSetters(String fieldName) {
		
		final String getField = GenerateGettersSetters.getterName(fieldName);
		boolean foundGet = false;
		final String setField = GenerateGettersSetters.setterName(fieldName);
		boolean foundSet = false;
		
		Collection<MethodDeclaration> methods = _containingClass.getMethods();
		for (MethodDeclaration methodDeclaration : methods) {
			if( !foundGet && methodDeclaration.getSimpleName().equals(getField) ) {
				foundGet = true;
			}
			if ( !foundSet && methodDeclaration.getSimpleName().equals(setField) ) {
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
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private boolean testMethodModifiers(MethodDeclaration methodDeclaration) {
		Collection<Modifier> modifiers = methodDeclaration.getModifiers();

		if(modifiers.contains(Modifier.FINAL)&&!modifiers.contains(Modifier.PRIVATE)){
			reportError(methodDeclaration, " The class declares the final method " 
					+ methodDeclaration.getSimpleName() + ".\n" 
					+ ErrorMessages.HAS_FINAL_MEMBER_ERROR_MESSAGE );
			return false;
		}
		
		return true;
	}
	
	/*
	 * test whether a class implements the serializable interface
	 * @return: true , is the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
//	private boolean implementsSerializable(ClassDeclaration classDeclaration) {
//		
//		Collection<InterfaceType> implementedInterfaces = classDeclaration.getSuperinterfaces();
//		// one of the implemented interfaces must be Serializable
//		for (InterfaceType interfaceType : implementedInterfaces) {
//			if (Serializable.class.getName().equals(
//					interfaceType.getDeclaration().getQualifiedName()
//					)) {
//				return true;
//			}
//		}
//		
//		return false;
//	}

	
	/*
	 * test for the criteria that must be met by the class constructors:
	 * * there must be the empty no-arg constructor
	 * * constructor parameters must be serializable 
	 * @return: true , if the class cannot be an active object
	 * 			false, if the object can be an active object
	 */
	private ConstructorCheckResult verifyConstructors(ClassDeclaration classDeclaration) {
		
		ConstructorCheckResult ccr = new ConstructorCheckResult();
		
		Collection<ConstructorDeclaration> constructors = classDeclaration.getConstructors();
		
		if (constructors.isEmpty()) {
			// has no constructors at all! son of a butch!
			ccr.hasNoArgConstructor = true; 
			return ccr;
		}
		
		// one of the constructors must have no args
		for (ConstructorDeclaration constructorDeclaration : constructors) {
			
			if(constructorDeclaration.getParameters().isEmpty()) {
				ccr.hasNoArgConstructor = true;
				if(constructorDeclaration.getModifiers().contains(Modifier.PRIVATE))
					ccr.isFinalNoArgConstructor = true;
			}
			if(!paramsSerializable(constructorDeclaration.getParameters())) {
				ccr.allParamsSerializable = false;
				ccr.offendingConstructors.add(constructorDeclaration);
			}
						
		}
		
		return ccr;
	}
	
	final class ConstructorCheckResult {
		
		public ConstructorCheckResult() {
			hasNoArgConstructor = false;
			allParamsSerializable = true;
			isFinalNoArgConstructor = false;
		}
		
		public ConstructorCheckResult(boolean hasNoArgs, boolean allParams, boolean isfinal) {
			hasNoArgConstructor = hasNoArgs;
			allParamsSerializable = allParams;
			isFinalNoArgConstructor = isfinal;
		}
		
		public boolean hasNoArgConstructor;
		public boolean isFinalNoArgConstructor;
		public boolean allParamsSerializable;
		public List<ConstructorDeclaration> offendingConstructors = new ArrayList<ConstructorDeclaration>();
	}
	
	// all the constructor parameters must implement Serializable interface
	private boolean paramsSerializable(Collection<ParameterDeclaration> params) {
		for( ParameterDeclaration param:params ){
			// trust me! I know it's a DeclaredType! :P
			if(param.getType() instanceof DeclaredType) {
				DeclaredType paramType = (DeclaredType)param.getType();
				if(!implementsSerializable(paramType)) {
					return false;
				}
			}
		}
		return true;
	}


	// check if the given type implements Serializable
	private boolean implementsSerializable(DeclaredType paramType) {
		
		boolean isSerializable = false;
		
		// now, verify its base class
		TypeDeclaration paramTypeDecl = paramType.getDeclaration();
		if( paramTypeDecl == null ){
			_compilerOutput.printError( _containingClass.getPosition() , paramType.toString() + " type cannot be found.");
			// no class definition => we cannot discuss about serialization
			return true;
		}
		
		if( paramTypeDecl instanceof ClassDeclaration ) {
			ClassDeclaration paramClass = (ClassDeclaration)paramTypeDecl;
			// if we have a superclass...
			if( paramClass.getSuperclass() != null )
				isSerializable = isSerializable | implementsSerializable( paramClass.getSuperclass() );
		}
		
		System.out.println("Verifying if " + paramType.toString() + " is Serializable");
		for( InterfaceType implementedInterface : paramTypeDecl.getSuperinterfaces() ){
			
			System.out.println("testing interface:" + implementedInterface);
			InterfaceDeclaration implementedInterfaceType = implementedInterface.getDeclaration();
			if( implementedInterfaceType == null ){
				_compilerOutput.printError( paramTypeDecl.getPosition() , implementedInterface.toString() + " type cannot be found.");
				// if I found undefined interfaces, then maybe those interfaces implement Serializable!
				return true;
			}
			
			isSerializable = isSerializable |
				// the interface is actually Serializable
				Serializable.class.getName().equals(
					implementedInterfaceType.getQualifiedName()
					) |
				// verify if one of the superclasses implements Serializable
				implementsSerializable(implementedInterface) ;
		}

		return isSerializable;
		
	}


	protected void reportError( Declaration declaration , String msg ) {
		SourcePosition classPos = declaration.getPosition();
		_compilerOutput.printError( classPos , "[ERROR]" + ERROR_PREFIX + msg + ERROR_SUFFIX);
	}
	
	protected void reportWarning( Declaration declaration, String msg) {
		SourcePosition classPos = declaration.getPosition();
		_compilerOutput.printWarning( classPos , "[WARNING]" + ERROR_PREFIX + msg + ERROR_SUFFIX);
	}
}
