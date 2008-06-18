package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class TextureBasket {
    public static final String path = "org/objectweb/proactive/ic2d/jmxmonitoring/ic3d/resources/";
    public static final BufferedImage pebbles = loadImage(path + "light_pebbles.jpg");
    public static final BufferedImage white_sand = loadImage(path + "white_sand.jpg");
    public static final BufferedImage metal_walk = loadImage(path + "metal_walk.jpg");
    public static final BufferedImage fedora = loadImage(path + "fedoraTexture.jpg");
    public static final BufferedImage kde = loadImage(path + "kde.png");
    public static final BufferedImage ao = loadImage(path + "ActiveObject.jpg");
    public static final BufferedImage node_border = loadImage(path + "node_skin.jpg");
    public static final BufferedImage round_corners = loadImage(path + "round_corners.png");
    public static final BufferedImage earth = loadImage(path + "EarthMap.jpg");
    public static final BufferedImage flatMap = loadImage(path + "france.gif");

	private static BufferedImage loadImage(final String fname) {
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
		final InputStream res = TextureBasket.class.getClassLoader()
				.getResourceAsStream(fname);

		BufferedImage bufferedImage;
		try {
			// URL res= TextureBasket.class.getClassLoader().getResource(fname);
			bufferedImage = ImageIO.read(res);
			return bufferedImage;
		} catch (final Exception e) {
			bufferedImage = new BufferedImage(100, 100,
					BufferedImage.TYPE_INT_RGB);

			System.out.println("Could not load resource from " + fname);

			return bufferedImage;
		}
	}
}
