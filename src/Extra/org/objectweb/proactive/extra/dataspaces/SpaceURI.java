/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.io.Serializable;

/**
 * resp: - stores URI as fields: app id, type, name | rt and node - correctness
 * guarantee - immutable - used for lookup (comparator) - serializable (sent
 * across network) col: - SpacesDirectory - instantiated in resolveFile -
 * instantiated during DS configuration - SpaceInstanceInfo
 * 
 */
public final class SpaceURI implements Serializable, Comparable<SpaceURI> {

	public static final String VFS_SCHEME = "vfs:///";

	/**
	 * 
	 */
	private static final long serialVersionUID = 7148704434729348732L;

	public static SpaceURI createApplicationSpacesURI(long appId) {
		return new SpaceURI(appId, null, null, null, null, null);
	}

	public static SpaceURI createScratchSpaceURI(long appId) {
		return createScratchSpaceURI(appId, null);
	}

	public static SpaceURI createScratchSpaceURI(long appId, String runtimeId) {
		return createScratchSpaceURI(appId, runtimeId, null);
	}

	public static SpaceURI createScratchSpaceURI(long appId, String runtimeId,
			String nodeId) {
		return createScratchSpaceURI(appId, runtimeId, nodeId, null);
	}

	public static SpaceURI createScratchSpaceURI(long appId, String runtimeId,
			String nodeId, String path) {
		return new SpaceURI(appId, SpaceType.SCRATCH, null, runtimeId, nodeId,
				path);
	}

	public static SpaceURI createInputSpaceURI(long appId) {
		return createInputSpaceURI(appId, null);
	}

	public static SpaceURI createInputSpaceURI(long appId, String name) {
		return createInputSpaceURI(appId, name, null);
	}

	public static SpaceURI createInputSpaceURI(long appId, String name,
			String path) {
		return new SpaceURI(appId, SpaceType.INPUT, name, null, null, path);
	}

	public static SpaceURI createOutputSpaceURI(long appId) {
		return createOutputSpaceURI(appId, null);
	}

	public static SpaceURI createOutputSpaceURI(long appId, String name) {
		return createOutputSpaceURI(appId, name, null);
	}

	public static SpaceURI createOutputSpaceURI(long appId, String name,
			String path) {
		return new SpaceURI(appId, SpaceType.OUTPUT, name, null, null, path);
	}

	public static SpaceURI parseURI(String uri) {
		// TODO
		throw new UnsupportedOperationException("write me, pleeease");
	}

	private final long appId;

	private final SpaceType spaceType;

	private final String name;

	private final String runtimeId;

	private final String nodeId;

	private final String path;

	private SpaceURI(long appId, SpaceType spaceType, String name,
			String runtimeId, String nodeId, String path) {
		if ((spaceType == null && (name != null || runtimeId != null))
				|| (runtimeId == null && nodeId != null)
				|| ((nodeId == null || name == null) && path != null)) {
			throw new IllegalArgumentException(
					"Malformed URI. Provided arguments do not meet hierarchy consistency requirement.");
		}

		if ((spaceType == SpaceType.INPUT || spaceType == SpaceType.OUTPUT)
				&& runtimeId != null) {
			throw new IllegalArgumentException(
					"Malformed URI. Input/output can not have runtime id.");
		}

		if (spaceType == SpaceType.SCRATCH && name != null) {
			throw new IllegalArgumentException(
					"Malformed URI. Scratch can not have name.");
		}

		this.appId = appId;
		this.spaceType = spaceType;
		this.name = name;
		this.runtimeId = runtimeId;
		this.nodeId = nodeId;
		this.path = path;
	}

	public long getAppId() {
		return appId;
	}

	public SpaceType getSpaceType() {
		return spaceType;
	}

	public String getName() {
		return name;
	}

	public String getRuntimeId() {
		return runtimeId;
	}

	public String getNodeId() {
		return nodeId;
	}

	public String getPath() {
		return path;
	}

	public boolean isComplete() {
		return spaceType != null
				&& (name != null || (runtimeId != null && nodeId != null));
	}

	public SpaceURI getURIWithPath(String path) {
		if (path.length() > 0 && !isComplete()) {
			throw new IllegalStateException(
					"only complete URIs can have path");
		}
		return new SpaceURI(appId, spaceType, name, runtimeId, nodeId, path);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(VFS_SCHEME);

		sb.append(Long.toString(appId));
		sb.append('/');

		if (spaceType == null) {
			return sb.toString();
		}
		sb.append(spaceType.getDirectoryName());
		sb.append('/');

		switch (spaceType) {
		case INPUT:
		case OUTPUT:
			if (name == null) {
				return sb.toString();
			}
			sb.append(name);
			break;
		case SCRATCH:
			if (runtimeId == null) {
				return sb.toString();
			}
			sb.append(runtimeId);
			sb.append('/');

			if (nodeId == null) {
				return sb.toString();
			}
			sb.append(nodeId);
			break;
		default:
			assert false;
		}
		sb.append('/');

		return sb.toString();
	}

	public int compareTo(SpaceURI other) {
		if (this == other) {
			return 0;
		}
		if (other == null) {
			throw new NullPointerException();
		}

		if (appId != other.appId) {
			if (appId < other.appId) {
				return -1;
			}
			return 1;
		}

		if (spaceType == null) {
			if (other.spaceType != null) {
				return -1;
			}
			return 0;
		} else {
			if (other.spaceType == null) {
				return 1;
			}
			final int cmp = spaceType.compareTo(other.spaceType);
			if (cmp != 0) {
				return cmp;
			}
		}

		switch (spaceType) {
		case INPUT:
		case OUTPUT:
			if (name == null) {
				if (other.name != null) {
					return -1;
				}
				return 0;
			} else {
				if (other.name == null) {
					return 1;
				}
				final int cmp = name.compareTo(other.name);
				if (cmp != 0) {
					return cmp;
				}
			}
			break;
		case SCRATCH:
			if (runtimeId == null) {
				if (other.runtimeId != null) {
					return -1;
				}
				return 0;
			} else {
				if (other.runtimeId == null) {
					return 1;
				}
				final int cmp = runtimeId.compareTo(other.runtimeId);
				if (cmp != 0) {
					return cmp;
				}
			}

			if (nodeId == null) {
				if (other.nodeId != null) {
					return -1;
				}
				return 0;
			} else {
				if (other.nodeId == null) {
					return 1;
				}
				final int cmp = nodeId.compareTo(other.nodeId);
				if (cmp != 0) {
					return cmp;
				}
			}
			break;
		}

		if (path == null) {
			if (other.path != null) {
				return -1;
			}
			return 0;
		} else {
			if (other.path == null) {
				return 1;
			}
			return path.compareTo(path);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SpaceURI)) {
			return false;
		}

		SpaceURI other = (SpaceURI) obj;
		return compareTo(other) == 0;
	}
}
