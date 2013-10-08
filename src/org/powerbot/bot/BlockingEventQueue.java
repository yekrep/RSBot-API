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

import org.powerbot.gui.BotChrome;

/**
 * @author Timer
 */
public class BlockingEventQueue extends EventQueue {
	/* We only want one instance of this -- ever */
	private static BlockingEventQueue eventQueue = new BlockingEventQueue();
	private Map<Component, EventCallback> callbacks = new ConcurrentHashMap<>();

	private BlockingEventQueue() {
	}

	public static BlockingEventQueue getEventQueue() {
		return eventQueue;
	}

	public void addComponent(Component component, EventCallback callback) {
		callbacks.put(component, callback);
	}

	public void removeComponent(Component component) {
		callbacks.remove(component);
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
		if (event instanceof RawAWTEvent) {
			AWTEvent e = ((RawAWTEvent) event).getEvent();
			((Component) e.getSource()).dispatchEvent(e);
			return;
		}

		Object source = event.getSource();
		/* Check if event is from a blocked source */
		statement:
		if (source != null && callbacks.containsKey(source)) {
			int mask = BotChrome.getInstance().getInputMask();
			if (event instanceof MouseEvent && (mask & BotChrome.INPUT_MOUSE) != 0) {
				break statement;
			}
			if (event instanceof KeyEvent && (mask & BotChrome.INPUT_KEYBOARD) != 0) {
				break statement;
			}
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
}