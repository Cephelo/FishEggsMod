package dev.cephelo.fisheggs.mixin;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.entity.ai.goal.SeekFishFoodGoal;
import dev.cephelo.fisheggs.entity.ai.goal.SquidHuntGoal;
import dev.cephelo.fisheggs.sound.ModSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(Mob.class)
public class FeedEntityMixin {

    @Unique
    private static ItemStack fishEggsMod$getFoodStackForSquid(ItemStack input, EntityType type) {
        if (!Config.SQUID_IDS.get().isEmpty()) {
            if (!Config.SQUID_FOOD_TAGS.get().isEmpty()) {
                for (int i = 0; i < Config.SQUID_IDS.get().size(); i++) {
                    Optional<EntityType<?>> oFish = EntityType.byString(Config.SQUID_IDS.get().get(i));

                    if (oFish.isPresent() && type == oFish.get()) {
                        TagKey<Item> key = TagKey.create(Registries.ITEM,
                                ResourceLocation.parse(Config.SQUID_FOOD_TAGS.get().get(Math.min(i, Config.SQUID_FOOD_TAGS.get().size()-1)))
                        );

                        if (input.is(key)) return input;
                    }
                }
            } else { // food tags list is empty
                TagKey<Item> fishfoodKey = TagKey.create(Registries.ITEM, ResourceLocation.parse("fisheggs:squid_food"));

                if (input.is(fishfoodKey)) return input;
            }
        }

        return ItemStack.EMPTY;
    }

    @Unique
    private static void fishEggsMod$consumeItemInHand(Player player, InteractionHand hand, ItemStack stack) {
        if (!player.hasInfiniteMaterials()) {
            if (player.getItemInHand(hand).getItem() instanceof BucketItem) {
                player.getItemInHand(hand).shrink(1);
                player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, new ItemStack(Items.WATER_BUCKET)));
            } else {
                player.getItemInHand(hand).shrink(1);
            }
        }
    }

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    protected void mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if ((Object)this instanceof AbstractFish fish) {

            // blacklistContains (handfed)
            if (Config.FISH_HANDFED_BLACKLIST.get().contains(EntityType.getKey(fish.getType()).toString())
                    == !Config.FISH_HANDFED_BLACKLIST_IS_WHITELIST.get()) {
                return;
            }

            if (fish.getData(FishDataAttachments.FISHINLOVE) > 0
                || fish.getData(FishDataAttachments.BREED_COOLDOWN) > 0) return;

            ItemStack stack = SeekFishFoodGoal.getFoodStack(player.getItemInHand(hand), fish.getType());
            if (!stack.isEmpty()) {
                SeekFishFoodGoal.setLoveState(fish);
                fish.level().playSound(null, fish.getOnPos(), ModSounds.FISH_EATS.get(), SoundSource.NEUTRAL);
                fishEggsMod$consumeItemInHand(player, hand, stack);
                cir.cancel();
            }
        }

        if ((Object)this instanceof Squid squid) {
            if (squid.isBaby() || squid.getData(FishDataAttachments.FISHINLOVE) > 0
                    || squid.getData(FishDataAttachments.BREED_COOLDOWN) > 0) return;

            ItemStack stack = fishEggsMod$getFoodStackForSquid(player.getItemInHand(hand), squid.getType());
            if (!stack.isEmpty()) {
                SquidHuntGoal.setLoveState(squid);
                squid.level().playSound(null, squid.getOnPos(), ModSounds.SQUID_EATS.get(), SoundSource.NEUTRAL, 1.0f, 0.5f);
                fishEggsMod$consumeItemInHand(player, hand, stack);
                cir.setReturnValue(InteractionResult.CONSUME);
                cir.cancel();
            }
        }
    }

}
