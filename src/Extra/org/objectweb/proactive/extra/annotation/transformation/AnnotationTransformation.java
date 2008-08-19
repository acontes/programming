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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.objectweb.proactive.core.util.log.Loggers;

import recoder.CrossReferenceServiceConfiguration;
import recoder.java.CompilationUnit;
import recoder.java.PrettyPrinter;
import recoder.kit.ProblemReport;
import recoder.kit.Transformation;

/**
 * This is a generic class for annotation tranformation.
 * Basically, it calls the AnnotationTransformationVisitor 
 * for all the compilation units from the Service Configuration
 * 
 * The Service Configuration should already be configured so that 
 * getCompilationUnits() should return all the compilation units that need
 * to be processed.
 * 
 * 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.00
 */
public class AnnotationTransformation extends Transformation {

	// the class of the annotation which mark the to-be-processed elements
	private final Class _annotationClass;
	// the list of compilation units that will be processed
	private List<CompilationUnit> _compilationUnits;
	// the transformation kernel, which will be used by the visitor to generate code
	private final TransformationKernel _kernel;
	
	private static final Logger _logger = Logger.getLogger(Loggers.ANNOTATIONS); 
	
	// error reporting hak
	private boolean _visitorError;
	private String _errorMsg;
	public void notifyVisitorError(String errorMsg) { 
		_visitorError = true;
		_errorMsg = errorMsg;
	}

	public Class getAnnotation() { return _annotationClass; }
	public TransformationKernel getKernel() { return _kernel; }
	public List<CompilationUnit> getCompilationUnits() { return _compilationUnits; }
	
	public AnnotationTransformation(CrossReferenceServiceConfiguration serviceConfig,
			Class annotationClass,
			TransformationKernel kernel) {
		
		super(serviceConfig);
		// argument null checks
		if( serviceConfig == null ) {
			throw new IllegalArgumentException("Invalid service configurator. Maybe you didn't initialize recoder correctly?");
		}
		if( annotationClass == null ){
			throw new IllegalArgumentException("Invalid annotation class!");
		}
		if( kernel == null ) {
			throw new IllegalArgumentException("Invalid transformation kernel!");
		}
		
		_annotationClass = annotationClass;
		_kernel = kernel;
		// load all the compilation units from path
		_compilationUnits = serviceConfig.getSourceFileRepository().getCompilationUnits();
		// there should be some compilation units...
		if( _compilationUnits == null || _compilationUnits.isEmpty() ){
			throw new IllegalArgumentException("No compilation units to process. Maybe you didn't initialize recoder correctly?"); 
		}
	}
	
	// this counstructor should be used if we want to process only part of the 
	// compilation units from the input path. 
	// the compilation units are given as parameter, they are initialized elsewhere
	public AnnotationTransformation(CrossReferenceServiceConfiguration serviceConfig,
			List<CompilationUnit> compilationUnits,
			Class annotationClass,
			TransformationKernel kernel) {
		
		this(serviceConfig , annotationClass , kernel);
		
		// only hold the desired compilation units
		_compilationUnits = compilationUnits;
	}

	@Override
	public ProblemReport execute() {

		// an output writer that "eats up" all the output
		Writer blackOut
		   = new BufferedWriter(new OutputStreamWriter(
				   new BlackHoleOutputStream()));
		PrettyPrinter annotationVisitor = new AnnotationTransformationVisitor( blackOut, this );
		
		// pass through all compilation units
		for(CompilationUnit compilationUnit : _compilationUnits ){
			// visit the compilation unit using the provided visitor
			_visitorError = false;
			compilationUnit.accept(annotationVisitor);
			if (_visitorError) {
				return setProblemReport(new AnnotationProblem(_errorMsg)); 
			}
		}
		
		// close the writer
		try {
			blackOut.close();
		} catch (IOException e) {
			// it will never come to this
		}
		
		return setProblemReport(EQUIVALENCE);
	}
	
}

//an OutputStream that discards all output it receives
final class BlackHoleOutputStream extends OutputStream {
	@Override
	public void write(int b) throws IOException {
		// black hole - do nothing!
		return;
	}
}