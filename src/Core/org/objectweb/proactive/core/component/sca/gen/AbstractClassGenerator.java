/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 * 				Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
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
 * If needed, contact us to obtain a release under GPL Version 2 
 * or a different license than the GPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.core.component.sca.gen;

import javassist.ClassPool;

import org.apache.log4j.Logger;
import org.objectweb.proactive.core.component.sca.exceptions.ClassGenerationFailedException;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 * This class is the parent of classes for generating SCA component classes. It provides utility methods that are
 * used in subclasses.
 *
 * @author The ProActive Team
 */
public abstract class AbstractClassGenerator {
    protected static final transient Logger logger = ProActiveLogger.getLogger(Loggers.COMPONENTS_GEN_ITFS);
    protected static final ClassPool pool = ClassPool.getDefault();

    protected Class<?> loadClass(final String className) throws ClassNotFoundException {
        // try to fetch the class from the default class loader
        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    public abstract String generateClass(final String className) throws ClassGenerationFailedException;
}
