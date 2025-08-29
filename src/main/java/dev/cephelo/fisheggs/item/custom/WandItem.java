package dev.cephelo.fisheggs.item.custom;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.item.FishEggsItemEntity;
import dev.cephelo.fisheggs.item.ModItems;
import dev.cephelo.fisheggs.item.squid.SquidEggsItemEntity;
import dev.cephelo.fisheggs.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class WandItem extends Item {
    private static final ChatFormatting[] achatformatting = new ChatFormatting[]{ChatFormatting.ITALIC, ChatFormatting.GRAY};

    public WandItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack other = player.getItemInHand(InteractionHand.OFF_HAND);
        if (other.getItem() == ModItems.FISH_EGGS.get().asItem() || other.getItem() == ModItems.SQUID_EGGS.get().asItem()) {
            player.addItem(other.copy());
            level.playSound(null, player.blockPosition(), ModSounds.WAND_USE.get(), SoundSource.PLAYERS);
            return InteractionResultHolder.consume(player.getItemInHand(usedHand));
        } else if (player.isCrouching() && player.getLookAngle().y() == 1.0 && player.level() instanceof ServerLevel serverLevel) { // hatch all egg items
            player.swing(usedHand);

            List<? extends Entity> list = serverLevel.getEntities(player, player.getBoundingBox().inflate(Config.WAND_BIGUSE_RADIUS.get()));
            for (Entity entity : list) {
                if (entity instanceof FishEggsItemEntity eggs) {
                    FishEggsItemEntity.attemptHatchEggs(serverLevel, eggs);
                    eggs.discard();
                }
                else if (entity instanceof SquidEggsItemEntity eggs) {
                    SquidEggsItemEntity.attemptHatchEggs(serverLevel, eggs);
                    eggs.discard();
                }
            }

            player.level().playSound(null, player.blockPosition(), ModSounds.WAND_USE_BIG.get(), SoundSource.PLAYERS);
            return InteractionResultHolder.consume(player.getItemInHand(usedHand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (interactionTarget instanceof AbstractFish || interactionTarget instanceof Squid) {
            if (player.isCrouching()) {
                List<? extends LivingEntity> list = player.level().getEntitiesOfClass(interactionTarget.getClass(), player.getBoundingBox().inflate(Config.WAND_BIGUSE_RADIUS.get()));

                player.swing(usedHand);
                for (LivingEntity entity : list) {
                    if (entity.getData(FishDataAttachments.BREED_COOLDOWN.get()) > 0
                            || entity.getData(FishDataAttachments.HUNT_COOLDOWN.get()) > 0) {
                        removeCooldowns(entity);
                    }
                }

                player.level().playSound(null, player.blockPosition(), ModSounds.WAND_USE_BIG.get(), SoundSource.PLAYERS);
                return InteractionResult.CONSUME;
            } else if (interactionTarget.getData(FishDataAttachments.BREED_COOLDOWN.get()) > 0
                    || interactionTarget.getData(FishDataAttachments.HUNT_COOLDOWN.get()) > 0) {
                player.swing(usedHand);
                removeCooldowns(interactionTarget);
                player.level().playSound(null, player.blockPosition(), ModSounds.WAND_USE.get(), SoundSource.PLAYERS);
                return InteractionResult.CONSUME;
            }

            player.level().playSound(null, player.blockPosition(), ModSounds.WAND_FAIL.get(), SoundSource.PLAYERS);

        }

        return InteractionResult.PASS;
    }

    private static void removeCooldowns(LivingEntity target) {
        target.setData(FishDataAttachments.BREED_COOLDOWN.get(), 0);
        target.setData(FishDataAttachments.HUNT_COOLDOWN.get(), 0);
        target.removeEffect(MobEffects.BLINDNESS); // "baby" squids have blindness effect to indicate their baby status
        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 30, 0, false, false));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (tooltipFlag.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.fisheggs.wand_line1").withStyle(achatformatting));
            tooltipComponents.add(Component.translatable("tooltip.fisheggs.wand_line2").withStyle(achatformatting));
            tooltipComponents.add(Component.translatable("tooltip.fisheggs.wand_line3").withStyle(achatformatting));
            tooltipComponents.add(Component.translatable("tooltip.fisheggs.wand_line4").withStyle(achatformatting));
        } else tooltipComponents.add(Component.translatable("tooltip.fisheggs.wand").withStyle(achatformatting));
    }
}
