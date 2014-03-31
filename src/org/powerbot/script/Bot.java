package org.powerbot.script;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.Closeable;
import java.util.concurrent.atomic.AtomicBoolean;

import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.ScriptClassLoader;
import org.powerbot.gui.BotChrome;

public abstract class Bot<C extends ClientContext<? extends Client>> implements Runnable, Closeable {
	public final C ctx;
	protected final BotChrome chrome;
	public final EventDispatcher dispatcher;
	public final ThreadGroup threadGroup;
	public Applet applet;
	public final AtomicBoolean pending;

	public Bot(final BotChrome chrome, final EventDispatcher dispatcher) {
		this.chrome = chrome;
		this.dispatcher = dispatcher;
		threadGroup = new ThreadGroup("game"); // TODO: mask in live mode
		pending = new AtomicBoolean(false);
		ctx = ctx();
	}

	protected abstract C ctx();

	protected void display() {
		chrome.getContentPane().removeAll();
		chrome.add(applet);
		final Dimension d = applet.getMinimumSize();
		final Insets s = chrome.getInsets();
		chrome.setMinimumSize(new Dimension(d.width + s.right + s.left, d.height + s.top + s.bottom));
		chrome.pack();
	}

	@Override
	public void close() {
		ctx().controller().stop();
		if (Thread.currentThread().getContextClassLoader() instanceof ScriptClassLoader) {
			return;
		}

		dispatcher.close();

		if (applet != null) {
			new Thread(threadGroup, new Runnable() {
				@Override
				public void run() {
					applet.stop();
					applet.destroy();
					threadGroup.interrupt();
				}
			}).start();
			ctx().client(null);
		} else {
			threadGroup.interrupt();
		}
	}
}
