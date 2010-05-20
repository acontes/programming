package org.objectweb.proactive.core.body.future;

import java.io.Serializable;

import org.objectweb.proactive.core.body.UniversalBody;
import org.objectweb.proactive.core.body.tags.MessageTags;

/**
 * Container for storing bodies and associated tags.
 * To be used by the FuturePool when storing the destination bodies, and then adding
 * new Automatic Continuations.
 * 
 * @author cruz
 *
 */
public class BodiesAndTags implements Serializable {
	
	/** destination body */
	UniversalBody body;
	
	/** message tags to be attached when a sending a reply for this body */
	MessageTags tags;
	
	public BodiesAndTags() {
		this.body = null;
		this.tags = null;
	}
	
	public BodiesAndTags(UniversalBody body) {
		this.body = body;
		this.tags = null;
	}
	
	public BodiesAndTags(UniversalBody body, MessageTags tags) {
		this.body = body;
		this.tags = tags;
	}
	
	public UniversalBody getBody() {
		return this.body;
	}
	
	public MessageTags getTags() {
		return this.tags;
	}
	
	public void setTags(MessageTags tags) {
		this.tags = tags;
	}
	
}
