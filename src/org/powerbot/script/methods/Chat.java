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
	private static final int[][] WIDGET_CONTINUE = {{1189, 11}, {1184, 11}, {1186, 6}, {1191, 11}};

	public Chat(MethodContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ChatOption> get() {
		List<ChatOption> options = new ArrayList<ChatOption>(5);
		for (int i = 0; i < 5; i++) {
			Component component = ctx.widgets.get(WIDGET, COMPONENT_CHAT_OPTIONS[i]);
			if (!component.isValid()) {
				continue;
			}
			options.add(new ChatOption(ctx, i, component));
		}
		return options;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChatOption getNil() {
		return new ChatOption(ctx, -1, null);
	}

	public boolean isChatting() {
		if (ctx.widgets.get(WIDGET, 0).isValid()) {
			return true;
		}
		for (int[] arr : WIDGET_CONTINUE) {
			if (ctx.widgets.get(arr[0], 0).isValid()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the chat is continuable.
	 *
	 * @return <tt>true</tt> if the chat is continuable; otherwise <tt>false</tt>
	 */
	public boolean isContinue() {
		return getContinue() != null;
	}

	/**
	 * Continues the chat.
	 *
	 * @return <tt>true</tt> if the chat was continued; otherwise <tt>false</tt>
	 */
	public boolean clickContinue() {
		Component c = getContinue();
		return c != null && c.click();
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
}
