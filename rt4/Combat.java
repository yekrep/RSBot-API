package org.powerbot.script.rt4;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.util.StringUtils;

public class Combat extends ClientAccessor {
	public Combat(final ClientContext ctx) {
		super(ctx);
	}

	public int health() {
		return StringUtils.parseInt(ctx.widgets.component(160, 5).text());
	}

	public int specialPercentage() {
		return ctx.varpbits.varpbit(300) / 10;
	}

	public boolean specialAttack() {
		return ctx.varpbits.varpbit(301) == 1;
	}

	public boolean specialAttack(final boolean queued) {
		final int a = specialPercentage();
		final Component c = ctx.widgets.widget(593).component(31);
		return specialAttack() != queued && ctx.game.tab(Game.Tab.ATTACK) && c.visible() && c.click() && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return specialAttack() == queued || specialPercentage() != a;
			}
		}, 300, 6);
	}
}
