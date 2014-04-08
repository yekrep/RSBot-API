package org.powerbot.bot.rt6.activation;

import java.util.logging.Logger;

import org.powerbot.script.rt6.ClientAccessor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.MessageListener;

public class MessageLogger extends ClientAccessor implements MessageListener {
	private static final Logger log = Logger.getLogger("Messages");

	public MessageLogger(final ClientContext ctx) {
		super(ctx);
	}

	public void messaged(final MessageEvent e) {
		if (e.getSender().isEmpty()) {
			log.info("[" + e.getId() + "] " + e.getMessage());
		} else {
			log.info("[" + e.getId() + "] " + e.getSender() + ": " + e.getMessage());
		}
	}
}
