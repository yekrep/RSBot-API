package org.powerbot.game.loader.wrapper;

import org.powerbot.game.loader.RsClassLoader;

import java.applet.Applet;
import java.awt.Graphics;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An applet for manipulation of the wrapped client class.
 * Loads the game for use.
 *
 * @author Timer
 */
public final class Rs2Applet extends Applet {
	private static Logger log = Logger.getLogger(Rs2Applet.class.getName());
	private static final long serialVersionUID = 1L;
	private final Map<String, byte[]> classes = new HashMap<String, byte[]>();
	private String game;
	public Runnable callback;

	public Object clientInstance = null;
	private Class<?> clientClass = null;

	public Rs2Applet(Map<String, byte[]> classes, String game, Runnable callback) {
		this.classes.putAll(classes);
		this.game = game;
		this.callback = callback;
	}

	public final void init() {
		try {
			RsClassLoader loader = new RsClassLoader(this.classes, new URL(game));
			clientClass = loader.loadClass("client");
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to load classes: ", e);
		}

		if (clientClass == null) {
			throwException(new RuntimeException("unable to find client.class"));
			return;
		}

		Constructor<?> constructor;
		try {
			constructor = this.clientClass.getConstructor((Class[]) null);
			this.clientInstance = constructor.newInstance((Object[]) null);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Failed to construct client class: ", e);
		}

		if (callback != null) {
			try {
				callback.run();
			} catch (Throwable t) {
				log.log(Level.SEVERE, "Callback failed to execute: ", t);
			}
		}

		invokeMethod(new Object[]{this}, new Class[]{Applet.class}, "supplyApplet");
		invokeMethod(null, null, "init");
	}

	public final void start() {
		if (this.clientInstance != null) {
			invokeMethod(null, null, "start");
		}
	}

	public final void stop() {
		if (this.clientInstance != null) {
			invokeMethod(null, null, "stop");
		}
	}

	public final void destroy() {
		if (this.clientInstance != null) {
			invokeMethod(null, null, "destroy");
		}
	}

	public final void paint(Graphics render) {
		if (this.clientInstance != null) {
			invokeMethod(new Object[]{render}, new Class[]{Graphics.class}, "paint");
		}
	}

	public final void update(Graphics render) {
		if (this.clientInstance != null) {
			invokeMethod(new Object[]{render}, new Class[]{Graphics.class}, "update");
		}
	}

	private final void invokeMethod(Object[] parameters, Class<?>[] parameterTypes, String name) {
		try {
			Method method = this.clientClass.getMethod(name, parameterTypes);
			method.invoke(this.clientInstance, parameters);
		} catch (Exception e) {
			log.log(Level.SEVERE, "Error invoking client method: ", e);
		}
	}


	private final void throwException(Throwable throwable) {
		log.log(Level.SEVERE, "Client exception: ", throwable);
	}
}