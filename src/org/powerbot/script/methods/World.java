package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.script.methods.tabs.Equipment;
import org.powerbot.script.methods.tabs.Inventory;
import org.powerbot.script.methods.tabs.Prayer;
import org.powerbot.script.methods.tabs.Skills;
import org.powerbot.script.methods.tabs.Summoning;
import org.powerbot.script.methods.widgets.ActionBar;
import org.powerbot.script.methods.widgets.Bank;
import org.powerbot.script.methods.widgets.DepositBox;
import org.powerbot.script.methods.widgets.Lobby;

public class World {
	private Client client;
	private final Game.Toolkit toolkit;
	private final Game.Viewport viewport;

	public final Camera camera;
	public final Components components;
	public final Game game;
	public final GroundItems groundItems;
	public final HintArrows hintArrows;
	public final Items items;
	public final Keyboard keyboard;
	public final Menu menu;
	public final Mouse mouse;
	public final Movement movement;
	public final Npcs npcs;
	public final Objects objects;
	public final Players players;
	public final Projectiles projectiles;
	public final Settings settings;
	public final Widgets widgets;

	public final Equipment equipment;
	public final Inventory inventory;
	public final Prayer prayer;
	public final Skills skills;
	public final Summoning summoning;

	public final ActionBar actionBar;
	public final Bank bank;
	public final DepositBox depositBox;
	public final Lobby lobby;

	public World() {
		this.client = null;
		this.toolkit = new Game.Toolkit();
		this.viewport = new Game.Viewport();

		this.camera = new Camera(this);
		this.components = new Components(this);
		this.game = new Game(this);
		this.groundItems = new GroundItems(this);
		this.hintArrows = new HintArrows(this);
		this.items = new Items(this);
		this.keyboard = new Keyboard(this);
		this.menu = new Menu(this);
		this.mouse = new Mouse(this);
		this.movement = new Movement(this);
		this.npcs = new Npcs(this);
		this.objects = new Objects(this);
		this.players = new Players(this);
		this.projectiles = new Projectiles(this);
		this.settings = new Settings(this);
		this.widgets = new Widgets(this);

		this.equipment = new Equipment(this);
		this.inventory = new Inventory(this);
		this.prayer = new Prayer(this);
		this.skills = new Skills(this);
		this.summoning = new Summoning(this);

		this.actionBar = new ActionBar(this);
		this.bank = new Bank(this);
		this.depositBox = new DepositBox(this);
		this.lobby = new Lobby(this);
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public Client getClient() {
		return this.client;
	}

	public Game.Toolkit getToolkit() {
		return this.toolkit;
	}

	public Game.Viewport getViewport() {
		return this.viewport;
	}
}
