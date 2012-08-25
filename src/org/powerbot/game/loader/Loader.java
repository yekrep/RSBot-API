package org.powerbot.game.loader;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.powerbot.game.bot.Bot;
import org.powerbot.game.loader.applet.ClientStub;
import org.powerbot.game.loader.applet.Rs2Applet;
import org.powerbot.util.Configuration;

/**
 * A runnable loader that loads the game of a game environment.
 *
 * @author Timer
 */
public class Loader implements Runnable {
	private static Logger log = Logger.getLogger(Loader.class.getName());
	private final Bot bot;
	private final ClientLoader clientLoader;

	public Loader(final Bot bot) {
		this.bot = bot;
		clientLoader = bot.getClientLoader();
	}

	/**
	 * Creates the wrapping applet of the game, generates an appropriate stub, and initializes client activities.
	 */
	public void run() {
		log.fine("Generating applet wrapper");
		bot.appletContainer = new Rs2Applet(clientLoader.getClasses(), "http://" + Configuration.URLs.GAME + "/", bot.callback);
		try {
			log.fine("Generating stub");
			bot.stub = new ClientStub(clientLoader.crawler.game, clientLoader.crawler.archive, clientLoader.crawler.parameters);
		} catch (final Exception e) {
			log.log(Level.SEVERE, "Error creating stub: ", e);
			return;
		}
		log.fine("Setting stub");
		bot.appletContainer.setStub(bot.stub);
		bot.stub.setApplet(bot.appletContainer);
		bot.stub.setActive(true);
		log.fine("Initializing and starting applet wrapper");
		bot.appletContainer.init();
		bot.appletContainer.start();
	}
}
