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
package org.objectweb.proactive.extra.annotation.virtualnode;

import java.io.File;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.NodeException;
import org.objectweb.proactive.extensions.gcmdeployment.PAGCMDeployment;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.transformation.CodeGenerationException;
import org.objectweb.proactive.extra.annotation.transformation.TransformationKernel;
import org.objectweb.proactive.gcmdeployment.GCMApplication;
import org.objectweb.proactive.gcmdeployment.GCMVirtualNode;

import recoder.ParserException;
import recoder.ServiceConfiguration;
import recoder.java.Declaration;
import recoder.java.ProgramElement;
import recoder.java.Statement;
import recoder.java.StatementBlock;
import recoder.java.declaration.AnnotationElementValuePair;
import recoder.java.declaration.AnnotationUseSpecification;
import recoder.java.declaration.LocalVariableDeclaration;
import recoder.java.statement.Try;
import recoder.list.generic.ASTList;
import recoder.util.StringUtils;

/**
 * Code generation for the VirtualNode annotation
 * see {@link org.objectweb.proactive.extra.annotation.virtualnode.VirtualNode} for a description of the usage of the annotation
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.00
 */
public class VirtualNodeKernel extends TransformationKernel {

	public VirtualNodeKernel(ServiceConfiguration sc) {
		super(sc);
	}
	
	private static final String CATCH_ERROR_MESSAGE = "Error while trying to load the virtual node ";
	private static final String EXCEPTION_VAR_NAME = "e";

	@Override
	public void generateAnnotationReplacement(Declaration parentDeclaration,
			AnnotationUseSpecification annotation) throws CodeGenerationException {
		
		if( ! (parentDeclaration instanceof LocalVariableDeclaration) ){
			throw new CodeGenerationException("Annotation can only be applied on local variable declarations");
		}
		
		LocalVariableDeclaration annotatedDeclaration = (LocalVariableDeclaration)parentDeclaration;
		
		// check if the ActiveObject annotation also exists
		if( !hasActiveObjectAnnotation(annotatedDeclaration) ){
			throw new CodeGenerationException("The annotation " + 
					org.objectweb.proactive.extra.annotation.virtualnode.VirtualNode.class.getSimpleName() + 
					" can only be applied on elements already annotated with " + ActiveObject.class.getSimpleName());
		}
		
		ProgramElement parent = annotatedDeclaration.getASTParent();
		if( ! (parent instanceof StatementBlock) ) {
			throw new CodeGenerationException(" Found a variable declaration outside a statement block");
		}
		
		StatementBlock enclosingBlock = (StatementBlock)parent;
		
		VirtualNodeAnnotationElements attributes = new VirtualNodeAnnotationElements(annotation);
		
		StatementBlock catchBody = _cgHelper.generateCatchBody(
				attributes._logger , CATCH_ERROR_MESSAGE + attributes._name , EXCEPTION_VAR_NAME, null);
		
		if( catchBody == null ) {
			throw new CodeGenerationException("Could not generate the catch block to catch the exceptions from the virtual node creation");
		}

		// variable declarations
		ASTList<Statement> statementsOutsideTry = createVirtualNodeDeclarations(attributes);
		// shutdown hook
		Statement shutdownHook = createShutdownHook(attributes);
		// node creation
		ASTList<Statement> statementsInsideTry = createVirtualNodeCreationStatements(attributes);
		statementsInsideTry.add(shutdownHook);
		
		Class<? extends Exception>[] exceptionsList = new Class[]{
				ProActiveException.class,
				//NodeException.class
			};
		
		Try vnodeCreation = _cgHelper.surroundWithTryCatch(
				statementsInsideTry, exceptionsList, EXCEPTION_VAR_NAME, catchBody);
		
		statementsOutsideTry.add(vnodeCreation);
		_cgHelper.addStatementListBefore( enclosingBlock, annotatedDeclaration , statementsOutsideTry );
		
	}
	
	private boolean hasActiveObjectAnnotation(LocalVariableDeclaration annotatedDeclaration) {
		for (AnnotationUseSpecification annotation : annotatedDeclaration.getAnnotations()) {
			if (annotation.getTypeReference().getName().equals(ActiveObject.class.getSimpleName())) 
				return true;
		}
		
		return false;
	}

	private Statement createShutdownHook(VirtualNodeAnnotationElements attributes) {
		String shutdownText = "";
		if(attributes._descriptorType.equals("old")){
			shutdownText = "try {\n" +
				attributes._padVarName + ".killall(false);\n"
				+ "} catch(" +  ProActiveException.class.getName() + " " + EXCEPTION_VAR_NAME + "){\n" 
				// nothing!
				+ "}\n";
		}
		else if(attributes._descriptorType.equals("gcm")){
			shutdownText = attributes._padVarName + ".kill();\n";
		}
		return _cgHelper.createShutdownHook(shutdownText);
	}

	private ASTList<Statement> createVirtualNodeDeclarations(
			VirtualNodeAnnotationElements attributes) {
		
		if(attributes._descriptorType.equals("old"))
			return createVirtualNodeDeclarationsOld(attributes);
		
		if(attributes._descriptorType.equals("gcm"))
			return createVirtualNodeDeclarationsGcm(attributes);
		
		return null;
	}



	private ASTList<Statement> createVirtualNodeCreationStatements(
			VirtualNodeAnnotationElements attributes) {
		
		if(attributes._descriptorType.equals("old"))
			return createVirtualNodeCreationStatementsOld(attributes);
		
		if(attributes._descriptorType.equals("gcm"))
			return createVirtualNodeCreationStatementsGcm(attributes);
		
		return null;
	}

	private ASTList<Statement> createVirtualNodeCreationStatementsOld(
			VirtualNodeAnnotationElements attributes) {
	
		String vNodeCreationText =
			// ProActiveDescriptor pad = PADeployment.getProactiveDescriptor(descriptor);
			attributes._padVarName + " = " + PADeployment.class.getName() + 
				".getProactiveDescriptor(\" " +  attributes._descriptorFile +  " \");\n" +
			// VirtualNode vn = pad.getVirtualNode("workers");
			attributes._vnVarName + " = " + attributes._padVarName +
				".getVirtualNode(\"" + attributes._name + "\");\n" +
			// vn.activate();
			attributes._vnVarName + ".activate();\n"
			;
		
		try {
			return _codeGen.parseStatements(vNodeCreationText);
		} catch (ParserException e) {
			// we should never arrive here!
			_logger.error("Cannot generate AST for the following code text:" + vNodeCreationText, e);
			return null;
		}
		
	}

	private ASTList<Statement> createVirtualNodeDeclarationsOld(
			VirtualNodeAnnotationElements attributes) {

		String vNodeDeclarationText = "final " +  ProActiveDescriptor.class.getName() + " " + attributes._padVarName + ";\n" + 
				VirtualNode.class.getName() + " " + attributes._vnVarName + "= null" + ";\n";
		try {
			return _codeGen.parseStatements(vNodeDeclarationText);
		} catch (ParserException e) {
			// we should never arrive here!
			_logger.error("Cannot generate AST for the following code text:" + vNodeDeclarationText, e);
			return null;
		}
	}
	
	private ASTList<Statement> createVirtualNodeCreationStatementsGcm(
			VirtualNodeAnnotationElements attributes) {
	
		String vNodeCreationText =
			// GCMApplication pad = PAGCMDeployment.loadApplicationDescriptor(new File(descriptor));
			attributes._padVarName + " = " + PAGCMDeployment.class.getName() + 
				".loadApplicationDescriptor(new java.io.File(\" " +  attributes._descriptorFile +  " \"));\n" +
			// pad.startDeployment();
			attributes._padVarName + ".startDeployment();\n"	+
			// GCMVirtualNode vn = pad.getVirtualNode("name");
			attributes._vnVarName + " = " + attributes._padVarName +
				".getVirtualNode(\"" + attributes._name + "\");\n";
		
		try {
			return _codeGen.parseStatements(vNodeCreationText);
		} catch (ParserException e) {
			// we should never arrive here!
			_logger.error("Cannot generate AST for the following code text:" + vNodeCreationText, e);
			return null;
		}
		
	}
	
	private ASTList<Statement> createVirtualNodeDeclarationsGcm(
			VirtualNodeAnnotationElements attributes) {
		String vNodeDeclarationText = "final " + GCMApplication.class.getName() + " " + attributes._padVarName + ";\n" + 
				GCMVirtualNode.class.getName() + " " + attributes._vnVarName + "= null" + ";\n";
		try {
			return _codeGen.parseStatements(vNodeDeclarationText);
		} catch (ParserException e) {
			// we should never arrive here!
			_logger.error("Cannot generate AST for the following code text:" + vNodeDeclarationText, e);
			return null;
		}
	}

}
