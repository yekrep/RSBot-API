package org.powerbot.script.rt4;

import java.awt.Graphics;
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

public class Chat extends ClientAccessor {
	public Chat(final ClientContext ctx) {
		super(ctx);
	}

	private final AtomicBoolean registered = new AtomicBoolean(false);

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
}
