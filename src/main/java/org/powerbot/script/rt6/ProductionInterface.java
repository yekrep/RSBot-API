package org.powerbot.script.rt6;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Random;

/**
 * ProductionInterface
 * Utilities for interacting with the in-game Production Interface.
 */
public class ProductionInterface extends ClientAccessor {

	public ProductionInterface(final ClientContext ctx) {
		super(ctx);
	}

	private static final int START_BUTTON_COMPONENT = 12;
	private static final int PROGRESS_INTERFACE_COMPONENT = 11;
	private static final int ITEM_SELECTION_PANE_COMPONENT = 44;
	private static final int ITEM_SCROLLBAR_COMPONENT = 47;
	private static final int SELECTED_AMOUNT_COMPONENT = 145;
	private static final int AMOUNT_SCROLL_BAR_COMPONENT = 148;
	private static final int MAX_AMOUNT_COMPONENT = 35;
	private static final int SELECTED_CATEGORY_COMPONENT = 51;
	private static final int CATEGORY_SELECTION_COMBOBOX_COMPONENT = 60;
	private static final int CATEGORY_SELECTION_SCROLL_BAR_COMPONENT = 61;
	private static final int AMOUNT_INPUT_BOX_COMPONENT = 72;

	private static final int MAIN_INTERFACE_WIDGET = 1371;
	private static final int ITEM_INFO_INTERFACE_WIDGET = 1370;
	private static final int PROGRESS_INTERFACE_WIDGET = 1251;
	private static final int CHOOSE_TOOL_WIDGET = 1179;

	private static final int SELECTED_ITEM_VARPBIT = 1170;
	private static final int SELECTED_AMOUNT_VARPBIT = 312;

	private static final int CLOSE_BUTTON_TEXTURE = 5450;

	private static long workingTimeLimit = -1;

	private boolean categoryComboOpened() {
		return ctx.widgets.component(MAIN_INTERFACE_WIDGET, CATEGORY_SELECTION_COMBOBOX_COMPONENT).visible();
	}

	/**
	 * Returns the currently selected item category.
	 *
	 * @return the currently selected item category
	 */
	public String selectedCategory() {
		return ctx.widgets.component(MAIN_INTERFACE_WIDGET, SELECTED_CATEGORY_COMPONENT).component(0).text();
	}

	/**
	 * Selects the desired item category if it's not already selected.
	 *
	 * @param categoryName name of the item category to be selected
	 * @return {@code true} if the desired item category was successfully selected or is already selected; otherwise {@code false}
	 */
	public boolean selectCategory(final String categoryName) {
		if (selectedCategory().toLowerCase().contains(categoryName.toLowerCase())) {
			return true;
		}
		if (!categoryComboOpened()) {
			final Component openTabs = ctx.widgets.component(MAIN_INTERFACE_WIDGET, SELECTED_CATEGORY_COMPONENT);
			if (!openTabs.valid() || !openTabs.click()) {
				return false;
			}

			if (!Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return categoryComboOpened();
				}
			}, 250, 10)) {
				return false;
			}
		}
		final Component comp = findTextComponent(MAIN_INTERFACE_WIDGET, categoryName);
		if (comp == null || !comp.valid()) {
			return false;
		}
		final Component pane = ctx.widgets.component(MAIN_INTERFACE_WIDGET, CATEGORY_SELECTION_COMBOBOX_COMPONENT);
		final Component scrollBar = ctx.widgets.component(MAIN_INTERFACE_WIDGET, CATEGORY_SELECTION_SCROLL_BAR_COMPONENT);
		return ctx.widgets.scroll(comp, pane, scrollBar, true) && comp.click() && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return selectedCategory().toLowerCase().contains(categoryName.toLowerCase());
			}
		}, 350, 10);
	}

	/**
	 * Returns the id of currently selected item.
	 *
	 * @return id of the selected item
	 */

	public int selectedItemId() {
		return ctx.varpbits.varpbit(SELECTED_ITEM_VARPBIT);
	}

	/**
	 * Determines if the item with the provided id is selected.
	 *
	 * @param id of the item
	 * @return {@code true} if the item with the provided id is selected; otherwise {@code false}
	 */
	public boolean itemSelected(final int id) {
		return selectedItemId() == id;
	}

	/**
	 * Returns if the Production Interface is opened or not.
	 *
	 * @return {@code true} if the Production interface is opened; otherwise {@code false}
	 */
	public boolean opened() {
		return selectedItemId() != -1;
	}

	/**
	 * Starts making the selected item.
	 *
	 * @param hotKey {@code true} to press space; {@code false} to use the mouse
	 * @return {@code true} if the item is being made; otherwise {@code false}
	 */
	public boolean makeItem(final boolean hotKey) {
		return (hotKey ? ctx.input.send("{VK_SPACE}") : ctx.widgets.component(ITEM_INFO_INTERFACE_WIDGET, START_BUTTON_COMPONENT).click()) && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return working();
			}
		}, 300, 10);
	}

	/**
	 * Starts making the selected item using the mouse.
	 *
	 * @return {@code true} if the item is being made; otherwise {@code false}
	 */
	public boolean makeItem() {
		return makeItem(false);
	}

	/**
	 * Determines if the item with the provided id can be made or not (Production Interface must be opened).
	 *
	 * @param id of the item which will be checked if it can be made or not
	 * @return {@code true} if the item can be made; {@code false} if the item cannot be made or if the Production Interface is not opened
	 */

	public boolean makeable(final int id) {
		if (!opened()) {
			return false;
		}

		final Component itemComp = findItemComponent(MAIN_INTERFACE_WIDGET, id);
		return itemComp != null && ctx.widgets.component(MAIN_INTERFACE_WIDGET, ITEM_SELECTION_PANE_COMPONENT).component(itemComp.index() + 1).textureId() == -1;
	}


	/**
	 * Selects the item with the provided id, if it's not already selected.
	 *
	 * @param id of the item to select
	 * @return {@code true} the item was successfully selected or it's already selected; otherwise {@code false}
	 */
	public boolean selectItem(final int id) {
		if (itemSelected(id)) {
			return true;
		}
		final Component component = findItemComponent(MAIN_INTERFACE_WIDGET, id);
		if (component == null) {
			return false;
		}
		final Component pane = ctx.widgets.component(MAIN_INTERFACE_WIDGET, ITEM_SELECTION_PANE_COMPONENT);
		final Component scrollBar = ctx.widgets.component(MAIN_INTERFACE_WIDGET, ITEM_SCROLLBAR_COMPONENT);
		return ctx.widgets.scroll(component, pane, scrollBar, true) && component.click() && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return itemSelected(id);
			}
		}, 350, 8);
	}

	/**
	 * Closes the Production Interface if it's opened.
	 *
	 * @param hotKey {@code true} to use Escape key; {@code false} to use the mouse
	 * @return {@code true} if the interface was successfully closed or if it's already closed; otherwise {@code false}
	 */
	public boolean close(final boolean hotKey) {
		if (!opened()) {
			return true;
		}
		final boolean interacted;
		if (hotKey) {
			interacted = ctx.input.send("{VK_ESCAPE");
		} else {
			final Component closeButton = findTextureComponent(ITEM_INFO_INTERFACE_WIDGET, CLOSE_BUTTON_TEXTURE);
			interacted = closeButton != null && closeButton.click();
		}
		return interacted && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return !opened();
			}
		}, 250, 10);
	}

	/**
	 * Closes the Production Interface if it's opened, using the mouse.
	 *
	 * @return {@code true} if the interface was successfully closed or if it's already closed; otherwise {@code false}
	 */
	public boolean close() {
		return close(false);
	}

	/**
	 * Returns the currently selected amount of items to make.
	 *
	 * @return currently selected amount of items to make
	 */
	public int selectedAmount() {
		return ctx.varpbits.varpbit(SELECTED_AMOUNT_VARPBIT, 26, 0x1f);
	}

	/**
	 * Determines if maximum available amount of items to make is selected.
	 *
	 * @return {@code true} if the maximum amount of items is selected; otherwise {@code false}
	 */
	public boolean maxSelected() {
		return ctx.widgets.component(MAIN_INTERFACE_WIDGET, SELECTED_AMOUNT_COMPONENT).width() == 170;
	}

	/**
	 * Selects the maximum available amount of items to make.
	 *
	 * @return {@code true} if maximum amount was selected or is already selected; otherwise {@code false}
	 */
	public boolean selectMax() {
		if (maxSelected()) {
			return true;
		}

		final Widgets widgets = ctx.widgets;
		return ctx.input.move(widgets.component(MAIN_INTERFACE_WIDGET, AMOUNT_SCROLL_BAR_COMPONENT).nextPoint()) && ctx.input.drag(widgets.component(MAIN_INTERFACE_WIDGET, MAX_AMOUNT_COMPONENT).nextPoint(), true) && maxSelected();
	}

	/**
	 * Selects the desired amount of items to make.
	 *
	 * @param amount amount of items to select
	 * @return {@code true} if the desired amount was selected, it's already selected or maximum amount was selected; otherwise {@code false}
	 */
	public boolean selectAmount(final int amount) {
		if (selectedAmount() == amount) {
			return true;
		}
		final Component inputBox = ctx.widgets.component(ITEM_INFO_INTERFACE_WIDGET, AMOUNT_INPUT_BOX_COMPONENT);
		if (!inputBox.visible()) {
			if (!inputBox.click()) {
				return false;
			}
			if (!Condition.wait(new Condition.Check() {
				@Override
				public boolean poll() {
					return inputBox.visible();
				}
			}, 250, 10)) {
				return false;
			}
		}
		return ctx.input.sendln(String.valueOf(amount)) && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return selectedAmount() == amount || maxSelected();
			}
		}, 300, 10);
	}

	/**
	 * Determines if you are currently making any items.
	 *
	 * @return {@code true} if the progress interface is visible or if the interface is disabled and the character is animating; otherwise {@code false}
	 */
	public boolean working() {
		return progressInterfaceEnabled() ? ctx.widgets.component(PROGRESS_INTERFACE_WIDGET, PROGRESS_INTERFACE_COMPONENT).visible() : workingByAnimation();
	}

	/**
	 * Determines if the tool selection interface is opened ot not.
	 *
	 * @return {@code true} if the tool selection interface is opened; otherwise {@code false}
	 */
	public boolean toolSelectionOpened() {
		return ctx.widgets.widget(CHOOSE_TOOL_WIDGET).valid();
	}

	/**
	 * Selects the desired tools.
	 *
	 * @param ids of the tools to select
	 * @return {@code true} if the tool was selected or Production Interface is already opened; otherwise {@code false}
	 */
	public boolean selectTool(final int... ids) {
		if (!toolSelectionOpened()) {
			return opened();
		}
		final Component comp = findItemComponent(CHOOSE_TOOL_WIDGET, ids);
		return comp != null && comp.valid() && comp.click() && Condition.wait(new Condition.Check() {
			@Override
			public boolean poll() {
				return opened();
			}
		}, 10, 250);
	}


	private boolean workingByAnimation() {
		if (ctx.players.local().animation() != -1) {
			workingTimeLimit = System.currentTimeMillis() + ((long) (Random.nextDouble(2.5, 4) * 1000));
			return true;
		}
		return System.currentTimeMillis() < workingTimeLimit;
	}

	private boolean progressInterfaceEnabled() {
		return ctx.varpbits.varpbit(1173, 31, 0x1) == 0;
	}


	private Component findTextureComponent(final int widget, final int texture) {
		return findComponent(widget, new Filter<Component>() {
			@Override
			public boolean accept(Component component) {
				return component.textureId() == texture;
			}
		});
	}

	private Component findTextComponent(final int widget, final String text) {
		final String lowerCase = text.toLowerCase();
		return findComponent(widget, new Filter<Component>() {
			@Override
			public boolean accept(Component component) {
				return component.text().toLowerCase().contains(lowerCase);
			}
		});
	}

	private Component findItemComponent(final int widget, final int... itemIds) {
		return findComponent(widget, new Filter<Component>() {
			@Override
			public boolean accept(final Component component) {
				final int id = component.itemId();
				for (final int itemId : itemIds) {
					if (id == itemId) {
						return true;
					}
				}
				return false;
			}
		});
	}

	private Component findComponent(final int widget, final Filter<Component> filter) {
		for (final Component c1 : ctx.widgets.widget(widget)) {
			if (filter.accept(c1)) {
				return c1;
			}
			for (final Component c2 : c1.components()) {
				if (filter.accept(c2)) {
					return c2;
				}
			}
		}
		return null;
	}
}
