package org.powerbot.gui.component;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.powerbot.util.io.Resources;

/**
 * @author Paris
 */
public final class BotStarRatings extends JPanel implements MouseListener {
	private static final long serialVersionUID = -3867651583852191797L;
	private final JLabel[] stars;
	private final ImageIcon[] icons;
	private int rating, rate;

	public BotStarRatings(final int rating) {
		stars = new JLabel[5];
		icons = new ImageIcon[] { new ImageIcon(Resources.Paths.UNSTAR), new ImageIcon(Resources.Paths.STAR) };
		this.rating = rating;
		rate = 0;

		for (int i = 0; i < stars.length; i++) {
			stars[i] = new JLabel(icons[i < rating ? 1 : 0]);
			add(stars[i]);
		}
	}

	public int getRating() {
		return rating;
	}

	private void adjust(final int rate) {
		for (int i = 0; i < stars.length; i++) {
			stars[i].setIcon(icons[i < rate ? 1 : 0]);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
		rating = rate;
	}

	@Override
	public void mousePressed(final MouseEvent e) {
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(final MouseEvent e) {
		adjust(rating);
	}
}
