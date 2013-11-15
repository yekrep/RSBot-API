package org.powerbot.bot;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.powerbot.gui.BotChrome;

public class SelectiveEventQueue extends EventQueue {
	private static final SelectiveEventQueue instance = new SelectiveEventQueue();
	private AtomicBoolean blocking;
	private Map<Component, EventCallback> callbacks = new ConcurrentHashMap<Component, EventCallback>();

	private SelectiveEventQueue() {
		this.blocking = new AtomicBoolean(false);
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

	public void addComponent(Component component, EventCallback callback) {
		callbacks.put(component, callback);
	}

	public void removeComponent(Component component) {
		callbacks.remove(component);
	}

	@Override
	protected final void dispatchEvent(AWTEvent event) {
		if (event instanceof RawAWTEvent) {
			AWTEvent e = ((RawAWTEvent) event).getEvent();
			((Component) e.getSource()).dispatchEvent(e);
			return;
		}

		Object source = event.getSource();
		/* Check if event is from a blocked source */
		if (source != null && blocking.get() && callbacks.containsKey(source)) {
		    /* Block input events */
			if (event instanceof MouseEvent || event instanceof KeyEvent ||
					event instanceof WindowEvent || event instanceof FocusEvent) {
				/* If an input event is blocked, dispatch it on our event caster. */
				if (event instanceof MouseEvent || event instanceof KeyEvent) {
					BotChrome.getInstance().getBot().getEventMulticaster().dispatch(event);
				}
				/* Execute a callback for this source when we block an event */
				EventCallback callback = callbacks.get(source);
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
