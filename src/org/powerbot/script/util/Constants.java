package org.powerbot.script.util;

import java.util.HashMap;
import java.util.Map;

import org.powerbot.script.wrappers.Tile;

/**
 * @author Paris
 */
public class Constants {
	private static final Map<String, Object> data = new HashMap<>();

	static {
		data.put("backpack.widget", 1473);
		data.put("backpack.component.scrollbar", 6);
		data.put("backpack.component.view", 7);
		data.put("backpack.component.container", 8);
		data.put("backpack.widget.bank", 762 << 16 | 54);
		data.put("backpack.widget.depositbox", 11 << 16 | 15);
		data.put("backpack.widget.gear", 1474 << 16 | 13);

		data.put("bank.npc.ids", new int[]{
				44, 45, 166, 494, 495, 496, 497, 498, 499, 553, 909, 953, 958, 1036, 1360, 1702, 2163, 2164, 2354, 2355,
				2568, 2569, 2570, 2617, 2618, 2619, 2718, 2759, 3046, 3198, 3199, 3293, 3416, 3418, 3824, 4456, 4457,
				4458, 4459, 4519, 4907, 5257, 5258, 5259, 5260, 5488, 5776, 5777, 5901, 6200, 6362, 7049, 7050, 7605,
				8948, 9710, 13932, 14707, 14923, 14924, 14925, 15194, 16603, 16602
		});
		data.put("bank.booth.ids", new int[]{
				782, 2213, 3045, 5276, 6084, 10517, 11338, 11758, 12759, 12798, 12799, 14369, 14370,
				16700, 19230, 20325, 20326, 20327, 20328, 22819, 24914, 25808, 26972, 29085, 34752, 35647,
				36262, 36786, 37474, 49018, 49019, 52397, 52589, 76274, 69024, 69023, 69022,
				83954
		});
		data.put("bank.counter.ids", new int[]{
				42217, 42377, 42378, 2012, 66665, 66666, 66667
		});
		data.put("bank.chest.ids", new int[]{
				2693, 4483, 8981, 12308, 14382, 20607, 21301, 27663, 42192, 57437, 62691, 83634, 81756
		});
		data.put("bank.unreachable.tiles", new Tile[]{
				new Tile(3191, 3445, 0), new Tile(3180, 3433, 0)
		});
		data.put("bank.widget", 762);
		data.put("bank.component.button.close", 50);
		data.put("bank.component.container.items", 39);
		data.put("bank.component.button.withdraw.mode", 8);
		data.put("bank.component.button.deposit.inventory", 11);
		data.put("bank.component.button.deposit.money", 13);
		data.put("bank.component.button.deposit.equipment", 15);
		data.put("bank.component.button.deposit.familiar", 17);
		data.put("bank.component.scroll.bar", 40);
		data.put("bank.setting.bank.state", 110);
		data.put("bank.setting.withdraw.mode", 160);

		data.put("chat.widget", 1188);
		data.put("chat.component.chat.options", new int[]{
				11, 19, 24, 29, 34
		});
		data.put("chat.widget.continue", new int[][]{{1189, 11}, {1184, 13}, {1186, 6}, {1191, 12}});

		data.put("combatbar.widget", 1430);
		data.put("combatbar.setting.adrenaline", 679);
		data.put("combatbar.component.button.heal", 2);
		data.put("combatbar.setting.retaliation", 462);
		data.put("combatbar.component.button.retaliate", 6);
		data.put("combatbar.component.button.prayer", 4);
		data.put("combatbar.component.button.summoning", 5);
		data.put("combatbar.component.health", 82);
		data.put("combatbar.component.adrenaline", 92);
		data.put("combatbar.component.prayer", 88);
		data.put("combatbar.component.summoning", 94);
		data.put("combatbar.component.text", 7);
		data.put("combatbar.component.bounds", 72);
		data.put("combatbar.num.slots", 12);
		data.put("combatbar.component.bar", 77);
		data.put("combatbar.component.lock", 19);
		data.put("combatbar.component.trash", 20);
		data.put("combatbar.widget.layout", 1477);
		data.put("combatbar.component.button.toggle", 7);
		data.put("combatbar.component.button.toggle.idx", 1);
		data.put("combatbar.setting.item", 811);
		data.put("combatbar.setting.ability", 727);
		data.put("combatbar.component.slot.action", 97);
		data.put("combatbar.component.slot.cool.down", 98);
		data.put("combatbar.component.slot.bind", 100);

		data.put("depositbox.box.ids", new int[]{
				2045, 2133, 6396, 6402, 6404, 6417, 6418, 6453, 6457, 6478, 6836, 9398, 15985, 20228, 24995, 25937, 26969,
				32924, 32930, 32931, 34755, 36788, 39830, 45079, 66668, 70512, 73268, 79036
		});
		data.put("depositbox.widget", 11);
		data.put("depositbox.component.button.close", 14);
		data.put("depositbox.component.container.items", 15);
		data.put("depositbox.component.button.deposit.inventory", 17);
		data.put("depositbox.component.button.deposit.equipment", 21);
		data.put("depositbox.component.button.deposit.familiar", 23);
		data.put("depositbox.component.button.deposit.pouch", 19);

		data.put("equipment.widget", 1464);
		data.put("equipment.component.container", 28);
		data.put("equipment.widget.gear", 1462);
		data.put("equipment.component.gear.container", 13);
		data.put("equipment.num.slots", 13);

		data.put("game.index.login.screen", 3);
		data.put("game.index.lobby.screen", 7);
		data.put("game.index.logging.in", 9);
		data.put("game.index.map.loaded", 11);
		data.put("game.index.map.loading", 12);

		data.put("hud.widget.hud", 1477);
		data.put("hud.widget.menu", 1431);
		data.put("hud.widget.menu.bounds", 32);
		data.put("hud.widget.menu.windows", 1432);
		data.put("hud.component.menu.windows.list", 4);

		data.put("lobby.state.lobby.idle", 7);
		data.put("lobby.state.logging.in", 9);
		data.put("lobby.login.default.timeout", 30000);
		data.put("lobby.widget.main.lobby", 906);
		data.put("lobby.widget.button.play.game", 202);
		data.put("lobby.widget.button.logout", 224);
		data.put("lobby.widget.label.current.world", 11);
		data.put("lobby.widget.worlds.table", 62);
		data.put("lobby.widget.worlds.table.scrollbar", 86);
		data.put("lobby.widget.worlds.rows", 77);
		data.put("lobby.widget.worlds.column.favourite", 68);
		data.put("lobby.widget.worlds.column.world.number", 69);
		data.put("lobby.widget.worlds.column.members", 70);
		data.put("lobby.widget.worlds.column.players", 71);
		data.put("lobby.widget.worlds.column.activity", 72);
		data.put("lobby.widget.worlds.column.loot.share", 75);
		data.put("lobby.widget.worlds.column.ping", 76);

		data.put("movement.widget.map", 1465);
		data.put("movement.component.map", 12);
		data.put("movement.component.run", 4);
		data.put("movement.component.run.energy", 5);
		data.put("movement.setting.run.enabled", 463);

		data.put("powers.setting.prayer.points", 3274);
		data.put("powers.setting.prayer.book", 3277);
		data.put("powers.setting.prayers", 3272);
		data.put("powers.setting.curses", 3275);
		data.put("powers.setting.prayers.quick", 1770);
		data.put("powers.setting.prayers.selection", 1769);
		data.put("powers.setting.curses.quick", 1768);
		data.put("powers.book.prayers", 0);
		data.put("powers.book.curses", 1);
		data.put("powers.widget.prayer", 1458);
		data.put("powers.component.prayer.container", 24);
		data.put("powers.component.prayer.select.container", 25);
		data.put("powers.component.prayer.select.confirm", 4);
		data.put("powers.component.quick.selection", 32);

		data.put("skills.xp.table", new int[]{0, 0, 83, 174, 276, 388, 512, 650, 801, 969, 1154, 1358, 1584, 1833, 2107,
				2411, 2746, 3115, 3523, 3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031, 13363, 14833,
				16456, 18247, 20224, 22406, 24815, 27473, 30408, 33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983,
				75127, 83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636, 184040, 203254, 224466, 247886, 273742,
				302288, 333804, 368599, 407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445, 899257, 992895,
				1096278, 1210421, 1336443, 1475581, 1629200, 1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594,
				3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253, 7195629, 7944614, 8771558, 9684577, 10692629,
				11805606, 13034431, 14391160, 15889109, 17542976, 19368992, 21385073, 23611006, 26068632, 28782069,
				31777943, 35085654, 38737661, 42769801, 47221641, 52136869, 57563718, 63555443, 70170840, 77474828,
				85539082, 94442737, 104273167});
		data.put("skills.attack", 0);
		data.put("skills.defense", 1);
		data.put("skills.strength", 2);
		data.put("skills.constitution", 3);
		data.put("skills.range", 4);
		data.put("skills.prayer", 5);
		data.put("skills.magic", 6);
		data.put("skills.cooking", 7);
		data.put("skills.woodcutting", 8);
		data.put("skills.fletching", 9);
		data.put("skills.fishing", 10);
		data.put("skills.firemaking", 11);
		data.put("skills.crafting", 12);
		data.put("skills.smithing", 13);
		data.put("skills.mining", 14);
		data.put("skills.herblore", 15);
		data.put("skills.agility", 16);
		data.put("skills.thieving", 17);
		data.put("skills.slayer", 18);
		data.put("skills.farming", 19);
		data.put("skills.runecrafting", 20);
		data.put("skills.hunter", 21);
		data.put("skills.construction", 22);
		data.put("skills.summoning", 23);
		data.put("skills.dungeoneering", 24);
		data.put("skills.divination", 25);

		data.put("summoning.widget", 662);
		data.put("summoning.component.name", 54);
		data.put("summoning.component.take.bob", 68);
		data.put("summoning.component.renew", 70);
		data.put("summoning.component.call", 50);
		data.put("summoning.component.dismiss", 52);
		data.put("summoning.setting.npc.id", 1784);
		data.put("summoning.setting.time.left", 1786);
		data.put("summoning.setting.special.points", 1787);
		data.put("summoning.setting.left.option", 1789);
		data.put("summoning.setting.left.selected", 1790);
		data.put("summoning.setting.pouch.id", 1831);
		data.put("summoning.widget.left.select", 880);
		data.put("summoning.component.confirm", 6);
	}

	public static int getInt(final String k) {
		return getObj(k, Integer.class);
	}

	public static int[] getIntA(final String k) {
		return getObj(k, int[].class);
	}

	public static String getStr(final String k) {
		return getObj(k, String.class);
	}

	public static String[] getStrA(final String k) {
		return getObj(k, String[].class);
	}

	public static <T> T getObj(final String k, final Class<T> type) {
		return type.cast(data.get(k));
	}
}
