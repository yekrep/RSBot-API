package org.powerbot.script;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.io.Closeable;
import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.ScriptClassLoader;
import org.powerbot.gui.BotChrome;

public abstract class Bot<C extends ClientContext<? extends Client>> implements Runnable, Closeable {
	protected final Logger log = BotChrome.log;
	public final C ctx;
	protected final BotChrome chrome;
	public final EventDispatcher dispatcher;
	public Applet applet;
	public final AtomicBoolean pending;
	private volatile AWTEventListener awtel;

	public Bot(final BotChrome chrome, final EventDispatcher dispatcher) {
		this.chrome = chrome;
		this.dispatcher = dispatcher;
		pending = new AtomicBoolean(false);
		ctx = newContext();

		final File random = new File(System.getProperty("user.home"), "random.dat");
		if (random.isFile()) {
			random.delete();
		}
	}

	protected abstract C newContext();

	protected void display() {
		chrome.panel.setVisible(false);
		chrome.add(applet);
		final Dimension d = applet.getMinimumSize(), d2 = chrome.getJMenuBar().getSize();
		final Insets s = chrome.getInsets();
		chrome.setMinimumSize(new Dimension(d.width + s.right + s.left, d.height + s.top + s.bottom + d2.height));
		chrome.pack();

		Toolkit.getDefaultToolkit().addAWTEventListener(awtel = new AWTEventListener() {
			@Override
			public void eventDispatched(final AWTEvent e) {
				final InputSimulator input = (InputSimulator) ctx.input;
				if (chrome.overlay.get() != null && e.getSource().equals(chrome.overlay.get())) {
					input.redirect(e);
					return;
				}

				final boolean b = InputSimulator.eventQueue.remove(e);
				final Component c = input.getComponent();
				if (c != null && e.getSource().equals(c) && !b) {
					dispatcher.dispatch(e);

					if (input.blocking()) {
						((InputEvent) e).consume();
					} else {
						input.processEvent(e);
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK + AWTEvent.MOUSE_EVENT_MASK + AWTEvent.MOUSE_MOTION_EVENT_MASK + AWTEvent.MOUSE_WHEEL_EVENT_MASK);
	}

	@Override
	public void close() {
		ctx.controller.stop();
		if (Thread.currentThread().getContextClassLoader() instanceof ScriptClassLoader) {
			return;
		}

		if (awtel != null) {
			Toolkit.getDefaultToolkit().removeAWTEventListener(awtel);
			awtel = null;
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

		chrome.bot.set(null);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				chrome.reset();
			}
		});
	}
}
