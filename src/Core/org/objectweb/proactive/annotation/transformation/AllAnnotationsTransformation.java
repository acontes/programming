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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.proactive.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.annotation.activeobject.ActiveObjectKernel;

import recoder.CrossReferenceServiceConfiguration;
import recoder.java.CompilationUnit;
import recoder.kit.Problem;
import recoder.kit.ProblemReport;
import recoder.kit.Transformation;

/**
 * This transformation is the composition of all the annotation transformations
 * It is the transformation to be applied directly to the annotated code
 * as it parses for all annotations recognized by ProActive
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.00
 */
public class AllAnnotationsTransformation extends Transformation {
	
	private final List<AnnotationTransformation> _knownTransformations 
		= new ArrayList<AnnotationTransformation>();
	
	private final Map<Class , TransformationKernel> _knownAnnotations = 
		new HashMap<Class , TransformationKernel>();
	
	public AllAnnotationsTransformation(CrossReferenceServiceConfiguration serviceConfig) {
		super(serviceConfig);
		populateKnownAnnotations(serviceConfig);
		populateKnownTransformations(serviceConfig);
	}
	
	// create the annotation transformations that should execute on the given compilation units
	public AllAnnotationsTransformation(CrossReferenceServiceConfiguration serviceConfig,
			String[] processedClassNames
			) 
	{
		super(serviceConfig);
		populateKnownAnnotations(serviceConfig);
		populateKnownTransformations(serviceConfig,processedClassNames);
	}
	
	private void populateKnownAnnotations(
			CrossReferenceServiceConfiguration serviceConfig) {
		// TODO add here other implementations for code generation annotations
		//ActiveObject
		_knownAnnotations.put(ActiveObject.class, new ActiveObjectKernel(serviceConfig));
	}

	private void populateKnownTransformations(
			CrossReferenceServiceConfiguration serviceConfig) {

		for( Map.Entry<Class, TransformationKernel> pair : _knownAnnotations.entrySet()) {
			_knownTransformations.add(new AnnotationTransformation( serviceConfig,
					pair.getKey(),	pair.getValue()));
		}
		
	}
	
	private void populateKnownTransformations(
			CrossReferenceServiceConfiguration serviceConfig,
			String[] processedClassNames) {
		
		List<CompilationUnit> compilationUnits, allCompilationUnits;
		
		allCompilationUnits = serviceConfig.getSourceFileRepository().getCompilationUnits();
		// there should be some compilation units...
		if( allCompilationUnits == null || allCompilationUnits.isEmpty() ){
			throw new IllegalArgumentException("No compilation units to process. Maybe you didn't initialize recoder correctly?"); 
		}
		
		compilationUnits = new ArrayList<CompilationUnit>();
		for( String className : processedClassNames ) {
			CompilationUnit cu = getCompilationUnit(allCompilationUnits, className);
			if( cu == null){
				throw new IllegalArgumentException("Compilation unit " + className + " could not be loaded.");
			}
			compilationUnits.add(cu);
		}
		
		for( Map.Entry<Class, TransformationKernel> pair : _knownAnnotations.entrySet()) {
			_knownTransformations.add(new AnnotationTransformation( serviceConfig,
					compilationUnits, pair.getKey(), pair.getValue()));
		}
		
	}
	
	/*
	 * try to get the compilation unit for the given class name.
	 * The compilation unit must be already loaded by the Service Configurator
	 */
	private CompilationUnit getCompilationUnit(List<CompilationUnit> allCompilationUnits, String className) {
		
		for( CompilationUnit cu : allCompilationUnits ){
			if(cu.getPrimaryTypeDeclaration().getFullName().equals(className)){
				return cu;
			}
		}
		
		return null;
		
	}

	@Override
	public ProblemReport execute() {

		// execute all the known annotations
		List<ProblemReport> transformationResults = new ArrayList<ProblemReport>();
		for (AnnotationTransformation transformation : _knownTransformations) {
			ProblemReport result = transformation.execute();
			if( result instanceof Problem)
				transformationResults.add(result);
		}
		
		if(transformationResults.isEmpty())
			// all went fine and dandy
			return setProblemReport(EQUIVALENCE);
		
		// something went wrong in heaven
		StringBuilder errorMsg = new StringBuilder();
		errorMsg.append("The following errors were encountered while processing all the annotations:\n");
		for (ProblemReport error : transformationResults) {
			errorMsg.append(error.toString() + "\n");
		}
		
		return setProblemReport(new AnnotationProblem(errorMsg.toString()));
	}
}
