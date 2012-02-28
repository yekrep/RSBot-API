package org.powerbot.gui.component;

import org.powerbot.game.bot.Bot;
import org.powerbot.gui.Chrome;
import org.powerbot.util.io.Resources;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * A panel that re-dispatches human events to the game's applet.
 * Contains an image buffered from the applet.
 * <p/>
 * Missing vital event dispatching of certain events, not safe for prolonged use.
 *
 * @author Timer
 */
public class BotPanel extends JPanel {
	private Bot bot;
	private int xOff;

	public BotPanel() {
		setSize(new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT));
		setMinimumSize(new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT));
		setPreferredSize(new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT));
		setBackground(Color.black);
		xOff = 0;

		setLayout(new GridBagLayout());
		add(new JLabel(new ImageIcon(Resources.getImage(Resources.Paths.ARROWS))));
		final JLabel info = new JLabel("  Loading...");
		info.setFont(info.getFont().deriveFont(Font.BOLD, 24));
		info.setForeground(Color.WHITE);
		add(info);

		addComponentListener(new ComponentAdapter() {
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
			g.drawImage(bot.image, xOff, 0, null);
		}
	}

	public void setBot(Bot game) {
		if (this.bot != null) {
			this.bot.setPanel(null);
		}
		if (game != null) {
			this.bot = game;
			this.bot.setPanel(this);
			if (bot.getCanvas() != null) {
				offset();
			}
		}
	}

	public void offset() {
		Canvas c = bot.getCanvas();
		if (c != null) {
			xOff = (getWidth() - c.getWidth()) / 2;
		}
	}

	private void redispatch(AWTEvent e) {
		if (e instanceof MouseEvent) {
			((MouseEvent) e).translatePoint(-xOff, 0);
		}
		if (bot != null && bot.appletContainer != null && bot.appletContainer.getComponentCount() > 0) {
			bot.appletContainer.getComponent(0).dispatchEvent(e);
		}
	}
}
