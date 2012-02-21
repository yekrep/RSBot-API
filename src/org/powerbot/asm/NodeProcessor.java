package org.powerbot.asm;

import org.powerbot.lang.AdaptException;

public interface NodeProcessor {
	public void adapt() throws AdaptException;

	public byte[] process(String name, byte[] data);
}
