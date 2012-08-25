package org.powerbot.game.bot;

import java.util.concurrent.Future;

import org.powerbot.event.EventDispatcher;
import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.util.internal.Constants;
import org.powerbot.game.api.util.internal.Multipliers;
import org.powerbot.game.bot.handler.RandomHandler;
import org.powerbot.game.bot.handler.input.MouseExecutor;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.Render;
import org.powerbot.game.client.RenderData;

public class BotComposite {
	MouseExecutor executor;
	EventDispatcher eventDispatcher;
	ActiveScript activeScript;
	RandomHandler randomHandler;
	Future<?> antiRandomFuture;
	Context context;

	Client client;
	public final Calculations.Toolkit toolkit;
	public final Calculations.Viewport viewport;

	public Constants constants;
	public Multipliers multipliers;

	public BotComposite(final Bot bot) {
		executor = null;
		eventDispatcher = new EventDispatcher();
		activeScript = null;
		randomHandler = new RandomHandler(bot);
		antiRandomFuture = null;

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
}
