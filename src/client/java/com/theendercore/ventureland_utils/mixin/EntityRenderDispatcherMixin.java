package com.theendercore.ventureland_utils.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.theendercore.ventureland_utils.VenturelandUtilsClient.config;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin<E extends Entity> {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInvisible()Z", ordinal = 1))
    boolean showHitboxesForInv(boolean original, @Local(argsOnly = true) E entity) {
        if ((entity instanceof PlayerEntity) || config.getHitboxBlacklist().contains(entity.getType())) return original;
        return !config.getInvisibleEntityHitboxes();
    }

    @Inject(method = "renderHitbox", at = @At("HEAD"), cancellable = true)
    private static void disableItemHitbox(MatrixStack matrices, VertexConsumer vertices, Entity entity, float tickDelta, float red, float green, float blue, CallbackInfo ci) {
        if (config.getHideDisplayItemHitboxes() && entity instanceof ItemEntity && entity.hasVehicle()) {
            ci.cancel();
        }
    }
}
