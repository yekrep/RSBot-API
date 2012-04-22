package org.powerbot.service.scripts;

import java.net.URL;
import java.util.Map;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.IniParser;

/**
 * @author Paris
 */
public final class ScriptDefinition {
	private final String name, id, description, website;
	private final double version;
	private final String[] authors;
	private final boolean premium;

	public String className;
	public URL source;
	public boolean local = false;

	public ScriptDefinition(final Class<? extends ActiveScript> clazz) {
		this(clazz.getAnnotation(Manifest.class));
	}

	public ScriptDefinition(final Manifest manifest) {
		name = manifest.name();
		id = null;
		description = manifest.description();
		version = manifest.version();
		authors = manifest.authors();
		website = manifest.website();
		premium = manifest.premium();
	}

	public ScriptDefinition(final String name, final String id, final String description, final double version, final String[] authors, final String website, final boolean premium) {
		this.name = name;
		this.id = id;
		this.description = description;
		this.version = version;
		this.authors = authors;
		this.website = website;
		this.premium = premium;
	}

	public String getName() {
		String name = StringUtil.stripHtml(this.name.trim());
		name = name.replaceAll("\\s*(?:[~-]\\s*)?(?:[Vv]\\s*)?[\\d\\.]+\\s*$", "");
		name = name.replaceAll("\\s*\\(\\s*[\\d\\.]+\\s*\\)\\s*$", "");
		return name;
	}

	public String getID() {
		return id;
	}

	public String getDescription() {
		if (description == null || description.isEmpty()) {
			return "";
		}
		String s = StringUtil.stripHtml(description.trim());
		if (s.length() > 2 && s.substring(s.length() - 1).equals(".") && !s.substring(0, s.length() - 1).contains(".")) {
			s = s.substring(0, s.length() - 1);
		}
		s = s.replaceFirst("\\s*[Vv]\\s*[\\d\\.]+\\s*", "");
		s = s.replaceFirst("\\s*[-~]+\\s*[Bb][Yy]\\s+.*$", "");
		s = s.replaceFirst("\\s+[-~]+\\s*.*$", "");
		s = s.replaceFirst("\\s*[Bb][Yy]\\s+.*$", "");
		return s;
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

	public boolean isPremium() {
		return premium;
	}

	public boolean matches(final String query) {
		final String tag = String.format("%s %s %s", getName(), getDescription(), getAuthors()).toLowerCase();
		return tag.contains(query.toLowerCase());
	}

	@Override
	public String toString() {
		return getName().toLowerCase();
	}

	public static ScriptDefinition fromMap(final Map<String, String> data) {
		final String name = data.containsKey("name") ? data.get("name") : null;
		final String id = data.containsKey("id") ? data.get("id") : null;
		final String description = data.containsKey("description") ? data.get("description") : null;
		final String website = data.containsKey("website") ? data.get("website") : null;
		final boolean premium = data.containsKey("premium") ? IniParser.parseBool(data.get("premium")) : false;
		final String[] authors = data.containsKey("authors") ? data.get("authors").split(",") : new String[] {};
		double version = 1d;

		if (data.containsKey("version")) {
			try {
				version = Double.parseDouble(data.get("version"));
			} catch (final NumberFormatException ignored) {
			}
		}

		return name == null || name.isEmpty() ? null : new ScriptDefinition(name, id, description, version, authors, website, premium);
	}
}
