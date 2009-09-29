/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2009 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@ow2.org
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
 * $$PROACTIVE_INITIAL_DEV$$
 */
package functionalTests.activeobject.webservices;

import java.io.Serializable;


public class Weather implements Serializable {
    private float temperature;
    private String forecast;
    private boolean rain;
    private float howMuchRain;

    public void setTemperature(float temp) {
        temperature = temp;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setForecast(String fore) {
        forecast = fore;
    }

    public String getForecast() {
        return forecast;
    }

    public void setRain(boolean r) {
        rain = r;
    }

    public boolean getRain() {
        return rain;
    }

    public void setHowMuchRain(float howMuch) {
        howMuchRain = howMuch;
    }

    public float getHowMuchRain() {
        return howMuchRain;
    }
}
