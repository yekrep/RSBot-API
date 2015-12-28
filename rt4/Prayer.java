package org.powerbot.script.rt4;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.StringUtils;

public class Prayer extends ClientAccessor {
	public Prayer(final ClientContext ctx) {
		super(ctx);
	}

	public int points() {
		return StringUtils.parseInt(ctx.widgets.component(160, 15).text());
	}

	public boolean praying() {
		return ctx.varpbits.varpbit(83) > 0;
	}

	public boolean quickPrayer() {
		return ctx.widgets.component(160, 16).textureId() == 1066;
	}

	public boolean quickPrayer(final boolean on) {
		return quickPrayer() == on || (ctx.widgets.component(160, 14).click() && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return quickPrayer() == on;
			}
		}, 300, 6));
	}
}
