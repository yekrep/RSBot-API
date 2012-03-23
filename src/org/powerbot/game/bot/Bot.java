package org.powerbot.game.bot;

import java.awt.Canvas;
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
import org.powerbot.concurrent.Task;
import org.powerbot.event.EventDispatcher;
import org.powerbot.game.GameDefinition;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Constants;
import org.powerbot.game.api.Multipliers;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.input.Keyboard;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.bot.event.PaintEvent;
import org.powerbot.game.bot.event.TextPaintEvent;
import org.powerbot.game.bot.random.RandomHandler;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.Render;
import org.powerbot.game.client.RenderAbsoluteX;
import org.powerbot.game.client.RenderAbsoluteY;
import org.powerbot.game.client.RenderData;
import org.powerbot.game.client.RenderFloats;
import org.powerbot.game.client.RenderRenderData;
import org.powerbot.game.client.RenderXMultiplier;
import org.powerbot.game.client.RenderYMultiplier;
import org.powerbot.game.loader.Loader;
import org.powerbot.game.loader.script.ModScript;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.component.BotPanel;
import org.powerbot.lang.AdaptException;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.Resources;

/**
 * An environment of the game that is automated.
 *
 * @author Timer
 */
public class Bot extends GameDefinition implements Runnable {
	private static Logger log = Logger.getLogger(Bot.class.getName());
	public static final LinkedList<Bot> bots = new LinkedList<Bot>();
	private static final Map<ThreadGroup, Bot> context = new HashMap<ThreadGroup, Bot>();

	private ModScript modScript;
	private BotPanel panel;
	private Client client;
	public Constants constants;
	public Multipliers multipliers;
	public final Calculations.Toolkit toolkit;
	public final Calculations.Viewport viewport;

	private EventDispatcher eventDispatcher;
	private ActiveScript activeScript;
	private RandomHandler randomHandler;

	public BufferedImage image;
	private BufferedImage backBuffer;
	private final PaintEvent paintEvent;
	private final TextPaintEvent textPaintEvent;

	public Bot() {
		final Dimension d = new Dimension(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);
		image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		backBuffer = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_RGB);
		client = null;
		panel = null;
		paintEvent = new PaintEvent();
		textPaintEvent = new TextPaintEvent();
		eventDispatcher = new EventDispatcher();
		container.submit(eventDispatcher);
		toolkit = new Calculations.Toolkit();
		viewport = new Calculations.Viewport();
		activeScript = null;
		randomHandler = new RandomHandler(this);
	}

	/**
	 * Initializes this bot and adds it to reference.
	 */
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
		container.submit(new Loader(this));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeProcessor getProcessor() throws AdaptException {
		final String id = "(" + packHash.substring(0, 6) + ")";
		log.info("Loading client patch " + id);
		try {
			modScript = new ModScript(IOHelper.read(HttpClient.openStream(new URL(String.format(Resources.getServerLinks().get("clientpatch"), packHash)))));
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
		if (eventDispatcher != null) {
			eventDispatcher.setActive(false);
		}
		if (stub != null) {
			log.fine("Terminating stub activities");
			stub.setActive(false);
		}
		Task task = null;
		if (appletContainer != null) {
			log.fine("Shutting down applet");
			task = new Task() {
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
		if (task != null) {
			container.submit(task);
		}
		container.stop();
	}

	/**
	 * Sets the panel currently associated with this bot to relay events to.
	 *
	 * @param panel The <code>BotPanel</code> responsible for displaying this bot.
	 */
	public void setPanel(final BotPanel panel) {
		this.panel = panel;
	}

	/**
	 * Resizes this bot's back buffer and container.
	 *
	 * @param width  Width of the component.
	 * @param height Height of the component.
	 */
	public void resize(final int width, final int height) {
		backBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		appletContainer.setSize(width, height);
		appletContainer.update(backBuffer.getGraphics());
		appletContainer.paint(backBuffer.getGraphics());
	}

	/**
	 * Returns the buffered graphics associated with this bot's client after painting information provided by events.
	 *
	 * @return The <code>Graphics</code> to be displayed in the <code>Canvas</code>.
	 */
	public Graphics getBufferGraphics() {
		final Graphics back = backBuffer.getGraphics();
		if (client != null) {
			paintEvent.graphics = back;
			textPaintEvent.graphics = back;
			textPaintEvent.id = 0;
			eventDispatcher.fire(paintEvent);
			eventDispatcher.fire(textPaintEvent);
		}
		back.dispose();
		image.getGraphics().drawImage(backBuffer, 0, 0, null);
		if (panel != null) {
			panel.repaint();
		}
		return backBuffer.getGraphics();
	}

	/**
	 * Sets the client of this bot to the provided <code>Client</code>, while initializing it to be associated with this bot via callback.
	 *
	 * @param client The <code>Client</code> to associate with this bot.
	 */
	private void setClient(final Client client) {
		this.client = client;
		client.setCallback(new CallbackImpl(this));
		constants = new Constants(modScript.constants);
		multipliers = new Multipliers(modScript.multipliers);
		container.submit(new SafeMode(this));
	}

	public Client getClient() {
		return client;
	}

	/**
	 * @return The <code>Canvas</code> of this bot's client.
	 */
	public Canvas getCanvas() {
		return client != null ? client.getCanvas() : null;
	}

	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	public ActiveScript getActiveScript() {
		return activeScript;
	}

	public void updateToolkit(final Render render) {
		final Object renderData = render.getData();
		final Object toolkit = ((RenderFloats) renderData).getRenderFloats();
		this.toolkit.absoluteX = ((RenderAbsoluteX) toolkit).getRenderAbsoluteX();
		this.toolkit.absoluteY = ((RenderAbsoluteY) toolkit).getRenderAbsoluteY();
		this.toolkit.xMultiplier = ((RenderXMultiplier) toolkit).getRenderXMultiplier();
		this.toolkit.yMultiplier = ((RenderYMultiplier) toolkit).getRenderYMultiplier();
		final float[] viewport = ((RenderData) ((RenderRenderData) renderData).getRenderRenderData()).getFloats();
		this.viewport.xOff = viewport[constants.VIEWPORT_XOFF];
		this.viewport.xX = viewport[constants.VIEWPORT_XX];
		this.viewport.xY = viewport[constants.VIEWPORT_XY];
		this.viewport.xZ = viewport[constants.VIEWPORT_XZ];
		this.viewport.yOff = viewport[constants.VIEWPORT_YOFF];
		this.viewport.yX = viewport[constants.VIEWPORT_YX];
		this.viewport.yY = viewport[constants.VIEWPORT_YY];
		this.viewport.yZ = viewport[constants.VIEWPORT_YZ];
		this.viewport.zOff = viewport[constants.VIEWPORT_ZOFF];
		this.viewport.zX = viewport[constants.VIEWPORT_ZX];
		this.viewport.zY = viewport[constants.VIEWPORT_ZY];
		this.viewport.zZ = viewport[constants.VIEWPORT_ZZ];
	}

	/**
	 * @return The bot belonging to the invoking thread-group.
	 */
	public static Bot resolve() {
		final Bot bot = Bot.context.get(Thread.currentThread().getThreadGroup());
		if (bot == null) {
			final RuntimeException exception = new RuntimeException(Thread.currentThread() + "@" + Thread.currentThread().getThreadGroup());
			log.log(Level.SEVERE, "Client does not exist: ", exception);
			throw exception;
		}
		return bot;
	}

	/**
	 * @author Timer
	 */
	private static final class SafeMode extends Task {
		private final Bot bot;

		public SafeMode(final Bot bot) {
			this.bot = bot;
		}

		/**
		 * Enters the game into SafeMode by pressing 's'.
		 */
		public void run() {
			if (bot != null && !bot.killed && bot.getClient() != null && !Keyboard.isReady()) {
				while (!bot.killed && !Keyboard.isReady() && !Mouse.isReady()) {
					Time.sleep(150);
				}
				Keyboard.sendKey('s');
			}
		}
	}
}
