package org.powerbot.script.rt6;

import java.util.HashMap;
import java.util.Map;

import org.powerbot.bot.cache.Block;
import org.powerbot.bot.cache.AbstractCacheWorker;
import org.powerbot.bot.cache.JagexStream;
import org.powerbot.bot.rt6.Bot;
import org.powerbot.bot.rt6.client.Cache;
import org.powerbot.script.Validatable;

/**
 * CacheNpcConfig
 */
public class CacheNpcConfig implements Validatable {
	public final int index;
	private final AbstractCacheWorker worker;
	private final JagexStream stream;
	public String name = "null";
	public String[] actions = new String[5];
	public int combatLevel = -1;
	public int headIcon = -1;
	public boolean clickable = true;
	public boolean visible = true;
	public int scriptId, configId;
	public int[] childrenIds = new int[0];
	public int[] modelIds = new int[0];
	public int[][] modelOffsets = new int[0][];
	public int[] originalColors = new int[0];
	public int[] modifiedColors = new int[0];
	public float[] resize = new float[]{128, 128, 128};
	public final Map<Integer, Object> params = new HashMap<Integer, Object>();

	private CacheNpcConfig(final AbstractCacheWorker worker, final Block.Sector sector, final int index) {
		this.index = index;
		this.worker = worker;
		this.stream = new JagexStream(sector.getPayload());

		read();
	}

	private CacheNpcConfig() {
		this.index = -1;
		this.worker = null;
		this.stream = null;
	}

	/**
	 * Loads an NPC from the cache.
	 *
	 * @param id the id
	 * @return the value
	 * @deprecated use {@code CacheNpcConfig.load(ctx.bot().getCacheWorker(), id)}
	 */
	@Deprecated
	public static CacheNpcConfig load(final int id) {
		return load(Bot.CACHE_WORKER, id);
	}

	public static CacheNpcConfig load(final AbstractCacheWorker worker, final int id) {
		final Block b = worker.getBlock(18, id >>> 7);
		if (b == null) {
			return new CacheNpcConfig();
		}
		final Block.Sector s = b.getSector(id & 0x7f);
		if (s == null) {
			return new CacheNpcConfig();
		}
		return new CacheNpcConfig(worker, s, id);
	}

	private void read() {
		int opcode;
		while ((opcode = stream.getUByte()) != 0) {
			switch (opcode) {
			case 1: {
				final int size = stream.getUByte();
				modelIds = new int[size];
				for (int i = 0; i < size; i++) {
					modelIds[i] = stream.getBigSmart();
				}
				break;
			}
			case 2: {
				this.name = stream.getString();
				break;
			}
			case 12: {
				stream.getUByte();
				break;
			}
			case 30:
			case 31:
			case 32:
			case 33:
			case 34: {
				this.actions[opcode - 30] = stream.getString();
				break;
			}
			case 40: {
				final int size = stream.getUByte();
				this.originalColors = new int[size];
				this.modifiedColors = new int[size];
				for (int E = 0; E < size; E++) {
					originalColors[E] = stream.getUShort();
					modifiedColors[E] = stream.getUShort();
				}
				break;
			}
			case 41: {
				final int size = stream.getUByte();
				final int[] arr1 = new int[size];
				final int[] arr2 = new int[size];
				for (int v = 0; v < size; v++) {
					arr1[v] = stream.getShort();
					arr2[v] = stream.getShort();
				}
				break;
			}
			case 42: {
				final int size = stream.getUByte();
				final int[] arr = new int[size];
				for (int F = 0; F < size; F++) {
					arr[F] = stream.getByte();
				}
				break;
			}
			case 44: {
				stream.getUShort();
				break;
			}
			case 45: {
				stream.getUShort();
				break;
			}
			case 60: {
				final int size = stream.getUByte();
				final int[] arr = new int[size];
				for (int i = 0; i < size; i++) {
					arr[i] = stream.getBigSmart();
				}
				break;
			}
			case 93: {
				this.visible = false;
				break;
			}
			case 95: {
				this.combatLevel = stream.getUShort();
				break;
			}
			case 97: {
				this.resize[0] = this.resize[2] = stream.getUShort();
				break;
			}
			case 98: {
				resize[1] = stream.getUShort();
				break;
			}
			case 99: {
				//this.a = true;
				break;
			}
			case 100: {
				stream.getByte();
				break;
			}
			case 101: {
				stream.getByte();
				break;
			}
			case 102: {
				final int G = stream.getUByte();
				int x = 0;
				int C = G;
				while (C != 0) {
					x++;
					C >>= 1;
				}
				final int[] arr1 = new int[x];
				final int[] arr2 = new int[x];
				for (int k = 0; k < x; k++) {
					if ((G & (1 << k)) == 0) {
						arr1[k] = -1;
						arr2[k] = -1;
					} else {
						arr1[k] = stream.getBigSmart();
						arr2[k] = stream.getSmartMinusOne();
					}
				}
				break;
			}
			case 103: {
				stream.getUShort();
				break;
			}
			case 106:
			case 118: {
				this.scriptId = stream.getUShort();
				if (this.scriptId == 65535) {
					this.scriptId = -1;
				}
				this.configId = stream.getUShort();
				if (this.configId == 65535) {
					this.configId = -1;
				}
				int defaultChildId = -1;
				if (opcode == 118) {
					defaultChildId = stream.getUShort();
					if (defaultChildId == 65535) {
						defaultChildId = -1;
					}
				}
				final int childCount = stream.getUByte();
				this.childrenIds = new int[childCount + 2];
				for (int o = 0; o <= childCount; o++) {
					this.childrenIds[o] = stream.getUShort();
					if (this.childrenIds[o] == 65535) {
						this.childrenIds[o] = -1;
					}
				}
				this.childrenIds[childCount + 1] = defaultChildId;
				break;
			}
			case 107: {
				this.clickable = false;
				break;
			}
			case 109: {
				//this.au = false;
				break;
			}
			case 111: {
				//this.ao = false;
				break;
			}
			case 113: {
				stream.getUShort();
				stream.getUShort();
				break;
			}
			case 114: {
				stream.getByte();
				stream.getByte();
				break;
			}
			case 119: {
				stream.getUByte();
				break;
			}
			case 121: {
				this.modelOffsets = new int[this.modelIds.length][3];
				final int t = stream.getUByte();
				for (int r = 0; r < t; r++) {
					final int u = stream.getUByte();
					this.modelOffsets[u][0] = stream.getByte();
					this.modelOffsets[u][1] = stream.getByte();
					this.modelOffsets[u][2] = stream.getByte();
				}
				break;
			}
			case 123: {
				stream.getUShort();
				break;
			}
			case 125: {
				stream.getByte();
				break;
			}
			case 127: {
				stream.getUShort();
				break;
			}
			case 128: {
				stream.getUByte();
				break;
			}
			case 134: {
				int _txc = stream.getUShort();
				if (_txc == 65535) {
					_txc = -1;
				}
				int _txe = stream.getUShort();
				if (_txe == 65535) {
					_txe = -1;
				}
				int _txf = stream.getUShort();
				if (_txf == 65535) {
					_txf = -1;
				}
				int _txh = stream.getUShort();
				if (_txh == 65535) {
					_txh = -1;
				}
				final int _txi = stream.getUByte();
				break;
			}
			case 137: {
				stream.getUShort();
				break;
			}
			case 138: {
				this.headIcon = stream.getBigSmart();
				break;
			}
			case 139: {
				stream.getBigSmart();
				break;
			}
			case 140: {
				stream.getUByte();
				break;
			}
			case 141: {
				//assigns unknown value to true
				break;
			}
			case 142: {
				stream.getUShort();
				break;
			}
			case 143: {
				//assigns unknown value to true
				break;
			}
			case 150:
			case 151:
			case 152:
			case 153:
			case 154: {
				this.actions[opcode - 150] = stream.getString();
				break;
			}
			case 155: {
				stream.getByte();
				stream.getByte();
				stream.getByte();
				stream.getByte();
				break;
			}
			case 158: {
				//assigns unknown value to 1
				break;
			}
			case 159: {
				//assigns unknown value to 0
				break;
			}
			case 160: {
				final int I = stream.getUByte();
				final int[] arr = new int[I];
				for (int D = 0; D < I; D++) {
					arr[D] = stream.getUShort();
				}
				break;
			}
			case 162: {
				//assigns unknown value to true
				break;
			}
			case 163: {
				stream.getUByte();
				break;
			}
			case 164: {
				stream.getUShort();
				stream.getUShort();
				break;
			}
			case 165: {
				stream.getUByte();
				break;
			}
			case 168: {
				stream.getUByte();
				break;
			}
			case 169: {
				//assigns unknown value to false
				break;
			}
			case 170:
			case 171:
			case 172:
			case 173:
			case 174:
			case 175: {
				stream.getUShort();
				break;
			}
			case 178: {
				//assigns unknown value to false
				break;
			}
			case 179: {
				stream.getSmart();
				stream.getSmart();
				stream.getSmart();
				stream.getSmart();
				stream.getSmart();
				stream.getSmart();
				break;
			}
			case 180: {
				stream.getUByte();
				break;
			}
			case 181: {
				stream.getUShort();
				stream.getUByte();
				break;
			}
			case 182: {
				//assigns unknown value to true
				break;
			}
			case 249: {
				final int h = stream.getUByte();
				for (int m = 0; m < h; m++) {
					final boolean r = stream.getUByte() == 1;
					final int key = stream.getUInt24();
					final Object value = r ? stream.getString() : stream.getInt();
					params.put(key, value);
				}
				break;
			}
			}
		}
	}

	@Override
	public boolean valid() {
		return index > -1 && worker != null && stream != null;
	}

	@Override
	public String toString() {
		return String.format("%s[index=%d,combatLevel=%d,visible=%s]", name, index, combatLevel, Boolean.toString(visible));
	}
}
