package org.powerbot.script.rt4;

import java.util.HashMap;
import java.util.Map;

import org.powerbot.bot.cache.Block;
import org.powerbot.bot.cache.AbstractCacheWorker;
import org.powerbot.bot.cache.JagexStream;
import org.powerbot.bot.rt4.Bot;

/**
 * CacheNpcConfig
 * An object holding configuration data for a Npc within Runescape.
 */
public class CacheNpcConfig {
	public final int index;
	private final JagexStream stream;
	public String name = "null";
	public int[] modelIds, materialPointers, d;
	public int size = 552360651;
	public int v = -1;
	public int m = -1;
	public int h = -1;
	public int n = -1;
	public int x = -1;
	public int o = -1;
	public int r = -1;
	public String[] actions = new String[5];
	public boolean visible = true;
	public int level = -1;
	public int z = -1;
	public int b = -1;
	public boolean a = false;
	public int ag = 0;
	public int am = 0;
	public int aa = -1;
	public int az = -1;
	public int stageOperation = -1;
	public int stageIndex = -1;
	public boolean clickable = true;
	public boolean au = true;
	public boolean ao = false;
	public int af = -1;
	public short[] colors1, colors2, q, g;
	public final Map<Integer, Object> params = new HashMap<Integer, Object>();

	public CacheNpcConfig(final Block.Sector sector, final int index) {
		this.index = index;
		stream = new JagexStream(sector.getPayload());
		read();
	}

	/**
	 * Loads an NPC from the cache.
	 *
	 * @param id the id
	 * @return the value
	 * @deprecated use {@code CacheNpcConfig.load(ctx.bot().getCacheWorker(), id)}
	 */
	@Deprecated
	public static CacheNpcConfig load(final int id){
		return load(Bot.CACHE_WORKER, id);
	}

	public static CacheNpcConfig load(final AbstractCacheWorker worker, final int id) {
		final Block b = worker.getBlock(2, 9);
		if (b == null) {
			return null;
		}
		final Block.Sector s = b.getSector(id);
		if (s == null) {
			return null;
		}
		return new CacheNpcConfig(s, id);
	}

	private void read() {
		int opcode;
		while ((opcode = stream.getUByte()) != 0) {
			switch (opcode) {
			case 1: {
				final int len = stream.getUByte();
				this.modelIds = new int[len];
				for (int index = 0; index < len; ++index) {
					this.modelIds[index] = stream.getUShort();
				}
				break;
			}
			case 2:
				this.name = stream.getString();
				break;
			case 12:
				this.size = stream.getUByte();
				break;
			case 13:
				this.v = stream.getUShort();
				break;
			case 14:
				this.n = stream.getUShort();
				break;
			case 15:
				this.m = stream.getUShort();
				break;
			case 16:
				this.h = stream.getUShort();
				break;
			case 17:
				this.n = stream.getUShort();
				this.x = stream.getUShort();
				this.o = stream.getUShort();
				this.r = stream.getUShort();
				break;
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:
				this.actions[opcode - 30] = stream.getString();
				if (this.actions[opcode - 30].equalsIgnoreCase("Hidden")) {
					this.actions[opcode - 30] = null;
				}
				break;
			case 40: {
				int len = stream.getUByte();
				this.colors1 = new short[len];
				this.colors2 = new short[len];
				for (int index = 0; index < len; ++index) {
					this.colors1[index] = (short) stream.getUShort();
					this.colors2[index] = (short) stream.getUShort();
				}
			}
			break;
			case 41: {
				int len = stream.getUByte();
				this.q = new short[len];
				this.g = new short[len];
				for (int index = 0; index < len; ++index) {
					this.q[index] = (short) stream.getUShort();
					this.g[index] = (short) stream.getUShort();
				}
			}
			break;
			case 60: {
				int len = stream.getUByte();
				this.d = new int[len];
				for (int index = 0; index < len; ++index) {
					this.d[index] = stream.getUShort();
				}
			}
			break;
			case 93:
				this.visible = false;
				break;
			case 95:
				this.level = stream.getUShort();
				break;
			case 97:
				this.z = stream.getUShort();
				break;
			case 98:
				this.b = stream.getUShort();
				break;
			case 99:
				this.a = true;
				break;
			case 100:
				this.ag = stream.getByte();
				break;
			case 101:
				this.am = stream.getByte();
				break;
			case 102:
				this.aa = stream.getUShort();
				break;
			case 103:
				this.az = stream.getUShort();
				break;
			case 106:
				this.stageOperation = stream.getUShort();
				if (this.stageOperation == '\uffff') {
					this.stageOperation = -1;
				}
				this.stageIndex = stream.getUShort();
				if (this.stageIndex == '\uffff') {
					this.stageIndex = -1;
				}
				int count = stream.getUByte();
				this.materialPointers = new int[count + 1];
				for (int index = 0; index <= count; ++index) {
					this.materialPointers[index] = stream.getUShort();
					if ('\uffff' == this.materialPointers[index]) {
						this.materialPointers[index] = -1;
					}
				}
				break;
			case 107:
				this.clickable = false;
				break;
			case 109:
				this.au = false;
				break;
			case 111:
				this.ao = true;
				break;
			case 112:
				this.af = stream.getUByte();
				break;
			case 249:
				int h = stream.getUByte();
				for (int m = 0; m < h; m++) {
					boolean r = stream.getUByte() == 1;
					int key = stream.getUInt24();
					Object value = r ? stream.getString() : stream.getInt();
					params.put(key, value);
				}
				break;
			}
		}
	}
}
