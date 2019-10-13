package org.powerbot.bot;

import org.junit.Test;
import org.powerbot.script.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;

public class TestBase {

	@Test
	public void environmentValid() {
		final Script.Manifest m = ContextClassLoader.class.getAnnotation(Script.Manifest.class);

		assertFalse(m.name().isEmpty());
		assertFalse(m.description().isEmpty());

		try {
			final InetAddress a = InetAddress.getByName(m.description());
		} catch (final UnknownHostException e) {
			fail();
		}
	}
}
