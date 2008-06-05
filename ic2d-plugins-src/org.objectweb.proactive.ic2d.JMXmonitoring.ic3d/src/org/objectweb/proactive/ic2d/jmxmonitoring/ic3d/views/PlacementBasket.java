/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;


import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;


/**
 * THis class holds the placement strategies for
 * the figures. 
 *  <br/>
 *  WORK IN PROGRESS
 * @author vjuresch
 *
 */
public class PlacementBasket {
    /**
     * Arranges figures in spiral 
     * @param figureIndex  the number of the figure  to placed, it is needed to know where to place the figure in the spiral
     * @param figure the figure to be placed
     */
    public static void spiralArrangement(int figureIndex,
        AbstractFigure3D figure) {
        double x = Math.cos(figureIndex) * Math.sqrt(figureIndex);
        double y = Math.sin(figureIndex) * Math.sqrt(figureIndex);
        figure.placeSubFigure(figure, x, y, 0);
    }

    /**
     * This method places a subfigure on the x axis with the 
     * specified spacing and with a padding on the y axis (relative
     * to the parent figure). It is positioned relative
     * to the parent figure specified.  
     * @param spacing  x coord spacing between subfigures  - between [0, 1]
     * @param padding	y coord padding - between [0, 1]
     * @param figureIndex the index of the figure in the list of figures
     * @param figures	the total number of figures
     * @param figure	the figure to be added
     * @param parentFigure	the parentFigure
     */
    public static void xArrangement(double spacing, double padding, double height,
        int figureIndex, int figures, AbstractFigure3D figure, AbstractFigure3D parentFigure) {
        
    	Vector3d newScale = new Vector3d(1, 1, 1);
        Transform3D parentFigurePosition = new Transform3D();

        double subFigureWidth = (1 -
            ((figures + 1) * spacing)) / figures;

        newScale.x = subFigureWidth;
        newScale.y = 1-padding*2;//oldScale.y * (1-padding*2)*GeometryBasket.FIGURE_SCALE; //*X* length - slightly smaller
        newScale.z = height;  //GeometryBasket.FIGURE_SCALE; //height - slightly bigger so it's visible

        TransformGroup moveOld;
        
    	parentFigurePosition.setScale(newScale);
        parentFigurePosition.setTranslation(new Vector3d(
      					spacing * figureIndex + subFigureWidth * (figureIndex - 1),
      					padding,	//connected to the scale above   *X*
      					0));
        moveOld = (TransformGroup) figure.getParent().getParent();
        moveOld.setTransform(parentFigurePosition);

    }

    
    /**
     * This method places a subfigure on the y axis with the 
     * specified spacing and with a padding on the x axis (relative
     * to the parent figure). It is positioned relative
     * to the parent figure specified.  
     * @param spacing  y coord spacing between subfigures	- between [0, 1]
     * @param padding	x coord padding - between [0, 1]
     * @param figureIndex the index of the figure in the list of figures
     * @param figures	the total number of figures
     * @param figure	the figure to be added
     * @param parentFigure	the parentFigure
     */
    public static void yArrangement(double spacing,double padding, double height,
        int figureIndex, int figures, AbstractFigure3D figure, AbstractFigure3D parentFigure) {
        
    	Vector3d newScale = new Vector3d(1, 1, 1);
        Transform3D parentFigurePosition = new Transform3D();
        
        double subFigureWidth = (1 -
            ((figures + 1) * spacing)) / figures;

        newScale.x = (1-padding*2); //*X* length - slightly smaller
        newScale.y = subFigureWidth;
        newScale.z =  height; //height - slightly bigger so it's visible

        TransformGroup moveOld;
        
    	parentFigurePosition.setScale(newScale);
        parentFigurePosition.setTranslation(new Vector3d(
					padding,	//connected to the scale above   *X*
        			spacing * figureIndex + subFigureWidth * (figureIndex - 1),
                	0));
        moveOld = (TransformGroup) figure.getParent().getParent();
        moveOld.setTransform(parentFigurePosition);

    }
    
    /**
     * Positions active object figures vertically over the node. 
     * The ao is rescaled so it is GeometryBasket.FIGURE_SCALE size
     * and then resized through scale.  
     * @param scale		size scale of the figure after resizing to GeometryBasket.FIGURE_SCALE (1 means no rescale)
     * @param spacing	spacing between figures
     * @param figureIndex	the index of the figure in the list of figures
     * @param figures	number of figures
     * @param figure	the actual figure
     * @param parentFigure	the parent figure
     */
    //TODO positioning is messed up, needs fixing
    public static void sphericalAOVerticalArrangement(double scale, double spacing,
        int figureIndex, int figures, AbstractFigure3D figure, AbstractFigure3D parentFigure) {
        
    	Vector3d newScale = new Vector3d(1, 1, 1);
    	//gets the total transform up to the ao and rescales it to 
    	//a spherical shape (divide by 1/oldScale) than rescales to scale size
    	Vector3d oldScale = new Vector3d();
        Transform3D parentFigurePosition = new Transform3D();
        Transform3D oldScaleT = new Transform3D();
        parentFigure.getLocalToVworld(oldScaleT);
        oldScaleT.getScale(oldScale);
        newScale.x =scale* 1/oldScale.x; //*X* length - slightly smaller
        newScale.y = scale*1/oldScale.y;
        newScale.z = scale*1/oldScale.z; //height - slightly bigger so it's visible
        
        TransformGroup moveOld;
    	parentFigurePosition.setScale(newScale);
        parentFigurePosition.setTranslation(new Vector3d(
					oldScale.x/2,	
					oldScale.y/2,
                	spacing*figureIndex ));
        moveOld = (TransformGroup) figure.getParent().getParent();
        moveOld.setTransform(parentFigurePosition);

    }

    
    /**
     * Places the figure on a sphere of radius
     * radius. The host is placed according to 
     * the two angles in radians.
     * 
     * @param alpha 	angle 
     * @param beta		angle
     * @param figure	figure to be placed 
     */
    public static void sphereArrangement(double alpha,double beta,
            AbstractFigure3D figure) {
    		
    	
    	
    		final double RADIUS = 16;
            //get the coordinates in a plane 
    		double x = Math.cos(alpha) * RADIUS;
            double y = Math.sin(alpha) * RADIUS;
            
            //get the z coordinate
            double z = Math.cos(beta)* RADIUS;
            
           //rotate
//            TransformGroup rot = (TransformGroup)figure.getParent();
//            Transform3D t3d = new Transform3D();
//            rot.getTransform(t3d);
//            t3d.rotX(alpha);
//            t3d.rotZ(beta);
//            rot.setTransform(t3d);
            //make the x and y proportional
            x = x*Math.sin(beta);
            y = y*Math.sin(beta);
            figure.placeSubFigure(figure, x, y, z);
            
            
            
            
            
        }

}
