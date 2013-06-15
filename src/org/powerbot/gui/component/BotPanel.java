package org.powerbot.gui.component;

import org.powerbot.bot.Bot;
import org.powerbot.client.input.Mouse;
import org.powerbot.gui.BotChrome;
import org.powerbot.util.io.Resources;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;
import java.util.EventObject;
import java.util.logging.Logger;

/**
 * A panel that re-dispatches human events to the game's applet.
 * Contains an image buffered from the applet.
 *
 * @author Timer
 */
public class BotPanel extends JPanel {
	public static final int INPUT_MOUSE = 1, INPUT_KEYBOARD = 2;
	private int inputMask;
	private Bot bot;
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
		final JLabel status;
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
	public void paintComponent(final Graphics g) {
		if (BotChrome.getInstance().isMinimised()) {
			return;
		}

		super.paintComponent(g);

		if (bot != null) {
			g.drawImage(bot.getImage(), xOff, yOff, null);
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

	public void setInputMask(final int inputMask) {
		this.inputMask = inputMask;
	}

	public int getInputMask() {
		return inputMask;
	}

	public void resize() {
		if (bot != null && bot.getAppletContainer() != null) {
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

	private void redispatch(MouseEvent event) {
		if (event == null) return;
		if (bot == null || bot.getMethodContext().getClient() == null) return;
		if (!bot.getMethodContext().mouse.isReady()) return;
		Mouse mouse = bot.getMouseHandler().getMouse();
		if (mouse == null) return;

		event.translatePoint(-xOff, -yOff);
		bot.getEventMulticaster().dispatch(event);
		if ((inputMask & INPUT_MOUSE) == 0) {
			return;
		}

		mouse.sendEvent(generate(event, bot.getMouseHandler().getSource(), mouse.isPresent()));
	}

	private void redispatch(KeyEvent event) {
		if (event == null) return;
		if (bot == null || bot.getMethodContext().getClient() == null) return;
		if (!bot.getMethodContext().keyboard.isReady()) return;
		Component c = bot.getInputHandler().getSource();
		try {
			final Field f = EventObject.class.getDeclaredField("source");
			final boolean a = f.isAccessible();
			f.setAccessible(true);
			f.set(event, c);
			f.setAccessible(a);
		} catch (final Exception ignored) {
		}
		bot.getEventMulticaster().dispatch(event);
		if ((inputMask & INPUT_KEYBOARD) == 0) {
			return;
		}
		bot.getInputHandler().send(event);
	}

	private MouseEvent generate(MouseEvent event, Component component, boolean present) {
		int mouseX = event.getX(), mouseY = event.getY();
		int modifiers = event.getModifiers(), clickCount = event.getClickCount();
		if (event.getID() != MouseEvent.MOUSE_EXITED &&
				mouseX > 0 && mouseX < component.getWidth() && mouseY > 0 && mouseY < component.getHeight()) {
			if (present) {
				if (event instanceof MouseWheelEvent) {
					final MouseWheelEvent mouseWheelEvent = (MouseWheelEvent) event;
					return new MouseWheelEvent(
							component, event.getID(),
							System.currentTimeMillis(), modifiers,
							mouseX, mouseY, clickCount, event.isPopupTrigger(),
							mouseWheelEvent.getScrollType(), mouseWheelEvent.getScrollAmount(), mouseWheelEvent.getWheelRotation()
					);
				} else {
					return new MouseEvent(
							component, event.getID(),
							System.currentTimeMillis(), modifiers,
							mouseX, mouseY, clickCount, event.isPopupTrigger(),
							event.getButton()
					);
				}
			} else {
				return new MouseEvent(
						component, MouseEvent.MOUSE_ENTERED,
						System.currentTimeMillis(), modifiers,
						mouseX, mouseY, clickCount, false
				);
			}
		} else if (present) {
			return new MouseEvent(
					component, MouseEvent.MOUSE_EXITED,
					System.currentTimeMillis(), modifiers,
					mouseX, mouseY, clickCount, false
			);
		}
		return null;
	}
}
