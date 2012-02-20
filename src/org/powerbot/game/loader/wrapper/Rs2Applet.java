package org.powerbot.game.loader.wrapper;

import org.powerbot.game.loader.RsClassLoader;

import java.applet.Applet;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * An applet for manipulation of the wrapped client class.
 * Loads the game for use.
 *
 * @author Timer
 */
public final class Rs2Applet extends Applet {
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
			e.printStackTrace();
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
			e.printStackTrace();
		}

		if (callback != null) {
			callback.run();
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
			System.out.println(e.getMessage());
			e.getCause().printStackTrace();
		}
	}


	private final void throwException(Throwable throwable) {
		throwable.printStackTrace();
	}
}