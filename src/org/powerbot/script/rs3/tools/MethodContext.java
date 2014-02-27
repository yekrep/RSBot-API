package org.powerbot.script.rs3.tools;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.bot.rs3.Bot;
import org.powerbot.bot.rs3.client.Client;
import org.powerbot.bot.rs3.client.Constants;
import org.powerbot.script.internal.InputHandler;
import org.powerbot.script.internal.methods.Items;
import org.powerbot.script.internal.methods.Map;

public class MethodContext {
	private final AtomicReference<Client> client;
	private final AtomicReference<Bot> bot;
	public final AtomicReference<InputHandler> inputHandler;
	public final AtomicReference<Constants> constants;

	/**
	 * <p>A set of properties for the environment.</p>
	 * <p/>
	 * <table border="1" cellpadding="2">
	 * <tr>
	 * <th>Key</th>
	 * <th>Meaning</th>
	 * </tr>
	 * <tr>
	 * <td>{@code "login.world"}</td>
	 * <td>The preferred world to log into.</td>
	 * </tr>
	 * </table>
	 */
	public final Properties properties;

	public final Antipatterns antipatterns;
	public final CombatBar combatBar;
	public final Bank bank;
	public final Camera camera;
	public final Chat chat;
	public final DepositBox depositBox;
	public final Environment environment;
	public final Equipment equipment;
	public final Game game;
	public final GroundItems groundItems;
	public final HintArrows hintArrows;
	public final Hud hud;
	public final Backpack backpack;
	public final Keyboard keyboard;
	public final Lobby lobby;
	public final Menu menu;
	public final Mouse mouse;
	public final Movement movement;
	public final Npcs npcs;
	public final Objects objects;
	public final Players players;
	public final Powers powers;
	public final Projectiles projectiles;
	public final Settings settings;
	public final Skills skills;
	public final Summoning summoning;
	public final Widgets widgets;

	final Items items;
	final Map map;

	private MethodContext(final Bot bot) {
		client = new AtomicReference<Client>(null);
		this.bot = new AtomicReference<Bot>(bot);
		inputHandler = new AtomicReference<InputHandler>(null);
		constants = new AtomicReference<Constants>(null);

		properties = new Properties();
		antipatterns = new Antipatterns(this);
		combatBar = new CombatBar(this);
		backpack = new Backpack(this);
		bank = new Bank(this);
		camera = new Camera(this);
		chat = new Chat(this);
		depositBox = new DepositBox(this);
		environment = new Environment(this);
		equipment = new Equipment(this);
		game = new Game(this);
		groundItems = new GroundItems(this);
		hintArrows = new HintArrows(this);
		hud = new Hud(this);
		keyboard = new Keyboard(this);
		lobby = new Lobby(this);
		menu = new Menu(this);
		mouse = new Mouse(this);
		movement = new Movement(this);
		npcs = new Npcs(this);
		objects = new Objects(this);
		players = new Players(this);
		powers = new Powers(this);
		projectiles = new Projectiles(this);
		settings = new Settings(this);
		skills = new Skills(this);
		summoning = new Summoning(this);
		widgets = new Widgets(this);

		items = new Items(this);
		map = new Map(this);
	}

	public static MethodContext newContext(final Bot bot) {
		return new MethodContext(bot);
	}

	public MethodContext(final MethodContext ctx) {
		client = ctx.client;
		bot = ctx.bot;
		inputHandler = ctx.inputHandler;
		constants = ctx.constants;

		properties = ctx.properties;
		antipatterns = ctx.antipatterns;
		combatBar = ctx.combatBar;
		backpack = ctx.backpack;
		bank = ctx.bank;
		camera = ctx.camera;
		chat = ctx.chat;
		depositBox = ctx.depositBox;
		environment = ctx.environment;
		equipment = ctx.equipment;
		game = ctx.game;
		groundItems = ctx.groundItems;
		hintArrows = ctx.hintArrows;
		hud = ctx.hud;
		keyboard = ctx.keyboard;
		lobby = ctx.lobby;
		menu = ctx.menu;
		mouse = ctx.mouse;
		movement = ctx.movement;
		npcs = ctx.npcs;
		objects = ctx.objects;
		players = ctx.players;
		powers = ctx.powers;
		projectiles = ctx.projectiles;
		settings = ctx.settings;
		skills = ctx.skills;
		summoning = ctx.summoning;
		widgets = ctx.widgets;

		items = ctx.items;
		map = ctx.map;
	}

	/**
	 * @deprecated see {@link #MethodContext(MethodContext)}
	 */
	@Deprecated
	public void init(final MethodContext ctx) {
		bot.set(ctx.getBot());
		setClient(ctx.getClient());
	}

	public void setClient(final Client client) {
		this.client.set(client);
	}

	public Client getClient() {
		return client.get();
	}

	public Bot getBot() {
		return bot.get();
	}

	/**
	 * Gets the preferred world.
	 *
	 * @return the requested preferred world if set, otherwise {@code -1}
	 * @deprecated see {@link #properties}
	 */
	@Deprecated
	public int getPreferredWorld() {
		int w = -1;
		if (properties.containsKey("login.world")) {
			try {
				w = Integer.parseInt(properties.getProperty("login.world"));
			} catch (final NumberFormatException ignored) {
			}
		}
		return w;
	}

	/**
	 * Sets the preferred world.
	 *
	 * @param world the preferred world to log into
	 * @deprecated see {@link #properties}
	 */
	@Deprecated
	void setPreferredWorld(final int world) {
		properties.setProperty("login.world", Integer.toString(world));
	}
}
