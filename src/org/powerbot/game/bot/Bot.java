package org.powerbot.game.bot;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.powerbot.asm.NodeProcessor;
import org.powerbot.concurrent.RunnableTask;
import org.powerbot.event.EventDispatcher;
import org.powerbot.game.GameDefinition;
import org.powerbot.game.api.Constants;
import org.powerbot.game.api.Multipliers;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.client.Client;
import org.powerbot.game.event.PaintEvent;
import org.powerbot.game.event.listener.PaintListener;
import org.powerbot.game.loader.Loader;
import org.powerbot.game.loader.script.ModScript;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.lang.AdaptException;
import org.powerbot.util.Configuration;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

public class Bot extends GameDefinition implements Runnable {
	private static Logger log = Logger.getLogger(Bot.class.getName());
	public static final LinkedList<Bot> bots = new LinkedList<Bot>();
	private static final Map<ThreadGroup, Bot> context = new HashMap<ThreadGroup, Bot>();

	private ModScript modScript;
	private BotPanel panel;
	public Client client;
	public Constants constants;
	public Multipliers multipliers;

	public EventDispatcher eventDispatcher;

	public BufferedImage image;
	private BufferedImage backBuffer;
	private final PaintEvent paintEvent;

	public Bot() {
		final Dimension d = new Dimension(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		client = null;
		panel = null;
		paintEvent = new PaintEvent();
		eventDispatcher = new EventDispatcher();
		processor.submit(eventDispatcher);
		eventDispatcher.accept(new BasicDebug());
	}

	public void run() {
		Bot.bots.add(this);
		if (initializeEnvironment()) {
			startEnvironment();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void startEnvironment() {
		if (killed) {
			return;
		}
		log.info("Starting bot");
		context.put(threadGroup, this);
		callback = new Runnable() {
			public void run() {
				setClient((Client) appletContainer.clientInstance);
				appletContainer.paint(image.getGraphics());
				resize(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
			}
		};
		log.fine("Submitting loader");
		processor.submit(new Loader(this));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeProcessor getProcessor() throws AdaptException {
		final String id = "(" + packHash.substring(0, 6) + ")";
		log.info("Loading client patch " + id);
		try {
			modScript = new ModScript(IOHelper.read(HttpClient.openStream(new URL(Configuration.URLs.CLIENT_PATCH + packHash))));
			return modScript;
		} catch (final SocketTimeoutException ignored) {
			log.severe("Please try again later " + id);
		} catch (final NullPointerException ignored) {
			log.severe("Please try again later " + id);
		} catch (final IOException e) {
			log.log(Level.FINE, "Failed to get processor: ", e);
		}
		throw new AdaptException("Failed to load processor; unable to reach server or client unsupported");
	}

	/**
	 * {@inheritDoc}
	 */
	public void killEnvironment() {
		this.killed = true;
		log.info("Unloading environment");
		if (stub != null) {
			log.fine("Terminating stub activities");
			stub.setActive(false);
		}
		Runnable r = null;
		if (appletContainer != null) {
			log.fine("Shutting down applet");
			r = new Runnable() {
				public void run() {
					appletContainer.stop();
					appletContainer.destroy();
					appletContainer = null;
					stub = null;
				}
			};
		}
		bots.remove(this);
		context.remove(threadGroup);
		if (r != null) {
			processor.submit(RunnableTask.create(r));
		}
	}

	public void setPanel(final BotPanel panel) {
		this.panel = panel;
	}

	public void resize(final int width, final int height) {
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		appletContainer.setSize(width, height);
		appletContainer.update(backBuffer.getGraphics());
		appletContainer.paint(backBuffer.getGraphics());
	}

	public Graphics getBufferGraphics() {
		final Graphics back = backBuffer.getGraphics();
		paintEvent.graphics = back;
		eventDispatcher.fire(paintEvent);
		back.dispose();
		image.getGraphics().drawImage(backBuffer, 0, 0, null);
		if (panel != null) {
			panel.repaint();
		}
		return backBuffer.getGraphics();
	}

	private void setClient(final Client client) {
		this.client = client;
		client.setCallback(new CallbackImpl(this));
		constants = new Constants(modScript.constants);
		multipliers = new Multipliers(modScript.multipliers);
	}

	public Canvas getCanvas() {
		return client != null ? client.getCanvas() : null;
	}

	public static Bot resolve() {
		final Bot bot = Bot.context.get(Thread.currentThread().getThreadGroup());
		if (bot == null) {
			final RuntimeException exception = new RuntimeException(Thread.currentThread() + "@" + Thread.currentThread().getThreadGroup());
			log.log(Level.SEVERE, "Client does not exist: ", exception);
			throw exception;
		}
		return bot;
	}

	public static Bot getBot(final Object o) {
		final ClassLoader cl = o.getClass().getClassLoader();
		for (final Bot bot : Bot.bots) {
			final Component c = bot.appletContainer.getComponent(0);
			final ClassLoader componentParent = c.getClass().getClassLoader();
			if (cl == componentParent) {
				return bot;
			}
		}
		return null;
	}

	private final class BasicDebug implements PaintListener {
		public void onRepaint(final Graphics render) {
			render.setColor(Color.white);
			render.drawString("Client state: " + Game.getClientState(), 10, 20);
			render.drawString("Floor  " + Game.getPlane(), 10, 32);
		}
	}
}
