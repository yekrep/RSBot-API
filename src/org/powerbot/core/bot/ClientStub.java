package org.powerbot.core.bot;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
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

import org.powerbot.loader.ClientLoader;

/**
 * @author Timer
 */
public class ClientStub implements AppletStub, AppletContext {
	private final Map<URL, WeakReference<Image>> IMAGE_CACHE = new HashMap<URL, WeakReference<Image>>();
	private final Map<String, InputStream> INPUT_CACHE = Collections.synchronizedMap(new HashMap<String, InputStream>(2));

	private boolean active = false;
	private final Map<String, String> parameters;
	private URL documentBase;
	private URL codeBase;
	public Applet applet;

	public ClientStub(final RSLoader loader) {
		final ClientLoader clientLoader = loader.getClientLoader();
		this.parameters = clientLoader.crawler.parameters;
		try {
			this.documentBase = new URL(clientLoader.crawler.game);
			this.codeBase = new URL(clientLoader.crawler.archive);
		} catch (final MalformedURLException ignored) {
		}
	}

	public void setActive(final boolean active) {
		this.active = active;
	}

	public void setApplet(final Applet applet) {
		this.applet = applet;
	}

	public boolean isActive() {
		return active;
	}

	public URL getDocumentBase() {
		return documentBase;
	}

	public URL getCodeBase() {
		return codeBase;
	}

	public String getParameter(final String name) {
		final String value = parameters.get(name);
		return value == null ? "" : value;
	}

	public void appletResize(final int width, final int height) {
		final Dimension size = new Dimension(width, height);
		applet.setSize(size);
		applet.setPreferredSize(size);
	}

	public AppletContext getAppletContext() {
		return this;
	}

	public void showStatus(final String status) {
		System.out.println("Status: " + status);
	}

	public void showDocument(final URL url) {
		showDocument(url, "");
	}

	public void showDocument(final URL url, final String target) {
		if (!target.equals("tbi")) {
			System.out.println("Attempting to show: " + url.toString() + " [" + target + "]");
		}
	}

	public void setStream(final String key, final InputStream stream) throws IOException {
		INPUT_CACHE.put(key, stream);
	}

	public InputStream getStream(final String key) {
		return INPUT_CACHE.get(key);
	}

	public Iterator<String> getStreamKeys() {
		return Collections.unmodifiableSet(INPUT_CACHE.keySet()).iterator();
	}

	public Image getImage(final URL url) {
		synchronized (IMAGE_CACHE) {
			WeakReference<Image> ref = IMAGE_CACHE.get(url);
			Image img;
			if (ref == null || (img = ref.get()) == null) {
				img = Toolkit.getDefaultToolkit().createImage(url);
				ref = new WeakReference<Image>(img);
				IMAGE_CACHE.put(url, ref);
			}
			return img;
		}
	}

	public Applet getApplet(final String name) {
		final String thisName = parameters.get("name");
		if (thisName == null) {
			return null;
		}
		return thisName.equals(name) ? applet : null;
	}

	public Enumeration<Applet> getApplets() {
		final Vector<Applet> apps = new Vector<Applet>();
		apps.add(applet);
		return apps.elements();
	}

	public java.applet.AudioClip getAudioClip(final URL url) {
		return new AudioClip(url);
	}
}