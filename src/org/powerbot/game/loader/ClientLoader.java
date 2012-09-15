package org.powerbot.game.loader;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.powerbot.asm.NodeManipulator;
import org.powerbot.core.bot.Bot;
import org.powerbot.game.loader.script.ModScript;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

public class ClientLoader implements Callable<Boolean> {
	private static final Logger log = Logger.getLogger(ClientLoader.class.getName());

	public final Crawler crawler;
	private final Map<String, byte[]> classes;
	private String packHash;

	private boolean cancelled = false;

	public ClientLoader() {
		crawler = new Crawler();
		classes = new HashMap<>();
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void cancel() {
		cancelled = true;
	}

	@Override
	public Boolean call() {
		this.cancelled = false;
		log.info("Initializing game environment");
		classes.clear();
		log.fine("Crawling (for) game information");
		if (!crawler.crawl()) {
			log.severe("Please try again");
			return false;
		}
		if (cancelled) {
			return false;
		}
		log.fine("Downloading loader");
		final byte[] loader = getLoader(crawler);
		if (loader != null) {
			final String secretKeySpecKey = crawler.parameters.get("0");
			final String ivParameterSpecKey = crawler.parameters.get("-1");
			if (secretKeySpecKey == null || ivParameterSpecKey == null) {
				log.fine("Invalid secret spec key and/or iv parameter spec key");
				return false;
			}
			if (cancelled) {
				return false;
			}
			log.fine("Removing key ciphering");
			final byte[] secretKeySpecBytes = Crypt.decode(secretKeySpecKey);
			final byte[] ivParameterSpecBytes = Crypt.decode(ivParameterSpecKey);
			log.fine("Extracting classes from loader");
			final Map<String, byte[]> classes = Deflator.extract(secretKeySpecBytes, ivParameterSpecBytes, loader);
			log.fine("Generating client hash");
			packHash = StringUtil.byteArrayToHexString(Deflator.inner_pack_hash);
			log.fine("Client hash (" + packHash + ")");
			if (classes != null && classes.size() > 0) {
				this.classes.putAll(classes);
				classes.clear();
			}
			if (cancelled) {
				return false;
			}
			if (this.classes.size() > 0) {
				NodeManipulator nodeManipulator;
				try {
					nodeManipulator = getNodeManipulator();
				} catch (final Throwable e) {
					log.log(Level.FINE, "Failed to load manipulator: ", e);
					return false;
				}
				if (nodeManipulator != null) {
					log.fine("Running node manipulator");
					try {
						nodeManipulator.adapt();
					} catch (final AdaptException e) {
						log.log(Level.FINE, "Node manipulation failed", e);
						return false;
					}
					log.fine("Processing classes");
					for (final Map.Entry<String, byte[]> clazz : this.classes.entrySet()) {
						final String name = clazz.getKey();
						this.classes.put(name, nodeManipulator.process(name, clazz.getValue()));
					}
				}

				return !cancelled;
			}
		}
		return false;
	}

	public Map<String, byte[]> getClasses() {
		final Map<String, byte[]> classes = new HashMap<>();
		classes.putAll(this.classes);
		return classes;
	}

	public String getHash() {
		return packHash;
	}

	public static byte[] getLoader(final Crawler crawler) {
		try {
			final URLConnection clientConnection = HttpClient.getHttpConnection(new URL(crawler.archive));
			clientConnection.addRequestProperty("Referer", crawler.game);
			return IOHelper.read(HttpClient.getInputStream(clientConnection));
		} catch (final IOException ignored) {
		}
		return null;
	}

	public NodeManipulator getNodeManipulator() throws AdaptException {
		final String packHash = getHash();

		final String id = "(" + packHash.substring(0, 6) + ")";
		log.info("Loading client patch " + id);
		try {
			return Bot.getInstance().modScript = new ModScript(IOHelper.read(HttpClient.openStream(new URL(String.format(Configuration.URLs.CLIENTPATCH, packHash)))));
		} catch (final SocketTimeoutException ignored) {
			log.severe("Cannot connect to update server " + id);
		} catch (final NullPointerException ignored) {
			log.severe("Error parsing client patch " + id);
		} catch (final IOException e) {
			log.log(Level.SEVERE, "Client patch " + id + " unavailable", "Outdated");
		}
		throw new AdaptException("Failed to load node manipulator; unable to reach server or client unsupported");
	}
}
