/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

/**
 * @author vjuresch
 *
 */
public class Arrow3D extends AbstractArrow3D {
    public Arrow3D(String name, Vector3f start, Vector3f stop) {
        super(name);
        // TODO Auto-generated constructor stub
        this.setGeometry(createGeometry(start, stop));
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
     */
    @Override
    public void arrangeSubFigures() {
        // TODO Auto-generated method stub
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
     */
    @Override
    protected Appearance createAppearance() {
        return AppearanceBasket.defaultLineAppearance;
    }

    /* (non-Javadoc)
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
     */
    @Override
    protected Geometry createGeometry() {
        return null;
    }

    private Geometry createGeometry(Vector3f start, Vector3f stop) {
    	return GeometryBasket.getDefaultLineGeometry(start, stop);
    }

    @Override
    protected TransformGroup createTextBranch() {
        // TODO Auto-generated method stub
        return null;
    }

	/* (non-Javadoc)
	 * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
	 */
	@Override
	protected void animateCreation() {
		// TODO Auto-generated method stub
		
	}
}
