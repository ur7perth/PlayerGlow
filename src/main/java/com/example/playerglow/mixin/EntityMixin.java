Enterpackage com.example.playerglow.mixin;

import com.example.playerglow.GlowManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Inject(method = "getTeamColorValue", at = @At("HEAD"), cancellable = true)
    private void playerglow$color(CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof PlayerEntity p && GlowManager.isTracked(p.getUuid())) {
            cir.setReturnValue(GlowManager.GLOW_COLOR);
        }
    }
}
