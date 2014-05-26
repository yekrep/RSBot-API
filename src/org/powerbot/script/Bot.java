package org.powerbot.script;

import java.applet.Applet;
import java.awt.EventQueue;
import java.io.Closeable;
import java.io.File;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.powerbot.bot.ClientTransform;
import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.ScriptClassLoader;
import org.powerbot.gui.BotLauncher;

public abstract class Bot<C extends ClientContext<? extends Client>> implements Runnable, Closeable {
	protected final Logger log = Logger.getLogger(getClass().getName());
	public final C ctx;
	public final BotLauncher launcher;
	public final EventDispatcher dispatcher;
	public Applet applet;
	public final AtomicBoolean pending;

	public Bot(final BotLauncher launcher, final EventDispatcher dispatcher) {
		this.launcher = launcher;
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

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				launcher.menu.get().update();
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
