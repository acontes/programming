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
package org.objectweb.proactive.extra.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.proactive.extra.annotation.activeobject.ActiveObject;
import org.objectweb.proactive.extra.annotation.activeobject.ActiveObjectVisitorAPT;
import org.objectweb.proactive.extra.annotation.migration.strategy.OnArrival;
import org.objectweb.proactive.extra.annotation.migration.strategy.OnArrivalVisitorAPT;
import org.objectweb.proactive.extra.annotation.callbacks.isready.VirtualNodeIsReadyCallback;
import org.objectweb.proactive.extra.annotation.callbacks.isready.VirtualNodeIsReadyCallbackVisitorAPT;
import org.objectweb.proactive.extra.annotation.callbacks.nodeattachment.NodeAttachmentCallback;
import org.objectweb.proactive.extra.annotation.callbacks.nodeattachment.NodeAttachmentCallbackVisitorAPT;
import org.objectweb.proactive.extra.annotation.migration.strategy.OnDeparture;
import org.objectweb.proactive.extra.annotation.migration.strategy.OnDepartureVisitorAPT;
import org.objectweb.proactive.extra.annotation.remoteobject.RemoteObject;
import org.objectweb.proactive.extra.annotation.remoteobject.RemoteObjectVisitorAPT;

import com.sun.mirror.apt.AnnotationProcessor;
import com.sun.mirror.apt.AnnotationProcessorEnvironment;
import com.sun.mirror.apt.Messager;
import com.sun.mirror.declaration.AnnotationTypeDeclaration;
import com.sun.mirror.declaration.Declaration;
import com.sun.mirror.util.SimpleDeclarationVisitor;

/**
 * The AnnotationProcessor that processes the ActiveObject annotation.
 * It processes only objects.
 * For every object encountered, the ActiveObjectVisitor is used to 
 * visit the declaration.
 * 
 * @author fabratu
 * @version %G%, %I%
 * @since ProActive 3.90
 */

public class ProActiveProcessorApt implements AnnotationProcessor {
	
	private final AnnotationProcessorEnvironment _environment;
	
	private final Map<Class, SimpleDeclarationVisitor> _annotationVisitors = 
		new HashMap<Class, SimpleDeclarationVisitor>();

	public ProActiveProcessorApt(AnnotationProcessorEnvironment env) {
		
		_environment = env;
		Messager messager = _environment.getMessager();

		_annotationVisitors.put(ActiveObject.class, new ActiveObjectVisitorAPT(messager));
		_annotationVisitors.put(RemoteObject.class, new RemoteObjectVisitorAPT(messager));
		_annotationVisitors.put(OnDeparture.class, new OnDepartureVisitorAPT(messager));
		_annotationVisitors.put(OnArrival.class, new OnArrivalVisitorAPT(messager));
		_annotationVisitors.put(NodeAttachmentCallback.class, new NodeAttachmentCallbackVisitorAPT(messager));
		_annotationVisitors.put(VirtualNodeIsReadyCallback.class, new VirtualNodeIsReadyCallbackVisitorAPT(messager));
		
	}

	@Override
	public void process() {
		for( Entry<Class, SimpleDeclarationVisitor> av_pair : _annotationVisitors.entrySet()) {
			Class annotation = av_pair.getKey();
			String annotName = annotation.getName();
			AnnotationTypeDeclaration annotDeclaration = 
				(AnnotationTypeDeclaration)_environment.getTypeDeclaration(annotName);
			Target applicableOn = annotDeclaration.getAnnotation(Target.class);
			SimpleDeclarationVisitor visitor = av_pair.getValue();
			
			if(visitor == null)
				return;
			
			for( Declaration typeDeclaration : _environment.getDeclarationsAnnotatedWith(annotDeclaration) ) {
				
				if(!testSuitableDeclaration( typeDeclaration , applicableOn)) {
					_environment.getMessager().printError(typeDeclaration.getPosition(), 
							"[ERROR] The @" + annotation.getSimpleName() + " annotation is not applicable for this type of Java construct.");
				}
				
				// check using the visitor
				typeDeclaration.accept(visitor);
			}
		}
	}

	private boolean testSuitableDeclaration(Declaration typeDeclaration,
			Target applicableOn) {
		
		for( ElementType applicableType : applicableOn.value() ) {
			if(Utils.applicableOnDeclaration(applicableType,typeDeclaration))
				return true;
		}
		return false;
	}	

}
