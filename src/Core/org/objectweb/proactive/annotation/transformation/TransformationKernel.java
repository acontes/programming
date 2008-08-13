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

import org.jboss.logging.Logger;
import org.objectweb.proactive.core.util.log.Loggers;

import recoder.ProgramFactory;
import recoder.ServiceConfiguration;
import recoder.java.Declaration;
import recoder.java.declaration.AnnotationUseSpecification;
import recoder.service.ChangeHistory;

/**
 * This interface specifies the general contract for how an annotation transformation
 * should generate code which should replace the annotation 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.00
 */
public abstract class TransformationKernel {
	
	protected final ChangeHistory _changes;
	// all kernels will need to generate code
	protected final ProgramFactory _codeGen;
	// high-level code generation constructs
	protected final CodeGenerationHelper _cgHelper;
	
	protected static final Logger _logger = Logger.getLogger(Loggers.ANNOTATIONS); 
	
	public TransformationKernel(ServiceConfiguration sc) {
		_codeGen = sc.getProgramFactory();
		_changes = sc.getChangeHistory();
		_cgHelper = new CodeGenerationHelper(_codeGen , _changes);
	}
	
	public abstract void generateAnnotationReplacement(
			Declaration parentDeclaration, // the annotated declaration
			AnnotationUseSpecification annotation)
			throws CodeGenerationException;
}