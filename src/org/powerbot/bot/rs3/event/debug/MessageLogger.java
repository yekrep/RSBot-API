package org.powerbot.bot.rs3.event.debug;

import java.util.logging.Logger;

import org.powerbot.script.event.MessageEvent;
import org.powerbot.script.event.MessageListener;

public class MessageLogger implements MessageListener {
	private static final Logger log = Logger.getLogger("Messages");

	public void messaged(final MessageEvent e) {
		if (e.getSender().equals("")) {
			log.info("[" + e.getId() + "] " + e.getMessage());
		} else {
			log.info("[" + e.getId() + "] " + e.getSender() + ": " + e.getMessage());
		}
	}
}
