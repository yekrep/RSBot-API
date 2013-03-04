package org.powerbot.util;

import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.powerbot.gui.component.BotLocale;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

public final class LoadUpdates implements Callable<Boolean> {
	private static final Logger log = Logger.getLogger(LoadUpdates.class.getName());

	public Boolean call() throws Exception {
		log.log(Level.INFO, "Checking for updates", BotLocale.STARTING);
		final int version;
		try {
			version = Integer.parseInt(IOHelper.readString(HttpClient.openStream(new URL(Configuration.URLs.VERSION))).trim());
		} catch (final Exception e) {
			String msg = "Error reading server data";
			if (SocketException.class.isAssignableFrom(e.getClass()) || SocketTimeoutException.class.isAssignableFrom(e.getClass())) {
				msg = "Could not connect to " + Configuration.URLs.DOMAIN + " server";
			}
			log.log(Level.SEVERE, msg, BotLocale.ERROR);
			return false;
		}
		if (version > Configuration.VERSION) {
			log.log(Level.SEVERE, String.format("A newer version is available, please download from %s", BotLocale.WEBSITE), "Update");
			return false;
		}
		Configuration.VERSION_LATEST = version;
		return true;
	}
}
