package dev.cephelo.fisheggs.entity.ai.goal;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.FishEggsMod;
import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.item.FishEggsItemEntity;
import dev.cephelo.fisheggs.item.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

// Adapted from BreedGoal
public class FishBreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(8.0).ignoreLineOfSight();
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
        if (this.partner == null) return;

        this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
        this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
        ++this.loveTime;
        if (this.loveTime >= this.adjustedTickDelay(40) && this.animal.distanceToSqr(this.partner) < 1.0) {
            if (canUse()) this.breed();
        }

    }

    @Nullable
    private AbstractFish getFreePartner() {
        List<? extends AbstractFish> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0F));
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
                ModItems.FISH_EGGS.toStack(), this.animal.getType(),
                 this.animal instanceof TropicalFish fish ? fish.getPackedVariant() : 0,
                this.partner instanceof TropicalFish fish ? fish.getPackedVariant() : 0));

        this.animal.setData(FishDataAttachments.FISHINLOVE, 0);
        this.animal.setData(FishDataAttachments.BREED_COOLDOWN, Config.BREED_COOLDOWN_TIME.get());
        this.animal.addEffect(new MobEffectInstance(MobEffects.REGENERATION, Config.REGEN_TIME.get()));
        this.animal.removeEffect(MobEffects.NIGHT_VISION);

        if (this.partner != null) {
            this.partner.setData(FishDataAttachments.FISHINLOVE, 0);
            this.partner.setData(FishDataAttachments.BREED_COOLDOWN, Config.BREED_COOLDOWN_TIME.get());
            this.partner.addEffect(new MobEffectInstance(MobEffects.REGENERATION, Config.REGEN_TIME.get()));
            this.partner.removeEffect(MobEffects.NIGHT_VISION);
        }
    }
}
