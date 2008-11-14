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
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
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
import com.sun.source.tree.ForLoopTree;
import com.sun.source.tree.IdentifierTree;
import com.sun.source.tree.IfTree;
import com.sun.source.tree.MemberSelectTree;
import com.sun.source.tree.MethodInvocationTree;
import com.sun.source.tree.MethodTree;
import com.sun.source.tree.ModifiersTree;
import com.sun.source.tree.ReturnTree;
import com.sun.source.tree.StatementTree;
import com.sun.source.tree.Tree;
import com.sun.source.tree.TryTree;
import com.sun.source.tree.WhileLoopTree;
import com.sun.source.tree.Tree.Kind;
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

		BlockVisitInfo bvi = _visitedBlocks.get(methodNode.getBody());
		if(!bvi.hasMigrateTo){
			reportError( _methodPosition, ErrorMessages.MIGRATE_TO_NOT_FOUND_ERROR_MESSAGE);
		}
		else if(!bvi.migrationUsedCorrectly) {
			reportError( _methodPosition, ErrorMessages.MIGRATE_TO_NOT_FINAL_STATEMENT_ERROR_MESSAGE);
		}

		_methodPosition = null;
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
		public boolean migrationUsedCorrectly;
		public BlockVisitInfo() {
			hasMigrateTo = false;
			migrationUsedCorrectly = false;
		}
		
		public BlockVisitInfo(boolean hasMT , boolean isLast) {
			hasMigrateTo = hasMT;
			migrationUsedCorrectly = isLast;
		}
		@Override
		public boolean equals(Object obj) {
			BlockVisitInfo bvi = (BlockVisitInfo)obj;
			return hasMigrateTo == bvi.hasMigrateTo && migrationUsedCorrectly == bvi.migrationUsedCorrectly;
		}
		
		@Override
		public String toString() {
			return "Has a migrateTo call:" + hasMigrateTo + ";is used correctly:" + migrationUsedCorrectly;
		}
	}
	
	private Map<BlockTree,BlockVisitInfo> _visitedBlocks = new HashMap<BlockTree, BlockVisitInfo>();
	@Override
	public Void visitBlock(BlockTree blockNode, Trees trees) {
		
		// visit the descendants first
		Void ret = super.visitBlock( blockNode , trees); 
		
		if( _methodPosition == null ) {
			// not in a method
			return ret;
		}
		
		StatementTree migrateToStatement = null;
		boolean migrationUsedCorrectlyInSubBlocks = true;
		boolean hasInSubBlocks = false;
		
		List<? extends StatementTree> statements = blockNode.getStatements(); 
		for( StatementTree statement : statements ) {

			// is a migrateTo statement?
			if(isMigrationCall(statement,trees))
				if(migrateToStatement == null)
					migrateToStatement = statement;
			
			List<UnderlyingStatementsInfo> underlyingBlocks = statementContainsBlock(statement);
			
			// is it a loop statement?
			// the sweet C-style hakz! :P
//			StatementTree underlyingLoop;
//			if((underlyingLoop = isLoop(statement)) != null) {
//				// if it has the migrateTo call, then definitely a logical error of the control flow
//				if(underlyingLoop.getKind().equals(Kind.BLOCK)) {
//					markMisusedMigrateTo((BlockTree)underlyingLoop);
//					underlyingBlocks.add((BlockTree)underlyingLoop);
//				}
//				else {
//					// simple statement as a loop body
//					if(isMigrationCall(underlyingLoop, trees)){
//						// loop logical error
//						hasInSubBlocks = true;
//						migrationUsedCorrectlyInSubBlocks = false;
//						continue;
//					}
//				}
//			}
			
			// is it a statement that contains sub-blocks/sub-statements?
			if( !underlyingBlocks.isEmpty() ){
				for (UnderlyingStatementsInfo underInfo : underlyingBlocks) {
					BlockVisitInfo bvi;
					if(underInfo.underlyingKind.equals(Kind.BLOCK)) {
						// we have underlying block
						bvi = _visitedBlocks.get(underInfo.underlyingBlock);
					}
					else {
						// we have underlying statement. it is always the last one in the sub-context of the super-construct
						bvi = new BlockVisitInfo(isMigrationCall(underInfo.underlyingStatement, trees),true);
					}
					hasInSubBlocks = hasInSubBlocks | bvi.hasMigrateTo;
					migrationUsedCorrectlyInSubBlocks = migrationUsedCorrectlyInSubBlocks & bvi.migrationUsedCorrectly;
				}
			}
		}
		
		// case 1 - migrateTo not in this block
		if( migrateToStatement == null ) {
			if(!hasInSubBlocks) {
				_visitedBlocks.put(blockNode, new BlockVisitInfo(false,false));
				return ret;
			}
			if(!migrationUsedCorrectlyInSubBlocks) {
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
				_visitedBlocks.put(blockNode, new BlockVisitInfo(true,false));
				return ret;
			}
		}
		else {
			// definitely not the last statement
			_visitedBlocks.put(blockNode, new BlockVisitInfo(true,false));
			return ret;
		}
		
		_visitedBlocks.put(blockNode, new BlockVisitInfo(true,true));
		return ret;

	}
	
	class UnderlyingStatementsInfo {
		public Kind underlyingKind;
		BlockTree underlyingBlock = null;
		StatementTree underlyingStatement = null;
		
		public UnderlyingStatementsInfo() {
			this.underlyingBlock = null;
			this.underlyingKind = Kind.BLOCK;
			this.underlyingStatement = null;
		}
		
		public UnderlyingStatementsInfo(BlockTree underlyingBlock) {
			super();
			this.underlyingBlock = underlyingBlock;
			this.underlyingKind = Kind.BLOCK;
		}
		
		public UnderlyingStatementsInfo(StatementTree underlyingStatement) {
			super();
			this.underlyingStatement = underlyingStatement;
			this.underlyingKind = Kind.EXPRESSION_STATEMENT;
		}

		
		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			
			if (underlyingKind.equals(Kind.BLOCK)) {
				ret.append("block:" + underlyingBlock);
			}
			else {
				ret.append("statement:" + underlyingStatement);
			}
			
			return ret.toString();
		}
		
	}
	
	private List<UnderlyingStatementsInfo> statementContainsBlock(StatementTree statement) {
		
		List<UnderlyingStatementsInfo> statementInfo = new ArrayList<UnderlyingStatementsInfo>();
		Kind statementKind = statement.getKind();
		
		if(statementKind.equals(Kind.BLOCK)) {
			statementInfo.add(new UnderlyingStatementsInfo((BlockTree)statement));
		}
		else if(statementKind.equals(Kind.TRY)){
			TryTree tryTree = (TryTree)statement;
			if(tryTree.getFinallyBlock() != null ) {
				// in finally migrateTo should be the last
				statementInfo.add(new UnderlyingStatementsInfo(tryTree.getFinallyBlock()));
				return statementInfo;
			}
			if(tryTree.getBlock()!=null)
				statementInfo.add(new UnderlyingStatementsInfo(tryTree.getBlock()));
			for (CatchTree catchTree : tryTree.getCatches()) {
				statementInfo.add(new UnderlyingStatementsInfo(catchTree.getBlock()));
			}
		}
		else if(statementKind.equals(Kind.IF)){
			IfTree fi = (IfTree)statement;
			StatementTree thenStatement = fi.getThenStatement();
			if(thenStatement.getKind().equals(Kind.BLOCK)) {
				statementInfo.add(new UnderlyingStatementsInfo((BlockTree)fi.getThenStatement()));
			}
			else {
				statementInfo.add(new UnderlyingStatementsInfo(fi.getThenStatement()));
			}
			StatementTree esle = fi.getElseStatement(); 
			if( esle != null )
				if(esle.getKind().equals(Kind.BLOCK))
					statementInfo.add(new UnderlyingStatementsInfo((BlockTree)esle));
				else
					statementInfo.add(new UnderlyingStatementsInfo(esle));
		}

		// TODO all kinds of statements with enclosing blocks!
		return statementInfo;
	}

	/**
	 * Verify if a statement is a loop type statement, 
	 * and if it is, return the enclosing block - if it exists...
	 * The problem with loops is that if you call PAMobile.migrateTo() inside
	 * the loop block, then it means that you want to call it multiple times.
	 * This is a logical error - migrateTo should only be called once.
	 * @param statement
	 * @return null - if the statement is not a loop, or it is a loop but without enclosing block
	 * 			non-null - the enclosing block 
	 */
	private final StatementTree isLoop(StatementTree statement) {
		Kind statementKind = statement.getKind();
		System.out.println("Have a statement of kind:" + statementKind);
		if(statementKind.equals(Kind.DO_WHILE_LOOP)){
			return ((DoWhileLoopTree)statement).getStatement();
		}
		else if(statementKind.equals(Kind.FOR_LOOP)){
			return ((ForLoopTree)statement).getStatement();
		}
		else if(statementKind.equals(Kind.ENHANCED_FOR_LOOP)){
			return ((EnhancedForLoopTree)statement).getStatement();
		}
		else if(statementKind.equals(Kind.WHILE_LOOP)){
			return ((WhileLoopTree)statement).getStatement();
		}
		
		// else, not a loop
		return null;
	}
	
	/**
	 * The problem with loops is that if you call PAMobile.migrateTo() inside
	 * the loop block, then it means that you want to call it multiple times.
	 * This is a logical error - migrateTo should only be called once.
	 */
	private void markMisusedMigrateTo(BlockTree block) {
		BlockVisitInfo bvi = _visitedBlocks.get(block);
		// if it contains migrateTo call
		if(bvi.hasMigrateTo) {
			// mark error
			bvi.migrationUsedCorrectly = false;
			_visitedBlocks.put(block, bvi);
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
