package org.powerbot.gui;

import javax.swing.JDialog;

public class BotBoundingUtility extends JDialog {
	private final BotChrome parent;

	public BotBoundingUtility(final BotChrome parent) {
		this.parent = parent;

		javax.swing.JButton jButton1;
		javax.swing.JButton jButton2;
		javax.swing.JButton jButton3;
		javax.swing.JComboBox jComboBox1;
		javax.swing.JLabel jLabel1;
		javax.swing.JLabel jLabel3;
		javax.swing.JLabel jLabel4;
		javax.swing.JLabel jLabel5;
		javax.swing.JLabel jLabel6;
		javax.swing.JLabel jLabel7;
		javax.swing.JLabel jLabel8;
		javax.swing.JSpinner jSpinner1;
		javax.swing.JSpinner jSpinner2;
		javax.swing.JSpinner jSpinner3;
		javax.swing.JSpinner jSpinner4;
		javax.swing.JSpinner jSpinner5;
		javax.swing.JSpinner jSpinner6;
		javax.swing.JToggleButton jToggleButton1;

		jLabel1 = new javax.swing.JLabel();
		jComboBox1 = new javax.swing.JComboBox();
		jButton1 = new javax.swing.JButton();
		jButton2 = new javax.swing.JButton();
		jToggleButton1 = new javax.swing.JToggleButton();
		jLabel3 = new javax.swing.JLabel();
		jLabel4 = new javax.swing.JLabel();
		jLabel5 = new javax.swing.JLabel();
		jLabel6 = new javax.swing.JLabel();
		jLabel7 = new javax.swing.JLabel();
		jLabel8 = new javax.swing.JLabel();
		jSpinner1 = new javax.swing.JSpinner();
		jSpinner2 = new javax.swing.JSpinner();
		jSpinner3 = new javax.swing.JSpinner();
		jSpinner4 = new javax.swing.JSpinner();
		jSpinner5 = new javax.swing.JSpinner();
		jSpinner6 = new javax.swing.JSpinner();
		jButton3 = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

		jLabel1.setText("Choose type:");

		jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Item 1", "Item 2", "Item 3", "Item 4"}));

		jButton1.setText("Exit");

		jButton2.setLabel("Copy to Clipboard");

		jToggleButton1.setText("Toggle Select");

		jLabel3.setText("X");

		jLabel4.setText("Y");

		jLabel5.setText("Z");

		jLabel6.setText("Start");

		jLabel7.setText("Stop");

		jLabel8.setText("Target: unknown");

		jButton3.setText("Reset");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
												.addGap(0, 0, Short.MAX_VALUE)
												.addComponent(jButton3)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jToggleButton1)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jButton2)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(jButton1)
												.addGap(10, 10, 10))
										.addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
														.addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
														.addGroup(layout.createSequentialGroup()
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(jLabel4)
																				.addGap(18, 18, 18)
																				.addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(jLabel5)
																				.addGap(18, 18, 18)
																				.addComponent(jSpinner3, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
																		.addGroup(layout.createSequentialGroup()
																				.addComponent(jLabel3)
																				.addGap(18, 18, 18)
																				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																						.addComponent(jLabel6)
																						.addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
																.addGap(18, 18, 18)
																.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
																		.addComponent(jLabel7)
																		.addComponent(jLabel8)
																		.addComponent(jSpinner4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
																		.addComponent(jSpinner6, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))))
												.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addGroup(layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel1)
										.addComponent(jLabel8))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
								.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jLabel7))
								.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel3)
										.addComponent(jSpinner1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jSpinner4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel4)
										.addComponent(jSpinner2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jSpinner5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGap(18, 18, 18)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jLabel5)
										.addComponent(jSpinner3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(jSpinner6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
										.addComponent(jButton1)
										.addComponent(jButton2)
										.addComponent(jToggleButton1)
										.addComponent(jButton3))
								.addContainerGap())
		);

		pack();
	}
}
