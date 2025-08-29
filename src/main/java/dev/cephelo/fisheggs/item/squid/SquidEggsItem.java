package dev.cephelo.fisheggs.item.squid;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.item.component.ModDataComponents;
import dev.cephelo.fisheggs.item.component.SquidEggsComponent;
import dev.cephelo.fisheggs.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SquidEggsItem extends Item {
    private static final ChatFormatting[] achatformatting = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};

    public SquidEggsItem(Item.Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public @Nullable Entity createEntity(Level level, Entity location, ItemStack stack) {
        SquidEggsComponent comp = stack.get(ModDataComponents.SE_COMP);
        // handles null from creative inventory
        if (comp == null) comp = new SquidEggsComponent(EntityType.SQUID);

        return new SquidEggsItemEntity(level, location.getX(), location.getY(), location.getZ(), location.getDeltaMovement(), stack, 40,
                comp.type());
    }

    private boolean inWater(ServerLevel serverLevel, BlockPos pos) {
        TagKey<Fluid> allowedFluids = TagKey.create(Registries.FLUID, ResourceLocation.parse("fisheggs:allowed_hatch_fluids"));
        return !Config.SQUID_EGGS_NEED_WATER.get() || serverLevel.getFluidState(pos).is(allowedFluids);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockPos targetPos = context.getClickedPos().relative(context.getClickedFace(), 1);
        if (context.getLevel() instanceof ServerLevel serverLevel && inWater(serverLevel, targetPos)) {
            context.getPlayer().swing(context.getHand());
            SquidEggsComponent comp = context.getItemInHand().get(ModDataComponents.SE_COMP);
            // handles null from creative inventory
            if (comp == null) comp = new SquidEggsComponent(EntityType.SQUID);

            spawnSquids(serverLevel, targetPos, comp);
            context.getItemInHand().shrink(1);

            return InteractionResult.CONSUME;
        }

        return super.useOn(context);
    }

    public static void spawnSquids(ServerLevel level, BlockPos pos, SquidEggsComponent data) {
        // handles null from creative inventory
        if (data == null) data = new SquidEggsComponent(EntityType.SQUID);

        for (int j = 0; j < Mth.randomBetweenInclusive(level.random, 1, Config.MAX_SQUID_FROM_EGGS.get()); j++) {
            Entity thing = data.type().spawn(level, pos, MobSpawnType.BUCKET); // Baby squids don't exist in 1.21.1 :/
            if (thing != null) {
                thing.setData(FishDataAttachments.BREED_COOLDOWN, Config.SQUID_HATCH_BREED_COOLDOWN_TIME.get());
                if (thing instanceof Mob mob) {
                    mob.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, Config.SQUID_HATCH_BREED_COOLDOWN_TIME.get()));
                    if (!Config.SQUID_HATCHED_CAN_DESPAWN.get()) // Prevents despawning
                        mob.setPersistenceRequired();
                }
            }
        }

        level.playSound(null, pos, ModSounds.SQUID_EGGS_HATCH.get(), SoundSource.NEUTRAL);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        SquidEggsComponent comp = stack.get(ModDataComponents.SE_COMP.get());
        if (comp != null) {
            if (Config.SQUID_SHOW_TYPE_TOOLTIP.get())
                tooltipComponents.add(Component.translatable(comp.type().getDescriptionId()).withStyle(achatformatting));
        }

    }
}
