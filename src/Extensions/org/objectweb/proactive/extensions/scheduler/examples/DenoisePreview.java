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
package org.objectweb.proactive.extensions.scheduler.examples;

import javax.swing.JPanel;

import org.objectweb.proactive.extensions.scheduler.common.task.ResultPreview;
import org.objectweb.proactive.extensions.scheduler.common.task.TaskResult;
import org.objectweb.proactive.extensions.scheduler.common.task.util.ResultPreviewTool;
import org.objectweb.proactive.extensions.scheduler.common.task.util.ResultPreviewTool.SimpleImagePanel;


/**
 * @author The ProActive Team
 * @since 2.2
 */
public class DenoisePreview extends ResultPreview {
    private static final String MATCH_PATTERN = "Saving output image '";

    @Override
    public JPanel getGraphicalDescription(TaskResult r) {
        String path = this.getPathToImage(r.getOuput().getStderrLogs(false));
        System.out.println("[RESULT_DESCRIPTOR] Displaying " + path);
        return new SimpleImagePanel(path);
    }

    @Override
    public String getTextualDescription(TaskResult r) {
        return this.getPathToImage(r.getOuput().getStderrLogs(false));
    }

    private String getPathToImage(String output) {
        int pos = output.indexOf(MATCH_PATTERN, 0);
        pos += MATCH_PATTERN.length();
        String extracted = output.substring(pos, output.indexOf('\'', pos));
        extracted = ResultPreviewTool.getSystemCompliantPath(extracted);
        return extracted;
    }
}