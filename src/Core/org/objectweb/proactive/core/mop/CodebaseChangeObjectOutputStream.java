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
package org.objectweb.proactive.core.mop;

import java.io.IOException;
import java.io.OutputStream;
import java.rmi.server.RMIClassLoader;


/**
 * This oos annotates all classes with the same fixed codebase
 * The codebase is the same as the class given as parameter to the constructor
 * The oos first verifies if the classes are compatible, using ClassComparator 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 4.10
 */
public class CodebaseChangeObjectOutputStream extends SunMarshalOutputStream {

    private final Class<?> originalClass;
    private final String codebaseAnnotation;

    public CodebaseChangeObjectOutputStream(OutputStream out, Class<?> originalClass) throws IOException {
        super(out);
        this.originalClass = originalClass;
        this.codebaseAnnotation = Utils.getCodebase(originalClass);
    }

    @Override
    protected void annotateClass(Class<?> arg0) throws IOException {
    	try{
    		if( ClassComparator.compare(originalClass, arg0) )
    			// classes compatible => write the new codebase value
    			writeLocation(codebaseAnnotation);
    		else
    			// go with the default
    			super.annotateClass(arg0);
    	} catch(IllegalArgumentException e){
    		throw new IOException(e);
    	}
    }

}
