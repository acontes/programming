package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;


public class TextureBasket {
    public static String path = new String(
            "org/objectweb/proactive/ic2d/jmxmonitoring/ic3d/resources/");
    public static BufferedImage pebbles = loadImage(path + "light_pebbles.jpg");
    public static BufferedImage white_sand = loadImage(path + "white_sand.jpg");
    public static BufferedImage metal_walk = loadImage(path + "metal_walk.jpg");
    public static BufferedImage fedora = loadImage(path + "fedoraTexture.jpg");
    public static BufferedImage kde = loadImage(path + "kde.png");
    public static BufferedImage ao = loadImage(path + "ActiveObject.jpg");
    public static BufferedImage node_border = loadImage(path + "node_skin.jpg");
    public static BufferedImage round_corners = loadImage(path +
            "round_corners.png");
    public static BufferedImage earth = loadImage(path + "EarthMap.jpg");

    private static BufferedImage loadImage(String fname) {
        // Level logLevel = Level.FINE;
        // Logger logger =
        // Logger.getLogger("org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views.views");
        // ConsoleHandler handler = new ConsoleHandler();
        //
        // logger.log(Level.FINE,"Loading texture: " + fname);
        //		
        // //initialize logger
        // logger.setLevel(logLevel);
        // handler.setLevel(logLevel);
        // logger.addHandler(handler);
        InputStream res = TextureBasket.class.getClassLoader()
                                             .getResourceAsStream(fname);

        BufferedImage bufferedImage;
        try {
            // URL res= TextureBasket.class.getClassLoader().getResource(fname);
            bufferedImage = (BufferedImage) ImageIO.read(res);
            return bufferedImage;
        } catch (Exception e) {
            bufferedImage = new BufferedImage(100, 100,
                    BufferedImage.TYPE_INT_RGB);

            System.out.println("Could not load resource from " + fname);

            return bufferedImage;
        }
    }
}
