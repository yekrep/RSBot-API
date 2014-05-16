package org.powerbot.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.powerbot.misc.GoogleAnalytics;
import org.powerbot.script.PaintListener;
import org.powerbot.script.rt6.ClientContext;
import org.powerbot.script.rt6.Component;
import org.powerbot.script.rt6.Widget;

class RT6WidgetExplorer extends JFrame implements PaintListener {
	private static final long serialVersionUID = 3674322588956559479L;
	private static final Map<BotLauncher, RT6WidgetExplorer> instances = new HashMap<BotLauncher, RT6WidgetExplorer>();
	private final BotLauncher launcher;
	private final JTree tree;
	private final WidgetTreeModel treeModel;
	private JPanel infoArea;
	private JTextField searchBox;
	private Rectangle highlightArea = null;
	private final List<Component> list = new ArrayList<Component>();

	private RT6WidgetExplorer(final BotLauncher launcher) {
		super("Widget Explorer");
		this.launcher = launcher;
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				setVisible(false);
				launcher.bot.get().dispatcher.remove(RT6WidgetExplorer.this);
				highlightArea = null;
				dispose();
				instances.remove(launcher);
			}
		});
		treeModel = new WidgetTreeModel();
		treeModel.update("");
		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = 2674122583955569479L;

			@Override
			public java.awt.Component getTreeCellRendererComponent(final JTree tree,
			                                                       final Object value, final boolean selected, final boolean expanded,
			                                                       final boolean leaf, final int row, final boolean hasFocus) {
				super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
				this.setForeground(Color.black);
				if (value instanceof ComponentWrapper) {
					if (((ComponentWrapper) value).isHit()) {
						this.setForeground(Color.red);
					}
				}
				return this;
			}
		});
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(final TreeSelectionEvent e) {
				try {
					final Object node = tree.getLastSelectedPathComponent();
					if (node == null || node instanceof WidgetWrapper) {
						return;
					}
					infoArea.removeAll();
					Component c = null;
					if (node instanceof ComponentWrapper) {
						highlightArea = ((ComponentWrapper) node).get().viewportRect();
						c = ((ComponentWrapper) node).get();
					}
					if (c == null) {
						return;
					}
					addInfo("Index: ", Integer.toString(c.index()));
					addInfo("Validated: ", Boolean.toString(c.valid()));
					addInfo("Visible: ", Boolean.toString(c.visible()));
					addInfo("Absolute location: ", c.screenPoint().toString());
					addInfo("Relative location: ", c.relativePoint().toString());
					addInfo("Width: ", Integer.toString(c.width()));
					addInfo("Height: ", Integer.toString(c.height()));
					addInfo("Id: ", Integer.toString(c.id()));
					addInfo("Type: ", Integer.toString(c.type()));
					addInfo("Special type: ", Integer.toString(c.contentType()));
					addInfo("Child id: ", Integer.toString(c.itemId()));
					addInfo("Child index: ", Integer.toString(c.itemIndex()));
					addInfo("Texture id: ", Integer.toString(c.textureId()));
					addInfo("Text: ", c.text());
					addInfo("Text color: ", Integer.toString(c.textColor()));
					addInfo("Shadow color: ", Integer.toString(c.shadowColor()));
					addInfo("Tooltip: ", c.tooltip());
					addInfo("Border thickness: ", Integer.toString(c.borderThickness()));
					addInfo("Selected action: ", c.selectedAction());
					addInfo("Model id: ", Integer.toString(c.modelId()));
					addInfo("Model type: ", Integer.toString(c.modelType()));
					addInfo("Model zoom: ", Integer.toString(c.modelZoom()));
					addInfo("Inventory: ", Boolean.toString(c.inventory()));
					addInfo("Child stack size: ", Integer.toString(c.itemStackSize()));
					addInfo("Parent id: ", Integer.toString(c.parentId()));
					addInfo("getHorizontalScrollPosition: ", Integer.toString(c.scrollX()));
					addInfo("getVerticalScrollPosition: ", Integer.toString(c.scrollY()));
					addInfo("getScrollableContentWidth: ", Integer.toString(c.scrollWidthMax()));
					addInfo("getScrollableContentHeight: ", Integer.toString(c.scrollHeightMax()));
					addInfo("getHorizontalScrollThumbSize: ", Integer.toString(c.scrollWidth()));
					addInfo("getVerticalScrollThumbSize: ", Integer.toString(c.scrollHeight()));
					infoArea.validate();
					infoArea.repaint();
				} catch (final Exception ignored) {
				}
			}

			private void addInfo(final String key, final String value) {
				final JPanel row = new JPanel();
				row.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
				row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
				for (final String data : new String[]{key, value}) {
					final JLabel label = new JLabel(data);
					label.setAlignmentY(java.awt.Component.TOP_ALIGNMENT);
					row.add(label);
				}
				infoArea.add(row);
			}
		});
		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setPreferredSize(new Dimension(250, 500));
		add(scrollPane, BorderLayout.WEST);

		infoArea = new JPanel();
		infoArea.setLayout(new BoxLayout(infoArea, BoxLayout.Y_AXIS));
		scrollPane = new JScrollPane(infoArea);
		scrollPane.setPreferredSize(new Dimension(250, 500));
		add(scrollPane, BorderLayout.CENTER);

		final ActionListener actionListener = new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				treeModel.update(searchBox.getText());
				infoArea.removeAll();
				infoArea.validate();
				infoArea.repaint();
			}
		};

		final JPanel toolArea = new JPanel();
		toolArea.setLayout(new FlowLayout(FlowLayout.LEFT));
		toolArea.add(new JLabel("Filter:"));

		searchBox = new JTextField(20);
		searchBox.addActionListener(actionListener);
		toolArea.add(searchBox);

		final JButton updateButton = new JButton("Update");
		updateButton.addActionListener(actionListener);
		toolArea.add(updateButton);
		add(toolArea, BorderLayout.NORTH);

		pack();
		setLocationRelativeTo(getOwner());
		setVisible(false);

		GoogleAnalytics.getInstance().pageview("widgetexplorer/", getTitle());
	}

	public static synchronized RT6WidgetExplorer getInstance(final BotLauncher launcher) {
		if (!instances.containsKey(launcher)) {
			instances.put(launcher, new RT6WidgetExplorer(launcher));
		}
		return instances.get(launcher);
	}

	public void display() {
		if (isVisible()) {
			launcher.bot.get().dispatcher.remove(this);
			highlightArea = null;
		}
		treeModel.update("");
		launcher.bot.get().dispatcher.add(this);
		setVisible(true);
	}

	public void repaint(final Graphics g) {
		if (highlightArea != null) {
			g.setColor(Color.orange);
			g.drawRect(highlightArea.x, highlightArea.y, highlightArea.width, highlightArea.height);
		}
	}

	private final class WidgetTreeModel implements TreeModel {
		private final Object root = new Object();
		private final List<TreeModelListener> treeModelListeners = new ArrayList<TreeModelListener>();
		private final List<WidgetWrapper> widgetWrappers = new ArrayList<WidgetWrapper>();

		public Object getRoot() {
			return root;
		}

		public Object getChild(final Object parent, final int index) {
			if (parent == root) {
				return widgetWrappers.get(index);
			} else if (parent instanceof WidgetWrapper) {
				return new ComponentWrapper(((WidgetWrapper) parent).get().components()[index]);
			} else if (parent instanceof ComponentWrapper) {
				return new ComponentWrapper(((ComponentWrapper) parent).get().components()[index]);
			}
			return null;
		}

		public int getChildCount(final Object parent) {
			if (parent == root) {
				return widgetWrappers.size();
			} else if (parent instanceof WidgetWrapper) {
				return ((WidgetWrapper) parent).get().components().length;
			} else if (parent instanceof ComponentWrapper) {
				return ((ComponentWrapper) parent).get().components().length;
			}
			return 0;
		}

		public boolean isLeaf(final Object node) {
			return node instanceof ComponentWrapper && ((ComponentWrapper) node).get().components().length == 0;
		}

		public void valueForPathChanged(final TreePath path, final Object newValue) {
		}

		public int getIndexOfChild(final Object parent, final Object child) {
			if (parent == root) {
				return widgetWrappers.indexOf(child);
			} else if (parent instanceof WidgetWrapper) {
				return Arrays.asList(((WidgetWrapper) parent).get().components()).indexOf(((ComponentWrapper) child).get());
			} else if (parent instanceof ComponentWrapper) {
				return Arrays.asList(((ComponentWrapper) parent).get().components()).indexOf(((ComponentWrapper) child).get());
			}
			return -1;
		}

		public void addTreeModelListener(final TreeModelListener l) {
			treeModelListeners.add(l);
		}

		public void removeTreeModelListener(final TreeModelListener l) {
			treeModelListeners.remove(l);
		}

		private void fireTreeStructureChanged(final Object oldRoot) {
			final TreeModelEvent e = new TreeModelEvent(this, new Object[]{oldRoot});
			for (final TreeModelListener tml : treeModelListeners) {
				tml.treeStructureChanged(e);
			}
		}

		public void update(final String search) {
			widgetWrappers.clear();
			final Widget[] loaded;
			for (final Widget widget : loaded = ((ClientContext) launcher.bot.get().ctx).widgets.array()) {
				children:
				for (final Component Component : widget.components()) {
					if (search(Component, search)) {
						widgetWrappers.add(new WidgetWrapper(widget));
						break;
					}
					for (final Component widgetSubChild : Component.components()) {
						if (search(widgetSubChild, search)) {
							widgetWrappers.add(new WidgetWrapper(widget));
							break children;
						}
					}
				}
			}
			list.clear();
			if (search != null && !search.isEmpty()) {
				for (final Widget widget : loaded) {
					for (final Component child : widget.components()) {
						if (search(child, search)) {
							list.add(child);
						}
						for (final Component child2 : child.components()) {
							if (search(child2, search)) {
								list.add(child2);
							}
						}
					}
				}
			}
			fireTreeStructureChanged(root);
		}

		private boolean search(final Component child, final String string) {
			try {
				return child.text().toLowerCase().contains(string.toLowerCase());
			} catch (final NullPointerException ignored) {
				return false;
			}
		}
	}

	private final class WidgetWrapper {
		private final Widget widget;

		public WidgetWrapper(final Widget widget) {
			this.widget = widget;
		}

		public Widget get() {
			return widget;
		}

		@Override
		public boolean equals(final Object object) {
			return object != null && object instanceof WidgetWrapper && widget.equals(((WidgetWrapper) object).get());
		}

		@Override
		public String toString() {
			return "Widget-" + widget.index();
		}
	}

	private final class ComponentWrapper {
		private final Component component;

		public ComponentWrapper(final Component component) {
			this.component = component;
		}

		public Component get() {
			return component;
		}

		public boolean isHit() {
			return list.contains(component);
		}

		@Override
		public boolean equals(final Object object) {
			return object != null && object instanceof ComponentWrapper && component.equals(((ComponentWrapper) object).get());
		}

		@Override
		public String toString() {
			return "Component-" + component.index();
		}
	}
}
