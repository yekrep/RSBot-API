package org.powerbot.script.xenon;

import org.powerbot.bot.Bot;
import org.powerbot.script.internal.input.InputHandler;

public class Keyboard {
	public static void type(final String str) {
		type(str, false);
	}

	public static void type(String str, final boolean pressEnter) {
		final InputHandler inputHandler = Bot.inputHandler();
		if (inputHandler == null) return;
		if (pressEnter) str += '\n';
		inputHandler.send(str);
	}
}
