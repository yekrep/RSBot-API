package org.powerbot.game.loader;

import org.powerbot.game.loader.io.AudioClip;

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
import java.util.*;

/**
 * Represents the applet stub and context provided to the applet for use.
 *
 * @author Timer
 */
public class ClientStub implements AppletStub, AppletContext {
	private final Map<URL, WeakReference<Image>> IMAGE_CACHE = new HashMap<URL, WeakReference<Image>>();
	private final Map<String, InputStream> INPUT_CACHE = Collections.synchronizedMap(new HashMap<String, InputStream>(2));

	private boolean active = false;
	private Map<String, String> parameters;
	private URL documentBase;
	private URL codeBase;
	public Applet applet;

	/**
	 * Instantiates a new stub and context with appropriate information.
	 *
	 * @param documentBase The document base.
	 * @param codeBase     The code base.
	 * @param parameters   The parameters to provide the applet.
	 * @throws java.net.MalformedURLException Malformed or invalid URL.
	 */
	public ClientStub(String documentBase, String codeBase, Map<String, String> parameters) throws MalformedURLException {
		this.parameters = parameters;
		this.documentBase = new URL(documentBase);
		this.codeBase = new URL(codeBase);
	}

	/**
	 * Changes the active state of this stub.
	 *
	 * @param active <tt>true</tt> if active; otherwise <tt>false</tt>.
	 */
	public void setActive(boolean active) {
		this.active = active;
	}

	/**
	 * Sets the applet associated with this stub instance.
	 *
	 * @param applet The applet to reference when requested.
	 */
	public void setApplet(Applet applet) {
		this.applet = applet;
	}

	/**
	 * Returns if active or not.
	 *
	 * @return <tt>true</tt> if active; otherwise <tt>false</tt>.
	 */
	public boolean isActive() {
		return this.active;
	}

	/**
	 * Returns the document base of this applet.
	 *
	 * @return The <tt>URL</tt> of the document base.
	 */
	public URL getDocumentBase() {
		return this.documentBase;
	}

	/**
	 * Returns the code base of this applet.
	 *
	 * @return The <tt>URL</tt> of the code base.
	 */
	public URL getCodeBase() {
		return this.codeBase;
	}

	/**
	 * Gets the parameter associated with the given hash.
	 *
	 * @param name The hash to search for.
	 * @return The <tt>String</tt> associated with the hash.
	 */
	public String getParameter(String name) {
		String value = this.parameters.get(name);
		return value == null ? "" : value;
	}

	/**
	 * Passes a resize to the applet.
	 *
	 * @param width  Pixel width
	 * @param height Pixel height
	 */
	public void appletResize(int width, int height) {
		Dimension size = new Dimension(width, height);
		applet.setSize(size);
		applet.setPreferredSize(size);
	}

	/**
	 * Returns the context associated with this stub.
	 *
	 * @return Returns the current instance of this class.
	 */
	public AppletContext getAppletContext() {
		return this;
	}

	/**
	 * Prints a status.
	 *
	 * @param status The status to show.
	 */
	public void showStatus(String status) {
		System.out.println("Status: " + status);
	}

	/**
	 * Shows a document.
	 *
	 * @param url The <code>URL</code> of the document.
	 */
	public void showDocument(URL url) {
		showDocument(url, "");
	}

	/**
	 * Shows a document.
	 *
	 * @param url    The <code>URL</code> of the document.
	 * @param target The target.
	 */
	public void showDocument(URL url, String target) {
		if (!target.equals("tbi")) {
			System.out.println("Attempting to show: " + url.toString() + " [" + target + "]");
		}
	}

	/**
	 * Caches an instance of an InputStream.
	 *
	 * @param key    The hash to classify the stream under.
	 * @param stream The input stream.
	 * @throws java.io.IOException
	 */
	public void setStream(String key, InputStream stream) throws IOException {
		INPUT_CACHE.put(key, stream);
	}

	/**
	 * Retrieves an input stream for the given hash.
	 *
	 * @param key The hash to look up.
	 * @return The <code>InputStream</code> associated with the given hash.
	 */
	public InputStream getStream(String key) {
		return INPUT_CACHE.get(key);
	}

	/**
	 * Returns an iterator of the stream hashes.
	 *
	 * @return An Iterator of String.
	 */
	public Iterator<String> getStreamKeys() {
		return Collections.unmodifiableSet(INPUT_CACHE.keySet()).iterator();
	}

	/**
	 * Loads an image from the given resource and caches it.
	 *
	 * @param url The URL location of the image on the local computer.
	 * @return The <code>Image</code> requested from the cache.
	 */
	public Image getImage(URL url) {
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

	/**
	 * Returns an applet for the given name.
	 *
	 * @param name The name of the applet.
	 * @return The <code>Applet</code> for the given name.
	 */
	public Applet getApplet(String name) {
		String thisName = parameters.get("name");
		if (thisName == null) {
			return null;
		}
		return thisName.equals(name) ? applet : null;
	}

	/**
	 * An Enumeration of the loaded Applet.
	 *
	 * @return An enumeration containing the loaded applet.
	 */
	public Enumeration<Applet> getApplets() {
		Vector<Applet> apps = new Vector<Applet>();
		apps.add(applet);
		return apps.elements();
	}

	/**
	 * Creates an auto clip.
	 *
	 * @param url The location of this audio clip.
	 * @return An instance of an <code>AudioClip</code>.
	 */
	public java.applet.AudioClip getAudioClip(URL url) {
		return new AudioClip(url);
	}
}