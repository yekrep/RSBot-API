package org.powerbot.script.rt4;

/**
 * Grand Exchange pricing.
 */
public class GeItem extends org.powerbot.script.GeItem {

	/**
	 * Creates an instance.
	 */
	public GeItem(final int id) {
		super("oldschool", id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public org.powerbot.script.GeItem nil() {
		return new GeItem(0);
	}
}
