package org.powerbot.script.internal.scripts;

import org.powerbot.script.internal.Antipattern;
import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;
import org.powerbot.script.wrappers.Interactive;
import org.powerbot.script.wrappers.Npc;

public class ExaminePattern extends Antipattern {
	public ExaminePattern(final MethodContext factory) {
		super(factory);
	}

	@Override
	public void run() {
		if (isAggressive()) {
			for (final Npc n : ctx.npcs.select().select(new Filter<Npc>() {
				@Override
				public boolean accept(Npc npc) {
					return npc.isOnScreen();
				}
			}).shuffle().limit(Random.nextInt(1, isAggressive() ? 5 : 3))) {
				hover(n);
			}

			return;
		}

		for (final GameObject o : ctx.objects.select().select(new Filter<GameObject>() {
			@Override
			public boolean accept(final GameObject o) {
				return o.getType() == GameObject.Type.INTERACTIVE && o.isOnScreen();
			}
		}).shuffle().limit(Random.nextInt(1, isAggressive() ? 5 : 3))) {
			hover(o);
		}
	}

	private void hover(Interactive o) {
		boolean a = isAggressive();
		for (int i = a ? 0 : 1; i < 2 && o.hover(); i++) {
			sleep(80, 120);
			if (ctx.menu.click(Menu.filter("Examine")) && a) {
				sleep(100, 2000);
			}
		}
	}
}
