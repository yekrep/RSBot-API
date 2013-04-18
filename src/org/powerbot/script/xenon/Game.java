package org.powerbot.script.xenon;

import java.awt.Canvas;
import java.awt.Dimension;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.BaseInfo;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSInfo;
import org.powerbot.script.internal.Constants;
import org.powerbot.script.xenon.util.Delay;
import org.powerbot.script.xenon.util.Timer;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.script.xenon.wrappers.Tile;

public class Game {
	public static final int TAB_NONE = -1;
	public static final int TAB_COMBAT = 0;
	public static final int TAB_NOTICEBOARD = 1;
	public static final int TAB_STATS = 2;
	public static final int TAB_ACADEMY = 3;
	public static final int TAB_INVENTORY = 4;
	public static final int TAB_EQUIPMENT = 5;
	public static final int TAB_PRAYER = 6;
	public static final int TAB_ABILITY_BOOK = 7;
	public static final int TAB_EXTRAS = 8;
	public static final int TAB_FRIENDS = 9;
	public static final int TAB_FRIENDS_CHAT = 10;
	public static final int TAB_CLAN_CHAT = 11;
	public static final int TAB_OPTIONS = 12;
	public static final int TAB_EMOTES = 13;
	public static final int TAB_MUSIC = 14;
	public static final int TAB_NOTES = 15;
	public static final int TAB_LOGOUT = 16;
	public static final String[] TAB_NAMES = {
			"Combat", "Noticeboard", "Stats", "Combat Academy", "Inventory", "Worn Equipment", "Prayer List", "Ability Book",
			"Extras", "Friends List", "Friends Chat", "Clan Chat", "Options", "Emotes", "Music Player", "Notes", "Exit"
	};
	public static final int INDEX_LOGIN_SCREEN = 3;
	public static final int INDEX_LOBBY_SCREEN = 7;
	public static final int INDEX_LOGGING_IN = 9;
	public static final int INDEX_MAP_LOADED = 11;
	public static final int INDEX_MAP_LOADING = 12;
	public static final int[] INDEX_LOGGED_IN = {INDEX_MAP_LOADED, INDEX_MAP_LOADING};

	public static int getCurrentTab() {
		Component c;
		for (int i = 0; i < TAB_NAMES.length - 1; i++) {
			if ((c = Components.getTab(i)) != null) {
				if (c.getTextureId() != -1) return i;
			}
		}
		if ((c = Widgets.get(182, 1)) != null && c.isVisible()) return TAB_LOGOUT;
		return TAB_NONE;
	}

	public static boolean openTab(final int index) {
		if (index < 0 || index >= TAB_NAMES.length) return false;
		final Component c = Components.getTab(index);
		if (c != null && c.isValid() && c.click(true)) {
			final Timer t = new Timer(800);
			while (t.isRunning() && getCurrentTab() != index) Delay.sleep(15);
		}
		return getCurrentTab() == index;
	}

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

	public static boolean isFixed() {
		final Client client = Bot.client();
		if (client == null) return false;
		return client.getGUIRSInterfaceIndex() != 746;
	}

	public static void setPreferredWorld(final int world) {
		//TODO this
	}

	public static Dimension getDimensions() {
		final Client client = Bot.client();
		final Canvas canvas;
		if (client == null || (canvas = client.getCanvas()) == null) return new Dimension(0, 0);
		return new Dimension(canvas.getWidth(), canvas.getHeight());
	}
}
