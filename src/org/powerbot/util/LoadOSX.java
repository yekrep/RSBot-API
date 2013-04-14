package org.powerbot.util;

import org.powerbot.OSXAdapter;
import org.powerbot.gui.BotChrome;
import org.powerbot.gui.controller.BotInteract;

import java.util.concurrent.Callable;

/**
 * @author Paris
 */
public class LoadOSX implements Callable<Boolean> {

	@Override
	public Boolean call() throws Exception {
		if (Configuration.OS == Configuration.OperatingSystem.MAC) {
			OSXAdapter.setAboutHandler(this, getClass().getDeclaredMethod("about", null));
			OSXAdapter.setQuitHandler(null, getClass().getDeclaredMethod("quit", null));
		}
		return true;
	}

	public static void about() {
		BotInteract.showDialog(BotInteract.Action.ABOUT);
	}

	public static void quit() {
		BotInteract.tabClose(false);
	}
}
