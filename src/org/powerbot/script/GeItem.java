package org.powerbot.script;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.powerbot.Configuration;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;

/**
 * Retrieves information about an item on the Grand Exchange.
 */
public abstract class GeItem implements Comparable<GeItem>, Nillable<GeItem> {
	public final String db, name, description;
	public final int id, price;
	public final boolean members;

	/**
	 * Query the item database.
	 *
	 * @param db the database URL suffix
	 * @param id the item ID
	 */
	protected GeItem(final String db, final int id) {
		this.db = db;
		final String url = "http://" + Configuration.URLs.GAME_SERVICES_DOMAIN + "/m=itemdb_" + db + "/api/catalogue/detail.json?item=" + id;
		String txt = "";
		try {
			txt = IOUtils.readString(HttpUtils.openStream(new URL(url)));
		} catch (final IOException ignored) {
		}

		name = getValue(txt, "name");
		description = getValue(txt, "description");
		String x = getValue(txt, "id");
		this.id = x.isEmpty() ? 0 : Integer.parseInt(x);
		x = getValue(txt, "price");
		price = x.isEmpty() ? 0 : formatPrice(x);
		x = getValue(txt, "members");
		members = x.equalsIgnoreCase("true");
	}

	/**
	 * Query the item database.
	 *
	 * @param id the item id
	 */
	protected GeItem(final int id) {
		this("", id);
	}

	private static String getValue(final String json, final String k) {
		final Pattern p = Pattern.compile("\"\\Q" + k + "\\E\"\\s*:\\s*([\\+\\-]*\\d+(?:\\.\\d*)?|true|false|null|\\[[^\\]]*\\]|\"[^\\\"]*\")", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		final Matcher m = p.matcher(json);
		if (!m.find()) {
			return "";
		}
		String s = m.group(1);
		if (s.length() > 1 && s.charAt(0) == '"' && s.charAt(s.length() - 1) == s.charAt(0)) {
			s = s.substring(1, s.length() - 1);
		}
		return s.replace("\\/", "/");
	}

	private static int formatPrice(String s) {
		s = s.replace(",", "").replace(" ", "").trim();
		double f = 1;
		if (s.length() > 1) {
			final char x = s.charAt(s.length() - 1);
			switch (x) {
			case 'B':
			case 'b':
				f = 1000000000d;
				break;
			case 'M':
			case 'm':
				f = 1000000d;
				break;
			case 'K':
			case 'k':
				f = 1000d;
				break;
			}
			if (f != 1) {
				s = s.substring(0, s.length() - 1);
			}
		}
		return (int) (Double.parseDouble(s) * f);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return id == 0 ? "" : String.format("%s (%s): %s; %s", name, id, price, description);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final GeItem o) {
		final int z = db.compareTo(o.db);
		return z == 0 ? id - o.id : z;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		return obj instanceof GeItem && ((GeItem) obj).id == id;
	}
}
