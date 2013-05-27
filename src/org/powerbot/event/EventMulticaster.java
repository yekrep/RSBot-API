package org.powerbot.event;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.script.framework.Stoppable;

public class EventMulticaster implements Runnable, Stoppable {
	public static final int MOUSE_EVENT = 0x1;
	public static final int MOUSE_MOTION_EVENT = 0x2;
	public static final int MOUSE_WHEEL_EVENT = 0x4;
	public static final int FOCUS_EVENT = 0x8;
	public static final int KEY_EVENT = 0x10;
	private final CopyOnWriteArrayList<EventListener> listeners;
	private final Map<EventListener, Long> listenerMasks;
	private final Queue<EventObject> queue;
	private final Map<Class<? extends EventListener>, Integer> masks;
	private boolean active, stopping = false;

	public EventMulticaster() {
		listeners = new CopyOnWriteArrayList<>();
		listenerMasks = new ConcurrentHashMap<>();
		queue = new ConcurrentLinkedQueue<>();

		masks = new HashMap<>();
		masks.put(MouseListener.class, EventMulticaster.MOUSE_EVENT);
		masks.put(MouseMotionListener.class, EventMulticaster.MOUSE_MOTION_EVENT);
		masks.put(MouseWheelListener.class, EventMulticaster.MOUSE_WHEEL_EVENT);
		masks.put(KeyListener.class, EventMulticaster.KEY_EVENT);
		masks.put(FocusListener.class, EventMulticaster.FOCUS_EVENT);
		masks.put(MessageListener.class, MessageEvent.ID);
		masks.put(PaintListener.class, PaintEvent.ID);
		masks.put(TextPaintListener.class, TextPaintEvent.ID);

		active = true;
	}

	private static int getType(final EventObject e) {
		if (e instanceof MouseEvent) {
			final MouseEvent me = (MouseEvent) e;
			switch (me.getID()) {
			case MouseEvent.MOUSE_PRESSED:
			case MouseEvent.MOUSE_RELEASED:
			case MouseEvent.MOUSE_CLICKED:
			case MouseEvent.MOUSE_ENTERED:
			case MouseEvent.MOUSE_EXITED:
				return EventMulticaster.MOUSE_EVENT;

			case MouseEvent.MOUSE_MOVED:
			case MouseEvent.MOUSE_DRAGGED:
				return EventMulticaster.MOUSE_MOTION_EVENT;

			case MouseEvent.MOUSE_WHEEL:
				return EventMulticaster.MOUSE_WHEEL_EVENT;
			}
		} else if (e instanceof FocusEvent) {
			final FocusEvent fe = (FocusEvent) e;
			switch (fe.getID()) {
			case FocusEvent.FOCUS_GAINED:
			case FocusEvent.FOCUS_LOST:
				return EventMulticaster.FOCUS_EVENT;
			}
		} else if (e instanceof KeyEvent) {
			final KeyEvent ke = (KeyEvent) e;
			switch (ke.getID()) {
			case KeyEvent.KEY_TYPED:
			case KeyEvent.KEY_PRESSED:
			case KeyEvent.KEY_RELEASED:
				return EventMulticaster.KEY_EVENT;
			}
		} else if (e instanceof AbstractEvent) {
			return ((AbstractEvent) e).id;
		}

		throw new RuntimeException("bad event");
	}

	private long getMask(final EventListener listener) {
		long mask = 0;

		for (final Entry<Class<? extends EventListener>, Integer> entry : masks.entrySet()) {
			if (entry.getKey().isInstance(listener)) {
				mask |= entry.getValue();
			}
		}

		return mask;
	}

	public void dispatch(final EventObject event) {
		queue.offer(event);

		synchronized (queue) {
			try {
				queue.notify();
			} catch (final IllegalThreadStateException ignored) {
			}
		}
	}

	public void fire(final EventObject eventObject) {
		fire(eventObject, getType(eventObject));
	}

	private void fire(final EventObject eventObject, final int type) {
		if (!active) {
			return;
		}
		for (final EventListener listener : listeners) {
			final Long mask = listenerMasks.get(listener);
			if (mask == null) {
				continue;
			}
			if ((mask & type) == 0) {
				continue;
			}

			if (eventObject instanceof MouseEvent) {
				final MouseEvent me = (MouseEvent) eventObject;
				switch (me.getID()) {
				case MouseEvent.MOUSE_PRESSED:
					((MouseListener) listener).mousePressed(me);
					break;
				case MouseEvent.MOUSE_RELEASED:
					((MouseListener) listener).mouseReleased(me);
					break;
				case MouseEvent.MOUSE_CLICKED:
					((MouseListener) listener).mouseClicked(me);
					break;
				case MouseEvent.MOUSE_ENTERED:
					((MouseListener) listener).mouseEntered(me);
					break;
				case MouseEvent.MOUSE_EXITED:
					((MouseListener) listener).mouseExited(me);
					break;
				case MouseEvent.MOUSE_MOVED:
					((MouseMotionListener) listener).mouseMoved(me);
					break;
				case MouseEvent.MOUSE_DRAGGED:
					((MouseMotionListener) listener).mouseDragged(me);
					break;
				case MouseEvent.MOUSE_WHEEL:
					((MouseWheelListener) listener).mouseWheelMoved((MouseWheelEvent) me);
					break;
				}
				break;
			} else if (eventObject instanceof FocusEvent) {
				final FocusEvent focusEvent = (FocusEvent) listener;
				switch (focusEvent.getID()) {
				case FocusEvent.FOCUS_GAINED:
					((FocusListener) listener).focusGained(focusEvent);
					break;
				case FocusEvent.FOCUS_LOST:
					((FocusListener) listener).focusLost(focusEvent);
					break;
				}
			} else if (eventObject instanceof KeyEvent) {
				final KeyEvent ke = (KeyEvent) eventObject;
				switch (ke.getID()) {
				case KeyEvent.KEY_TYPED:
					((KeyListener) listener).keyTyped(ke);
					break;
				case KeyEvent.KEY_PRESSED:
					((KeyListener) listener).keyPressed(ke);
					break;
				case KeyEvent.KEY_RELEASED:
					((KeyListener) listener).keyReleased(ke);
					break;
				}
			} else if (eventObject instanceof AbstractEvent) ((AbstractEvent) eventObject).dispatch(listener);
		}
	}

	public void addListener(final EventListener eventListener) {
		if (listeners.addIfAbsent(eventListener)) {
			listenerMasks.put(eventListener, getMask(eventListener));
		}
	}

	public void removeListener(final EventListener eventListener) {
		listeners.remove(eventListener);
		listenerMasks.remove(eventListener);
	}

	public EventListener[] getListeners() {
		final int size = this.listeners.size();
		return this.listeners.toArray(new EventListener[size]);
	}

	@Override
	public boolean isStopping() {
		return stopping;
	}

	@Override
	public void stop() {
		stopping = true;
		active = false;

		synchronized (queue) {
			try {
				queue.notify();
			} catch (final IllegalThreadStateException ignored) {
			}
		}
	}

	@Override
	public void run() {
		while (active) {
			final EventObject event = queue.poll();
			if (event != null) {
				try {
					fire(event);
				} catch (final Exception ignored) {
				}
				continue;
			}

			try {
				synchronized (queue) {
					queue.wait();
				}
			} catch (final InterruptedException ignored) {
				stop();
				return;
			}
		}
	}
}
