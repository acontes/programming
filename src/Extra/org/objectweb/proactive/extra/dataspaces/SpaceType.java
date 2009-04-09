/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.io.Serializable;

/**
 * Data space type with defined succ method.
 */
public enum SpaceType implements Serializable, Comparable<SpaceType> {
	INPUT, OUTPUT, SCRATCH;

	public String getDirectoryName() {
		return name().toLowerCase();
	}

	public SpaceType succ() {
		final SpaceType[] v = SpaceType.values();
		final int n = this.ordinal() + 1;

		if (n < v.length)
			return v[n];
		else
			return null;
	}
}
