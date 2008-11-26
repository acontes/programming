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

import com.sun.mirror.apt.AnnotationProcessor;


/** This annotation processor processes the annotations provided by default
 * whith JDK 1.5. This is needed in order to suppress the unnecessary warnings that
 * apt generates for these default annotations.
 * See also http://forums.sun.com/thread.jspa?threadID=5345947
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */

public class BogusAnnotationProcessor implements AnnotationProcessor {

    public BogusAnnotationProcessor() {
    }

    public void process() {
        // nothing! 
    }

}
