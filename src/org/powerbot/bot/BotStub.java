package org.powerbot.bot;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import org.powerbot.bot.loader.Crawler;

public class BotStub implements AppletStub, AppletContext {
	private static final Logger log = Logger.getLogger(BotStub.class.getName());
	private final Map<URL, WeakReference<Image>> IMAGE_CACHE;
	private final Map<String, InputStream> INPUT_CACHE;
	private final Applet applet;
	private final URL documentBase;
	private final URL codeBase;
	private final Map<String, String> params;
	private boolean active;

	public BotStub(final Applet applet, final Crawler crawler) {
		IMAGE_CACHE = new HashMap<>();
		INPUT_CACHE = Collections.synchronizedMap(new HashMap<String, InputStream>(2));

		this.applet = applet;
		try {
			this.documentBase = new URL(crawler.game);
			this.codeBase = new URL(crawler.archive);
		} catch (final MalformedURLException e) {
			throw new RuntimeException(e);
		}
		this.params = crawler.parameters;
		this.active = false;
	}

	@Override
	public AudioClip getAudioClip(final URL url) {
		throw new UnsupportedOperationException("NOT YET IMPLEMENTED " + AudioClip.class.getName() + " = " + url.toString());//TODO this
	}

	@Override
	public Image getImage(final URL url) {
		synchronized (IMAGE_CACHE) {
			WeakReference<Image> ref = IMAGE_CACHE.get(url);
			Image img;
			if (ref == null || (img = ref.get()) == null) {
				img = Toolkit.getDefaultToolkit().createImage(url);
				ref = new WeakReference<>(img);
				IMAGE_CACHE.put(url, ref);
			}
			return img;
		}
	}

	@Override
	public Applet getApplet(final String name) {
		final String n;
		if ((n = params.get("name")) != null && n.equals(name)) {
			return applet;
		}
		return null;
	}

	@Override
	public Enumeration<Applet> getApplets() {
		final Vector<Applet> applets = new Vector<>(1);
		applets.add(applet);
		return applets.elements();
	}

	@Override
	public void showDocument(final URL url) {
		showDocument(url, "");
	}

	@Override
	public void showDocument(final URL url, final String target) {
		if (!target.equals("tbi")) {
			log.info("Attempting to show: " + url.toString() + " [" + target + "]");
		}
	}

	@Override
	public void showStatus(final String status) {
		log.info("Status: " + status);
	}

	@Override
	public void setStream(final String key, final InputStream stream) throws IOException {
		INPUT_CACHE.put(key, stream);
	}

	@Override
	public InputStream getStream(final String key) {
		return INPUT_CACHE.get(key);
	}

	@Override
	public Iterator<String> getStreamKeys() {
		return Collections.unmodifiableSet(INPUT_CACHE.keySet()).iterator();
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	@Override
	public URL getDocumentBase() {
		return this.documentBase;
	}

	@Override
	public URL getCodeBase() {
		return this.codeBase;
	}

	@Override
	public String getParameter(final String name) {
		final String v = params.get(name);
		return v != null ? v : "";
	}

	@Override
	public AppletContext getAppletContext() {
		return this;
	}

	@Override
	public void appletResize(final int width, final int height) {
		final Dimension d = new Dimension(width, height);
		applet.setSize(d);
		applet.setPreferredSize(d);
	}
}
