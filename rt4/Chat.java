package org.powerbot.script.rt4;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.bot.AbstractBot;
import org.powerbot.bot.EventDispatcher;
import org.powerbot.bot.rt4.client.Client;
import org.powerbot.bot.rt4.client.Entry;
import org.powerbot.bot.rt4.client.EntryList;
import org.powerbot.bot.rt4.client.MessageEntry;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.PaintListener;

/**
 * Chat
 * A utility class for simplifying interacting with the chat box.
 */
public class Chat extends ClientAccessor {
	private final AtomicBoolean registered = new AtomicBoolean(false);

	public Chat(final ClientContext ctx) {
		super(ctx);
	}

	public void register() {
		if (!registered.compareAndSet(false, true)) {
			return;
		}
		final EventDispatcher e = ((AbstractBot) ctx.bot()).dispatcher;
		e.add(new PaintListener() {
			private final AtomicReference<Entry> previous = new AtomicReference<Entry>(null);

			@Override
			public void repaint(final Graphics render) {
				final Client client = ctx.client();
				if (client == null) {
					return;
				}
				final EntryList q = client.getLoggerEntries();
				final Entry s = q.getSentinel();
				Entry c = s.getNext();
				final Entry f = c;
				while (!s.equals(c) && !c.isNull() && !c.equals(previous.get())) {
					final MessageEntry m = new MessageEntry(c.reflector, c);
					e.dispatch(new MessageEvent(m));
					c = c.getNext();
				}
				previous.set(f);
			}
		});
	}

	public boolean chatting() {
		return ctx.widgets.widget(Constants.CHAT_NPC).componentCount() > 0 ||
				ctx.widgets.widget(Constants.CHAT_OPTIONS).componentCount() > 0 ||
				ctx.widgets.widget(Constants.CHAT_PLAYER).componentCount() > 0;
	}

	public boolean canContinue() {
		return ctx.widgets.component(Constants.CHAT_NPC, Constants.CHAT_CONTINUE).valid() ||
				ctx.widgets.component(Constants.CHAT_PLAYER, Constants.CHAT_CONTINUE).valid();
	}

	public List<Component> chatOptions() {
		final List<Component> options = new ArrayList<Component>();
		final Component component = ctx.widgets.component(Constants.CHAT_OPTIONS, 0);
		for (int i = 1; i < component.componentCount() - 2; i++) {
			options.add(component.components()[i]);
		}
		return options;
	}

	public boolean continueChat(final String... options) {
		return continueChat(false, options);
	}

	public boolean continueChat(final boolean useKeys, final String... options) {
		if (!chatting()) {
			return false;
		}
		if (canContinue()) {
			Component c = ctx.widgets.component(Constants.CHAT_NPC, Constants.CHAT_CONTINUE);
			if (!c.valid()) {
				c = ctx.widgets.component(Constants.CHAT_PLAYER, Constants.CHAT_CONTINUE);
			}
			return useKeys ? ctx.input.send("{VK_SPACE}") : c.valid() && c.click("Continue");
		}
		if (options != null) {
			final List<String> o = Arrays.asList(options);
			final List<Component> ol = chatOptions();
			for (int i = 0; i < ol.size(); i++) {
				final Component component = ol.get(i);
				if (!o.contains(component.text())) {
					continue;
				}
				return useKeys ? ctx.input.send(String.valueOf(i + 1)) : component.click("Continue");
			}
		}
		return false;
	}

	public boolean pendingInput() {
		return ctx.widgets.component(162, 32).visible();
	}
}
