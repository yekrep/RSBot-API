package org.powerbot.bot.loader;

import java.applet.Applet;
import java.applet.AppletContext;
import java.applet.AppletStub;
import java.applet.AudioClip;
import java.awt.Image;
import java.io.IOException;
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

	public GameStub(Map<String, String> params, String codeBase) {
		this.params = params;
		this.codeBase = codeBase;
	}

	public final void showStatus(String s) {
		throw new UnsupportedOperationException();
	}

	public final void showDocument(URL url) {
	}

	public final AppletContext getAppletContext() {
		return this;
	}

	public final AudioClip getAudioClip(URL url) {
		throw new UnsupportedOperationException();
	}

	public final Applet getApplet(String s) {
		throw new UnsupportedOperationException();
	}

	public final boolean isActive() {
		return true;
	}

	public final void showDocument(URL url, String s) {
	}

	public final Image getImage(URL url) {
		throw new UnsupportedOperationException();
	}

	public final URL getCodeBase() {
		try {
			return new URL(codeBase);
		} catch (MalformedURLException malformedurlexception) {
			throw new InvalidParameterException();
		}
	}

	public final InputStream getStream(String s) {
		throw new UnsupportedOperationException();
	}

	public final URL getDocumentBase() {
		try {
			return new URL(codeBase);
		} catch (MalformedURLException malformedurlexception) {
			throw new InvalidParameterException();
		}
	}

	public final Iterator getStreamKeys() {
		throw new UnsupportedOperationException();
	}

	public final void setStream(String s, InputStream inputstream) throws IOException {
		throw new UnsupportedOperationException();
	}

	public final String getParameter(String s) {
		final String v = params.get(s);
		return v != null ? v : "";
	}

	public final void appletResize(int i, int j) {
	}

	public final Enumeration getApplets() {
		throw new UnsupportedOperationException();
	}
}
