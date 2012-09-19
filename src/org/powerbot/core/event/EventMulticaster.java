package org.powerbot.core.event;

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
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

import org.powerbot.core.event.listeners.MessageListener;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.event.listeners.TextPaintListener;

public class EventMulticaster implements EventManager {
	private final CopyOnWriteArrayList<EventListener> listeners;
	private final Map<EventListener, Long> listenerMasks;
	private final Queue<EventObject> queue;

	private boolean active;

	public static final int MOUSE_EVENT = 0x1;
	public static final int MOUSE_MOTION_EVENT = 0x2;
	public static final int MOUSE_WHEEL_EVENT = 0x4;
	public static final int FOCUS_EVENT = 0x8;
	public static final int KEY_EVENT = 0x10;

	public static final int MESSAGE_EVENT = 0x20;
	public static final int PAINT_EVENT = 0x40;
	public static final int TEXT_PAINT_EVENT = 0x80;

	public EventMulticaster() {
		listeners = new CopyOnWriteArrayList<>();
		listenerMasks = new ConcurrentHashMap<>();
		queue = new ConcurrentLinkedQueue<>();

		active = true;
	}

	@Override
	public void dispatch(final EventObject event) {
		queue.offer(event);

		synchronized (queue) {
			try {
				queue.notify();
			} catch (final IllegalThreadStateException ignored) {
			}
		}
	}

	@Override
	public void fire(final EventObject eventObject) {
		fire(eventObject, getType(eventObject));
	}

	private void fire(final EventObject eventObject, final int type) {
		if (!active) {
			return;
		}
		for (final EventListener listener : listeners) {
			final long mask = listenerMasks.get(listener);
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
			} else if (eventObject instanceof GameEvent) {
				final GameEvent gameEvent = (GameEvent) eventObject;
				gameEvent.dispatch(listener);
			}
		}
	}

	@Override
	public void addListener(final EventListener eventListener) {
		if (!listeners.addIfAbsent(eventListener)) {
			listenerMasks.put(eventListener, getMask(eventListener));
		}
	}

	@Override
	public void removeListener(final EventListener eventListener) {
		listeners.remove(eventListener);
		listenerMasks.remove(eventListener);
	}

	@Override
	public EventListener[] getListeners() {
		return (EventListener[]) this.listeners.toArray();
	}

	@Override
	public void stop() {
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

	private static long getMask(final EventListener listener) {
		long mask = 0;
		if (listener instanceof MouseListener) {
			mask |= EventMulticaster.MOUSE_EVENT;
		}
		if (listener instanceof MouseMotionListener) {
			mask |= EventMulticaster.MOUSE_MOTION_EVENT;
		}
		if (listener instanceof MouseWheelListener) {
			mask |= EventMulticaster.MOUSE_WHEEL_EVENT;
		}
		if (listener instanceof KeyListener) {
			mask |= EventMulticaster.KEY_EVENT;
		}
		if (listener instanceof FocusListener) {
			mask |= EventMulticaster.FOCUS_EVENT;
		}

		if (listener instanceof MessageListener) {
			mask |= EventMulticaster.MESSAGE_EVENT;
		}
		if (listener instanceof PaintListener) {
			mask |= EventMulticaster.PAINT_EVENT;
		}
		if (listener instanceof TextPaintListener) {
			mask |= EventMulticaster.TEXT_PAINT_EVENT;
		}

		return mask;
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
		} else if (e instanceof GameEvent) {
			return ((GameEvent) e).type;
		}

		throw new RuntimeException("bad event");
	}
}
