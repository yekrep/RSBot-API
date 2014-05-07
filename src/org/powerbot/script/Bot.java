package org.powerbot.script;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.Insets;
import java.io.Closeable;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.ScriptClassLoader;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.IOUtils;

public abstract class Bot<C extends ClientContext<? extends Client>> implements Runnable, Closeable {
	protected final Logger log = Logger.getLogger(getClass().getName());
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
		ctx = newContext();
	}

	protected abstract C newContext();

	protected void display() {
		chrome.panel.setVisible(false);
		chrome.add(applet);
		final Dimension d = applet.getMinimumSize(), d2 = chrome.getJMenuBar().getSize();
		final Insets s = chrome.getInsets();
		chrome.setMinimumSize(new Dimension(d.width + s.right + s.left, d.height + s.top + s.bottom + d2.height));
		chrome.pack();
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

		chrome.bot.set(null);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				chrome.reset();
			}
		});
	}

	protected void clearPreferences() {
		final File root = new File(System.getProperty("user.home"));

		final File random = new File(root, "random.dat");
		if (random.isFile()) {
			random.delete();
		}

		final File path = new File(root, String.format("jagex_cl_%s_LIVE.dat", ctx.rtv().equals("4") ? "oldschool" : "runescape"));
		if (!path.isFile()) {
			return;
		}

		final byte[] b = IOUtils.read(path);
		String s = "";
		for (int i = 0; i < b.length; i++) {
			if (b[i] == '&' || b[i] == '(') {
				try {
					s = new String(b, ++i, b.length - i, "UTF-8");
				} catch (final UnsupportedEncodingException ignored) {
					return;
				}
				break;
			}
		}
		if (s.isEmpty()) {
			return;
		}

		final File live = new File(s);
		if (!live.isDirectory()) {
			return;
		}

		final File[] files = live.listFiles();
		if (files == null) {
			return;
		}

		for (final File f : files) {
			final String n = f.getName();
			if (n.startsWith("preferences") && n.endsWith(".dat")) {
				f.delete();
			}
		}
	}
}
