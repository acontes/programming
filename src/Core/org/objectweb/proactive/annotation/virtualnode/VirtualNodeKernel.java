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
package org.objectweb.proactive.annotation.virtualnode;

import java.io.File;

import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.annotation.transformation.CodeGenerationException;
import org.objectweb.proactive.annotation.transformation.TransformationKernel;
import org.objectweb.proactive.api.PADeployment;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.node.NodeException;

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

/**
 * Code generation for the VirtualNode annotation
 * see {@link org.objectweb.proactive.annotation.virtualnode.VirtualNode} for a description of the usage of the annotation
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
		
		ProgramElement parent = annotatedDeclaration.getASTParent();
		if( ! (parent instanceof StatementBlock) ) {
			throw new CodeGenerationException(" Found a variable declaration outside a statement block");
		}
		
		StatementBlock enclosingBlock = (StatementBlock)parent;
		
		AnnotationElements attributes = new AnnotationElements(annotation);
		
		ASTList<Statement> virtualNodeCreationStatement = createVirtualNodeCreationStatements(attributes);		
		
		Class<? extends Exception>[] exceptionsList = new Class[]{
				ProActiveException.class,
				NodeException.class
			};
		
		StatementBlock catchBody = _cgHelper.generateCatchBody(
				"tataia", CATCH_ERROR_MESSAGE + attributes._name , EXCEPTION_VAR_NAME, "mamaia");
		
		if( catchBody == null ) {
			throw new CodeGenerationException("Could not generate the catch block to catch the exceptions from the virtual node creation");
		}
		
		Try vnodeCreation = _codeGen.createTry( 
				_codeGen.createStatementBlock(virtualNodeCreationStatement));
		
		_cgHelper.attachCatchBranchesSameBody( vnodeCreation, 
				exceptionsList, EXCEPTION_VAR_NAME, catchBody);
		
		_cgHelper.addStatementBefore(enclosingBlock, annotatedDeclaration, vnodeCreation);
		
	}
	
	private ASTList<Statement> createVirtualNodeCreationStatements(
			AnnotationElements attributes) {
	
		String padVarName = "pad" + attributes._id;
		String vnodeVarName = "vn" + attributes._id;
		String vNodeCreationText =
			// ProActiveDescriptor pad = PADeployment.getProactiveDescriptor(descriptor);
			ProActiveDescriptor.class.getName() + " " + padVarName + " = " + PADeployment.class.getName() + 
				".getProactiveDescriptor(\" " +  attributes._descriptorFile +  " \");\n" +
			// VirtualNode vn = pad.getVirtualNode("workers");
			VirtualNode.class.getName() + " " + vnodeVarName + " = " + padVarName +
				".getVirtualNode(\"" + attributes._name + "\");\n" +
			// vn.activate();
			vnodeVarName + ".activate();\n"
			;
		
		try {
			return _codeGen.parseStatements(vNodeCreationText);
		} catch (ParserException e) {
			// we should never arrive here!
			_logger.error("Cannot generate AST for the following code text:" + vNodeCreationText, e);
			return null;
		}
		
	}

	class AnnotationElements {
		String _name;
		String _descriptorType;
		String _descriptorFile;
		String _id;
		
		private static final String NAME_ELEMENT = "name";
		private static final String DESCR_TYPE_ELEMENT = "descriptorType";
		private static final String DESCR_FILE_ELEMENT = "descriptorFile";
		
		public AnnotationElements() {
			_name = null;
			_descriptorType = "gcm";
			_descriptorFile = null;
		}
		
		public AnnotationElements(AnnotationUseSpecification annotation) 
			throws CodeGenerationException 
		{
			// load default values
			this();
			if( annotation.getElementValuePairs() == null || annotation.getElementValuePairs().isEmpty() )
				throw new CodeGenerationException("The " + NAME_ELEMENT + " and " + DESCR_FILE_ELEMENT + " elements are mandatory for annotation " + VirtualNode.class.getSimpleName());
			// overwrite user-specified values
			for( AnnotationElementValuePair pair : annotation.getElementValuePairs()) {
				if(pair.getElementName().equals(NAME_ELEMENT)){
					_name = getStringValue(pair.getValue());
				}
				if(pair.getElementName().equals(DESCR_TYPE_ELEMENT)) {
					_descriptorType = getStringValue(pair.getValue());
				}
				if(pair.getElementName().equals(DESCR_FILE_ELEMENT)) {
					_descriptorFile = getStringValue(pair.getValue());
				}
			}
			
			if(_name == null)
				throw new CodeGenerationException("The " + NAME_ELEMENT + " element is mandatory for annotation " + VirtualNode.class.getSimpleName());
			if(_descriptorFile == null)
				throw new CodeGenerationException("The " + DESCR_FILE_ELEMENT + " element is mandatory for annotation " + VirtualNode.class.getSimpleName());
			
			// get only the file name without the extension
			File descr = new File(_descriptorFile);
			String fileName = descr.getName();
			fileName = fileName.substring(0, fileName.indexOf('.'));
			
			_id = _name + fileName + _descriptorType;
		}
		
		private String getStringValue(Object value) {
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
