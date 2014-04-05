package org.powerbot.bot.rt4.randoms;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Widget;

public class Quiz extends AntiRandom {
	private static final int WIDGET = 191;
	private static final int SLOT_1 = 5, SLOT_2 = 6, SLOT_3 = 7;
	private static final int WIDGET_PRIZE = 228;
	private static final int COMPONENT_COINS = 1, COMPONENT_BOX = 2;
	private static final int[][] MODELS = {{6189, 6190}, {6191, 6192}, {6193, 6194}, {6195, 6196}, {6197, 6198}};
	private static final String[] GROUPS = {"Fish-related", "Weapon-related", "Defence-related", "Farming-related", "Jewelry-related"};

	@Override
	public void poll() {
		final Widget w1 = ctx.widgets.widget(WIDGET);
		final Component c1 = w1.component(SLOT_1), c2 = w1.component(SLOT_2), c3 = w1.component(SLOT_3);
		final Widget w2 = ctx.widgets.widget(WIDGET_PRIZE);
		final Component o1 = w2.component(COMPONENT_COINS), o2 = w2.component(COMPONENT_BOX);

		final int s1 = c1.modelId(), s2 = c2.modelId(), s3 = c3.modelId();
		if (!(s1 == -1 || s2 == -1 || s3 == -1)) {
			final Component[] comps = {c1, c2, c3};
			final int answer = answer(new int[]{s1, s2, s3});
			if (answer == -1 || !comps[answer].click()) {
				Condition.sleep(100);
				return;
			}
			if (!Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return !(c1.valid() && c2.valid() && c3.valid()) ||
							c1.modelId() == -1 || c2.modelId() == -1 || c3.modelId() == -1;
				}
			}, 150, 10)) {
				return;
			}
			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return (!(c1.modelId() == -1 || c2.modelId() == -1 || c3.modelId() == -1) && c1.valid() && c2.valid() && c3.valid()) ||
							(o1.valid() && o2.valid());
				}
			});
			return;
		}

		if (o1.valid() && o2.valid()) {
			final Component[] options = {o1, o2};
			options[Random.nextInt(0, options.length)].interact("Continue");

			Condition.wait(new Callable<Boolean>() {
				@Override
				public Boolean call() {
					return !valid();
				}
			});
			return;
		}

		Condition.sleep(500);
	}

	@Override
	public boolean valid() {
		return ctx.npcs.select().name("Quiz Master").poll().valid();
	}

	private int answer(final int[] arr) {
		for (final int[] models : MODELS) {
			Arrays.sort(models);
			int count = 0;
			int answer = -1;
			for (int slot = 0; slot < arr.length; slot++) {
				if (Arrays.binarySearch(models, arr[slot]) < 0) {
					answer = slot;
					continue;
				}
				++count;
			}
			if (count == 2 && answer != -1) {
				return answer;
			}
		}
		return -1;
	}
}
