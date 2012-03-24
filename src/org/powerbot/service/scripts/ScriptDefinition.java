package org.powerbot.service.scripts;

import org.powerbot.game.api.ActiveScript;
import org.powerbot.game.api.Manifest;

/**
 * @author Paris
 */
public final class ScriptDefinition {
	private final String name, description, website;
	private final double version;
	private final String[] authors;
	private final boolean premium;
	private String price = null;

	public ScriptDefinition(final Class<? extends ActiveScript> clazz) {
		this(clazz.getAnnotation(Manifest.class));
	}

	public ScriptDefinition(final Manifest manifest) {
		name = manifest.name();
		description = manifest.description();
		version = manifest.version();
		authors = manifest.authors();
		website = manifest.website();
		premium = manifest.premium();
	}

	public ScriptDefinition(final String name, final String description, final double version, final String[] authors, final String website, final boolean premium) {
		this.name = name;
		this.description = description;
		this.version = version;
		this.authors = authors;
		this.website = website;
		this.premium = premium;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public double getVersion() {
		return version;
	}

	public String[] getAllAuthors() {
		return authors;
	}

	public String getAuthors() {
		final StringBuilder sb = new StringBuilder();
		final String[] authors = getAllAuthors();
		for (int i = 0; i < authors.length; i++) {
			if (i != 0) {
				sb.append(", ");
			}
			sb.append(authors[i]);
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

	public void setPrice(final String price) {
		this.price = price;
	}

	public String getPrice() {
		return price;
	}

	public boolean matches(final String query) {
		final String tag = String.format("%s %s %s", getName(), getDescription(), getAuthors()).toLowerCase();
		return tag.contains(query.toLowerCase());
	}

	@Override
	public String toString() {
		return getName().toLowerCase();
	}
}
