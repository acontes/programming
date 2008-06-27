package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.loadmonitoring;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D;


/**
 * All representation of a grid should extend this class. Usually the grid is an
 * invisible figure that contains the hosts. However there may be cases where a
 * some geometry for the grid might be desirable.
 * 
 * @author vjuresch
 * 
 */
public abstract class AbstractLoadRuntime3D extends AbstractFigure3D {
    /**
     * @param name - name of the figure, for a grid is usually empty 
     */
    public AbstractLoadRuntime3D(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }
    
}
