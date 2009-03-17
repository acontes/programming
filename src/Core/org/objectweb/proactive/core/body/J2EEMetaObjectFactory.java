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
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;

import org.objectweb.proactive.Body;
import org.objectweb.proactive.core.body.ProActiveMetaObjectFactory.RequestReceiverFactoryImpl;
import org.objectweb.proactive.core.body.exceptions.InactiveBodyException;
import org.objectweb.proactive.core.body.request.Request;
import org.objectweb.proactive.core.body.request.RequestImpl;
import org.objectweb.proactive.core.body.request.RequestQueue;
import org.objectweb.proactive.core.body.request.RequestReceiver;
import org.objectweb.proactive.core.body.request.RequestReceiverFactory;
import org.objectweb.proactive.core.body.request.RequestReceiverImpl;
import org.objectweb.proactive.core.config.PAProperties;
import org.objectweb.proactive.core.mop.MethodCall;
import org.objectweb.proactive.core.mop.Utils;
import org.objectweb.proactive.core.util.converter.MakeDeepCopy;

/**
 * Adaptation of ProActiveMetaObjectFactory for J2EE 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class J2EEMetaObjectFactory extends ProActiveMetaObjectFactory implements Serializable{
  
	private static final MetaObjectFactory instance = new J2EEMetaObjectFactory();
	
	//singleton instance
	public static MetaObjectFactory newInstance() {
		return instance;
	}

	private J2EEMetaObjectFactory() {
		super();
	}
	
	@Override
	protected RequestReceiverFactory newRequestReceiverFactorySingleton() {
		return new J2EERequestReceiverFactory(); 
	}
	
    @Override
    public Object clone() throws CloneNotSupportedException {
        try {
            return MakeDeepCopy.WithObjectStream.makeDeepCopy(this);
        } catch (IOException e) {
            throw (CloneNotSupportedException) new CloneNotSupportedException(e.getMessage()).initCause(e);
        } catch (ClassNotFoundException e) {
            throw (CloneNotSupportedException) new CloneNotSupportedException(e.getMessage()).initCause(e);
        }
    }
    
    protected class J2EERequestReceiverFactory extends RequestReceiverFactoryImpl 
    	implements Serializable {
    	
    	@Override
    	public RequestReceiver newRequestReceiver() {
    		return new J2EERequestReceiver();
    	}
    	
    	protected class J2EERequestReceiver extends RequestReceiverImpl implements java.io.Serializable {
    		
    		@Override
    		public int receiveRequest(Request req, Body bodyReceiver) {
    			if(!PAProperties.PA_J2EE.isSet())
    				return super.receiveRequest(req, bodyReceiver);
    			MethodCall methodCall = req.getMethodCall();
    			String targetObjectClassName = bodyReceiver.getReifiedObject().getClass().getName();
				String targetObjectCodebase = RMIClassLoader.getClassAnnotation(bodyReceiver.getReifiedObject().getClass());
    			try {
    				Class<?> targetCl = RMIClassLoader.loadClass(targetObjectCodebase, targetObjectClassName);
    				ClassDefinitionForger cdf = new ClassDefinitionForger(targetCl);
    				MethodCall newMethodCall = cdf.rebuildMethodCall(methodCall);
    				Request newReq = new RequestImpl(newMethodCall, req.getSender(), req.isOneWay(),
    						req.getSequenceNumber());
    				return super.receiveRequest(newReq, bodyReceiver);
    			} catch (ClassNotFoundException e) {
    				logger.warn("Cannot load class " + targetObjectClassName + " from codebase " 
    						+ targetObjectCodebase + " reason:", e);
    			} catch (MalformedURLException e) {
    				logger.warn("Cannot load class " + targetObjectClassName + " from codebase " 
    						+ targetObjectCodebase + " reason:", e);
    	        } catch (SecurityException e) {
    				logger.warn("Cannot rebuild method call " + methodCall.getReifiedMethod() + " reason:", e);
    			} catch (NoSuchMethodException e) {
    				logger.warn("Cannot rebuild method call " + methodCall.getReifiedMethod() + " reason:", e);
    			} catch (IOException e) {
    				logger.warn("Cannot rebuild method call " + methodCall.getReifiedMethod() + " reason:", e);
    			}
    			
    			logger.warn("Will go for the default request processing mechanism...");
    			return super.receiveRequest(req, bodyReceiver);
    		}
    	}
    }
	
	class ClassDefinitionForger {
    	private final Class<?> targetCl;
    	
    	public ClassDefinitionForger(Class<?> clz) {
    		targetCl = clz;
		}
    	
    	public MethodCall rebuildMethodCall(MethodCall oldmc) throws SecurityException,
    		NoSuchMethodException, IOException {

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
    			newEffectiveArguments = Utils.makeDeepCopy(oldmc.getEffectiveArguments(), newm
    					.getParameterTypes());
    		}
    		MethodCall mc = MethodCall.getMethodCall(newm, oldmc.getGenericTypesMapping(), newEffectiveArguments,
    				oldmc.getExceptionContext());

    		return mc;
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
}
