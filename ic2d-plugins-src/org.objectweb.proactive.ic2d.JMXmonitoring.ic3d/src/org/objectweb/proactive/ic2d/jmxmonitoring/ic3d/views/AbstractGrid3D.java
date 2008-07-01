package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.util.Hashtable;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets.SiteBasket;



/**
 * All representation of a grid should extend this class. Usually the grid is an
 * invisible figure that contains the hosts. However there may be cases where a
 * some geometry for the grid might be desirable.
 * 
 * @author vjuresch
 * 
 */
public abstract class AbstractGrid3D extends AbstractFigure3D {
	
	protected final Hashtable<String, Integer> hostSites = new Hashtable<String, Integer>();
	
	/**
     * @param name - name of the figure, for a grid is usually empty 
     */
    public AbstractGrid3D(String name) {
        super(name);
    }

    public void setTranslation(Vector3d translation) {
        TransformGroup translate = this.getTranslateScaleTransform();
        Transform3D transform3d = new Transform3D();
        translate.getTransform(transform3d);
        transform3d.setTranslation(translation);
        translate.setTransform(transform3d);
    }
    
    /**
     * This method removes a subfigure for the caller figure. It also removes
     * all the children for the subfigure BEFORE removing the actual subfigure.
     * 
     * @param figure
     */
    public void removeSubFigure(final Figure3D figure) {
    	// get the branch: figure ->rotTG -> TG->particularBG -> *figures BG*
        final BranchGroup generalBranch = figure.getRootBranch();
        // get the parent branch (the subfigure branch of the superfigure)
        final BranchGroup superBranch = (BranchGroup) generalBranch.getParent();

        // tell the subfigure to remove all the subsubfigures
        figure.removeAllSubFigures();
        generalBranch.detach();
        // superBranch.removeAllChildren();
        // FIXME - superBranch should never be null !!!!
        if (superBranch != null) {
            superBranch.removeChild(generalBranch);
        }
        // remove from the hashtable
        this.figures.values().remove(figure);
        this.arrangeSubFigures();
        
        // maintain the site hashtable
        final String site = SiteBasket.getSite(figure.getFigureName());
        Integer siteHostCount = hostSites.get(site);
        if(siteHostCount == null) {
        	return;
        }
        else if(siteHostCount == 1) {
        	hostSites.remove(site);
        }
        else {
        	siteHostCount--;
        	hostSites.put(site, siteHostCount);
        }
    }
    
    /**
     * Adds a subfigure to the subFigures BranchGroup.
     * 
     * @param key
     *            the key of the figure (should be unique)
     * @param figure
     *            the Abstract3DFigure to be added
     */
    public void addSubFigure(final String key, final Figure3D figure) {
       // gets the main branch group of the subFigure
        // and it adds it to the subFigures BranchGroup

        // figure ->rotTG ->generalTG-> particularBG -> rootBG (from the
        // constructor)
        final BranchGroup root = figure.getRootBranch();

        root.compile();
        // add the figure
        this.subFiguresBranch.addChild(root);
        this.figures.put(key, figure);
        
        // maintain the site hashtable
        final String site = SiteBasket.getSite(figure.getFigureName());
        Integer siteHostCount = hostSites.get(site);
        if(siteHostCount == null) {
        	hostSites.put(site, 1);
        }
        else {
        	hostSites.put(site, siteHostCount + 1);
        }
        
        // update the view
        this.arrangeSubFigures();
        // animate the creation
        figure.animateCreation();
        
        
    }
    
    /**
     * Removes a subfigure by key. This method calls
     * removeSubFigure(AbstractFigure3D figure).
     * 
     * @param key
     */
    public void removeSubFigure(final String key) {
        this.removeSubFigure(this.figures.get(key));
    }
}
