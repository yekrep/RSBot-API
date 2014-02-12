package org.powerbot.os.bot;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.os.api.internal.InputEngine;
import org.powerbot.os.gui.BotChrome;

public class SelectiveEventQueue extends EventQueue {
	private static final SelectiveEventQueue instance = new SelectiveEventQueue();
	private final AtomicBoolean blocking;
	private final AtomicReference<InputEngine> engine;
	private final AtomicReference<EventCallback> callback;

	private SelectiveEventQueue() {
		blocking = new AtomicBoolean(false);
		engine = new AtomicReference<InputEngine>(null);
		callback = new AtomicReference<EventCallback>(null);
	}

	public static SelectiveEventQueue getInstance() {
		return instance;
	}

	public static void pushSelectiveQueue() {
		if (!isPushed()) {
			Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
			if (!isPushed()) {
				throw new RuntimeException();
			}
		}
	}

	private static boolean isPushed() {
		return Toolkit.getDefaultToolkit().getSystemEventQueue() instanceof SelectiveEventQueue;
	}

	public boolean isBlocking() {
		return blocking.get();
	}

	public void setBlocking(final boolean blocking) {
		this.blocking.set(blocking);
		if (!blocking) {
			final InputEngine e = engine.get();
			if (e != null) {
				e.destroy();
			}
			engine.set(null);
		} else {
			pushSelectiveQueue();
		}
	}

	public void block(final Component component, final EventCallback callback) {
		final InputEngine engine = this.engine.get();
		final Component c = engine != null ? engine.getComponent() : null;
		if (c == component) {
			return;
		}
		final boolean b = isBlocking() || engine == null;
		setBlocking(false);
		this.engine.set(new InputEngine(component));
		this.callback.set(callback);
		final BotChrome chrome = BotChrome.getInstance();
		if (b) {
			setBlocking(true);
			chrome.requestFocusInWindow();
		}
	}

	@Override
	protected final void dispatchEvent(final AWTEvent event) {
		if (event instanceof RawAWTEvent) {
			final AWTEvent e = ((RawAWTEvent) event).getEvent();
			((Component) e.getSource()).dispatchEvent(e);
			return;
		}
		final Object source = event.getSource();
		if (source == null) {
			return;
		}
		final InputEngine engine = this.engine.get();
		final Component component = engine != null ? engine.getComponent() : null;
		/* Check if event is from a blocked source */
		if (blocking.get() && source == component) {
			/* Block input events */
			if (event instanceof MouseEvent || event instanceof KeyEvent ||
					event instanceof WindowEvent || event instanceof FocusEvent) {
				/* If an input event is blocked, dispatch it on our event caster. */
				if (event instanceof MouseEvent || event instanceof KeyEvent) {
					BotChrome.getInstance().bot.get().dispatcher.dispatch(event);
				}
				/* Execute a callback for this source when we block an event */
				final EventCallback callback = this.callback.get();
				if (callback != null) {
					callback.execute(event);
				}
				return;
			}
		}

		/* Otherwise, dispatch events to everything else non-blocked */
		super.dispatchEvent(event);
	}
}
