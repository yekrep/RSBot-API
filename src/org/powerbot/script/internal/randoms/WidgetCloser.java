package org.powerbot.script.internal.randoms;

import org.powerbot.script.xenon.util.Random;
import org.powerbot.script.xenon.util.Timer;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.wrappers.widget.WidgetChild;

@RandomManifest(name = "Widget closer")
public class WidgetCloser extends AntiRandom {
	private static final int INTERVAL = 60000;
	private static final int[] COMPONENTS = {
			21 << 16 | 43,//beholding a player's statuette (duellist's cap)
			1234 << 16 | 15,//membership offers
			906 << 16 | 354,//membership offers
			906 << 16 | 493,//email register
			1252 << 16 | 8,//Squeal of Fortune notification
			1253 << 16 | 16,//Squeal of Fortune window
			1218 << 16 | 77,//Advanced skill guide
			1107 << 16 | 157,//clan thingys
			755 << 16 | 44,//world map
	};
	private final Timer threshold;
	private WidgetChild component;
	private int tries;

	public WidgetCloser() {
		this.threshold = new Timer(0);
	}

	@Override
	public boolean valid() {
		if (threshold.isRunning()) return false;
		WidgetChild child;
		for (final int p : COMPONENTS) {
			child = Widgets.get(p >> 16, p & 0xffff);
			if (child != null && child.validate()) {
				component = child;
				break;
			}
		}
		return component != null;
	}

	@Override
	public int loop() {
		if (!valid()) return -1;

		if (++tries > 3) {
			threshold.setEndIn(INTERVAL);
			return -1;
		}
		if (component.validate() && component.click(true)) {
			final Timer timer = new Timer(Random.nextInt(2000, 2500));
			while (timer.isRunning() && component.validate()) sleep(100, 250);
		}
		return 0;
	}

	@Override
	public void onFinish() {
		component = null;
		tries = 0;
	}
}
