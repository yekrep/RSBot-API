package org.powerbot.game.loader;

import org.powerbot.game.GameDefinition;
import org.powerbot.game.loader.wrapper.Rs2Applet;
import org.powerbot.util.Configuration;

public class Loader implements Runnable {
	private GameDefinition definition;

	public Loader(GameDefinition definition) {
		this.definition = definition;
	}

	public void run() {
		if (definition.initializeEnvironment()) {
			definition.appletContainer = new Rs2Applet(definition.classes(), "http://" + Configuration.Paths.URLs.GAME + "/", definition.callback);
			try {
				definition.stub = new ClientStub(definition.crawler.game, definition.crawler.archive, definition.crawler.parameters);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			definition.appletContainer.setStub(definition.stub);
			definition.stub.setApplet(definition.appletContainer);
			definition.stub.setActive(true);
			definition.appletContainer.init();
			definition.appletContainer.start();
		}
	}
}
