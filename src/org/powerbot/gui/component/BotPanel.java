package org.powerbot.gui.component;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
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

import org.powerbot.concurrent.Task;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.ViewportEntity;
import org.powerbot.game.bot.Bot;
import org.powerbot.game.bot.event.HumanInputEvent;
import org.powerbot.game.bot.handler.input.MouseReactor;
import org.powerbot.game.bot.handler.input.util.MouseNode;
import org.powerbot.game.client.input.Mouse;
import org.powerbot.gui.BotChrome;

/**
 * A panel that re-dispatches human events to the game's applet.
 * Contains an image buffered from the applet.
 *
 * @author Timer
 */
public class BotPanel extends JPanel {
	public static final int INPUT_MOUSE = 1, INPUT_KEYBOARD = 2;
	private int inputMask;
	private MouseNode mouseNode;
	private final HumanInputEvent mouseRequest;
	private final Point pressLocation, releaseLocation;

	private static final long serialVersionUID = 1L;
	private Bot bot;
	private int xOff, yOff;
	public final BotLoadingPanel loadingPanel;

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
		mouseNode = null;
		mouseRequest = new HumanInputEvent();
		pressLocation = new Point(-1, -1);
		releaseLocation = new Point(-1, -1);

		setLayout(new GridBagLayout());
		add(loadingPanel = new BotLoadingPanel(parent));

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
		super.paintComponent(g);

		if (bot != null) {
			loadingPanel.setVisible(false);
			g.drawImage(bot.image, xOff, yOff, null);
		}
	}

	public void setBot(final Bot bot) {
		if (this.bot != null) {
			this.bot.setPanel(null);
		}
		this.bot = bot;
		loadingPanel.setVisible(true);
		if (bot != null) {
			getGraphics().setColor(Color.BLACK);
			getGraphics().fillRect(0, 0, getWidth(), getHeight());
			loadingPanel.validate();
			loadingPanel.repaint();
			bot.getContainer().submit(new BotSet(bot.threadGroup));
			bot.setPanel(this);
			if (bot.getCanvas() != null) {
				offset();
			}
		} else {
			loadingPanel.set(null);
		}
	}

	public void setInputMask(final int inputMask) {
		this.inputMask = inputMask;
	}

	public int getInputMask() {
		return inputMask;
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
				bot.getClient() == null) {
			return;
		}
		mouseEvent.translatePoint(-xOff, -yOff);
		final Mouse mouse = bot.getClient().getMouse();
		if (mouse == null) {
			return;
		}
		final boolean present = mouse.isPresent();
		final Component component = bot.appletContainer.getComponent(0);
		notifyListeners(component, mouseEvent, present);
		final MouseReactor reactor = bot.getReactor();
		if (reactor != null) {
			int mouseX = mouseEvent.getX(), mouseY = mouseEvent.getY();
			final int modifiers = mouseEvent.getModifiers(), clickCount = mouseEvent.getClickCount();
			if (mouseX <= 2 || mouseX >= component.getWidth() - 2 || mouseY <= 2 || mouseY >= component.getHeight() - 2) {
				return;
			}
			if (mouseEvent instanceof MouseWheelEvent && present) {
				final MouseWheelEvent mouseWheelEvent = (MouseWheelEvent) mouseEvent;
				component.dispatchEvent(new MouseWheelEvent(
						component, mouseEvent.getID(),
						System.currentTimeMillis(), modifiers,
						mouseX, mouseY, clickCount, mouseEvent.isPopupTrigger(),
						mouseWheelEvent.getScrollType(), mouseWheelEvent.getScrollAmount(), mouseWheelEvent.getWheelRotation()
				));
				return;
			}
			if (mouseEvent.getID() != MouseEvent.MOUSE_MOVED && mouseEvent.getID() != MouseEvent.MOUSE_CLICKED) {
				if (mouseEvent.getID() == MouseEvent.MOUSE_PRESSED) {
					pressLocation.setLocation(mouseEvent.getPoint());
					releaseLocation.setLocation(-1, -1);
				} else if (mouseEvent.getID() == MouseEvent.MOUSE_RELEASED) {
					releaseLocation.setLocation(mouseEvent.getPoint());
				}
				if (pressLocation.getX() == -1 || releaseLocation.getX() == -1 || pressLocation.distance(releaseLocation) <= Math.sqrt(2)) {
					return;
				}
			}
			final MouseNode prevNode = mouseNode;
			final int button = mouseEvent.getButton();
			if (mouseEvent.getID() == MouseEvent.MOUSE_RELEASED) {
				mouseX = (int) pressLocation.getX();
				mouseY = (int) pressLocation.getY();
			}
			final int f_mouseX = mouseX, f_mouseY = mouseY;
			mouseNode = new MouseNode(
					org.powerbot.game.api.methods.input.Mouse.PRIORITY_REAL_TIME,
					new ViewportEntity() {
						private final Rectangle area = new Rectangle(f_mouseX - 1, f_mouseY - 1, 3, 3);

						public Point getCentralPoint() {
							return new Point(f_mouseX, f_mouseY);
						}

						public Point getNextViewportPoint() {
							return new Point(f_mouseX + Random.nextInt(-1, 2), f_mouseY + Random.nextInt(-1, 2));
						}

						public boolean contains(final Point point) {
							return area.contains(point);
						}

						public boolean validate() {
							return true;
						}
					},
					mouseEvent.getID() == MouseEvent.MOUSE_CLICKED || mouseEvent.getID() == MouseEvent.MOUSE_RELEASED ? new FilterClick(mouseEvent.getButton(), pressLocation, releaseLocation) : FILTER_MOVE
			);
			final boolean input_enabled = (inputMask & INPUT_MOUSE) != 0;
			if (input_enabled || button == MouseEvent.BUTTON1 || button == MouseEvent.BUTTON3) {
				if (prevNode != null && !(prevNode.getFilter() instanceof FilterClick) && !(mouseNode.getFilter() instanceof FilterClick)) {
					prevNode.cancel();
				}
				if (!input_enabled) {
					mouseRequest.init(new Point(f_mouseX, f_mouseY));
					bot.getEventDispatcher().fire(mouseRequest);
				}
				if (input_enabled || mouseRequest.accepted()) {
					reactor.process(mouseNode);
				}
			}
		}
	}

	private void redispatch(final KeyEvent keyEvent) {
		if (keyEvent == null || bot == null || bot.appletContainer == null || bot.appletContainer.getComponentCount() == 0 ||
				bot.getClient() == null) {
			return;
		}
		bot.getEventDispatcher().dispatch(keyEvent);
		if ((inputMask & INPUT_KEYBOARD) == 0) {
			return;
		}
		final Component component = bot.appletContainer.getComponent(0);
		if (component != null) {
			component.dispatchEvent(keyEvent);
		}
	}

	private void notifyListeners(final Component component, final MouseEvent mouseEvent, final boolean present) {
		if (component != null && mouseEvent != null) {
			bot.getEventDispatcher().dispatch(mouseEvent);
		}
	}

	private final class BotSet implements Task {
		private final ThreadGroup threadGroup;

		private BotSet(final ThreadGroup threadGroup) {
			this.threadGroup = threadGroup;
		}

		public void run() {
			loadingPanel.set(threadGroup);
		}
	}

	private static final Filter<Point> FILTER_MOVE = new Filter<Point>() {
		@Override
		public boolean accept(final Point point) {
			return true;
		}
	};

	private final class FilterClick implements Filter<Point> {
		private final int button;
		private final Point start, end;

		public FilterClick(final int button, final Point start, final Point end) {
			this.button = button;
			this.start = start;
			this.end = end;
		}

		@Override
		public boolean accept(final Point point) {
			if (start.distance(end) >= 2d) {
				org.powerbot.game.api.methods.input.Mouse.move(start);
				org.powerbot.game.api.methods.input.Mouse.drag(end);
				return true;
			}

			if (button == MouseEvent.BUTTON1) {
				org.powerbot.game.api.methods.input.Mouse.click(true);
			} else if (button == MouseEvent.BUTTON3) {
				org.powerbot.game.api.methods.input.Mouse.click(false);
			}
			return true;
		}
	}
}
