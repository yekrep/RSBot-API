package org.powerbot.script.methods.widgets;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.powerbot.script.methods.World;
import org.powerbot.script.methods.WorldImpl;
import org.powerbot.script.util.Delay;
import org.powerbot.script.util.Filter;
import org.powerbot.script.util.Random;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Widget;

public class Lobby extends WorldImpl {
	public static final int STATE_LOBBY_IDLE = 7;
	public static final int STATE_LOGGING_IN = 9;
	public static final int LOGIN_DEFAULT_TIMEOUT = 30000;
	public static final int WIDGET_MAIN_LOBBY = 906;
	public static final int WIDGET_BUTTON_PLAY_GAME = 197;
	public static final int WIDGET_BUTTON_LOGOUT = 221;
	public static final int WIDGET_LABEL_CURRENT_WORLD = 11;
	public static final int WIDGET_WORLDS_TABLE = 62;
	public static final int WIDGET_WORLDS_TABLE_SCROLLBAR = 86;
	public static final int WIDGET_WORLDS_ROWS = 77;
	public static final int WIDGET_WORLDS_COLUMN_FAVOURITE = 68;
	public static final int WIDGET_WORLDS_COLUMN_WORLD_NUMBER = 69;
	public static final int WIDGET_WORLDS_COLUMN_MEMBERS = 70;
	public static final int WIDGET_WORLDS_COLUMN_PLAYERS = 71;
	public static final int WIDGET_WORLDS_COLUMN_ACTIVITY = 72;
	public static final int WIDGET_WORLDS_COLUMN_LOOT_SHARE = 75;
	public static final int WIDGET_WORLDS_COLUMN_PING = 76;

	public Lobby(World world) {
		super(world);
	}

	public boolean isOpen() {
		return world.game.getClientState() == STATE_LOBBY_IDLE;
	}

	/**
	 * Logs out of RuneScape by clicking the X button in the upper-right corner of the lobby widget.
	 *
	 * @return <tt>true</tt> if the logout button was clicked; otherwise <tt>false</tt>.
	 */
	public boolean close() {
		if (!isOpen() || !closeDialog()) {
			return false;
		}
		final Component child = world.widgets.get(WIDGET_MAIN_LOBBY, WIDGET_BUTTON_LOGOUT);
		return child != null && child.isValid() && child.click(true);
	}

	public boolean enterGame() {
		return enterGame(LOGIN_DEFAULT_TIMEOUT);
	}

	public boolean enterGame(final int timeout) {
		return enterGame(null, timeout);
	}

	public boolean enterGame(final Server server) {
		return enterGame(server, LOGIN_DEFAULT_TIMEOUT);
	}

	/**
	 * Attempts to login to the game from the lobby. It will close any open dialogs prior to logging in. This is
	 * a blocking method; it will wait until the account is logged in, or the timeout is reached, before the
	 * method exits.
	 * <p/>
	 * If the login fails, the {@link Dialog} will still be open when the method finishes as it allows the
	 * developer to diagnose the reason for login failure.
	 *
	 * @param server  The world to select before logging in. Can be <tt>null</tt> if no world selection is wanted.
	 * @param timeout The amount of time (in milliseconds) to wait for the account to login. If the timeout is
	 *                reached, the method will exit regardless the the current login state.
	 * @return <tt>true</tt> if the account is logged in; otherwise <tt>false</tt>.
	 */
	public boolean enterGame(final Server server, final int timeout) {
		if (world.game.getClientState() == STATE_LOBBY_IDLE) {
			if (!closeDialog() || (Tab.OPTIONS.isOpen() && !Tab.PLAYER_INFO.open())) {
				return false;
			}
			final Server selected = (server != null) ? getSelectedWorld() : null;
			if (selected != null && !selected.equals(server) && !server.click()) {
				return false;
			}
			final Component child = world.widgets.get(WIDGET_MAIN_LOBBY, WIDGET_BUTTON_PLAY_GAME);
			if (!(child != null && child.isValid() && child.click(true))) {
				return false;
			}
		}
		final Timer t = new Timer(timeout);
		while (t.isRunning() && !world.game.isLoggedIn()) {
			final Dialog dialog = getOpenDialog();
			if (dialog == Dialog.TRANSFER_COUNTDOWN || (dialog != null && dialog.clickContinue())) {
				t.reset();
			} else if (dialog != null) {
				Delay.sleep(500, 1000);
				break;
			}
			Delay.sleep(5);
		}
		return world.game.isLoggedIn();
	}

	/**
	 * Gets the currently selected world on the World Select panel. If the panel cannot be isValidd, the method
	 * will open the World Select tab in order to isValid it.
	 *
	 * @return The currently selected world, or <tt>null</tt> if unable to retrieve world.
	 */
	public Server getSelectedWorld() {
		if (!isOpen() || !closeDialog() || (!Tab.WORLD_SELECT.getPanelWidget().isValid() && !Tab.WORLD_SELECT.open())) {
			return null;
		}
		final Widget panel = Tab.WORLD_SELECT.getPanelWidget();
		final String text = panel.isValid() ? panel.getComponent(WIDGET_LABEL_CURRENT_WORLD).getText() : null;
		if (text != null) {
			final Matcher m = Pattern.compile("^World\\s(\\d*)$").matcher(text);
			if (m.find()) {
				return getWorld(Integer.parseInt(m.group(1)));
			}
		}
		return null;
	}

	public Server getWorld(final int worldNumber) {
		final Server[] servers = getWorlds(new Filter<Server>() {
			@Override
			public boolean accept(final Server world) {
				return world.getNumber() == worldNumber;
			}
		});
		return servers.length == 1 ? servers[0] : null;
	}

	public Server[] getWorlds() {
		return getWorlds(new Filter<Server>() {
			@Override
			public boolean accept(final Server server) {
				return true;
			}
		});
	}

	public Server[] getWorlds(final Filter<Server> filter) {
		if (!isOpen() || !closeDialog()) {
			return new Server[0];
		}
		final Widget panel = Tab.WORLD_SELECT.getPanelWidget();
		if (!panel.isValid() && !Tab.WORLD_SELECT.open()) {
			return new Server[0];
		}
		final ArrayList<Server> servers = new ArrayList<>();
		final Component[] rows = panel.getComponent(WIDGET_WORLDS_ROWS).getChildren();
		for (final Component row : rows) {
			final Server server = new Server(row.getIndex());
			if (filter.accept(server)) {
				servers.add(server);
			}
		}
		return servers.toArray(new Server[servers.size()]);
	}

	public Dialog getOpenDialog() {
		for (final Dialog d : Dialog.values()) {
			if (d.isOpen()) {
				return d;
			}
		}
		return null;
	}

	private boolean closeDialog() {
		final Dialog dialog = getOpenDialog();
		return dialog == null || (dialog.hasBack() && dialog.clickBack());
	}

	/**
	 * Representation of the lobby tabs.
	 */
	public enum Tab {
		PLAYER_INFO(230, 907), WORLD_SELECT(28, 910), FRIENDS(27, 909),
		FRIENDS_CHAT(280, 589), CLAN_CHAT(26, 912), OPTIONS(25, 911);
		private final int widgetTabIndex;
		private final int widgetPanelIndex;

		private Tab(final int widgetTabIndex, final int widgetPanelIndex) {
			this.widgetTabIndex = widgetTabIndex;
			this.widgetPanelIndex = widgetPanelIndex;
		}

		/**
		 * Gets the widget of the clickable tab.
		 *
		 * @return The widget of the tab.
		 */
		public Component getWidget() {
			if (!Lobby.this.isOpen()) {
				return null;
			}
			return Lobby.this.world.widgets.get(WIDGET_MAIN_LOBBY, widgetTabIndex);
		}

		/**
		 * Gets the tab's panel widget.
		 *
		 * @return The tab's panel widget.
		 */
		public Widget getPanelWidget() {
			if (!Lobby.this.isOpen()) {
				return null;
			}
			return Lobby.this.world.widgets.get(widgetPanelIndex);
		}

		public boolean isOpen() {
			final Component child = getWidget();
			return child != null && child.isValid() && child.getTextureId() == 4671;
		}

		public boolean open() {
			final Component child = getWidget();
			if (isOpen()) {
				return true;
			}
			if (child != null && child.isValid() && child.click(true)) {
				Delay.sleep(Random.nextInt(1200, 2000));
				return true;
			}
			return false;
		}
	}

	/**
	 * Representation of the lobby dialogs.
	 */
	public enum Dialog {
		TRANSFER_COUNTDOWN(255, -1, 252, "^You have only just left another world."),
		ACCOUNT_IN_USE(260, -1, 252, "^Your account has not logged out from its last session."),
		LOGIN_LIMIT_EXCEEDED(260, -1, 252, "^Login limit exceeded: too many connections from your address."),
		MEMBERS_ONLY_WORLD(260, -1, 252, "^You need a member's account to log in to this world."),
		INSUFFICIENT_SKILL_TOTAL(260, -1, 252, "^You must have a total skill level of"),
		//ACCOUNT_BANNED(-1, -1, -1, null), //TODO
		WILDERNESS_WARNING(118, 120, 113, "^Warning: This is a High-risk Wilderness world."),
		VALIDATE_EMAIL(379, 379, 352, "^Validate your email now for increased account security");
		private final int backButtonIndex;
		private final int continueButtonIndex;
		private final int textIndex;
		private final Pattern textPattern;

		private Dialog(final int backButtonIndex, final int continueButtonIndex, final int textIndex, final String textPattern) {
			this.backButtonIndex = backButtonIndex;
			this.continueButtonIndex = continueButtonIndex;
			this.textIndex = textIndex;
			this.textPattern = Pattern.compile(textPattern);
		}

		public boolean isOpen() {
			final Component child = Lobby.this.world.widgets.get(WIDGET_MAIN_LOBBY, textIndex);
			if (child != null && child.isOnScreen()) {
				final String text = child.getText();
				return text != null && textPattern.matcher(text).find();
			}
			return false;
		}

		public boolean hasContinue() {
			return continueButtonIndex != -1;
		}

		public boolean clickContinue() {
			if (!hasContinue()) {
				return false;
			}
			final Component child = Lobby.this.world.widgets.get(WIDGET_MAIN_LOBBY, continueButtonIndex);
			return child != null && child.isOnScreen() && child.click(true);
		}

		public boolean hasBack() {
			return backButtonIndex != -1;
		}

		public boolean clickBack() {
			if (!hasBack()) {
				return false;
			}
			final Component child = Lobby.this.world.widgets.get(WIDGET_MAIN_LOBBY, backButtonIndex);
			return child != null && child.isOnScreen() && child.click(true);
		}
	}

	public class Server {
		private final int number;
		private final boolean members;
		private final String activity;
		private final boolean lootShare;
		private int players;
		private int ping;
		private boolean favorite;

		private Server(final int widgetIndex) {
			final Widget panel = Tab.WORLD_SELECT.getPanelWidget();
			this.number = Integer.parseInt(panel.getComponent(WIDGET_WORLDS_COLUMN_WORLD_NUMBER).getChild(widgetIndex).getText());
			this.members = panel.getComponent(WIDGET_WORLDS_COLUMN_MEMBERS).getChild(widgetIndex).getTextureId() == 1531;
			this.activity = panel.getComponent(WIDGET_WORLDS_COLUMN_ACTIVITY).getChild(widgetIndex).getText();
			this.lootShare = panel.getComponent(WIDGET_WORLDS_COLUMN_LOOT_SHARE).getChild(widgetIndex).getTextureId() == 699;
			this.players = getPlayers();
			this.ping = getPing();
			this.favorite = isFavorite();
		}

		private int getWidgetIndex(final int worldNumber) {
			final Widget panel = Tab.WORLD_SELECT.getPanelWidget();
			if (panel == null || !panel.isValid()) {
				return -1;
			}
			for (final Component child : panel.getComponent(WIDGET_WORLDS_COLUMN_WORLD_NUMBER).getChildren()) {
				if (child.getText().equals(String.valueOf(worldNumber))) {
					return child.getIndex();
				}
			}
			return -1;
		}

		public int getNumber() {
			return number;
		}

		public boolean isMembers() {
			return members;
		}

		public String getActivity() {
			return activity;
		}

		public boolean isLootShare() {
			return lootShare;
		}

		/**
		 * Gets the current number of players.
		 *
		 * @return the number of players, or -1 if the world is offline or full.
		 */
		public int getPlayers() {
			final int index = getWidgetIndex(number);
			if (index != -1) {
				final Widget panel = Tab.WORLD_SELECT.getPanelWidget();
				try {
					players = Integer.parseInt(panel.getComponent(WIDGET_WORLDS_COLUMN_PLAYERS).getChild(index).getText());
				} catch (final NumberFormatException ex) {
					players = -1;
				}
			}
			return players;
		}

		public int getPing() {
			final int index = getWidgetIndex(number);
			if (index != -1) {
				final Widget panel = Tab.WORLD_SELECT.getPanelWidget();
				try {
					ping = Integer.parseInt(panel.getComponent(WIDGET_WORLDS_COLUMN_PING).getChild(index).getText());
				} catch (final NumberFormatException ex) {
					ping = 999;
				}
			}
			return ping;
		}

		public boolean isFavorite() {
			final int index = getWidgetIndex(number);
			if (index != -1) {
				final Widget panel = Tab.WORLD_SELECT.getPanelWidget();
				favorite = panel.getComponent(WIDGET_WORLDS_COLUMN_FAVOURITE).getChild(index).getTextureId() == 1541;
			}
			return favorite;
		}

		/**
		 * Opens the World Select tab and clicks on the correct world.
		 *
		 * @return <tt>true</tt> if the world is selected; otherwise <tt>false</tt>.
		 */
		public boolean click() {
			if (!isOpen() || (!Tab.WORLD_SELECT.isOpen() && !Tab.WORLD_SELECT.open())) {
				return false;
			}
			final Server selected = getSelectedWorld();
			if (selected != null && selected.equals(this)) {
				return true;
			}
			final int index = getWidgetIndex(number);
			if (index == -1) {
				return false;
			}
			final Widget panel = Tab.WORLD_SELECT.getPanelWidget();
			final Component table = panel.getComponent(WIDGET_WORLDS_TABLE);
			final Component row = panel.getComponent(WIDGET_WORLDS_ROWS).getChild(index);
			if (table != null && table.isValid() && row != null && row.isValid()) {
				final Rectangle visibleBounds = new Rectangle(
						table.getAbsoluteLocation(),
						new Dimension(table.getWidth(), table.getHeight() - row.getHeight())
				);
				if (!visibleBounds.contains(row.getAbsoluteLocation())) {
					final Component scrollBar = panel.getComponent(WIDGET_WORLDS_TABLE_SCROLLBAR);
					if (scrollBar == null || !world.widgets.scroll(row, scrollBar)) {
						return false;
					}
				}
				return row.click(true);
			}
			return false;
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof Server && ((Server) o).number == this.number;
		}
	}
}