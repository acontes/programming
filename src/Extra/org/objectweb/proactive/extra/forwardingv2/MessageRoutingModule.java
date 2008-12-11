package org.objectweb.proactive.extra.forwardingv2;

import org.objectweb.proactive.extra.forwardingv2.client.Endpoint;
import org.objectweb.proactive.extra.forwardingv2.client.EndpointImpl;
import org.objectweb.proactive.extra.forwardingv2.remoteobject.util.MessageRoutingRegistry;

import com.google.inject.Binder;
import com.google.inject.Module;

public class MessageRoutingModule implements Module {

	public void configure(Binder binder) {
		binder.bind(Endpoint.class).to(EndpointImpl.class);
	}
	
}
