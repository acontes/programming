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

package functionalTests.activeobject.webservices.cxf;

import static org.junit.Assert.assertTrue;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientFactoryBean;
import org.apache.log4j.Logger;
import org.objectweb.proactive.api.PAActiveObject;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.WebServices;

import functionalTests.activeobject.webservices.common.Weather;
import functionalTests.activeobject.webservices.common.WeatherService;


public class TestWeather {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    String url;

    @org.junit.Before
    public void deployWeatherService() {

        try {
            // Get the HTTP server enabling us to retrieve the jetty
            // port number
            HTTPServer httpServer = HTTPServer.get();
            String port = PAProperties.PA_XMLHTTP_PORT.getValue();
            //        String port = "8081";
            this.url = "http://localhost:" + port + "/";

            WeatherService weatherService = (WeatherService) PAActiveObject.newActive(
                    "functionalTests.activeobject.webservices.common.WeatherService", new Object[] {});
            WebServices.exposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, weatherService, this.url,
                    "WeatherService");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @org.junit.Test
    public void TestWeatherService() {
        ClientFactoryBean factory = new ClientFactoryBean();
        factory.setServiceClass(WeatherService.class);
        factory.setAddress(url + WSConstants.SERVICES_PATH + "WeatherService");
        Client client = factory.create();

        try {

            Weather w = new Weather();

            w.setTemperature((float) 39.3);
            w.setForecast("Cloudy with showers");
            w.setRain(true);
            w.setHowMuchRain((float) 4.5);

            client.invoke("setWeather", new Object[] { w });

            Object[] response = client.invoke("getWeather");

            Weather result = (Weather) response[0];

            // Displaying the result
            logger.info("Temperature               : " + result.getTemperature());
            logger.info("Forecast                  : " + result.getForecast());
            logger.info("Rain                      : " + result.getRain());
            logger.info("How much rain (in inches) : " + result.getHowMuchRain());

            assertTrue(((Float) result.getTemperature()).equals(new Float(39.3)));
            assertTrue(result.getForecast().equals("Cloudy with showers"));
            assertTrue(result.getRain());
            assertTrue(((Float) result.getHowMuchRain()).equals(new Float(4.5)));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @org.junit.After
    public void undeployWeatherService() {
        try {
            WebServices
                    .unExposeAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, this.url, "WeatherService");
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
