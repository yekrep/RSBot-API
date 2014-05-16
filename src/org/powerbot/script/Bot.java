package org.powerbot.script;

import java.applet.Applet;
import java.io.Closeable;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.ScriptClassLoader;
import org.powerbot.gui.BotLauncher;

public abstract class Bot<C extends ClientContext<? extends Client>> implements Runnable, Closeable {
	protected final Logger log = Logger.getLogger(getClass().getName());
	public final C ctx;
	public final BotLauncher launcher;
	public final EventDispatcher dispatcher;
	public final ThreadGroup threadGroup;
	public Applet applet;
	public final AtomicBoolean pending;

	public Bot(final BotLauncher launcher, final EventDispatcher dispatcher) {
		this.launcher = launcher;
		this.dispatcher = dispatcher;
		threadGroup = new ThreadGroup("game"); // TODO: mask in live mode
		pending = new AtomicBoolean(false);
		ctx = newContext();

		final File random = new File(System.getProperty("user.home"), "random.dat");
		if (random.isFile()) {
			random.delete();
		}
	}

	protected abstract C newContext();

	protected void display() {
		launcher.menu.get().update();
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
			new Thread(threadGroup, new Runnable() {
				@Override
				public void run() {
					applet.stop();
					applet.destroy();
					threadGroup.interrupt();
				}
			}).start();
			ctx.client(null);
		} else {
			threadGroup.interrupt();
		}

		launcher.bot.set(null);
		launcher.menu.get().update();
	}
}
