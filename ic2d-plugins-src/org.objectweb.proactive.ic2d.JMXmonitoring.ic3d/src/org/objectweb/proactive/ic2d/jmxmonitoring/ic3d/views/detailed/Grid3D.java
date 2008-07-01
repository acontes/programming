/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.detailed;

import java.util.Hashtable;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.FigureType;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.PlacementBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.SiteBasket;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractGrid3D;
import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.Figure3D;


/**
 * @author vjuresch
 * 
 */
public class Grid3D extends AbstractGrid3D {
	
	public Grid3D() {
        super("");
    }

    // add a new host and translate its
    // position according to the coordinates
    // (add to a custom location)

    // TODO ***HORRIBLE PLACING ALGORITHM*** MUST BE CHANGED !!! the cost is n^2
    // * method calls in depth
    // for each host or removed placed, the entire scene gets rearranged

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#arrangeSubFigures()
     */
    @Override
    public void arrangeSubFigures() {
    	//for (final Figure3D site : this.getSubFigures().values()) {
		//	site.arrangeSubFigures();
		//}
    	Hashtable<String, Integer> countedHosts = new Hashtable<String, Integer>();
    	for (final Figure3D host : this.getSubFigures().values()) {
    		final String site = SiteBasket.getSite(host.getFigureName());
            Integer siteHostCount = hostSites.get(site);
            Integer siteHostIndex = countedHosts.get(site);
            if(siteHostIndex == null) {
            	siteHostIndex = 1;
            	countedHosts.put(site, siteHostIndex);
            }
            else {
            	siteHostIndex++;
            	countedHosts.put(site, siteHostIndex );
            }
            
            PlacementBasket.matrixGridArrangement(siteHostIndex, host, siteHostCount, SiteBasket.getFlatLocation(host.getFigureName()));
            host.arrangeSubFigures();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createGeometry()
     */
    @Override
    protected Geometry createGeometry() {
        //return GeometryBasket.getCoordinatesGeometry();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createTextBranch()
     */
    @Override
    protected TransformGroup createTextBranch() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#animateCreation()
     */
    @Override
    public void animateCreation() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#createAppearance()
     */
    @Override
    protected Appearance createAppearance() {
        //return AppearanceBasket.coordinatesAppearance;
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.AbstractFigure3D#setArrow(javax.vecmath.Vector3f,
     *      javax.vecmath.Vector3f)
     */
    @Override
    protected Figure3D setArrow(final String name, final Vector3f start, final Vector3f stop) {
        // TODO Auto-generated method stub
        return new Arrow3D("", start, stop);
    }

	@Override
	public FigureType getType() {
		// TODO Auto-generated method stub
		return FigureType.GRID;
	}
}
