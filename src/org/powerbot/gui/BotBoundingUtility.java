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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
		final SpinnerNumberModel
				modelX1 = new SpinnerNumberModel(-256, min, max, step),
				modelY1 = new SpinnerNumberModel(0, min, max, step),
				modelZ1 = new SpinnerNumberModel(-256, min, max, step),
				modelX2 = new SpinnerNumberModel(256, min, max, step),
				modelY2 = new SpinnerNumberModel(512, min, max, step),
				modelZ2 = new SpinnerNumberModel(256, min, max, step);
		final ChangeListener l = new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent changeEvent) {
				if (target != null) {
					target.setBounds(
							modelX1.getNumber().intValue(), modelX2.getNumber().intValue(),
							modelY1.getNumber().intValue(), modelY2.getNumber().intValue(),
							modelZ1.getNumber().intValue(), modelZ2.getNumber().intValue()
					);
				}
			}
		};
		final JSpinner spinnerStartX = new JSpinner(modelX1);
		spinnerStartX.addChangeListener(l);
		final JSpinner spinnerStartY = new JSpinner(modelY1);
		spinnerStartY.addChangeListener(l);
		final JSpinner spinnerStartZ = new JSpinner(modelZ1);
		spinnerStartZ.addChangeListener(l);
		final JSpinner spinnerEndX = new JSpinner(modelX2);
		spinnerEndX.addChangeListener(l);
		final JSpinner spinnerEndY = new JSpinner(modelY2);
		spinnerEndY.addChangeListener(l);
		final JSpinner spinnerEndZ = new JSpinner(modelZ2);
		spinnerEndZ.addChangeListener(l);

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
																				.addComponent(spinnerStartY, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(labelZ)
																				.addGap(18, 18, 18)
																				.addComponent(spinnerStartZ, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(labelX)
																				.addGap(18, 18, 18)
																				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
																						.addComponent(labelStart)
																						.addComponent(spinnerStartX, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))))
																.addGap(18, 18, 18)
																.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
																		.addComponent(labelStop)
																		.addComponent(labelTarget)
																		.addComponent(spinnerEndX, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
																		.addComponent(spinnerEndY, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
																		.addComponent(spinnerEndZ, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))))
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
										.addComponent(spinnerStartX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(spinnerEndX, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(labelY)
										.addComponent(spinnerStartY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(spinnerEndY, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(labelZ)
										.addComponent(spinnerStartZ, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addComponent(spinnerEndZ, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
