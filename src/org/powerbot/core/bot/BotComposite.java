package org.powerbot.core.bot;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.powerbot.core.bot.handler.ScriptHandler;
import org.powerbot.core.event.EventDispatcher;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.util.internal.Constants;
import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.bot.Context;
import org.powerbot.game.bot.handler.input.MouseExecutor;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.Render;
import org.powerbot.game.client.RenderData;
import org.powerbot.game.loader.ClientLoader;
import org.powerbot.gui.BotChrome;

public class BotComposite {
	private final Bot bot;

	MouseExecutor executor;
	EventDispatcher eventDispatcher;
	ScriptHandler scriptHandler;
	Context context;

	Client client;
	public final Calculations.Toolkit toolkit;
	public final Calculations.Viewport viewport;

	public Constants constants;
	public Multipliers multipliers;

	public BotComposite(final Bot bot) {
		this.bot = bot;

		executor = null;
		eventDispatcher = new EventDispatcher();
		scriptHandler = new ScriptHandler(bot);

		client = null;
		toolkit = new Calculations.Toolkit();
		viewport = new Calculations.Viewport();
	}

	public void setup(final Constants constants) {
		this.constants = constants;
	}

	public void updateToolkit(final Render render) {
		if (constants == null) {
			return;
		}

		this.toolkit.absoluteX = render.getAbsoluteX();
		this.toolkit.absoluteY = render.getAbsoluteY();
		this.toolkit.xMultiplier = render.getXMultiplier();
		this.toolkit.yMultiplier = render.getYMultiplier();
		final RenderData toolkit = render.getViewport();
		final float[] viewport = toolkit.getFloats();
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

	public void reload() {
		Bot.log.info("Refreshing environment");
		if (scriptHandler != null && scriptHandler.isActive()) {
			scriptHandler.pause();
		}

		bot.terminateApplet();
		bot.resize(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);

		BotChrome.getInstance().panel.setBot(bot);

		bot.clientLoader = new ClientLoader();
		if (bot.clientLoader.call()) {
			final Future<?> future = bot.start();
			if (future == null) {
				return;
			}
			try {
				future.get();
			} catch (final InterruptedException | ExecutionException ignored) {
			}

			if (scriptHandler != null && scriptHandler.isActive()) {
				scriptHandler.resume();
			}
		}

		bot.refreshing = false;
	}
}
