/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.core.group;

import java.util.Vector;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.LocalBodyStore;
import org.objectweb.proactive.core.body.proxy.UniversalBodyProxy;
import org.objectweb.proactive.core.component.representative.ProActiveComponentRepresentative;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.Proxy;
import org.objectweb.proactive.core.mop.StubObject;


/**
 * This class provides multithreading for the oneway methodcall on a group.
 *
 * @author Laurent Baduel
 */
public class ProcessForOneWayCall extends AbstractProcessForGroup
    implements Runnable {
    private int index;
    private MethodCall mc;
    private Body body;
    private ExceptionListException exceptionList;

    public ProcessForOneWayCall(ProxyForGroup proxyGroup, Vector memberList,
        int index, MethodCall mc, Body body,
        ExceptionListException exceptionList) {
        this.proxyGroup = proxyGroup;
        this.memberList = memberList;
        this.index = index;
        this.mc = mc;
        this.body = body;
        this.exceptionList = exceptionList;
    }

    public void run() {
        LocalBodyStore.getInstance().setCurrentThreadBody(body);
        Object object = null;

        // try-catch block added by AdC for P2P
        try {
            object = this.memberList.get(this.index);
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }
        boolean objectIsLocal = false;

        /* only do the communication (reify) if the object is not an error nor an exception */
        if (!(object instanceof Throwable)) {
            Proxy lastProxy = AbstractProcessForGroup.findLastProxy(object);
            if (lastProxy instanceof UniversalBodyProxy) {
                objectIsLocal = ((UniversalBodyProxy) lastProxy).isLocal();
            }
            try {
                if (lastProxy == null) {
                    // means we are dealing with a non-reified object (a standard Java Object)
                    this.mc.execute(object);
                } else if (objectIsLocal) {
                    if (!(mc instanceof MethodCallControlForGroup)) {
                        ((StubObject) object).getProxy().reify(new MethodCall(
                                this.mc));
                    } else {
                        if (object instanceof ProActiveComponentRepresentative) {
                            // delegate to the corresponding interface
                            Object target;
                            if (mc.getComponentMetadata() !=null) {
                                target = object;
                            } else {
                                target = ((ProActiveComponentRepresentative) object).getFcInterface(mc.getComponentMetadata().getComponentInterfaceName());
                            }
                            this.mc.execute(target);
                        } else {
                            ((StubObject) object).getProxy().reify(this.mc);
                        }
                    }
                } else {
                    if (object instanceof ProActiveComponentRepresentative) {
                        // delegate to the corresponding interface
                        Object target = ((ProActiveComponentRepresentative) object).getFcInterface(mc.getComponentMetadata().getComponentInterfaceName());
                        // TODO : possible optimization? (just reify, avoid execution)
                        this.mc.execute(target);
                    } else {
                        ((StubObject) object).getProxy().reify(this.mc);
                    }
                }
            } catch (Throwable e) {
                this.exceptionList.add(new ExceptionInGroup(object, this.index,
                        e.fillInStackTrace()));
            }
        }
    }
}
