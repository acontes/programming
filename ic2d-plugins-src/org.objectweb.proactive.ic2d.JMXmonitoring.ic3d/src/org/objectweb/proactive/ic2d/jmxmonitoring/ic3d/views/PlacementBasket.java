/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.proearth.EarthGrid3D;


/**
 * THis class holds the placement strategies for the figures. <br/> WORK IN
 * PROGRESS
 * 
 * @author vjuresch
 * 
 */
public class PlacementBasket {
	/**
	 * Arranges figures in spiral
	 * 
	 * @param figureIndex
	 *            the number of the figure to placed, it is needed to know where
	 *            to place the figure in the spiral
	 * @param figure
	 *            the figure to be placed
	 */
	public static void spiralArrangement(final int figureIndex,
			final Figure3D figure) {
		final double x = Math.cos(figureIndex) * Math.sqrt(figureIndex)
				* GeometryBasket.FIGURE_SCALE;
		final double z = Math.sin(figureIndex) * Math.sqrt(figureIndex)
				* GeometryBasket.FIGURE_SCALE;
		figure.placeSubFigure(figure, x, 0, z);
	}

	/**
	 * Arranges figures in matrix You can find index of the figure with:
	 * 
	 * <br/> x == y -> idx = x^2,
	 * 
	 * <br/> ( x < y ) -> y^2 - y + x,
	 * 
	 * <br/> ( x > y ) -> x^2 -2x + y <br/>
	 * 
	 * 
	 * 
	 * @param figureIndex
	 * @param figure
	 */
	public static void matrixArrangement(final int figureIndex,
			final Figure3D figure) {
		/* Setting up our variables */
		double x, z; /* Our host 2D placement */
		int figureIndexSquareRoot, c;
		/* Checking parameters */
		if (figureIndex < 1) {
			throw new IllegalArgumentException(
					"The figure index must be larger than 0");
		}
		figureIndexSquareRoot = (int) Math.sqrt(figureIndex);
		c = figureIndexSquareRoot + 1;
		/* Short sample of the 2D placement policy */
		/*
		 * Could have been easier but maintains an order relation. Moreover you
		 * can find the index with the 2D coordinates.
		 * 
		 * 1 2 5 10 17 3 4 6 11 18 7 8 9 12 19 13 14 15 16 20 21 22 23 24 25
		 */

		/* Our index is a square so x and y coordinates are the same */
		if (figureIndexSquareRoot * figureIndexSquareRoot == figureIndex) {
			x = figureIndexSquareRoot;
			z = figureIndexSquareRoot;
		}
		/*
		 * Our index is lower or equal than the middle of next and previous
		 * squares (N)
		 */
		else if (figureIndex <= (figureIndexSquareRoot * figureIndexSquareRoot + c
				* c) / 2) {
			x = c;
			z = Math.abs(figureIndexSquareRoot * figureIndexSquareRoot
					- figureIndex);
		}
		/* The index is greater than the middle of next and previous squares (N) */
		else {
			x = c - Math.abs(c * c - figureIndex);
			z = c;
		}
		figure.placeSubFigure(figure, x, 0, z);
	}

	/**
	 * This method places a subfigure on the x axis with the specified spacing
	 * and with a padding on the y axis (relative to the parent figure). It is
	 * positioned relative to the parent figure specified.
	 * 
	 * @param spacing
	 *            x coord spacing between subfigures - between [0, 1]
	 * @param padding
	 *            y coord padding - between [0, 1]
	 * @param height
	 *            z height multiples of {@link GeometryBasket#FIGURE_SCALE}
	 * @param figureIndex
	 *            the index of the figure in the list of figures
	 * @param figures
	 *            the total number of figures
	 * @param figure
	 *            the figure to be added
	 * @param parentFigure
	 *            the parentFigure
	 */
	public static void xArrangement(final double spacing, final double padding,
			final double height, final int figureIndex, final int figures,
			final Figure3D figure, final Figure3D parentFigure) {
		// the scale operations are relative to the
		// GeometryBasket.FIGURE_SCALE because the object gets
		// resized automatically to the size of the parent as it is
		// connected to the transforms of the parent
		final Vector3d newScale = new Vector3d(1, 1, 1);
		final Transform3D figureTransforms = new Transform3D();

		// 2. (the figure width is the width of parent - 2*pading(%) -
		// - (nrfigures - 1) * spacing %) / nr of figures
		final double subFigureWidth = (1 - 2 * padding - (figures - 1)
				* spacing)
				/ figures;

		// x size
		newScale.x = subFigureWidth;
		// y size //oldScale.y * (1-padding*2)*GeometryBasket.FIGURE_SCALE;
		// //*X* length - slightly smaller
		newScale.z = 1 - padding * 2;
		// GeometryBasket.FIGURE_SCALE; //height - slightly bigger so it's
		// visible z size
		newScale.y = height;

		TransformGroup moveOld;
		moveOld = figure.getTranslateScaleTransform();
		moveOld.setTransform(figureTransforms);
		figureTransforms.setScale(newScale);

		// translation has to been done in relation to the global sizes (they
		// are absolute values not relative values)
		// therefore we use GeometryBasket.FIGURE_SCALE

		final Vector3d totalScale = new Vector3d(1, 1, 1);
		final Transform3D totalTransform = figure.getCoordinates();
		totalTransform.getScale(totalScale);

		figureTransforms
				.setTranslation(new Vector3d(
						GeometryBasket.FIGURE_SCALE
								* (padding + (figureIndex - 1) * subFigureWidth + (figureIndex - 1)
										* spacing), 0,
						GeometryBasket.FIGURE_SCALE * padding // connected to
				// the scale
				// above *X*
				));

		// figureTransforms.setTranslation(new Vector3d(
		// GeometryBasket.FIGURE_SCALE * (padding*totalScale.x +
		// (figureIndex -1 )* subFigureWidth * totalScale.x+
		// (figureIndex -1 )* spacing * totalScale.x),
		// GeometryBasket.FIGURE_SCALE *padding*totalScale.y, //connected to the
		// scale above *X*
		// 0));
		moveOld.setTransform(figureTransforms);

	}

	/**
	 * This method places a subfigure on the y axis with the specified spacing
	 * and with a padding on the x axis (relative to the parent figure). It is
	 * positioned relative to the parent figure specified.
	 * 
	 * @param spacing
	 *            y coord spacing between subfigures - between [0, 1]
	 * @param padding
	 *            x coord padding - between [0, 1]
	 * @param figureIndex
	 *            the index of the figure in the list of figures
	 * @param figures
	 *            the total number of figures
	 * @param figure
	 *            the figure to be added
	 * @param parentFigure
	 *            the parentFigure
	 */

	public static void yArrangement(final double spacing, final double padding,
			final double height, final int figureIndex, final int figures,
			final Figure3D figure, final Figure3D parentFigure) {
		// the scale operations are relative to the
		// GeometryBasket.FIGURE_SCALE because the object gets
		// resized automatically to the size of the parent as it is
		// connected to the transforms of the parent
		final Vector3d newScale = new Vector3d(1, 1, 1);
		final Transform3D figureTransforms = new Transform3D();

		// 2. (the figure width is the width of parent (%)- 2*pading(%) -
		// - (nrfigures - 1) * spacing %) / nr of figures
		final double subFigureWidth = (1 - 2 * padding - (figures - 1)
				* spacing)
				/ figures;

		newScale.x = 1 - padding * 2;// y size //oldScale.y *
		// (1-padding*2)*GeometryBasket.FIGURE_SCALE;
		// //*X* length - slightly smaller
		newScale.z = subFigureWidth; // y size
		newScale.y = height; // GeometryBasket.FIGURE_SCALE; //height -
		// slightly bigger so it's visible z size

		TransformGroup moveOld;
		moveOld = figure.getTranslateScaleTransform();
		moveOld.setTransform(figureTransforms);
		figureTransforms.setScale(newScale);

		// translation has to been done in relation to the global sizes (they
		// are absolute values not relative values)
		// therefore we use GeometryBasket.FIGURE_SCALE

		final Vector3d totalScale = new Vector3d(1, 1, 1);
		final Transform3D totalTransform = figure.getCoordinates();

		totalTransform.getScale(totalScale);
		figureTransforms
				.setTranslation(new Vector3d(
						GeometryBasket.FIGURE_SCALE * padding, // connected
						// to
						// the
						// scale
						// above
						// *X*
						0,
						GeometryBasket.FIGURE_SCALE
								* (padding + (figureIndex - 1) * subFigureWidth + (figureIndex - 1)
										* spacing)));
		// figureTransforms.setTranslation(new Vector3d(
		// GeometryBasket.FIGURE_SCALE *padding*totalScale.x, //connected to the
		// scale above *X*
		// GeometryBasket.FIGURE_SCALE * (padding*totalScale.y +
		// (figureIndex -1 )* subFigureWidth * totalScale.y+
		// (figureIndex -1 )* spacing * totalScale.y),
		// 0));
		moveOld.setTransform(figureTransforms);

	}

	/**
	 * Positions active object figures vertically over the node. The ao is
	 * rescaled so it is GeometryBasket.FIGURE_SCALE size and then resized
	 * through scale.
	 * 
	 * @param scale
	 *            size scale of the figure after resizing to
	 *            GeometryBasket.FIGURE_SCALE (1 means no rescale)
	 * @param spacing
	 *            spacing between figures
	 * @param figureIndex
	 *            the index of the figure in the list of figures
	 * @param figures
	 *            number of figures
	 * @param figure
	 *            the actual figure
	 * @param parentFigure
	 *            the parent figure
	 */
	// TODO positioning is messed up, needs fixing
	// to position the figures in the center we need
	// to get the scale up to the parent (the sum of all
	// transformations) and the GeometryBasket.FIGURE_SCALE
	public static void sphericalAOVerticalArrangement(final double scale,
			final double spacing, final int figureIndex, final int figures,
			final Figure3D figure, final Figure3D parentFigure) {

		final Vector3d newScale = new Vector3d(1, 1, 1);
		// gets the total transform up to the ao and rescales it to
		// a spherical shape (divide by 1/oldScale) than rescales to scale size
		final Vector3d oldScale = new Vector3d();
		final Transform3D parentFigurePosition = new Transform3D();
		final Transform3D oldScaleT = parentFigure.getCoordinates();
		oldScaleT.getScale(oldScale);
		// invert the scale (so the size is GeometryBasket.FIGURE_SCALE)and
		// apply the
		// scale passed as a parameter
		newScale.x = scale * 1 / oldScale.x;
		newScale.y = scale * 1 / oldScale.y;
		newScale.z = scale * 1 / oldScale.z;

		TransformGroup moveOld;
		parentFigurePosition.setScale(newScale);
		parentFigurePosition.setTranslation(new Vector3d(
				GeometryBasket.FIGURE_SCALE / 2, spacing * figureIndex,
				GeometryBasket.FIGURE_SCALE / 2));
		moveOld = figure.getTranslateScaleTransform();
		moveOld.setTransform(parentFigurePosition);

	}

    /**
     * Places the figure on a sphere of EARTH_RADIUS
     * EARTH_RADIUS. The host is placed according to 
     * the two angles in radians.
     * 
     * @param theta 	x, z angle (polar)
     * @param phi		y angle
     * @param figure	figure to be placed 
     */
    public static void sphereArrangement(double theta, double phi, AbstractFigure3D figure) {
    	// Phi should be [0,PI] and theta between [0, 2PI] 
    	phi %= Math.PI;
    	theta %= Math.PI * 2;
        //get the coordinates in a plane 
        double x = Math.cos(theta) * Math.sin(phi) * GeometryBasket.EARTH_RADIUS;
        double z = Math.sin(theta) * Math.sin(phi) * GeometryBasket.EARTH_RADIUS;

        //get the z coordinate
        double y = Math.cos(phi) * GeometryBasket.EARTH_RADIUS;
        
        // Create the lookAt Transform Matrix
        TransformGroup rotation = figure.getRotationTransform();
        Transform3D lookAtTransform = new Transform3D();
        lookAtTransform.lookAt(new Point3d(), new Point3d(x, y , z), new Vector3d(0, 1, 0));
        lookAtTransform.invert(); // Keep it else wont work ( java3d failure )
        
        // Rotate the lookAt Matrix the right way
        Transform3D rotationMatrix = new Transform3D();
        rotationMatrix.rotX(-Math.PI/2);
        // Add the second transform
        lookAtTransform.mul(rotationMatrix);
        lookAtTransform.normalize();
        
        // And commit the changes
        rotation.setTransform(lookAtTransform);
        figure.placeSubFigure(figure, x, y, z);
    }

}
