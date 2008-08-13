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

import org.jboss.logging.Logger;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.annotation.transformation.CodeGenerationException;
import org.objectweb.proactive.annotation.transformation.TransformationKernel;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.core.util.log.Loggers;

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
		
		Statement turnActiveMethodCall = 
			createTurnActive( variableType , varName.getText() );
		
		addTurnActive( enclosingBlock , annotatedDeclaration , turnActiveMethodCall );
		
		Class<? extends Exception>[] exceptionsList = new Class[]{
			ActiveObjectCreationException.class,
			NodeException.class
		};
		
		AnnotationAttributes attribs; 
		if( annotation.getElementValuePairs() == null )
			attribs = new AnnotationAttributes(); 
		else
			attribs	= new AnnotationAttributes(annotation);
		
		StatementBlock catchBlock = generateCatchBodyForActiveObject(
				attribs._loggerName, ERROR_MESSAGE, EXCEPTION_VAR_NAME, varName.getText());
		
		if( catchBlock == null ) {
			throw new CodeGenerationException("Could not generate the catch block to catch the exceptions fromthe call " + PA_API_METHOD_NAME);
		}
		
		_cgHelper.surroundWithTryCatch( turnActiveMethodCall, exceptionsList, EXCEPTION_VAR_NAME, catchBlock );
		
	}
	
	private static final String ERROR_MESSAGE = "Error";
	public static final String EXCEPTION_VAR_NAME = "e";
	// the exceptions are eaten miam-miam; but they are logged to the specified logger
	// it logger is null, the error is logged to System.err
	private StatementBlock generateCatchBodyForActiveObject(String logger, // logger to report exceptions
			String errorMessage, // error message
			String exceptionVarName, // the name of the exception variable in the catch()
			String aoVarName)    // the name of the active object variable
	{
		
		String statementsText=null;
		
		try {
			if(logger == null) {
				// log error to System.err
				statementsText =	"System.err.println(\"" + errorMessage + "\");\n" +
						exceptionVarName + ".printStackTrace();\n" +
						aoVarName + " = null;\n";
			}
			else {
				// log error to the specified logger
				statementsText = logger + ".error(\"" + errorMessage + "\" , " + exceptionVarName + ");\n" + 
								aoVarName + " = null;\n";
			}

			return new StatementBlock(_codeGen.parseStatements(statementsText));
		} catch (ParserException e) {
			_logger.error("Could not generate statements for the folowing code text:" + statementsText , e);
			return null;
		}
	}
	
	private boolean testInitializer(Expression initializer) {
		
		if(initializer == null)
			return false;
		
		return initializer instanceof recoder.java.expression.operator.New;
	}

	private void addTurnActive(StatementBlock enclosingBlock,
			Statement afterWhich,
			Statement turnActiveMethodCall) {
		
		List<Statement> statements = enclosingBlock.getBody(); 
		int index = statements.indexOf(afterWhich);
		// add the new method call
		statements.add( index + 1, turnActiveMethodCall);
		//notify the change
		turnActiveMethodCall.setStatementContainer(enclosingBlock);
		_changes.attached(turnActiveMethodCall);
		
	}

	private Statement createTurnActive( TypeReference variableType, String varName) {
		
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
		// TODO args.add(_codeGen.createNullLiteral()); // arg #2 - for now, is null literal 

		// create the method call
		MethodReference turnActiveMethodCall = _codeGen.createMethodReference(apiClassName, methodName, args);
		// create the assignment statement
		Statement assignment = _codeGen.createCopyAssignment(
				_codeGen.createVariableReference(
						_codeGen.createIdentifier(varName)),
				_codeGen.createTypeCast( turnActiveMethodCall , variableType)
				);
		
		return assignment;
		
	}
	
	class AnnotationAttributes {
		
		String _loggerName;
		private static final String LOGGER_ATTR = "logger"; 
		
		public AnnotationAttributes() {
			// load default values
			_loggerName = null;
		}
		
		public AnnotationAttributes(AnnotationUseSpecification annotation) {
			// load default values
			this();
			// overwrite user-specified values
			for( AnnotationElementValuePair pair : annotation.getElementValuePairs()) {
				if(pair.getElementName().equals(LOGGER_ATTR)) {
					_loggerName = getLoggerName(pair.getValue());
				}
			}
		}

		private String getLoggerName(Object value) {
			if( value instanceof String == false )
				return null;
			String name = (String)value;
			// trim eventual commas
			if( name.startsWith("\"") )
				name = name.substring(1, name.length()-1);
			return name;
		}

		
	}

}
