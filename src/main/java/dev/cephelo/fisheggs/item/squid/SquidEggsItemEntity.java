package dev.cephelo.fisheggs.item.squid;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.item.component.ModDataComponents;
import dev.cephelo.fisheggs.item.component.SquidEggsComponent;
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

public class SquidEggsItemEntity extends ItemEntity {
    public SquidEggsItemEntity(Level level, double posX, double posY, double posZ, ItemStack itemStack, int pickupDelay, EntityType squid) {
        this(level, posX, posY, posZ, new Vec3(level.random.nextDouble() * 0.2 - 0.05, -0.1, level.random.nextDouble() * 0.2 - 0.1), itemStack, pickupDelay, squid);
    }

    public SquidEggsItemEntity(Level level, double posX, double posY, double posZ, Vec3 delta, ItemStack itemStack, int pickupDelay, EntityType squid) {
        super(level, posX, posY, posZ, itemStack, delta.x(), delta.y(), delta.z());
        this.setPickUpDelay(pickupDelay);
        this.lifespan = Config.SQUID_HATCH_TIME.get();
        itemStack.set(ModDataComponents.SE_COMP, new SquidEggsComponent(squid));
    }

    @Override
    public void tick() {
        // squiddy spawny before item discardy
        if (!this.level().isClientSide && this.getAge() >= this.lifespan - 1) {
            this.lifespan = Mth.clamp(this.lifespan + EventHooks.onItemExpire(this), 0, 32766);
            if (this.getAge() >= this.lifespan - 1 && this.level() instanceof ServerLevel serverLevel) {
                attemptHatchEggs(serverLevel, this);
            }
        }
        super.tick();
    }

    public static void attemptHatchEggs(ServerLevel serverLevel, SquidEggsItemEntity entity) {
        TagKey<Fluid> allowedFluids = TagKey.create(Registries.FLUID, ResourceLocation.parse("fisheggs:allowed_hatch_fluids"));

        if (!Config.SQUID_EGGS_NEED_WATER.get() || serverLevel.getFluidState(entity.getOnPos()).is(allowedFluids)) {
            for (int i = 0; i < entity.getItem().getCount(); i++)
                SquidEggsItem.spawnSquids(serverLevel, entity.getOnPos().above(),
                        entity.getItem().get(ModDataComponents.SE_COMP));
        }
    }
}