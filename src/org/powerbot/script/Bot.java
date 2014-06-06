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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.powerbot.bot.ClientTransform;
import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.InputSimulator;
import org.powerbot.bot.ScriptClassLoader;
import org.powerbot.gui.BotLauncher;

public abstract class Bot<C extends ClientContext<? extends Client>> implements Runnable, Closeable {
	protected final Logger log = Logger.getLogger("Bot");
	public final C ctx;
	public final BotLauncher launcher;
	public final EventDispatcher dispatcher;
	public final AtomicBoolean pending;
	private AWTEventListener awtel;

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

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			@Override
			public void eventDispatched(final AWTEvent e) {
				final InputSimulator input = (InputSimulator) ctx.input;
				if (launcher.overlay.get() != null && e.getSource().equals(launcher.overlay.get()) && e instanceof InputEvent) {
					input.redirect(e);
					return;
				}

				final Component c = input.getComponent();
				if (c != null && e.getSource().equals(c) && InputSimulator.lastEvent != e) {
					dispatcher.dispatch(e);

					if (e instanceof InputEvent) {
						if (input.blocking()) {
							((InputEvent) e).consume();
						} else {
							input.processEvent(e);
						}
					}
				}
			}
		}, AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);

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

		if (awtel != null) {
			Toolkit.getDefaultToolkit().removeAWTEventListener(awtel);
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
