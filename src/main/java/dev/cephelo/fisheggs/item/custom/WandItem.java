package dev.cephelo.fisheggs.item.custom;

import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.item.ModItems;
import dev.cephelo.fisheggs.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
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
            level.playSound(null, player.blockPosition(), ModSounds.WAND_USE.get(), SoundSource.NEUTRAL);
            return InteractionResultHolder.consume(player.getItemInHand(usedHand));
        }
        return InteractionResultHolder.pass(player.getItemInHand(usedHand));
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity interactionTarget, InteractionHand usedHand) {
        if (interactionTarget instanceof AbstractFish || interactionTarget instanceof Squid) {
            if (interactionTarget.getData(FishDataAttachments.BREED_COOLDOWN.get()) > 0) {
                player.swing(usedHand);
                interactionTarget.setData(FishDataAttachments.BREED_COOLDOWN.get(), 0);
                interactionTarget.removeEffect(MobEffects.BLINDNESS); // "baby" squids have blindness effect to indicate their baby status
                player.level().playSound(null, player.blockPosition(), ModSounds.WAND_USE.get(), SoundSource.NEUTRAL);
                return InteractionResult.CONSUME;
            } else if (interactionTarget.getData(FishDataAttachments.HUNT_COOLDOWN.get()) > 0) {
                player.swing(usedHand);
                interactionTarget.setData(FishDataAttachments.HUNT_COOLDOWN.get(), 0);
                player.level().playSound(null, player.blockPosition(), ModSounds.WAND_USE.get(), SoundSource.NEUTRAL);
                return InteractionResult.CONSUME;
            } else{
                player.level().playSound(null, player.blockPosition(), ModSounds.WAND_FAIL.get(), SoundSource.NEUTRAL);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (tooltipFlag.hasShiftDown()) {
            tooltipComponents.add(Component.translatable("tooltip.fisheggs.wand_line1").withStyle(achatformatting));
            tooltipComponents.add(Component.translatable("tooltip.fisheggs.wand_line2").withStyle(achatformatting));
        } else tooltipComponents.add(Component.translatable("tooltip.fisheggs.wand").withStyle(achatformatting));
    }
}
