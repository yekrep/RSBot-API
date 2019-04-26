package org.powerbot.script.rt6;


import org.powerbot.bot.cache.Block;
import org.powerbot.bot.cache.AbstractCacheWorker;
import org.powerbot.bot.cache.JagexStream;

public class CacheVarbitConfig {
	private final int index;
	private final JagexStream stream;
	public int configId = -1;
	public int configType = -1;
	public int lowerBitIndex = -1;
	public int upperBitIndex = -1;

	CacheVarbitConfig(final Block.Sector sector, final int index) {
		this.index = index;
		this.stream = new JagexStream(sector.getPayload());
		read();
	}

	public static CacheVarbitConfig load(final AbstractCacheWorker worker, final int id) {
		final Block b = worker.getBlock(16, 69);
		if (b == null) {
			return null;
		}
		final Block.Sector s = b.getSector(id);
		if (s == null) {
			return null;
		}
		return new CacheVarbitConfig(s, id);
	}

	private void read() {
		int opcode;
		while ((opcode = stream.getUByte()) != 0) {
			if (opcode == 1) {
				configType = stream.getUByte();
				configId = stream.getBigSmart();
			} else if (opcode == 2) {
				lowerBitIndex = stream.getUByte();
				upperBitIndex = stream.getUByte();
			}
		}
	}
}
