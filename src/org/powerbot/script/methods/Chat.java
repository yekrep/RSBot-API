package org.powerbot.script.methods;

import java.util.ArrayList;
import java.util.List;

import org.powerbot.script.lang.TextQuery;
import org.powerbot.script.wrappers.ChatOption;
import org.powerbot.script.wrappers.Component;

import static org.powerbot.script.util.Constants.getInt;
import static org.powerbot.script.util.Constants.getIntA;
import static org.powerbot.script.util.Constants.getObj;

public class Chat extends TextQuery<ChatOption> {
	public static final int WIDGET = getInt("chat.widget");
	private static final int[] COMPONENT_CHAT_OPTIONS = getIntA("chat.component.chat.options");
	private static final int[][] WIDGET_CONTINUE = getObj("chat.widget.continue", int[][].class);

	public Chat(MethodContext factory) {
		super(factory);
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChatOption getNil() {
		return new ChatOption(ctx, -1, null);
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
