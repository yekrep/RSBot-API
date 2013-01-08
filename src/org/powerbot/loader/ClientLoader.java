package org.powerbot.loader;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;

import org.powerbot.asm.NodeManipulator;
import org.powerbot.core.bot.Bot;
import org.powerbot.loader.script.ModScript;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

public class ClientLoader {
	private static final Logger log = Logger.getLogger(ClientLoader.class.getName());

	public final Crawler crawler;
	private final Map<String, byte[]> classes;
	private String packHash;

	private ClientLoader() {
		crawler = new Crawler();
		classes = new HashMap<>();

		load();
	}

	public static ClientLoader getInstance() {
		return new ClientLoader();
	}

	public void load() {
		log.info("Loading game");
		classes.clear();
		log.fine("Crawling (for) game information");
		if (!crawler.crawl()) {
			throw new RuntimeException("please check your firewall and internet connection");
		}
		log.fine("Downloading loader");
		final byte[] loader = getLoader(crawler);
		if (loader != null) {
			final String secretKeySpecKey = crawler.parameters.get("0");
			final String ivParameterSpecKey = crawler.parameters.get("-1");
			if (secretKeySpecKey == null || ivParameterSpecKey == null) {
				log.fine("Invalid secret spec key and/or iv parameter spec key");
				throw new RuntimeException("decryption mismatch");
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
			if (this.classes.size() > 0) {
				NodeManipulator nodeManipulator = null;
				try {
					while (true) {
						try {
							nodeManipulator = getNodeManipulator(this.classes);
							break;
						} catch (final PendingException p) {
							int d = p.getDelay() / 1000;
							log.warning("Request pending, trying again in " + (d < 60 ? d + " seconds" : (int) Math.ceil(d / 60) + " minutes"));
							Thread.sleep(p.getDelay());
						}
					}
				} catch (final Throwable e) {
					throw new RuntimeException("failed to load t-spec");
				}
				if (nodeManipulator != null) {
					log.fine("Running node manipulator");
					try {
						nodeManipulator.adapt();
					} catch (final AdaptException e) {
						throw new RuntimeException("t-spec adaption error");
					}
					log.fine("Processing classes");
					for (final Map.Entry<String, byte[]> clazz : this.classes.entrySet()) {
						final String name = clazz.getKey();
						this.classes.put(name, nodeManipulator.process(name, clazz.getValue()));
					}
				}
			}
		}
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

	public NodeManipulator getNodeManipulator(final Map<String, byte[]> classes) throws AdaptException, IOException, PendingException {
		final String packHash = getHash();
		final int delay = 1000 * 60 * 3 + 30;

		final HttpURLConnection con = HttpClient.getHttpConnection(new URL(String.format(Configuration.URLs.CLIENTPATCH, packHash)));
		con.setInstanceFollowRedirects(false);
		con.connect();
		if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return Bot.getInstance().modScript = new ModScript(IOHelper.read(HttpClient.getInputStream(con)));
		} else {
			final HttpURLConnection bucket = HttpClient.getHttpConnection(new URL(String.format(Configuration.URLs.CLIENTBUCKET, packHash)));
			bucket.setInstanceFollowRedirects(false);
			bucket.connect();
			switch (bucket.getResponseCode()) {
			case HttpURLConnection.HTTP_SEE_OTHER:
				final String dest = bucket.getHeaderField("Location");
				log.info("Updating client pack, please do not disconnect or close");
				final HttpURLConnection put = HttpClient.getHttpConnection(new URL(dest));
				put.setRequestMethod("PUT");
				put.setDoOutput(true);
				final OutputStream out = new GZIPOutputStream(put.getOutputStream());
				writePack(classes, out);
				out.flush();
				out.close();
				final int r = put.getResponseCode();
				put.disconnect();
				if (r == HttpURLConnection.HTTP_OK) {
					final HttpURLConnection bucket_notify = HttpClient.getHttpConnection(new URL(String.format(Configuration.URLs.CLIENTBUCKET, packHash)));
					bucket_notify.setRequestMethod("PUT");
					bucket_notify.connect();
					final int r_notify = bucket_notify.getResponseCode();
					bucket_notify.disconnect();
					if (r_notify == HttpURLConnection.HTTP_OK || r_notify == HttpURLConnection.HTTP_ACCEPTED) {
						throw new PendingException(delay * 2);
					} else {
						throw new AdaptException("Failed to load node manipulator; after reupload");
					}
				} else {
					throw new AdaptException("Failed to load node manipulator");
				}
			case HttpURLConnection.HTTP_ACCEPTED:
				throw new PendingException(delay);
			}
		}

		return null;
	}

	private void writePack(final Map<String, byte[]> classes, final OutputStream out) throws IOException {
		final int MAGIC = 0xC1A5700F, END_OF_FILE = 0x1;
		writeInt(MAGIC, out);

		synchronized (classes) {
			for (final byte[] data : classes.values()) {
				writeInt(data.length, out);
				out.write(data);
			}
		}

		writeInt(END_OF_FILE, out);
	}

	private void writeInt(final int v, final OutputStream o) throws IOException {
		final int m = 0xff;
		o.write(v >>> 24);
		o.write(v >>> 16 & m);
		o.write(v >>> 8 & m);
		o.write(v & m);
	}

	private final class PendingException extends Exception {
		private static final long serialVersionUID = -6937383190630297216L;
		private final int delay;

		public PendingException(final int delay) {
			this.delay = delay;
		}

		public int getDelay() {
			return delay;
		}
	}
}
