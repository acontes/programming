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
package org.objectweb.proactive.ic2d.data;


/**
 * Holder class for all hosts
 */
public class IC2DObject extends AbstractDataObject {

	// TODO Il y a peut etre mieux?
	private String worldObjectKey;
	
	//
    // -- CONSTRUCTORS -----------------------------------------------
    //
	
	/**
	 * TODO
	 */
	public IC2DObject(){
		super(null);
		WorldObject child = new WorldObject(this);
		worldObjectKey = child.getKey();
		putChild(worldObjectKey, child);
	}
	
	
	//
    // -- PUBLICS METHODS --------------------------------------------
    //
	
	public WorldObject getWorldObject(){
		return (WorldObject)getChild(worldObjectKey);
	}
	
	/**
	 * Destroys this object
	 */
	public void destroyObject() {
		this.destroy();
	}
	
	public String getKey() {
		// An IC2DObject doesn't need a key because it is the top level parent,
		// that's why it doesn't be in a hashmap.
		return "IC2DObject";
	}
}
