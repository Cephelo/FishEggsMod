package dev.cephelo.fisheggs.item;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.item.component.FishEggComponents;
import dev.cephelo.fisheggs.item.component.ModDataComponents;
import dev.cephelo.fisheggs.item.handler.FishHatchHandler;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.EventHooks;

public class FishEggsItemEntity extends ItemEntity {
    public FishEggsItemEntity(Level level, double posX, double posY, double posZ, ItemStack itemStack, int pickupDelay, EntityType fish, int variant1, int variant2) {
        this(level, posX, posY, posZ, new Vec3(level.random.nextDouble() * 0.2 - 0.05, -0.1, level.random.nextDouble() * 0.2 - 0.1), itemStack, pickupDelay, fish, variant1, variant2);
    }

    public FishEggsItemEntity(Level level, double posX, double posY, double posZ, Vec3 delta, ItemStack itemStack, int pickupDelay, EntityType fish, int variant1, int variant2) {
        super(level, posX, posY, posZ, itemStack, delta.x(), delta.y(), delta.z());//level.random.nextDouble() * 0.2 - 0.1, 0.2, level.random.nextDouble() * 0.2 - 0.1);
        this.setPickUpDelay(pickupDelay);
        this.lifespan = Config.HATCH_TIME.get();
        itemStack.set(ModDataComponents.FE_COMP, new FishEggComponents(fish, variant1, variant2));
    }

    @Override
    public void tick() {
        // fishy spawny before item discardy
        if (!this.level().isClientSide && this.getAge() >= this.lifespan - 1) {
            this.lifespan = Mth.clamp(this.lifespan + EventHooks.onItemExpire(this), 0, 32766);
            if (this.getAge() >= this.lifespan - 1 && this.level() instanceof ServerLevel serverLevel) {
                attemptHatchEggs(serverLevel, this);
            }
        }
        super.tick();
    }

    public static void attemptHatchEggs(ServerLevel serverLevel, FishEggsItemEntity entity) {
        TagKey<Fluid> allowedFluids = TagKey.create(Registries.FLUID, ResourceLocation.parse("fisheggs:allowed_hatch_fluids"));

        if (!Config.FISH_EGGS_NEED_WATER.get() || serverLevel.getFluidState(entity.getOnPos()).is(allowedFluids)) {
            for (int i = 0; i < entity.getItem().getCount(); i++)
                FishHatchHandler.spawnFish(serverLevel, entity.getOnPos().above(),
                        entity.getItem().get(ModDataComponents.FE_COMP));
        }
    }
}
