package org.powerbot.script.methods;

import org.powerbot.client.Client;
import org.powerbot.script.internal.methods.Items;

import java.util.concurrent.atomic.AtomicReference;

public class ClientFactory {
	private AtomicReference<Client> client;
	public int preferredWorld;

	public Camera camera;
	Components components;
	public Environment environment;
	public Game game;
	public GroundItems groundItems;
	public HintArrows hintArrows;
	public Keyboard keyboard;
	public Menu menu;
	public Mouse mouse;
	public Movement movement;
	public Npcs npcs;
	public Objects objects;
	public Players players;
	public Projectiles projectiles;
	public Settings settings;
	public Widgets widgets;

	public Items items;

	public Equipment equipment;
	public Inventory inventory;
	public Prayer prayer;
	public Skills skills;
	public Summoning summoning;

	public ActionBar actionBar;
	public Bank bank;
	public DepositBox depositBox;
	public Lobby lobby;

	public ClientFactory() {
		this.client = new AtomicReference<>(null);
		this.preferredWorld = -1;

		camera = new Camera(this);
		components = new Components(this);
		environment = new Environment(this);
		game = new Game(this);
		groundItems = new GroundItems(this);
		hintArrows = new HintArrows(this);
		keyboard = new Keyboard(this);
		menu = new Menu(this);
		mouse = new Mouse(this);
		movement = new Movement(this);
		npcs = new Npcs(this);
		objects = new Objects(this);
		players = new Players(this);
		projectiles = new Projectiles(this);
		settings = new Settings(this);
		widgets = new Widgets(this);

		items = new Items(this);

		equipment = new Equipment(this);
		inventory = new Inventory(this);
		prayer = new Prayer(this);
		skills = new Skills(this);
		summoning = new Summoning(this);

		actionBar = new ActionBar(this);
		bank = new Bank(this);
		depositBox = new DepositBox(this);
		lobby = new Lobby(this);
	}

	public void setClient(Client client) {
		this.client.set(client);
	}

	public Client getClient() {
		return this.client.get();
	}
}
