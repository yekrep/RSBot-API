package org.powerbot.script.rt4;

import java.util.concurrent.Callable;

import org.powerbot.script.Condition;
import org.powerbot.script.StringUtils;

/**
 * Combat
 * A utility class with methods for parsing widget and varpbit values.
 */
public class Combat extends ClientAccessor {
	public Combat(final ClientContext ctx) {
		super(ctx);
	}

	public int health() {
		return StringUtils.parseInt(ctx.widgets.component(160, 5).text());
	}

	/**
	 * The special attack percentage.
	 *
	 * @return The percentage (represented as 0-100) of the player's special attack.
	 */
	public int specialPercentage() {
		return ctx.varpbits.varpbit(300) / 10;
	}

	/**
	 * Whether or not the player has a special attack queued.
	 *
	 * @return <ii>true</ii> if the player can execute a special attack, <ii>false</ii> otherwise.
	 */
	public boolean specialAttack() {
		return ctx.varpbits.varpbit(301) == 1;
	}

	/**
	 * Whether or not the player is in a multi-combat area.
	 *
	 * @return <ii>true</ii> if within a multi-combat area, <ii>false</ii> otherwise.
	 */
	public boolean inMultiCombat() {
		return ctx.varpbits.varpbit(1021, 5, 0x1) == 1;
	}

	/**
	 * Executes a special attack.
	 *
	 * @param select Whether or not to select the percentage bar.
	 * @return <ii>true</ii> if the special attack is selected, <ii>false</ii>
	 * otherwise.
	 */
	public boolean specialAttack(final boolean select) {
		if (specialAttack() == select) {
			return true;
		}

		Component c = null;
		for (final Component comp : ctx.widgets.widget(593).components()) {
			if (comp.text().contains("Special attack:")) {
				c = comp;
				break;
			}
		}

		final int current = specialPercentage();
		return c != null && ctx.game.tab(Game.Tab.ATTACK) && c.visible() && c.click() && Condition.wait(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return specialAttack() == select || specialPercentage() != current;
			}
		}, 300, 6);
	}
}
