package org.objectweb.proactive.examples.components.helloworld;

/***
 * Julia: France Telecom's implementation of the Fractal API
 * Copyright (C) 2001-2002 France Telecom R&D
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Contact: Eric.Bruneton@rd.francetelecom.com
 *
 * Author: Eric Bruneton
 */
import org.objectweb.fractal.api.control.BindingController;

import org.objectweb.proactive.ProActive;


public class ClientImpl implements Main, BindingController {
    private Service service;

    public void main(final String[] args) {
        System.out.println(
            "main method on client implementation object on node : " +
            ProActive.getBodyOnThis().getNodeURL());
        service.print("hello world");
    }

    public String[] listFc() {
        return new String[] { "s" };
    }

    public Object lookupFc(final String cItf) {
        if (cItf.equals("s")) {
            return service;
        }
        return null;
    }

    public void bindFc(final String cItf, final Object sItf) {
        if (cItf.equals("s")) {
            service = (Service) sItf;
        }
    }

    public void unbindFc(final String cItf) {
        if (cItf.equals("s")) {
            service = null;
        }
    }
}
