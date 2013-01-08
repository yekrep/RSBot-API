package org.powerbot.core.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.powerbot.core.bot.handlers.ScriptHandler;
import org.powerbot.core.event.EventManager;
import org.powerbot.core.event.EventMulticaster;
import org.powerbot.core.loader.ClientLoader;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.util.internal.Constants;
import org.powerbot.game.bot.Context;
import org.powerbot.game.bot.handler.input.MouseExecutor;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.Render;
import org.powerbot.game.client.RenderData;
import org.powerbot.gui.BotChrome;

public class BotComposite {//TODO remove the use of a composite ... export data elsewhere
	private final Bot bot;

	MouseExecutor executor;
	EventManager eventManager;
	ScriptHandler scriptHandler;
	Context context;

	Client client;
	public final Calculations.Toolkit toolkit;
	public final Calculations.Viewport viewport;

	public Constants constants;

	public BotComposite(final Bot bot) {
		this.bot = bot;

		executor = null;
		eventManager = new EventMulticaster();
		scriptHandler = new ScriptHandler(eventManager);

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
		final RenderData toolkit = render.getRenderData();
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

			final Job[] jobs = scriptHandler.getScriptContainer().enumerate();
			final List<LoopTask> loopTasks = new ArrayList<>();
			for (final Job job : jobs) {
				if (job instanceof LoopTask) loopTasks.add((LoopTask) job);
			}
			final long mark = System.currentTimeMillis();
			for (final LoopTask task : loopTasks) {
				while (!task.isPaused() && task.isAlive() && System.currentTimeMillis() - mark < 120000) {
					Task.sleep(150);
				}
			}
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
