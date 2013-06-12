package org.powerbot.script.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.powerbot.util.Configuration;
import org.powerbot.util.StringUtil;
import org.powerbot.util.io.HttpClient;
import org.powerbot.util.io.IOHelper;

/**
 * Retrieves a player's hiscore profile.
 * Results are cached.
 *
 * @author Paris
 */
public class Hiscores {
	private final static Map<String, Hiscores> cache = new ConcurrentHashMap<>();
	private final static String PAGE = "http://" + Configuration.URLs.GAME_SERVICES_DOMAIN + "/m=hiscore/index_lite.ws?player=%s";
	private final String username;
	private final Map<Stats, SkillStats> skills;
	private final Map<Stats, ActivityStats> activities;
	private final long updated;

	/**
	 * Downloads and parses a hiscore profile.
	 *
	 * @param username the username to query
	 * @throws IOException
	 */
	private Hiscores(final String username) throws IOException {
		final String txt = IOHelper.readString(HttpClient.openStream(String.format(PAGE, StringUtil.urlEncode(username.replace(" ", "%A0")))));

		this.username = username;
		skills = new HashMap<>();
		activities = new HashMap<>();
		updated = System.currentTimeMillis();

		final Map<Integer, Stats> map = new HashMap<>();
		for (final Stats s : Stats.values()) {
			map.put(s.getIndex(), s);
		}

		final String[] groups = txt.split("\n");
		for (int i = 0; i < groups.length; i++) {
			if (map.containsKey(i)) {
				final String[] parts = groups[i].split(",");
				final Stats s = map.get(i);

				switch (s.getType()) {
				case SKILL:
					if (parts.length == 3) {
						final int[] x = new int[3];
						try {
							for (int j = 0; j < x.length; j++) {
								x[j] = Integer.parseInt(parts[j]);
							}
						} catch (final NumberFormatException ignored) {
							ignored.printStackTrace();
							break;
						}
						skills.put(s, new SkillStats(s, x[1], x[2], x[0]));
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
	}

	/**
	 * Returns a {@link Hiscores} profile.
	 *
	 * @param username the player username
	 * @return a {@link Hiscores} profile or {@code null} if none was found
	 */
	public static synchronized Hiscores getProfile(String username) {
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
	public String getUsername() {
		return username;
	}

	/**
	 * Returns the specified skill profile.
	 *
	 * @param key the {@link Hiscores.Stats} to lookup
	 * @return the associated {@link Hiscores.SkillStats}
	 */
	public SkillStats getSkill(final Stats key) {
		return skills.get(key);
	}

	/**
	 * Returns the specified activity profile.
	 *
	 * @param key the {@link Hiscores.Stats} to lookup
	 * @return the associated {@link Hiscores.ActivityStats}
	 */
	public ActivityStats getActivity(final Stats key) {
		return activities.get(key);
	}

	/**
	 * Returns the last update timestamp of when this profile.
	 *
	 * @return the time (in milliseconds)
	 */
	public long getUpdated() {
		return updated;
	}

	/**
	 * Clears the internal cache.
	 */
	protected void clear() {
		cache.clear();
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
		OVERALL(0, StatsType.SKILL),
		ATTACK(1, StatsType.SKILL),
		DEFENCE(2, StatsType.SKILL),
		STRENGTH(3, StatsType.SKILL),
		CONSTITUTION(4, StatsType.SKILL),
		RANGED(5, StatsType.SKILL),
		PRAYER(6, StatsType.SKILL),
		MAGIC(7, StatsType.SKILL),
		COOKING(8, StatsType.SKILL),
		WOODCUTTING(9, StatsType.SKILL),
		FLETCHING(10, StatsType.SKILL),
		FISHING(11, StatsType.SKILL),
		FIREMAKING(12, StatsType.SKILL),
		CRAFTING(13, StatsType.SKILL),
		SMITHING(14, StatsType.SKILL),
		MINING(15, StatsType.SKILL),
		HERBLORE(16, StatsType.SKILL),
		AGILITY(17, StatsType.SKILL),
		THIEVING(18, StatsType.SKILL),
		SLAYER(19, StatsType.SKILL),
		FARMING(20, StatsType.SKILL),
		RUNECRAFTING(21, StatsType.SKILL),
		HUNTER(22, StatsType.SKILL),
		CONSTRUCTION(23, StatsType.SKILL),
		SUMMONING(24, StatsType.SKILL),
		DUNGEONEERING(25, StatsType.SKILL),
		BOUNTY_HUNTERS(26, StatsType.ACTIVITY),
		BOUNTY_HUNTER_ROGUES(27, StatsType.ACTIVITY),
		DOMINION_TOWER(28, StatsType.ACTIVITY),
		THE_CRUCIBLE(29, StatsType.ACTIVITY),
		CASTLE_WARS_GAMES(30, StatsType.ACTIVITY),
		BA_ATTACKERS(31, StatsType.ACTIVITY),
		BA_DEFENDERS(32, StatsType.ACTIVITY),
		BA_COLLECTORS(33, StatsType.ACTIVITY),
		BA_HEALERS(34, StatsType.ACTIVITY),
		DUEL_TOURNAMENT(35, StatsType.ACTIVITY),
		MOBILISING_ARMIES(36, StatsType.ACTIVITY),
		CONQUEST(37, StatsType.ACTIVITY),
		FIST_OF_GUTHIX(38, StatsType.ACTIVITY),
		GG_RESOURCE_RACE(39, StatsType.ACTIVITY),
		GG_ATHLETICS(40, StatsType.ACTIVITY);
		private final int index;
		private final StatsType type;

		/**
		 * Creates a new {@link Hiscores.Stats} object.
		 *
		 * @param index the index
		 * @param type  the type
		 */
		private Stats(final int index, final StatsType type) {
			this.index = index;
			this.type = type;
		}

		/**
		 * Returns the index.
		 *
		 * @return the index
		 */
		public int getIndex() {
			return index;
		}

		/**
		 * Returns the type.
		 *
		 * @return the type
		 */
		public StatsType getType() {
			return type;
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
		public Stats getStats() {
			return stats;
		}

		/**
		 * Returns the level.
		 *
		 * @return the level
		 */
		public int getLevel() {
			return level;
		}

		/**
		 * Returns the total experience points.
		 *
		 * @return the total number of experience points
		 */
		public int getTotalXp() {
			return xp;
		}

		/**
		 * Returns the global rank.
		 *
		 * @return the global rank
		 */
		public int getRank() {
			return rank;
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
		public Stats getStats() {
			return stats;
		}

		/**
		 * Returns the score.
		 *
		 * @return the score
		 */
		public int getScore() {
			return score;
		}

		/**
		 * Returns the global rank.
		 *
		 * @return the global rank
		 */
		public int getRank() {
			return rank;
		}
	}
}

