package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.lang.TextQuery;
import org.powerbot.script.wrappers.ChatOption;
import org.powerbot.script.wrappers.Component;

public class Chat extends TextQuery<ChatOption> {
	public static final int WIDGET = 1188;
	private static final int[] COMPONENT_CHAT_OPTIONS = {
			11, 19, 24, 29, 34
	};
	private static final int[][] WIDGET_CONTINUE = {{1189, 11}, {1184, 13}, {1186, 6}, {1191, 12}};

	public Chat(MethodContext factory) {
		super(factory);
	}

	@Override
	public List<ChatOption> get() {
		List<ChatOption> options = new ArrayList<>(5);
		for (int i = 0; i < 5; i++) {
			Component component = ctx.widgets.get(WIDGET, COMPONENT_CHAT_OPTIONS[i]);
			if (!component.isValid()) {
				continue;
			}
			options.add(new ChatOption(ctx, i, component));
		}
		return options;
	}

	@Override
	public ChatOption getNil() {
		return new ChatOption(ctx, -1, null);
	}

	private Component getContinue() {
		for (int[] a : WIDGET_CONTINUE) {
			Component c = ctx.widgets.get(a[0], a[1]);
			if (!c.isValid()) {
				continue;
			}
			return c;
		}
		return null;
	}

	public boolean isContinue() {
		return getContinue() != null;
	}

	public boolean clickContinue() {
		Component c = getContinue();
		return c != null && c.click();
	}
}
