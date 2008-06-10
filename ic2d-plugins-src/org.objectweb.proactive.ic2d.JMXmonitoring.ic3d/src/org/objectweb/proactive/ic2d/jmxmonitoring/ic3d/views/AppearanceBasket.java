package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

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

import com.sun.j3d.utils.image.TextureLoader;


/**
 * @author vjuresch
 *
 */
public class AppearanceBasket {
    public static final Appearance defaultNodeAppearance = getDefaultNodeAppearance();
    public static final Appearance defaultRuntimeAppearance = getDefaultRuntimeAppearance();
    public static final Appearance defaultHostAppearance = getDefaultHostAppearance();
    public static final Appearance defaultLineAppearance = getDefaultLineAppearance();
    public static final Appearance coordinatesAppearance = getDefaultCoordinatesAppearance();
    public static final Appearance queueAppearance = getDefaultQueueAppearance();

    // generic unknown appearance
    public static final Appearance defaultUnkownStateAppearance = getDefaultUnknownStateAppearance();

    // active objects states
    public static final Appearance objectMigratingAppearance = getActiveObjectMigrating();
    public static final Appearance defaultActiveObjectAppearance = getDefaultActiveObjectAppearance();
    public static final Appearance servingRequestAppearance = getServingRequestAppearance();
    public static final Appearance waitingForRequestAppearance = getWaitingForRequestAppearance();

    //Earth grid appearances
    public static final Appearance earthGridAppearance = getEarthGridAppearance();

    //make this class non instantiable
    private AppearanceBasket() {
    };

    private static Appearance getDefaultQueueAppearance() {
        Appearance appear = new Appearance();

        ColoringAttributes colorAttrib = new ColoringAttributes(1f, 1f, 0.0f, 3);

        Color3f matAmbient = ColorPalette.YELLOW;
        Color3f matEmissive = ColorPalette.LIGHT_GRAY;
        Color3f matDiffuse = ColorPalette.BLUE;
        Color3f matSpecular = ColorPalette.GREEN;
        float matShininess = 73.0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appear.setPolygonAttributes(polyAttributes);

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0.7f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getEarthGridAppearance() {
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
            tex = new TextureLoader(TextureBasket.earth);
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
        polyAttributes.setPolygonMode(PolygonAttributes.POLYGON_POINT);
        polyAttributes.setCullFace(1);
        appear.setPolygonAttributes(polyAttributes);

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NICEST, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultUnknownStateAppearance() {
        Appearance appear = new Appearance();

        ColoringAttributes colorAttrib = new ColoringAttributes(1f, 1f, 0.0f, 3);

        Color3f matAmbient = ColorPalette.RED;
        Color3f matEmissive = ColorPalette.RED;
        Color3f matDiffuse = ColorPalette.RED;
        Color3f matSpecular = ColorPalette.RED;
        float matShininess = 73.0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appear.setPolygonAttributes(polyAttributes);

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultNodeAppearance() {
        Appearance appear = new Appearance();

        ColoringAttributes colorAttrib = new ColoringAttributes(1f, 1f, 0.0f, 3);

        Color3f matAmbient = ColorPalette.WHITE;
        Color3f matEmissive = ColorPalette.WHITE;
        Color3f matDiffuse = ColorPalette.WHITE;
        Color3f matSpecular = ColorPalette.WHITE;
        float matShininess = 73.0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        TextureUnitState[] textureUnitState = new TextureUnitState[2];
        TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(3);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.kde);
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
        polyAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appear.setPolygonAttributes(polyAttributes);

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NICEST, 0.7f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultRuntimeAppearance() {
        Appearance appear = new Appearance();
        Material mat = new Material();
        mat.setShininess(50);
        mat.setEmissiveColor(ColorPalette.BLUE);
        mat.setAmbientColor(ColorPalette.BLACK);
        mat.setSpecularColor(ColorPalette.DARK_GRAY);
        /*
         * ambientColor the material's ambient color emissiveColor the
         * material's emissive color diffuseColor the material's diffuse color
         * when illuminated by a light specularColor the material's specular
         * color when illuminated to generate a highlight shininess the
         * material's shininess in the range [1.0, 128.0] with 1.0 being not
         * shiny and 128.0 being very shiny. Values outside this range are
         * clamped.
         */
        appear.setMaterial(mat);

        // TODO optimization:all faces are rendered, render only those visible
        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appear.setPolygonAttributes(polygonAttributes);

        TextureLoader loader = new TextureLoader(TextureBasket.node_border);

        // Create Texture object
        Texture metal_walk = loader.getTexture();

        // Create Appearance Attributes and give to Appearance.
        // TextureAttributes can be used for transforming texture (e.g. scaling)
        TextureAttributes ta = new TextureAttributes();
        appear.setTextureAttributes(ta);

        // Attach Texture object to Appearance object
        appear.setTexture(metal_walk);
        TexCoordGeneration tex = new TexCoordGeneration();
        tex.setEnable(true);
        appear.setTexCoordGeneration(tex);
        return appear;
    }

    private static Appearance getDefaultHostAppearance() {
        Appearance appear = new Appearance();
        Material mat = new Material();
        mat.setShininess(50);
        mat.setEmissiveColor(ColorPalette.BLUE);
        mat.setAmbientColor(ColorPalette.BLACK);
        mat.setSpecularColor(ColorPalette.PINK);
        /*
         * ambientColor the material's ambient color emissiveColor the
         * material's emissive color diffuseColor the material's diffuse color
         * when illuminated by a light specularColor the material's specular
         * color when illuminated to generate a highlight shininess the
         * material's shininess in the range [1.0, 128.0] with 1.0 being not
         * shiny and 128.0 being very shiny. Values outside this range are
         * clamped.
         */
        appear.setMaterial(mat);

        // TODO optimization:all faces are rendered, render only those visible
        PolygonAttributes polygonAttributes = new PolygonAttributes();
        polygonAttributes.setCullFace(PolygonAttributes.CULL_NONE);
        appear.setPolygonAttributes(polygonAttributes);

        // TODO remove constant
        TextureLoader loader = new TextureLoader(TextureBasket.white_sand);

        // Create Texture object
        Texture metal_walk = loader.getTexture();

        // Create Appearance Object
        Appearance appearance = new Appearance();

        // Create Appearance Attributes and give to Appearance.
        // TextureAttributes can be used for transforming texture (e.g. scaling)
        TextureAttributes ta = new TextureAttributes();

        appearance.setTextureAttributes(ta);

        // Attach Texture object to Appearance object
        appearance.setTexture(metal_walk);
        TexCoordGeneration tex = new TexCoordGeneration();
        tex.setEnable(true);
        appearance.setTexCoordGeneration(tex);

        return appearance;
    }

    private static Appearance getDefaultActiveObjectAppearance() {
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
            tex = new TextureLoader(TextureBasket.metal_walk);
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

    private static Appearance getActiveObjectMigrating() {
        Appearance appear = new Appearance();

        ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        Color3f matAmbient = ColorPalette.ORANGE;
        Color3f matEmissive = ColorPalette.ORANGE;
        Color3f matDiffuse = ColorPalette.ORANGE;
        Color3f matSpecular = ColorPalette.ORANGE;
        float matShininess = 73.0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        TextureUnitState[] textureUnitState = new TextureUnitState[2];
        TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(2);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.metal_walk);
            Texture texture1 = tex.getTexture();
            textureUnitState[0] = new TextureUnitState();
            textureUnitState[0].setTexture(texture1);
            textureUnitState[0].setTextureAttributes(texAttr1);
            appear.setTextureUnitState(textureUnitState);
        } catch (Exception ex) {
            System.out.println("imgeURL1 - texture null !");
            ex.printStackTrace();
        }

        TransparencyAttributes transparencyAttributes = new TransparencyAttributes(
            TransparencyAttributes.NONE, 0f);
        transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);

        PolygonAttributes polyAttributes = new PolygonAttributes();
        polyAttributes.setPolygonMode(2);
        polyAttributes.setCullFace(1);
        appear.setPolygonAttributes(polyAttributes);

        appear.setTransparencyAttributes(transparencyAttributes);
        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultLineAppearance() {
        Appearance appear = new Appearance();
        LineAttributes attrib = new LineAttributes(1, LineAttributes.PATTERN_SOLID, true);
        appear.setLineAttributes(attrib);

        ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        Color3f matAmbient = ColorPalette.BLACK;
        Color3f matEmissive = ColorPalette.BLACK;
        Color3f matDiffuse = ColorPalette.BLACK;
        Color3f matSpecular = ColorPalette.BLACK;
        float matShininess = 73.0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getDefaultCoordinatesAppearance() {
        Appearance appear = new Appearance();
        LineAttributes attrib = new LineAttributes(1, LineAttributes.PATTERN_SOLID, true);
        appear.setLineAttributes(attrib);

        ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        Color3f matAmbient = ColorPalette.GREEN;
        Color3f matEmissive = ColorPalette.RED;
        Color3f matDiffuse = ColorPalette.BLACK;
        Color3f matSpecular = ColorPalette.BLACK;
        float matShininess = 0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        appear.setMaterial(material);
        appear.setColoringAttributes(colorAttrib);

        return appear;
    }

    private static Appearance getServingRequestAppearance() {
        Appearance appear = new Appearance();

        ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        Color3f matAmbient = ColorPalette.BLUE;
        Color3f matEmissive = ColorPalette.BLUE;
        Color3f matDiffuse = ColorPalette.BLUE;
        Color3f matSpecular = ColorPalette.BLUE;
        float matShininess = 73.0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        TextureUnitState[] textureUnitState = new TextureUnitState[2];
        TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(2);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.metal_walk);
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

    private static Appearance getWaitingForRequestAppearance() {
        Appearance appear = new Appearance();

        ColoringAttributes colorAttrib = new ColoringAttributes(0.0f, 0.0f, 0.0f, 3);

        Color3f matAmbient = ColorPalette.GREEN;
        Color3f matEmissive = ColorPalette.GREEN;
        Color3f matDiffuse = ColorPalette.GREEN;
        Color3f matSpecular = ColorPalette.GREEN;
        float matShininess = 73.0f;

        Material material = new Material(matAmbient, matEmissive, matDiffuse, matSpecular, matShininess);

        TextureUnitState[] textureUnitState = new TextureUnitState[2];
        TextureAttributes texAttr1 = new TextureAttributes();
        texAttr1.setTextureMode(2);
        TextureLoader tex;

        try {
            tex = new TextureLoader(TextureBasket.metal_walk);
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
