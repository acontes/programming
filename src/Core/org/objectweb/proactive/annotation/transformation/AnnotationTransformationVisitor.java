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

import java.io.Writer;
import java.util.Properties;

import org.jboss.logging.Logger;
import org.objectweb.proactive.core.util.log.Loggers;

import recoder.java.Declaration;
import recoder.java.NonTerminalProgramElement;
import recoder.java.PrettyPrinter;
import recoder.java.declaration.AnnotationUseSpecification;

/**
 * Generic visitor for annotation processing
 * the PrettyPrinter is morphed into a "generic" visitor by
 * providing a black hole OutputWriter to it
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.00
 */
public class AnnotationTransformationVisitor extends PrettyPrinter {

	private AnnotationTransformation _transformation;
	
	private TransformationKernel _kernel;
	
	private static final Logger _logger = Logger.getLogger(Loggers.ANNOTATIONS); 
	
	public AnnotationTransformationVisitor( Writer out , 
			AnnotationTransformation at
			){
		this(out, at.getServiceConfiguration().getProjectSettings().getProperties());
		_transformation = at;
		_kernel = at.getKernel();
	}
	
	protected AnnotationTransformationVisitor(Writer out, Properties props) {
		super(out, props);
	}
	
	@Override
	public void visitAnnotationUse(AnnotationUseSpecification a) {
		
		try {
			
			// test if the annotation is what we are looking for 
			if( !_transformation.getAnnotation().getSimpleName().equals(a.getTypeReference().getName())) {
				// not of interest
				_logger.debug("The annotation:" + a.getTypeReference().getName() + "is not of interest.");
				return;
			}
			
			Declaration declaration = a.getParentDeclaration();
			_logger.debug("I have found the annotation:" + a.getTypeReference().getName() 
					+ " applied on the element:" + declaration.toSource() );


			// use the kernel to generate the code
			_kernel.generateAnnotationReplacement(declaration, a);

			// detach the annotation, it is no longer needed
			// detachAnnotation( a );

		}
		catch(CodeGenerationException e){
			_logger.error("An error occured while generating the code corresponding to the annotation:"
					+ _transformation.getAnnotation().getName() + ".Will notify the transformation which will report the error.");
			_transformation.notifyVisitorError(e.getMessage());
		}
		finally {
			super.visitAnnotationUse(a);
		}
	}

	// see Transformation.detach
	private void detachAnnotation(AnnotationUseSpecification annotation) {
		
		// detach the annotation
		NonTerminalProgramElement parent = annotation.getASTParent();
        int position;
        if (parent != null) {
            position = parent.getChildPositionCode(annotation);
            parent.replaceChild(annotation, null);
        } else {
            position = 0;
        }
        
        // notify the change history
        _kernel._changes.detached(annotation, position);
		
	}

}
