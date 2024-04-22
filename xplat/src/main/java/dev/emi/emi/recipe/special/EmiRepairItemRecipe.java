package dev.emi.emi.recipe.special;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import dev.emi.emi.EmiPort;
import dev.emi.emi.api.recipe.EmiPatternCraftingRecipe;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.GeneratedSlotWidget;
import dev.emi.emi.api.widget.SlotWidget;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class EmiRepairItemRecipe extends EmiPatternCraftingRecipe {
	public static final List<Item> TOOLS = EmiPort.getItemRegistry().stream()
			.filter(i -> i.getComponents().getOrDefault(DataComponentTypes.MAX_DAMAGE, 0) > 0).collect(Collectors.toList());
	private final Item tool;

	public EmiRepairItemRecipe(Item tool, Identifier id) {
		super(List.of(
				EmiStack.of(tool),
				EmiStack.of(tool)),
				EmiStack.of(tool), id);
		this.tool = tool;
	}
	@Override
	public SlotWidget getInputWidget(int slot, int x, int y) {
		return new GeneratedSlotWidget(r -> {
			List<ItemStack> items = getItems(r);
			if (slot < 2) {
				return EmiStack.of(items.get(slot));
			}
			return EmiStack.EMPTY;

		}, unique, x, y);
	}

	@Override
	public SlotWidget getOutputWidget(int x, int y) {
		return new GeneratedSlotWidget(r -> EmiStack.of(getMergeItems(r)), unique, x, y);
	}

	private List<ItemStack> getItems(Random random) {
		List<ItemStack> items = Lists.newArrayList();
		items.add(getTool(random));
		items.add(getTool(random));
		return items;
	}

	private ItemStack getMergeItems(Random random) {
		List<ItemStack> items = getItems(random);
		ItemStack item = tool.getDefaultStack();
		int maxDamage = item.getMaxDamage();
		int damage = items.get(0).getDamage() - (21 * maxDamage)/20 + items.get(1).getDamage();
		if (damage > 0) {
			item.setDamage(damage);
		}
		return item;
	}

	private ItemStack getTool(Random r) {
		ItemStack stack = tool.getDefaultStack();
		if (stack.getMaxDamage() <= 0) {
			return stack;
		}
		int d = r.nextInt(stack.getMaxDamage());
		stack.setDamage(d);
		return stack;
	}
}
