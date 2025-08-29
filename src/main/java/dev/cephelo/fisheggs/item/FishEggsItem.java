package dev.cephelo.fisheggs.item;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.item.component.FishEggComponents;
import dev.cephelo.fisheggs.item.component.ModDataComponents;
import dev.cephelo.fisheggs.item.handler.FishHatchHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FishEggsItem extends Item {
    private static final ChatFormatting[] achatformatting = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};

    public FishEggsItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public @Nullable Entity createEntity(Level level, Entity location, ItemStack stack) {
        FishEggComponents comp = stack.get(ModDataComponents.FE_COMP);
        // handles null from creative inventory
        if (comp == null) comp = new FishEggComponents(EntityType.COD, 0, 0);

        return new FishEggsItemEntity(level, location.getX(), location.getY(), location.getZ(), location.getDeltaMovement(), stack, 40,
                comp.type(), comp.variant1(), comp.variant2());
    }

    private boolean inWater(ServerLevel serverLevel, BlockPos pos) {
        TagKey<Fluid> allowedFluids = TagKey.create(Registries.FLUID, ResourceLocation.parse("fisheggs:allowed_hatch_fluids"));
        return !Config.FISH_EGGS_NEED_WATER.get() || serverLevel.getFluidState(pos).is(allowedFluids);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos targetPos = context.getClickedPos().relative(context.getClickedFace(), 1);

        if (context.getLevel() instanceof ServerLevel serverLevel && inWater(serverLevel, targetPos)) {
            context.getPlayer().swing(context.getHand());
            FishEggComponents comp = context.getItemInHand().get(ModDataComponents.FE_COMP);
            // handles null from creative inventory
            if (comp == null) comp = new FishEggComponents(EntityType.COD, 0, 0);

            FishHatchHandler.spawnFish(serverLevel, targetPos, comp);
            context.getItemInHand().shrink(1);

            return InteractionResult.CONSUME;
        }

        return super.useOn(context);
    }

    private static MutableComponent variantString(int variant) {
        String baseColor = "color.minecraft." + TropicalFish.getBaseColor(variant);
        String patternColor = "color.minecraft." + TropicalFish.getPatternColor(variant);
        Component pattern = TropicalFish.getPattern(variant).displayName();

        MutableComponent mutablecomponent = Component.translatable(baseColor);
        if (!baseColor.equals(patternColor)) {
            mutablecomponent.append("-").append(Component.translatable(patternColor));
        }

        mutablecomponent.append(" ").append(pattern);
        return mutablecomponent;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        FishEggComponents comp = stack.get(ModDataComponents.FE_COMP.get());
        if (comp != null) {
            if (Config.SHOW_TYPE_TOOLTIP.get())
                tooltipComponents.add(Component.translatable(comp.type().getDescriptionId()).withStyle(achatformatting));

            if (comp.type() == EntityType.TROPICAL_FISH && Config.SHOW_VARIANT_TOOLTIP.get()) {
                tooltipComponents.add(variantString(comp.variant1()).append(", ").append(variantString(comp.variant2())).withStyle(achatformatting));
            }
        }

    }
}
