package org.powerbot.script.rt6;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.powerbot.script.StringUtils;
import org.powerbot.util.Environment;
import org.powerbot.util.HttpUtils;
import org.powerbot.util.IOUtils;

/**
 * Hiscores
 * Retrieves a player's hiscore profile.
 * Results are cached.
 */
public class Hiscores {
	private final static Map<String, Hiscores> cache = new ConcurrentHashMap<String, Hiscores>();
	private final static String PAGE = "http://services." + Environment.DOMAINS[1] + "/m=hiscore/index_lite.ws?player=%s";
	private final String username;
	private final Map<Stats, SkillStats> skills;
	private final Map<Stats, ActivityStats> activities;
	private final long totalxp, updated;

	/**
	 * Downloads and parses a hiscore profile.
	 *
	 * @param username the username to query
	 * @throws IOException
	 */
	private Hiscores(final String username) throws IOException {
		final String txt = IOUtils.readString(HttpUtils.openStream(new URL(String.format(PAGE, StringUtils.urlEncode(username).replace("+", "%A0")))));

		this.username = username;
		skills = new HashMap<Stats, SkillStats>();
		activities = new HashMap<Stats, ActivityStats>();
		updated = System.currentTimeMillis();
		long totalxp = 0L;

		final Map<Integer, Stats> map = new HashMap<Integer, Stats>();
		for (final Stats s : Stats.values()) {
			map.put(s.ordinal(), s);
		}

		final String[] groups = txt.split("\n");
		for (int i = 0; i < groups.length; i++) {
			if (map.containsKey(i)) {
				final String[] parts = groups[i].split(",");
				final Stats s = map.get(i);

				switch (s.type()) {
				case SKILL:
					if (parts.length == 3) {
						final int[] x = new int[3];
						for (int j = 0; j < x.length; j++) {
							try {
								x[j] = Integer.parseInt(parts[j]);
							} catch (final NumberFormatException ignored) {
								if (s == Stats.OVERALL && j == 2) {
									x[j] = Integer.MAX_VALUE;
								} else {
									ignored.printStackTrace();
									break;
								}
							}

							if (j == x.length - 1) {
								if (s == Stats.OVERALL) {
									try {
										totalxp = Long.parseLong(parts[j]);
									} catch (final NumberFormatException ignored) {
										totalxp = x[2];
									}
								}
								skills.put(s, new SkillStats(s, x[1], x[2], x[0]));
							}
						}
					}
					break;

				case ACTIVITY:
					if (parts.length == 2) {
						final int[] x = new int[2];
						try {
							for (int j = 0; j < x.length; j++) {
								x[j] = Integer.parseInt(parts[j]);
							}
						} catch (final NumberFormatException ignored) {
							break;
						}
						activities.put(s, new ActivityStats(s, x[1], x[0]));
					}
					break;
				}
			}
		}

		this.totalxp = totalxp;
	}

	/**
	 * Returns a {@link Hiscores} profile.
	 *
	 * @param username the player username
	 * @return a {@link Hiscores} profile or {@code null} if none was found
	 */
	public static synchronized Hiscores profile(String username) {
		username = normaliseUsername(username);
		if (cache.containsKey(username)) {
			return cache.get(username);
		}
		Hiscores profile = null;
		try {
			profile = new Hiscores(username);
		} catch (final IOException ignored) {
		}
		cache.put(username, profile);
		return profile;
	}

	/**
	 * Normalises a player username.
	 *
	 * @param u the username
	 * @return a normalised name which is correctly formatted
	 */
	private static String normaliseUsername(String u) {
		u = u.trim().toLowerCase();
		final StringBuilder s = new StringBuilder(u.length());
		boolean p = true;

		for (int i = 0; i < u.length(); i++) {
			final char c = u.charAt(i);
			if (" \t\u00A0_".indexOf(c) != -1) {
				p = true;
				s.append(' ');
			} else if (p) {
				p = false;
				s.append(String.valueOf(c).toUpperCase());
			} else {
				s.append(c);
			}
		}

		return s.toString();
	}

	/**
	 * Returns the username of this player profile.
	 *
	 * @return the username of this player profile
	 */
	public String username() {
		return username;
	}

	/**
	 * Returns the specified skill profile.
	 *
	 * @param key the {@link Hiscores.Stats} to lookup
	 * @return the associated {@link Hiscores.SkillStats}
	 */
	public SkillStats skill(final Stats key) {
		return skills.get(key);
	}

	/**
	 * Returns the specified activity profile.
	 *
	 * @param key the {@link Hiscores.Stats} to lookup
	 * @return the associated {@link Hiscores.ActivityStats}
	 */
	public ActivityStats activity(final Stats key) {
		return activities.get(key);
	}

	/**
	 * Returns the overall total experience points, which may be truncated in {@link #skill(Hiscores.Stats)}.
	 *
	 * @return the overal total experience points
	 */
	public long totalXp() {
		return totalxp;
	}

	/**
	 * Returns the last update timestamp of when this profile.
	 *
	 * @return the time (in milliseconds)
	 */
	public long updated() {
		return updated;
	}

	/**
	 * Clears the internal cache.
	 */
	protected void clear() {
		cache.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final String lf = System.getProperty("line.separator"), d = " * ";
		s.append(username).append(" (").append(totalxp).append(")").append(lf);

		for (final SkillStats t : skills.values()) {
			s.append(d).append(t.toString()).append(lf);
		}

		for (final ActivityStats t : activities.values()) {
			s.append(d).append(t.toString()).append(lf);
		}

		s.setLength(s.length() - lf.length());
		return s.toString();
	}

	/**
	 * The type of {@link Hiscores.Stats}.
	 */
	public enum StatsType {
		SKILL, ACTIVITY
	}

	/**
	 * Stats information.
	 */
	public enum Stats {
		OVERALL,
		ATTACK,
		DEFENCE,
		STRENGTH,
		CONSTITUTION,
		RANGED,
		PRAYER,
		MAGIC,
		COOKING,
		WOODCUTTING,
		FLETCHING,
		FISHING,
		FIREMAKING,
		CRAFTING,
		SMITHING,
		MINING,
		HERBLORE,
		AGILITY,
		THIEVING,
		SLAYER,
		FARMING,
		RUNECRAFTING,
		HUNTER,
		CONSTRUCTION,
		SUMMONING,
		DUNGEONEERING,
		DIVINATION,
		BOUNTY_HUNTERS,
		BOUNTY_HUNTER_ROGUES,
		DOMINION_TOWER,
		THE_CRUCIBLE,
		CASTLE_WARS_GAMES,
		BA_ATTACKERS,
		BA_DEFENDERS,
		BA_COLLECTORS,
		BA_HEALERS,
		DUEL_TOURNAMENT,
		MOBILISING_ARMIES,
		CONQUEST,
		FIST_OF_GUTHIX,
		GG_RESOURCE_RACE,
		GG_ATHLETICS;

		/**
		 * Creates a new {@link Hiscores.Stats} object.
		 */
		Stats() {
		}

		/**
		 * Returns the type.
		 *
		 * @return the type
		 */
		public StatsType type() {
			return ordinal() > 25 ? StatsType.ACTIVITY : StatsType.SKILL;
		}
	}

	/**
	 * Skill information.
	 */
	public class SkillStats {
		public final int level;
		public final int xp;
		public final int rank;
		public final Stats stats;

		/**
		 * Creates a new {@link Hiscores.SkillStats} object.
		 *
		 * @param stats the type of stat
		 * @param level the level
		 * @param xp    the experience points
		 * @param rank  the global rank
		 */
		public SkillStats(final Stats stats, final int level, final int xp, final int rank) {
			this.stats = stats;
			this.level = level;
			this.xp = xp;
			this.rank = rank;
		}

		/**
		 * Returns the type of stat.
		 *
		 * @return the type of stat
		 */
		public Stats stats() {
			return stats;
		}

		/**
		 * Returns the level.
		 *
		 * @return the level
		 */
		public int level() {
			return level;
		}

		/**
		 * Returns the total experience points.
		 *
		 * @return the total number of experience points
		 */
		public int totalXp() {
			return xp;
		}

		/**
		 * Returns the global rank.
		 *
		 * @return the global rank
		 */
		public int rank() {
			return rank;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String toString() {
			return StringUtils.toDisplayCase(stats.toString()) + ": " + level + " (" + xp + ") #" + rank;
		}
	}

	/**
	 * Activity information.
	 */
	public class ActivityStats {
		public final int score;
		public final int rank;
		public final Stats stats;

		/**
		 * Creates a new {@link Hiscores.ActivityStats} object.
		 *
		 * @param stats the type of stat
		 * @param score the score
		 * @param rank  the global rank
		 */
		public ActivityStats(final Stats stats, final int score, final int rank) {
			this.stats = stats;
			this.score = score;
			this.rank = rank;
		}

		/**
		 * Returns the type of stat.
		 *
		 * @return the type of stat
		 */
		public Stats stats() {
			return stats;
		}

		/**
		 * Returns the score.
		 *
		 * @return the score
		 */
		public int score() {
			return score;
		}

		/**
		 * Returns the global rank.
		 *
		 * @return the global rank
		 */
		public int rank() {
			return rank;
		}

		@Override
		public String toString() {
			return StringUtils.toDisplayCase(stats.toString()) + ": " + score + " #" + rank;
		}
	}
}

