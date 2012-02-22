package org.powerbot.game.bot;

import org.powerbot.asm.NodeProcessor;
import org.powerbot.game.GameDefinition;
import org.powerbot.game.loader.Loader;

public class Bot extends GameDefinition {
	/**
	 * {@inheritDoc}
	 */
	public void startEnvironment() {
		processor.submit(new Loader(this));
	}

	/**
	 * {@inheritDoc}
	 */
	public NodeProcessor getProcessor() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void killEnvironment() {
		if (stub != null) {
			stub.setActive(false);
		}
		if (appletContainer != null) {
			appletContainer.stop();
			appletContainer.destroy();
			appletContainer = null;
			stub = null;
		}
	}
}
