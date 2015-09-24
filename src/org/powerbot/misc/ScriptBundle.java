package org.powerbot.misc;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.powerbot.script.Script;
import org.powerbot.util.StringUtils;

public class ScriptBundle {
	public final Definition definition;
	public final Class<? extends Script> script;
	public final AtomicReference<Script> instance;

	public ScriptBundle(final Definition definition, final Class<? extends Script> script) {
		this.definition = definition;
		this.script = script;
		instance = new AtomicReference<Script>(null);
	}

	public static final class Definition implements Comparable<Definition> {
		public static final String LOCALID = "0/local";
		private final String name, id, description;

		public String className;
		public byte[] key;
		public String source, website;
		public boolean local = false, assigned = false;
		public Type client;

		public Definition(final Script.Manifest manifest) {
			name = manifest.name();
			id = null;
			description = manifest.description();
		}

		public Definition(final String name, final String id, final String description) {
			this.name = name;
			this.id = id;
			this.description = description;
		}

		private String getCleanText(final String s) {
			return s == null || s.isEmpty() ? "" : StringUtils.stripHtml(s.trim());
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

		@Override
		public String toString() {
			return getName().toLowerCase();
		}

		public static Definition fromMap(final Map<String, String> data) {
			final String name = data.containsKey("name") ? data.get("name") : null;

			if (name == null || name.isEmpty()) {
				return null;
			}

			final String id = data.containsKey("id") ? data.get("id") : null;
			final String description = data.containsKey("description") ? data.get("description") : null;

			final Definition def = new Definition(name, id, description);
			def.assigned = data.containsKey("assigned") && !data.get("assigned").equals("0");
			def.website = data.containsKey("website") ? data.get("website") : "";

			if (data.containsKey("client")) {
				final String t = data.get("client");
				if (t.equals("6")) {
					def.client = org.powerbot.script.rt6.ClientContext.class;
				} else if (t.equals("4")) {
					def.client = org.powerbot.script.rt4.ClientContext.class;
				}
			}

			return def;
		}

		@Override
		public int compareTo(final Definition o) {
			final String a = getID(), b = o.getID();
			return a == null || b == null ? 0 : a.compareTo(b);
		}
	}

	public static Map<String, String> parseProperties(final String s) {
		final Map<String, String> items = new HashMap<String, String>();
		final String[] p = s.split("(?<!\\\\);");

		for (final String x : p) {
			final String[] e = x.split("(?<!\\\\)=", 2);
			items.put(e[0].trim(), e.length > 1 ? e[1].trim() : "");
		}

		return items;
	}
}
