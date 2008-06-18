package org.objectweb.proactive.ic2d.jmxmonitoring.ic3d.views;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class TextureBasket {
	public static final String path = "org/objectweb/proactive/ic2d/jmxmonitoring/ic3d/resources/";
	public static final BufferedImage pebbles = TextureBasket
			.loadImage(TextureBasket.path + "light_pebbles.jpg");
	public static final BufferedImage white_sand = TextureBasket
			.loadImage(TextureBasket.path + "white_sand.jpg");
	public static final BufferedImage metal_walk = TextureBasket
			.loadImage(TextureBasket.path + "metal_walk.jpg");
	public static final BufferedImage fedora = TextureBasket
			.loadImage(TextureBasket.path + "fedoraTexture.jpg");
	public static final BufferedImage kde = TextureBasket
			.loadImage(TextureBasket.path + "kde.png");
	public static final BufferedImage ao = TextureBasket
			.loadImage(TextureBasket.path + "ActiveObject.jpg");
	public static final BufferedImage node_border = TextureBasket
			.loadImage(TextureBasket.path + "node_skin.jpg");
	public static final BufferedImage round_corners = TextureBasket
			.loadImage(TextureBasket.path + "round_corners.png");
	public static final BufferedImage earth = TextureBasket
			.loadImage(TextureBasket.path + "EarthMap.jpg");

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
