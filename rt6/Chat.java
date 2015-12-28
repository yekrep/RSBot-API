package org.powerbot.script.rt6;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.bot.AbstractBot;
import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.rt6.client.Client;
import org.powerbot.bot.rt6.client.MessageEntry;
import org.powerbot.bot.rt6.client.NodeSub;
import org.powerbot.bot.rt6.client.NodeSubQueue;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.PaintListener;

/**
 * Chat
 */
public class Chat extends TextQuery<ChatOption> {
	private final AtomicBoolean registered;

	public Chat(final ClientContext factory) {
		super(factory);
		registered = new AtomicBoolean(false);
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

	public void register() {
		if (!registered.compareAndSet(false, true)) {
			return;
		}
		final EventDispatcher e = ((AbstractBot) ctx.bot()).dispatcher;
		e.add(new PaintListener() {
			private final AtomicReference<NodeSub> previous = new AtomicReference<NodeSub>(null);

			@Override
			public void repaint(final Graphics graphics) {
				final Client client = ctx.client();
				if (client == null) {
					return;
				}

				final NodeSubQueue q = client.getLoggerEntries();
				final NodeSub s = q.getSentinel();
				NodeSub c = s.getNextSub();
				if (previous.get() != null && !previous.get().isNull()) {
					final NodeSub n = previous.get().getNextSub();
					c = n.isNull() ? c : n;
				}
				while (!s.equals(c)) {
					final MessageEntry m = new MessageEntry(c.reflector, c);
					e.dispatch(new MessageEvent(m));
					previous.set(c);
					c = c.getNextSub();
				}
			}
		});
	}
}
