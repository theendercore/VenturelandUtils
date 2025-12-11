package com.theendercore.ventureland_utils.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import static com.theendercore.ventureland_utils.VenturelandUtilsClient.config;

@Mixin(EntityRenderDispatcher.class)
public class EntityRenderDispatcherMixin<E extends Entity> {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;isInvisible()Z", ordinal = 1))
    boolean showHitboxesForInv(boolean original, @Local(argsOnly = true) E entity) {
        if ((entity instanceof PlayerEntity)) return original;
        return !config.getInvisibleEntityHitboxes();
    }
}
