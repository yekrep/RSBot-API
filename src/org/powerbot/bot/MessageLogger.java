package org.powerbot.bot;

import java.util.logging.Logger;

import org.powerbot.script.ClientAccessor;
import org.powerbot.script.ClientContext;
import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;

public class MessageLogger<C extends ClientContext> extends ClientAccessor<C> implements MessageListener {
	private static final Logger log = Logger.getLogger("Messages");

	public MessageLogger(final C ctx) {
		super(ctx);
	}

	public void messaged(final MessageEvent e) {
		log.info("[" + e.type() + "] " + e.source() + (e.source().isEmpty() ? "" : ": " + e.text()));
	}
}
