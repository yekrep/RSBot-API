package org.powerbot.script.rt6;

import java.util.LinkedHashMap;
import java.util.Map;

import org.powerbot.bot.cache.Block;
import org.powerbot.bot.cache.AbstractCacheWorker;
import org.powerbot.bot.cache.JagexStream;
import org.powerbot.bot.rt6.Bot;
import org.powerbot.script.Validatable;

/**
 * CacheItemConfig
 */
public class CacheItemConfig implements Validatable {
	private static final int ADRENALINE_PARAM = 4332, ADRENALINE_TEMPLATE_PARAM = 4338;
	private static final int[] EQUIPPED_ACTIONS_PARAMS = {528, 529, 530, 531, 1211};
	private static final int[] BANK_ACTIONS_PARAMS = {1264, 1265};
	public final int index;
	private final AbstractCacheWorker worker;
	private final JagexStream stream;
	public String name = "";
	public boolean cosmetic, noted, lent;
	public boolean tradeable;
	public boolean stackable;
	public boolean members;
	public boolean specialAttack;
	public boolean twoHand;
	public int team = -1;
	public int value = -1;
	public int slot = -1;
	public int secondSlot = -1;
	public int modelId = -1;
	public int modelZoom = 0;
	public int modelOffsetX = 0;
	public int modelOffsetY = 0;
	public int modelRotationX = 0;
	public int modelRotationY = 0;
	public int modelRotationZ = 0;
	public int[] originalColors = new int[0];
	public int[] modifiedColors = new int[0];
	public int cosmeticTemplateId = -1;
	public int cosmeticId = -1;
	public int lentTemplateId = -1;
	public int lentId = -1;
	public int certTemplateId = -1;
	public int certId = -1;
	public int stackId = -1;
	public int stackAmount = -1;
	public int maleEquip1 = -1;
	public int maleEquip2 = -1;
	public int maleWearX = -1;
	public int maleWearY = -1;
	public int maleWearZ = -1;
	public int femaleEquip1 = -1;
	public int femaleEquip2 = -1;
	public int femaleWearX = -1;
	public int femaleWearY = -1;
	public int femaleWearZ = -1;
	public int adrenaline = -1;
	public int adrenalineTemplateId = -1;
	public String[] actions = {null, null, null, null, "Drop"};
	public String[] groundActions = {null, null, "Take", null, null};
	public String[] equippedActions = new String[0];
	public String[] bankActions = new String[0];
	public final Map<Integer, Object> params = new LinkedHashMap<Integer, Object>();

	private CacheItemConfig(final AbstractCacheWorker worker, final Block.Sector sector, final int index) {
		this.index = index;
		this.worker = worker;
		this.stream = new JagexStream(sector.getPayload());

		read();
		loadParams();
		inherit(this);
	}

	private CacheItemConfig() {
		this.index = -1;
		this.worker = null;
		this.stream = null;
	}

	/**
	 * Loads an item from the cache.
	 *
	 * @param id the id
	 * @return the value
	 * @deprecated use {@code CacheItemConfig.load(ctx.bot().getCacheWorker(), id)}
	 */
	@Deprecated
	public static CacheItemConfig load(final int id) {
		return load(Bot.CACHE_WORKER, id);
	}

	public static CacheItemConfig load(final AbstractCacheWorker worker, final int id) {
		final Block b = worker.getBlock(19, id >>> 8);
		if (b == null) {
			return new CacheItemConfig();
		}
		final Block.Sector s = b.getSector(id & 0xff);
		if (s == null) {
			return new CacheItemConfig();
		}
		return new CacheItemConfig(worker, s, id);
	}

	private void read() {
		int opcode;
		while ((opcode = stream.getUByte()) != 0) {
			if (opcode == 1) {
				modelId = stream.getBigSmart();
			} else if (opcode == 2) {
				name = stream.getString();
			} else if (opcode == 4) {
				modelZoom = stream.getUShort();
			} else if (opcode == 5) {
				modelRotationX = stream.getUShort();
			} else if (opcode == 6) {
				modelRotationY = stream.getUShort();
			} else if (opcode == 7) {
				modelOffsetX = stream.getUShort();
				if (modelOffsetX > 32767) {
					modelOffsetX = 65536;
				}
			} else if (opcode == 8) {
				modelOffsetY = stream.getUShort();
				if (modelOffsetY > 32767) {
					modelOffsetY -= 65536;
				}
			} else if (opcode == 11) {
				stackable = true;
			} else if (opcode == 12) {
				value = stream.getInt();
			} else if (opcode == 13) {
				slot = stream.getUByte();
			} else if (opcode == 14) {
				this.secondSlot = stream.getUByte();
				if (this.secondSlot == 5) {
					this.twoHand = true;
				}
			} else if (opcode == 16) {
				members = true;
			} else if (opcode == 18) {
				final int s = stream.getUShort();
			} else if (opcode == 23) {
				maleEquip1 = stream.getBigSmart();
			} else if (opcode == 24) {
				femaleEquip1 = stream.getBigSmart();
			} else if (opcode == 25) {
				maleEquip2 = stream.getBigSmart();
			} else if (opcode == 26) {
				femaleEquip2 = stream.getBigSmart();
			} else if (opcode == 27) {
				int b = stream.getUByte();
			} else if (opcode >= 30 && opcode < 35) {
				this.groundActions[opcode - 30] = stream.getString();
			} else if (opcode >= 35 && opcode < 40) {
				this.actions[opcode - 35] = stream.getString();
			} else if (opcode == 40) {
				final int size = stream.getUByte();
				originalColors = new int[size];
				modifiedColors = new int[size];
				for (int u = 0; u < size; u++) {
					originalColors[u] = stream.getUShort();
					modifiedColors[u] = stream.getUShort();
				}
			} else if (opcode == 41) {
				final int size = stream.getUByte();
				final int[] arr1 = new int[size];
				final int[] arr2 = new int[size];
				for (int o = 0; o < size; o++) {
					arr1[o] = stream.getShort();
					arr2[o] = stream.getShort();
				}
			} else if (opcode == 42) {
				final int g = stream.getUByte();
				final int[] arr = new int[g];
				for (int x = 0; x < g; x++) {
					arr[x] = stream.getByte();
				}
			} else if (opcode == 43) {
				final int value1 = stream.getInt();
				final boolean value2 = true;
			} else if (opcode == 44) {
				final int s = stream.getUShort();
			} else if (opcode == 45) {
				final int s = stream.getUShort();
			} else if (opcode == 65) {
				tradeable = true;
			} else if (opcode == 78) {
				final int i = stream.getBigSmart();
			} else if (opcode == 79) {
				final int i = stream.getBigSmart();
			} else if (opcode == 90) {
				final int i = stream.getBigSmart();
			} else if (opcode == 91) {
				final int i = stream.getBigSmart();
			} else if (opcode == 92) {
				final int i = stream.getBigSmart();
			} else if (opcode == 93) {
				final int i = stream.getBigSmart();
			} else if (opcode == 94) {
				final int s = stream.getUShort();
			} else if (opcode == 95) {
				modelRotationZ = stream.getUShort();
			} else if (opcode == 96) {
				final int b = stream.getUByte();
			} else if (opcode == 97) {
				certId = stream.getUShort();
			} else if (opcode == 98) {
				certTemplateId = stream.getUShort();
			} else if (opcode >= 100 && opcode < 110) {
				// array index k-100
				stackId = stream.getUShort();
				stackAmount = stream.getUShort();
			} else if (opcode >= 110 && opcode <= 112) {
				// array index k-110 default 128
				final float resize = stream.getUShort();
			} else if (opcode == 113) {
				final byte b = stream.getByte();
			} else if (opcode == 114) {
				final int b = stream.getByte() * 5;
			} else if (opcode == 115) {
				team = stream.getUByte();
			} else if (opcode == 121) {
				lentId = stream.getUShort();
			} else if (opcode == 122) {
				lentTemplateId = stream.getUShort();
			} else if (opcode == 125) {
				maleWearX = stream.getByte() << 2;
				maleWearY = stream.getByte() << 2;
				maleWearZ = stream.getByte() << 2;
			} else if (opcode == 126) {
				femaleWearX = stream.getByte() << 2;
				femaleWearY = stream.getByte() << 2;
				femaleWearZ = stream.getByte() << 2;
			} else if (opcode == 132) {
				final int size = stream.getUByte();
				final int[] arr = new int[size];
				for (int v = 0; v < size; v++) {
					arr[v] = stream.getUShort();
				}
			} else if (opcode == 134) {
				final int b = stream.getUByte();
			} else if (opcode == 139) {
				cosmeticId = stream.getUShort();
			} else if (opcode == 140) {
				cosmeticTemplateId = stream.getUShort();
			} else if (opcode >= 142 && opcode < 147) {
				// array index k-142 default -1
				final int value = stream.getUShort();
			} else if (opcode >= 150 && opcode < 155) {
				// array index k-150 default -1
				final int value = stream.getUShort();
			} else if (opcode == 156) {
				final boolean b = false;
			} else if (opcode == 157) {
				final boolean b = true;
			} else if (opcode == 161) {
				final int s = stream.getUShort();
			} else if (opcode == 162) {
				final int s = stream.getUShort();
			} else if (opcode == 163) {
				final int s = stream.getUShort();
			} else if (opcode == 164) {
				final String s = stream.getString();
			} else if (opcode == 165) {
				final int i = 2;
			} else if (opcode == 249) {
				final int h = stream.getUByte();
				for (int m = 0; m < h; m++) {
					final boolean r = stream.getUByte() == 1;
					final int key = stream.getUInt24();
					final Object value = r ? stream.getString() : stream.getInt();
					params.put(key, value);
				}
			}
		}
	}

	private void inherit(final CacheItemConfig item) {
		if (item.lentTemplateId != -1 && item.lentId != -1) {
			inheritLent(item);
		}
		if (item.certTemplateId != -1) {
			inheritCert(item);
		}
		if (item.cosmeticTemplateId != -1) {
			inheritCosmetic(item);
		}
		if (item.adrenalineTemplateId != -1) {
			inheritAdrenaline(item);
		}
	}

	private void delegate(final CacheItemConfig item, final int sourceId) {
		final CacheItemConfig source = load(sourceId);
		if (!source.valid()) {
			return;
		}
		item.groundActions = source.groundActions;
		item.actions = source.actions;
		item.name = source.name;
		item.members = source.members;
		item.value = 0;
		item.team = source.team;
		item.actions[4] = "Discard";
		item.twoHand = source.twoHand;
		item.secondSlot = source.secondSlot;
	}

	private void inheritLent(final CacheItemConfig item) {
		delegate(item, item.lentId);
		item.lent = true;
	}

	private void inheritCert(final CacheItemConfig item) {
		final CacheItemConfig note = load(item.certId);
		if (!note.valid()) {
			return;
		}
		item.value = note.value;
		item.name = note.name;
		item.stackable = true;
		item.members = note.members;
		item.noted = true;
	}

	private void inheritCosmetic(final CacheItemConfig item) {
		delegate(item, item.cosmeticId);
		item.cosmetic = true;
	}

	private void inheritAdrenaline(final CacheItemConfig item) {
		final CacheItemConfig source = load(item.certId);
		if (!source.valid()) {
			return;
		}
		item.specialAttack = true;
		item.adrenaline = source.adrenaline;
	}

	private void loadParams() {
		loadSpecialAttack();
		loadEquippedActions();
		loadBankActions();
	}

	private void loadEquippedActions() {
		int count = 0, idx = 0;
		for (final int id : EQUIPPED_ACTIONS_PARAMS) {
			if (params.containsKey(id)) {
				count++;
			}
		}
		if (count == 0) {
			return;
		}
		equippedActions = new String[count];
		for (final int id : EQUIPPED_ACTIONS_PARAMS) {
			final String action = (String) params.get(id);
			if (action != null) {
				equippedActions[idx++] = action;
			}
		}
	}

	private void loadBankActions() {
		int count = 0, idx = 0;
		for (final int id : BANK_ACTIONS_PARAMS) {
			if (params.containsKey(id)) {
				count++;
			}
		}
		if (count == 0) {
			return;
		}
		bankActions = new String[count];
		for (final int id : BANK_ACTIONS_PARAMS) {
			final String action = (String) params.get(id);
			if (action != null) {
				bankActions[idx++] = action;
			}
		}
	}

	private void loadSpecialAttack() {
		if (params.containsKey(ADRENALINE_PARAM)) {
			this.specialAttack = true;
			this.adrenaline = (Integer) params.get(ADRENALINE_PARAM);
		} else if (params.containsKey(ADRENALINE_TEMPLATE_PARAM)) {
			this.adrenalineTemplateId = (Integer) params.get(ADRENALINE_TEMPLATE_PARAM);
		}
	}

	@Override
	public boolean valid() {
		return index > -1 && worker != null && stream != null;
	}
}
