package org.powerbot.script.rt4;

import org.powerbot.bot.rt4.client.Client;
import org.powerbot.script.Calculations;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import static java.awt.image.ImageObserver.HEIGHT;
import static java.awt.image.ImageObserver.WIDTH;

/**
 * Inventory
 */
public class Inventory extends ItemQuery<Item> {
	public Inventory(final ClientContext ctx) {
		super(ctx);
	}

	@Override
	protected List<Item> get() {
		final List<Item> items = new ArrayList<Item>(Constants.INVENTORY_SIZE);
		final Component comp = component();
		if (comp.componentCount() > 0) {
			for (final Component c : comp.components()) {
				final int id = c.itemId();
				if (id <= -1 || id == 6512 || c.itemStackSize() <= 0) {
					continue;
				}
				items.add(new Item(ctx, c, id, c.itemStackSize()));
			}
			return items;
		}
		final int[] ids = comp.itemIds(), stacks = comp.itemStackSizes();
		for (int i = 0; i < Math.min(ids != null ? ids.length : -1, stacks != null ? stacks.length : -1); i++) {
			final int id = ids[i], stack = stacks[i];
			if (id <= 0) {
				continue;
			}
			items.add(new Item(ctx, comp, i, id, stack));
		}
		return items;
	}

	public Item[] items() {
		final Item[] items = new Item[Constants.INVENTORY_SIZE];
		final Component comp = component();
		if (comp.componentCount() > 0) {
			final Component[] comps = comp.components();
			final int len = comps.length;
			for (int i = 0; i < Constants.INVENTORY_SIZE; i++) {
				if (i >= len) {
					items[i] = nil();
					continue;
				}
				final Component c = comps[i];
				final int id = c.itemId();
				if (id <= -1 || id == 6512 || c.itemStackSize() <= 0) {
					items[i] = nil();
					continue;
				}
				items[i] = new Item(ctx, c, id, c.itemStackSize());
			}
			return items;
		}
		final int[] ids = comp.itemIds(), stacks = comp.itemStackSizes();
		for (int i = 0; i < Math.min(ids != null ? ids.length : -1, stacks != null ? stacks.length : -1); i++) {
			final int id = ids[i], stack = stacks[i];
			if (id >= 1) {
				items[i] = new Item(ctx, comp, i, id, stack);
			} else {
				items[i] = nil();
			}
		}
		return items;
	}

	public Item itemAt(final int index) {
		return index >= 0 && index < Constants.INVENTORY_SIZE ? items()[index] : nil();
	}

	public int selectionType() {
		final Client client = ctx.client();
		return client != null ? client.getSelectionType() : 0;
	}

	public int selectedItemIndex() {
		final Client client = ctx.client();
		return client != null && selectionType() == 1 ? client.getSelectionIndex() : -1;
	}

	public Item selectedItem() {
		final int index = selectedItemIndex();
		return itemAt(index);
	}

	public Component component() {
		Component c;
		for (final int[] alt : Constants.INVENTORY_ALTERNATIVES) {
			if ((c = ctx.widgets.widget(alt[0]).component(alt[1])).valid() && c.visible()) {
				return c;
			}
		}
		return ctx.widgets.widget(Constants.INVENTORY_WIDGET).component(Constants.INVENTORY_ITEMS);
	}

	/**
	 * Drops specified item via regular or shift dropping.
	 *
	 * @param i     The item to drop
	 * @param shift Shift dropping, if true the method will verify it is enabled and fall back to regular if not
	 * @return Success
	 */
	public boolean drop(Item i, boolean shift) {
		if (shift && shiftDroppingEnabled()) {
			return ctx.input.send("{VK_SHIFT down}") && i.click(true) && ctx.input.send("{VK_SHIFT up}");
		} else {
			return i.interact("Drop", i.name());
		}
	}

	/**
	 * Drops specified items, uses shift dropping if enabled
	 *
	 * @param items The items to drop in query form
	 * @return Success
	 */
	public boolean drop(ItemQuery<Item> items) {
		if (shiftDroppingEnabled()) {
			ctx.input.send("{VK_SHIFT down}");
			for (Item i : items) {
				i.click(true);
				Condition.sleep(Random.getDelay());
				if(ctx.controller.isStopping()){
					break;
				}
			}
			ctx.input.send("{VK_SHIFT up}");
			return true;
		} else {
			for (Item i : items) {
				i.interact("Drop", i.name());
				if(ctx.controller.isStopping()){
					break;
				}
			}
			return true;
		}
	}
	
	/**
	 * Finds the centerPoint of the inventory's index
	 * @param index 0-(Constants.INVENTORY_SIZE-1), index of inventory
	 * @return centerPoint of the index param
	 * @throws IndexOutOfBoundsException if index is below 0 or above (Constants.INVENTORY_SIZE-1)
	 */
	public Point indexCenterPoint(int index){
		if(index < 0 || index > Constants.INVENTORY_SIZE){
			throw new IndexOutOfBoundsException();
		}
		
		final Point base = component().screenPoint();
		final int x = base.x - 3 + (index % 4) * WIDTH;
		final int y = base.y - 2 + (index / 4) * HEIGHT;
		return new Point(x + WIDTH / 2, y + HEIGHT / 2);
	}
	
	/**
	 * Finds the boundingRectangle of the desired index, not every area within the rectangle will click the item
	 * @param index 0-(Constants.INVENTORY_SIZE-1), index of inventory
	 * @return boundingRectangle of the index param
	 * @throws IndexOutOfBoundsException if index is below 0 or above (Constants.INVENTORY_SIZE-1)
	 */
	public Rectangle boundingRect(int index){
		if(index < 0 || index > Constants.INVENTORY_SIZE-1){
			throw new IndexOutOfBoundsException();
		}
		
		final Point base = component().screenPoint();
		final int x = base.x - 3 + (index % 4) * WIDTH;
		final int y = base.y - 2 + (index / 4) * HEIGHT;
		
		return new Rectangle(x, y, WIDTH, HEIGHT);
	}
	
	/**
	 * Drags the given item to the given index
	 * 
	 * @param item Item to be dragged
	 * @param index Index to drag the item to
	 * @return True if the item is at the index or the drag was successful, false otherwise
	 * @throws IndexOutOfBoundsException if index is below 0 or above (Constants.INVENTORY_SIZE-1)
	 */
	public boolean drag(final Item item, final int index){
		if(!item.valid()){
			return false;
		}
		
		if(item.inventoryIndex() == index){
			return true;
		}
		
		if(index < 0 || index > Constants.INVENTORY_SIZE-1){
			throw new IndexOutOfBoundsException();
		}
		
		if(!ctx.input.move(item.nextPoint())){
			return false;
		}
		
		final Rectangle r = boundingRect(index);
		final int xOff = r.width / 8, yOff = r.height / 8;
		final Rectangle objectRectangle = new Rectangle(r.x + r.width / 2 - xOff, r.y + r.height / 2 - yOff, r.width / 4, r.height / 4);
		
		return ctx.input.drag(Calculations.nextPoint(r, objectRectangle), true);
	}

	public boolean shiftDroppingEnabled() {
		return ctx.varpbits.varpbit(1055, 17, 0x1) == 1;
	}
	
	public boolean isFull() {
		return ctx.inventory.select().size() > Constants.INVENTORY_SIZE-1;
	}

	@Override
	public Item nil() {
		return new Item(ctx, null, -1, -1, -1);
	}

}
