package org.powerbot.bot.rt6.client;

import org.powerbot.bot.ReflectProxy;
import org.powerbot.bot.Reflector;

public class RSGroundInfo extends ReflectProxy {
	public RSGroundInfo(final Reflector engine, final Object parent) {
		super(engine, parent);
	}

	public TileData[] getTileData() {
		final Object[] arr = reflector.access(this, Object[].class);
		final TileData[] arr2 = arr != null ? new TileData[arr.length] : null;
		if (arr != null) {
			for (int i = 0; i < arr.length; i++) {
				arr2[i] = new TileData(reflector, arr[i]);
			}
		}
		return arr2;
	}

	public RSGround[][][] getRSGroundArray() {
		final Object[][][] arr = reflector.access(this, Object[][][].class);
		if (arr == null) {
			return null;
		}
		final RSGround[][][] arr2 = new RSGround[arr.length][][];
		for (int i = 0; i < arr.length; i++) {
			final Object[][] sub = arr[i];
			if (sub == null) {
				arr2[i] = null;
				continue;
			}
			final RSGround[][] sub2 = new RSGround[sub.length][];
			arr2[i] = sub2;
			for (int i2 = 0; i2 < sub.length; i2++) {
				final Object[] sub2_1 = sub[i2];
				if (sub2_1 == null) {
					sub2[i] = null;
					continue;
				}
				final RSGround[] sub2_2 = new RSGround[sub2_1.length];
				sub2[i2] = sub2_2;
				for (int i3 = 0; i3 < sub2_1.length; i3++) {
					sub2_2[i3] = new RSGround(reflector, sub2_1[i3]);
				}
			}
		}
		return arr2;
	}
}
