package org.powerbot.os.bot.event;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.AbstractCollection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class EventDispatcher extends AbstractCollection<EventListener> implements Runnable {
	public static final int MOUSE_EVENT = 0x1;
	public static final int MOUSE_MOTION_EVENT = 0x2;
	public static final int MOUSE_WHEEL_EVENT = 0x4;
	public static final int FOCUS_EVENT = 0x8;
	public static final int KEY_EVENT = 0x10;

	private final AtomicReference<Thread> thread;
	private final CopyOnWriteArrayList<EventListener> listeners;
	private final Map<EventListener, Long> bitmasks;
	private final BlockingQueue<EventObject> queue;
	private final Map<Class<? extends EventListener>, Integer> masks;

	public EventDispatcher() {
		thread = new AtomicReference<Thread>(null);
		listeners = new CopyOnWriteArrayList<EventListener>();
		bitmasks = new ConcurrentHashMap<EventListener, Long>();
		queue = new LinkedBlockingQueue<EventObject>();

		masks = new HashMap<Class<? extends EventListener>, Integer>();
		masks.put(MouseListener.class, EventDispatcher.MOUSE_EVENT);
		masks.put(MouseMotionListener.class, EventDispatcher.MOUSE_MOTION_EVENT);
		masks.put(MouseWheelListener.class, EventDispatcher.MOUSE_WHEEL_EVENT);
		masks.put(KeyListener.class, EventDispatcher.KEY_EVENT);
		masks.put(FocusListener.class, EventDispatcher.FOCUS_EVENT);
		masks.put(PaintListener.class, PaintEvent.PAINT_EVENT);
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
				return EventDispatcher.MOUSE_EVENT;
			case MouseEvent.MOUSE_MOVED:
			case MouseEvent.MOUSE_DRAGGED:
				return EventDispatcher.MOUSE_MOTION_EVENT;
			case MouseEvent.MOUSE_WHEEL:
				return EventDispatcher.MOUSE_WHEEL_EVENT;
			}
		} else if (e instanceof FocusEvent) {
			final FocusEvent fe = (FocusEvent) e;
			switch (fe.getID()) {
			case FocusEvent.FOCUS_GAINED:
			case FocusEvent.FOCUS_LOST:
				return EventDispatcher.FOCUS_EVENT;
			}
		} else if (e instanceof KeyEvent) {
			final KeyEvent ke = (KeyEvent) e;
			switch (ke.getID()) {
			case KeyEvent.KEY_TYPED:
			case KeyEvent.KEY_PRESSED:
			case KeyEvent.KEY_RELEASED:
				return EventDispatcher.KEY_EVENT;
			}
		} else if (e instanceof AbstractEvent) {
			return ((AbstractEvent) e).id;
		}

		throw new IllegalArgumentException("bad event");
	}

	private long getMask(final EventListener e) {
		long m = 0;

		for (final Entry<Class<? extends EventListener>, Integer> entry : masks.entrySet()) {
			if (entry.getKey().isInstance(e)) {
				m |= entry.getValue();
			}
		}

		return m;
	}

	public void dispatch(final EventObject event) {
		queue.offer(event);
	}

	public void consume(final EventObject eventObject) {
		consume(eventObject, getType(eventObject));
	}

	private void consume(final EventObject eventObject, final int type) {
		if (Thread.interrupted()) {
			return;
		}
		for (final EventListener listener : this) {
			final Long mask = bitmasks.get(listener);
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
			} else if (eventObject instanceof AbstractEvent) {
				((AbstractEvent) eventObject).call(listener);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isStopping() {
		return Thread.interrupted();
	}

	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		final Thread t = thread.get();
		if (t != null) {
			t.interrupt();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		if (!thread.compareAndSet(null, Thread.currentThread())) {
			return;
		}

		while (!Thread.interrupted()) {
			final EventObject o;

			try {
				o = queue.take();
			} catch (final InterruptedException ignored) {
				break;
			}

			try {
				consume(o);
			} catch (final Exception ignored) {
			}
		}

		thread.set(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<EventListener> iterator() {
		final Iterator<EventListener> e = listeners.iterator();
		return new Iterator<EventListener>() {
			private volatile EventListener o = null;

			@Override
			public boolean hasNext() {
				return e.hasNext();
			}

			@Override
			public EventListener next() {
				o = e.next();
				return o;
			}

			@Override
			public void remove() {
				if (o == null) {
					throw new IllegalStateException();
				}
				EventDispatcher.this.remove(o);
				o = null;
			}
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return listeners.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean add(final EventListener e) {
		if (listeners.addIfAbsent(e)) {
			bitmasks.put(e, getMask(e));
			return true;
		}

		return false;
	}

	public boolean remove(final EventListener e) {
		if (listeners.remove(e)) {
			bitmasks.remove(e);
			return true;
		}

		return false;
	}

	public boolean contains(final Class<? extends EventListener> o) {
		for (final EventListener e : listeners) {
			if (e.getClass().isAssignableFrom(o)) {
				return true;
			}
		}
		return false;
	}
}
