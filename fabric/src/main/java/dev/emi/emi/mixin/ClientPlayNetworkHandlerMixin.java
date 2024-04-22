package dev.emi.emi.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import dev.emi.emi.runtime.EmiLog;
import dev.emi.emi.runtime.EmiReloadManager;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.SynchronizeRecipesS2CPacket;

/**
 * This entire mixin assumes that no one will modify how recipes and tags are synced.
 * In vanilla, first connect gets them in one order, and then reloads send them reversed.
 * This waits for both, then reloads.
 * If only one comes, no reload will occur, which would be weird behavior.
 */
@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
	@Unique
	private int infoMask = 0;

	@Inject(at = @At(value = "INVOKE", target = "net/minecraft/recipe/RecipeManager.setRecipes(Ljava/lang/Iterable;)V",
		shift = Shift.AFTER), method = "onSynchronizeRecipes")
	private void onSynchronizeRecipes(SynchronizeRecipesS2CPacket packet, CallbackInfo info) {
		EmiReloadManager.reloadRecipes();
	}

	@Inject(at = @At("RETURN"), method = "onSynchronizeTags")
	private void refreshTagBasedData(CallbackInfo info) {
		EmiReloadManager.reloadTags();
	}

	@Inject(at = @At("RETURN"), method = "onGameJoin")
	private void onGameJoin(CallbackInfo info) {
		EmiLog.info("Joining server, EMI waiting for data from server...");
	}
}
