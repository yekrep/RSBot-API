package org.powerbot.gui.component;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class BotHoverLabel extends JLabel implements MouseListener {
	private static final long serialVersionUID = 9128610756239839615L;
	private final List<ActionListener> listeners;
	private Icon initial, hover;

	public BotHoverLabel(final String initial, final String hover) {
		super();

		this.initial = new ImageIcon(Resources.getImage(initial));
		this.hover = new ImageIcon(Resources.getImage(hover));

		setPreferredSize(new Dimension(20, 20));
		super.setIcon(this.initial);
		addMouseListener(this);
		listeners = new ArrayList<ActionListener>(1);
	}

	public void addActionListener(final ActionListener l) {
		listeners.add(l);
	}

	@Override
	public void setIcon(final Icon icon) {
		setIcon(icon, true);
	}

	public void setIcon(final Icon icon, final boolean initial) {
		if (initial) {
			this.initial = icon;
		} else {
			hover = icon;
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		super.setIcon(hover);
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		super.setIcon(initial);
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		for (final ActionListener l : listeners) {
			final ActionEvent ae = new ActionEvent(e.getSource(), e.getID(), null, e.getWhen(), e.getModifiers());
			l.actionPerformed(ae);
		}
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}
}
