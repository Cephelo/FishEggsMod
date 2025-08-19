package dev.cephelo.fisheggs.item;

import dev.cephelo.fisheggs.item.component.ModDataComponents;
import dev.cephelo.fisheggs.item.handler.FishHatchHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class FishEggsItem extends Item {
    public FishEggsItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public @Nullable Entity createEntity(Level level, Entity location, ItemStack stack) {
        return new FishEggsItemEntity(level, location.getX(), location.getY(), location.getZ(), stack,
                Objects.requireNonNull(stack.get(ModDataComponents.FE_COMP)).type(),
                Objects.requireNonNull(stack.get(ModDataComponents.FE_COMP)).variant1(),
                Objects.requireNonNull(stack.get(ModDataComponents.FE_COMP)).variant2());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        context.getPlayer().swing(context.getHand());
        if (context.getLevel() instanceof ServerLevel serverLevel)
            FishHatchHandler.spawnFish(serverLevel, context.getClickedPos().relative(context.getClickedFace(), 1),
                    Objects.requireNonNull(context.getItemInHand().get(ModDataComponents.FE_COMP.get())));
        context.getItemInHand().shrink(1);
        return super.useOn(context);
    }
}
