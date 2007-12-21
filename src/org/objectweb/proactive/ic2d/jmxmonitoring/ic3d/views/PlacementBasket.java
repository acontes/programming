/**
 *
 */
package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import javax.vecmath.Point3d;


/**
 * @author vjuresch
 *
 */
public class PlacementBasket {
    public static void spiralArrangement(int figureIndex,
        AbstractFigure3D figure) {
        double x = Math.cos(figureIndex) * Math.sqrt(figureIndex);
        double y = Math.sin(figureIndex) * Math.sqrt(figureIndex);
        figure.placeSubFigure(figure, x, y, 0);
    }

    public static void lengthArrangement(Point3d container, double spacing,
        int figureIndex, int figures, AbstractFigure3D figure) {
    }
}
