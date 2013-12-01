package org.powerbot.gui.component;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.powerbot.bot.Bot;
import org.powerbot.bot.SelectiveEventQueue;
import org.powerbot.event.EventMulticaster;
import org.powerbot.gui.BotChrome;

/**
 * @author Paris
 */
public class BotOverlay extends JDialog {
	private final BotChrome parent;

	public BotOverlay(final BotChrome parent) {
		super(parent);
		this.parent = parent;

		final Color a = new Color(0, 0, 0, 0);
		setUndecorated(true);
		getRootPane().setOpaque(false);
		getContentPane().setBackground(a);
		setBackground(a);
		setFocusableWindowState(false);

		addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged(final MouseEvent e) {
				redispatch(e);
			}

			@Override
			public void mouseMoved(final MouseEvent e) {
				redispatch(e);
			}
		});

		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				redispatch(e);
			}

			@Override
			public void mousePressed(final MouseEvent e) {
				redispatch(e);
			}

			@Override
			public void mouseReleased(final MouseEvent e) {
				redispatch(e);
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				redispatch(e);
			}

			@Override
			public void mouseExited(final MouseEvent e) {
				redispatch(e);
			}
		});

		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(final MouseWheelEvent e) {
				redispatch(e);
			}
		});

		final JPanel panel = new JPanel() {
			@Override
			public void paintComponent(final Graphics g) {
				if (g != null) {
					final Bot b = parent.getBot();
					if (b != null) {
						final EventMulticaster m = b.getEventMulticaster();
						if (m != null) {
							m.paint(g);
						}
					}
				}
			}
		};
		panel.setBackground(getBackground());
		add(panel);

		final Point p = parent.getLocation();
		final Insets s = parent.getInsets();
		p.translate(s.left, s.top);
		setLocation(p);
		setSize(parent.getContentPane().getSize());

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					try {
						repaint();
						Thread.sleep(40);
					} catch (final Exception ignored) {
						ignored.printStackTrace();
						break;
					}
				}
			}
		}).start();
	}

	private void redispatch(final AWTEvent e) {
		final SelectiveEventQueue q = SelectiveEventQueue.getInstance();

		if (q.isBlocking()) {
			// TODO: invoke callbacks
			return;
		}

		Component s = parent;

		final Bot b = parent.getBot();
		if (b != null) {
			final Component a = b.getCanvas();
			if (a != null) {
				s = a;
			}
		}

		e.setSource(s);
		s.dispatchEvent(e);
	}
}
