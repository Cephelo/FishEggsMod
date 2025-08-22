package dev.cephelo.fisheggs.mixin;

import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import net.minecraft.world.entity.animal.Squid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.entity.animal.Squid$SquidRandomMovementGoal")
public class SquidMixins {
    @Shadow @Final private Squid squid;

    // So Squid$SquidRandomMovementGoal doesn't occur while squid is hunting or searching for a mate
    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    public void isHuntingOrBreeding(CallbackInfoReturnable<Boolean> cir) {
        if (this.squid.getData(FishDataAttachments.HAS_TARGET)) {
            cir.cancel();
        }
    }
}
