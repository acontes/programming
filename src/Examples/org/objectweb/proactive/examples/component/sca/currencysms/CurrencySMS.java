/*
 * ################################################################
 *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2010 INRIA/University of 
 *              Nice-Sophia Antipolis/ActiveEon
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
package org.objectweb.proactive.examples.component.sca.currencysms;

import org.objectweb.fractal.api.NoSuchInterfaceException;
import org.objectweb.fractal.api.control.BindingController;
import org.objectweb.fractal.api.control.IllegalBindingException;
import org.objectweb.fractal.api.control.IllegalLifeCycleException;
import org.osoa.sca.annotations.Property;


public class CurrencySMS implements BindingController, Runner {
    public static final String CURRENCY_SERVICE_NAME = "CurrencyService";
    public static final String ORANGE_SERVICE_NAME = "OrangeService";

    /* SCA Properties */
    @Property
    private String fromCurrency;
    @Property
    private String toCurrency;
    @Property
    private String id;
    @Property
    private String from;
    @Property
    private String to;

    /* SCA References (client interfaces) */
    private CurrencyService currencyService;
    private OrangeService orangeService;

    public String getFromCurrency() {
        return fromCurrency;
    }

    public String getToCurrency() {
        return toCurrency;
    }

    public String getFrom() {
        return from;
    }

    public String getId() {
        return id;
    }

    public String getTo() {
        return to;
    }

    public void setFromCurrency(String fromCurrency) {
        this.fromCurrency = fromCurrency;

    }

    public void setToCurrency(String toCurrency) {
        this.toCurrency = toCurrency;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void execute() {
        double currency = currencyService.ConversionRate(fromCurrency, toCurrency);
        String message = "1" + fromCurrency + " = " + currency + toCurrency;
        boolean success = orangeService.sendSMS(id, from, to, message);
        if (success) {
            System.out.println("SMS successfully sent");
        } else {
            System.out.println("Error while sending SMS");
        }
    }

    public void bindFc(String clientItfName, Object serverItf) throws NoSuchInterfaceException,
            IllegalBindingException, IllegalLifeCycleException {
        if (CURRENCY_SERVICE_NAME.equals(clientItfName)) {
            currencyService = (CurrencyService) serverItf;
        } else if (ORANGE_SERVICE_NAME.equals(clientItfName)) {
            orangeService = (OrangeService) serverItf;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public String[] listFc() {
        return new String[] { CURRENCY_SERVICE_NAME, ORANGE_SERVICE_NAME };
    }

    public Object lookupFc(String clientItfName) throws NoSuchInterfaceException {
        if (CURRENCY_SERVICE_NAME.equals(clientItfName)) {
            return currencyService;
        } else if (ORANGE_SERVICE_NAME.equals(clientItfName)) {
            return orangeService;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }

    public void unbindFc(String clientItfName) throws NoSuchInterfaceException, IllegalBindingException,
            IllegalLifeCycleException {
        if (CURRENCY_SERVICE_NAME.equals(clientItfName)) {
            currencyService = null;
        } else if (ORANGE_SERVICE_NAME.equals(clientItfName)) {
            orangeService = null;
        } else {
            throw new NoSuchInterfaceException(clientItfName);
        }
    }
}