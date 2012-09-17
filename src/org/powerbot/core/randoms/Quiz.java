package org.powerbot.core.randoms;

import java.util.Arrays;

import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@Manifest(name = "Quiz Master", authors = {"Timer"}, version = 1.0)
public class Quiz extends AntiRandom {
	private static final int WIDGET_QUIZ = 191;
	private static final int[] WIDGET_QUIZ_CHILD_ID_FISH = {6189, 6190};
	private static final int[] WIDGET_QUIZ_CHILD_ID_COMBAT = {6192, 6194};
	private static final int[] WIDGET_QUIZ_CHILD_ID_FARMING = {6195, 6196};
	private static final int[] WIDGET_QUIZ_CHILD_ID_JEWELRY = {6197, 6198};

	private static final int[][] WIDGET_QUIZ_ITEMS = {
			WIDGET_QUIZ_CHILD_ID_FISH,
			WIDGET_QUIZ_CHILD_ID_COMBAT,
			WIDGET_QUIZ_CHILD_ID_FARMING,
			WIDGET_QUIZ_CHILD_ID_JEWELRY
	};

	private static final String[] WIDGET_QUIZ_NAMES = {
			"Fish", "Combat", "Farming", "Jewelry"
	};

	@Override
	public boolean activate() {
		return NPCs.getNearest(2477) != null;
	}

	@Override
	public void execute() {
		if (Widgets.clickContinue()) {
			log("Listening to his energetic shouting in this black hole...");
			sleep(Random.nextInt(2000, 3500));
			return;
		}

		if (getSlotId(0) != -1) {
			log("WIDGET VALIDATED: Quiz question");
			final int[] slots = {getSlotId(0), getSlotId(1), getSlotId(2)};
			log("WIDGET DEBUG: Slots " + Arrays.toString(slots));

			int[] valid = null;
			log("Attempting matches");
			int name_index = 0;
			for (final int[] QUIZ_ITEMS : WIDGET_QUIZ_ITEMS) {
				log("Checking " + WIDGET_QUIZ_NAMES[name_index] + " " + Arrays.toString(QUIZ_ITEMS));
				int count = 0;
				for (final int ITEM : QUIZ_ITEMS) {
					log("SEARCH: " + ITEM + " in " + Arrays.toString(QUIZ_ITEMS));
					for (final int slot : slots) {
						if (slot == ITEM) {
							count++;
						}
					}
					log("NEW COUNT: " + count);
				}
				log("FINAL COUNT: " + count);

				if (count == 2) {
					log("Generalized type: " + WIDGET_QUIZ_NAMES[name_index] + " " + Arrays.toString(QUIZ_ITEMS) + ".");
					valid = QUIZ_ITEMS;
					break;
				}

				++name_index;
			}

			if (valid != null) {
				log("== ENTER SEARCH ==");
				for (int index = 0; index < slots.length; index++) {
					int count = 0;
					for (final int id : valid) {
						if (id == slots[index]) {
							count++;
						}
					}
					log("SEARCH " + index + " (" + slots[index] + ") IN " + Arrays.toString(valid) + " RETURNED " + count);
					if (count == 0) {
						log("NO MATCH == FOUND INVALID CHILD");
						final WidgetChild widgetChild = getSlot(index);
						log("Attempting click.");
						if (widgetChild != null && widgetChild.validate()) {
							log("WIDGET INTERACT: \"Continue\": " + Boolean.toString(widgetChild.interact("Continue")));
							sleep(Random.nextInt(800, 1200));
						}
						return;
					}
				}
			}
		}
	}

	private int getSlotId(final int slot) {
		return Widgets.get(WIDGET_QUIZ, 5 + slot).getChildId();
	}

	private WidgetChild getSlot(final int slot) {
		return Widgets.get(WIDGET_QUIZ, 2 + slot);
	}
}