package org.powerbot.bot.rt6;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.Interactive;
import org.powerbot.script.rt6.Menu;
import org.powerbot.script.rt6.Npc;

public class ExaminePattern extends Antipattern.Module {

	public ExaminePattern(final ClientContext ctx) {
		super(ctx);
		freq.set(1);
	}

	@Override
	public void run() {
		if (isAggressive()) {
			for (final Npc n : ctx.npcs.select().select(new Filter<Npc>() {
				@Override
				public boolean accept(final Npc npc) {
					return npc.inViewport();
				}
			}).shuffle().limit(isAggressive() ? 1 : Random.nextInt(1, 3))) {
				hover(n);
			}

			return;
		}

		for (final GameObject o : ctx.objects.select(Random.nextInt(7, 15)).nearest().limit(Random.nextInt(200, 440)).select(new Filter<GameObject>() {
			@Override
			public boolean accept(final GameObject o) {
				return o.type() == GameObject.Type.INTERACTIVE && o.inViewport();
			}
		}).shuffle().limit(isAggressive() ? 1 : Random.nextInt(1, 3))) {
			hover(o);
		}
	}

	private void hover(final Interactive o) {
		final boolean a = isAggressive();
		for (int i = a ? 0 : 1; i < 2 && o.hover(); i++) {
			if (ctx.menu.click(Menu.filter("Examine")) && a) {
				Condition.sleep(600);
			}
		}
	}
}
