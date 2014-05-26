package org.powerbot.script;

import java.applet.Applet;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.EventQueue;
import java.io.Closeable;
import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import org.powerbot.bot.ClientTransform;
import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.ScriptClassLoader;
import org.powerbot.gui.BotLauncher;

public abstract class Bot<C extends ClientContext<? extends Client>> implements Runnable, Closeable {
	protected final Logger log = Logger.getLogger(getClass().getName());
	public final C ctx;
	public final BotLauncher launcher;
	public final AtomicReference<Canvas> canvas;
	public final EventDispatcher dispatcher;
	public final AtomicBoolean pending;

	public Bot(final BotLauncher launcher, final EventDispatcher dispatcher) {
		this.launcher = launcher;
		canvas = new AtomicReference<Canvas>(null);
		this.dispatcher = dispatcher;
		pending = new AtomicBoolean(false);
		ctx = newContext();

		final File random = new File(System.getProperty("user.home"), "random.dat");
		if (random.isFile()) {
			random.delete();
		}
	}

	protected abstract C newContext();

	protected abstract Map<String, byte[]> getClasses();

	@Override
	public final void run() {
		final Map<String, byte[]> c = getClasses();
		final String hash = ClientTransform.hash(c);
		log.info("Hash: " + hash + " size: " + c.size());

		Component[] p;
		do {
			Thread.yield();
			p = ((Applet) launcher.target.get()).getComponents();
		} while (p == null || p.length == 0 || !(p[0] instanceof Canvas));
		canvas.set((Canvas) p[0]);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				launcher.update();
			}
		});
	}

	public boolean overlay() {
		return false;
	}

	@Override
	public void close() {
		ctx.controller.stop();
		if (Thread.currentThread().getContextClassLoader() instanceof ScriptClassLoader) {
			return;
		}

		dispatcher.close();

		final Applet applet = (Applet) launcher.target.get();
		if (applet != null) {
			applet.setVisible(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					applet.stop();
					applet.destroy();
				}
			}).start();
			ctx.client(null);
		}

		launcher.bot.set(null);
		launcher.menu.get().update();
	}
}
