package org.powerbot.gui;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;

import org.powerbot.event.PaintListener;
import org.powerbot.script.wrappers.Drawable;
import org.powerbot.script.wrappers.Interactive;

public class BotBoundingUtility extends JDialog implements PaintListener {
	private final BotChrome chrome;
	private Interactive target;

	public BotBoundingUtility(final BotChrome chrome) {
		this.chrome = chrome;
		target = null;

		final JLabel labelType = new JLabel("Choose type:");
		final JLabel labelX = new JLabel("X");
		final JLabel labelY = new JLabel("Y");
		final JLabel labelZ = new JLabel("Z");
		final JLabel labelStart = new JLabel("Start");
		final JLabel labelStop = new JLabel("Stop");
		final JLabel labelTarget = new JLabel("Target: unknown");

		final JComboBox comboBoxTarget = new JComboBox();

		final int min = -5120, max = 5120, step = 4;
		final JSpinner jSpinner1 = new JSpinner(new SpinnerNumberModel(-1, min, max, step));
		final JSpinner jSpinner2 = new JSpinner(new SpinnerNumberModel(-2, min, max, step));
		final JSpinner jSpinner3 = new JSpinner(new SpinnerNumberModel(-3, min, max, step));
		final JSpinner jSpinner4 = new JSpinner(new SpinnerNumberModel(1, min, max, step));
		final JSpinner jSpinner5 = new JSpinner(new SpinnerNumberModel(2, min, max, step));
		final JSpinner jSpinner6 = new JSpinner(new SpinnerNumberModel(3, min, max, step));

		final JButton buttonExit = new JButton("Exit");
		final JButton buttonCopy = new JButton("Copy to Clipboard");
		final JToggleButton buttonSelectMode = new JToggleButton("Toggle Select");
		final JButton buttonReset = new JButton("Reset");

		chrome.getBot().dispatcher.add(this);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				setVisible(false);
				chrome.getBot().dispatcher.remove(BotBoundingUtility.this);
				dispose();
			}
		});

		comboBoxTarget.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

		final GroupLayout layout = new GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
												.addGap(0, 0, Short.MAX_VALUE)
												.addComponent(buttonReset)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(buttonSelectMode)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(buttonCopy)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(buttonExit)
												.addGap(10, 10, 10))
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
														.addComponent(comboBoxTarget, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
														.addGroup(layout.createSequentialGroup()
																.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
																		.addComponent(labelType, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(labelY)
																				.addGap(18, 18, 18)
																				.addComponent(jSpinner2, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(labelZ)
																				.addGap(18, 18, 18)
																				.addComponent(jSpinner3, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(labelX)
																				.addGap(18, 18, 18)
																				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
																						.addComponent(labelStart)
																						.addComponent(jSpinner1, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))))
																.addGap(18, 18, 18)
																.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
																		.addComponent(labelStop)
																		.addComponent(labelTarget)
																		.addComponent(jSpinner4, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
																		.addComponent(jSpinner5, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
																		.addComponent(jSpinner6, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))))
												.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(labelType)
										.addComponent(labelTarget))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(comboBoxTarget, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(labelStart, GroupLayout.PREFERRED_SIZE, 14, GroupLayout.PREFERRED_SIZE)
										.addComponent(labelStop))
								.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(labelX)
										.addComponent(jSpinner1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jSpinner4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(labelY)
										.addComponent(jSpinner2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jSpinner5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(labelZ)
										.addComponent(jSpinner3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(jSpinner6, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(buttonExit)
										.addComponent(buttonCopy)
										.addComponent(buttonSelectMode)
										.addComponent(buttonReset))
								.addContainerGap())
		);

		pack();
	}

	@Override
	public void repaint(final Graphics render) {
		if (target != null && target instanceof Drawable) {
			((Drawable) target).draw(render);
		}
	}
}
