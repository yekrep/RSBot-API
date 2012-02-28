package org.powerbot.game.bot;

import org.powerbot.asm.NodeProcessor;
import org.powerbot.game.GameDefinition;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.input.Mouse;
import org.powerbot.game.loader.Loader;
import org.powerbot.game.loader.script.ModScript;
import org.powerbot.gui.Chrome;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.util.Configuration;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Bot extends GameDefinition {
	private Logger log = Logger.getLogger(Bot.class.getName());
	public BufferedImage image;
	private BufferedImage backBuffer;
	private BotPanel panel;

	private Client client;

	public static final LinkedList<Bot> bots = new LinkedList<Bot>();

	public Bot() {
		Dimension d = new Dimension(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT);
		this.image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		this.backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		this.client = null;
		this.panel = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void startEnvironment() {
		bots.add(this);
		this.callback = new Runnable() {
			public void run() {
				setClient((Client) appletContainer.clientInstance);
				appletContainer.paint(image.getGraphics());
				resize(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT);
			}
		};
		processor.submit(new Loader(this));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeProcessor getProcessor() {
		log.info("Client ID: " + packHash);
		try {
			return new ModScript(IOHelper.read(HttpClient.openStream(new URL(Configuration.URLs.CLIENT_PATCH + packHash))));
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void killEnvironment() {
		if (stub != null) {
			stub.setActive(false);
		}
		if (appletContainer != null) {
			appletContainer.stop();
			appletContainer.destroy();
			appletContainer = null;
			stub = null;
		}
		bots.remove(this);
	}

	private void setClient(Client client) {
		this.client = client;
		client.setCallback(new CallbackImpl(this));
	}

	public void setPanel(BotPanel panel) {
		this.panel = panel;
	}

	public void resize(int width, int height) {
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		appletContainer.setSize(width, height);
		appletContainer.update(backBuffer.getGraphics());
		appletContainer.paint(backBuffer.getGraphics());
	}

	public Canvas getCanvas() {
		return client != null ? client.getCanvas() : null;
	}

	public Graphics getBufferGraphics() {
		Graphics back = backBuffer.getGraphics();
		back.setColor(Color.white);
		back.drawString(" -- RSBot v4 | Canvas [hacked]", 10, 50);
		Mouse mouse = client.getMouse();
		if (mouse != null) {
			int mouse_x = mouse.getX(), mouse_y = mouse.getY();
			int mouse_press_x = mouse.getPressX(), mouse_press_y = mouse.getPressY();
			long mouse_press_time = mouse.getPressTime();
			back.setColor(Color.YELLOW.darker());
			back.drawLine(mouse_x - 5, mouse_y - 5, mouse_x + 5, mouse_y + 5);
			back.drawLine(mouse_x + 5, mouse_y - 5, mouse_x - 5, mouse_y + 5);
			if (System.currentTimeMillis() - mouse_press_time < 1000) {
				back.setColor(Color.RED);
				back.drawLine(mouse_press_x - 5, mouse_press_y - 5, mouse_press_x + 5, mouse_press_y + 5);
				back.drawLine(mouse_press_x + 5, mouse_press_y - 5, mouse_press_x - 5, mouse_press_y + 5);
			}
		}
		back.dispose();
		image.getGraphics().drawImage(backBuffer, 0, 0, null);
		if (panel != null) {
			panel.repaint();
		}
		return backBuffer.getGraphics();
	}

	public static Bot getBot(Object o) {
		ClassLoader cl = o.getClass().getClassLoader();
		for (Bot bot : Bot.bots) {
			Component c = bot.appletContainer.getComponent(0);
			ClassLoader componentParent = c.getClass().getClassLoader();
			if (cl == componentParent) {
				return bot;
			}
		}
		return null;
	}
}
