package org.powerbot.bot.rt4.client;

import org.powerbot.bot.ContextAccessor;
import org.powerbot.bot.ReflectionEngine;

public class Landscape extends ContextAccessor {
	public Landscape(final ReflectionEngine engine, final Object parent) {
		super(engine, parent);
	}

	public Tile[][][] getTiles() {
		final Object[][][] arr = engine.access(this, Object[][][].class);
		if (arr == null) {
			return null;
		}
		final Tile[][][] arr2 = new Tile[arr.length][][];
		for (int i = 0; i < arr.length; i++) {
			final Object[][] sub = arr[i];
			if (sub == null) {
				arr2[i] = null;
				continue;
			}
			final Tile[][] sub2 = new Tile[sub.length][];
			arr2[i] = sub2;
			for (int i2 = 0; i2 < sub.length; i2++) {
				final Object[] sub2_1 = sub[i2];
				if (sub2_1 == null) {
					sub2[i] = null;
					continue;
				}
				final Tile[] sub2_2 = new Tile[sub2_1.length];
				sub2[i2] = sub2_2;
				for (int i3 = 0; i3 < sub2_1.length; i3++) {
					sub2_2[i3] = new Tile(engine, sub2_1[i3]);
				}
			}
		}
		return arr2;
	}
}
