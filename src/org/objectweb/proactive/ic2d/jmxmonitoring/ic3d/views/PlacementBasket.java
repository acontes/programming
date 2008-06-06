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
        double x = Math.cos(figureIndex) * Math.sqrt(figureIndex)*GeometryBasket.FIGURE_SCALE;
        double y = Math.sin(figureIndex) * Math.sqrt(figureIndex)*GeometryBasket.FIGURE_SCALE;
        figure.placeSubFigure(figure, x, y, 0);
    }

    /**
     * This method places a subfigure on the x axis with the 
     * specified spacing and with a padding on the y axis (relative
     * to the parent figure). It is positioned relative
     * to the parent figure specified.  
     * @param spacing  x coord spacing between subfigures  - between [0, 1]
     * @param padding	y coord padding - between [0, 1]
     * @param height	z height  multiples of {@link GeometryBasket#FIGURE_SCALE}
     * @param figureIndex the index of the figure in the list of figures
     * @param figures	the total number of figures
     * @param figure	the figure to be added
     * @param parentFigure	the parentFigure
     */
    public static void xArrangement(double spacing, double padding, double height,
        int figureIndex, int figures, AbstractFigure3D figure, AbstractFigure3D parentFigure) {
    	//the scale operations are relative to the 
    	//GeometryBasket.FIGURE_SCALE because the object gets
    	//resized automatically to the size of the parent as it is 
    	//connected to the transforms of the parent
    	Vector3d newScale = new Vector3d(1, 1, 1);
        Transform3D figureTransforms = new Transform3D();
        
        //2. (the figure width is the width of parent - 2*pading(%) -
        // - (nrfigures - 1) * spacing %) / nr of figures  
        double subFigureWidth = (1 - 2* padding -(figures-1) * spacing) / figures;

        newScale.x = subFigureWidth;  //x size
        newScale.y = 1 -  padding * 2;//y size //oldScale.y * (1-padding*2)*GeometryBasket.FIGURE_SCALE; //*X* length - slightly smaller
        newScale.z = height;  //GeometryBasket.FIGURE_SCALE; //height - slightly bigger so it's visible z size
        
        TransformGroup moveOld;
        moveOld = figure.getRootTransform();
        moveOld.setTransform(figureTransforms);
    	figureTransforms.setScale(newScale);

        //translation has to been done in relation to the global sizes (they are absolute values not relative values)
    	//therefore we use GeometryBasket.FIGURE_SCALE 
    	
    	Vector3d totalScale  = new Vector3d(1,1,1);
    	Transform3D totalTransform = new Transform3D();
    	figure.getLocalToVworld(totalTransform);
    	totalTransform.getScale(totalScale);
    	
    	figureTransforms.setTranslation(new Vector3d(
				GeometryBasket.FIGURE_SCALE * (padding+ 
						(figureIndex -1 )* subFigureWidth+ 
							(figureIndex -1 )* spacing ),
							GeometryBasket.FIGURE_SCALE *padding,	//connected to the scale above   *X*
				0));

//    	figureTransforms.setTranslation(new Vector3d(
//    						GeometryBasket.FIGURE_SCALE * (padding*totalScale.x + 
//    								(figureIndex -1 )* subFigureWidth * totalScale.x+ 
//    									(figureIndex -1 )* spacing * totalScale.x),
//    									GeometryBasket.FIGURE_SCALE *padding*totalScale.y,	//connected to the scale above   *X*
//      					0));
    	moveOld.setTransform(figureTransforms);
        
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
    	//the scale operations are relative to the 
    	//GeometryBasket.FIGURE_SCALE because the object gets
    	//resized automatically to the size of the parent as it is 
    	//connected to the transforms of the parent
    	Vector3d newScale = new Vector3d(1, 1, 1);
        Transform3D figureTransforms = new Transform3D();
        
        //2. (the figure width is the width of parent (%)- 2*pading(%) -
        // - (nrfigures - 1) * spacing %) / nr of figures  
        double subFigureWidth = (1 - 2* padding -(figures-1) * spacing) / figures;

        newScale.x =  1 -  padding * 2;//y size //oldScale.y * (1-padding*2)*GeometryBasket.FIGURE_SCALE; //*X* length - slightly smaller
        newScale.y = subFigureWidth;  //y size
        newScale.z = height;  //GeometryBasket.FIGURE_SCALE; //height - slightly bigger so it's visible z size
        
        TransformGroup moveOld;
        moveOld = figure.getRootTransform();
        moveOld.setTransform(figureTransforms);
    	figureTransforms.setScale(newScale);

        //translation has to been done in relation to the global sizes (they are absolute values not relative values)
    	//therefore we use GeometryBasket.FIGURE_SCALE 
    	
    	Vector3d totalScale  = new Vector3d(1,1,1);
    	Transform3D totalTransform = new Transform3D();
    	figure.getLocalToVworld(totalTransform);
    	totalTransform.getScale(totalScale);
       	figureTransforms.setTranslation(new Vector3d(
				GeometryBasket.FIGURE_SCALE *padding,	//connected to the scale above   *X*
					GeometryBasket.FIGURE_SCALE * (padding+ 
							(figureIndex -1 )* subFigureWidth + 
								(figureIndex -1 )* spacing),
					0));
//    	figureTransforms.setTranslation(new Vector3d(
//    					GeometryBasket.FIGURE_SCALE *padding*totalScale.x,	//connected to the scale above   *X*
//    						GeometryBasket.FIGURE_SCALE * (padding*totalScale.y + 
//    								(figureIndex -1 )* subFigureWidth * totalScale.y+ 
//    									(figureIndex -1 )* spacing * totalScale.y),
//      					0));
    	moveOld.setTransform(figureTransforms);

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
    //to position the figures in the center we need 
    //to get the scale up to the parent (the sum of all
    //transformations) and the         GeometryBasket.FIGURE_SCALE
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
        //invert the scale (so the size is  GeometryBasket.FIGURE_SCALE)and apply the
        //scale passed as a parameter
        newScale.x = scale* 1/oldScale.x; 
        newScale.y = scale*1/oldScale.y;
        newScale.z = scale*1/oldScale.z;
        
        TransformGroup moveOld;
    	parentFigurePosition.setScale(newScale);
        parentFigurePosition.setTranslation(new Vector3d(
        		GeometryBasket.FIGURE_SCALE/2,	
        		GeometryBasket.FIGURE_SCALE/2,
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
            TransformGroup rot = (TransformGroup)figure.getParent();
            Transform3D t3d = new Transform3D();
            rot.getTransform(t3d);
            t3d.rotY(alpha);
            t3d.rotX(beta);
            rot.setTransform(t3d);
            //make the x and y proportional
            x = x*Math.sin(beta);
            y = y*Math.sin(beta);
            figure.placeSubFigure(figure, x, y, z);
            
            
            
            
            
        }

}
