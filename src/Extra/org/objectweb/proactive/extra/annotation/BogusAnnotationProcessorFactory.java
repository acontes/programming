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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.AnnotationProcessorFactory;
import com.sun.mirror.apt.AnnotationProcessors;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;


/** This processor factory provides the bogus annotation processor for 
 * the default annotations exported in the JDK 1.5.
 * This is needed in order to suppress the unnecessary warnings that
 * apt generates for these default annotations.
 * See also http://forums.sun.com/thread.jspa?threadID=5345947
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class BogusAnnotationProcessorFactory implements AnnotationProcessorFactory {

    private static final Collection<String> _supportedAnnotations = new ArrayList<String>();

    public BogusAnnotationProcessorFactory() {
        _supportedAnnotations.add(Override.class.getName());
        _supportedAnnotations.add(SuppressWarnings.class.getName());
        _supportedAnnotations.add(Deprecated.class.getName());
    }

    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> annotations,
            AnnotationProcessorEnvironment env) {
        if (annotations.isEmpty()) {
            return AnnotationProcessors.NO_OP;
        } else {
            return new BogusAnnotationProcessor();
        }
    }

    public Collection<String> supportedAnnotationTypes() {
        return _supportedAnnotations;
    }

    public Collection<String> supportedOptions() {
        Collection<String> ret = new LinkedList<String>();
        return ret;
    }

}
