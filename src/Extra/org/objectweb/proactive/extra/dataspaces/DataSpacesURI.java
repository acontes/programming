/**
 * 
 */
package org.objectweb.proactive.extra.dataspaces;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.objectweb.proactive.extra.dataspaces.exceptions.MalformedURIException;


/**
 * Represents any valid URI used in Data Spaces - abstract location in Data Spaces valid across set
 * of nodes.
 * 
 * <p>
 * Example URI:
 * 
 * <pre>
 * vfs:///439654/output/stats/some_dir/file.txt
 * </pre>
 * 
 * URI is represented in meaningful way, i.e. its structure has semantic. It may consists of
 * following components forming a hierarchy, separated by slashes:
 * <ol>
 * <li>URI scheme, always present; always vfs:///
 * <li>identifier of application, always present; e.g. 439654</li>
 * <li>type of data space: input, output or scratch; e.g. output</li>
 * <li>name of input/output OR scratch runtime and node id; e.g. stats OR runtimeXX/nodeZZ</li>
 * <li>defined path within data space; e.g. some_dir/file.txt</li>
 * </ol>
 * 
 * Every component except scheme and application id can be unspecified in URI. However, components
 * hierarchy must be obeyed - if higher component is not specified, then all lower components must
 * be unspecified.<br>
 * All described components can be directly accessed through methods.
 * 
 * <p>
 * URI is said to be <strong>complete</strong>, when all its elements are specified, except optional
 * path. Complete URI always points to concrete data space.
 * 
 * <p>
 * Instances of this class are comparable, in a way corresponding to described hierarchy,
 * <code>equals</code> and <code>hashCode</code> methods are also defined.
 * 
 * <p>
 * URI instances are created through dedicated factory methods or parsing factory. Instances of this
 * class are immutable, thread-safe.
 * 
 */
public final class DataSpacesURI implements Serializable, Comparable<DataSpacesURI> {

    /**
     * Scheme of Data Spaces URI.
     */
    public static final String SCHEME = "vfs:///";

    /**
     * Default input and output spaces name.
     */
    public static final String DEFAULT_IN_OUT_NAME = "default";

    /**
     * 
     */
    private static final long serialVersionUID = 7148704434729348732L;

    private static final Pattern PATTERN = Pattern
            .compile("^vfs:///(\\d+)(/(((input|output)(/(([^/]+)(/(.+)?)?)?)?)|scratch(/(([^/]+)((/(([^/]+)(/(.+)?)?)?)?)?)?)?)?)?$");

    /**
     * Creates URI with only application id being specified.
     * 
     * This method is only a shortcut for {@link #createURI(long, SpaceType)} with <code>null</code>
     * spaceType argument.
     * 
     * @param appId
     *            application id
     * @return URI for that specification
     */
    public static DataSpacesURI createURI(long appId) {
        return createURI(appId, null);
    }

    /**
     * Creates URI with only application id and type specified.
     * 
     * @param appId
     *            application id
     * @param spaceType
     *            space type. May be <code>null</code>.
     * @return URI for that specification
     */
    public static DataSpacesURI createURI(long appId, SpaceType spaceType) {
        return new DataSpacesURI(appId, spaceType, null, null, null, null);
    }

    /**
     * Creates URI of scratch type with only runtimeId specified.
     * 
     * This is only a shortcut for {@link #createScratchSpaceURI(long, String, String, String)} with
     * <code>null</code> values for nodeId and path.
     * 
     * @param appId
     *            application id
     * @param runtimeId
     *            runtimeId where scratch space is located. May be <code>null</code>.
     * @return URI for that specification
     */
    public static DataSpacesURI createScratchSpaceURI(long appId, String runtimeId) {
        return createScratchSpaceURI(appId, runtimeId, null);
    }

    /**
     * Creates URI of scratch type with only runtimeId and nodeId specified. Created URI is always
     * complete if arguments are not <code>null</code>.
     * 
     * This is only a shortcut for {@link #createScratchSpaceURI(long, String, String, String)} with
     * <code>null</code> value for path.
     * 
     * @param appId
     *            application id
     * @param runtimeId
     *            runtimeId where scratch space is located. May be <code>null</code> if following
     *            argument nodeId is also <code>null</code>.
     * @param nodeId
     *            nodeId (for node being on runtime with runtimeId) where scratch space is located.
     *            May be <code>null</code>.
     * @return URI for that specification
     * @throws IllegalArgumentException
     *             when <code>null</code> values in arguments do not obey URI components hierarchy
     *             requirements.
     */
    public static DataSpacesURI createScratchSpaceURI(long appId, String runtimeId, String nodeId) {
        return createScratchSpaceURI(appId, runtimeId, nodeId, null);
    }

    /**
     * Creates URI of scratch type. Created URI is always complete if arguments are not
     * <code>null</code>.
     * 
     * @param appId
     *            application id
     * @param runtimeId
     *            runtimeId where scratch space is located. May be <code>null</code> if following
     *            arguments are also <code>null</code>.
     * @param nodeId
     *            nodeId (for node being on runtime with runtimeId) where scratch space is located.
     *            May be <code>null</code> if following argument path is also <code>null</code>.
     * @param path
     *            path within data space. May be <code>null</code>.
     * @return URI for that specification
     * @throws IllegalArgumentException
     *             when <code>null</code> values in arguments do not obey URI components hierarchy
     *             requirements.
     */
    public static DataSpacesURI createScratchSpaceURI(long appId, String runtimeId, String nodeId, String path) {
        return new DataSpacesURI(appId, SpaceType.SCRATCH, null, runtimeId, nodeId, path);
    }

    /**
     * Creates URI of input or output type with only appId and name specified. Created URI is always
     * complete if arguments are not <code>null</code>.
     * 
     * This method is only a shortcut for
     * {@link #createInOutSpaceURI(long, SpaceType, String, String)} with <code>null</code> value
     * for argument path.
     * 
     * @param appId
     *            application id
     * @param spaceType
     *            space type - only {@link SpaceType#INPUT} or {@link SpaceType#OUTPUT} is allowed
     *            here. May be <code>null</code> if following argument name is also
     *            <code>null</code>.
     * @param name
     *            name of input. May be <code>null</code>.
     * @return URI for that specification
     * @throws IllegalArgumentException
     *             when <code>null</code> values in arguments do not obey URI components hierarchy
     *             requirements.
     */
    public static DataSpacesURI createInOutSpaceURI(long appId, SpaceType spaceType, String name) {
        return createInOutSpaceURI(appId, spaceType, name, null);
    }

    /**
     * Creates URI of input or output type. Created URI is always complete if arguments are not
     * <code>null</code>.
     * 
     * @param appId
     *            application id
     * @param spaceType
     *            space type - only {@link SpaceType#INPUT} or {@link SpaceType#OUTPUT} is allowed
     *            here. May be <code>null</code> if following arguments are also <code>null</code>.
     * @param name
     *            name of input. May be <code>null</code> if following path argument is also
     *            <code>null</code>.
     * @param path
     *            path within data space. May be <code>null</code>.
     * @return URI for that specification
     * @throws IllegalArgumentException
     *             when <code>null</code> values in arguments do not obey URI components hierarchy
     *             requirements.
     */
    public static DataSpacesURI createInOutSpaceURI(long appId, SpaceType spaceType, String name, String path) {
        return new DataSpacesURI(appId, spaceType, name, null, null, path);
    }

    /**
     * Parses string to URI instance.
     * 
     * Input string should conform rules mentioned in class description. Any valid URI string is
     * parsable. Scheme and application need to be always present in provided string, while other
     * components are optional.
     * 
     * End slash after last component (except path) is allowed, but not required. It is recommended
     * to not use it, as it is not used in URI canonical form returned by {@link #toString()}
     * method.
     * 
     * @param uri
     *            string with URI to parse
     * @return parsed URI
     * @throws MalformedURIException
     *             when provided string does not conform to URI format.
     */
    public static DataSpacesURI parseURI(String uri) throws MalformedURIException {
        final Matcher m = PATTERN.matcher(uri);
        if (!m.matches()) {
            throw new MalformedURIException("Unexpected URI format");
        }

        final String appIdString = m.group(1);
        final long appId;
        try {
            appId = Long.parseLong(appIdString);
        } catch (NumberFormatException x) {
            throw new MalformedURIException("Wrong application id format", x);
        }

        if (m.group(3) == null) {
            // just vfs:///123/
            return new DataSpacesURI(appId, null, null, null, null, null);
        }

        if (m.group(4) != null) {
            // vfs:///123/input/ OR vfs:///123/output/

            final String spaceTypeString = m.group(5).toUpperCase();
            // regexp patter guarantees correct space type enum name
            final SpaceType spaceType = SpaceType.valueOf(spaceTypeString);

            // both name and path may be null,
            // but hierarchy is guaranteed by the expression
            final String name = m.group(8);
            final String path = m.group(10);
            return new DataSpacesURI(appId, spaceType, name, null, null, path);
        } else {
            // vfs://123/scratch/

            // any of these can be null,
            // but hierarchy is guaranteed by the expression
            final String runtimeId = m.group(13);
            final String nodeId = m.group(17);
            final String path = m.group(19);
            return new DataSpacesURI(appId, SpaceType.SCRATCH, null, runtimeId, nodeId, path);
        }
    }

    private final long appId;

    private final SpaceType spaceType;

    private final String name;

    private final String runtimeId;

    private final String nodeId;

    private final String path;

    private DataSpacesURI(long appId, SpaceType spaceType, String name, String runtimeId, String nodeId,
            String path) {

        if ((spaceType == null && (name != null || runtimeId != null)) ||
            (runtimeId == null && nodeId != null) || (nodeId == null && name == null && path != null)) {
            throw new IllegalArgumentException(
                "Malformed URI. Provided arguments do not meet hierarchy consistency requirement.");
        }

        if ((spaceType == SpaceType.INPUT || spaceType == SpaceType.OUTPUT) && runtimeId != null) {
            throw new IllegalArgumentException("Malformed URI. Input/output can not have runtime id.");
        }

        if (spaceType == SpaceType.SCRATCH && name != null) {
            throw new IllegalArgumentException("Malformed URI. Scratch can not have name.");
        }

        this.appId = appId;
        this.spaceType = spaceType;
        this.name = name;
        this.runtimeId = runtimeId;
        this.nodeId = nodeId;
        this.path = path;
    }

    /**
     * @return application id
     */
    public long getAppId() {
        return appId;
    }

    /**
     * @return space type. May be <code>null</code>.
     */
    public SpaceType getSpaceType() {
        return spaceType;
    }

    /**
     * @return name for input and output spaces. May be <code>null</code> for input/output, is
     *         <code>null</code> for scratch or undefined space type.
     */
    public String getName() {
        return name;
    }

    /**
     * @return runtimeId for scratch space. May be <code>null</code> for scratch, is
     *         <code>null</code> for input/output or undefined space type.
     */
    public String getRuntimeId() {
        return runtimeId;
    }

    /**
     * @return nodeId for scratch space. May be <code>null</code> for scratch, is <code>null</code>
     *         for input/output or undefined space type.
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * @return path within space. May be <code>null</code>.
     */
    public String getPath() {
        return path;
    }

    /**
     * Checks whether URI is completely defined, i.e. points to concrete data space.
     * 
     * Complete URI has space type and name (or runtimeId/NodeId in case of scratch) defined. Path
     * specification is optional.
     * 
     * @return <code>true</code> if this URI is completely defined. <code>false</code> otherwise.
     */
    public boolean isComplete() {
        return spaceType != null && (name != null || (runtimeId != null && nodeId != null));
    }

    /**
     * Creates copy of this URI with new path specified.
     * 
     * @param path
     *            path to set in newly created URI. May be <code>null</code>. Can be non-null and
     *            nonempty String if this URI is completely defined.
     * @return copy of this URI with provided path set.
     * @throws IllegalStateException
     *             when nonempty path was requested for incomplete URI definition.
     * @see #isComplete()
     */
    public DataSpacesURI withPath(String path) {
        if (this.path == path || (path != null && this.path != null && path.equals(this.path))) {
            return this;
        }
        if (path != null && path.length() > 0 && !isComplete()) {
            throw new IllegalStateException("only complete URIs can have path");
        }
        return new DataSpacesURI(appId, spaceType, name, runtimeId, nodeId, path);
    }

    /**
     * Generates next URI key (in sense of comparator of this class) in the same path level. This is
     * method for <strong>internal usage only</strong>.
     * 
     * @return next URI key, in the same path level.
     * @throws IllegalStateException
     *             if this URI is complete.
     */
    public DataSpacesURI nextURI() {
        long newAppId = this.appId;
        SpaceType newSpaceType = this.spaceType;
        String newName = this.name;
        String newRuntimeId = this.runtimeId;
        String newNodeId = this.nodeId;

        // case: appid/type/name/
        // case: appid/type/rt/node/
        if (isComplete())
            throw new IllegalStateException("Source key uri is complete. Doesn't make sens, giving up.");

        // case: appid/ - just ++
        if (newSpaceType == null) {
            newAppId++;
            return new DataSpacesURI(newAppId, newSpaceType, newName, newRuntimeId, newNodeId, null);
        }

        // case: appid/SCRATCH/rt/ - just build next rt string
        if (newSpaceType == SpaceType.SCRATCH && newRuntimeId != null) {
            newRuntimeId = newRuntimeId + '\0';
            return new DataSpacesURI(newAppId, newSpaceType, newName, newRuntimeId, newNodeId, null);
        }

        // case: appid/type/ - paste a next type
        newSpaceType = newSpaceType.succ();

        // case: appid/last_type/ - there was no next type?
        if (newSpaceType == null)
            newAppId++;

        return new DataSpacesURI(newAppId, newSpaceType, newName, newRuntimeId, newNodeId, null);
    }

    /**
     * Returns string representation of this URI. This string may be directly used by user-level
     * code.
     * 
     * Returned URI does not have end slash.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(SCHEME);

        sb.append(Long.toString(appId));

        if (spaceType == null) {
            return sb.toString();
        }
        sb.append('/');
        sb.append(spaceType.getDirectoryName());

        switch (spaceType) {
            case INPUT:
            case OUTPUT:
                if (name == null) {
                    return sb.toString();
                }
                sb.append('/');
                sb.append(name);
                break;
            case SCRATCH:
                if (runtimeId == null) {
                    return sb.toString();
                }
                sb.append('/');
                sb.append(runtimeId);

                if (nodeId == null) {
                    return sb.toString();
                }
                sb.append('/');
                sb.append(nodeId);
                break;
            default:
                throw new IllegalStateException("Unexpected space type");
        }

        if (path != null) {
            sb.append('/');
            sb.append(path);
        }

        return sb.toString();
    }

    public int compareTo(DataSpacesURI other) {
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
            return path.compareTo(other.path);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof DataSpacesURI)) {
            return false;
        }

        DataSpacesURI other = (DataSpacesURI) obj;
        return compareTo(other) == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (appId ^ (appId >>> 32));
        result = prime * result + ((spaceType == null) ? 0 : spaceType.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((runtimeId == null) ? 0 : runtimeId.hashCode());
        result = prime * result + ((nodeId == null) ? 0 : nodeId.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }
}
