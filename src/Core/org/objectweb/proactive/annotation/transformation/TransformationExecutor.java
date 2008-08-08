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

import java.io.IOException;
import java.util.List;

import org.jboss.logging.Logger;
import org.objectweb.proactive.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.annotation.activeobject.ActiveObjectKernel;
import org.objectweb.proactive.core.util.log.Loggers;

import recoder.CrossReferenceServiceConfiguration;
import recoder.ParserException;
import recoder.convenience.Format;
import recoder.io.SourceFileRepository;
import recoder.java.CompilationUnit;
import recoder.kit.Problem;
import recoder.kit.ProblemReport;
import recoder.kit.Transformation;

/**
 * Execute annotation transformations
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.00
 */
public class TransformationExecutor {
	
	private static final Logger _logger = Logger.getLogger(Loggers.ANNOTATIONS);
	
	public final CrossReferenceServiceConfiguration _sourceConfig;
	public final SourceFileRepository _sourceFileRepo;
	
	public TransformationExecutor() throws ParserException {
		_sourceConfig = new CrossReferenceServiceConfiguration();
		_sourceFileRepo = _sourceConfig.getSourceFileRepository();
		_sourceFileRepo.getAllCompilationUnitsFromPath();
	}
	
	public void execute(Transformation transform) {

		try {
			_logger.debug( "***execute the transformation***" );
			ProblemReport report = transform.execute();

			if (report instanceof Problem) {
				_logger.error( "Errors in the transformation:" + report.toString() );
			} else {		
				_logger.debug("Transformation succeeded - writing results");

				List<CompilationUnit> units = _sourceFileRepo.getCompilationUnits();

				for (int i = 0; i < units.size(); i += 1) {
					CompilationUnit cu = units.get(i);
					// only if the compilation unit is modified...
					if (!_sourceFileRepo.isUpToDate(cu)) {
						_logger.debug(Format.toString("%u [%f]", cu));
						// ...write the results to the output file
						_sourceConfig.getSourceFileRepository().print(cu);	
					}
				}
			}
		} catch (IOException ioe) {
			_logger.error("Error while trying to write the output of the preprocessor. Error details: " , ioe);
		}

		
	}
	
	public static void main(String[] args) {

		try {
			TransformationExecutor firestarter = new TransformationExecutor();

			TransformationKernel kernel = new ActiveObjectKernel(firestarter._sourceConfig); 
			Transformation transform = new AnnotationTransformation(
					firestarter._sourceConfig , 
					ActiveObject.class,
					kernel);

			firestarter.execute(transform);

		} catch (ParserException e) {
			_logger.error("Cannot get the compilation units from the path: " + System.getProperty("input.path"), e );
			return;
		}
	}
		
}
