package org.powerbot.script.xenon.wrappers;

import java.awt.Point;
import java.awt.Rectangle;

import org.powerbot.bot.Bot;
import org.powerbot.game.client.Client;
import org.powerbot.game.client.RSInterface;
import org.powerbot.game.client.RSInterfaceNode;
import org.powerbot.script.internal.wrappers.HashTable;
import org.powerbot.script.xenon.Widgets;
import org.powerbot.script.xenon.util.Random;

public class Component extends Interactive {
	private final Widget widget;
	private final Component parent;
	private final int index;

	public Component(final Widget widget, final int index) {
		this(widget, null, index);
	}

	public Component(final Widget widget, final Component parent, final int index) {
		this.widget = widget;
		this.parent = parent;
		this.index = index;
	}

	public Widget getWidget() {
		return this.widget;
	}

	public Component getParent() {
		return this.parent;
	}

	public int getIndex() {
		return this.index;
	}

	public Component[] getChildren() {
		final RSInterface component = getInternalComponent();
		final RSInterface[] interfaces;
		if (component != null && (interfaces = component.getComponents()) != null) {
			final Component[] components = new Component[interfaces.length];
			for (int i = 0; i < interfaces.length; i++) components[i] = new Component(widget, this, i);
			return components;
		}
		return new Component[0];
	}

	public int getChildrenCount() {
		final RSInterface component = getInternalComponent();
		final RSInterface[] interfaces;
		if (component != null && (interfaces = component.getComponents()) != null) return interfaces.length;
		return 0;
	}

	public Component getChild(final int index) {
		final Component[] components = getChildren();
		return index >= 0 && index < components.length ? components[index] : null;
	}

	public String[] getActions() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getActions() : null;
	}

	public int getTextureId() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getTextureID() : -1;
	}

	public int getBorderThickness() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getBorderThinkness() : -1;
	}

	public int getId() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getID() : -1;
	}

	public int getChildIndex() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getComponentIndex() : -1;
	}

	public String getItemName() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getComponentName() : null;
	}

	public int getItemId() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getComponentID() : -1;
	}

	public int getItemStackSize() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getComponentStackSize() : -1;
	}

	public int getModelId() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getModelID() : -1;
	}

	public int getModelType() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getModelType() : -1;
	}

	public int getModelZoom() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getModelZoom() : -1;
	}

	public int getParentId() {
		final Client client = Bot.client();
		final RSInterface component = getInternalComponent();
		if (client == null || component == null) return -1;

		final int pId = component.getParentID();
		if (pId != -1) return pId;

		final int containerId = getId() >>> 16;
		final HashTable ncI = new HashTable(client.getRSInterfaceNC());
		for (RSInterfaceNode node = (RSInterfaceNode) ncI.first(); node != null; node = (RSInterfaceNode) ncI.next()) {
			if (containerId == node.getMainID()) {
				return (int) node.getId();
			}
		}

		return -1;
	}

	public Point getAbsoluteLocation() {
		final Client client = Bot.client();
		final RSInterface component = getInternalComponent();
		if (client == null || component == null) return new Point(-1, -1);
		final int pId = getParentId();
		int x = 0, y = 0;
		if (pId != -1) {
			final Point point = Widgets.get(pId >> 16, pId & 0xffff).getAbsoluteLocation();
			x = point.x;
			y = point.y;
		} else {
			final Rectangle[] bounds = client.getRSInterfaceBoundsArray();
			final int index = component.getBoundsArrayIndex();
			if (bounds != null && index > 0 && index < bounds.length && bounds[index] != null) {
				return new Point(bounds[index].x, bounds[index].y);
			}
			//x = getMasterX();
			//y = getMasterY();
		}
		if (pId != -1) {
			final Component child = Widgets.get(pId >> 16, pId & 0xffff);
			final int horizontalScrollSize = child.getMaxHorizontalScroll(), verticalScrollSize = child.getMaxVerticalScroll();
			if (horizontalScrollSize > 0 || verticalScrollSize > 0) {
				x -= child.getScrollX();
				y -= child.getScrollY();
			}
		}
		x += component.getX();
		y += component.getY();
		return new Point(x, y);
	}

	public Point getRelativeLocation() {
		final RSInterface component = getInternalComponent();
		return component != null ? new Point(component.getX(), component.getY()) : new Point(-1, -1);
	}

	public String getSelectedAction() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getSelectedActionName() : null;
	}

	public int getShadowColor() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getShadowColor() : -1;
	}

	public int getContentType() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getSpecialType() : -1;
	}

	public String getText() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getText() : null;
	}

	public int getTextColor() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getTextColor() : -1;
	}

	public String getTooltip() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getTooltip() : null;
	}

	public int getType() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getType() : -1;
	}

	public int getWidth() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getWidth() : -1;
	}

	public int getHeight() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getHeight() : -1;
	}

	public int getXRotation() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getXRotation() : -1;
	}

	public int getYRotation() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getYRotation() : -1;
	}

	public int getZRotation() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getZRotation() : -1;
	}

	public boolean isVerticallyFlipped() {
		final RSInterface component = getInternalComponent();
		return component != null && component.isVerticallyFlipped();
	}

	public boolean isHorizontallyFlipped() {
		final RSInterface component = getInternalComponent();
		return component != null && component.isHorizontallyFlipped();
	}

	public int getScrollX() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getHorizontalScrollbarPosition() : -1;
	}

	public int getMaxHorizontalScroll() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getHorizontalScrollbarSize() : -1;
	}

	public int getScrollWidth() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getHorizontalScrollbarThumbSize() : -1;
	}

	public int getScrollY() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getVerticalScrollbarPosition() : -1;
	}

	public int getMaxVerticalScroll() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getVerticalScrollbarSize() : -1;
	}

	public int getScrollHeight() {
		final RSInterface component = getInternalComponent();
		return component != null ? component.getVerticalScrollbarThumbSize() : -1;
	}

	public boolean isInventory() {
		final RSInterface component = getInternalComponent();
		return component != null && component.isInventoryInterface();
	}

	public boolean isVisible() {
		final RSInterface internal = getInternalComponent();
		int id = 0;
		if (internal != null && isValid() && !internal.isHidden()) id = getParentId();
		return id == -1 || Widgets.get(id >> 16, id & 0xffff).isVisible();
	}

	public Rectangle getArea() {
		return getInteractRectangle();
	}

	@Override
	public Point getInteractPoint() {
		return getNextPoint();
	}

	@Override
	public Point getNextPoint() {
		final Rectangle interact = getInteractRectangle();
		return interact != null ? new Point(
				Random.nextGaussian(interact.x, interact.x + interact.width, interact.width / 10),
				Random.nextGaussian(interact.y, interact.y + interact.height, interact.height / 10)
		) : null;
	}

	@Override
	public Point getCenterPoint() {
		final Rectangle interact = getInteractRectangle();
		return interact != null ? new Point((int) interact.getCenterX(), (int) interact.getCenterY()) : null;
	}

	@Override
	public boolean contains(final Point point) {
		final Rectangle interact = getInteractRectangle();
		return interact != null && interact.contains(point);
	}

	@Override
	public boolean isValid() {
		final RSInterface internal = getInternalComponent();
		return internal != null && (parent == null || parent.isVisible()) &&
				getId() != -1 && internal.getBoundsArrayIndex() != -1;
	}

	private Rectangle getInteractRectangle() {
		final Point absLocation = getAbsoluteLocation();
		if (absLocation.x == -1 && absLocation.y == -1) return null;
		final boolean canScroll = isInScrollableArea();
		final Rectangle r = new Rectangle(absLocation.x + 1, absLocation.y + 1,
				(canScroll ? getWidth() : getScrollWidth()) - 1,
				(canScroll ? getHeight() : getScrollHeight()) - 1
		);
		return r.x > 0 && r.y > 0 && r.width > 0 && r.height > 0 ? r : null;
	}

	private boolean isInScrollableArea() {
		int pId = getParentId();
		if (pId == -1) return false;

		Component scrollableArea = Widgets.get(pId >> 16, pId & 0xffff);
		while (scrollableArea.getMaxVerticalScroll() == 0 && (pId = scrollableArea.getParentId()) != -1) {
			scrollableArea = Widgets.get(pId >> 16, pId & 0xffff);
		}

		return scrollableArea.getMaxVerticalScroll() != 0;
	}

	private RSInterface getInternalComponent() {
		RSInterface[] components;
		if (parent != null) {
			final RSInterface parentComponent = parent.getInternalComponent();
			components = parentComponent != null ? parentComponent.getComponents() : null;
		} else {
			components = widget.getInternalComponents();
		}
		return components != null && index < components.length ? components[index] : null;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == null || !(o instanceof Component)) return false;
		final Component c = (Component) o;
		return c.widget.equals(widget) && c.index == index && (parent == null || parent.equals(c.parent));
	}
}
