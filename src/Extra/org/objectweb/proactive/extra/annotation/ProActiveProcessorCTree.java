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
package org.objectweb.proactive.extra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObjectVisitorCTree;
import org.objectweb.proactive.extra.annotation.migration.MigrationSignalVisitorCTree;

import com.sun.source.util.TreePathScanner;
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
			"org.objectweb.proactive.extra.annotation.activeobject.ActiveObject",
			"org.objectweb.proactive.extra.annotation.migration.MigrationSignal"
		}
	) 
@SupportedOptions("enableTypeGenerationInEditor")
public class ProActiveProcessorCTree extends AbstractProcessor {

	Trees trees;
	Messager messager;
	HashMap<String, TreePathScanner<Void,Trees>> scanners = new HashMap<String, TreePathScanner<Void,Trees>>();
	
	
	// because of BLEAH, absurdities continue...
	public static final String ACTIVE_OBJECT_ANNOTATION = "org.objectweb.proactive.extra.annotation.activeobject.ActiveObject";
	public static final String MIGRATION_SIGNAL_ANNOTATION = "org.objectweb.proactive.extra.annotation.migration.MigrationSignal";
	
	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		trees = Trees.instance(processingEnv);
		messager = processingEnv.getMessager();
		
		scanners.put(ACTIVE_OBJECT_ANNOTATION, new ActiveObjectVisitorCTree(messager));
		scanners.put(MIGRATION_SIGNAL_ANNOTATION, new MigrationSignalVisitorCTree(messager));
	}
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		
		if (annotations.isEmpty()) {
			// called with no annotations
			return true;
		}
		
		for (TypeElement annotation : annotations) {
			
			TreePathScanner<Void,Trees> scanner = scanners.get(annotation.getQualifiedName().toString());
			if ( scanner == null ) {
				// annotation is not intended to be used for code checking
				continue;
			}
			
			// check whether the annotation is used in correct place and perform the verification
			// of target element using appropriate scanner
			Target target = annotation.getAnnotation(Target.class);
			Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);
			for ( Element element: annotatedElements ) {
				
				if ( target == null ) {
					// annotation can be used everywhere
				} else { 
					
					boolean usedInCorrectPlace = false;
					
					for (ElementType type: target.value()) {
						if ( Utils.convertToElementType(element.getKind()).equals(type) ) {
							usedInCorrectPlace = true;
						}
					}
					
					if ( !usedInCorrectPlace ) {
						messager.printMessage(Diagnostic.Kind.ERROR,
								"The @" + annotation.getSimpleName() + " annotation is declared to be used with " + target.toString());
						continue;
					}
				}
								
				scanner.scan( trees.getPath(element) , trees );
			}
		}
		
		return true;
	}
	
}
