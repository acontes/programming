/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.io.Serializable;

/**
 * 
 *
 */
public enum SpaceType implements Serializable, Comparable<SpaceType> {
    INPUT, OUTPUT, SCRATCH;
    
    public String getDirectory() {
		return name().toLowerCase();
	}
}
