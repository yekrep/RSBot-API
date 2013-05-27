package org.powerbot.script.framework;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.powerbot.bot.Bot;

public class ScreenCapture {
	private static final Logger log = Logger.getLogger(ScreenCapture.class.getName());
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-hhmmss");

	public static void save() {
		final String name = ScreenCapture.dateFormat.format(new Date()) + ".png";
		final File dir = new File(".");
		if (dir.isDirectory() || dir.mkdirs()) {
			ScreenCapture.save(new File(dir, name), "png");
		}
	}

	public static void save(final File file, final String type) {
		try {
			final BufferedImage image = capture();
			ImageIO.write(image, type, file);
			log.severe("Saved screen capture as " + file.getName());
		} catch (final Exception ignored) {
			log.severe("Failed to save screen capture (" + file.getName() + ")");
		}
	}

	public static BufferedImage capture() {
		final BufferedImage source = Bot.getInstance().getImage();
		final WritableRaster raster = source.copyData(null);
		return new BufferedImage(source.getColorModel(), raster, source.isAlphaPremultiplied(), null);
	}

	public static BufferedImage getScreenBuffer() {
		final BufferedImage source = Bot.getInstance().getBuffer();
		final WritableRaster raster = source.copyData(null);
		return new BufferedImage(source.getColorModel(), raster, source.isAlphaPremultiplied(), null);
	}
}
