package org.powerbot.bot;

import java.applet.Applet;
import java.awt.Graphics;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.powerbot.loader.ClientLoader;
import org.powerbot.loader.script.TransformSpec;
import org.powerbot.util.Configuration;

/**
 * @author Timer
 */
public class RSLoader extends Applet implements Runnable {
	private static final Logger log = Logger.getLogger(RSLoader.class.getName());
	private Runnable callback;
	private Class<?> clazz;
	private Object client;
	private ClientLoader clientLoader;
	private RSClassLoader classLoader;
	private TransformSpec tspec;

	public boolean load() {
		try {
			clientLoader = new ClientLoader();
			clientLoader.run();
			tspec = clientLoader.getTspec();
			classLoader = new RSClassLoader(clientLoader.classes(), new URL("http://" + Configuration.URLs.GAME + "/"));
		} catch (final Exception e) {
			log.severe("Unable to load client: " + e.getMessage());
			return false;
		}
		return true;
	}

	public ClientLoader getClientLoader() {
		return clientLoader;
	}

	public Object getClient() {
		return client;
	}

	public TransformSpec getTspec() {
		return tspec;
	}

	public void setCallback(final Runnable callback) {
		this.callback = callback;
	}

	@Override
	public void run() {
		try {
			clazz = classLoader.loadClass("client");
			Constructor<?> constructor = clazz.getConstructor((Class[]) null);
			client = constructor.newInstance((Object[]) null);

			invoke(new Object[]{this}, new Class[]{Applet.class}, "supplyApplet");
			callback.run();
			init();
			start();
		} catch (final Exception ignored) {
			log.severe("Unable to load client, please check your firewall and internet connection.");
		}
	}

	@Override
	public final void init() {
		safeInvoke("init");
	}

	@Override
	public final void start() {
		safeInvoke("start");
	}

	@Override
	public final void stop() {
		safeInvoke("stop");
	}

	@Override
	public final void destroy() {
		safeInvoke("destroy");
	}

	@Override
	public final void paint(final Graphics render) {
		invoke(new Object[]{render}, new Class[]{Graphics.class}, "paint");
	}

	@Override
	public final void update(final Graphics render) {
		safeInvoke(new Object[]{render}, new Class[]{Graphics.class}, "update");
	}

	@Override
	public boolean isShowing() {
		return true;
	}

	private void safeInvoke(final String name) {
		safeInvoke(null, null, name);
	}

	private void safeInvoke(final Object[] parameters, final Class<?>[] parameterTypes, final String name) {
		if (client != null) {
			invoke(parameters, parameterTypes, name);
		}
	}

	private void invoke(final Object[] parameters, final Class<?>[] parameterTypes, final String name) {
		try {
			final Method method = clazz.getMethod(name, parameterTypes);
			method.invoke(client, parameters);
		} catch (final Exception e) {
			log.log(Level.SEVERE, "Error invoking client method: ", e);
		}
	}
}
