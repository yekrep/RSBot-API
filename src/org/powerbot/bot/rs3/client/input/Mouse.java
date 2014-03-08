package org.powerbot.bot.rs3.client.input;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

public abstract class Mouse extends Focus implements MouseListener, MouseMotionListener, MouseWheelListener {
	public abstract void _mouseClicked(MouseEvent e);

	public abstract void _mouseDragged(MouseEvent e);

	public abstract void _mouseEntered(MouseEvent e);

	public abstract void _mouseExited(MouseEvent e);

	public abstract void _mouseMoved(MouseEvent e);

	public abstract void _mousePressed(MouseEvent e);

	public abstract void _mouseReleased(MouseEvent e);

	public abstract void _mouseWheelMoved(MouseWheelEvent e);

	@Override
	public final void mouseClicked(final MouseEvent e) {
		_mouseClicked(e);
	}

	@Override
	public final void mouseDragged(final MouseEvent e) {
		_mouseDragged(e);
	}

	@Override
	public final void mouseEntered(final MouseEvent e) {
		_mouseEntered(e);
	}

	@Override
	public final void mouseExited(final MouseEvent e) {
		_mouseExited(e);
	}

	@Override
	public final void mouseMoved(final MouseEvent e) {
		_mouseMoved(e);
	}

	@Override
	public final void mousePressed(final MouseEvent e) {
		_mousePressed(e);
	}

	@Override
	public final void mouseReleased(final MouseEvent e) {
		_mouseReleased(e);
	}

	@Override
	public void mouseWheelMoved(final MouseWheelEvent e) {
		_mouseWheelMoved(e);
	}
}