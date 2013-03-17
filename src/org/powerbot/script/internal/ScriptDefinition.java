package org.powerbot.script.internal;

import java.io.Serializable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.powerbot.script.Manifest;
import org.powerbot.script.Script;
import org.powerbot.util.StringUtil;

/**
 * @author Paris
 */
public final class ScriptDefinition implements Comparable<ScriptDefinition>, Serializable {
	private static final long serialVersionUID = 7424073911663414957L;
	private Script script;
	private final String name, id, description, website;
	private final double version;
	private final String[] authors;
	private final int instantces;

	public String className;
	public byte[] key;
	public String source;
	public boolean local = false;
	private Category category = null;

	public enum Category {
		AGILITY(0, "course"),
		AIO(1, ""),
		COMBAT(2, "fight|kill|duel|soul"),
		CONSTRUCTION(3, "constr"),
		COOKING(4, "cook"),
		CRAFTING(5, "craft|spin|crush|grind|flax|vial"),
		DUNGEONEERING(6, "dung"),
		FARMING(7, "farm"),
		FIREMAKING(8, "fire"),
		FISHING(9, "fish"),
		FLETCHING(10, "fletch"),
		HERBLORE(11, "herb|clean|mix"),
		HUNTER(12, "hunt|catch"),
		MAGIC(13, "mage"),
		MINIGAME(14, "sorc|puzzle|minigame|pest|cape"),
		MINING(15, "mine|ore"),
		MONEY(16, "gp|gold"),
		PRAYER(17, "pray|bones"),
		QUEST(18, "quest"),
		RANGED(19, "range|arrow"),
		RUNECRAFTING(20, "rune|ess"),
		SMITHING(21, "smith|bars"),
		SUMMONING(22, "summ"),
		THIEVING(23, "thief|steal|thiev"),
		WOODCUTTING(24, "wood|chop");

		public final int index;
		public final String regex;

		private Category(final int index, final String regex) {
			this.index = index;
			this.regex = regex;
		}
	}

	public ScriptDefinition(final Script script) {
		this(script, script.getClass().getAnnotation(Manifest.class));
	}

	public ScriptDefinition(final Script script, final Manifest manifest) {
		this.script = script;
		name = manifest.name();
		id = null;
		description = manifest.description();
		version = manifest.version();
		authors = manifest.authors();
		website = manifest.website();
		instantces = manifest.instantces();
	}

	public ScriptDefinition(final Script script, final Map<String, String> data) {
		final Class<Manifest> manifest = Manifest.class;

		this.script = script;
		name = data.containsKey("name") ? data.get("name") : null;
		id = data.containsKey("id") ? data.get("id") : null;
		description = data.containsKey("description") ? data.get("description") : null;
		website = data.containsKey("website") ? data.get("website") : null;
		authors = data.containsKey("authors") ? data.get("authors").split(",") : new String[]{};

		double version = 1d;
		if (data.containsKey("version")) {
			try {
				version = Double.parseDouble(data.get("version"));
			} catch (final NumberFormatException ignored) {
			}
		}
		this.version = version;

		int instantces = Integer.MAX_VALUE;
		try {
			instantces = (int) manifest.getMethod("instantces").getDefaultValue();
		} catch (final NoSuchMethodException ignored) {
		}
		if (data.containsKey("instantces")) {
			try {
				instantces = Integer.parseInt(data.get("instantces"));
			} catch (final NumberFormatException ignored) {
			}
		}
		this.instantces = instantces;
	}

	public ScriptDefinition(final Map<String, String> data) {
		this(null, data);
	}

	public Script getScript() {
		return script;
	}

	public void setScript(final Script script) {
		if (this.script != null) {
			throw new IllegalStateException();
		}
		this.script = script;
	}

	private String getCleanText(String s) {
		return s == null || s.isEmpty() ? "" : StringUtil.stripHtml(s.trim());
	}

	public String getName() {
		return getCleanText(name);
	}

	public String getID() {
		return id == null ? "" : id;
	}

	public String getDescription() {
		return getCleanText(description);
	}

	public double getVersion() {
		return version;
	}

	public String[] getAllAuthors() {
		return authors;
	}

	public String getAuthors() {
		if (authors == null || authors.length == 0) {
			return "";
		}
		final StringBuilder sb = new StringBuilder();
		final String[] authors = getAllAuthors();
		for (int i = 0; i < authors.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(StringUtil.stripHtml(authors[i].trim()));
		}
		return sb.toString();
	}

	public String getWebsite() {
		final String url = website;
		return url != null && !url.isEmpty() && (url.startsWith("http://") || url.startsWith("https://")) ? url : null;
	}

	public int getInstantces() {
		return instantces;
	}

	public Category getCategory() {
		if (category != null) {
			return category;
		}

		category = Category.AIO;
		final String sig = (getName() + " " + getDescription()).toLowerCase();
		int x = 0xfff;

		for (final Category c : Category.class.getEnumConstants()) {
			int z = sig.indexOf(c.name().toLowerCase());
			if (z != -1 && z < x) {
				x = z;
				category = c;
			}
			if (c.regex.length() == 0) {
				continue;
			}
			final Matcher m = Pattern.compile(c.regex).matcher(sig);
			if (m.find(0)) {
				z = m.start();
				if (z != -1 && z < x) {
					x = z;
					category = c;
				}
			}
		}

		return category;
	}

	public boolean matches(final String query) {
		final String tag = String.format("%s %s %s", getName(), getDescription(), getAuthors()).toLowerCase();
		return tag.contains(query.toLowerCase());
	}

	@Override
	public String toString() {
		return getName().toLowerCase();
	}

	@Override
	public int compareTo(final ScriptDefinition o) {
		final String a = getID(), b = o.getID();
		return a == null || b == null ? 0 : a.compareTo(b);
	}
}
