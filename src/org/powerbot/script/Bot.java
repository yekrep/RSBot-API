package org.powerbot.script;

import java.applet.Applet;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.io.Closeable;
import java.io.File;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.powerbot.bot.ClientTransform;
import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.ScriptClassLoader;
import org.powerbot.gui.BotChrome;

public abstract class Bot<C extends ClientContext<? extends Client>> implements Runnable, Closeable {
	protected final Logger log = Logger.getLogger("Bot");
	public final C ctx;
	public final BotChrome chrome;
	protected final Timer timer;
	public final EventDispatcher dispatcher;
	public final AtomicBoolean pending;
	private volatile AWTEventListener awtel;

	public Bot(final BotChrome chrome, final EventDispatcher dispatcher) {
		this.chrome = chrome;
		timer = new Timer(true);
		this.dispatcher = dispatcher;
		pending = new AtomicBoolean(false);
		ctx = newContext();

		final File random = new File(System.getProperty("user.home"), "random.dat");
		if (random.isFile()) {
			random.delete();//TODO: review: we can't delete this...
		}
	}

	protected abstract C newContext();

	protected abstract Map<String, byte[]> getClasses();

	@Override
	public final void run() {
		final Map<String, byte[]> c = getClasses();
		final String hash = ClientTransform.hash(c);
		log.info("Hash: " + hash + " size: " + c.size());
		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(final AWTEvent e) {
				final InputSimulator input = (InputSimulator) ctx.input;
				if (chrome.overlay.get() != null && e.getSource().equals(chrome.overlay.get()) && e instanceof InputEvent) {
					input.redirect(e);
					return;
				}

				final boolean b = input.eq.remove(e);
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

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				chrome.update();
			}
		});
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

		timer.cancel();
		dispatcher.close();

		final Applet applet = (Applet) chrome.target.get();
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
		chrome.menu.get().update();
	}
}
