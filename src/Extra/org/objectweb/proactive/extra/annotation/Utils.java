package org.objectweb.proactive.extra.annotation;

import java.lang.annotation.ElementType;
import javax.lang.model.element.ElementKind;

import org.objectweb.proactive.core.ProActiveRuntimeException;

import com.sun.mirror.declaration.ClassDeclaration;
import com.sun.mirror.declaration.ConstructorDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.declaration.FieldDeclaration;
import com.sun.mirror.declaration.MethodDeclaration;

public class Utils {

	public static ElementType convertToElementType(ElementKind kind) {
		
		switch (kind) {
			case ANNOTATION_TYPE :
				return ElementType.ANNOTATION_TYPE;
			case CLASS : 
				return ElementType.TYPE;
			case CONSTRUCTOR : 
				return ElementType.CONSTRUCTOR;
			case FIELD : 
				return ElementType.FIELD;
			case INTERFACE : 
				return ElementType.TYPE;
			case LOCAL_VARIABLE : 
				return ElementType.LOCAL_VARIABLE;
			case METHOD : 
				return ElementType.METHOD;
			case PACKAGE : 
				return ElementType.PACKAGE;
			case PARAMETER : 
				return ElementType.PARAMETER;
			// no match for the following fields
			case INSTANCE_INIT : 
			case ENUM : 
			case ENUM_CONSTANT : 
			case EXCEPTION_PARAMETER : 
			case STATIC_INIT : 
			case TYPE_PARAMETER : 
			case OTHER : 
		}
		
		throw new ProActiveRuntimeException(
				"Cannot match from java.lang.annotation.ElementType." + 
				kind + 
				" to java.lang.annotation.ElementType");
	}

	// hack
	public static boolean applicableOnDeclaration(ElementType applicableType,
			Declaration typeDeclaration) {
		
		if( typeDeclaration instanceof ClassDeclaration )
			return applicableType.equals(ElementType.TYPE);
		if( typeDeclaration instanceof MethodDeclaration )
			return applicableType.equals(ElementType.METHOD);
		if( typeDeclaration instanceof FieldDeclaration)
			return applicableType.equals(ElementType.FIELD);
		if( typeDeclaration instanceof ConstructorDeclaration)
			return applicableType.equals(ElementType.CONSTRUCTOR);
		// TODO add others when needed
		
		return false;
	}
	
	

}