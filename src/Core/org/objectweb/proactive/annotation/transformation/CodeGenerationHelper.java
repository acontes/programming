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
package org.objectweb.proactive.annotation.transformation;

import org.objectweb.proactive.api.PAActiveObject;

import recoder.ParserException;
import recoder.ProgramFactory;
import recoder.java.Identifier;
import recoder.java.NonTerminalProgramElement;
import recoder.java.Statement;
import recoder.java.StatementBlock;
import recoder.java.declaration.ParameterDeclaration;
import recoder.java.reference.PackageReference;
import recoder.java.reference.TypeReference;
import recoder.java.statement.Branch;
import recoder.java.statement.Try;
import recoder.list.generic.ASTArrayList;
import recoder.list.generic.ASTList;
import recoder.service.ChangeHistory;

/**
 * This class contains helper methods to create generic language constructs, that 
 * are not specific to a particular annotation transformation
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.00
 */
public class CodeGenerationHelper {

	private final ProgramFactory _codeGen;
	private final ChangeHistory _changes;
	
	public CodeGenerationHelper( ProgramFactory codeGen,
			 ChangeHistory hist) {
		_codeGen = codeGen;
		_changes = hist;
	}
	
	// create a statement block that contains
	public  StatementBlock generateSingleBlockStatement(Statement statement) {
		ASTList<Statement> statementList = new ASTArrayList<Statement>();
		statementList.add(statement);
		return _codeGen.createStatementBlock(statementList);
	}
	
	// surround the given statement with Try
	public Try surroundWithTry( Statement surroundedElement ) {
		Try enclosingTryBlock;
		NonTerminalProgramElement oldParent = surroundedElement.getASTParent(); 
		if( oldParent instanceof Try ) {
			// statement block already inside try block
			enclosingTryBlock = (Try)oldParent; 
		}
		else {
			// create a new try block
			StatementBlock enclosingBlock = generateSingleBlockStatement(surroundedElement);
			enclosingTryBlock = _codeGen.createTry(enclosingBlock); 
			// replace the statement block with the try block
			oldParent.replaceChild(surroundedElement, enclosingTryBlock);
			_changes.replaced(oldParent, enclosingTryBlock);
		}
		
		return enclosingTryBlock;
	}
	
	public static final String EXCEPTION_VAR_NAME = "e";
	public Try surroundWithTryCatch(Statement surroundedElement,
				Class<? extends Exception>[] exceptionsList
			) 
	{
		Try enclosingTryBlock = surroundWithTry(surroundedElement);
		ASTList<Branch> branches = new ASTArrayList<Branch>();
		for (Class<? extends Exception> exceptionClazz : exceptionsList) {
			Branch catchBranch = _codeGen.createCatch( 
					generateCatchHeader(exceptionClazz , EXCEPTION_VAR_NAME), 
					generateCatchBody() );
			branches.add(catchBranch);
		}
		
		enclosingTryBlock.setBranchList(branches);
		return enclosingTryBlock;
	}
	
	private StatementBlock generateCatchBody() {
		try {
			return new StatementBlock(_codeGen.parseStatements("System.out.println(\"Error\");"));
		} catch (ParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private ParameterDeclaration generateCatchHeader(
			Class<? extends Exception> exceptionClazz,
			String exceptionVarName) {
		return _codeGen.createParameterDeclaration( createTypeReference(exceptionClazz) , 
				_codeGen.createIdentifier(exceptionVarName));
	}
	
	public TypeReference createTypeReference(Class clazz) {
		return _codeGen.createTypeReference(
				createPackageReference(clazz.getPackage().getName()), 
				_codeGen.createIdentifier(clazz.getSimpleName()));
	}

	// create a package reference out of a package name
	public PackageReference createPackageReference(String packageName) {

		PackageReference ret = null;
		String[] packageComponents = packageName.split("\\.");
		for (String identifier : packageComponents) {
			Identifier newId = _codeGen.createIdentifier(identifier); 
			if(ret == null ) {
				ret = _codeGen.createPackageReference(newId);
			}
			else{
				ret = _codeGen.createPackageReference(ret, newId);
			}
		}
		
		return ret;
	}
	
}
