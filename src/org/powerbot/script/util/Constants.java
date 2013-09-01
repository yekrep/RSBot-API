package org.powerbot.script.util;

import java.util.HashMap;
import java.util.Map;

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

	}

	public static int getInt(final String k) {
		return (int) data.get(k);
	}

	public static int[] getIntA(final String k) {
		return (int[]) data.get(k);
	}

	public static String getStr(final String k) {
		return (String) data.get(k);
	}

	public static String[] getStrA(final String k) {
		return (String[]) data.get(k);
	}
}
