package org.powerbot.script.golem;

import java.util.EnumSet;

import org.powerbot.script.lang.Filter;
import org.powerbot.script.methods.Menu;
import org.powerbot.script.methods.MethodContext;
import org.powerbot.script.util.Random;
import org.powerbot.script.wrappers.GameObject;

public class ExaminePattern extends Antipattern {
	public ExaminePattern(MethodContext factory) {
		super(factory);
	}

	@Override
	public void run(EnumSet<Preference> preferences) {
		boolean aggressive = System.nanoTime() % 5 == 0;
		for (GameObject object : ctx.objects.select().select(new Filter<GameObject>() {
			@Override
			public boolean accept(GameObject object) {
				return object.getType() == GameObject.Type.INTERACTIVE && object.isOnScreen();
			}
		}).shuffle().limit(Random.nextInt(1, aggressive ? 5 : 3))) {
			boolean repeat = aggressive && System.nanoTime() % 5 == 0;
			for (; object.hover(); ) {
				ctx.menu.click(Menu.filter("Examine"));
				if (repeat) {
					repeat = false;
					continue;
				}
				break;
			}
		}
	}
}
