package dev.cephelo.fisheggs.entity.ai.goal;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.sound.ModSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
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
    @Nullable
    protected ItemEntity item;
    private final double range;

    public SeekFishFoodGoal(PathfinderMob mob, double speedModifier) {
        this.mob = mob;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.range = Config.FOOD_SEARCH_RANGE.get();
    }

    public static ItemStack getFoodStack(ItemStack input, EntityType type) {
        if (!Config.FISH_IDS.get().isEmpty() && !Config.FOOD_TAGS.get().isEmpty()) {

            for (int i = 0; i < Config.FISH_IDS.get().size(); i++) {
                Optional<EntityType<?>> oFish = EntityType.byString(Config.FISH_IDS.get().get(i));

                if (oFish.isPresent() && type == oFish.get()) {
                    TagKey<Item> key = TagKey.create(Registries.ITEM,
                            ResourceLocation.parse(Config.FOOD_TAGS.get().get(Math.min(i, Config.FOOD_TAGS.get().size()-1)))
                    );

                    if (input.is(key)) return input;
                }
            }

            // if fish type not in FISH_IDS
            if (!Config.FISH_IDS.get().contains(EntityType.getKey(type).toString())) {
                TagKey<Item> fishfoodKey = TagKey.create(Registries.ITEM, ResourceLocation.parse("fisheggs:fish_food"));

                if (input.is(fishfoodKey)) return input;
            }

        } else { // either list is empty
            TagKey<Item> fishfoodKey = TagKey.create(Registries.ITEM, ResourceLocation.parse("fisheggs:fish_food"));

            if (input.is(fishfoodKey)) return input;
        }

        return ItemStack.EMPTY;
    }

    // Thank you maxvar
    private ItemEntity findNearestFood() {
        List<ItemEntity> entities = this.mob.level().getEntitiesOfClass(
                ItemEntity.class, this.mob.getBoundingBox().inflate(this.range),
                itemEntity -> ItemStack.isSameItem(itemEntity.getItem(), getFoodStack(itemEntity.getItem(), this.mob.getType())));

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

    private boolean tooManyFish() {
        return this.mob.level().getEntitiesOfClass(AbstractFish.class, this.mob.getBoundingBox().inflate(Config.FISH_PREVENT_LOVE_RADIUS.get())).size()
                > Config.FISH_PREVENT_LOVE_AMOUNT.get();
    }

    private static boolean blacklistContains(EntityType type) {
        return Config.FISH_BLACKLIST.get().contains(EntityType.getKey(type).toString())
                == !Config.FISH_BLACKLIST_IS_WHITELIST.get();
    }

    private boolean cannotEat() {
        //FishEggsMod.LOGGER.info("id {}, match {}, list {}", EntityType.getKey(this.mob.getType()).toString(), blacklistContains(this.mob.getType()), Config.FISH_BLACKLIST.get());
        if (blacklistContains(this.mob.getType())) return true;
        if (Config.FISH_PREVENT_LOVE_HUNT.get() && tooManyFish()) return true;
        return this.mob.getData(FishDataAttachments.FISHINLOVE) > 0 || this.mob.getData(FishDataAttachments.BREED_COOLDOWN) > 0;
    }

    @Override
    public boolean canUse() {
        if (cannotEat()) return false;

        if (this.mob.getData(FishDataAttachments.HUNT_COOLDOWN) > 0) {
            return false;
        } else {
            this.item = this.findNearestFood();
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
        this.mob.setData(FishDataAttachments.HUNT_COOLDOWN, reducedTickDelay(Config.CALM_DOWN_TIME.get()));
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void tick() {
        if (this.item == null) return;

        //FishEggsMod.LOGGER.info("distsqrF {}", this.mob.distanceToSqr(this.item));
        this.mob.getLookControl().setLookAt(this.item, this.mob.getMaxHeadYRot(), this.mob.getMaxHeadXRot());
        if (this.mob.distanceToSqr(this.item) < Config.DIST_FOOD.get()) { // 0.75 // 0.6
            if (this.item.getItem().getCount() > 0 && this.mob.getData(FishDataAttachments.FISHINLOVE) == 0) {
                this.item.getItem().shrink(1);
                mob.level().playSound(null, mob.getOnPos(), ModSounds.FISH_EATS.get(), SoundSource.NEUTRAL);

                if (this.mob.level().getRandom().nextInt(100) <= Config.FISH_EAT_BREED_CHANCE.get() * 100) {
                    if (tooManyFish()) {
                        mob.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 400));
                    } else setLoveState(this.mob);
                }
                this.mob.getNavigation().stop();
                stop();
            }
        } else {
            this.mob.getNavigation().moveTo(this.item, this.speedModifier);
        }
    }

    public static void setLoveState(Mob mob) {
        mob.setData(FishDataAttachments.FISHINLOVE, Config.LOVE_TIME.get());

        // particle indicator
        mob.addEffect(new MobEffectInstance(MobEffects.LUCK, Config.LOVE_TIME.get()));
        mob.removeEffect(MobEffects.UNLUCK);
    }
}
