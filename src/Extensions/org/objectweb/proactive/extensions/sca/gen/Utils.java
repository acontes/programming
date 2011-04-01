/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 * ################################################################
 * $$PROACTIVE_INITIAL_DEV$$
 */
package org.objectweb.proactive.extensions.sca.gen;

import java.util.Calendar;


/**
 * Utility class for bytecode generation operations.
 *
 * @author The ProActive Team
 */
public class Utils extends org.objectweb.proactive.core.component.gen.Utils {
    public static final String GENERATED_CLASSNAME_SUFFIX = GEN_ESCAPE_CHAR + "SCAClass";

    public static String getPropertyClassName(String className) {
        return GENERATED_DEFAULT_PREFIX + escapeString(className) + GEN_MIDDLE_SEPARATOR + "property" +
            GENERATED_CLASSNAME_SUFFIX;
    }

    public static String getIntentClassName(String className) {
        return GENERATED_DEFAULT_PREFIX + escapeString(className) + GEN_MIDDLE_SEPARATOR + "intent" +
            GENERATED_CLASSNAME_SUFFIX;
    }
}
