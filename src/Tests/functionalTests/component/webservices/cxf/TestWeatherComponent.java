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

package functionalTests.component.webservices.cxf;

import static org.junit.Assert.assertTrue;

import org.apache.cxf.frontend.ClientProxyFactoryBean;
import org.apache.log4j.Logger;
import org.objectweb.fractal.api.Component;
import org.objectweb.fractal.api.factory.GenericFactory;
import org.objectweb.fractal.api.type.ComponentType;
import org.objectweb.fractal.api.type.InterfaceType;
import org.objectweb.fractal.api.type.TypeFactory;
import org.objectweb.fractal.util.Fractal;
import org.objectweb.proactive.core.component.Constants;
import org.objectweb.proactive.core.component.ContentDescription;
import org.objectweb.proactive.core.component.ControllerDescription;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.httpserver.HTTPServer;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;
import org.objectweb.proactive.extensions.webservices.WSConstants;
import org.objectweb.proactive.extensions.webservices.WebServices;

import functionalTests.component.webservices.common.Weather;
import functionalTests.component.webservices.common.WeatherServiceComponent;
import functionalTests.component.webservices.common.WeatherServiceItf;


public class TestWeatherComponent {

    private static Logger logger = ProActiveLogger.getLogger(Loggers.WEB_SERVICES);

    String url;

    @org.junit.Before
    public void deployWeatherService() {
        try {
            // Get the HTTP server enabling us to retrieve the jetty
            // port number
            HTTPServer httpServer = HTTPServer.get();
            String port = PAProperties.PA_XMLHTTP_PORT.getValue();
            this.url = "http://localhost:" + port + "/";

            Component boot = null;
            Component comp = null;

            boot = org.objectweb.fractal.api.Fractal.getBootstrapComponent();

            TypeFactory tf = Fractal.getTypeFactory(boot);
            GenericFactory cf = Fractal.getGenericFactory(boot);

            ComponentType typeComp = tf.createFcType(new InterfaceType[] { tf.createFcItfType(
                    "weather-service", WeatherServiceItf.class.getName(), false, false, false) });

            comp = cf.newFcInstance(typeComp, new ControllerDescription("server", Constants.PRIMITIVE),
                    new ContentDescription(WeatherServiceComponent.class.getName(), null));

            Fractal.getLifeCycleController(comp).startFc();

            WebServices.exposeComponentAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, comp, url,
                    "server", new String[] { "weather-service" });

            logger.info("Deployed an weather-service interface as a webservice service on : " + url);
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }

    @org.junit.Test
    public void TestWeatherService() {

        ClientProxyFactoryBean factory = new ClientProxyFactoryBean();
        factory.setServiceClass(WeatherServiceItf.class);
        factory.setAddress(url + WSConstants.SERVICES_PATH + "server_weather-service");
        WeatherServiceItf client = (WeatherServiceItf) factory.create();

        try {

            Weather w = new Weather();

            w.setTemperature((float) 39.3);
            w.setForecast("Cloudy with showers");
            w.setRain(true);
            w.setHowMuchRain((float) 4.5);

            client.setWeather(w);

            Weather weather = client.getWeather();

            // Display the result
            logger.info("Temperature               : " + weather.getTemperature());
            logger.info("Forecast                  : " + weather.getForecast());
            logger.info("Rain                      : " + weather.getRain());
            logger.info("How much rain (in inches) : " + weather.getHowMuchRain());

            assertTrue(((Float) weather.getTemperature()).equals(new Float(39.3)));
            assertTrue(weather.getForecast().equals("Cloudy with showers"));
            assertTrue(weather.getRain());
            assertTrue(((Float) weather.getHowMuchRain()).equals(new Float(4.5)));

        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }

    }

    @org.junit.After
    public void undeployWeatherService() {
        try {
            WebServices.unExposeComponentAsWebService(WSConstants.CXF_FRAMEWORK_IDENTIFIER, this.url,
                    "server", new String[] { "weather-service" });
        } catch (Exception e) {
            e.printStackTrace();
            assertTrue(false);
        }
    }
}
