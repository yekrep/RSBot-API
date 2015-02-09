package org.powerbot.script.rt4;

import org.powerbot.bot.cache.Block;
import org.powerbot.bot.cache.CacheWorker;
import org.powerbot.bot.cache.JagexStream;

class CacheObjectConfig {
	private final CacheWorker worker;
	private final Block.Sector sector;
	private final JagexStream stream;

	public final int index;

	public String name = "null";
	public String[] actions = new String[5];
	public int xSize = 1;
	public int[] materialPointers;
	public int ySize = 1;
	public int[] meshId;
	public int[] meshType;
	public boolean swapYZ = false;
	public int yScale = 128;
	public int xScale = 128;
	public int zScale = 128;
	public int xTranslate = 0;
	public int yTranslate = 0;
	public int stageOperationId = -1;
	public int stageIndex = -1;
	public int zTranslate = 0;

	public CacheObjectConfig(final CacheWorker worker, final Block.Sector sector, final int index) {
		this.index = index;
		this.worker = worker;
		this.sector = sector;
		stream = new JagexStream(sector.getPayload());

		read();
	}

	static CacheObjectConfig load(final CacheWorker worker, final int id) {
		final Block b = worker.getBlock(16, id >>> 8);
		if (b == null) {
			return null;
		}
		final Block.Sector s = b.getSector(id & 0xff);
		if (s == null) {
			return null;
		}
		return new CacheObjectConfig(worker, s, id);
	}

	private void read() {
		int opcode;
		while ((opcode = stream.getUByte()) != 0) {
			if (opcode == 1) {
				final int len = stream.getUByte();
				if (len <= 0) {
					continue;
				}
				if (meshId != null) {
					stream.seek(stream.getLocation() + len * 3);
					continue;
				}

				meshId = new int[len];
				meshType = new int[len];
				for (int i = 0; i < len; i++) {
					meshId[i] = stream.getUShort();
					meshType[i] = stream.getUByte();
				}
			} else if (opcode == 2) {
				name = stream.getString();
			} else if (opcode == 5) {
				final int len = stream.getUByte();
				if (len <= 0) {
					continue;
				}
				if (meshId != null) {
					stream.seek(stream.getLocation() + len * 2);
					continue;
				}
				meshId = new int[len];
				meshType = null;
				for (int i = 0; i < len; i++) {
					meshId[i] = stream.getUShort();
				}
			} else if (opcode == 14) {
				xSize = stream.getUByte();
			} else if (opcode == 15) {
				ySize = stream.getUByte();
			} else if (opcode == 17 || opcode == 18) {
			} else if (opcode == 19) {
				stream.getByte();
			} else if (opcode == 21 || opcode == 22 || opcode == 23) {
			} else if (opcode == 24) {
				stream.getShort();
			} else if (opcode == 27) {
			} else if (opcode == 28 || opcode == 29 || opcode == 39) {
				stream.getByte();
			} else if (opcode >= 30 && opcode < 35) {
				actions[opcode - 30] = stream.getString();
				if (actions[opcode - 30].equalsIgnoreCase("Hidden")) {
					actions[opcode - 30] = null;
				}
			} else if (opcode == 40 || opcode == 41) {
				final int len = stream.getUByte();
				final short[] arr1 = new short[len], arr2 = new short[len];
				for (int i = 0; i < len; i++) {
					arr1[i] = (short) stream.getUShort();
					arr2[i] = (short) stream.getUShort();
				}
			} else if (opcode == 60) {
				stream.getUShort();
			} else if (opcode == 62) {
				swapYZ = true;
			} else if (opcode == 64) {
			} else if (opcode == 65) {
				xScale = stream.getUShort();
			} else if (opcode == 66) {
				yScale = stream.getUShort();
			} else if (opcode == 67) {
				zScale = stream.getUShort();
			} else if (opcode == 68) {
				stream.getUShort();
			} else if (opcode == 69) {
				stream.getUByte();
			} else if (opcode == 70) {
				xTranslate = stream.readSmartB();
			} else if (opcode == 71) {
				yTranslate = stream.readSmartB();
			} else if (opcode == 72) {
				zTranslate = stream.readSmartB();
			} else if (opcode == 73 || opcode == 74) {
			} else if (opcode == 75) {
				stream.getUByte();
			} else if (opcode == 77) {
				stageOperationId = stream.getShort() & 0xFFFF;
				if (65535 == stageOperationId) {
					stageOperationId = -1;
				}
				stageIndex = stream.getShort() & 0xFFFF;
				if (65535 == stageIndex) {
					stageIndex = -1;
				}
				final int len = stream.getUByte();
				materialPointers = new int[1 + len];
				for (int i = 0; i <= len; i++) {
					materialPointers[i] = stream.getShort() & 0xFFFF;
					if (65535 != materialPointers[i]) {
						continue;
					}
					materialPointers[i] = -1;
				}
			} else if (opcode == 78) {
				stream.getUShort();
				stream.getUByte();
			} else if (opcode == 79) {
				stream.getUShort();
				stream.getUShort();
				stream.getUByte();
				final int len = stream.getUByte();
				for (int i = 0; i < len; i++) {
					stream.getUShort();
				}
			} else if (opcode == 81) {
				stream.getUByte();
			}
		}
	}
}
