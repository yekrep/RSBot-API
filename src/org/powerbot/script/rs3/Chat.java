package org.powerbot.script.rs3;

import java.util.ArrayList;
import java.util.List;

public class Chat extends TextQuery<ChatOption> {
	public static final int WIDGET = 1188;
	private static final int[] COMPONENT_CHAT_OPTIONS = {
			12, 18, 23, 28, 33
	};
	private static final int[][] WIDGET_CONTINUE = {{1189, 11}, {1184, 11}, {1186, 6}, {1191, 11}};

	public Chat(final ClientContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ChatOption> get() {
		final List<ChatOption> options = new ArrayList<ChatOption>(5);
		for (int i = 0; i < 5; i++) {
			final Component component = ctx.widgets.get(WIDGET, COMPONENT_CHAT_OPTIONS[i]);
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
		for (final int[] arr : WIDGET_CONTINUE) {
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
		return clickContinue(false);
	}

	/**
	 * Continues the chat.
	 *
	 * @param key <tt>true</tt> to press space; <tt>false</tt> to use the mouse.
	 * @return <tt>true</tt> if the chat was continued; otherwise <tt>false</tt>
	 */
	public boolean clickContinue(final boolean key) {
		final Component c = getContinue();
		return c != null && (key ? ctx.keyboard.send(" ") : c.click());
	}

	private Component getContinue() {
		for (final int[] a : WIDGET_CONTINUE) {
			final Component c = ctx.widgets.get(a[0], a[1]);
			if (!c.isValid()) {
				continue;
			}
			return c;
		}
		return null;
	}
}
