package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed.AbstractGrid3D;


/**
 * All representation of a grid should extend this class. Usually the grid is an
 * invisible figure that contains the hosts. However there may be cases where a
 * some geometry for the grid might be desirable.
 * 
 * @author vjuresch
 * 
 */
public abstract class AbstractLoadGrid3D extends AbstractGrid3D {
    /**
     * @param name - name of the figure, for a grid is usually empty 
     */
    public AbstractLoadGrid3D(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

}
