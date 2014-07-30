package org.powerbot.script.rt6;

import org.powerbot.script.Tile;

/**
 * Global constants for client access.
 */
public class Constants {
	public static final int GAME_INDEX_LOGIN_SCREEN = 3;
	public static final int GAME_INDEX_LOBBY_SCREEN = 7;
	public static final int GAME_INDEX_LOGGING_IN = 9;
	public static final int GAME_INDEX_MAP_LOADED = 11;
	public static final int GAME_INDEX_MAP_LOADING = 12;

	public static final int BACKPACK_WIDGET = 1473;
	public static final int BACKPACK_COMPONENT_SCROLL_BAR = 30;
	public static final int BACKPACK_COMPONENT_VIEW = 31;
	public static final int BACKPACK_COMPONENT_CONTAINER = 34;
	public static final int BACKPACK_WIDGET_BANK = 762 << 16 | 7;
	public static final int BACKPACK_WIDGET_DEPOSIT_BOX = 11 << 16 | 1;
	public static final int BACKPACK_WIDGET_GEAR = 1474 << 16 | 13;

	public static final int[] BANK_NPC_IDS = {
			44, 45, 166, 494, 495, 496, 497, 498, 499, 553, 909, 953, 958, 1036, 1360, 1702, 2163, 2164, 2354, 2355,
			2568, 2569, 2570, 2617, 2618, 2619, 2718, 2759, 3046, 3198, 3199, 3293, 3416, 3418, 3824, 4456, 4457,
			4458, 4459, 4519, 4907, 5257, 5258, 5259, 5260, 5488, 5776, 5777, 5901, 6200, 6362, 7049, 7050, 7605,
			8948, 9710, 13932, 14707, 14923, 14924, 14925, 15194, 16603, 16602, 19086
	};
	public static final int[] BANK_BOOTH_IDS = {
			782, 2213, 3045, 5276, 6084, 10517, 11338, 11758, 12759, 12798, 12799, 14369, 14370,
			16700, 19230, 20325, 20326, 20327, 20328, 22819, 24914, 25808, 26972, 29085, 34752, 35647,
			36262, 36786, 37474, 49018, 49019, 52397, 52589, 76274, 69024, 69023, 69022, 25688
	};
	public static final int[] BANK_COUNTER_IDS = {
			42217, 42377, 42378, 2012, 66665, 66666, 66667
	};
	public static final int[] BANK_CHEST_IDS = {
			2693, 4483, 8981, 12308, 14382, 20607, 21301, 27663, 42192, 57437, 62691, 83634, 81756, 79036, 83954
	};
	public static final Tile[] BANK_UNREACHABLE_TILES = new Tile[]{
			new Tile(3191, 3445, 0), new Tile(3180, 3433, 0)
	};
	public static final int BANK_WIDGET = 762;
	public static final int BANK_COMPONENT_BUTTON_CLOSE = 303;
	public static final int BANK_COMPONENT_CONTAINER_ITEMS = 215;
	public static final int BANK_COMPONENT_PRESET_SETUP = 39;
	public static final int BANK_COMPONENT_LOADOUT_1 = 47;
	public static final int BANK_COMPONENT_LOADOUT_2 = 55;
	public static final int BANK_COMPONENT_BUTTON_WITHDRAW_MODE = 56;
	public static final int BANK_COMPONENT_BUTTON_DEPOSIT_INVENTORY = 85;
	public static final int BANK_COMPONENT_BUTTON_DEPOSIT_MONEY = 109;
	public static final int BANK_COMPONENT_BUTTON_DEPOSIT_EQUIPMENT = 93;
	public static final int BANK_COMPONENT_BUTTON_DEPOSIT_FAMILIAR = 101;
	public static final int BANK_COMPONENT_SCROLL_BAR = 210;
	public static final int BANK_SETTING_BANK_STATE = 110;
	public static final int BANK_SETTING_WITHDRAW_MODE = 160;
	public static final int[] BANK_BACKPACK_ALTERNATIVE_WIDGETS = {
			BACKPACK_WIDGET_BANK,
			BACKPACK_WIDGET_DEPOSIT_BOX,
			BACKPACK_WIDGET_GEAR,
	};

	public static final int CHAT_WIDGET = 1188;
	public static final int[] CHAT_COMPONENT_OPTIONS = {
			12, 18, 23, 28, 33
	};
	public static final int[][] CHAT_WIDGET_CONTINUE = {{1189, 11}, {1184, 11}, {1186, 6}, {1191, 11}};

	public static final int COMBATBAR_WIDGET = 1430;
	public static final int COMBATBAR_SETTING_ADRENALINE = 679;
	public static final int COMBATBAR_COMPONENT_BUTTON_HEAL = 3;
	public static final int COMBATBAR_SETTING_RETALIATION = 462;
	public static final int COMBATBAR_COMPONENT_BUTTON_RETALIATE = 45;
	public static final int COMBATBAR_COMPONENT_BUTTON_PRAYER = 8;
	public static final int COMBATBAR_COMPONENT_BUTTON_SUMMONING = 14;
	public static final int COMBATBAR_COMPONENT_HEALTH = 4;
	public static final int COMBATBAR_COMPONENT_ADRENALINE = 28;
	public static final int COMBATBAR_COMPONENT_PRAYER = 24;
	public static final int COMBATBAR_COMPONENT_SUMMONING = 30;
	public static final int COMBATBAR_COMPONENT_TEXT = 7;
	public static final int COMBATBAR_COMPONENT_BOUNDS = 0;
	public static final int COMBATBAR_NUM_SLOTS = 12;
	public static final int COMBATBAR_COMPONENT_BAR = 49;
	public static final int COMBATBAR_COMPONENT_LOCK = 246;
	public static final int COMBATBAR_WIDGET_LAYOUT = 1477;
	public static final int COMBATBAR_SETTING_ITEM = 811;
	public static final int COMBATBAR_SETTING_ABILITY = 727;
	public static final int COMBATBAR_COMPONENT_SLOT_ACTION = 54;
	public static final int COMBATBAR_COMPONENT_SLOT_COOL_DOWN = 55;
	public static final int COMBATBAR_COMPONENT_SLOT_BIND = 57;
	public static final int COMBATBAR_COMPONENT_SLOT_LENGTH = 13;

	public static final int FLOATINGMESSAGE_TEXTURE_INFO = 8515;
	public static final int FLOATINGMESSAGE_TEXTURE_WARNING = 8524;

	public static final int EQUIPMENT_WIDGET = 1464;
	public static final int EQUIPMENT_COMPONENT_CONTAINER = 15;
	public static final int EQUIPMENT_WIDGET_GEAR = 1462;
	public static final int EQUIPMENT_COMPONENT_GEAR_CONTAINER = 14;

	public static final int[] DEPOSITBOX_IDS = new int[]{
			2045, 2133, 6396, 6402, 6404, 6417, 6418, 6453, 6457, 6478, 6836, 9398, 15985, 20228, 24995, 25937, 26969,
			32924, 32930, 32931, 34755, 36788, 39830, 45079, 66668, 70512, 73268, 79036
	};
	public static final int DEPOSITBOX_WIDGET = 11;
	public static final int DEPOSITBOX_COMPONENT_BUTTON_CLOSE = 41;
	public static final int DEPOSITBOX_COMPONENT_CONTAINER_ITEMS = 1;
	public static final int DEPOSITBOX_COMPONENT_BUTTON_DEPOSIT_INVENTORY = 13;
	public static final int DEPOSITBOX_COMPONENT_BUTTON_DEPOSIT_EQUIPMENT = 21;
	public static final int DEPOSITBOX_COMPONENT_BUTTON_DEPOSIT_FAMILIAR = 29;
	public static final int DEPOSITBOX_COMPONENT_BUTTON_DEPOSIT_POUCH = 37;

	public static final int HUD_WIDGET = 1477;
	public static final int HUD_WIDGET_MENU = 1431;
	public static final int HUD_WIDGET_MENU_BOUNDS = 28;
	public static final int HUD_WIDGET_MENU_WINDOWS = 1432;
	public static final int HUD_COMPONENT_MENU_WINDOWS_LIST = 4;

	public static final int LOBBY_STATE_IDLE = 7;
	public static final int LOBBY_STATE_LOGGING_IN = 9;
	public static final int LOBBY_LOGIN_DEFAULT_TIMEOUT = 30000;
	public static final int LOBBY_WIDGET_MAIN_LOBBY = 906;
	public static final int LOBBY_WIDGET_BUTTON_PLAY_GAME = 152;
	public static final int LOBBY_WIDGET_BUTTON_LOGOUT = 226;
	public static final int LOBBY_WIDGET_LABEL_CURRENT_WORLD = 509;
	public static final int LOBBY_WIDGET_WORLDS_TABLE = 64;
	public static final int LOBBY_WIDGET_WORLDS_TABLE_SCROLLBAR = 92;
	public static final int LOBBY_WIDGET_WORLDS_ROWS = 77;
	public static final int LOBBY_WIDGET_WORLDS_COLUMN_FAVOURITE = 74;
	public static final int LOBBY_WIDGET_WORLDS_COLUMN_WORLD_NUMBER = 75;
	public static final int LOBBY_WIDGET_WORLDS_COLUMN_MEMBERS = 70;
	public static final int LOBBY_WIDGET_WORLDS_COLUMN_PLAYERS = 77;
	public static final int LOBBY_WIDGET_WORLDS_COLUMN_ACTIVITY = 72;
	public static final int LOBBY_WIDGET_WORLDS_COLUMN_LOOT_SHARE = 75;
	public static final int LOBBY_WIDGET_WORLDS_COLUMN_PING = 76;

	public static final int MOVEMENT_WIDGET_MAP = 1465;
	public static final int MOVEMENT_COMPONENT_MAP = 4;
	public static final int MOVEMENT_COMPONENT_RUN = 19;
	public static final int MOVEMENT_COMPONENT_RUN_ENERGY = 20;
	public static final int MOVEMENT_SETTING_RUN_ENABLED = 463;

	public static final int POWERS_SETTING_PRAYER_POINTS = 3274;
	public static final int POWERS_SETTING_PRAYER_BOOK = 3277;
	public static final int POWERS_SETTING_PRAYERS = 3272;
	public static final int POWERS_SETTING_CURSES = 3275;
	public static final int POWERS_SETTING_PRAYERS_QUICK = 1770;
	public static final int POWERS_SETTING_PRAYERS_SELECTION = 1769;
	public static final int POWERS_SETTING_CURSES_QUICK = 1768;
	public static final int POWERS_BOOK_PRAYERS = 20;
	public static final int POWERS_BOOK_CURSES = 21;
	public static final int POWERS_WIDGET_PRAYER = 1458;
	public static final int POWERS_COMPONENT_PRAYER_CONTAINER = 31;
	public static final int POWERS_COMPONENT_PRAYER_SELECT_CONTAINER = 32;
	public static final int POWERS_COMPONENT_PRAYER_SELECT_CONFIRM = 4;
	public static final int POWERS_COMPONENT_QUICK_SELECTION = 37;

	public static final int SUMMONING_WIDGET = 662;
	public static final int SUMMONING_COMPONENT_NAME = 54;
	public static final int SUMMONING_COMPONENT_TAKE_BOB = 68;
	public static final int SUMMONING_COMPONENT_RENEW = 70;
	public static final int SUMMONING_COMPONENT_CALL = 50;
	public static final int SUMMONING_COMPONENT_DISMISS = 52;
	public static final int SUMMONING_SETTING_NPC_ID = 1784;
	public static final int SUMMONING_SETTING_TIME_LEFT = 1786;
	public static final int SUMMONING_SETTING_SPECIAL_POINTS = 1787;
	public static final int SUMMONING_SETTING_LEFT_OPTION = 1789;
	public static final int SUMMONING_SETTING_LEFT_SELECTED = 1790;
	public static final int SUMMONING_SETTING_POUCH_ID = 1831;
	public static final int SUMMONING_WIDGET_LEFT_SELECT = 880;
	public static final int SUMMONING_COMPONENT_CONFIRM = 6;

	public static final int[] SKILLS_XP = {0, 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107,
			2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833,
			16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983,
			75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742,
			302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895,
			1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594,
			3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629,
			11805606, 13034431, 14391160, 15889109, 17542976, 19368992, 21385073, 23611006, 26068632, 28782069,
			31777943, 35085654, 38737661, 42769801, 47221641, 52136869, 57563718, 63555443, 70170840, 77474828,
			85539082, 94442737, 104273167};
	public static final int SKILLS_ATTACK = 0;
	public static final int SKILLS_DEFENSE = 1;
	public static final int SKILLS_STRENGTH = 2;
	public static final int SKILLS_CONSTITUTION = 3;
	public static final int SKILLS_RANGE = 4;
	public static final int SKILLS_PRAYER = 5;
	public static final int SKILLS_MAGIC = 6;
	public static final int SKILLS_COOKING = 7;
	public static final int SKILLS_WOODCUTTING = 8;
	public static final int SKILLS_FLETCHING = 9;
	public static final int SKILLS_FISHING = 10;
	public static final int SKILLS_FIREMAKING = 11;
	public static final int SKILLS_CRAFTING = 12;
	public static final int SKILLS_SMITHING = 13;
	public static final int SKILLS_MINING = 14;
	public static final int SKILLS_HERBLORE = 15;
	public static final int SKILLS_AGILITY = 16;
	public static final int SKILLS_THIEVING = 17;
	public static final int SKILLS_SLAYER = 18;
	public static final int SKILLS_FARMING = 19;
	public static final int SKILLS_RUNECRAFTING = 20;
	public static final int SKILLS_HUNTER = 21;
	public static final int SKILLS_CONSTRUCTION = 22;
	public static final int SKILLS_SUMMONING = 23;
	public static final int SKILLS_DUNGEONEERING = 24;
	public static final int SKILLS_DIVINATION = 25;

	public static final int BANKPIN_SETTING_PIN_STEP = 163;
	public static final int BANKPIN_WIDGET = 13;
	public static final int BANKPIN_COMPONENT = 0;
	public static final int BANKPIN_COMPONENT_PIN_OFFSET = 7;

	public static final int ITEMS_INDEX_INVENTORY = 93;
	public static final int ITEMS_INDEX_MONEY_POUCH = 623;
	public static final int ITEMS_INDEX_EQUIPMENT = 670;
	public static final int ITEMS_INDEX_BANK = 95;

	public static final int LOGIN_WIDGET = 596;
	public static final int LOGIN_WIDGET_LOGIN_ERROR = 167;
	public static final int LOGIN_WIDGET_LOGIN_TRY_AGAIN = 177;
	public static final int LOGIN_WIDGET_LOGIN_USERNAME_TEXT = 38;
	public static final int LOGIN_WIDGET_LOGIN_PASSWORD_TEXT = 63;
	public static final int LOGIN_WIDGET_LOGIN_BUTTON = 71;

	public static final int[] TICKETDESTROY_ITEM_IDS = {24154, 24155};

	public static final int[] WIDGETCLOSER_COMPONENTS = {//TODO: review all these components
			906 << 16 | 545,//transaction
			335 << 16 | 68,//trade window
			1422 << 16 | 18, //world map
			1253 << 16 | 176, // Squeal of Fortune window
			906 << 16 | 231, // validate email
			1139 << 16 | 12, // Extras window
			438 << 16 | 24,//recruit a friend
			622 << 16 | 21,//member loyalty
			204 << 16 | 3,//membership offer
			149 << 16 | 237,//pickaxe
			1252 << 16 | 6, // Squeal of Fortune notification
			1223 << 16 | 18,//Achievement continue button
			1048 << 16 | 21, // key tokens
			669 << 16 | 1,//hints [A]
	};
	public static final int[] WIDGETCLOSER_COMPONENTS_ACTIVE = {
			669 << 16 | 1,//hints
	};
	public static final int[] WIDGETCLOSER_COMPONENTS_DIE = {
			906 << 16 | 476, // change email
	};
}
