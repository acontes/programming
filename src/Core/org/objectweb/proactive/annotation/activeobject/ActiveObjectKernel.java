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

import java.util.List;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.annotation.transformation.CodeGenerationException;
import org.objectweb.proactive.annotation.transformation.TransformationKernel;
import org.objectweb.proactive.annotation.virtualnode.VirtualNode;
import org.objectweb.proactive.annotation.virtualnode.VirtualNodeAnnotationElements;
import org.objectweb.proactive.annotation.virtualnode.VirtualNodeKernel;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;

import recoder.ParserException;
import recoder.ServiceConfiguration;
import recoder.java.Declaration;
import recoder.java.Expression;
import recoder.java.Identifier;
import recoder.java.ProgramElement;
import recoder.java.Statement;
import recoder.java.StatementBlock;
import recoder.java.declaration.AnnotationElementValuePair;
import recoder.java.declaration.AnnotationUseSpecification;
import recoder.java.declaration.LocalVariableDeclaration;
import recoder.java.declaration.VariableSpecification;
import recoder.java.reference.MethodReference;
import recoder.java.reference.ReferencePrefix;
import recoder.java.reference.TypeReference;
import recoder.list.generic.ASTArrayList;
import recoder.list.generic.ASTList;
import recoder.util.StringUtils;

/**
 * Code generation for the ActiveObject annotation
 * This will generate for a variable declared like this:
<input>
 	@ActiveObject
	SomeClass karamba = new SomeClass();
</input>
	the output will be:
<output>
	SomeClass karamba = new SomeClass();
	karamba = (SomeClass)org.objectweb.proactive.api.PAActiveObject.turnActive(karamba);
</output>
	
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.00
 */
public class ActiveObjectKernel extends TransformationKernel {
	
	// the API method
	private static final String PA_API_METHOD_NAME = "turnActive";
	
	public ActiveObjectKernel(ServiceConfiguration sc) {
		super(sc);
	}

	@Override
	public void generateAnnotationReplacement(Declaration parentDeclaration,
			AnnotationUseSpecification annotation) throws CodeGenerationException {
		
		if( ! (parentDeclaration instanceof LocalVariableDeclaration) ){
			throw new CodeGenerationException("Annotation can only be applied on local variable declarations");
		}
		
		LocalVariableDeclaration annotatedDeclaration = (LocalVariableDeclaration)parentDeclaration;
		
		ProgramElement parent = annotatedDeclaration.getASTParent();
		if( ! (parent instanceof StatementBlock) ) {
			throw new CodeGenerationException(" Found a variable declaration outside a statement block");
		}
		
		StatementBlock enclosingBlock = (StatementBlock)parent;
		
		TypeReference variableType = annotatedDeclaration.getTypeReference();
		
		List<VariableSpecification> variables = annotatedDeclaration.getVariables();
		if ( variables.size() != 1 ) {
			throw new CodeGenerationException(" ActiveObject annotation cannot be used on lists of variable declarations ");
		}
		
		VariableSpecification varSpec = variables.get(0);
		Identifier varName = varSpec.getIdentifier();
		
		// verify whether the initializer of the object is really a constructor call
		if( !testInitializer(varSpec.getInitializer())) {
			throw new CodeGenerationException("The annotated variable declaration does not have an initializer that is a contructor call");
		}
		
		ActiveObjectAnnotationElements attribs; 
		if( annotation.getElementValuePairs() == null )
			attribs = new ActiveObjectAnnotationElements(); 
		else
			attribs	= new ActiveObjectAnnotationElements(annotation);
		
		// if we have a virtual node name
		String vnodeVarName = null;
		if( attribs._virtualNode != null ) {
			// search for the virtual node name in a previous annotation
			vnodeVarName = searchVirtualNode( attribs._virtualNode , annotatedDeclaration);
		}
		
		Statement turnActiveMethodCall = 
			createTurnActive( variableType, varName.getText(), vnodeVarName );
		
		_cgHelper.addStatementAfter( enclosingBlock , annotatedDeclaration , turnActiveMethodCall );
		
		Class<? extends Exception>[] exceptionsList = new Class[]{
			ActiveObjectCreationException.class,
			NodeException.class
		};
		
		StatementBlock catchBlock = _cgHelper.generateCatchBody(
				attribs._loggerName, CATCH_ERROR_MESSAGE + varName.getText() , EXCEPTION_VAR_NAME, varName.getText());
		
		if( catchBlock == null ) {
			throw new CodeGenerationException("Could not generate the catch block to catch the exceptions fromthe call " + PA_API_METHOD_NAME);
		}
		
		_cgHelper.surroundWithTryCatch( turnActiveMethodCall, exceptionsList, EXCEPTION_VAR_NAME, catchBlock );
		
	}
	
	private String searchVirtualNode( String vnName ,
			LocalVariableDeclaration annotatedDeclaration) throws CodeGenerationException 
	{
		
		AnnotationUseSpecification vnAnnotation = getVirtualNodeAnnotation(annotatedDeclaration);
		
		VirtualNodeAnnotationElements attribs = new VirtualNodeAnnotationElements(vnAnnotation);
		
		if( !attribs.getVirtualNodeName().equals(vnName) ) {
			throw new CodeGenerationException( "The virtual node name " + attribs.getVirtualNodeName() + " in annotation " +
					VirtualNode.class.getSimpleName() + " conflicts with the name " + vnName + 
					" specified in annotation " + ActiveObject.class.getSimpleName());
		}
		
		return attribs.getVnVarName();
		
	}

	private AnnotationUseSpecification getVirtualNodeAnnotation(
			LocalVariableDeclaration annotatedDeclaration) throws CodeGenerationException 
	{
		
		for (AnnotationUseSpecification annotation : annotatedDeclaration.getAnnotations()) {
			if (annotation.getTypeReference().getName().equals(VirtualNode.class.getSimpleName())) 
				return annotation;
		}
		
		throw new CodeGenerationException("Element " + ActiveObjectAnnotationElements.VIRTUAL_NODE_ELEMENT + 
				" specified for the annotation " + ActiveObject.class.getSimpleName() + 
				" but the virtual node was not defined using the " + VirtualNode.class.getSimpleName() + " annotation");
	}

	private static final String CATCH_ERROR_MESSAGE = "Error while creating the active object ";
	private static final String EXCEPTION_VAR_NAME = "e";
	
	private boolean testInitializer(Expression initializer) {
		
		if(initializer == null)
			return false;
		
		return initializer instanceof recoder.java.expression.operator.New;
	}

	private Statement createTurnActive( TypeReference variableType, 
			String varName, String vnodeVarName) 
	{
		
		String turnActiveText = PAActiveObject.class.getName() + "." + PA_API_METHOD_NAME + 
			"( " +  varName + 
			( (vnodeVarName!=null) ? " , " +   vnodeVarName + ".getNode()" : "" ) 
			+ ");\n" ;
		
		try{
			return _codeGen.parseStatements(turnActiveText).get(0); // we know it is a single statement
		} catch (ParserException e) {
			_logger.error("Could not generate statements for the folowing code text:" + turnActiveText , e);
			return null;
		}
	
		/*
		// create PAActiveObject, prefixed with the package name
		ReferencePrefix apiClassName = _cgHelper.createTypeReference(PAActiveObject.class);
		
		// create turnActive identifier
		Identifier methodName = _codeGen.createIdentifier(PA_API_METHOD_NAME);
		//create args list
		ASTList<Expression> args = new ASTArrayList<Expression>();
		args.add(
				_codeGen.createVariableReference(
						_codeGen.createIdentifier(varName))
			); // arg #1 - variable reference, name = varName
		// arg #2 - Node 

		// create the method call
		MethodReference turnActiveMethodCall = _codeGen.createMethodReference(apiClassName, methodName, args);
		// create the assignment statement
		Statement assignment = _codeGen.createCopyAssignment(
				_codeGen.createVariableReference(
						_codeGen.createIdentifier(varName)),
				_codeGen.createTypeCast( turnActiveMethodCall , variableType)
				);
		
		return assignment;
		*/
		
	}

}
