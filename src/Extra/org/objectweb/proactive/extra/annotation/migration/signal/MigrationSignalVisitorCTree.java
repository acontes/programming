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
package org.objectweb.proactive.extra.annotation.migration.signal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.objectweb.proactive.api.PAMobileAgent;
import org.objectweb.proactive.extra.annotation.ErrorMessages;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;

import com.sun.source.tree.BlockTree;
import com.sun.source.tree.CatchTree;
import com.sun.source.tree.ClassTree;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.tree.DoWhileLoopTree;
import com.sun.source.tree.EnhancedForLoopTree;
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
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.Tree.Kind;
import com.sun.source.util.TreePath;
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
public class MigrationSignalVisitorCTree extends TreePathScanner<Void,Trees> {
	
	// error messages
	protected String ERROR_PREFIX_STATIC = " method is annotated using the " 
		+ MigrationSignal.class.getSimpleName() + " annotation.\n";
	protected final String ERROR_SUFFIX = "Please refer to the ProActive manual for further help on creating Active Objects.\n";
	protected String ERROR_PREFIX;

	// where we should signal the errors
	private final Messager _compilerOutput;
	
	public MigrationSignalVisitorCTree(Messager messager) {
		_compilerOutput = messager;
	}

	// marks the position of the method declaration inside the source code
	// also a marker for the moment of finding a method declaration
	private Element _methodPosition = null;
	private ClassTree _containingClass;
	private CompilationUnitTree _containingCompilationUnit;
	
	private Map<BlockTree,Boolean> visitedBlocks = new HashMap<BlockTree, Boolean>();
	@Override
	public Void visitMethod(MethodTree methodNode, Trees trees) {
		
		ERROR_PREFIX = methodNode.getName() + ERROR_PREFIX_STATIC; 
		_methodPosition = trees.getElement(getCurrentPath());
		_containingCompilationUnit = getCurrentPath().getCompilationUnit();
		
		Element clazzElement = trees.getElement(getCurrentPath().getParentPath());
		if ( !((clazzElement instanceof TypeElement) && (clazzElement.getKind().isClass()) ) ) {
			// actually, this will be caught by javac. but nevertheless, we can also have a say...
			_compilerOutput.printMessage(Diagnostic.Kind.ERROR	, 
					"Found a method declaration outside a class declaration. Interesting!" , 
						_methodPosition );
			return super.visitMethod(methodNode, trees);
		}
		
		_containingClass = (ClassTree)trees.getTree(clazzElement);
		
		// the method must be within a class that is tagged with the ActiveObject annotation
		if ( clazzElement.getAnnotation(ActiveObject.class) == null ) {
			reportError(_methodPosition, ErrorMessages.NOT_IN_ACTIVE_OBJECT_ERROR_MESSAGE);
			return super.visitMethod(methodNode, trees);
		}
		
		// the method must be public
//		if( !testModifiers(methodNode.getModifiers()) ){
//			reportWarning( _methodPosition, ErrorMessages.NOT_PUBLIC_MIGRATION_SIGNAL_ERROR_MESSAGE);
//		}
		
		// we go by the default visit pattern, and do the checking when visiting enclosing blocks
		visitedBlocks.clear();
		Void ret = super.visitMethod(methodNode, trees);
		_methodPosition = null;
		
		trees.getPath(_containingCompilationUnit, methodNode);
		
		return ret;
		
	}
	
	public boolean testModifiers(ModifiersTree modifiersNode) {
		boolean foundPublic = false;
		
		for( Modifier methodModifier : modifiersNode.getFlags() ) {
			if (methodModifier.equals(Modifier.PUBLIC)) {
				foundPublic = true;
				break;
			}
		}
		
		return foundPublic;
	}

	/*
	 * In every block, check whether any migrateTo call is the last one
	 */
	
	class BlockVisitInfo {
		public boolean hasMigrateTo;
		public boolean migrateIsLastStatement;
		public BlockVisitInfo() {
			hasMigrateTo = false;
			migrateIsLastStatement = false;
		}
		
		public BlockVisitInfo(boolean hasMT , boolean isLast) {
			hasMigrateTo = hasMT;
			migrateIsLastStatement = isLast;
		}
	}
	
	private Map<BlockTree,BlockVisitInfo> _visitedBlocks = new HashMap<BlockTree, BlockVisitInfo>();
	@Override
	public Void visitBlock(BlockTree blockNode, Trees trees) {
		
		// visit the descendants first
		Void ret = super.visitBlock( blockNode , trees);; 
		
		if( _methodPosition == null ) {
			// not in a method
			return ret;
		}
		
		StatementTree migrateToStatement = null;
		boolean lastStatementInSubBlocks = true;
		boolean hasInSubBlocks = false;
		
		List<? extends StatementTree> statements = blockNode.getStatements(); 
		System.out.println("Visiting block with statements:" + statements.size());
		for( StatementTree statement : statements ) {

			// is a migrateTo statement?
			if(isMigrationCall(statement,trees))
				if(migrateToStatement == null)
					migrateToStatement = statement;
			
			// it is a statement that contains sub-blocks?
			List<BlockTree> underlyingBlocks = statementContainsBlock(statement); 
			if( !underlyingBlocks.isEmpty() ) 
				for (BlockTree underlyingBlock : underlyingBlocks) 					
					if(_visitedBlocks.containsKey(underlyingBlock)) {
						BlockVisitInfo bvi = _visitedBlocks.get(underlyingBlock);
						System.out.println("The subblock: hasInSB:" + bvi.hasMigrateTo + " isLast:" + bvi.migrateIsLastStatement);
						hasInSubBlocks = hasInSubBlocks | bvi.hasMigrateTo;
						lastStatementInSubBlocks = lastStatementInSubBlocks & bvi.migrateIsLastStatement;
					}
		}
		
		// case 1 - migrateTo not in this block
		if( migrateToStatement == null ) {
			if(!hasInSubBlocks) {
				reportError( _methodPosition, ErrorMessages.MIGRATE_TO_NOT_FOUND_ERROR_MESSAGE);
				_visitedBlocks.put(blockNode, new BlockVisitInfo(false,false));
				return ret;
			}
			if(!lastStatementInSubBlocks) {
				reportError( _methodPosition, ErrorMessages.MIGRATE_TO_NOT_FINAL_STATEMENT_ERROR_MESSAGE);
				_visitedBlocks.put(blockNode, new BlockVisitInfo(true,false));
			} 
			else {
				_visitedBlocks.put(blockNode, new BlockVisitInfo(true,true));
			}
			return ret;
		}
		
		// case 2 - migrateTo call in this block
		// must not also be in sub-blocks
		if(hasInSubBlocks) {
			_visitedBlocks.put(blockNode, new BlockVisitInfo(true,false));
			return ret;
		}
		int migrateToPos = statements.indexOf(migrateToStatement);
		int statementsNo = statements.size();
		
		// programmers count starting from 0
		if (statementsNo - 1 == migrateToPos ) {
			// perfekt!
			_visitedBlocks.put(blockNode, new BlockVisitInfo(true,true));
			return ret;
		}
		
		if( statementsNo - 1 == migrateToPos + 1 ) {
			// get info on statements
			StatementTree lastStatement = statements.get( statementsNo -1);

			if(!checkReturnStatement(lastStatement)) {
				reportError( _methodPosition ,	ErrorMessages.MIGRATE_TO_NOT_FINAL_STATEMENT_ERROR_MESSAGE);
				_visitedBlocks.put(blockNode, new BlockVisitInfo(true,false));
			}
		}
		else {
			// definitely not the last statement
			reportError( _methodPosition , ErrorMessages.MIGRATE_TO_NOT_FINAL_STATEMENT_ERROR_MESSAGE);
			_visitedBlocks.put(blockNode, new BlockVisitInfo(true,false));
		}
		
		_visitedBlocks.put(blockNode, new BlockVisitInfo(true,true));
		return ret;

	}
	
	private List<BlockTree> statementContainsBlock(StatementTree statement) {
		
		List<BlockTree> blocks = new ArrayList<BlockTree>();
		Kind statementKind = statement.getKind();
		if(statementKind.equals(Kind.BLOCK)) {
			blocks.add((BlockTree)statement);
		}
		else if(statementKind.equals(Kind.TRY)){
			TryTree tryTree = (TryTree)statement;
			if(tryTree.getFinallyBlock() != null ) {
				// in finally migrateTo should be the last
				blocks.add(tryTree.getFinallyBlock());
				return blocks;
			}
			if(tryTree.getBlock()!=null)
				blocks.add(tryTree.getBlock());
			for (CatchTree catchTree : tryTree.getCatches()) {
				blocks.add(catchTree.getBlock());
			}
			
		}
		// TODO
		return blocks;
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
		if( !(lastStatement.getKind().equals(Kind.RETURN)) ) {
			return false;
		}
		
		ReturnTree returnStatement = (ReturnTree)lastStatement;
		
		ExpressionTree returnExpression = returnStatement.getExpression();
		
		return !((returnExpression.getKind().equals(Kind.METHOD_INVOCATION)) || 
				 (returnExpression.getKind().equals(Kind.NEW_ARRAY)) || 
				 (returnExpression.getKind().equals(Kind.NEW_CLASS)) );
	}
	
	/** Testing whether a statement is either a migrateTo call, or a call to a 
	 * migration signal - ie a method already annotated with @MigrationSignal
	 * @param statement
	 * @return
	 */
	private boolean isMigrationCall(StatementTree statement,Trees trees) {
		
		if( !(statement.getKind().equals(Kind.EXPRESSION_STATEMENT)) )
			return false;
		
		ExpressionStatementTree lastStatementExpr = (ExpressionStatementTree)statement;
		
		// the method call is an expression ...
		ExpressionTree lastExpression = lastStatementExpr.getExpression();
		// ... of type MethodInvocationTree ...
		if(!lastExpression.getKind().equals(Kind.METHOD_INVOCATION)) {
			return false;
		}
		
		MethodInvocationTree methodCall = (MethodInvocationTree)lastExpression;
		
		return isMigrationSignalCall(methodCall,trees)||isMigrateToCall(methodCall);
	}

	private boolean isMigrationSignalCall(MethodInvocationTree methodCall,Trees trees) {
		ExpressionTree methodSelectionExpression = methodCall.getMethodSelect();
		// case 1 - identifier
		if ( methodSelectionExpression.getKind().equals(Kind.IDENTIFIER) ) {
			String methodName = ((IdentifierTree)methodSelectionExpression).getName().toString();
			return classContainsMigrationSignal(methodName,trees);
		}
		return false;
	}

	/**
	 * Verify if the containing class has a method with a given name 
	 * and that is annotated with the @MigrationSignal annotation
	 * @param methodName - the name of the searched method
	 * @param trees - helper class
	 * @return true if the containing class has a @MIgrationSignal with the given name
	 */
	private boolean classContainsMigrationSignal(String methodName,Trees trees) {
		for( Tree classMember : _containingClass.getMembers()) {
			if(classMember.getKind().equals(Kind.METHOD)){
				MethodTree method = (MethodTree)classMember;
				if( methodName.equals(method.getName().toString())) {
					Element methodElem = trees.getElement(trees.getPath(_containingCompilationUnit, method));
					if(methodElem.getAnnotation(MigrationSignal.class)!=null)
						return true;
				}
			}
		}
		return false;
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
	private boolean isMigrateToCall(MethodInvocationTree methodCall) {

		// .. and the selection expression must be ...
		ExpressionTree methodSelectionExpression = methodCall.getMethodSelect();
		// ... either an identifier(can be a static import for example) ...
		if ( methodSelectionExpression.getKind().equals(Kind.IDENTIFIER) ) {
			String methodName = ((IdentifierTree)methodSelectionExpression).getName().toString();
			return methodName.equals(MIGRATE_TO);
		}
		// ... or a member selection expression(most common) ...
		else if( methodSelectionExpression.getKind().equals(Kind.MEMBER_SELECT)) {
			MemberSelectTree memberSelectionExpression = (MemberSelectTree)methodSelectionExpression;
			String methodName = memberSelectionExpression.getIdentifier().toString();
			ExpressionTree typeNameExpression = memberSelectionExpression.getExpression();
			// ... which can only be of the form ClassName.migrateTo
			// ClassName can be ...
			String clazzName = null;
			// ... either unqualified name, ie PAMobileAgent ...
			if (typeNameExpression.getKind().equals(Kind.IDENTIFIER)) {
				clazzName = ((IdentifierTree)typeNameExpression).getName().toString();
			}
			// ... or qualified name, ie ${package.name}.PAMobileAgent.
			else if( typeNameExpression.getKind().equals(Kind.MEMBER_SELECT)) {
				clazzName = ((MemberSelectTree)typeNameExpression).getIdentifier().toString();
			}
			else return false;
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
		_compilerOutput.printMessage(Diagnostic.Kind.WARNING,  
				ERROR_PREFIX + errorMsg + ERROR_SUFFIX , 
					position );
	}

	
}
