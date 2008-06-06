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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

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
//cannot use ActiveObject.class.getName() the value must be a constant expression BLEAH!
@SupportedAnnotationTypes("org.objectweb.proactive.annotation.activeobject.ActiveObject") 
@SupportedOptions("enableTypeGenerationInEditor")
public class ProActiveProcessor extends AbstractProcessor {
	
	private boolean claimedMyAnnotations = false;
	
	@Override
	public boolean process(Set<? extends TypeElement> annotations,
			RoundEnvironment roundEnv) {
		
		if (annotations.isEmpty()) {
			// called with no annotations
			return true;
		}
		
		// if the prevoius round we processed our annotations, then return true
		if(claimedMyAnnotations)
			return true;
		else 
			claimedMyAnnotations = true;

		
		TypeElement proActiveAnotElement = annotations.iterator().next();
		// this hack is needed if cannot use the @SupportedAnnotation; 
		// just put * and check what is our annotation
		/*for (TypeElement typeElement : annotations) {
			if( typeElement.getQualifiedName().toString().equals("ActiveObject") ){
				proActiveAnotElement = typeElement;
				break;
			}
		}*/

		// initialisation stuff		
		Trees trees = Trees.instance(processingEnv);
		Messager messager = processingEnv.getMessager();
		ProActiveVisitor visitor = new ProActiveVisitor(messager);
		
		Set<? extends Element> annotatedElements = 
			roundEnv.getElementsAnnotatedWith(proActiveAnotElement);
		for( Element element : annotatedElements ) {
			// scan the nodes on the tree recursively
			if ( !(element instanceof TypeElement) ) {
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
		
		return false;
	}
	
}
