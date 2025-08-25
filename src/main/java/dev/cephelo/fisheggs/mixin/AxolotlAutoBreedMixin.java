package dev.cephelo.fisheggs.mixin;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Axolotl.class)
public class AxolotlAutoBreedMixin {

    @Inject(method = "onStopAttacking", at = @At("HEAD"))
    private static void breedCheck(Axolotl axolotl, LivingEntity target, CallbackInfo ci) {
        //if (axolotl.level().isClientSide()) return;
        if (axolotl.level().getRandom().nextInt(100) > Config.AXOLOTL_HUNT_BREED_CHANCE.get() * 100) return;

        boolean listCheck =
                Config.AXOLOTL_BREED_BLACKLIST.get().contains(EntityType.getKey(target.getType()).toString())
                        == Config.AXOLOTL_BREED_BLACKLIST_IS_WHITELIST.get();

        if (target.isDeadOrDying() && listCheck) {
            int i = axolotl.getAge();
            if (i == 0 && axolotl.canFallInLove()) {
                FishEggsMod.LOGGER.info("sex from {}", target.getType());
                axolotl.setInLove(null);
            }

            if (axolotl.isBaby())
                axolotl.ageUp(AgeableMob.getSpeedUpSecondsWhenFeeding(-i), true);
        }
    }
}
