package org.powerbot.script.rt6;

import java.util.ArrayList;
import java.util.List;

public class Chat extends TextQuery<ChatOption> {

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
			final Component component = ctx.widgets.component(Constants.CHAT_WIDGET, Constants.CHAT_OPTIONS[i]);
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
		if (ctx.widgets.component(Constants.CHAT_WIDGET, 0).valid()) {
			return true;
		}
		for (final int[] arr : Constants.CHAT_CONTINUE) {
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
		for (final int[] a : Constants.CHAT_CONTINUE) {
			final Component c = ctx.widgets.component(a[0], a[1]);
			if (!c.valid()) {
				continue;
			}
			return c;
		}
		return null;
	}
}
