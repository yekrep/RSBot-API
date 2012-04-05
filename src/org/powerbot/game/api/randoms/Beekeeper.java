package org.powerbot.game.api.randoms;

import java.awt.Point;

import org.powerbot.game.api.AntiRandom;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Npcs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.Locations;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.interactive.Player;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@Manifest(name = "Beekeeper", description = "Assembles a bee house for those stinging nuisances", authors = {"Timer"}, version = 1.0)
public class Beekeeper extends AntiRandom {
	private static final int NPC_ID_BEE_KEEPER = 8649;
	private static final int LOCATION_ID_BEE_HOUSE = 16168;

	private static final int WIDGET_HIVE = 420;
	private static final int WIDGET_HIVE_BUILD = 40;
	private static final int WIDGET_HIVE_CLOSE = 38;
	private static final int WIDGET_CHAT = 1188;
	private static final int WIDGET_CHAT_TRY_AGAIN = 1191;
	private static final int WIDGET_CHAT_TEXT_INDEX_2 = 24;
	private static final int WIDGET_CHAT_CONTINUE = 18;

	private static final int LID = 8, UP_MID = 9, LOW_MID = 10, LEGS = 11;
	private static final int LOWERMID = 16022, UPPERMID = 16025, BOTTOM = 16034, TOP = 16036;
	private static final int[][] WIDGET_HIVE_MODEL_IDS = {
			{TOP, -1, LID},
			{UPPERMID, -1, UP_MID},
			{LOWERMID, -1, LOW_MID},
			{BOTTOM, -1, LEGS}
	};

	@Override
	public boolean validate() {
		return Game.isLoggedIn() && Npcs.getNearest(NPC_ID_BEE_KEEPER) != null && Locations.getNearest(LOCATION_ID_BEE_HOUSE) != null;
	}

	@Override
	public void run() {
		final Player player = Players.getLocal();

		if (Widgets.get(WIDGET_CHAT, WIDGET_CHAT_TEXT_INDEX_2).validate()) {//try again!
			Widgets.get(WIDGET_CHAT, WIDGET_CHAT_TEXT_INDEX_2).click(true);
			Time.sleep(Random.nextInt(1800, 2500));
			return;
		}
		if (Widgets.get(WIDGET_CHAT_TRY_AGAIN, WIDGET_CHAT_CONTINUE).validate()) {//try again continue
			Widgets.get(WIDGET_CHAT_TRY_AGAIN, WIDGET_CHAT_CONTINUE).click(true);
			Time.sleep(Random.nextInt(1800, 2500));
			return;
		}
		if (Widgets.clickContinue()) {
			Time.sleep(Random.nextInt(1800, 2500));
			return;
		}

		final Widget widget = Widgets.get(WIDGET_HIVE);
		if (widget.validate()) {
			for (WidgetChild child : widget.getChildren()) {
				if (child.getIndex() < 30) {
					for (int i = 0; i < WIDGET_HIVE_MODEL_IDS.length; i++) {
						if (child.getModelId() == WIDGET_HIVE_MODEL_IDS[i][0]) {
							WIDGET_HIVE_MODEL_IDS[i][1] = child.getIndex();
							break;
						}
					}
				}
			}

			for (int i = 0; i < 4; i++) {
				merge(widget.getChild(WIDGET_HIVE_MODEL_IDS[i][1]), widget.getChild(WIDGET_HIVE_MODEL_IDS[i][2]));
				Time.sleep(Random.nextInt(300, 800));
			}
			Time.sleep(Random.nextInt(1800, 2800));
			if (Settings.get(805) == 0x68d1000) {
				widget.getChild(WIDGET_HIVE_BUILD).click(true);
				Time.sleep(Random.nextInt(1800, 2800));
				return;
			}

			widget.getChild(WIDGET_HIVE_CLOSE).interact("Close");
			return;
		}

		if (player.getInteracting() == null) {
			Npcs.getNearest(NPC_ID_BEE_KEEPER).interact("Talk-to");
			Time.sleep(Random.nextInt(800, 1200));
		}
	}

	private void merge(final WidgetChild child1, final WidgetChild child2) {
		final Point center_1 = child1.getCentralPoint();
		final Point center_2 = child2.getCentralPoint();
		Mouse.move(center_1.x, center_1.y);
		Mouse.drag(center_2.x, center_2.y, 25, 10);
	}
}
