package org.powerbot.util;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.Callable;

import org.powerbot.gui.BotChrome;
import org.powerbot.gui.BotLicense;
import org.powerbot.util.io.IOHelper;
import org.powerbot.util.io.IniParser;
import org.powerbot.util.io.SecureStore;

public final class LoadLicense implements Callable<Boolean> {
	public Boolean call() throws Exception {
		final String name = "license-accept.txt";
		try {
			if (new File(Configuration.STORE).isFile()) {
				final InputStream in = SecureStore.getInstance().read(name);
				if (in != null && IniParser.parseBool(IOHelper.readString(in))) {
					return true;
				}
			}
		} catch (final Exception ignored) {
		}
		new BotLicense(BotChrome.getInstance(), true);
		SecureStore.getInstance().write(name, StringUtil.getBytesUtf8("true"));
		return true;
	}
}
