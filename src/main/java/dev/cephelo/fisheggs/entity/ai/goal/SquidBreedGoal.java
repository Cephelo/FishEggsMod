package dev.cephelo.fisheggs.entity.ai.goal;

import dev.cephelo.fisheggs.Config;
import dev.cephelo.fisheggs.attachment.FishDataAttachments;
import dev.cephelo.fisheggs.item.ModItems;
import dev.cephelo.fisheggs.item.squid.SquidEggsItemEntity;
import dev.cephelo.fisheggs.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class SquidBreedGoal extends Goal {
    private static final TargetingConditions PARTNER_TARGETING = TargetingConditions.forNonCombat().range(Config.SQUID_MATE_SEARCH_RANGE.get()).ignoreLineOfSight();
    protected final Squid animal;
    private final Class<? extends Squid> partnerClass;
    protected final Level level;
    @Nullable
    protected Squid partner;
    private int loveTime;

    public SquidBreedGoal(Squid animal) {
        this(animal, animal.getClass());
    }

    public SquidBreedGoal(Squid animal, Class<? extends Squid> partnerClass) {
        this.animal = animal;
        this.level = animal.level();
        this.partnerClass = partnerClass;
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
        return this.partner.isAlive() && inLove(this.animal) && this.loveTime < 60 && !this.partner.isPanicking();
    }

    public void start() {
        if (cannotBreed()) stop();
        this.animal.setData(FishDataAttachments.HAS_TARGET.get(), true);
    }

    public void stop() {
        this.partner = null;
        this.loveTime = 0;
        this.animal.setData(FishDataAttachments.HAS_TARGET.get(), false);
    }

    public void tick() {
        if (this.partner == null) return;

        int i = this.animal.getNoActionTime();
        if (i > 100) {
            this.animal.setMovementVector(0.0F, 0.0F, 0.0F);
        } else if (this.animal.getRandom().nextInt(reducedTickDelay(50)) == 0 || !this.animal.isInWater() || !this.animal.hasMovementVector()) {
            Vector3f lookVec = this.animal.getPosition(0).vectorTo(this.partner.getPosition(0)).toVector3f();
            lookVec = lookVec.div((float) Math.sqrt((lookVec.x * lookVec.x) + (lookVec.y * lookVec.y) + (lookVec.z * lookVec.z)));
            this.animal.setMovementVector(lookVec.x * 0.2f, lookVec.y * 0.2f, lookVec.z * 0.2f);
            this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
        }

        //FishEggsMod.LOGGER.info("distsqrS {}", this.animal.distanceToSqr(this.partner));
        ++this.loveTime;
        if (this.loveTime >= this.adjustedTickDelay(40) && this.animal.distanceToSqr(this.partner) < Config.SQUID_DIST_BREED.get()) {
            if (canUse()) this.breed();
        }

    }

    private boolean cannotBreed() {
        if (!inLove(this.animal) || this.animal.getData(FishDataAttachments.BREED_COOLDOWN) > 0) return true;
        return blacklistContainsSquid(this.animal.getType());
    }

    private static boolean blacklistContainsSquid(EntityType type) {
        return !(Config.SQUID_BREED_BLACKLIST.get().contains(EntityType.getKey(type).toString())
                == Config.SQUID_BREED_BLACKLIST_IS_WHITELIST.get());
    }

    @Nullable
    private Squid getFreePartner() {
        List<? extends Squid> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(Config.SQUID_MATE_SEARCH_RANGE.get()));
        double d0 = Double.MAX_VALUE;
        Squid animal = null;

        for(Squid animal1 : list) {
            if (canMate(this.animal, animal1) && !animal1.isPanicking() && this.animal.distanceToSqr(animal1) < d0) {
                animal = animal1;
                d0 = this.animal.distanceToSqr(animal1);
            }
        }

        return animal;
    }

    public boolean canMate(Squid animal, Squid otherAnimal) {
        if (otherAnimal == animal) {
            return false;
        } else {
            return otherAnimal.getClass() == animal.getClass() && inLove(animal) && inLove(otherAnimal);
        }
    }

    private boolean inLove(Squid fish) {
        return fish.getData(FishDataAttachments.FISHINLOVE) > 0 && !fish.isBaby();
    }

    protected void breed() {
        this.level.addFreshEntity(new SquidEggsItemEntity(level, this.animal.getX(), this.animal.getY(), this.animal.getZ(),
                ModItems.SQUID_EGGS.toStack(), 20, this.animal.getType()));

        this.animal.setData(FishDataAttachments.FISHINLOVE, 0);
        this.animal.setData(FishDataAttachments.BREED_COOLDOWN, Config.SQUID_BREED_COOLDOWN_TIME.get());
        if (!Config.SQUID_HAS_BRED_DESPAWN.get()) this.animal.setPersistenceRequired(); // Prevents despawning

        this.animal.addEffect(new MobEffectInstance(MobEffects.REGENERATION, Config.SQUID_REGEN_TIME.get()));
        this.animal.removeEffect(MobEffects.LUCK); // remove inLove particle indicator

        if (this.partner != null) {
            this.partner.setData(FishDataAttachments.FISHINLOVE, 0);
            this.partner.setData(FishDataAttachments.BREED_COOLDOWN, Config.SQUID_BREED_COOLDOWN_TIME.get());
            if (!Config.SQUID_HAS_BRED_DESPAWN.get()) this.partner.setPersistenceRequired(); // Prevents despawning

            this.partner.addEffect(new MobEffectInstance(MobEffects.REGENERATION, Config.SQUID_REGEN_TIME.get()));
            this.partner.removeEffect(MobEffects.LUCK); // remove inLove particle indicator
        }

        if (level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT) && Config.SQUID_BREEDING_XP.get() != 0) {
            level.addFreshEntity(new ExperienceOrb(level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), level.getRandom().nextInt(Config.SQUID_BREEDING_XP.get() - 1) + 1));
        }

        level.playSound(null, this.animal.getOnPos(), ModSounds.SQUID_BREEDS.get(), SoundSource.NEUTRAL);
    }
}
