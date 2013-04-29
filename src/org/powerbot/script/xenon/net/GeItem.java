package org.powerbot.script.xenon.net;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.eclipsesource.json.JsonObject;
import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

/**
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
				prices.put(t, new Price(t, trendAsInt(c.get("trend").asString()), c.get("price").asInt()));
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

		members = json.get("members").asString().equals("true");
	}

	public static synchronized GeItem getProfile(final int id) {
		if (cache.containsKey(id)) {
			return cache.get(id);
		}
		GeItem ge = null;
		try {
			ge = new GeItem(id);
		} catch (final IOException ignored) {
			ignored.printStackTrace();
		}
		cache.put(id, ge);
		return ge;
	}

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

	public int getId() {
		return id;
	}

	public URL getIcon() {
		return icons[0];
	}

	public URL getLargeIcon() {
		return icons[1];
	}

	public String getCategory() {
		return category;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Price getPrice(final PriceType type) {
		return prices.get(type);
	}

	public Change getChange(final ChangeType type) {
		return changes.get(type);
	}

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

	protected void clear() {
		cache.clear();
	}

	public enum PriceType {CURRENT, TODAY}

	public enum ChangeType {DAY30, DAY90, DAY180}

	public class Price {
		private final PriceType type;
		private final int trend, price;

		public Price(final PriceType type, final int trend, final int price) {
			this.type = type;
			this.trend = trend;
			this.price = price;
		}

		public PriceType getType() {
			return type;
		}

		public int getTrend() {
			return trend;
		}

		public int getPrice() {
			return price;
		}
	}

	public class Change {
		private final ChangeType type;
		private final int trend;
		private final double change;

		public Change(final ChangeType type, final int trend, final double change) {
			this.type = type;
			this.trend = trend;
			this.change = change;
		}

		public ChangeType getType() {
			return type;
		}

		public int getTrend() {
			return trend;
		}

		public double getChange() {
			return change;
		}
	}
}
