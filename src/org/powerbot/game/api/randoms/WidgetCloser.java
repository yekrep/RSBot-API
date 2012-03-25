package org.powerbot.game.api.randoms;

import java.util.HashMap;
import java.util.Map;

import org.powerbot.game.api.AntiRandom;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Time;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@Manifest(name = "WidgetCloser", description = "Closes widgets that interrupt scripts.", version = 0.1, authors = {"Timer"})
public class WidgetCloser extends AntiRandom {
	private static final Map<Integer, Integer> children = new HashMap<Integer, Integer>();

	static {
		children.put(1252, 6);//Squeal of Fortune
	}

	public boolean applicable() {
		if (Game.isLoggedIn()) {
			for (final Map.Entry<Integer, Integer> child : children.entrySet()) {
				final WidgetChild widgetChild = Widgets.get(child.getKey(), child.getValue());
				if (widgetChild.verify()) {
					return true;
				}
			}
		}
		return false;
	}

	public void run() {
		try {
			for (final Map.Entry<Integer, Integer> child : children.entrySet()) {
				final WidgetChild widgetChild = Widgets.get(child.getKey(), child.getValue());
				if (widgetChild.verify()) {
					widgetChild.click(true);
					Time.sleep(Random.nextInt(1200, 2400));
				}
			}
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}
