package org.powerbot.gui;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.powerbot.script.Drawable;
import org.powerbot.script.PaintListener;
import org.powerbot.script.Tile;
import org.powerbot.script.rt6.Actor;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.GameObject;
import org.powerbot.script.rt6.GroundItem;
import org.powerbot.script.rt6.Interactive;
import org.powerbot.script.rt6.Model;
import org.powerbot.script.rt6.Npc;
import org.powerbot.script.rt6.Player;
import org.powerbot.script.rt6.Renderable;
import org.powerbot.script.rt6.TileMatrix;

class RT6BotBoundingUtility extends JFrame implements PaintListener, MouseListener, MouseMotionListener {
	private static final AtomicReference<RT6BotBoundingUtility> instance = new AtomicReference<RT6BotBoundingUtility>(null);
	private final JLabel labelTarget;
	private final SpinnerNumberModel
			modelX1 = new SpinnerNumberModel(-256, -5120, 5120, 4),
			modelY1 = new SpinnerNumberModel(-512, -5120, 5120, 4),
			modelZ1 = new SpinnerNumberModel(-256, -5120, 5120, 4),
			modelX2 = new SpinnerNumberModel(256, -5120, 5120, 4),
			modelY2 = new SpinnerNumberModel(0, -5120, 5120, 4),
			modelZ2 = new SpinnerNumberModel(256, -5120, 5120, 4);
	private final AtomicBoolean selecting;
	private final Point point;
	private final ChangeListener l;
	private TargetSelection<Interactive> selection;
	private Interactive target;

	public static synchronized RT6BotBoundingUtility getInstance(final BotChrome chrome) {
		if (instance.get() == null) {
			instance.set(new RT6BotBoundingUtility(chrome));
		}
		return instance.get();
	}

	@SuppressWarnings("unchecked")
	private RT6BotBoundingUtility(final BotChrome chrome) {
		selecting = new AtomicBoolean(false);
		point = new Point(-1, -1);
		selection = null;
		target = null;

		setTitle("Bounding Utility");
		final JLabel labelType = new JLabel("Choose type:");
		final JLabel labelX = new JLabel("X");
		final JLabel labelY = new JLabel("Y");
		final JLabel labelZ = new JLabel("Z");
		final JLabel labelStart = new JLabel("Start");
		final JLabel labelStop = new JLabel("Stop");
		labelTarget = new JLabel("Target: Block input to begin selection");

		final JComboBox comboBoxTarget = new JComboBox();
		final DefaultComboBoxModel m = new DefaultComboBoxModel(new TargetSelection[]{
				new TargetSelection<Player>("Player", new Callable<Player>() {
					@Override
					public Player call() {
						final ClientContext ctx = (ClientContext) chrome.bot.get().ctx;
						return (Player) nearest(ctx.players.select());
					}
				}),
				new TargetSelection<Npc>("Npc", new Callable<Npc>() {
					@Override
					public Npc call() {
						final ClientContext ctx = (ClientContext) chrome.bot.get().ctx;
						return (Npc) nearest(ctx.npcs.select());
					}
				}),
				new TargetSelection<GameObject>("Object", new Callable<GameObject>() {
					@Override
					public GameObject call() {
						final ClientContext ctx = (ClientContext) chrome.bot.get().ctx;
						return (GameObject) nearest(ctx.objects.select().within(10d));
					}
				}),
				new TargetSelection<GroundItem>("Ground Item", new Callable<GroundItem>() {
					@Override
					public GroundItem call() {
						final ClientContext ctx = (ClientContext) chrome.bot.get().ctx;
						return (GroundItem) nearest(ctx.groundItems.select());
					}
				}),
				new TargetSelection<TileMatrix>("Tile", new Callable<TileMatrix>() {
					@Override
					public TileMatrix call() {
						final ClientContext ctx = (ClientContext) chrome.bot.get().ctx;
						final List<TileMatrix> list = new ArrayList<TileMatrix>();
						final Tile t = ctx.players.local().tile();
						for (int x = -20; x <= 20; x++) {
							for (int y = -20; y <= 20; y++) {
								list.add(new TileMatrix(ctx, t.derive(x, y)));
							}
						}
						return (TileMatrix) nearest(list);
					}
				})
		});
		comboBoxTarget.setModel(m);
		final Object o = m.getElementAt(0);
		m.setSelectedItem(o);
		selection = (TargetSelection<Interactive>) o;
		comboBoxTarget.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				selection = (TargetSelection<Interactive>) comboBoxTarget.getSelectedItem();
			}
		});

		l = new ChangeListener() {
			@Override
			public void stateChanged(final ChangeEvent changeEvent) {
				if (target != null) {
					target.bounds(
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
		l.stateChanged(null);

		final JButton buttonCopy = new JButton("Output");
		buttonCopy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				final String str = String.format(
						"final int[] bounds = {%s, %s, %s, %s, %s, %s};",
						modelX1.getNumber().intValue(), modelX2.getNumber().intValue(),
						modelY1.getNumber().intValue(), modelY2.getNumber().intValue(),
						modelZ1.getNumber().intValue(), modelZ2.getNumber().intValue()
				);
				Logger.getLogger(getTitle()).info(str);
			}
		});
		final JButton buttonSelect = new JButton("Begin Select");
		buttonSelect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				selecting.set(true);
			}
		});
		final JButton buttonReset = new JButton("Reset");
		buttonReset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent actionEvent) {
				set(target);
			}
		});

		chrome.bot.get().dispatcher.add(this);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				setVisible(false);
				chrome.bot.get().dispatcher.remove(RT6BotBoundingUtility.this);
				dispose();
				instance.set(null);
			}
		});

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
												.addComponent(buttonSelect)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
												.addComponent(buttonCopy)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
										.addComponent(buttonCopy)
										.addComponent(buttonSelect)
										.addComponent(buttonReset))
								.addContainerGap())
		);

		pack();
		setLocationRelativeTo(chrome);
	}

	private Interactive nearest(final Iterable<? extends Interactive> list) {
		Interactive r = null;
		double d = Double.MAX_VALUE;
		for (final Interactive interactive : list) {
			final Point p = interactive.centerPoint();
			final double d2 = p.distance(point);
			if (d2 < d) {
				d = d2;
				r = interactive;
			}
		}
		return r;
	}

	private void set(final Interactive interactive) {
		if (interactive == null) {
			return;
		}
		final Model m;
		if (interactive instanceof Renderable && (m = ((Renderable) interactive).model()) != null) {
			m.update();
			int x1 = 0, y1 = 0, z1 = 0;
			int x2 = 0, y2 = 0, z2 = 0;
			for (int i = 0; i < m.numFaces; i++) {
				final int a = m.faceA[i], b = m.faceB[i], c = m.faceC[i];
				final int x = m.xPoints[a], y = m.yPoints[b], z = m.zPoints[c];
				x1 = Math.min(x1, x);
				x2 = Math.max(x2, x);
				y1 = Math.min(y1, y);
				y2 = Math.max(y2, y);
				z1 = Math.min(z1, z);
				z2 = Math.max(z2, z);
			}
			modelX1.setValue(x1);
			modelX2.setValue(x2);
			modelY1.setValue(y1);
			modelY2.setValue(y2);
			modelZ1.setValue(z1);
			modelZ2.setValue(z2);
		} else {
			if (interactive instanceof Actor) {
				modelX1.setValue(-192);
				modelX2.setValue(192);
				modelY1.setValue(-768);
				modelY2.setValue(0);
				modelZ1.setValue(-192);
				modelZ2.setValue(192);
			} else if (interactive instanceof GameObject) {
				modelX1.setValue(-128);
				modelX2.setValue(128);
				modelY1.setValue(-256);
				modelY2.setValue(0);
				modelZ1.setValue(-128);
				modelZ2.setValue(128);
			} else if (interactive instanceof GroundItem) {
				modelX1.setValue(-64);
				modelX2.setValue(64);
				modelY1.setValue(-64);
				modelY2.setValue(0);
				modelZ1.setValue(-64);
				modelZ2.setValue(64);
			} else {
				modelX1.setValue(-128);
				modelX2.setValue(128);
				modelY1.setValue(-256);
				modelY2.setValue(0);
				modelZ1.setValue(-128);
				modelZ2.setValue(128);
			}
		}
		l.stateChanged(null);
	}

	@Override
	public void repaint(final Graphics render) {
		if (target != null && target instanceof Drawable) {
			((Drawable) target).draw(render, 64);
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e) {
	}

	@Override
	public void mousePressed(final MouseEvent e) {
		mouseMoved(e);
		selecting.set(false);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
	}

	@Override
	public void mouseEntered(final MouseEvent e) {
	}

	@Override
	public void mouseExited(final MouseEvent e) {
	}

	@Override
	public void mouseDragged(final MouseEvent e) {
	}

	@Override
	public void mouseMoved(final MouseEvent e) {
		if (!selecting.get()) {
			return;
		}
		point.move(e.getX(), e.getY());
		if (selection != null) {
			try {
				target = selection.callable.call();
			} catch (final Exception ignored) {
				target = null;
			}
			labelTarget.setText("Target: " + target);
			pack();
			set(target);
		}
	}

	private final class TargetSelection<K> {
		private final String str;
		public final Callable<K> callable;

		private TargetSelection(final String str, final Callable<K> callable) {
			this.str = str;
			this.callable = callable;
		}

		@Override
		public String toString() {
			return str;
		}
	}
}
