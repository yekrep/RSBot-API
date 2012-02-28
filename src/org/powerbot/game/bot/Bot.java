package org.powerbot.game.bot;

import org.powerbot.asm.NodeProcessor;
import org.powerbot.game.GameDefinition;
import org.powerbot.game.api.Constants;
import org.powerbot.game.api.Multipliers;
import org.powerbot.game.client.Client;
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
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot extends GameDefinition {
	private static Logger log = Logger.getLogger(Bot.class.getName());
	public static final LinkedList<Bot> bots = new LinkedList<Bot>();
	private static final Map<ThreadGroup, Bot> context = new HashMap<ThreadGroup, Bot>();
	private ModScript modScript;

	private BotPanel panel;
	public Client client;
	public Constants constants;
	public Multipliers multipliers;
	public BufferedImage image;
	private BufferedImage backBuffer;

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
		log.info("Starting bot");
		bots.add(this);
		context.put(threadGroup, this);
		this.callback = new Runnable() {
			public void run() {
				setClient((Client) appletContainer.clientInstance);
				appletContainer.paint(image.getGraphics());
				resize(Chrome.PANEL_WIDTH, Chrome.PANEL_HEIGHT);
			}
		};
		log.fine("Submitting loader");
		processor.submit(new Loader(this));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeProcessor getProcessor() {
		final String id = "(" + packHash.substring(0, 6) + ")";
		log.info("Loading client patch " + id);
		try {
			modScript = new ModScript(IOHelper.read(HttpClient.openStream(new URL(Configuration.URLs.CLIENT_PATCH + packHash))));
			return modScript;
		} catch (SocketTimeoutException ignored) {
			log.severe("Please try again later " + id);
		} catch (NullPointerException ignored) {
			log.severe("Please try again later " + id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void killEnvironment() {
		log.info("Unloading environment");
		if (stub != null) {
			log.fine("Terminating stub activities");
			stub.setActive(false);
		}
		if (appletContainer != null) {
			log.fine("Shutting down applet");
			appletContainer.stop();
			appletContainer.destroy();
			appletContainer = null;
			stub = null;
		}
		bots.remove(this);
		context.remove(threadGroup);
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

	public Graphics getBufferGraphics() {
		Graphics back = backBuffer.getGraphics();
		back.dispose();
		image.getGraphics().drawImage(backBuffer, 0, 0, null);
		if (panel != null) {
			panel.repaint();
		}
		return backBuffer.getGraphics();
	}

	private void setClient(Client client) {
		this.client = client;
		client.setCallback(new CallbackImpl(this));
		this.constants = new Constants(modScript.constants);
		this.multipliers = new Multipliers(modScript.multipliers);
	}

	public Canvas getCanvas() {
		return client != null ? client.getCanvas() : null;
	}

	public static Bot resolve() {
		Bot bot = Bot.context.get(Thread.currentThread().getThreadGroup());
		if (bot == null) {
			RuntimeException exception = new RuntimeException(Thread.currentThread() + "@" + Thread.currentThread().getThreadGroup());
			log.log(Level.SEVERE, "Client does not exist: ", exception);
			throw exception;
		}
		return bot;
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
