package org.powerbot.bot.nloader;

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

/**
 * @author Timer
 */
public class BlockingEventQueue extends EventQueue {
	/* We only want one instance of this -- ever */
	private static BlockingEventQueue eventQueue = new BlockingEventQueue();
	private Map<Component, EventCallback> callbacks = new ConcurrentHashMap<>();
	private Map<Component, Boolean> blocking = new ConcurrentHashMap<>();

	private BlockingEventQueue() {
	}

	public static BlockingEventQueue getEventQueue() {
		return eventQueue;
	}

	public void addComponent(Component component, EventCallback callback) {
		callbacks.put(component, callback);
	}

	public void setBlocking(Component component, boolean block) {
		if (!callbacks.containsKey(component)) {
			throw new RuntimeException();
		}

		blocking.put(component, block);
	}

	public static void pushBlocking() {
		if (!isPushed()) {
			Toolkit.getDefaultToolkit().getSystemEventQueue().push(eventQueue);
			/* Let's make sure this functionality isn't disabled */
			if (!isPushed()) {
				throw new RuntimeException();
			}
		}
	}

	private static boolean isPushed() {
		return Toolkit.getDefaultToolkit().getSystemEventQueue() instanceof BlockingEventQueue;
	}

	@Override
	protected final void dispatchEvent(AWTEvent event) {
		Object source = event.getSource();
		/* Check if event is from a blocked source */
		if (source != null && callbacks.containsKey(source) &&
				blocking.get(source)) {
			/* Allow typical events, excluding MouseEvent, KeyEvent, WindowEvent, and FocusEvent(s) */
			if (event instanceof MouseEvent || event instanceof KeyEvent ||
					event instanceof WindowEvent || event instanceof FocusEvent) {
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
}
