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
}
