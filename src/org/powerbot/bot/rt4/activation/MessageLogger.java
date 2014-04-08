package org.powerbot.bot.rt4.activation;

import java.util.logging.Logger;

import org.powerbot.script.MessageEvent;
import org.powerbot.script.MessageListener;
import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;

public class MessageLogger extends ClientAccessor implements MessageListener {
	private static final Logger log = Logger.getLogger("Messages");

	public MessageLogger(final ClientContext ctx) {
		super(ctx);
	}

	public void messaged(final MessageEvent e) {
		if (e.source().isEmpty()) {
			log.info("[" + e.type() + "] " + e.text());
		} else {
			log.info("[" + e.type() + "] " + e.source() + ": " + e.text());
		}
	}
}
