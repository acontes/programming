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
package org.objectweb.proactive.extra.annotation.transformation;

import java.util.List;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.util.log.Loggers;

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
import recoder.java.statement.Catch;
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
	
	protected static final Logger _logger = Logger.getLogger(Loggers.ANNOTATIONS); 
	
	public CodeGenerationHelper( ProgramFactory codeGen,
			 ChangeHistory hist) {
		_codeGen = codeGen;
		_changes = hist;
	}
	
	// create a statement block that contains
	// a single statement
	public  StatementBlock generateBlockSingleStatement(Statement statement) {
		ASTList<Statement> statementList = new ASTArrayList<Statement>();
		statementList.add(statement);
		StatementBlock block = _codeGen.createStatementBlock(statementList);
		// notify the change
		_changes.attached(statement);
		
		return block;
	}
	
	// create a statement block that contains
	// the given list of statements
	public StatementBlock generateBlockMultipleStatements(ASTList<Statement> statementList) {
		StatementBlock block = _codeGen.createStatementBlock(statementList);
		// notify the changes
		for (Statement statement : statementList) {
			_changes.attached(statement);
		}
		
		return block;
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
			StatementBlock enclosingBlock = generateBlockSingleStatement(surroundedElement);
			enclosingTryBlock = _codeGen.createTry(enclosingBlock); 
			// replace the statement block with the try block
			oldParent.replaceChild(surroundedElement, enclosingTryBlock);
			_changes.replaced(oldParent, enclosingTryBlock);
		}
		
		return enclosingTryBlock;
	}
	
	// generate a try-catch block with the given exceptions caught
	// the same code is used in all the catch blocks
	public Try surroundWithTryCatch(Statement surroundedElement,
				Class<? extends Exception>[] exceptionsList,
				String exceptionVarName,
				StatementBlock catchBody
			) 
	{
		Try enclosingTryBlock = surroundWithTry(surroundedElement);
		
		attachCatchBranchesSameBody( enclosingTryBlock, 
						exceptionsList, exceptionVarName, catchBody);
		return enclosingTryBlock;
	}
	
	public Try surroundWithTryCatch(ASTList<Statement> statementList,
			Class<? extends Exception>[] exceptionsList,
			String exceptionVarName,
			StatementBlock catchBody
		) 
{
	Try enclosingTryBlock = surroundWithTry(statementList);
	
	attachCatchBranchesSameBody( enclosingTryBlock, 
					exceptionsList, exceptionVarName, catchBody);
	return enclosingTryBlock;
}
	
	public Try surroundWithTry(ASTList<Statement> statementList) {
		StatementBlock block = generateBlockMultipleStatements(statementList); 
		Try tryBlock = _codeGen.createTry(block);
		_changes.attached(block);
		
		return tryBlock;
	}

	// generate a list of branches that treat the given exceptions with the same body of code
	public void attachCatchBranchesSameBody(
			Try enclosingTry,
			Class<? extends Exception>[] exceptionsList,
			String exceptionVarName,
			StatementBlock catchBody) 
	{
		// get parent branches list
		ASTList<Branch> branches = enclosingTry.getBranchList();
		if( branches == null ) {
			branches = new ASTArrayList<Branch>();
			enclosingTry.setBranchList(branches);
		}
		
		// add our branches
		for (Class<? extends Exception> exceptionClazz : exceptionsList) {
			Catch catchBranch = _codeGen.createCatch( 
					generateCatchHeader(exceptionClazz , exceptionVarName), 
					catchBody );
			branches.add(catchBranch);
			catchBranch.setParent(enclosingTry);
			_changes.attached(catchBranch);
		}
	}
	
	// the exceptions are eaten miam-miam; but they are logged to the specified logger
	// it logger is null, the error is logged to System.err
	public StatementBlock generateCatchBody(String logger, // logger to report exceptions
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
						( aoVarName != null ? aoVarName + " = null;\n" : "" );
			}
			else {
				// log error to the specified logger
				statementsText = logger + ".error(\"" + errorMessage + "\" , " + exceptionVarName + ");\n" + 
				( aoVarName != null ? aoVarName + " = null;\n" : "" );
			}

			return generateBlockMultipleStatements(_codeGen.parseStatements(statementsText));
		} catch (ParserException e) {
			_logger.error("Could not generate statements for the folowing code text:" + statementsText , e);
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
	
	// add the statement newStatement into the statement block enclosingBlock
	// after the element afterWhich
	public void addStatementAfter(StatementBlock enclosingBlock,
			Statement afterWhich,
			Statement newStatement) {
		
		int position = enclosingBlock.getBody().indexOf(afterWhich) + 1;
		addStatementAtPosition(enclosingBlock, position , newStatement);
		
	}
	
	// add the statement newStatement into the statement block enclosingBlock
	// after the element afterWhich
	public void addStatementBefore(StatementBlock enclosingBlock,
			Statement beforeWhich,
			Statement newStatement) {
		
		int position = enclosingBlock.getBody().indexOf(beforeWhich);
		addStatementAtPosition(enclosingBlock, position , newStatement);
		
	}
	
	public void addStatementAtPosition(StatementBlock enclosingBlock,
			int position,
			Statement newStatement) {
		List<Statement> statements = enclosingBlock.getBody(); 
		// add the new statement
		statements.add( position , newStatement);
		//notify the change
		newStatement.setStatementContainer(enclosingBlock);
		_changes.attached(newStatement);
	}

	public void addStatementListBefore(StatementBlock enclosingBlock,
			Statement beforeWhich,
			ASTList<Statement> newStatements) {
		
		int position = enclosingBlock.getBody().indexOf(beforeWhich);
		addStatementListAtPosition(enclosingBlock, position, newStatements);
		
	}
	
	public void addStatementListAtPosition(StatementBlock enclosingBlock,
			int position,
			ASTList<Statement> newStatements) {
		
		List<Statement> statements = enclosingBlock.getBody(); 
		// add the new statement
		statements.addAll( position , newStatements);
		//notify the change
		for(Statement newStatement : newStatements ) {
			newStatement.setStatementContainer(enclosingBlock);
			_changes.attached(newStatement);
		}
		
	}

	// generates a shutdown hook. The code that will be executed
	// inside the run method of the thread is given as a parameter
	// in text form
	public Statement createShutdownHook(String blockText) {
		// java.lang.Runtime.getRuntime().addShutdownHook( 
		//		new Thread() {
		//			public void run() {
		//				StatementBlock
		//			}
		// 		});
			String sdhText = Runtime.class.getName() + ".getRuntime().addShutdownHook(\n" +
				"new " + Thread.class.getName() +  "() {\n" +
						"public void run() {\n" +
							blockText +
						"}\n" +
				"});\n";
		try{
			return _codeGen.parseStatements(sdhText).get(0);
		} catch (ParserException e) {
			_logger.error("Could not generate statements for the folowing code text:" + sdhText , e);
			return null;
		}
	}

	
}
