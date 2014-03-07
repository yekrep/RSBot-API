package org.powerbot.bot.os.loader;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

public class GameStub implements AppletStub, AppletContext {
	private final Map<String, String> params;
	private final String codeBase;

	public GameStub(final Map<String, String> params, final String codeBase) {
		this.params = params;
		this.codeBase = codeBase;
	}

	public final void showStatus(final String s) {
		throw new UnsupportedOperationException();
	}

	public final void showDocument(final URL url) {
	}

	public final AppletContext getAppletContext() {
		return this;
	}

	public final AudioClip getAudioClip(final URL url) {
		throw new UnsupportedOperationException();
	}

	public final Applet getApplet(final String s) {
		throw new UnsupportedOperationException();
	}

	public final boolean isActive() {
		return true;
	}

	public final void showDocument(final URL url, final String s) {
	}

	public final Image getImage(final URL url) {
		throw new UnsupportedOperationException();
	}

	public final URL getCodeBase() {
		try {
			return new URL(codeBase);
		} catch (final MalformedURLException ignored) {
			throw new InvalidParameterException();
		}
	}

	public final InputStream getStream(final String s) {
		throw new UnsupportedOperationException();
	}

	public final URL getDocumentBase() {
		try {
			return new URL(codeBase);
		} catch (final MalformedURLException ignored) {
			throw new InvalidParameterException();
		}
	}

	public final Iterator getStreamKeys() {
		throw new UnsupportedOperationException();
	}

	public final void setStream(final String s, final InputStream inputstream) {
		throw new UnsupportedOperationException();
	}

	public final String getParameter(final String s) {
		final String v = params.get(s);
		return v != null ? v : "";
	}

	public final void appletResize(final int i, final int j) {
	}

	public final Enumeration getApplets() {
		throw new UnsupportedOperationException();
	}
}
