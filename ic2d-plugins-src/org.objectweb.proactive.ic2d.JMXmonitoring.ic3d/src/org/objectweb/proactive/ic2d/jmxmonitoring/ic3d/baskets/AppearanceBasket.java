package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.baskets;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineAttributes;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.TexCoordGeneration;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TextureUnitState;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color3f;

import org.apache.log4j.Logger;

import com.sun.j3d.utils.image.TextureLoader;


/**
 * @author vjuresch
 * 
 */
public final class AppearanceBasket {
    private static final Logger logger = Logger.getLogger(AppearanceBasket.class.getName());
    public static final Appearance defaultNodeAppearance = AppearanceBasket.getDefaultNodeAppearance();
    public static final Appearance defaultRuntimeAppearance = AppearanceBasket.getDefaultRuntimeAppearance();
    public static final Appearance defaultHostAppearance = AppearanceBasket.getDefaultHostAppearance();
    public static final Appearance defaultLineAppearance = AppearanceBasket.getDefaultLineAppearance();
    public static final Appearance coordinatesAppearance = AppearanceBasket.getDefaultCoordinatesAppearance();
    public static final Appearance queueAppearance = AppearanceBasket.getDefaultQueueAppearance();

    // generic unknown appearance
    public static final Appearance defaultUnkownStateAppearance = AppearanceBasket
            .getDefaultUnknownStateAppearance();

    // active objects states
    public static final Appearance objectMigratingAppearance = getActiveObjectMigrating();
    public static final Appearance defaultActiveObjectAppearance = getDefaultActiveObjectAppearance();
    public static final Appearance servingRequestAppearance = getServingRequestAppearance();
    public static final Appearance waitingForRequestAppearance = getWaitingForRequestAppearance();
    public static final Appearance barMonitorAppearence = null;
    public static final Appearance monitorFull = getMonitoringAppearanceFull();
    public static final Appearance monitor = getMonitoringAppearance();
    public static final Appearance monitorLow = getMonitoringAppearanceLow();
    public static final Appearance flatMap = getFlatMapAppearance();

    // Earth grid appearances
    public static final Appearance earthGridAppearance = AppearanceBasket.getEarthGridAppearance();

    // make this class non instantiable
    private AppearanceBasket() {
    };

    private static Appearance getDefaultQueueAppearance() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(1f, 1f, 0.0f, 3);

        final Color3f matAmbient = ColorPalette.YELLOW;
        final Color3f matEmissive = ColorPalette.LIGHT_GRAY;
        final Color3f matDiffuse = ColorPalette.BLUE;
        final Color3f matSpecular = ColorPalette.GREEN;
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appear.setPolygonAttributes(polyAttributes);

        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0.7f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getEarthGridAppearance() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        final Color3f matAmbient = new Color3f(0.2f, 0.2f, 0.2f);
        final Color3f matEmissive = new Color3f(0.0f, 0.0f, 0.0f);
        final Color3f matDiffuse = new Color3f(1.0f, 1.0f, 1.0f);
        final Color3f matSpecular = new Color3f(1.0f, 1.0f, 1.0f);
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final TextureUnitState[] textureUnitState = new TextureUnitState[2];
        final TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(3);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.earth);
            final Texture texture1 = tex.getTexture();
            textureUnitState[0] = new TextureUnitState();
            textureUnitState[0].setTexture(texture1);
            textureUnitState[0].setTextureAttributes(texAttr1);
            appear.setTextureUnitState(textureUnitState);
        } catch (final Exception ex) {
            AppearanceBasket.logger.warn("Error loading texture: ", ex);
        }

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        polyAttributes.setCullFace(1);
        appear.setPolygonAttributes(polyAttributes);

        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NICEST, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultUnknownStateAppearance() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(1f, 1f, 0.0f, 3);

        final Color3f matAmbient = ColorPalette.RED;
        final Color3f matEmissive = ColorPalette.RED;
        final Color3f matDiffuse = ColorPalette.RED;
        final Color3f matSpecular = ColorPalette.RED;
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appear.setPolygonAttributes(polyAttributes);

        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getMonitoringAppearanceFull() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(1f, 0f, 0f, 3);

        final Color3f matAmbient = ColorPalette.RED;
        final Color3f matEmissive = ColorPalette.RED;
        final Color3f matDiffuse = ColorPalette.RED;
        final Color3f matSpecular = ColorPalette.RED;
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(PolygonAttributes.POLYGON_FILL);
        appear.setPolygonAttributes(polyAttributes);

        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NICEST, 0.25f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getMonitoringAppearance() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(1f, 1f, 0f, 3);

        final Color3f matAmbient = ColorPalette.YELLOW;
        final Color3f matEmissive = ColorPalette.YELLOW;
        final Color3f matDiffuse = ColorPalette.YELLOW;
        final Color3f matSpecular = ColorPalette.YELLOW;
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(PolygonAttributes.POLYGON_FILL);
        appear.setPolygonAttributes(polyAttributes);

        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NICEST, 0.25f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getMonitoringAppearanceLow() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(0f, 1f, 0f, 3);

        final Color3f matAmbient = ColorPalette.GREEN;
        final Color3f matEmissive = ColorPalette.GREEN;
        final Color3f matDiffuse = ColorPalette.GREEN;
        final Color3f matSpecular = ColorPalette.GREEN;
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(PolygonAttributes.POLYGON_FILL);
        appear.setPolygonAttributes(polyAttributes);

        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NICEST, 0.25f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultNodeAppearance() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(1f, 1f, 0.0f, 3);

        final Color3f matAmbient = ColorPalette.WHITE;
        final Color3f matEmissive = ColorPalette.WHITE;
        final Color3f matDiffuse = ColorPalette.WHITE;
        final Color3f matSpecular = ColorPalette.WHITE;
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final TextureUnitState[] textureUnitState = new TextureUnitState[2];
        final TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(3);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.node);
            final Texture texture1 = tex.getTexture();
            textureUnitState[0] = new TextureUnitState();
            textureUnitState[0].setTexture(texture1);
            textureUnitState[0].setTextureAttributes(texAttr1);
            appear.setTextureUnitState(textureUnitState);
        } catch (final Exception ex) {
            AppearanceBasket.logger.warn("Error loading texture: ", ex);
        }

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appear.setPolygonAttributes(polyAttributes);

        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NICEST, 0.7f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultRuntimeAppearance() {
        Appearance appear = new Appearance();

        ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        Color3f matAmbient = new Color3f(0.2f, 0.2f, 0.2f);
        Color3f matEmissive = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f matDiffuse = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f matSpecular = new Color3f(1.0f, 1.0f, 1.0f);
        float matShininess = 73.0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        TextureUnitState[] textureUnitState = new TextureUnitState[2];
        TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(3);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.runtime);
            Texture texture1 = tex.getTexture();
            textureUnitState[0] = new TextureUnitState();
            textureUnitState[0].setTexture(texture1);
            textureUnitState[0].setTextureAttributes(texAttr1);
            appear.setTextureUnitState(textureUnitState);
        } catch (Exception ex) {
            System.out.println("imgeURL1 - texture null !");
            ex.printStackTrace();
        }

        PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(1);
        appear.setPolygonAttributes(polyAttributes);

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultHostAppearance() {
    	Appearance appear = new Appearance();

        ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        Color3f matAmbient = new Color3f(0.2f, 0.2f, 0.2f);
        Color3f matEmissive = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f matDiffuse = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f matSpecular = new Color3f(1.0f, 1.0f, 1.0f);
        float matShininess = 73.0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        TextureUnitState[] textureUnitState = new TextureUnitState[2];
        TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(3);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.host);
            Texture texture1 = tex.getTexture();
            textureUnitState[0] = new TextureUnitState();
            textureUnitState[0].setTexture(texture1);
            textureUnitState[0].setTextureAttributes(texAttr1);
            appear.setTextureUnitState(textureUnitState);
        } catch (Exception ex) {
            System.out.println("imgeURL1 - texture null !");
            ex.printStackTrace();
        }

        PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(1);
        appear.setPolygonAttributes(polyAttributes);

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultActiveObjectAppearance() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        final Color3f matAmbient = new Color3f(0.2f, 0.2f, 0.2f);
        final Color3f matEmissive = new Color3f(0.0f, 0.0f, 0.0f);
        final Color3f matDiffuse = new Color3f(1.0f, 1.0f, 1.0f);
        final Color3f matSpecular = new Color3f(1.0f, 1.0f, 1.0f);
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final TextureUnitState[] textureUnitState = new TextureUnitState[2];
        final TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(3);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.metal_walk);
            final Texture texture1 = tex.getTexture();
            textureUnitState[0] = new TextureUnitState();
            textureUnitState[0].setTexture(texture1);
            textureUnitState[0].setTextureAttributes(texAttr1);
            appear.setTextureUnitState(textureUnitState);
        } catch (final Exception ex) {
            AppearanceBasket.logger.warn("Error loading texture: ", ex);
        }

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(1);
        appear.setPolygonAttributes(polyAttributes);

        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getActiveObjectMigrating() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        final Color3f matAmbient = ColorPalette.ORANGE;
        final Color3f matEmissive = ColorPalette.ORANGE;
        final Color3f matDiffuse = ColorPalette.ORANGE;
        final Color3f matSpecular = ColorPalette.ORANGE;
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final TextureUnitState[] textureUnitState = new TextureUnitState[2];
        final TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(2);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.metal_walk);
            final Texture texture1 = tex.getTexture();
            textureUnitState[0] = new TextureUnitState();
            textureUnitState[0].setTexture(texture1);
            textureUnitState[0].setTextureAttributes(texAttr1);
            appear.setTextureUnitState(textureUnitState);
        } catch (final Exception ex) {
            AppearanceBasket.logger.warn("Error loading texture: ", ex);
        }
        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(1);
        appear.setPolygonAttributes(polyAttributes);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultLineAppearance() {
        final Appearance appear = new Appearance();
        final LineAttributes attrib = new LineAttributes(1, LineAttributes.PATTERN_SOLID, true);
        appear.setLineAttributes(attrib);

        final ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        final Color3f matAmbient = ColorPalette.BLACK;
        final Color3f matEmissive = ColorPalette.BLACK;
        final Color3f matDiffuse = ColorPalette.BLACK;
        final Color3f matSpecular = ColorPalette.BLACK;
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultCoordinatesAppearance() {
        final Appearance appear = new Appearance();
        final LineAttributes attrib = new LineAttributes(1, LineAttributes.PATTERN_SOLID, true);
        appear.setLineAttributes(attrib);

        final ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        final Color3f matAmbient = ColorPalette.GREEN;
        final Color3f matEmissive = ColorPalette.RED;
        final Color3f matDiffuse = ColorPalette.BLACK;
        final Color3f matSpecular = ColorPalette.BLACK;
        final float matShininess = 0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getServingRequestAppearance() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        final Color3f matAmbient = ColorPalette.BLUE;
        final Color3f matEmissive = ColorPalette.BLUE;
        final Color3f matDiffuse = ColorPalette.BLUE;
        final Color3f matSpecular = ColorPalette.BLUE;
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final TextureUnitState[] textureUnitState = new TextureUnitState[2];
        final TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(2);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.metal_walk);
            final Texture texture1 = tex.getTexture();
            textureUnitState[0] = new TextureUnitState();
            textureUnitState[0].setTexture(texture1);
            textureUnitState[0].setTextureAttributes(texAttr1);
            appear.setTextureUnitState(textureUnitState);
        } catch (final Exception ex) {
            AppearanceBasket.logger.warn("Error loading texture: ", ex);
        }

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(1);
        appear.setPolygonAttributes(polyAttributes);

        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getWaitingForRequestAppearance() {
        final Appearance appear = new Appearance();

        final ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        final Color3f matAmbient = ColorPalette.GREEN;
        final Color3f matEmissive = ColorPalette.GREEN;
        final Color3f matDiffuse = ColorPalette.GREEN;
        final Color3f matSpecular = ColorPalette.GREEN;
        final float matShininess = 73.0f;

        final Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        final TextureUnitState[] textureUnitState = new TextureUnitState[2];
        final TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(2);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.metal_walk);
            final Texture texture1 = tex.getTexture();
            textureUnitState[0] = new TextureUnitState();
            textureUnitState[0].setTexture(texture1);
            textureUnitState[0].setTextureAttributes(texAttr1);
            appear.setTextureUnitState(textureUnitState);
        } catch (final Exception ex) {
            AppearanceBasket.logger.warn("Error loading texture: ", ex);
            ex.printStackTrace();
        }

        final PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(1);
        appear.setPolygonAttributes(polyAttributes);

        final TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getFlatMapAppearance() {
        Appearance appear = new Appearance();

        ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        Color3f matAmbient = new Color3f(0.2f, 0.2f, 0.2f);
        Color3f matEmissive = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f matDiffuse = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f matSpecular = new Color3f(1.0f, 1.0f, 1.0f);
        float matShininess = 73.0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        TextureUnitState[] textureUnitState = new TextureUnitState[2];
        TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(3);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.flatMap);
            Texture texture1 = tex.getTexture();
            textureUnitState[0] = new TextureUnitState();
            textureUnitState[0].setTexture(texture1);
            textureUnitState[0].setTextureAttributes(texAttr1);
            appear.setTextureUnitState(textureUnitState);
        } catch (Exception ex) {
            System.out.println("imgeURL1 - texture null !");
            ex.printStackTrace();
        }

        PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(1);
        appear.setPolygonAttributes(polyAttributes);

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }
}
