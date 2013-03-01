package org.powerbot.gui.component;

import java.awt.Component;
import java.io.InputStream;

import javax.swing.JFileChooser;

import org.powerbot.util.io.IOHelper;

public final class BotFileChooser {

	public static void saveAs(final InputStream in, final Component parent) {
		final JFileChooser fc = new JFileChooser();
		if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			IOHelper.write(in, fc.getSelectedFile());
		}
	}
}
