package org.powerbot.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotLoadingPanel;
import org.powerbot.service.NetworkAccount;
import org.powerbot.util.io.CryptFile;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IniParser;

public final class LoadAds implements Callable<Boolean> {

	@Override
	public Boolean call() throws Exception {
		final Map<String, String> data = IniParser.deserialise(HttpClient.openStream(new URL(Configuration.URLs.ADS))).get(IniParser.EMPTYSECTION);
		if (data.containsKey("image") && data.containsKey("link")) {
			final CryptFile cf = new CryptFile("ads/image.png", getClass());
			final String src = data.get("image");
			final String link = data.get("link");
			BufferedImage image = ImageIO.read(cf.download(new URL(src)));
			if (image.getWidth() > BotLoadingPanel.PANEL_WIDTH || image.getHeight() > BotLoadingPanel.PANEL_HEIGHT) {
				final float factor = (float) Math.min((double) BotLoadingPanel.PANEL_WIDTH / image.getWidth(), (double) BotLoadingPanel.PANEL_HEIGHT / image.getHeight());
				final BufferedImage resized = new BufferedImage((int) (image.getWidth() * factor), (int) (image.getHeight() * factor), BufferedImage.TYPE_INT_ARGB);
				final Graphics2D g = resized.createGraphics();
				g.drawImage(image, 0, 0, resized.getWidth(), resized.getHeight(), null);
				g.dispose();
				image = resized;
			}
			if (image != null) {
				final BufferedImage img = image;
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						BotChrome.getInstance().panel.loadingPanel.ad.setVisible(!NetworkAccount.getInstance().hasPermission(NetworkAccount.VIP));
						BotChrome.getInstance().panel.loadingPanel.ad.setImage(img, link);
					}
				});
			}
		}
		return true;
	}
}
