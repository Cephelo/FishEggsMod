package dev.cephelo.fisheggs.entity.ai.goal;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.item.FishEggsItemEntity;
import dev.cephelo.fisheggs.item.ModItems;
import dev.cephelo.fisheggs.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

// Adapted from BreedGoal
public class FishBreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(Config.MATE_SEARCH_RANGE.get()).ignoreLineOfSight();
    protected final AbstractFish animal;
    private final Class<? extends AbstractFish> partnerClass;
    protected final Level level;
    @Nullable
    protected AbstractFish partner;
    private int loveTime;
    private final double speedModifier;

    public FishBreedGoal(AbstractFish animal, double speedModifier) {
        this(animal, speedModifier, animal.getClass());
    }

    public FishBreedGoal(AbstractFish animal, double speedModifier, Class<? extends AbstractFish> partnerClass) {
        this.animal = animal;
        this.level = animal.level();
        this.partnerClass = partnerClass;
        this.speedModifier = speedModifier;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public boolean canUse() {
        if (!inLove(this.animal) || this.animal.getData(FishDataAttachments.BREED_COOLDOWN) > 0) {
            return false;
        } else {
            this.partner = this.getFreePartner();
            return this.partner != null;
        }
    }

    public boolean canContinueToUse() {
        if (this.partner == null) return false;
        return this.partner.isAlive() && inLove(this.animal) && this.loveTime < 40 && !this.partner.isPanicking();
    }

    public void stop() {
        this.partner = null;
        this.loveTime = 0;
    }

    public void tick() {
        if (this.partner == null || SeekFishFoodGoal.blacklistContains(this.animal.getType())) return;

        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.loveTime;
        //FishEggsMod.LOGGER.info("distsqrB {}", this.animal.distanceToSqr(this.partner));
        if (this.loveTime >= this.adjustedTickDelay(40) && this.animal.distanceToSqr(this.partner) < Config.DIST_BREED.get()) {
            if (canUse()) this.breed();
        }

    }

    @Nullable
    private AbstractFish getFreePartner() {
        List<? extends AbstractFish> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(Config.MATE_SEARCH_RANGE.get()));
        double d0 = Double.MAX_VALUE;
        AbstractFish animal = null;

        for(AbstractFish animal1 : list) {
            if (canMate(this.animal, animal1) && !animal1.isPanicking() && this.animal.distanceToSqr(animal1) < d0) {
                animal = animal1;
                d0 = this.animal.distanceToSqr(animal1);
            }
        }

        return animal;
    }

    public boolean canMate(AbstractFish animal, AbstractFish otherAnimal) {
        if (otherAnimal == animal) {
            return false;
        } else {
            return otherAnimal.getClass() == animal.getClass() && inLove(animal) && inLove(otherAnimal);
        }
    }

    private boolean inLove(AbstractFish fish) {
        return fish.getData(FishDataAttachments.FISHINLOVE) > 0;
    }

    protected void breed() {
        this.level.addFreshEntity(new FishEggsItemEntity(level, this.animal.getX(), this.animal.getY(), this.animal.getZ(),
                ModItems.FISH_EGGS.toStack(), 20, this.animal.getType(),
                 this.animal instanceof TropicalFish fish ? fish.getPackedVariant() : 0,
                this.partner instanceof TropicalFish fish ? fish.getPackedVariant() : 0));

        this.animal.setData(FishDataAttachments.FISHINLOVE, 0);
        this.animal.setData(FishDataAttachments.BREED_COOLDOWN, Config.BREED_COOLDOWN_TIME.get());
        this.animal.addEffect(new MobEffectInstance(MobEffects.REGENERATION, Config.REGEN_TIME.get()));
        this.animal.removeEffect(MobEffects.NIGHT_VISION); // remove inLove particle indicator

        if (this.partner != null) {
            this.partner.setData(FishDataAttachments.FISHINLOVE, 0);
            this.partner.setData(FishDataAttachments.BREED_COOLDOWN, Config.BREED_COOLDOWN_TIME.get());
            this.partner.addEffect(new MobEffectInstance(MobEffects.REGENERATION, Config.REGEN_TIME.get()));
            this.partner.removeEffect(MobEffects.NIGHT_VISION); // remove inLove particle indicator
        }

        if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) && Config.FISH_BREEDING_XP.get() != 0) {
            level.addFreshEntity(new ExperienceOrb(level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), level.getRandom().nextInt(Config.FISH_BREEDING_XP.get() - 1) + 1));
        }

        level.playSound(null, this.animal.getOnPos(), ModSounds.FISH_BREEDS.get(), SoundSource.NEUTRAL);
    }
}
