package dev.emi.emi.jemi;

import java.util.List;
import java.util.Optional;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.platform.EmiAgnos;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.fabric.ingredients.fluids.IJeiFluidIngredient;
import mezz.jei.api.helpers.IPlatformFluidHelper;
import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;

@SuppressWarnings({"rawtypes", "unchecked"})
public class JemiUtil {
	
	public static EmiIngredient getIngredient(List<ITypedIngredient<?>> ingredients) {
		if (ingredients.isEmpty()) {
			return EmiStack.EMPTY;
		}
		return EmiIngredient.of(ingredients.stream().map(JemiUtil::getStack).filter(i -> !i.isEmpty()).toList());
	}

	public static EmiStack getStack(Object ingredient) {
		Optional<IIngredientType> optional = (Optional<IIngredientType>) (Optional) JemiPlugin.runtime.getIngredientManager()
			.getIngredientTypeChecked(ingredient);
		if (optional.isPresent()) {
			return getStack(optional.get(), ingredient);
		}
		return EmiStack.EMPTY;
	}

	public static EmiStack getStack(ITypedIngredient<?> ingredient) {
		return getStack(ingredient.getType(), ingredient.getIngredient());
	}

	public static EmiStack getStack(IIngredientType<?> type, Object ingredient) {
		if (type == VanillaTypes.ITEM_STACK) {
			return EmiStack.of((ItemStack) ingredient);
		} else if (type == getFluidType()) {
			return EmiAgnos.createFluidStack(ingredient);
		} else {
			IIngredientManager im = JemiPlugin.runtime.getIngredientManager();
			IIngredientHelper helper = im.getIngredientHelper(type);
			if (helper.isValidIngredient(ingredient)) {
				return new JemiStack(type, helper, im.getIngredientRenderer(type), ingredient);
			}
		}
		return EmiStack.EMPTY;
	}

	public static Optional<ITypedIngredient<?>> getTyped(EmiStack stack) {
		if (stack.isEmpty()) {
			return Optional.empty();
		} else if (stack.getKey() instanceof Fluid f) {
			throw new UnsupportedOperationException("TODO port to components");
			//return JemiPlugin.runtime.getIngredientManager().createTypedIngredient(getFluidType(), getFluidHelper().create(f, stack.getAmount() == 0 ? 1000 : stack.getAmount(), stack.getComponents()));
		} else if (stack instanceof JemiStack js) {
			return JemiPlugin.runtime.getIngredientManager().getIngredientTypeChecked(js.ingredient)
				.map(t -> (Optional) JemiPlugin.runtime.getIngredientManager().createTypedIngredient(t, js.ingredient))
				.orElse(Optional.empty());
		}
		return (Optional) JemiPlugin.runtime.getIngredientManager().createTypedIngredient(VanillaTypes.ITEM_STACK, stack.getItemStack());
	}

	public static EmiStack getFluidFromJei(Object object) {
		if (object instanceof IJeiFluidIngredient fluid) {
			throw new UnsupportedOperationException("TODO port to components");
			//return EmiStack.of(fluid.getFluid(), fluid.getTag().orElseGet(() -> null), fluid.getAmount());
		}
		return EmiStack.EMPTY;
	}

	public static IPlatformFluidHelper getFluidHelper() {
		return JemiPlugin.runtime.getJeiHelpers().getPlatformFluidHelper();
	}

	public static IIngredientType getFluidType() {
		return getFluidHelper().getFluidIngredientType();
	}
}
