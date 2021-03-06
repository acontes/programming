/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2012 INRIA/University of
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
package unitTests.gcmdeployment.pathElement;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.proactive.extensions.gcmdeployment.PathElement;
import org.objectweb.proactive.extensions.gcmdeployment.GCMApplication.commandbuilder.CommandBuilderProActive;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.HostInfoImpl;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.Tool;
import org.objectweb.proactive.extensions.gcmdeployment.GCMDeployment.hostinfo.Tools;
import org.objectweb.proactive.extensions.gcmdeployment.PathElement.PathBase;
import org.objectweb.proactive.utils.OperatingSystem;


public class TestPathElement {
    final String path = "/zzzz/plop";
    final String homeDir = "/user/barbie";
    final String proactiveDir = "/bin/proactive";
    final String toolDir = "/tools/proactive";

    @Test
    public void testRoot() {
        PathElement pe;

        HostInfoImpl hostInfo = new HostInfoImpl();
        hostInfo.setOs(OperatingSystem.unix);

        pe = new PathElement(path);
        Assert.assertEquals(path, pe.getRelPath());
        Assert.assertEquals(path, pe.getFullPath(hostInfo, null));

        pe = new PathElement(path, PathBase.ROOT);
        Assert.assertEquals(path, pe.getRelPath());
        Assert.assertEquals(path, pe.getFullPath(hostInfo, null));

        pe = new PathElement(path, "root");
        Assert.assertEquals(path, pe.getRelPath());
        Assert.assertEquals(path, pe.getFullPath(hostInfo, null));
    }

    @Test
    public void testHome() {
        HostInfoImpl hostInfo = new HostInfoImpl();
        hostInfo.setHomeDirectory(homeDir);
        hostInfo.setOs(OperatingSystem.unix);

        PathElement pe = new PathElement(path, PathBase.HOME);
        String expected = PathElement.appendPath(homeDir, path, hostInfo);
        Assert.assertEquals(expected, pe.getFullPath(hostInfo, null));
    }

    @Test
    public void testProActive() {
        HostInfoImpl hostInfo = new HostInfoImpl();
        hostInfo.setHomeDirectory(homeDir);
        hostInfo.setOs(OperatingSystem.unix);

        CommandBuilderProActive cb = new CommandBuilderProActive();
        cb.setProActivePath(proactiveDir);

        PathElement pe = new PathElement(path, PathBase.PROACTIVE);
        String expected = PathElement.appendPath(homeDir, proactiveDir, hostInfo);
        expected = PathElement.appendPath(expected, path, hostInfo);
        Assert.assertEquals(expected, pe.getFullPath(hostInfo, cb));
    }

    @Test
    public void testTool() {
        HostInfoImpl hostInfo = new HostInfoImpl();
        hostInfo.setHomeDirectory(homeDir);
        hostInfo.setOs(OperatingSystem.unix);
        hostInfo.addTool(new Tool(Tools.PROACTIVE.id, toolDir));

        CommandBuilderProActive cb = new CommandBuilderProActive();
        cb.setProActivePath(proactiveDir);

        PathElement pe = new PathElement(path, PathBase.PROACTIVE);

        String expected = PathElement.appendPath(toolDir, path, hostInfo);
        Assert.assertEquals(expected, pe.getFullPath(hostInfo, cb));
    }

    @Test
    public void testToolException() {
        HostInfoImpl hostInfo = new HostInfoImpl();
        hostInfo.setHomeDirectory(homeDir);
        hostInfo.setOs(OperatingSystem.unix);

        CommandBuilderProActive cb = new CommandBuilderProActive();
        PathElement pe = new PathElement(path, PathBase.PROACTIVE);

        String expected = PathElement.appendPath(homeDir, toolDir, hostInfo);
        expected = PathElement.appendPath(expected, path, hostInfo);
        Assert.assertEquals(null, pe.getFullPath(hostInfo, cb));
    }

    @Test
    public void testClone() throws CloneNotSupportedException {
        PathElement pe = new PathElement(path, PathBase.PROACTIVE);
        Assert.assertEquals(pe, pe.clone());
    }

    @Test
    public void testAppendPath() {
        String s1;
        String s2;
        String expected;

        HostInfoImpl hostInfo = new HostInfoImpl();
        hostInfo.setOs(OperatingSystem.unix);

        expected = "/toto";
        s1 = "/";
        s2 = "toto";
        Assert.assertEquals(expected, PathElement.appendPath(s1, s2, hostInfo));
        s1 = "/";
        s2 = "/toto";
        Assert.assertEquals(expected, PathElement.appendPath(s1, s2, hostInfo));
        s1 = "";
        s2 = "toto";
        Assert.assertEquals(expected, PathElement.appendPath(s1, s2, hostInfo));
        s1 = "";
        s2 = "toto";
        Assert.assertEquals(expected, PathElement.appendPath(s1, s2, hostInfo));
    }
}
