package org.powerbot.script.xenon.widgets;

import org.powerbot.script.xenon.Settings;
import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.util.Delay;
import org.powerbot.script.xenon.util.Random;
import org.powerbot.script.xenon.util.Timer;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.script.xenon.wrappers.Item;
import org.powerbot.script.xenon.wrappers.Widget;

public class Bank {
	public static final int WIDGET = 762;
	public static final int COMPONENT_CLOSE = 45;
	public static final int SETTING_BANK_STATE = 1248;

	public static boolean isOpen() {
		final Widget widget = Widgets.get(WIDGET);
		return widget != null && widget.isValid();
	}

	public static boolean close(final boolean wait) {
		if (!isOpen()) return true;
		final Component c = Widgets.get(WIDGET, COMPONENT_CLOSE);
		if (c == null) return false;
		if (c.isValid() && c.interact("Close")) {
			if (!wait) return true;
			final Timer t = new Timer(Random.nextInt(1000, 2000));
			while (t.isRunning() && isOpen()) Delay.sleep(100);
			return !isOpen();
		}
		return false;
	}

	public static boolean close() {
		return close(true);
	}

	public static int getCurrentTab() {
		return ((Settings.get(SETTING_BANK_STATE) >>> 24) - 136) / 8;
	}

	public static boolean setCurrentTab(final int index) {
		final Component c = Widgets.get(WIDGET, 63 - (index * 2));
		if (c != null && c.isValid()) return c.click(true);
		return false;
	}

	public static Item getTabItem(final int index) {
		final Component c = Widgets.get(WIDGET, 63 - (index * 2));
		if (c != null && c.isValid()) return new Item(c);
		return null;
	}
}
