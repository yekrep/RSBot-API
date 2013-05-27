package org.powerbot.gui.component;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.MenuComponent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.powerbot.bot.Bot;
import org.powerbot.client.input.Mouse;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.Resources;

/**
 * A panel that re-dispatches human events to the game's applet.
 * Contains an image buffered from the applet.
 *
 * @author Timer
 */
public class BotPanel extends JPanel {
	public static final int INPUT_MOUSE = 1, INPUT_KEYBOARD = 2;
	private static final long serialVersionUID = 1L;
	private int inputMask;
	private Bot bot;
	private final JLabel status;
	private int xOff, yOff;

	public BotPanel(final Component parent) {
		final Dimension d = new Dimension(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
		setSize(d);
		setPreferredSize(d);
		setMinimumSize(d);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		setBackground(Color.black);
		bot = null;
		xOff = yOff = 0;
		inputMask = INPUT_MOUSE | INPUT_KEYBOARD;

		setLayout(new GridBagLayout());
		final JPanel panel = new JPanel();
		panel.setLayout(getLayout());
		panel.setBackground(getBackground());
		panel.add(new JLabel(new ImageIcon(Resources.getImage(Resources.Paths.ARROWS))), new GridBagConstraints());
		final GridBagConstraints c = new GridBagConstraints();
		c.gridy = 1;
		panel.add(status = new JLabel(), c);
		status.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		final Font f = status.getFont();
		status.setFont(new Font(f.getFamily(), f.getStyle(), f.getSize() + 1));
		add(panel);
		Logger.getLogger("").addHandler(new BotPanelLogHandler(status));

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent evt) {
				resize();
				requestFocus();
			}
		});
		addMouseListener(new MouseListener() {
			public void mouseClicked(final MouseEvent e) {
				redispatch(e);
				if (!hasFocus()) {
					requestFocus();
				}
			}

			public void mouseEntered(final MouseEvent e) {
				redispatch(e);
			}

			public void mouseExited(final MouseEvent e) {
				redispatch(e);
			}

			public void mousePressed(final MouseEvent e) {
				redispatch(e);
			}

			public void mouseReleased(final MouseEvent e) {
				redispatch(e);
			}
		});
		addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(final MouseEvent e) {
				redispatch(e);
			}

			public void mouseMoved(final MouseEvent e) {
				redispatch(e);
			}
		});
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(final MouseWheelEvent e) {
				redispatch(e);
			}
		});
		addKeyListener(new KeyListener() {
			public void keyPressed(final KeyEvent e) {
				redispatch(e);
			}

			public void keyReleased(final KeyEvent e) {
				redispatch(e);
			}

			public void keyTyped(final KeyEvent e) {
				redispatch(e);
			}
		});
	}

	@Override
	public void removeAll() {
		track();
	}

	@Override
	public void remove(final int index) {
		track();
	}

	@Override
	public void remove(final Component component) {
		track();
	}

	@Override
	public void remove(final MenuComponent component) {
		track();
	}

	private void track() {
		final StackTraceElement e = Thread.currentThread().getStackTrace()[3];
		Tracker.getInstance().trackEvent("BotPanel", "remove", e.getClassName() + ":" + e.getMethodName());
	}

	@Override
	public void paintComponent(final Graphics g) {
		if (BotChrome.minimised) {
			return;
		}

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
		getComponent(0).setVisible(bot == null);
		if (bot != null) {
			bot.setPanel(this);
			if (bot.getCanvas() != null) {
				offset();
			}
		}
	}

	public int getInputMask() {
		return inputMask;
	}

	public void setInputMask(final int inputMask) {
		this.inputMask = inputMask;
	}

	public void resize() {
		if (bot != null && bot.appletContainer != null) {
			bot.resize(getWidth(), getHeight());
			offset();
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
				Bot.client() == null) {
			return;
		}
		mouseEvent.translatePoint(-xOff, -yOff);
		final Mouse mouse = Bot.client().getMouse();
		if (mouse == null) {
			return;
		}
		final boolean present = mouse.isPresent();
		final Component component = bot.appletContainer.getComponent(0);
		notifyListeners(component, mouseEvent);
		if ((inputMask & INPUT_MOUSE) == 0) {
			return;
		}
		int mouseX = mouseEvent.getX(), mouseY = mouseEvent.getY();
		final int modifiers = mouseEvent.getModifiers(), clickCount = mouseEvent.getClickCount();
		if (mouseEvent.getID() != MouseEvent.MOUSE_EXITED &&
				mouseX > 0 && mouseX < component.getWidth() && mouseY > 0 && mouseY < component.getHeight()) {
			if (present) {
				if (mouseEvent instanceof MouseWheelEvent) {
					final MouseWheelEvent mouseWheelEvent = (MouseWheelEvent) mouseEvent;
					component.dispatchEvent(new MouseWheelEvent(
							component, mouseEvent.getID(),
							System.currentTimeMillis(), modifiers,
							mouseX, mouseY, clickCount, mouseEvent.isPopupTrigger(),
							mouseWheelEvent.getScrollType(), mouseWheelEvent.getScrollAmount(), mouseWheelEvent.getWheelRotation()
					));
				} else {
					component.dispatchEvent(new MouseEvent(
							component, mouseEvent.getID(),
							System.currentTimeMillis(), modifiers,
							mouseX, mouseY, clickCount, mouseEvent.isPopupTrigger(),
							mouseEvent.getButton()
					));
				}
			} else {
				component.dispatchEvent(new MouseEvent(
						component, MouseEvent.MOUSE_ENTERED,
						System.currentTimeMillis(), modifiers,
						mouseX, mouseY, clickCount, false
				));
			}
		} else if (present) {
			component.dispatchEvent(new MouseEvent(
					component, MouseEvent.MOUSE_EXITED,
					System.currentTimeMillis(), modifiers,
					mouseX, mouseY, clickCount, false
			));
		}
	}

	private void redispatch(final KeyEvent keyEvent) {
		if (keyEvent == null || bot == null || bot.appletContainer == null || bot.appletContainer.getComponentCount() == 0 ||
				Bot.client() == null) {
			return;
		}
		bot.getEventMulticaster().dispatch(keyEvent);
		if ((inputMask & INPUT_KEYBOARD) == 0) {
			return;
		}
		final Component component = bot.appletContainer.getComponent(0);
		if (component != null) {
			component.dispatchEvent(keyEvent);
		}
	}

	private void notifyListeners(final Component component, final MouseEvent mouseEvent) {
		if (component != null && mouseEvent != null) {
			bot.getEventMulticaster().dispatch(mouseEvent);
		}
	}
}
