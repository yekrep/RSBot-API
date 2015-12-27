package org.powerbot.script;

import org.powerbot.util.StringUtils;

public class MenuCommand {
	public final String action, option;

	public MenuCommand(final String a, final String o) {
		action = a != null ? StringUtils.stripHtml(a) : "";
		option = o != null ? StringUtils.stripHtml(o) : "";
	}

	@Override
	public String toString() {
		return String.format("%s %s", action, option).trim();
	}
}