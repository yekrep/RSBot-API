package org.powerbot.bot;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.gui.BotChrome;

public class SelectiveEventQueue extends EventQueue {
	private static final SelectiveEventQueue instance = new SelectiveEventQueue();
	private AtomicBoolean blocking;
	private AtomicReference<Component> component;
	private AtomicReference<EventCallback> callback;

	private SelectiveEventQueue() {
		this.blocking = new AtomicBoolean(false);
		this.component = new AtomicReference<Component>(null);
		this.callback = new AtomicReference<EventCallback>(null);
	}

	public static SelectiveEventQueue getInstance() {
		return instance;
	}

	public boolean isBlocking() {
		return blocking.get();
	}

	public void setBlocking(boolean blocking) {
		this.blocking.set(blocking);
		pushSelectiveQueue();
	}

	public static void pushSelectiveQueue() {
		if (!isPushed()) {
			Toolkit.getDefaultToolkit().getSystemEventQueue().push(instance);
			if (!isPushed()) {
				throw new RuntimeException();
			}
		}
	}

	public void block(Component component, EventCallback callback) {
		final Component c = this.component.get();
		if (c != null && c != component) {
			defocus();
		}

		this.component.set(component);
		this.callback.set(callback);
	}

	public void focus() {
		if (!isBlocking()) {
			return;
		}

		final Component component = this.component.get();
		if (component != null && (!component.isFocusOwner() || !component.isShowing())) {
			postEvent(new RawAWTEvent(new FocusEvent(component, FocusEvent.FOCUS_GAINED, false, null)));
		}
	}

	public void defocus() {
		if (!isBlocking()) {
			return;
		}

		final Component component = this.component.get();
		if (component != null && component.isFocusOwner()) {
			postEvent(new RawAWTEvent(new FocusEvent(component, FocusEvent.FOCUS_LOST, false, null)));
			postEvent(new RawAWTEvent(new FocusEvent(component, FocusEvent.FOCUS_LOST, false, null)));
		}
	}

	@Override
	protected final void dispatchEvent(AWTEvent event) {
		if (event instanceof RawAWTEvent) {
			AWTEvent e = ((RawAWTEvent) event).getEvent();
			((Component) e.getSource()).dispatchEvent(e);
			return;
		}
		Object source = event.getSource();
		if (source == null) {
			return;
		}

		final Component component = this.component.get();
		final BotChrome chrome = BotChrome.getInstance();
		if (source == chrome.overlay) {
			if (component != null) {
				event.setSource(component);
			} else {
				event.setSource(chrome);
			}
		}
		source = event.getSource();

		/* Check if event is from a blocked source */
		if (blocking.get() && source == component) {
			/* Block input events */
			if (event instanceof MouseEvent || event instanceof KeyEvent ||
					event instanceof WindowEvent || event instanceof FocusEvent) {
				/* If an input event is blocked, dispatch it on our event caster. */
				if (event instanceof MouseEvent || event instanceof KeyEvent) {
					BotChrome.getInstance().getBot().dispatcher.dispatch(event);
				}
				/* Execute a callback for this source when we block an event */
				EventCallback callback = this.callback.get();
				if (callback != null) {
					callback.execute(event);
				}
				return;
			}
		}

		/* Otherwise, dispatch events to everything else non-blocked */
		super.dispatchEvent(event);
	}


	private static boolean isPushed() {
		return Toolkit.getDefaultToolkit().getSystemEventQueue() instanceof SelectiveEventQueue;
	}
}
