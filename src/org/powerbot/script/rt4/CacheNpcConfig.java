package org.powerbot.script.rt4;

import org.powerbot.bot.cache.Block;
import org.powerbot.bot.cache.CacheWorker;
import org.powerbot.bot.cache.JagexStream;

public class CacheNpcConfig {
	private final JagexStream stream;

	public final int index;

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

	public CacheNpcConfig(final Block.Sector sector, final int index) {
		this.index = index;
		stream = new JagexStream(sector.getPayload());
		read();
	}

	static CacheNpcConfig load(final CacheWorker worker, final int id) {
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
			if (opcode == 1) {
				final int len = stream.getUByte();
				this.modelIds = new int[len];
				for (int index = 0; index < len; ++index) {
					this.modelIds[index] = stream.getUShort();
				}
			} else if (opcode == 2) {
				this.name = stream.getString();
			} else if (opcode == 12) {
				this.size = stream.getUByte();
			} else if (13 == opcode) {
				this.v = stream.getUShort();
			} else if (14 == opcode) {
				this.n = stream.getUShort();
			} else if (opcode == 15) {
				this.m = stream.getUShort();
			} else if (opcode == 16) {
				this.h = stream.getUShort();
			} else if (17 == opcode) {
				this.n = stream.getUShort();
				this.x = stream.getUShort();
				this.o = stream.getUShort();
				this.r = stream.getUShort();
			} else if (opcode >= 30 && opcode < 35) {
				this.actions[opcode - 30] = stream.getString();
				if (this.actions[opcode - 30].equalsIgnoreCase("Hidden")) {
					this.actions[opcode - 30] = null;
				}
			} else if (40 == opcode) {
				int len = stream.getUByte();
				this.colors1 = new short[len];
				this.colors2 = new short[len];

				for (int index = 0; index < len; ++index) {
					this.colors1[index] = (short) stream.getUShort();
					this.colors2[index] = (short) stream.getUShort();
				}
			} else if (41 == opcode) {
				int len = stream.getUByte();
				this.q = new short[len];
				this.g = new short[len];

				for (int index = 0; index < len; ++index) {
					this.q[index] = (short) stream.getUShort();
					this.g[index] = (short) stream.getUShort();
				}
			} else if (opcode == 60) {
				int len = stream.getUByte();
				this.d = new int[len];

				for (int index = 0; index < len; ++index) {
					this.d[index] = stream.getUShort();
				}
			} else if (opcode == 93) {
				this.visible = false;
			} else if (opcode == 95) {
				this.level = stream.getUShort();
			} else if (97 == opcode) {
				this.z = stream.getUShort();
			} else if (98 == opcode) {
				this.b = stream.getUShort();
			} else if (99 == opcode) {
				this.a = true;
			} else if (100 == opcode) {
				this.ag = stream.getUByte();
			} else if (opcode == 101) {
				this.am = stream.getUByte();
			} else if (102 == opcode) {
				this.aa = stream.getUShort();
			} else if (103 == opcode) {
				this.az = stream.getUShort();
			} else if (106 == opcode) {
				this.stageOperation = stream.getUShort();
				if (this.stageOperation * -1330184273 == '\uffff') {
					this.stageOperation = -1821357903;
				}

				this.stageIndex = stream.getUShort();
				if (this.stageIndex * -848078707 == '\uffff') {
					this.stageIndex = -1835674181;
				}

				int count = stream.getUByte();
				this.materialPointers = new int[count + 1];

				for (int index = 0; index <= count; ++index) {
					this.materialPointers[index] = stream.getUShort();
					if ('\uffff' == this.materialPointers[index]) {
						this.materialPointers[index] = -1;
					}
				}
			} else if (opcode == 107) {
				this.clickable = false;
			} else if (opcode == 109) {
				this.au = false;
			} else if (opcode == 111) {
				this.ao = true;
			} else if (opcode == 112) {
				this.af = stream.getUByte();
			}
		}
	}
}