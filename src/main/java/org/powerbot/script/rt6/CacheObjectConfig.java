package org.powerbot.script.rt6;

import org.powerbot.bot.cache.*;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CacheObjectConfig
 */
class CacheObjectConfig {
	private final JagexStream stream;

	public byte[] meshType;
	public int[][] meshId;
	public String name;
	public int reachableState;
	public boolean isObjectBlocked;
	public int meshMergeType;
	public int[] alternateFileIndex;
	public boolean swapYZ;
	public boolean clippedRange;
	public int[] childrenFileIndex;
	public final String[] menuActions;
	public int xSize;
	public int ySize;
	public short xScale = 128;
	public short yScale = 128;
	public short zScale = 128;
	public int xTranslate;
	public int yTranslate;
	public int zTranslate;
	public int stageOperationId;
	public int stageIndex;
	public int meshMergeValue;
	public int zOffsetOverride;
	public int yOffsetOverride;
	public int xOffsetOverride;
	public float xStart;
	public float yStart;
	public float zStart;
	public float xStop;
	public float yStop;
	public float zStop;
	public short[] originalColors, modifiedColors;

	public final Map<Integer, Object> params = new LinkedHashMap<>();

	CacheObjectConfig(final Block.Sector sector, final int index) {
		stream = new JagexStream(sector.getPayload());

		xSize = 1;
		ySize = 1;
		reachableState = 2;
		isObjectBlocked = true;
		menuActions = new String[10];
		read();
	}

	public static CacheObjectConfig load(final AbstractCacheWorker worker, final int id) {
		final Block b = worker.getBlock(16, id >>> 8);
		if (b == null) {
			return null;
		}
		final Block.Sector s = b.getSector(id & 0xff);
		if (s == null) {
			return null;
		}
		return new CacheObjectConfig(s, id);
	}

	private void read() {
		int opcode;
		while ((opcode = stream.getUByte()) != 0) {
			if (opcode == 1) {
				final int j = stream.getUByte();
				this.meshType = new byte[j];
				this.meshId = new int[j][];
				for (int i1 = 0; i1 < j; i1++) {
					this.meshType[i1] = stream.getByte();
					final int i2 = stream.getUByte();
					this.meshId[i1] = new int[i2];
					for (int i3 = 0; i3 < i2; i3++) {
						this.meshId[i1][i3] = stream.getBigSmart();
					}
				}
			} else if (opcode == 2) {
				this.name = stream.getString();
			} else if (opcode == 14) {
				this.xSize = stream.getUByte();
			} else if (opcode == 15) {
				this.ySize = stream.getUByte();
			} else if (17 == opcode) {
				this.reachableState = 0;
				this.isObjectBlocked = false;
			} else if (18 == opcode) {
				this.isObjectBlocked = false;
			} else if (19 == opcode) {
				stream.getUByte();
			} else if (opcode == 21) {
				this.meshMergeType = 1;
			} else if (22 == opcode) {
				final boolean f = true;
			} else if (23 == opcode) {
				final int q = 1;
			} else if (24 == opcode) {
				final int j = stream.getBigSmart();
				if (-1 != j) {
					this.alternateFileIndex = new int[]{j};
				}
			} else if (opcode == 27) {
				this.reachableState = 1;
			} else if (opcode == 28) {
				final int an = stream.getUByte() << 2;
			} else if (29 == opcode) {
				final int ad = stream.getByte();
			} else if (opcode == 39) {
				final int ak = stream.getByte();
			} else if ((opcode >= 30) && (opcode < 35)) {
				this.menuActions[(opcode - 30)] = stream.getString();
			} else if (40 == opcode) {
				final int j = stream.getUByte();
				originalColors = new short[j];
				modifiedColors = new short[j];
				for (int i1 = 0; i1 < j; i1++) {
					originalColors[i1] = stream.getShort();
					modifiedColors[i1] = stream.getShort();
				}
			} else if (opcode == 41) {
				final int j = stream.getUByte();
				final short[] n = new short[j];
				final short[] v = new short[j];
				for (int i1 = 0; i1 < j; i1++) {
					n[i1] = stream.getShort();
					v[i1] = stream.getShort();
				}
			} else if (42 == opcode) {
				final int j = stream.getUByte();
				final byte[] k = new byte[j];
				for (int i1 = 0; i1 < j; i1++) {
					k[i1] = stream.getByte();
				}
			} else if (44 == opcode) {
				final int j = stream.getUShort();
				int i1 = 0;
				int i2 = j;
				while (i2 > 0) {
					i1++;
					i2 >>= 1;
				}
				final byte[] w = new byte[i1];
				i2 = 0;
				for (int i3 = 0; i3 < i1; i3++) {
					if ((j & 1 << i3) > 0) {
						w[i3] = (byte) i2;
						i2 = (byte) (i2 + 1);
					} else {
						w[i3] = -1;
					}
				}
			} else if (opcode == 45) {
				final int j = stream.getUShort();
				int i1 = 0;
				int i2 = j;
				while (i2 > 0) {
					i1++;
					i2 >>= 1;
				}
				final byte[] i = new byte[i1];
				i2 = 0;
				for (int i3 = 0; i3 < i1; i3++) {
					if ((j & 1 << i3) > 0) {
						i[i3] = (byte) i2;
						i2 = (byte) (i2 + 1);
					} else {
						i[i3] = -1;
					}
				}
			} else if (opcode == 62) {
				this.swapYZ = true;
			} else if (64 == opcode) {
				final boolean am = false;
			} else if (opcode == 65) {
				this.xScale = stream.getShort();
			} else if (opcode == 66) {
				this.yScale = stream.getShort();
			} else if (opcode == 67) {
				this.zScale = stream.getShort();
			} else if (69 == opcode) {
				stream.getUByte();
			} else if (70 == opcode) {
				this.xTranslate = stream.getSmartShort() << 2;
			} else if (71 == opcode) {
				this.yTranslate = stream.getSmartShort() << 2;
			} else if (opcode == 72) {
				this.zTranslate = stream.getSmartShort() << 2;
			} else if (opcode == 73) {
				final boolean ar = true;
			} else if (opcode == 74) {
				this.clippedRange = true;
			} else if (75 == opcode) {
				final int bi = stream.getUByte();
			} else if ((opcode == 77) || (opcode == 92)) {
				this.stageOperationId = stream.getUShort();
				if (65535 == this.stageOperationId)      //0xffff0000
				{
					this.stageOperationId = -1;
				}
				this.stageIndex = stream.getUShort();
				if (65535 == this.stageIndex) {
					this.stageIndex = -1;
				}
				int j = -1;
				if (92 == opcode) {
					j = stream.getBigSmart();
				}
				final int i1 = stream.getUByte();
				this.childrenFileIndex = new int[i1 + 2];
				for (int i2 = 0; i2 <= i1; i2++) {
					this.childrenFileIndex[i2] = stream.getBigSmart();
				}
				this.childrenFileIndex[(i1 + 1)] = j;
			} else if (opcode == 78) {
				final int br = stream.getUShort();
				final int bk = stream.getUByte();
			} else if (opcode == 79) {
				final int bm = stream.getUShort();
				final int bd = stream.getUShort();
				final int bk = stream.getUByte();
				final int j = stream.getUByte();
				final int[] bj = new int[j];
				for (int i1 = 0; i1 < j; i1++) {
					bj[i1] = stream.getUShort();
				}
			} else if (opcode == 81) {
				this.meshMergeType = 2;
				this.meshMergeValue = stream.getUByte();
			} else if (82 == opcode) {
				final boolean bz = true;
			} else if (opcode == 88) {
				final boolean someBoolean1 = false;
			} else if (89 == opcode) {
				final boolean bo = false;
			} else if (opcode == 91) {
				final boolean be = true;
			} else if (93 == opcode) {
				this.meshMergeType = 3;
				this.meshMergeValue = stream.getUShort();
			} else if (94 == opcode) {
				this.meshMergeType = 4;
			} else if (opcode == 95) {
				this.meshMergeType = 5;
				this.meshMergeValue = stream.getSmartShort();//short
			} else if (opcode == 97) {
				final boolean af = true;
			} else if (opcode == 98) {
				final boolean dynamicBoolean1 = true;
			} else if ((99 == opcode) || (100 == opcode)) {
				stream.getByte();
				stream.getShort();
			} else if (opcode == 101) {
				final int au = stream.getUByte();
			} else if (102 == opcode) {
				final int aw = stream.getUShort();
			} else if (opcode == 103) {
				final int q = 0;
			} else if (104 == opcode) {
				final int bw = stream.getUByte();
			} else if (105 == opcode) {
				final boolean al = true;
			} else if (106 == opcode) {
				final int j = stream.getUByte();
				int i1 = 0;
				this.alternateFileIndex = new int[j];
				final int[] ap = new int[j];
				for (int i2 = 0; i2 < j; i2++) {
					this.alternateFileIndex[i2] = stream.getBigSmart();
					i1 += (ap[i2] = stream.getUByte());
				}
				for (int i2 = 0; i2 < j; i2++) {
					ap[i2] = (65535 * ap[i2] / i1);
				}
			} else if (opcode == 107) {
				final int ag = stream.getUShort();
			} else if ((opcode >= 150) && (opcode < 155)) {
				this.menuActions[(opcode - 150)] = stream.getString();
			} else if (160 == opcode) {
				final int j = stream.getUByte();
				final int[] bs = new int[j];
				for (int i1 = 0; i1 < j; i1++) {
					bs[i1] = stream.getUShort();
				}
			} else if (opcode == 162) {
				this.meshMergeType = 3;
				this.meshMergeValue = stream.getInt();
			} else if (opcode == 163) {
				final int r = stream.getByte();
				final int s = stream.getByte();
				final int g = stream.getByte();
				final int y = stream.getByte();
			} else if (opcode == 164) {
				this.xOffsetOverride = stream.getSmartShort();
			} else if (opcode == 165) {
				this.yOffsetOverride = stream.getSmartShort();
			} else if (166 == opcode) {
				this.zOffsetOverride = stream.getSmartShort();
			} else if (167 == opcode) {
				final int bv = stream.getUShort();
			} else if (opcode == 168) {
				final boolean bg = true;
			} else if (169 == opcode) {
				final boolean bu = true;
			} else if (170 == opcode) {
				final int ao = stream.getSignedSmart();
			} else if (171 == opcode) {
				final int at = stream.getSignedSmart();
			} else if (173 == opcode) {
				final int bx = stream.getUShort();
				final int bp = stream.getUShort();
			} else if (opcode == 177) {
				final boolean dynamicBoolean2 = true;
			} else if (opcode == 178) {
				final int bf = stream.getUByte();
			} else if (186 == opcode) {
				stream.getByte(); // & 0xFF
			} else if (189 == opcode) {
				final boolean ba = true;
			} else if ((opcode >= 190) && (opcode < 196)) {
				final int[] ab = new int[6];
				Arrays.fill(ab, -1);
				ab[(opcode - 190)] = stream.getUShort();
			} else if (opcode == 196) {
				//Invokes some method...
				stream.getByte(); //0xFF
			} else if (opcode == 197) {
				//Invokes some method...
				stream.getByte(); //0xFF
			} else if ((opcode != 198) && (opcode != 199)) {
				if (opcode == 200) {
					final boolean unknownBoolean = true;
				} else if (201 == opcode) {
					xStart = stream.getSmart();
					yStart = stream.getSmart();
					zStart = stream.getSmart();
					xStop = stream.getSmart();
					yStop = stream.getSmart();
					zStop = stream.getSmart();
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
	}
}
