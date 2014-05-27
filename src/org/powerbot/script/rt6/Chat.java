package org.powerbot.script.rt6;

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
			final Component component = ctx.widgets.component(WIDGET, COMPONENT_CHAT_OPTIONS[i]);
			if (!component.valid()) {
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
	public ChatOption nil() {
		return new ChatOption(ctx, -1, null);
	}

	public boolean chatting() {
		if (ctx.widgets.component(WIDGET, 0).valid()) {
			return true;
		}
		for (final int[] arr : WIDGET_CONTINUE) {
			if (ctx.widgets.component(arr[0], 0).valid()) {
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
	public boolean queryContinue() {
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
		if (c != null) {
			if (key) {
				ctx.input.send(" ");
				return true;
			} else {
				return c.click();
			}
		}
		return false;
	}

	private Component getContinue() {
		for (final int[] a : WIDGET_CONTINUE) {
			final Component c = ctx.widgets.component(a[0], a[1]);
			if (!c.valid()) {
				continue;
			}
			return c;
		}
		return null;
	}
}
