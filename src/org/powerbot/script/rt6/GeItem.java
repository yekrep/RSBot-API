package org.powerbot.script.rt6;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.powerbot.Configuration;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;
import org.powerbot.util.StringUtils;

/**
 * Retrieves information about an item on the Grand Exchange.
 * Results are cached.
 *
 */
public class GeItem {
	private final static Map<Integer, GeItem> cache = new ConcurrentHashMap<Integer, GeItem>();
	private final static Map<Integer, Integer> quotes = new ConcurrentHashMap<Integer, Integer>();
	private final int id;
	private final URL icons[];
	private final String category, name, description;
	private final Map<PriceType, Price> prices;
	private final Map<ChangeType, Change> changes;
	private final boolean members;

	/**
	 * Downloads and parses an item definition.
	 *
	 * @param id the item ID
	 * @throws IOException
	 */
	private GeItem(final int id) throws IOException {
		final String url = "http://" + Configuration.URLs.GAME_SERVICES_DOMAIN + "/m=itemdb_rs/api/catalogue/detail.json?item=%s";
		final String txt = IOUtils.readString(HttpUtils.openStream(new URL(String.format(url, StringUtils.urlEncode(Integer.toString(id))))));

		if (txt == null || txt.isEmpty() || txt.equals("[]") || txt.equals("{}")) {
			throw new IOException();
		}

		this.id = Integer.parseInt(getValue(txt, "id"));

		icons = new URL[2];
		icons[0] = new URL(getValue(txt, "icon"));
		icons[1] = new URL(getValue(txt, "icon_large"));

		category = getValue(txt, "type");
		name = getValue(txt, "name");
		description = getValue(txt, "description");

		prices = new HashMap<PriceType, Price>(PriceType.values().length);

		for (final PriceType t : PriceType.values()) {
			final String part = txt.substring(txt.indexOf(t.name().toLowerCase()));
			prices.put(t, new Price(t, trendAsInt(getValue(part, "trend")), formatPrice(getValue(part, "price"))));
		}

		changes = new HashMap<ChangeType, Change>(ChangeType.values().length);

		for (final ChangeType t : ChangeType.values()) {
			final String n = t.name().toLowerCase();
			int z = txt.indexOf(n);
			z = z == -1 ? txt.indexOf(n.replace("day", "days")) : z;
			final String part = txt.substring(z);
			changes.put(t, new Change(t, trendAsInt(getValue(part, "trend")), Double.parseDouble(getValue(part, "change").replace("%", ""))));
		}

		members = Boolean.parseBoolean(getValue(txt, "members"));
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
	 * Returns a {@link GeItem} profile for an item.
	 *
	 * @param id the item ID to query
	 * @return a {@link GeItem} profile or {@code null} if none was found
	 */
	public static synchronized GeItem profile(final int id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		}
		GeItem ge = null;
		try {
			ge = new GeItem(id);
		} catch (final IOException ignored) {
		}
		if (ge != null) {
			cache.put(id, ge);
		}
		return ge;
	}

	/**
	 * Returns the spot (current) price for an item.
	 * The results are cached and may be stale, but give a useful fast quote.
	 *
	 * @param id the item ID to query
	 * @return the quote or {@code -1} if none was found
	 */
	public static synchronized int price(final int id) {
		if (quotes.containsKey(id)) {
			return quotes.get(id);
		}
		if (cache.containsKey(id)) {
			final int p = cache.get(id).price(PriceType.CURRENT).price();
			quotes.put(id, p);
			return p;
		}

		int p = -1;
		try {
			final String txt = IOUtils.readString(HttpUtils.openStream(new URL(String.format("http://api.rsapi.org/ge/item/%s.json", Integer.toString(id)))));
			if (txt != null && !txt.isEmpty()) {
				final String s = getValue(txt, "exact");
				if (!s.isEmpty()) {
					try {
						p = Integer.parseInt(s);
					} catch (final NumberFormatException ignored) {
					}
				}
			}
		} catch (final IOException ignored) {
			p = profile(id).price(PriceType.CURRENT).price();
		}

		quotes.put(id, p);
		return p;
	}

	/**
	 * Maps a trend label to an integer value.
	 *
	 * @param s {@code "neutral"}, {@code "positive"} or {@code "negative"}
	 * @return the integer value for which {@code -1 <= x <= 1}
	 */
	private int trendAsInt(final String s) {
		if (s.equalsIgnoreCase("neutral")) {
			return 0;
		} else if (s.equals("positive")) {
			return 1;
		} else if (s.equals("negative")) {
			return -1;
		}
		return 0;
	}

	/**
	 * Returns the item ID.
	 *
	 * @return the item ID
	 */
	public int id() {
		return id;
	}

	/**
	 * Returns a {@link java.net.URL} to the icon of the item.
	 *
	 * @return a {@link java.net.URL} to the icon of the item
	 */
	public URL icon() {
		return icons[0];
	}

	/**
	 * Returns a {@link java.net.URL} to the large icon of the item.
	 *
	 * @return {@link java.net.URL} to the large icon of the item
	 */
	public URL largeIcon() {
		return icons[1];
	}

	/**
	 * Returns the named category of the item.
	 *
	 * @return the named category of the item
	 */
	public String category() {
		return category;
	}

	/**
	 * Returns the name of the item.
	 *
	 * @return the name of the item
	 */
	public String name() {
		return name;
	}

	/**
	 * Returns the description of the item.
	 *
	 * @return the description of the item
	 */
	public String description() {
		return description;
	}

	/**
	 * Returns the price.
	 *
	 * @param type the {@link GeItem.PriceType}
	 * @return the price
	 */
	public Price price(final PriceType type) {
		return prices.get(type);
	}

	/**
	 * Returns the change.
	 *
	 * @param type the {@link GeItem.ChangeType}
	 * @return the change
	 */
	public Change change(final ChangeType type) {
		return changes.get(type);
	}

	/**
	 * Returns the member status of the item.
	 *
	 * @return {@code true} if the item is members, otherwise {@code false}
	 */
	public boolean members() {
		return members;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final String lf = "\r\n";
		return name() + " (" + id() + ')' + lf + description() + lf + lf
				+ "Current Price:\t" + price(PriceType.CURRENT).price() + lf
				+ "Today's Change:\t" + price(PriceType.TODAY).price() + lf
				+ "30-Day Change:\t" + change(ChangeType.DAY30).change() + '%'+ lf
				+ "90-Day Change:\t" + change(ChangeType.DAY90).change() + '%' + lf
				+ "180-Day Change:\t" + change(ChangeType.DAY180).change() + '%' + lf;
	}

	/**
	 * Clears the internal cache.
	 */
	protected void clear() {
		cache.clear();
	}

	/**
	 * The type of {@link GeItem.Price}.
	 */
	public enum PriceType {
		CURRENT, TODAY
	}

	/**
	 * The type of {@link GeItem.Change}.
	 */
	public enum ChangeType {
		DAY30, DAY90, DAY180
	}

	/**
	 * Price information.
	 */
	public class Price {
		private final PriceType type;
		private final int trend, price;

		/**
		 * Creates a new {@link GeItem.Price} object.
		 *
		 * @param type  the {@link GeItem.PriceType}
		 * @param trend the trend
		 * @param price the price
		 */
		public Price(final PriceType type, final int trend, final int price) {
			this.type = type;
			this.trend = trend;
			this.price = price;
		}

		/**
		 * Returns the type of this {@link GeItem.Price}.
		 *
		 * @return the type
		 */
		public PriceType type() {
			return type;
		}

		/**
		 * Returns the trend.
		 *
		 * @return {@code -1} for negative, {@code 0} for neutral or {@code 1} for positive
		 */
		public int trend() {
			return trend;
		}

		/**
		 * Returns the price.
		 *
		 * @return the price
		 */
		public int price() {
			return price;
		}
	}

	/**
	 * Change information.
	 */
	public class Change {
		private final ChangeType type;
		private final int trend;
		private final double change;

		/**
		 * Creates a new {@link GeItem.Change} object.
		 *
		 * @param type   the {@link GeItem.ChangeType}
		 * @param trend  the trend
		 * @param change the relative change
		 */
		public Change(final ChangeType type, final int trend, final double change) {
			this.type = type;
			this.trend = trend;
			this.change = change;
		}

		/**
		 * Returns the type of this {@link GeItem.Change}.
		 *
		 * @return the type
		 */
		public ChangeType type() {
			return type;
		}

		/**
		 * Returns the trend.
		 *
		 * @return {@code -1} for negative, {@code 0} for neutral or {@code 1} for positive
		 */
		public int trend() {
			return trend;
		}

		/**
		 * Returns the change.
		 *
		 * @return the change as a relative percentage
		 */
		public double change() {
			return change;
		}
	}
}
