package org.powerbot.game.bot.random;

import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;

/**
 * @author Timer
 */
@Manifest(name = "Login", description = "Logs into the game and handles errors.", version = 0.1, authors = {"Timer"})
public class Login extends AntiRandom {
	public boolean applicable() {
		final int state = Game.getClientState();
		return state == Game.INDEX_LOGIN_SCREEN || state == Game.INDEX_LOBBY_SCREEN;
	}

	public void run() {
	}
}
