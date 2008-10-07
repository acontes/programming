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
package functionalTests.component.conform.components;

import org.objectweb.fractal.api.control.AttributeController;


public interface CAttributes extends AttributeController {
    boolean getX1();

    void setX1(boolean v);

    byte getX2();

    void setX2(byte v);

    char getX3();

    void setX3(char v);

    short getX4();

    void setX4(short v);

    int getX5();

    void setX5(int v);

    long getX6();

    void setX6(long v);

    float getX7();

    void setX7(float v);

    double getX8();

    void setX8(double v);

    String getX9();

    void setX9(String v);

    boolean getReadOnlyX10();

    void setWriteOnlyX11(boolean v);
}
