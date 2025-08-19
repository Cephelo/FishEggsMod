package dev.cephelo.fisheggs.entity.ai.goal;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.item.ModItems;
import dev.cephelo.fisheggs.sound.ModSounds;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

// Adapted from TemptGoal
public class SeekFishFoodGoal extends Goal {
    protected final PathfinderMob mob;
    private final double speedModifier;
    private double pRotX;
    @Nullable
    protected ItemEntity item;
    private int calmDown;
    private final double range;

    public SeekFishFoodGoal(PathfinderMob mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.range = Config.FOOD_SEARCH_RANGE.get();
    }

    private ItemStack getFoodStack(ItemEntity input) {
        if (!Config.FISH_IDS.get().isEmpty() && !Config.FOOD_TAGS.get().isEmpty()) {

            for (int i = 0; i < Config.FISH_IDS.get().size(); i++) {
                Optional<EntityType<?>> oFish = EntityType.byString(Config.FISH_IDS.get().get(i));

                if (oFish.isPresent() && this.mob.getType() == oFish.get()) {
                    TagKey<Item> key = TagKey.create(Registries.ITEM,
                            ResourceLocation.parse(Config.FOOD_TAGS.get().get(Math.min(i, Config.FOOD_TAGS.get().size()-1)))
                    );

                    if (input.getItem().is(key)) {
                        return input.getItem();
                    }
                }
            }

            // if fish type not in FISH_IDS
            if (!Config.FISH_IDS.get().contains(this.mob.getType().toString())) {
                TagKey<Item> fishfoodKey = TagKey.create(Registries.ITEM, ResourceLocation.parse("fisheggs:fish_food"));
                if (input.getItem().is(fishfoodKey)) {
                    return input.getItem();
                }
            }

        } else { // either list is empty
            TagKey<Item> fishfoodKey = TagKey.create(Registries.ITEM, ResourceLocation.parse("fisheggs:fish_food"));
            if (input.getItem().is(fishfoodKey)) {
                return input.getItem();
            }
        }

        return ItemStack.EMPTY;
    }

    // THIS IS YOINKED NEED TO REMAKE
    private ItemEntity findClosestFood() {
        List<ItemEntity> entities = this.mob.level().getEntitiesOfClass(
                ItemEntity.class, this.mob.getBoundingBox().inflate(this.range),
                itemEntity -> ItemStack.isSameItem(itemEntity.getItem(), getFoodStack(itemEntity)));

        ItemEntity food = null;
        double closedSquareDistance = this.range * this.range;
        for (ItemEntity itemEntity : entities) {
            double distance = this.mob.distanceToSqr(itemEntity);
            if (distance < closedSquareDistance) {
                food = itemEntity;
                closedSquareDistance = distance;
            }
        }
        return food;
    }

    private boolean cannotEat() {
        return this.mob.getData(FishDataAttachments.FISHINLOVE) > 0 || this.mob.getData(FishDataAttachments.BREED_COOLDOWN) > 0;
    }

    @Override
    public boolean canUse() {
        if (cannotEat()) return false;

        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        } else {
            this.item = this.findClosestFood();
            return this.item != null;
        }
    }

    public boolean canContinueToUse() {
        return canUse();
    }

    public void start() {
        if (cannotEat()) stop();
//        else if (this.item != null) {
//            FishEggsMod.LOGGER.info("begin seek");
//        }
    }

    public void stop() {
        //FishEggsMod.LOGGER.info("stop seek");
        this.item = null;
        this.mob.getNavigation().stop();
        this.calmDown = reducedTickDelay(Config.CALM_DOWN_TIME.get());
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        if (this.item == null) return;

        //FishEggsMod.LOGGER.info("distsqr {}", this.mob.distanceToSqr(this.item));
        this.mob.getLookControl().setLookAt(this.item, this.mob.getMaxHeadYRot(), this.mob.getMaxHeadXRot());
        if (this.mob.distanceToSqr(this.item) < 0.75) { // 0.6
            this.mob.getNavigation().stop();
            if (this.mob.getData(FishDataAttachments.FISHINLOVE) == 0) {
                this.item.getItem().shrink(1);
                this.mob.setData(FishDataAttachments.FISHINLOVE, Config.LOVE_TIME.get());
                // particle indicator
                this.mob.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Config.LOVE_TIME.get()));
                this.mob.level().playSound(null, this.mob.getOnPos(), ModSounds.FISH_EATS.get(), SoundSource.NEUTRAL);
            }
        } else {
            this.mob.getNavigation().moveTo(this.item, this.speedModifier);
        }

    }
}
