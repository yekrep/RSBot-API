package org.powerbot.bot.script.environment;

import org.powerbot.bot.script.Antipattern;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.rs3.tools.Menu;
import org.powerbot.script.rs3.tools.MethodContext;
import org.powerbot.script.util.Random;
import org.powerbot.script.rs3.tools.GameObject;
import org.powerbot.script.rs3.tools.Interactive;
import org.powerbot.script.rs3.tools.Npc;

public class ExaminePattern extends Antipattern {
	public ExaminePattern(final MethodContext factory) {
		super(factory);
		freq.set(freq.get() / 5);
	}

	@Override
	public void run() {
		if (isAggressive()) {
			for (final Npc n : ctx.npcs.select().select(new Filter<Npc>() {
				@Override
				public boolean accept(final Npc npc) {
					return npc.isInViewport();
				}
			}).shuffle().limit(isAggressive() ? 1 : Random.nextInt(1, 3))) {
				hover(n);
			}

			return;
		}

		for (final GameObject o : ctx.objects.select().select(new Filter<GameObject>() {
			@Override
			public boolean accept(final GameObject o) {
				return o.getType() == GameObject.Type.INTERACTIVE && o.isInViewport();
			}
		}).shuffle().limit(isAggressive() ? 1 : Random.nextInt(1, 3))) {
			hover(o);
		}
	}

	private void hover(final Interactive o) {
		final boolean a = isAggressive();
		for (int i = a ? 0 : 1; i < 2 && o.hover(); i++) {
			sleep(80, 120);
			if (ctx.menu.click(Menu.filter("Examine")) && a) {
				sleep(100, 2000);
			}
		}
	}
}
