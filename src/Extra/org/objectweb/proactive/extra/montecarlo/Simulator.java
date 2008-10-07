/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2007 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.extra.montecarlo;

import org.objectweb.proactive.extensions.masterworker.TaskException;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;


/**
 * Simulator
 *
 * This interface represents an access to the Monte-Carlo engine for solving a list of experience sets in parallel.
 *
 * @author The ProActive Team
 */
public interface Simulator {

    /**
     * Asks the engine to solve a list of experience sets which will be run in parallel
     * @param experienceSets list of experience sets to solve
     * @return a list of double which is a concatenation of each list of double produced by each experience set. The order of the ouput list is guarantied to be coherent with the order of the experience list.
     * @throws TaskException if an exception occured inside the user code
     */
    public <T extends Serializable> Enumeration<T> solve(List<ExperienceSet<T>> experienceSets)
            throws TaskException;
}
