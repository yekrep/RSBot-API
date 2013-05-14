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
import java.util.List;

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
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.powerbot.bot.Bot;
import org.powerbot.event.PaintListener;
import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.wrappers.Component;
import org.powerbot.script.xenon.wrappers.Widget;
import org.powerbot.util.Tracker;
import org.powerbot.util.io.Resources;

/**
 * @author Timer
 */
public class BotWidgetExplorer extends JFrame implements PaintListener {
	private static final long serialVersionUID = 1L;
	private static BotWidgetExplorer instance;
	private final JTree tree;
	private final WidgetTreeModel treeModel;
	private JPanel infoArea;
	private JTextField searchBox;
	private Rectangle highlightArea = null;

	public BotWidgetExplorer() {
		super("Widget Explorer");
		setIconImage(Resources.getImage(Resources.Paths.EDIT));
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				setVisible(false);
				Bot.getInstance().getEventMulticaster().removeListener(this);
				highlightArea = null;
			}
		});
		treeModel = new WidgetTreeModel();
		treeModel.update("");
		tree = new JTree(treeModel);
		tree.setRootVisible(false);
		tree.setEditable(false);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
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
						highlightArea = ((ComponentWrapper) node).get().getBoundingRect();
						c = ((ComponentWrapper) node).get();
					}
					if (c == null) {
						return;
					}
					addInfo("Index: ", Integer.toString(c.getIndex()));
					addInfo("Validated: ", Boolean.toString(c.isValid()));
					addInfo("Visible: ", Boolean.toString(c.isVisible()));
					addInfo("Absolute location: ", c.getAbsoluteLocation().toString());
					addInfo("Relative location: ", c.getRelativeLocation().toString());
					addInfo("Width: ", Integer.toString(c.getWidth()));
					addInfo("Height: ", Integer.toString(c.getHeight()));
					addInfo("Id: ", Integer.toString(c.getId()));
					addInfo("Type: ", Integer.toString(c.getType()));
					addInfo("Special type: ", Integer.toString(c.getContentType()));
					addInfo("Child id: ", Integer.toString(c.getItemId()));
					addInfo("Child index: ", Integer.toString(c.getChildIndex()));
					addInfo("Texture id: ", Integer.toString(c.getTextureId()));
					addInfo("Text: ", c.getText());
					addInfo("Text color: ", Integer.toString(c.getTextColor()));
					addInfo("Shadow color: ", Integer.toString(c.getShadowColor()));
					addInfo("Tooltip: ", c.getTooltip());
					addInfo("Border thickness: ", Integer.toString(c.getBorderThickness()));
					addInfo("Selected action: ", c.getSelectedAction());
					addInfo("Model id: ", Integer.toString(c.getModelId()));
					addInfo("Model type: ", Integer.toString(c.getModelType()));
					addInfo("Model zoom: ", Integer.toString(c.getModelZoom()));
					addInfo("Inventory: ", Boolean.toString(c.isInventory()));
					addInfo("Child stack size: ", Integer.toString(c.getItemStackSize()));
					addInfo("Parent id: ", Integer.toString(c.getParentId()));
					addInfo("getHorizontalScrollPosition: ", Integer.toString(c.getScrollX()));
					addInfo("getVerticalScrollPosition: ", Integer.toString(c.getScrollY()));
					addInfo("getScrollableContentWidth: ", Integer.toString(c.getMaxHorizontalScroll()));
					addInfo("getScrollableContentHeight: ", Integer.toString(c.getMaxVerticalScroll()));
					addInfo("getHorizontalScrollThumbSize: ", Integer.toString(c.getScrollWidth()));
					addInfo("getVerticalScrollThumbSize: ", Integer.toString(c.getScrollHeight()));
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

		Tracker.getInstance().trackPage("widgetexplorer/", getTitle());
	}

	private static BotWidgetExplorer getInstance() {
		if (instance == null) {
			instance = new BotWidgetExplorer();
		}
		return instance;
	}

	public static void display() {
		final BotWidgetExplorer botWidgetExplorer = getInstance();
		if (botWidgetExplorer.isVisible()) {
			Bot.getInstance().getEventMulticaster().removeListener(botWidgetExplorer);
			botWidgetExplorer.highlightArea = null;
		}
		botWidgetExplorer.treeModel.update("");
		Bot.getInstance().getEventMulticaster().addListener(botWidgetExplorer);
		botWidgetExplorer.setVisible(true);
	}

	public void onRepaint(final Graphics g) {
		if (highlightArea != null) {
			g.setColor(Color.orange);
			g.drawRect(highlightArea.x, highlightArea.y, highlightArea.width, highlightArea.height);
		}
	}

	private final class WidgetTreeModel implements TreeModel {
		private final Object root = new Object();
		private final List<TreeModelListener> treeModelListeners = new ArrayList<>();
		private final List<WidgetWrapper> widgetWrappers = new ArrayList<>();

		public Object getRoot() {
			return root;
		}

		public Object getChild(final Object parent, final int index) {
			try {
				if (parent == root) {
					return widgetWrappers.get(index);
				} else if (parent instanceof WidgetWrapper) {
					return new ComponentWrapper(((WidgetWrapper) parent).get().getComponents()[index]);
				} else if (parent instanceof ComponentWrapper) {
					return new ComponentWrapper(((ComponentWrapper) parent).get().getChildren()[index]);
				}
				return null;
			} finally {
			}
		}

		public int getChildCount(final Object parent) {
			try {
				if (parent == root) {
					return widgetWrappers.size();
				} else if (parent instanceof WidgetWrapper) {
					return ((WidgetWrapper) parent).get().getComponents().length;
				} else if (parent instanceof ComponentWrapper) {
					return ((ComponentWrapper) parent).get().getChildren().length;
				}
				return 0;
			} finally {
			}
		}

		public boolean isLeaf(final Object node) {
			try {
				return node instanceof ComponentWrapper && ((ComponentWrapper) node).get().getChildren().length == 0;
			} finally {
			}
		}

		public void valueForPathChanged(TreePath path, Object newValue) {
		}

		public int getIndexOfChild(final Object parent, final Object child) {
			try {
				if (parent == root) {
					return widgetWrappers.indexOf(child);
				} else if (parent instanceof WidgetWrapper) {
					return Arrays.asList(((WidgetWrapper) parent).get().getComponents()).indexOf(((ComponentWrapper) child).get());
				} else if (parent instanceof ComponentWrapper) {
					return Arrays.asList(((ComponentWrapper) parent).get().getChildren()).indexOf(((ComponentWrapper) child).get());
				}
				return -1;
			} finally {
			}
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
			for (final Widget widget : Widgets.getLoaded()) {
				children:
				for (final Component Component : widget.getComponents()) {
					if (search(Component, search)) {
						widgetWrappers.add(new WidgetWrapper(widget));
						break;
					}
					for (final Component widgetSubChild : Component.getChildren()) {
						if (search(widgetSubChild, search)) {
							widgetWrappers.add(new WidgetWrapper(widget));
							break children;
						}
					}
				}
			}
			fireTreeStructureChanged(root);
		}

		private boolean search(final Component child, final String string) {
			try {
				return child.getText().toLowerCase().contains(string.toLowerCase());
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
			return "Widget-" + widget.getIndex();
		}
	}

	private final class ComponentWrapper {
		private final Component Component;

		public ComponentWrapper(final Component Component) {
			this.Component = Component;
		}

		public Component get() {
			return Component;
		}

		@Override
		public boolean equals(final Object object) {
			return object != null && object instanceof ComponentWrapper && Component.equals(((ComponentWrapper) object).get());
		}

		@Override
		public String toString() {
			return "Component-" + Component.getIndex();
		}
	}
}
