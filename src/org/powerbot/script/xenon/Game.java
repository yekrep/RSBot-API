package org.powerbot.script.xenon;

import org.powerbot.bot.Bot;
import org.powerbot.script.internal.Constants;
import org.powerbot.script.xenon.wrappers.Tile;
import org.powerbot.game.client.BaseInfo;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSInfo;

public class Game {
	public static final int INDEX_LOGIN_SCREEN = 3;
	public static final int INDEX_LOBBY_SCREEN = 7;
	public static final int INDEX_LOGGING_IN = 9;
	public static final int INDEX_MAP_LOADED = 11;
	public static final int INDEX_MAP_LOADING = 12;
	public static final int[] INDEX_LOGGED_IN = {INDEX_MAP_LOADED, INDEX_MAP_LOADING};

	public static int getClientState() {
		final Client client = Bot.client();
		if (client == null) return -1;

		final Constants constants = Bot.constants();
		final int clientState = client.getLoginIndex();
		if (clientState == constants.CLIENTSTATE_3) {
			return 3;
		} else if (clientState == constants.CLIENTSTATE_7) {
			return 7;
		} else if (clientState == constants.CLIENTSTATE_9) {
			return 9;
		} else if (clientState == constants.CLIENTSTATE_11) {
			return 11;
		} else if (clientState == constants.CLIENTSTATE_12) {
			return 12;
		}
		return -1;
	}

	public static boolean isLoggedIn() {
		final int state = getClientState();
		for (final int loggedInState : INDEX_LOGGED_IN) if (loggedInState == state) return true;
		return false;
	}

	public static Tile getMapBase() {
		final Client client = Bot.client();
		if (client == null) return null;

		final RSInfo info = client.getRSGroundInfo();
		final BaseInfo baseInfo = info != null ? info.getBaseInfo() : null;
		return baseInfo != null ? new Tile(baseInfo.getX(), baseInfo.getY(), client.getPlane()) : null;
	}

	public static int getPlane() {
		final Client client = Bot.client();
		if (client == null) return -1;

		return client.getPlane();
	}
}
