package org.powerbot.core;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.core.bot.handlers.ScriptHandler;
import org.powerbot.core.event.EventManager;
import org.powerbot.core.event.EventMulticaster;
import org.powerbot.core.script.internal.Constants;
import org.powerbot.core.script.job.Job;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.bot.Context;
import org.powerbot.game.bot.handler.input.MouseExecutor;
import org.powerbot.game.client.Render;
import org.powerbot.game.client.RenderData;
import org.powerbot.gui.BotChrome;

public class BotComposite {//TODO remove the use of a composite ... export data elsewhere
	public final Calculations.Toolkit toolkit;
	public final Calculations.Viewport viewport;
	private final Bot bot;
	MouseExecutor executor;
	EventManager eventManager;
	ScriptHandler scriptHandler;
	Context context;

	public BotComposite(final Bot bot) {
		this.bot = bot;

		executor = null;
		eventManager = new EventMulticaster();
		scriptHandler = new ScriptHandler(eventManager);

		toolkit = new Calculations.Toolkit();
		viewport = new Calculations.Viewport();
	}

	public void updateToolkit(final Render render) {
		final Constants constants = Bot.constants();
		if (constants == null) return;

		this.toolkit.absoluteX = render.getAbsoluteX();
		this.toolkit.absoluteY = render.getAbsoluteY();
		this.toolkit.xMultiplier = render.getXMultiplier();
		this.toolkit.yMultiplier = render.getYMultiplier();
		this.toolkit.graphicsIndex = render.getGraphicsIndex();
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

		org.powerbot.core.script.methods.Calculations.toolkit.absoluteX = render.getAbsoluteX();
		org.powerbot.core.script.methods.Calculations.toolkit.absoluteY = render.getAbsoluteY();
		org.powerbot.core.script.methods.Calculations.toolkit.xMultiplier = render.getXMultiplier();
		org.powerbot.core.script.methods.Calculations.toolkit.yMultiplier = render.getYMultiplier();
		org.powerbot.core.script.methods.Calculations.toolkit.graphicsIndex = render.getGraphicsIndex();
		org.powerbot.core.script.methods.Calculations.viewport.xOff = viewport[constants.VIEWPORT_XOFF];
		org.powerbot.core.script.methods.Calculations.viewport.xX = viewport[constants.VIEWPORT_XX];
		org.powerbot.core.script.methods.Calculations.viewport.xY = viewport[constants.VIEWPORT_XY];
		org.powerbot.core.script.methods.Calculations.viewport.xZ = viewport[constants.VIEWPORT_XZ];
		org.powerbot.core.script.methods.Calculations.viewport.yOff = viewport[constants.VIEWPORT_YOFF];
		org.powerbot.core.script.methods.Calculations.viewport.yX = viewport[constants.VIEWPORT_YX];
		org.powerbot.core.script.methods.Calculations.viewport.yY = viewport[constants.VIEWPORT_YY];
		org.powerbot.core.script.methods.Calculations.viewport.yZ = viewport[constants.VIEWPORT_YZ];
		org.powerbot.core.script.methods.Calculations.viewport.zOff = viewport[constants.VIEWPORT_ZOFF];
		org.powerbot.core.script.methods.Calculations.viewport.zX = viewport[constants.VIEWPORT_ZX];
		org.powerbot.core.script.methods.Calculations.viewport.zY = viewport[constants.VIEWPORT_ZY];
		org.powerbot.core.script.methods.Calculations.viewport.zZ = viewport[constants.VIEWPORT_ZZ];
	}

	public void reload() {//TODO re-evaluate re-load method
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
					Task.sleep(1000);
				}
			}
		}

		bot.terminateApplet();
		bot.resize(BotChrome.PANEL_WIDTH, BotChrome.PANEL_HEIGHT);

		BotChrome.getInstance().panel.setBot(bot);

		bot.start();//TODO wait for loaded game
		if (scriptHandler != null && scriptHandler.isActive()) {
			scriptHandler.resume();
		}

		bot.refreshing = false;
	}
}
