package org.powerbot.script.golem;

import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;

public class ExaminePattern extends Antipattern {
	public ExaminePattern(final MethodContext factory) {
		super(factory);
	}

	@Override
	public void run() {
		for (final GameObject o : ctx.objects.select().select(new Filter<GameObject>() {
			@Override
			public boolean accept(final GameObject o) {
				return o.getType() == GameObject.Type.INTERACTIVE && o.isOnScreen();
			}
		}).shuffle().limit(Random.nextInt(1, isAggressive() ? 5 : 3))) {
			boolean a = isAggressive();
			for (int i = a ? 0 : 1; i < 2 && o.hover(); i++) {
				sleep(80, 120);
				if (ctx.menu.click(Menu.filter("Examine")) && a) {
					sleep(100, 2000);
				}
			}
		}
	}
}
