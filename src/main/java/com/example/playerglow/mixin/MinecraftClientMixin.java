package com.example.playerglow.mixin;

import com.example.playerglow.GlowManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "hasOutline", at = @At("HEAD"), cancellable = true)
    private void playerglow$hasOutline(Entity entity, CallbackInfoReturnable<Boolean> cir) {
        if (GlowManager.shouldGlow(entity)) {
            cir.setReturnValue(true);
        }
    }
}
