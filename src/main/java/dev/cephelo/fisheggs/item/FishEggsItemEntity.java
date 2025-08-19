package dev.cephelo.fisheggs.item;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.item.component.FishEggComponents;
import dev.cephelo.fisheggs.item.component.ModDataComponents;
import dev.cephelo.fisheggs.item.handler.FishHatchHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;

public class FishEggsItemEntity extends ItemEntity {
    public FishEggsItemEntity(EntityType<? extends ItemEntity> entityType, Level level, ItemStack stack) {
        super(entityType, level);
        this.setItem(stack);
        this.setPickUpDelay(40);
    }

    // need components that store entitytype and tropfish pattern data

    public FishEggsItemEntity(Level level, double posX, double posY, double posZ, ItemStack itemStack, EntityType fish, int variant1, int variant2) {
        super(level, posX, posY, posZ, itemStack, level.random.nextDouble() * 0.2 - 0.1, 0.2, level.random.nextDouble() * 0.2 - 0.1);
        this.setPickUpDelay(40);
        this.lifespan = Config.HATCH_TIME.get();
        itemStack.set(ModDataComponents.FE_COMP, new FishEggComponents(fish, variant1, variant2));
    }

    @Override
    public void tick() {
        // fishy spawny before item discardy
        if (!this.level().isClientSide && this.getAge() >= this.lifespan - 1) {
            this.lifespan = Mth.clamp(this.lifespan + EventHooks.onItemExpire(this), 0, 32766);
            if (this.getAge() >= this.lifespan - 1 && this.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < this.getItem().getCount(); i++)
                    FishHatchHandler.spawnFish(serverLevel, this.getOnPos().above(),
                            this.getItem().get(ModDataComponents.FE_COMP));
            }
        }
        super.tick();
    }
}
