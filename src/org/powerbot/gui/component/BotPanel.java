package org.powerbot.gui.component;

import org.powerbot.game.bot.Bot;
import org.powerbot.game.client.input.Mouse;
import org.powerbot.gui.BotChrome;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
		this.bot = null;
		this.xOff = this.yOff = 0;

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent evt) {
				if (bot != null) {
					bot.resize(getWidth(), getHeight());
					offset();
				}
				requestFocus();
			}
		});
		addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				redispatch(e);
				if (!hasFocus()) {
					requestFocus();
				}
			}

			public void mouseEntered(MouseEvent e) {
				redispatch(e);
			}

			public void mouseExited(MouseEvent e) {
				redispatch(e);
			}

			public void mousePressed(MouseEvent e) {
				redispatch(e);
			}

			public void mouseReleased(MouseEvent e) {
				redispatch(e);
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				redispatch(e);
			}

			public void mouseMoved(MouseEvent e) {
				redispatch(e);
			}
		});
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				redispatch(e);
			}
		});
		addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				redispatch(e);
			}

			public void keyReleased(KeyEvent e) {
				redispatch(e);
			}

			public void keyTyped(KeyEvent e) {
				redispatch(e);
			}
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (bot != null) {
			g.drawImage(bot.image, xOff, yOff, null);
		}
	}

	public void setBot(Bot bot) {
		if (this.bot != null) {
			this.bot.setPanel(null);
		}
		if (bot != null) {
			this.bot = bot;
			bot.setPanel(this);
			if (bot.getCanvas() != null) {
				offset();
			}
		}
	}

	public void offset() {
		if (bot != null) {
			Canvas canvas = bot.getCanvas();
			if (canvas != null) {
				xOff = (getWidth() - canvas.getWidth()) / 2;
				yOff = (getHeight() - canvas.getHeight()) / 2;
			}
		}
	}

	private void redispatch(MouseEvent mouseEvent) {
		if (mouseEvent == null || bot == null || bot.appletContainer == null || bot.appletContainer.getComponentCount() == 0 ||
				bot.client == null) {
			return;
		}
		mouseEvent.translatePoint(-xOff, -yOff);
		Mouse mouse = bot.client.getMouse();
		if (mouse == null) {
			return;
		}
		boolean present = mouse.isPresent();
		Component component = bot.appletContainer.getComponent(0);
		dispatchEvent(component, mouseEvent);
		int mouseX = mouseEvent.getX(), mouseY = mouseEvent.getY();
		if (mouseEvent.getID() != MouseEvent.MOUSE_EXITED &&
				mouseX > 0 && mouseX < component.getWidth() && mouseY > 0 && mouseY < component.getHeight()) {
			if (present) {
				if (mouseEvent instanceof MouseWheelEvent) {
					MouseWheelEvent mouseWheelEvent = (MouseWheelEvent) mouseEvent;
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

	private void redispatch(KeyEvent keyEvent) {
		if (keyEvent == null || bot == null || bot.appletContainer == null || bot.appletContainer.getComponentCount() == 0 ||
				bot.client == null) {
			return;
		}
		Component component = bot.appletContainer.getComponent(0);
		dispatchEvent(component, keyEvent);
		if (component != null) {
			component.dispatchEvent(keyEvent);
		}
	}

	private void dispatchEvent(Component component, AWTEvent event) {
		if (component != null && event != null) {
			if (event instanceof MouseEvent) {
			} else if (event instanceof KeyEvent) {
			}
		}
	}
}
