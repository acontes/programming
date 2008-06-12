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
package org.objectweb.proactive.annotation.jsr269;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.objectweb.proactive.annotation.migration.MigrationSignalVisitor;


import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

/**
 * This class implements a Processor for annotations, according to the 
 * Pluggable Annotation Processing API(jsr269) specification.
 *  
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
//cannot use ${Annotation}.class.getName() the value must be a constant expression BLEAH!
@SupportedAnnotationTypes(
		{
			"org.objectweb.proactive.annotation.activeobject.ActiveObject",
			"org.objectweb.proactive.annotation.migration.MigrationSignal"
		}
	) 
@SupportedOptions("enableTypeGenerationInEditor")
public class ProActiveProcessor extends AbstractProcessor {

	
	// because of BLEAH, absurdities continue...
	private static final String ACTIVE_OBJECT_ANNOTATION = "org.objectweb.proactive.annotation.activeobject.ActiveObject";
	private static final String MIGRATION_SIGNAL_ANNOTATION = "org.objectweb.proactive.annotation.migration.MigrationSignal"; 
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		
		if (annotations.isEmpty()) {
			// called with no annotations
			return true;
		}
		
		for (TypeElement annotationElement : annotations) {
			if ( annotationElement.getQualifiedName().toString().equals(ACTIVE_OBJECT_ANNOTATION) ) {
				processActiveObjectAnnotation(roundEnv, annotationElement);
			} else
			if ( annotationElement.getQualifiedName().toString().equals(MIGRATION_SIGNAL_ANNOTATION) ) {
				processMigrationSignalAnnotation(roundEnv, annotationElement);
			}
		}
		
		return true;
	}
	
	private void processMigrationSignalAnnotation(RoundEnvironment roundEnv,
			TypeElement migrartionSignalAnnotationElem) {
		
		// initialisation stuff		
		Trees trees = Trees.instance(processingEnv);
		Messager messager = processingEnv.getMessager();
		MigrationSignalVisitor visitor = new MigrationSignalVisitor(messager);
		
		Set<? extends Element> annotatedElements = 
			roundEnv.getElementsAnnotatedWith(migrartionSignalAnnotationElem);
		for( Element element : annotatedElements ) {
			
			if ( !((element instanceof ExecutableElement) && (element.getKind().equals(ElementKind.METHOD)) ) ) {
				messager.printMessage(Diagnostic.Kind.ERROR	, 
						"The @MigrationSignal annotation can only be used on method definitions" , 
							element );
				// carry on with the next annotated element
				continue;
			}
			
			ExecutableElement methodElement = (ExecutableElement)element;
			TreePath methodTree = trees.getPath(methodElement);
			
			// let's visit this tree!
			visitor.scan( methodTree , trees);
		}
		
	}

	private void processActiveObjectAnnotation(RoundEnvironment roundEnv,
			TypeElement proActiveAnotElement) {
		
		// initialisation stuff		
		Trees trees = Trees.instance(processingEnv);
		Messager messager = processingEnv.getMessager();
		ActiveObjectVisitor visitor = new ActiveObjectVisitor(messager);
		
		Set<? extends Element> annotatedElements = 
			roundEnv.getElementsAnnotatedWith(proActiveAnotElement);
		for( Element element : annotatedElements ) {
			
			if ( !((element instanceof TypeElement) && (element.getKind().isClass()) ) ) {
				messager.printMessage(Diagnostic.Kind.ERROR	, 
						"The @ActiveObject annotation can only be used on class definitions" , 
							element );
				// carry on with the next annotated element
				continue;
			}
			
			TypeElement clazzElement = (TypeElement)element;
			TreePath clazzTree = trees.getPath(clazzElement);
			
			// let's visit this tree!
			visitor.scan( clazzTree , trees);
		}
	}
	
}
