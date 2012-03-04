package org.powerbot.gui.component;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.input.Mouse;
import org.powerbot.gui.BotChrome;

/**
 * A panel that re-dispatches human events to the game's applet.
 * Contains an image buffered from the applet.
 *
 * @author Timer
 */
public class BotPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private Bot bot;
	private int xOff, yOff;

	public BotPanel() {
		final Dimension d = new Dimension(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
		setSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
		setBackground(Color.black);
		bot = null;
		xOff = yOff = 0;

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				if (bot != null) {
					bot.resize(getWidth(), getHeight());
					offset();
				}
				requestFocus();
			}
		});
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				redispatch(e);
				if (!hasFocus()) {
					requestFocus();
				}
			}

			@Override
			public void mouseEntered(final MouseEvent e) {
				redispatch(e);
			}

			@Override
			public void mouseExited(final MouseEvent e) {
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
		});
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
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(final MouseWheelEvent e) {
				redispatch(e);
			}
		});
		addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(final KeyEvent e) {
				redispatch(e);
			}

			@Override
			public void keyReleased(final KeyEvent e) {
				redispatch(e);
			}

			@Override
			public void keyTyped(final KeyEvent e) {
				redispatch(e);
			}
		});
	}

	@Override
	public void paintComponent(final Graphics g) {
		super.paintComponent(g);

		if (bot != null) {
			g.drawImage(bot.image, xOff, yOff, null);
		}
	}

	public void setBot(final Bot bot) {
		if (this.bot != null) {
			this.bot.setPanel(null);
		}
		this.bot = bot;
		if (bot != null) {
			bot.setPanel(this);
			if (bot.getCanvas() != null) {
				offset();
			}
		}
	}

	public void offset() {
		if (bot != null) {
			final Canvas canvas = bot.getCanvas();
			if (canvas != null) {
				xOff = (getWidth() - canvas.getWidth()) / 2;
				yOff = (getHeight() - canvas.getHeight()) / 2;
			}
		}
	}

	private void redispatch(final MouseEvent mouseEvent) {
		if (mouseEvent == null || bot == null || bot.appletContainer == null || bot.appletContainer.getComponentCount() == 0 ||
				bot.client == null) {
			return;
		}
		mouseEvent.translatePoint(-xOff, -yOff);
		final Mouse mouse = bot.client.getMouse();
		if (mouse == null) {
			return;
		}
		final boolean present = mouse.isPresent();
		final Component component = bot.appletContainer.getComponent(0);
		notifyListeners(component, mouseEvent, present);
		final int mouseX = mouseEvent.getX(), mouseY = mouseEvent.getY();
		if (mouseEvent.getID() != MouseEvent.MOUSE_EXITED &&
				mouseX > 0 && mouseX < component.getWidth() && mouseY > 0 && mouseY < component.getHeight()) {
			if (present) {
				if (mouseEvent instanceof MouseWheelEvent) {
					final MouseWheelEvent mouseWheelEvent = (MouseWheelEvent) mouseEvent;
					component.dispatchEvent(new MouseWheelEvent(
							component, mouseEvent.getID(),
							System.currentTimeMillis(), 0,
							mouseX, mouseY, 0, mouseEvent.isPopupTrigger(),
							mouseWheelEvent.getScrollType(), mouseWheelEvent.getScrollAmount(), mouseWheelEvent.getWheelRotation()
							));
				} else {
					component.dispatchEvent(new MouseEvent(
							component, mouseEvent.getID(),
							System.currentTimeMillis(), 0,
							mouseX, mouseY, 0, mouseEvent.isPopupTrigger(),
							mouseEvent.getButton()
							));
				}
			} else {
				component.dispatchEvent(new MouseEvent(
						component, MouseEvent.MOUSE_ENTERED,
						System.currentTimeMillis(), 0,
						mouseX, mouseY, 0, false
						));
			}
		} else if (present) {
			component.dispatchEvent(new MouseEvent(
					component, MouseEvent.MOUSE_EXITED,
					System.currentTimeMillis(), 0,
					mouseX, mouseY, 0, false
					));
		}
	}

	private void redispatch(final KeyEvent keyEvent) {
		if (keyEvent == null || bot == null || bot.appletContainer == null || bot.appletContainer.getComponentCount() == 0 ||
				bot.client == null) {
			return;
		}
		bot.eventDispatcher.dispatch(keyEvent);
		final Component component = bot.appletContainer.getComponent(0);
		if (component != null) {
			component.dispatchEvent(keyEvent);
		}
	}

	private void notifyListeners(final Component component, final MouseEvent mouseEvent, final boolean present) {
		if (component != null && mouseEvent != null) {
			final int mouseX = mouseEvent.getX(), mouseY = mouseEvent.getY();
			if (mouseX > 0 && mouseX < component.getWidth() && mouseY > 0 && mouseY < component.getHeight() && mouseEvent.getID() != MouseEvent.MOUSE_EXITED) {
				if (present) {
					bot.eventDispatcher.dispatch(mouseEvent);
				} else {
					bot.eventDispatcher.dispatch(new MouseEvent(component, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis(), 0, mouseX, mouseY, 0, false));
				}
			} else if (present) {
				bot.eventDispatcher.dispatch(new MouseEvent(component, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, mouseX, mouseY, 0, false));
			}
		}
	}
}
