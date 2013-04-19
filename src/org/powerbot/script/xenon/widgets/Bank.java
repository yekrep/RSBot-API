package org.powerbot.script.xenon.widgets;

import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.wrappers.Widget;

public class Bank {
	public static final int WIDGET = 762;

	public static boolean isOpen() {
		final Widget widget = getWidget();
		return widget != null && widget.isValid();
	}

	private static Widget getWidget() {
		return Widgets.get(WIDGET);
	}
}
