package org.powerbot.bot.rt6.activation;

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

import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.PaintListener;
import org.powerbot.script.TextPaintListener;

public class EventDispatcher extends org.powerbot.bot.EventDispatcher {
	public static final int MOUSE_EVENT = 0x1;
	public static final int MOUSE_MOTION_EVENT = 0x2;
	public static final int MOUSE_WHEEL_EVENT = 0x4;
	public static final int FOCUS_EVENT = 0x8;
	public static final int KEY_EVENT = 0x10;

	public EventDispatcher() {
		masks.put(MouseListener.class, MOUSE_EVENT);
		masks.put(MouseMotionListener.class, MOUSE_MOTION_EVENT);
		masks.put(MouseWheelListener.class, MOUSE_WHEEL_EVENT);
		masks.put(KeyListener.class, KEY_EVENT);
		masks.put(FocusListener.class, FOCUS_EVENT);
		masks.put(MessageListener.class, MessageEvent.ID);
		masks.put(PaintListener.class, PaintEvent.ID);
		masks.put(TextPaintListener.class, TextPaintEvent.ID);
	}

	@Override
	protected int getType(final EventObject e) {
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

		throw new RuntimeException("bad event");
	}

	@Override
	protected void consume(final EventObject e, final int t) {
		for (final EventListener listener : this) {
			final Long mask = bitmasks.get(listener);
			if (mask == null) {
				continue;
			}
			if ((mask & t) == 0) {
				continue;
			}

			if (e instanceof MouseEvent) {
				final MouseEvent me = (MouseEvent) e;
				switch (me.getID()) {
				case MouseEvent.MOUSE_PRESSED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((MouseListener) listener).mousePressed(me);
						}
					});
					break;
				case MouseEvent.MOUSE_RELEASED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((MouseListener) listener).mouseReleased(me);
						}
					});
					break;
				case MouseEvent.MOUSE_CLICKED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((MouseListener) listener).mouseClicked(me);
						}
					});
					break;
				case MouseEvent.MOUSE_ENTERED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((MouseListener) listener).mouseEntered(me);
						}
					});
					break;
				case MouseEvent.MOUSE_EXITED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((MouseListener) listener).mouseExited(me);
						}
					});
					break;
				case MouseEvent.MOUSE_MOVED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((MouseMotionListener) listener).mouseMoved(me);
						}
					});
					break;
				case MouseEvent.MOUSE_DRAGGED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((MouseMotionListener) listener).mouseDragged(me);
						}
					});
					break;
				case MouseEvent.MOUSE_WHEEL:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((MouseWheelListener) listener).mouseWheelMoved((MouseWheelEvent) me);
						}
					});
					break;
				}
				break;
			} else if (e instanceof FocusEvent) {
				final FocusEvent focusEvent = (FocusEvent) listener;
				switch (focusEvent.getID()) {
				case FocusEvent.FOCUS_GAINED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((FocusListener) listener).focusGained(focusEvent);
						}
					});
					break;
				case FocusEvent.FOCUS_LOST:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((FocusListener) listener).focusLost(focusEvent);
						}
					});
					break;
				}
			} else if (e instanceof KeyEvent) {
				final KeyEvent ke = (KeyEvent) e;
				switch (ke.getID()) {
				case KeyEvent.KEY_TYPED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((KeyListener) listener).keyTyped(ke);
						}
					});
					break;
				case KeyEvent.KEY_PRESSED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((KeyListener) listener).keyPressed(ke);
						}
					});
					break;
				case KeyEvent.KEY_RELEASED:
					exec.submit(new Runnable() {
						@Override
						public void run() {
							((KeyListener) listener).keyReleased(ke);
						}
					});
					break;
				}
			} else if (e instanceof AbstractEvent) {
				exec.submit(new Runnable() {
					@Override
					public void run() {
						((AbstractEvent) e).dispatch(listener);
					}
				});
			}
		}
	}
}
