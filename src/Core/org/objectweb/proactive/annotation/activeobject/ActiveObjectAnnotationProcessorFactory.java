package org.objectweb.proactive.annotation.activeobject;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;

/**
 * This is the factory that provides the ActiveObjectAnnotationProcessor 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */
public class ActiveObjectAnnotationProcessorFactory implements
		AnnotationProcessorFactory {
	
	// annotation factory supported options
	private static final Collection<String> _supportedOptions = 
		Collections.unmodifiableCollection(Arrays.asList(
				"enableTypeGenerationInEditor" // Eclipse IDE option
				));
	// the annotations for which this factory provides processors
	private static final Collection<String> _supportedAnnotations =
		Collections.singletonList(ActiveObject.class.getName());
	
	// empty no-arg constructor required by the Mirror API
	public ActiveObjectAnnotationProcessorFactory() {
		
	}

	/* (non-Javadoc)
	 * @see com.sun.mirror.apt.AnnotationProcessorFactory#getProcessorFor(java.util.Set, com.sun.mirror.apt.AnnotationProcessorEnvironment)
	 */
	@Override
	public AnnotationProcessor getProcessorFor(
			Set<AnnotationTypeDeclaration> annotations,
			AnnotationProcessorEnvironment env) {
		
		if (annotations.isEmpty()) {
			return AnnotationProcessors.NO_OP;
		} else {
			return new ActiveObjectAnnotationProcessor(env);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.sun.mirror.apt.AnnotationProcessorFactory#supportedAnnotationTypes()
	 */
	@Override
	public Collection<String> supportedAnnotationTypes() {
		return _supportedAnnotations;
	}

	/* (non-Javadoc)
	 * @see com.sun.mirror.apt.AnnotationProcessorFactory#supportedOptions()
	 */
	@Override
	public Collection<String> supportedOptions() {
		return _supportedOptions;
	}

}