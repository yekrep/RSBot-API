package org.powerbot.script.util;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import org.powerbot.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

/**
 * Retrieves information about an item on the Grand Exchange.
 * Results are cached.
 *
 * @author Paris
 */
public class GeItem {
	private final static Map<Integer, GeItem> cache = new ConcurrentHashMap<>();
	private final static String PAGE = "http://" + Configuration.URLs.GAME_SERVICES_DOMAIN + "/m=itemdb_rs/api/catalogue/detail.json?item=%s";
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
		final String txt = IOHelper.readString(HttpClient.openStream(String.format(PAGE, StringUtil.urlEncode(Integer.toString(id)))));
		final JsonObject json = JsonObject.readFrom(txt).get("item").asObject();

		this.id = json.get("id").asInt();

		icons = new URL[2];
		icons[0] = new URL(json.get("icon").asString());
		icons[1] = new URL(json.get("icon_large").asString());

		category = json.get("type").asString();
		name = json.get("name").asString();
		description = json.get("description").asString();

		final List<String> names = json.names();

		prices = new HashMap<>(PriceType.values().length);

		for (final PriceType t : PriceType.values()) {
			final String n = t.name().toLowerCase();
			if (names.contains(n)) {
				final JsonObject c = json.get(n).asObject();
				prices.put(t, new Price(t, trendAsInt(c.get("trend").asString()), parsePrice(c.get("price"))));
			}
		}

		changes = new HashMap<>(ChangeType.values().length);

		for (final ChangeType t : ChangeType.values()) {
			final String n = t.name().toLowerCase();
			if (names.contains(n)) {
				final JsonObject c = json.get(n).asObject();
				changes.put(t, new Change(t, trendAsInt(c.get("trend").asString()), Double.parseDouble(c.get("change").asString().replace("%", ""))));
			}
		}

		final JsonValue v = json.get("members");
		members = json.isBoolean() ? v.asBoolean() : v.asString().equals("true");
	}

	private static int parsePrice(final JsonValue v) {
		return v.isString() ? formatPrice(v.asString()) : v.isNumber() ? v.asInt() : 0;
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
	public static synchronized GeItem getProfile(final int id) {
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
	 * Maps a trend label to an integer value.
	 *
	 * @param s {@code "neutral"}, {@code "positive"} or {@code "negative"}
	 * @return the integer value for which {@code -1 <= x <= 1}
	 */
	private int trendAsInt(final String s) {
		switch (s) {
		case "neutral":
			return 0;
		case "positive":
			return 1;
		case "negative":
			return -1;
		}
		return 0;
	}

	/**
	 * Returns the item ID.
	 *
	 * @return the item ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns a {@link java.net.URL} to the icon of the item.
	 *
	 * @return a {@link java.net.URL} to the icon of the item
	 */
	public URL getIcon() {
		return icons[0];
	}

	/**
	 * Returns a {@link java.net.URL} to the large icon of the item.
	 *
	 * @return {@link java.net.URL} to the large icon of the item
	 */
	public URL getLargeIcon() {
		return icons[1];
	}

	/**
	 * Returns the named category of the item.
	 *
	 * @return the named category of the item
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * Returns the name of the item.
	 *
	 * @return the name of the item
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the description of the item.
	 *
	 * @return the description of the item
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param type
	 * @return
	 */
	public Price getPrice(final PriceType type) {
		return prices.get(type);
	}

	/**
	 * @param type
	 * @return
	 */
	public Change getChange(final ChangeType type) {
		return changes.get(type);
	}

	/**
	 * Returns the member status of the item.
	 *
	 * @return {@code true} if the item is members, otherwise {@code false}
	 */
	public boolean isMembers() {
		return members;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final String lf = "\r\n";
		s.append(getName()).append(" (").append(getId()).append(')').append(lf).append(getDescription()).append(lf).append(lf);
		s.append("Current Price:\t").append(getPrice(PriceType.CURRENT).getPrice()).append(lf);
		s.append("Today's Change:\t").append(getPrice(PriceType.TODAY).getPrice()).append(lf);
		s.append("30-Day Change:\t").append(getChange(ChangeType.DAY30).getChange()).append('%').append(lf);
		s.append("90-Day Change:\t").append(getChange(ChangeType.DAY90).getChange()).append('%').append(lf);
		s.append("180-Day Change:\t").append(getChange(ChangeType.DAY180).getChange()).append('%').append(lf);
		return s.toString();
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
		public PriceType getType() {
			return type;
		}

		/**
		 * Returns the trend.
		 *
		 * @return {@code -1} for negative, {@code 0} for neutral or {@code 1} for positive
		 */
		public int getTrend() {
			return trend;
		}

		/**
		 * Returns the price.
		 *
		 * @return the price
		 */
		public int getPrice() {
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
		public ChangeType getType() {
			return type;
		}

		/**
		 * Returns the trend.
		 *
		 * @return {@code -1} for negative, {@code 0} for neutral or {@code 1} for positive
		 */
		public int getTrend() {
			return trend;
		}

		/**
		 * Returns the change.
		 *
		 * @return the change as a relative percentage
		 */
		public double getChange() {
			return change;
		}
	}
}
