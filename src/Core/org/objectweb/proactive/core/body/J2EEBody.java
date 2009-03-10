/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2008 INRIA/University of Nice-Sophia Antipolis
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
package org.objectweb.proactive.core.body;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;

import javax.resource.spi.work.ExecutionContext;
import javax.resource.spi.work.Work;
import javax.resource.spi.work.WorkEvent;
import javax.resource.spi.work.WorkException;
import javax.resource.spi.work.WorkListener;
import javax.resource.spi.work.WorkManager;

import org.objectweb.proactive.Active;
import org.objectweb.proactive.ActiveObjectCreationException;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestImpl;
import org.objectweb.proactive.core.mop.ConstructorCall;
import org.objectweb.proactive.core.mop.ConstructorCallExecutionFailedException;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.Utils;


/**
 * This class provides a different method to start
 * the body's activity thread, according to the J2EE specs
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class J2EEBody extends ActiveBody implements Work {

    public J2EEBody(ConstructorCall c, String nodeURL, Active activity, MetaObjectFactory factory,
            String jobID) throws java.lang.reflect.InvocationTargetException,
            ConstructorCallExecutionFailedException, ActiveObjectCreationException {
        super(c, nodeURL, activity, factory, jobID);
    }

    private transient WorkManager wm;

    public void setWorkManager(WorkManager wm) {
        this.wm = wm;
    }

    @Override
    public void release() {

    }

    private String targetObjectClassName;
    private String targetObjectCodebase;

    public void setTargetObjectClassName(String targetObjectClassName) {
        this.targetObjectClassName = targetObjectClassName;
    }

    public void setTargetObjectCodebase(String targetObjectCodebase) {
        this.targetObjectCodebase = targetObjectCodebase;
    }

    @Override
    public void serve(Request request) {
        // load the class
        try {
            Class<?> targetCl = RMIClassLoader.loadClass(targetObjectCodebase, targetObjectClassName);
            ClassDefinitionForger cdf = new ClassDefinitionForger(targetCl);
            Request newReq = cdf.rebuildRequest(request);
            super.serve(newReq);
            return;
        } catch (MalformedURLException e) {
            logger.warn("Cannot load class " + targetObjectClassName + " reason:", e);
        } catch (ClassNotFoundException e) {
            logger.warn("Cannot load class " + targetObjectClassName + " reason:", e);
        } catch (SecurityException e) {
            logger.warn("Cannot rebuild request " + request + " reason:", e);
        } catch (NoSuchMethodException e) {
            logger.warn("Cannot rebuild request " + request + " reason:", e);
        } catch (IOException e) {
            logger.warn("Cannot rebuild request " + request + " reason:", e);
        }

        // serve!
        super.serve(request);
    }
    
    class ClassDefinitionForger {
    	private final Class<?> targetCl;
    	
    	public ClassDefinitionForger(Class<?> clz) {
    		targetCl = clz;
		}
    	
    	public Request rebuildRequest(Request request) throws SecurityException,
    	NoSuchMethodException, IOException {
    		Request ret;
    		MethodCall oldmc = request.getMethodCall();
    		Method oldm = oldmc.getReifiedMethod();

    		Method newm;
    		Object[] newEffectiveArguments;
    		try {
    			newm = targetCl.getMethod(oldm.getName(), oldm.getParameterTypes());
    			newEffectiveArguments = oldmc.getEffectiveArguments();
    		} catch (NoSuchMethodException e) {
    			// now it will get really nasty!
    			newm = getSimilarMethod(oldm);
    			// "adjust" classes of the effective arguments
    			newEffectiveArguments = Utils.makeJ2EEDeepCopy(oldmc.getEffectiveArguments(), newm
    					.getParameterTypes());
    		}

    		MethodCall mc = MethodCall.getMethodCall(newm, oldmc.getGenericTypesMapping(), newEffectiveArguments,
    				oldmc.getExceptionContext());

    		ret = new RequestImpl(mc, request.getSender(), request.isOneWay(), request.getSequenceNumber());
    		return ret;
    	}
    	
        private Method getSimilarMethod(Method oldm) throws NoSuchMethodException {
            for (Method m : targetCl.getMethods()) {
                if (m.getName().equals(oldm.getName()) && sameParams(m, oldm)) {
                    return m;
                }
            }
            throw new NoSuchMethodException(oldm.toString());
        }

        private boolean sameParams(Method m, Method oldm) {
            Class<?>[] p = m.getParameterTypes();
            Class<?>[] oldp = oldm.getParameterTypes();
            if (oldp.length != p.length)
                return false;
            for (int i = 0; i < p.length; i++) {
                Class<?> c = p[i];
                Class<?> oldc = oldp[i];
                if (!c.getName().equals(oldc.getName()))
                    return false;
            }
            return true;
        }
    }

    @Override
    public void startBody() {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting J2EE Body");
        }

        String bodyName = shortClassName(getName()) + " on " + getNodeURL();

        synchronized (this) {
            try {
                wm.startWork(this, Long.MAX_VALUE, new ExecutionContext(), new BodyWorkListener(bodyName));
                this.wait();
            } catch (InterruptedException e) {
                logger.warn(e.getMessage(), e);
            } catch (WorkException e) {
                logger.error("Unable to start the J2EE body thread:", e);
            }
        }
    }

    private String shortClassName(String fqn) {
        int n = fqn.lastIndexOf('.');
        if ((n == -1) || (n == (fqn.length() - 1))) {
            return fqn;
        }
        return fqn.substring(n + 1);
    }

    class BodyWorkListener implements WorkListener {

        private String bodyName;

        public BodyWorkListener(String bodyName) {
            this.bodyName = bodyName;
        }

        // TODO actually use these events!
        @Override
        public void workAccepted(WorkEvent arg0) {
            logger.debug("Thread for body " + bodyName + " accepted by the AS scheduler!");
        }

        @Override
        public void workCompleted(WorkEvent arg0) {
            logger.warn("Thread for body " + bodyName + " completed!");
        }

        @Override
        public void workRejected(WorkEvent arg0) {
            logger.warn("Thread for body " + bodyName +
                " rejected for scheduling by the AS! this is not good..");
        }

        @Override
        public void workStarted(WorkEvent arg0) {
            logger.warn("Work has been started on body " + bodyName);
        }

    }

}
