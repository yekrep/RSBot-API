package org.powerbot.gui;

import java.awt.Frame;

import javax.swing.JDialog;

import org.powerbot.gui.component.BotLocale;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.Resources;

/***
 * @author Paris
 */
public final class BotFeedback extends JDialog {
	private static final long serialVersionUID = 83859696285844888L;

	public BotFeedback(final Frame parent) {
		super(parent, BotLocale.FEEDBACK, true);
		setIconImage(Resources.getImage(Resources.Paths.COMMENTS));

		

		pack();
		setMinimumSize(getSize());
		setResizable(false);
		setLocationRelativeTo(getParent());
		setVisible(true);

		Tracker.getInstance().trackPage("feedback/", getTitle());
	}
}
