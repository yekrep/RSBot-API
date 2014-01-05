package org.powerbot.script.methods;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.powerbot.script.lang.Filter;
import org.powerbot.script.util.Condition;
import org.powerbot.script.util.Timer;
import org.powerbot.script.wrappers.Component;
import org.powerbot.script.wrappers.Widget;

/**
 * Utilities pertaining to the lobby.
 *
 * @author Timer
 */
@SuppressWarnings("deprecation")
public class Lobby extends MethodProvider {
	public static final int STATE_LOBBY_IDLE = 7;
	public static final int STATE_LOGGING_IN = 9;
	public static final int LOGIN_DEFAULT_TIMEOUT = 30000;
	public static final int WIDGET_MAIN_LOBBY = 906;
	public static final int WIDGET_BUTTON_PLAY_GAME = 201;
	public static final int WIDGET_BUTTON_LOGOUT = 225;
	public static final int WIDGET_LABEL_CURRENT_WORLD = 11;
	public static final int WIDGET_WORLDS_TABLE = 64;
	public static final int WIDGET_WORLDS_TABLE_SCROLLBAR = 86;
	public static final int WIDGET_WORLDS_ROWS = 77;
	public static final int WIDGET_WORLDS_COLUMN_FAVOURITE = 68;
	public static final int WIDGET_WORLDS_COLUMN_WORLD_NUMBER = 69;
	public static final int WIDGET_WORLDS_COLUMN_MEMBERS = 70;
	public static final int WIDGET_WORLDS_COLUMN_PLAYERS = 71;
	public static final int WIDGET_WORLDS_COLUMN_ACTIVITY = 72;
	public static final int WIDGET_WORLDS_COLUMN_LOOT_SHARE = 75;
	public static final int WIDGET_WORLDS_COLUMN_PING = 76;

	public Lobby(final MethodContext factory) {
		super(factory);
	}

	/**
	 * Determines if the lobby is open.
	 *
	 * @return <tt>true</tt> if the lobby is open; otherwise <tt>false</tt>
	 */
	public boolean isOpen() {
		return ctx.game.getClientState() == STATE_LOBBY_IDLE;
	}

	/**
	 * Logs out by clicking the X button in the upper-right corner of the lobby widget.
	 *
	 * @return <tt>true</tt> if the logout button was clicked; otherwise <tt>false</tt>.
	 */
	public boolean close() {
		if (!isOpen() || !closeDialog()) {
			return false;
		}
		final Component child = ctx.widgets.get(WIDGET_MAIN_LOBBY, WIDGET_BUTTON_LOGOUT);
		return child != null && child.isValid() && child.click(true);
	}

	/**
	 * Enters the game with default timeout.
	 *
	 * @return <tt>true</tt> if the account is logged in; otherwise <tt>false</tt>
	 * @see #enterGame(org.powerbot.script.methods.Lobby.World, int)
	 */
	public boolean enterGame() {
		return enterGame(LOGIN_DEFAULT_TIMEOUT);
	}

	/**
	 * Enters the game with provided timeout.
	 *
	 * @param timeout the timeout (in milliseconds)
	 * @return <tt>true</tt> if the account is logged in; otherwise <tt>false</tt>
	 * @see #enterGame(org.powerbot.script.methods.Lobby.World, int)
	 */
	public boolean enterGame(final int timeout) {
		return enterGame(null, timeout);
	}

	/**
	 * Enters the game in the specified world.
	 *
	 * @param world the world to enter
	 * @return <tt>true</tt> if the account is logged in; otherwise <tt>false</tt>
	 * @see #enterGame(org.powerbot.script.methods.Lobby.World, int)
	 */
	public boolean enterGame(final World world) {
		return enterGame(world, LOGIN_DEFAULT_TIMEOUT);
	}

	/**
	 * Attempts to login to the game from the lobby. It will close any open dialogs prior to logging in. This is
	 * a blocking method; it will wait until the account is logged in, or the timeout is reached, before the
	 * method exits.
	 * <p/>
	 * If the login fails, the {@link Dialog} will still be open when the method finishes as it allows the
	 * developer to diagnose the reason for login failure.
	 *
	 * @param world   The world to select before logging in. Can be <tt>null</tt> if no world selection is wanted
	 * @param timeout The amount of time (in milliseconds) to wait for the account to login. If the timeout is
	 *                reached, the method will exit regardless the the current login state
	 * @return <tt>true</tt> if the account is logged in; otherwise <tt>false</tt>
	 */
	public boolean enterGame(final World world, final int timeout) {
		if (ctx.game.getClientState() == STATE_LOBBY_IDLE) {
			if (!closeDialog() || (getCurrentTab() == Tab.OPTIONS && !openTab(Tab.PLAYER_INFO))) {
				return false;
			}
			final World selected = (world != null) ? getSelectedWorld() : null;
			if (selected != null && !selected.equals(world) && !world.click()) {
				return false;
			}
			final Component child = ctx.widgets.get(WIDGET_MAIN_LOBBY, WIDGET_BUTTON_PLAY_GAME);
			if (!(child != null && child.isValid() && child.click(true))) {
				return false;
			}
			for (int i = 0; i < 30 && ctx.game.getClientState() == STATE_LOBBY_IDLE; i++) {
				sleep(50, 100);
			}
			if (ctx.game.getClientState() == STATE_LOBBY_IDLE) {
				return false;
			}
		}
		final Timer t = new Timer(timeout);
		while (t.isRunning() && !ctx.game.isLoggedIn()) {
			final Dialog dialog = getOpenDialog();
			if (dialog == Dialog.TRANSFER_COUNTDOWN || (dialog != null && continueDialog())) {
				t.reset();
			} else if (dialog != null) {
				sleep(500, 1000);
				break;
			}
			sleep(5);
		}
		return ctx.game.isLoggedIn();
	}

	/**
	 * Gets the currently selected world on the World Select panel. If the panel cannot be isValid, the method
	 * will open the World Select tab in order to isValid it.
	 *
	 * @return he currently selected world, or <tt>null</tt> if unable to retrieve world.
	 */
	public World getSelectedWorld() {
		if (!isOpen() || !closeDialog() || (!ctx.widgets.get(Tab.WORLD_SELECT.getIndex()).isValid() && !openTab(Tab.WORLD_SELECT))) {
			return null;
		}
		final Widget panel = ctx.widgets.get(Tab.WORLD_SELECT.getIndex());
		final String text = panel.isValid() ? panel.getComponent(WIDGET_LABEL_CURRENT_WORLD).getText() : null;
		if (text != null) {
			final Matcher m = Pattern.compile("^World\\s(\\d*)$").matcher(text);
			if (m.find()) {
				return getWorld(Integer.parseInt(m.group(1)));
			}
		}
		return null;
	}

	/**
	 * Returns the {@link World} for the provided number.
	 *
	 * @param worldNumber the number of the world
	 * @return the {@link World} of the number
	 */
	public World getWorld(final int worldNumber) {
		final World[] worlds = getWorlds(new Filter<World>() {
			@Override
			public boolean accept(final World world) {
				return world.getNumber() == worldNumber;
			}
		});
		return worlds.length == 1 ? worlds[0] : null;
	}

	/**
	 * Returns all available worlds.
	 *
	 * @return the array of {@link World}s
	 */
	public World[] getWorlds() {
		return getWorlds(new Filter<World>() {
			@Override
			public boolean accept(final World world) {
				return true;
			}
		});
	}

	/**
	 * Returns all available filtered worlds.
	 *
	 * @param filter the filter to open
	 * @return the array of {@link World}s
	 */
	public World[] getWorlds(final Filter<World> filter) {
		if (!isOpen() || !closeDialog()) {
			return new World[0];
		}
		final Widget panel = ctx.widgets.get(Tab.WORLD_SELECT.getIndex());
		if (!panel.isValid() && !openTab(Tab.WORLD_SELECT)) {
			return new World[0];
		}
		final ArrayList<World> worlds = new ArrayList<World>();
		final Component[] rows = panel.getComponent(WIDGET_WORLDS_ROWS).getChildren();
		for (final Component row : rows) {
			final World world = new World(row.getIndex());
			if (filter.accept(world)) {
				worlds.add(world);
			}
		}
		return worlds.toArray(new World[worlds.size()]);
	}

	/**
	 * Returns the open dialog.
	 *
	 * @return the open dialog, or {@code null} if one is not open
	 */
	public Dialog getOpenDialog() {
		for (final Dialog d : Dialog.values()) {
			final Component child = ctx.widgets.get(WIDGET_MAIN_LOBBY, d.getTextIndex());
			if (child != null && child.isOnScreen()) {
				final String text = child.getText();
				if (text != null && text.toLowerCase().contains(d.getText())) {
					return d;
				}
			}
		}
		return null;
	}

	private boolean closeDialog() {
		final Dialog dialog = getOpenDialog();
		if (dialog == null) {
			return true;
		}
		if (!dialog.hasBack()) {
			return false;
		}
		final Component child = ctx.widgets.get(WIDGET_MAIN_LOBBY, dialog.getBackIndex());
		return child != null && child.isOnScreen() && child.click(true);
	}

	private boolean continueDialog() {
		final Dialog dialog = getOpenDialog();
		if (dialog == null || !dialog.hasContinue()) {
			return false;
		}
		final Component child = ctx.widgets.get(WIDGET_MAIN_LOBBY, dialog.getContinueIndex());
		return child != null && child.isOnScreen() && child.click(true);
	}

	public Tab getCurrentTab() {
		for (final Tab tab : Tab.values()) {
			if (ctx.widgets.get(WIDGET_MAIN_LOBBY, tab.getComponent()).getTextureId() == 4671) {
				return tab;
			}
		}
		return null;
	}

	public boolean openTab(final Tab tab) {
		if (getCurrentTab() == tab) {
			return true;
		}
		final Component child = ctx.widgets.get(WIDGET_MAIN_LOBBY, tab.getComponent());
		return child.click() && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return getCurrentTab() == tab;
			}
		}, 100, 20);
	}

	/**
	 * Representation of the lobby tabs.
	 */
	public static enum Tab {
		PLAYER_INFO(234, 907), WORLD_SELECT(32, 910), FRIENDS(31, 909),
		FRIENDS_CHAT(247, 589), CLAN_CHAT(30, 912), OPTIONS(29, 911);
		private final int widgetTabIndex;
		private final int widgetPanelIndex;

		private Tab(final int widgetTabIndex, final int widgetPanelIndex) {
			this.widgetTabIndex = widgetTabIndex;
			this.widgetPanelIndex = widgetPanelIndex;
		}

		public int getIndex() {
			return widgetPanelIndex;
		}

		public int getComponent() {
			return widgetTabIndex;
		}
	}

	/**
	 * Representation of the lobby dialogs.
	 */
	public static enum Dialog {
		TRANSFER_COUNTDOWN(255, -1, 253, "You have only just left another world."),
		ACCOUNT_IN_USE(260, -1, 253, "Your account has not logged out from its last session."),
		LOGIN_LIMIT_EXCEEDED(260, -1, 253, "Login limit exceeded: too many connections from your address."),
		MEMBERS_ONLY_WORLD(260, -1, 253, "You need a member's account to log in to this world."),
		INSUFFICIENT_SKILL_TOTAL(260, -1, 253, "You must have a total skill level of"),
		//ACCOUNT_BANNED(-1, -1, -1, null), //TODO
		WILDERNESS_WARNING(117, 119, 113, "Warning: This is a High-risk Wilderness world."),
		VALIDATE_EMAIL(379, 379, 355, "Validate your email now for increased account security"),
		STANDING_IN_MEMBERS(260, -1, 253, "You are standing in a members-only"),
		SERVER_UPDATED(261, -1, 253, "The server is being updated."),
		ERROR_CONNECTING(259, -1, 253, "Error connecting to server"),
		CHOOSE_ANOTHER_WORLD(259, -1, 253, "choose another"),
		SESSION_EXPIRED(259, -1, 253, "session has now ended");
		private final int backButtonIndex;
		private final int continueButtonIndex;
		private final int textIndex;
		private final String text;

		private Dialog(final int backButtonIndex, final int continueButtonIndex, final int textIndex, final String textPattern) {
			this.backButtonIndex = backButtonIndex;
			this.continueButtonIndex = continueButtonIndex;
			this.textIndex = textIndex;
			this.text = textPattern.toLowerCase();
		}

		public int getBackIndex() {
			return backButtonIndex;
		}

		public int getContinueIndex() {
			return continueButtonIndex;
		}

		public int getTextIndex() {
			return textIndex;
		}

		public String getText() {
			return text;
		}

		public boolean hasContinue() {
			return continueButtonIndex != -1;
		}

		public boolean hasBack() {
			return backButtonIndex != -1;
		}
	}

	private int getWorldIndex(final int worldNumber) {
		final Widget panel = ctx.widgets.get(Tab.WORLD_SELECT.getIndex());
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

	public class World {
		private final int number;
		private final boolean members;
		private final String activity;
		private final boolean lootShare;
		private int players;
		private int ping;
		private boolean favorite;

		private World(final int widgetIndex) {
			final Widget panel = ctx.widgets.get(Tab.WORLD_SELECT.getIndex());
			this.number = Integer.parseInt(panel.getComponent(WIDGET_WORLDS_COLUMN_WORLD_NUMBER).getChild(widgetIndex).getText());
			this.members = panel.getComponent(WIDGET_WORLDS_COLUMN_MEMBERS).getChild(widgetIndex).getTextureId() == 1531;
			this.activity = panel.getComponent(WIDGET_WORLDS_COLUMN_ACTIVITY).getChild(widgetIndex).getText();
			this.lootShare = panel.getComponent(WIDGET_WORLDS_COLUMN_LOOT_SHARE).getChild(widgetIndex).getTextureId() == 699;
			this.players = getPlayers();
			this.ping = getPing();
			this.favorite = isFavorite();
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
			final int index = getWorldIndex(number);
			if (index != -1) {
				final Widget panel = ctx.widgets.get(Tab.WORLD_SELECT.getIndex());
				try {
					players = Integer.parseInt(panel.getComponent(WIDGET_WORLDS_COLUMN_PLAYERS).getChild(index).getText());
				} catch (final NumberFormatException ex) {
					players = -1;
				}
			}
			return players;
		}

		public int getPing() {
			final int index = getWorldIndex(number);
			if (index != -1) {
				final Widget panel = ctx.widgets.get(Tab.WORLD_SELECT.getIndex());
				try {
					ping = Integer.parseInt(panel.getComponent(WIDGET_WORLDS_COLUMN_PING).getChild(index).getText());
				} catch (final NumberFormatException ex) {
					ping = 999;
				}
			}
			return ping;
		}

		public boolean isFavorite() {
			final int index = getWorldIndex(number);
			if (index != -1) {
				final Widget panel = ctx.widgets.get(Tab.WORLD_SELECT.getIndex());
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
			if (!isOpen() || (Tab.WORLD_SELECT != getCurrentTab() && !openTab(Tab.WORLD_SELECT))) {
				return false;
			}
			final World selected = getSelectedWorld();
			if (selected != null && selected.equals(this)) {
				return true;
			}
			final int index = getWorldIndex(number);
			if (index == -1) {
				return false;
			}
			final Widget panel = ctx.widgets.get(Tab.WORLD_SELECT.getIndex());
			final Component table = panel.getComponent(WIDGET_WORLDS_TABLE);
			final Component row = panel.getComponent(WIDGET_WORLDS_ROWS).getChild(index);
			if (table != null && table.isValid() && row != null && row.isValid()) {
				final Rectangle visibleBounds = new Rectangle(
						table.getAbsoluteLocation(),
						new Dimension(table.getWidth(), table.getHeight() - row.getHeight())
				);
				if (!visibleBounds.contains(row.getAbsoluteLocation())) {
					final Component scrollBar = panel.getComponent(WIDGET_WORLDS_TABLE_SCROLLBAR);
					if (scrollBar == null || !ctx.widgets.scroll(row, scrollBar, false)) {
						return false;
					}
				}
				return row.click(true);
			}
			return false;
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof World && ((World) o).number == this.number;
		}

		@Override
		public String toString() {
			return World.class.getSimpleName() + "[number=" + number + ",members=" + members + ",players=" + players + ",ping=" + ping + ",favorite=" + favorite + ",activity=" + activity + ",lootshare=" + lootShare + "]";
		}
	}
}