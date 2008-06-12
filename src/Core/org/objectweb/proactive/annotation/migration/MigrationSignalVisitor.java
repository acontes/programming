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
package org.objectweb.proactive.annotation.migration;

import java.util.List;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.objectweb.proactive.annotation.ErrorMessages;
import org.objectweb.proactive.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.api.PAMobileAgent;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.ExpressionStatementTree;
import com.sun.source.tree.ExpressionTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.NewArrayTree;
import com.sun.source.tree.NewClassTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.util.TreePathScanner;
import com.sun.source.util.Trees;

/**
 * <p>This class implements a visitor for the ProActiveProcessor, according to the Pluggable Annotation Processing API(jsr269) specification</p>
 * <p>It implements the checks for the 
 * {@link org.objectweb.proactive.annotation.migration.MigratableSignal} 
 * annotation. See the annotation javadoc for the description of the checks performed.
 * </p>
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public class MigrationSignalVisitor extends TreePathScanner<Void,Trees> {
	
	// error messages
	private static final String ERROR_PREFIX_STATIC = " method is annotated using the " 
		+ MigrationSignal.class.getSimpleName() + " annotation.\n";
	private static final String ERROR_SUFFIX = "Please refer to the ProActive manual for further help on creating Active Objects.\n";
	private String ERROR_PREFIX;

	// where we should signal the errors
	private Messager _compilerOutput;
	
	public MigrationSignalVisitor(Messager messager) {
		_compilerOutput = messager;
	}
	
	// this will hold all the necessary information on the position of the MigrateTo call
	// the visitor will magically populate these fields, as it parses the AST
	private StatementTree _migrateToMethodCallNode;
	private int _expressionStatementsNumber;
	private int _migrateToMethodCallIndex;
	private boolean _isNewMethodCall;
	
	// final means try to inline it
	private final void resetVisitorStateBlock() {
		_expressionStatementsNumber = _migrateToMethodCallIndex = 0;
	}
	
	private final void resetVisitorStateMethod() {
		_migrateToMethodCallNode = null;
	}
	
	private final void resetVisitorStateReturn() {
		_isNewMethodCall = false;
	}

	@Override
	public Void visitMethod(MethodTree methodNode, Trees trees) {
		
		ERROR_PREFIX = methodNode.getName() + ERROR_PREFIX_STATIC;
		
		Element clazzElement = trees.getElement(getCurrentPath().getParentPath());
		if ( !((clazzElement instanceof TypeElement) && (clazzElement.getKind().isClass()) ) ) {
			// actually, this will be caught by javac. but nevertheless, we can also have a say...
			_compilerOutput.printMessage(Diagnostic.Kind.ERROR	, 
					"Found a method declaration outside a class declaration. Interesting!" , 
						trees.getElement(getCurrentPath()) );
			return super.visitMethod(methodNode, trees);
		}
		
		// the method must be within a class that is tagged with the ActiveObject annotation
		if ( clazzElement.getAnnotation(ActiveObject.class) == null ) {
			reportError(trees.getElement(getCurrentPath()), 
					ErrorMessages.NOT_IN_ACTIVE_OBJECT_ERROR_MESSAGE);
			return super.visitMethod(methodNode, trees);
		}
		
		// we go by the default visit pattern, and do the checking when visiting enclosing blocks
		resetVisitorStateMethod();
		Void ret = super.visitMethod(methodNode, trees);
		
		// let's see if we found that method call
		if (_migrateToMethodCallNode == null) {
			// not found
			reportError(trees.getElement(getCurrentPath()),
					ErrorMessages.MIGRATE_TO_NOT_FOUND_ERROR_MESSAGE);
			return ret;
		}
		
		return ret;
		
	}
	
	@Override
	public Void visitModifiers(ModifiersTree modifiersNode, Trees trees) {
		
		boolean foundPublic = false;
		
		for( Modifier methodModifier : modifiersNode.getFlags() ) {
			if (methodModifier.equals(Modifier.PUBLIC)) {
				foundPublic = true;
				break;
			}
		}
		
		if (!foundPublic) {
			reportWarning(trees.getElement(getCurrentPath()),
					ErrorMessages.NOT_PUBLIC_MIGRATION_SIGNAL_ERROR_MESSAGE);
		}
		
		return super.visitModifiers(modifiersNode, trees);
	}

	/*
	 * In every block, check whether any migrateTo call is the last one
	 */
	@Override
	public Void visitBlock(BlockTree blockNode, Trees trees) {

		// let the visitor search for the MethodInvocation
		resetVisitorStateBlock();
		Void ret = super.visitBlock( blockNode , trees);

		if( _migrateToMethodCallNode != null ) {
			// we found it alright; now we should check to see 
			// if it is the last statement in the enclosing block
			checkPlacement( blockNode, trees);
		}
		
		return ret;
	}
	
	/*
	 * Checks whether the (found!) migrateTo method call is really the last call in 
	 * the body of the method. 
	 */
	private void checkPlacement(BlockTree blockBody, Trees trees) {
		
		if (_expressionStatementsNumber == _migrateToMethodCallIndex) {
			// perfekt!
			return;
		}
		
		if( _expressionStatementsNumber == _migrateToMethodCallIndex + 1 ) {
			// get info on statements
			List<? extends StatementTree> methodStatements = blockBody.getStatements();
			int methodsNo = methodStatements.size();
			StatementTree lastStatement = methodStatements.get(methodsNo-1);

			if(!checkReturnStatement(lastStatement)) {
				reportWarning(trees.getElement(getCurrentPath()),
						ErrorMessages.MIGRATE_TO_NOT_FINAL_STATEMENT_ERROR_MESSAGE);
			}
		}
		else {
			// definitely not the last statement
			reportWarning(trees.getElement(getCurrentPath()),
					ErrorMessages.MIGRATE_TO_NOT_FINAL_STATEMENT_ERROR_MESSAGE);
		}
		
	}
	
	/*
	 * this method check whether the given statement is a return statement, and the
	 * expression of the return statement is not another method call , 
	 * ie it does not generate another stack frame. This is done using the visitor pattern 
	 * actually - a precondition is that before calling checkReturnStatement, the enclosing block 
	 * has already been visited by the current visitor.
	 * @return: true, if it is a "simple" return statement.
	 */
	private boolean checkReturnStatement(StatementTree lastStatement) {
		resetVisitorStateReturn();
		if( !(lastStatement instanceof ReturnTree) ) {
			return false;
		}
		// the visitor pattern to the rescue!
		return !_isNewMethodCall;
	}
	

	@Override
	public Void visitReturn(ReturnTree retunrNode, Trees trees) {
		_isNewMethodCall = false;
		return super.visitReturn(retunrNode, trees);
	}
	
	@Override
	public Void visitMethodInvocation(MethodInvocationTree arg0, Trees arg1) {
		_isNewMethodCall = true;
		return super.visitMethodInvocation(arg0, arg1);
	}
	
	@Override
	public Void visitNewArray(NewArrayTree arg0, Trees arg1) {
		_isNewMethodCall = true;
		return super.visitNewArray(arg0, arg1);
	}
	
	@Override
	public Void visitNewClass(NewClassTree arg0, Trees arg1) {
		_isNewMethodCall = true;
		return super.visitNewClass(arg0, arg1);
	}
	
	@Override
	public Void visitExpressionStatement(ExpressionStatementTree statement, Trees trees) {
		_expressionStatementsNumber++;
		if( isMigrateToCall(statement) ){
			_migrateToMethodCallNode = statement;
			_migrateToMethodCallIndex = _expressionStatementsNumber;
		}
		
		return super.visitExpressionStatement(statement, trees);
	}
	
	/*
	 * This method tests whether the Java statement represented
	 * by the given StatementTree parameter, is a method call statement,
	 * which represents a call to the static method ${PAMobileAgent}.${MIGRATE_TO}.
	 * see JLS sect. 15.12 for the format of a Java method call. The following two forms 
	 * of method call statements are considered as a potential match for the call ${PAMobileAgent}.${MIGRATE_TO}:
	 * <ul>
	 * 	<li> MethodName ( ArgumentList_opt )</li>
	 * 	<li> TypeName . Identifier ( ArgumentList_opt ) </li>
	 * </ul>
	 * where MethodName/Identifier must be ${MIGRATE_TO}, and TypeName must be 
	 * the identifier [${package.name}]${PAMobileAgent}
	 * The ${package.name} and ${PAMobileAgent} names can be changed by refactoring without modifying 
	 * 	the source code of this visitor, but the ${MIGRATE_TO} method name is hardcoded 
	 * - see the declaration below - and it must be changed if the API changes. This is because I don't know yet
	 * how to do it otherwise. 
	 */
	private static final String MIGRATE_TO = "migrateTo"; // i dunno how to do it elseway
	private boolean isMigrateToCall(ExpressionStatementTree lastStatement) {

		// the method call is an expression ...
		ExpressionTree lastExpression = lastStatement.getExpression();
		// ... of type MethodInvocationTree ...
		if (!(lastExpression instanceof MethodInvocationTree)) {
			return false;
		}
		// .. and the selection expression must be ...
		ExpressionTree methodSelectionExpression = ((MethodInvocationTree)lastExpression).getMethodSelect();
		// ... either an identifier(can be a static import for example) ...
		if ( methodSelectionExpression instanceof IdentifierTree ) {
			String methodName = ((IdentifierTree)methodSelectionExpression).getName().toString();
			return methodName.equals(MIGRATE_TO);
		}
		// ... or a member selection expression(most common) ...
		else if( methodSelectionExpression instanceof MemberSelectTree ) {
			MemberSelectTree memberSelectionExpression = (MemberSelectTree)methodSelectionExpression;
			String methodName = memberSelectionExpression.getIdentifier().toString();
			ExpressionTree typeNameExpression = memberSelectionExpression.getExpression();
			// ... which can only be of the form ClassName.migrateTo
			// ClassName can be ...
			String clazzName = null;
			// ... either unqualified name, ie PAMobileAgent ...
			if (typeNameExpression instanceof IdentifierTree) {
				clazzName = ((IdentifierTree)typeNameExpression).getName().toString();
			}
			// ... or qualified name, ie ${package.name}.PAMobileAgent.
			else if( typeNameExpression instanceof MemberSelectTree ) {
				clazzName = ((MemberSelectTree)typeNameExpression).getIdentifier().toString();
			}
			else return false;
			System.out.println("Method name is:"+methodName);
			System.out.println("clazzName is:"+clazzName);
			System.out.println("compared to:"+PAMobileAgent.class.getSimpleName());
			return methodName.equals(MIGRATE_TO) && clazzName.equals(PAMobileAgent.class.getSimpleName());
		}
		else {
			// is not a migrateTo call!
			return false;
		}
		 
	}

	// error reporting methods
	private void reportError( Element position, String errorMsg ) {
		_compilerOutput.printMessage(Diagnostic.Kind.ERROR	, 
				ERROR_PREFIX + errorMsg + ERROR_SUFFIX , 
					position );
	}
	
	private void reportWarning( Element position, String errorMsg ) {
		_compilerOutput.printMessage(Diagnostic.Kind.WARNING	, 
				ERROR_PREFIX + errorMsg + ERROR_SUFFIX , 
					position );
	}

	
}
